package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrdersService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.*;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrdersServiceImpl implements OrdersService {

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private ShoppingCartMapping shoppingCartMapping;
    @Resource
    private AddressBookMapper addressBookMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private WeChatPayUtil weChatPayUtil;
    @Resource
    private WebSocketServer webSocketServer;


    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID);
        // 1.判断地址簿和购物车是否为空
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> shoppingCarts = shoppingCartMapping.list(shoppingCart);
        AddressBook addressBook = addressBookMapper.findById(ordersSubmitDTO.getAddressBookId());
        if ((shoppingCarts == null || shoppingCarts.isEmpty())) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        } else if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 2.将数据插入订单中,并且将id回传到orders中
        Orders orders = Orders.builder().build();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setNumber(String.valueOf(System.currentTimeMillis()) + userId)
                .setConsignee(addressBook.getConsignee())
                .setPayStatus(Orders.UN_PAID)
                .setUserId(userId)
                .setPhone(addressBook.getPhone())
                .setAddress(addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName())
                .setStatus(1)
                .setOrderTime(LocalDateTime.now());
        ordersMapper.insert(orders);

        // 3.将购物车中的商品批量插入到order_detail表中
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail orderDetail = OrderDetail.builder().build();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId())
                    .setId(null);
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetails);

        // 4.将当前用户购物车清空
        shoppingCartMapping.cleanShoppingCart(userId);

        // 5.填入参数,并返回
        return OrderSubmitVO.builder()
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .build();
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        //发现没有将结账时间 check_out属性赋值，所以在这里更新
        LocalDateTime check_out_time = LocalDateTime.now();
        String orderNumber = ordersPaymentDTO.getOrderNumber();
        // 订单状态修改未待接单，支付状态为已支付
        ordersMapper.updateStatus(Orders.TO_BE_CONFIRMED, Orders.PAID, check_out_time, orderNumber);
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = ordersMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        ordersMapper.update(orders);

        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("orderId", ordersDB.getId());
        map.put("content", "您有新的订单:" + ordersDB.getNumber() + "请及时处理");

        String json = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

    @Override
    public void reminder(Long id) {
        Orders order = ordersMapper.reminder(id);

        Map<String, Object> map = new HashMap<>();
        map.put("type", 2);
        map.put("orderId", order.getId());
        map.put("content", "您有新的订单:" + order.getNumber() + "请及时处理");
        String json = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

    /**
     * 历史订单分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        Orders orders = Orders.builder().build();
        BeanUtils.copyProperties(ordersPageQueryDTO, orders);
        orders.setUserId(BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID));
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<OrdersHistoryVO> ordersPage = ordersMapper.historyOrders(orders);
        return new PageResult(ordersPage.getTotal(), ordersPage.getResult());
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        Orders orders = Orders.builder().build();
        BeanUtils.copyProperties(ordersPageQueryDTO, orders);
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> ordersList = ordersMapper.conditionSearch(ordersPageQueryDTO);

        List<OrdersSearchVO> ordersSearchVOS = new ArrayList<>();
        if (ordersList != null && !ordersList.isEmpty()) {
            ordersList.forEach(order -> {
                OrdersSearchVO ordersSearchVO = OrdersSearchVO.builder().build();
                StringBuilder sb = new StringBuilder();
                List<String> dishesNames = orderDetailMapper.findDishesName(order.getId());
                for (int i = 0; i <= dishesNames.size() - 1; i++) {
                    sb.append(dishesNames.get(i));
                }
                BeanUtils.copyProperties(order, ordersSearchVO);
                ordersSearchVO.setOrderDishes(sb.toString());
                ordersSearchVOS.add(ordersSearchVO);
            });
        }

        return new PageResult(ordersList.getTotal(), ordersSearchVOS);
    }

    @Override
    public void confirm(Long id) {
        Orders orders = Orders.builder()
                .status(Orders.TO_BE_CONFIRMED)
                .id(id)
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void rejection(Long id, String rejectionReason) {
        Orders orders = Orders.builder()
                .status(Orders.CANCELLED)
                .rejectionReason(rejectionReason)
                .id(id)
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void cancel(Long id, String cancelReason) {
        Orders orders = Orders.builder()
                .status(Orders.CANCELLED)
                .cancelReason(cancelReason)
                .id(id)
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void delivery(Long id) {
        Orders orders = Orders.builder()
                .status(Orders.DELIVERY_IN_PROGRESS)
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(50))
                .id(id)
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void complete(Long id) {
        Orders orders = Orders.builder()
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .id(id)
                .build();
        ordersMapper.update(orders);
    }

}








