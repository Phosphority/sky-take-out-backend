package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Resource
    private ReportMapper reportMapper;


    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {

        // 得到日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!(begin.equals(end))) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 得到每天的所对应的营业额
        List<Double> totalList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map<String, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double total = reportMapper.sumByMap(map);
            total = total == null ? 0 : total;
            totalList.add(total);
        }

        // 返回封装好的数据
        return TurnoverReportVO
                .builder()
                .turnoverList(StringUtils.join(totalList,","))
                .dateList(StringUtils.join(dateList,","))
                .build();
    }
}
