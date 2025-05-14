package com.sky.controller.admin;

import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.OrdersSearchVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单相关接口")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单搜索:{}", ordersPageQueryDTO);
        PageResult ordersPageResult = ordersService.conditionSearch(ordersPageQueryDTO);
        return Result.success(ordersPageResult);
    }

    @PutMapping("confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody Integer id) {
        ordersService.confirm(Long.valueOf(id));
        return Result.success();
    }

    @PutMapping("rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestParam Integer id,@RequestParam String rejectionReason) {
        ordersService.rejection(Long.valueOf(id),rejectionReason);
        return Result.success();
    }

    @PutMapping("cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestParam Long id,@RequestParam String cancelReason) {
        ordersService.cancel(id,cancelReason);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id) {
        ordersService.delivery(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id) {
        ordersService.complete(id);
        return Result.success();
    }


}
















