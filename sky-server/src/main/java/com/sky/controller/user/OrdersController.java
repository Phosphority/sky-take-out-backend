package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController("userOrderController")
@Api(tags = "订单接口")
@Slf4j
@RequestMapping("/user/order")
public class OrdersController {

    @Resource
    private OrdersService ordersService;


    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("订单提交信息为:{}", ordersSubmitDTO.toString());
        OrderSubmitVO submitVO = ordersService.submit(ordersSubmitDTO);
        return Result.success(submitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = ordersService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation("用户获取历史订单")
    public Result<PageResult> historyOrders(@RequestBody OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("用户获取历史订单");
        PageResult ordersList = ordersService.historyOrders(ordersPageQueryDTO);
        return Result.success(ordersList);
    }

    @GetMapping("/reminder/{id}")
    @ApiOperation("用户催单")
    public Result reminder(@PathVariable Long id) {
        ordersService.reminder(id);
        return Result.success();
    }

}









