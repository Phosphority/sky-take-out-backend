package com.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.Orders;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Resource
    private ReportMapper reportMapper;
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private WorkspaceService workspaceService;


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

        // 得到日期列表 TODO这里可以优化，将获取日期列表的代码提取出来
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!(begin.equals(end))) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 每日的订单数列表
        List<Integer> orderCountList = new ArrayList<>();
        // 每日有效订单数列表
        List<Integer> validOrderCountList = new ArrayList<>();

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
            map.remove("status");
            validOrderCountList.add(validOrderCount);
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

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        // 得到日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!(begin.equals(end))) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        Map<String, Object> map = new HashMap<>();
        // 新增用户数列表
        List<Integer> newUserList = new ArrayList<>();
        // 总量用户数列表
        List<Integer> totalUserList = new ArrayList<>();
        // 当前用户总数
        Integer totalUser = userMapper.countByMap(map);
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            map.put("begin", beginTime);
            map.put("end", endTime);

            // 每日新增用户数
            Integer newUsers = userMapper.countByMap(map);

            newUserList.add(newUsers);
            // 每日新增之后的用户总数
            totalUser = totalUser + newUsers;
            totalUserList.add(totalUser);
        }

        String dateListStr = StringUtils.join(dateList, ",");
        String newUserListStr = StringUtils.join(newUserList, ",");
        String totalUserListStr = StringUtils.join(totalUserList, ",");
        return UserReportVO.builder()
                .dateList(dateListStr)
                .newUserList(newUserListStr)
                .totalUserList(totalUserListStr)
                .build();
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        Map<String, Object> map = new HashMap<>();
        map.put("begin", beginTime);
        map.put("end", endTime);

        List<GoodsSalesDTO> goodsSalesDTOList = ordersMapper.getSalesTop10(map);
        // 商品名称列表
        List<String> nameList = new ArrayList<>();
        // 商品销量列表
        List<Integer> numberList = new ArrayList<>();
//        List<String> nameList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
//        List<Integer> numberList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        for (GoodsSalesDTO goodsSalesDTO : goodsSalesDTOList) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }

        String nameListStr = StringUtils.join(nameList, ",");
        String numberListStr = StringUtils.join(numberList, ",");
        return SalesTop10ReportVO.builder()
                .nameList(nameListStr)
                .numberList(numberListStr)
                .build();
    }

    @Override
    public void export(HttpServletResponse response) {
        //1. 查询数据库，获取营业数据---查询最近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        //查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));

        //2. 通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //基于模板文件创建一个新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //获取表格文件的Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            //填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);

            //获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            //获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                //查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

                //获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            //3. 通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            log.error("导出excel数据IO异常");
        }

    }


}






















