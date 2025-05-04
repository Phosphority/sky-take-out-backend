package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Resource
    OrdersMapper ordersMapper;

    @Scheduled(cron = "0 * * * * *")
    public void cancelPastPaymentOrder () {
        log.info("定时任务超过15分钟的订单自动取消");
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime time = now.plusMinutes(-15);

        // 超过15分钟未支付的订单自动取消
        List<Orders> ordersList = ordersMapper.findByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,time);

        if(ordersList != null && !ordersList.isEmpty()) {
            ordersList.forEach(orders -> {
                orders.setCancelTime(LocalDateTime.now());
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时未支付 自动取消");
                ordersMapper.update(orders);
            });
        }
    }

    /**
     * 每天凌晨一点检查前一天是否有订单还是显示未完成
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrders () {
        log.info("将前一天还在派送中的订单修改未已完成");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime time = now.plusMinutes(-60);

        List<Orders> ordersList = ordersMapper.findByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,time);

        if(ordersList != null && !ordersList.isEmpty()) {
            ordersList.forEach(orders -> {
                orders.setCancelTime(LocalDateTime.now());
                orders.setStatus(Orders.COMPLETED);
                ordersMapper.update(orders);
            });
        }
    }
}
