package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        while (!dateList.isEmpty()) {
            LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
            Map<String, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double total = reportMapper.sumByMap(map);
            totalList.add(total);
        }

        // 返回封装好的数据
        return TurnoverReportVO
                .builder()
                .turnoverList(StringUtils.join())
                .dateList(StringUtils.collectionToCommaDelimitedString(dateList))
                .build();
    }
}
