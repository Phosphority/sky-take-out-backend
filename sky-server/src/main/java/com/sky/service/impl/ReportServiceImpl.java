package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Resource
    private ReportMapper reportMapper;
    @Autowired
    private OrdersMapper ordersMapper;


    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {

        // 得到日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        // 通过循环得到从begin到end的日期
        while (!(begin.equals(end))) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 得到每天的所对应的营业额 TODO 这里营业额的计算应该考虑到取消订单的额度
        List<Double> totalList = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 得到当天日期的开始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 得到当天已完成订单状态的营业额
            Map<String, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double oneDayTurnover = reportMapper.sumByMap(map);
            oneDayTurnover = oneDayTurnover == null ? 0 : oneDayTurnover;
            totalList.add(oneDayTurnover);
        }

        // 将list中转化为字符串，并且每个元素之间都以","分隔
        // NOTICE 前端实现eChart所需要的数据类型为这种格式
        String dateStr = StringUtils.join(dateList, ",");
        String totalStr = StringUtils.join(totalList, ",");

        // 返回封装好的数据
        return TurnoverReportVO
                .builder()
                .turnoverList(totalStr)
                .dateList(dateStr)
                .build();
    }

    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin.toString());
        map.put("end", end.toString());

        // 每日的订单数列表
        List<Integer> orderCountList = new ArrayList<>();
        // 每日有效订单数列表
        List<Integer> validOrderCountList = new ArrayList<>();

        // 得到日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!(begin.equals(end))) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 得到订单的每日数据
        for (LocalDate date : dateList) {
            // 得到每日的开始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 将之前的begin和end覆盖
            map.put("begin", beginTime);
            map.put("end", endTime);
            // 每日订总单数
            Integer orderCount = ordersMapper.countByMap(map);
            orderCountList.add(orderCount);

            // 每日有效订单数
            map.put("status", Orders.COMPLETED);
            Integer validOrderCount = ordersMapper.countByMap(map);
            validOrderCountList.add(validOrderCount);
            map.remove("status");
        }
        // 订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(0, Integer::sum);
        // 有效订单总数
        Integer validOrderCount = validOrderCountList.stream().reduce(0, Integer::sum);
        // 计算orderCompletionRate
        double orderCompletionRate = 0.0;
        if (totalOrderCount > 0 && validOrderCount > 0) {
            orderCompletionRate = (double) validOrderCount / totalOrderCount;
        }

        // 将list类型的数据处理成String类型
        String dateListStr = StringUtils.join(dateList, ",");
        String orderCountListStr = StringUtils.join(orderCountList, ",");
        String validOrderCountListStr = StringUtils.join(validOrderCountList, ",");
        return OrderReportVO.builder()
                .dateList(dateListStr)
                .totalOrderCount(totalOrderCount)
                .orderCountList(orderCountListStr)
                .validOrderCountList(validOrderCountListStr)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }
}






















