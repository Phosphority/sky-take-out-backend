package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrdersService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);


    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    void reminder(Long id);

    PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    void confirm(Long id);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    void cancel(Long id, String cancelReason);

    void delivery(Long id);

    void complete(Long id);

    OrderStatisticsVO statistics();

    OrderVO details(Long id);
}
