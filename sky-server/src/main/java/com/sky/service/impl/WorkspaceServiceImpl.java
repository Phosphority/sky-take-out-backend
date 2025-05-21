package com.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.StatusConstant;
import com.sky.entity.AddressBook;
import com.sky.entity.Orders;
import com.sky.mapper.*;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    @Resource
    private OrdersMapper orderMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private SetmealMapper setmealMapper;

    /**
     * 根据时间段统计营业数据
     *
     * @param begin
     * @param end
     * @return
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        /**
         * 营业额：当日已完成订单的总金额
         * 有效订单：当日已完成订单的数量
         * 订单完成率：有效订单数 / 总订单数
         * 平均客单价：营业额 / 有效订单数
         * 新增用户：当日新增用户的数量
         */
        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);

        // 获取当日已完成订单的数量
        map.put("status", Orders.COMPLETED);
        Integer completedOrder = orderMapper.countByMap(map);
        // 当日已经完成订单的总金额
        Double turnover = orderMapper.sumByMap(map);
        // 使用完之后将map中的status删除
        map.remove("status");
        // 获取当日所有状态订单的数量
        Integer allOrders = orderMapper.countByMap(map);

        double orderCompletedRate = 0.0;
        double unitPrice = 0.0;
        if (!completedOrder.equals(0) && !allOrders.equals(0)) {
            // 当日的订单完成率
            orderCompletedRate =  completedOrder / (double)allOrders;
            // 当日已完成的订单的平均客单价
            unitPrice = turnover / completedOrder;
        }

        // 当日新增用户
        Integer newUsers = userMapper.countByMap(map);
        return BusinessDataVO.builder()
                .newUsers(newUsers)
                .turnover(turnover)
                .unitPrice(unitPrice)
                .orderCompletionRate(orderCompletedRate)
                .build();
    }


    /**
     * 查询订单管理数据
     *
     * @return
     */
    public OrderOverViewVO getOrderOverView() {
        Map<String,Object> map = new HashMap<>();

        // 查询所有订单
        Integer allOrders = orderMapper.countByMap(map);

        // 查询已完成订单
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.countByMap(map);

        // 查询已取消订单
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countByMap(map);

        // 查询待派送订单
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countByMap(map);

        // 查询待接单订单
        map.put("status", Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.countByMap(map);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    public DishOverViewVO getDishOverView() {
        Map<String, Object> map = new HashMap<>();
        // 启用菜品数量
        map.put("status", StatusConstant.ENABLE);
        Integer sold = dishMapper.countByMap(map);

        // 未启用菜品数量
        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = dishMapper.countByMap(map);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询套餐总览
     *
     * @return
     */
    public SetmealOverViewVO getSetmealOverView() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = setmealMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = setmealMapper.countByMap(map);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}
