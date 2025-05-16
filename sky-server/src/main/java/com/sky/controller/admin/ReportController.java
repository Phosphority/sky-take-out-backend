package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;

// 前端是使用eChart实现的
@Api(tags = "数据统计相关接口")
@RestController
@Slf4j
@RequestMapping("/admin/report")
public class ReportController {

    @Resource
    private ReportService reportService;


    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计接口")
    public Result<TurnoverReportVO> turnoverStatistics(
            @RequestParam("begin")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end
    ) {
        TurnoverReportVO turnoverStatistics = reportService.turnoverStatistics(begin, end);
        return Result.success(turnoverStatistics);
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计接口")
    public Result<OrderReportVO> ordersStatistics(
            @RequestParam("begin")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end
    ) {
        return Result.success(reportService.ordersStatistics(begin,end));
    }

}














