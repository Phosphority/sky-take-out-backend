package com.sky.service.impl;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.service.OrdersService;
import com.sky.vo.OrderSubmitVO;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Resource
    private OrdersMapper ordersMapper;


    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        Orders orders = Orders.builder().build();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        ordersMapper.submit(orders);
        return null;
    }
}














