package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.entity.Orders;
import com.sky.vo.OrdersHistoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface OrdersMapper {

    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Select("select * from orders where status = #{status} and order_time < #{time} ")
    List<Orders> findByStatusAndOrderTimeLT(@Param("status") Integer status, @Param("time") LocalDateTime time);

    @Select("select * from orders where id = #{id}")
    Orders reminder(Long id);

    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where number = #{number}")
    void updateStatus(@Param("orderStatus") Integer orderStatus,@Param("orderPaidStatus") Integer orderPaidStatus,@Param("check_out_time") LocalDateTime check_out_time,@Param("number") String number);

    Page<OrdersHistoryVO> historyOrders(Orders orders);
}
