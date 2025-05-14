package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
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
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.*;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
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
        // 根据id查询order
        Orders orderDB = ordersMapper.reminder(id);

        if(orderDB == null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("type", 2);
        map.put("orderId", orderDB.getId());
        map.put("content", "订单号:" + orderDB.getNumber() + "，用户催单请及时处理");
        String json = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(json);
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

    /**
     * 历史订单分页查询
     *
     * @param
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 开启分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> ordersPage = ordersMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> orderVOList = new ArrayList<>();
        if (ordersPage != null && !ordersPage.isEmpty()) {
            ordersPage.forEach(order -> {
                Long orderId = order.getId();

                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
                OrderVO orderVO = OrderVO.builder()
                        .orderDetailList(orderDetails)
                        .build();

                orderVOList.add(orderVO);
            });
        }

        return new PageResult(ordersPage.getTotal(), orderVOList);
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orderDB = ordersMapper.getById(ordersRejectionDTO.getId());

        // 订单不存在或订单状态不为2(待接单状态)才可以拒单
        if (orderDB == null || !orderDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Integer payStatus = orderDB.getPayStatus();
        if (payStatus.equals(Orders.PAID)) {
            // 拒单需要退款，还需要更新订单的信息
            Orders orders = Orders.builder()
                    .payStatus(Orders.REFUND)
                    .status(Orders.REFUNDED)
                    .rejectionReason(ordersRejectionDTO.getRejectionReason())
                    .cancelTime(LocalDateTime.now())
                    .id(ordersRejectionDTO.getId())
                    .build();

            ordersMapper.update(orders);
        }
    }


    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = Orders.builder()
                .status(Orders.CANCELLED)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .id(ordersCancelDTO.getId())
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void delivery(Long id) {
        // 根据id查询订单
        Orders orderDB = ordersMapper.getById(id);

        // 判断订单是否存在，以及判断订单是否是3(已接单)的状态,如果不是已接单的状态就抛出状态异常
        if (orderDB == null || !orderDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 之后再开始修改订单
        Orders orders = Orders.builder()
                .status(Orders.DELIVERY_IN_PROGRESS)
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(50))
                .id(id)
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void complete(Long id) {
        // 根据id查询order
        Orders orderDB = ordersMapper.getById(id);

        // 判断是否存在以及是否为4(派送中)
        if (orderDB == null || !orderDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = Orders.builder()
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .id(id)
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public OrderStatisticsVO statistics() {
        Integer toBeConfirmed = ordersMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = ordersMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = ordersMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        return OrderStatisticsVO.builder()
                .toBeConfirmed(toBeConfirmed)
                .confirmed(confirmed)
                .deliveryInProgress(deliveryInProgress)
                .build();
    }

    @Override
    public OrderVO details(Long id) {
        Orders orders = ordersMapper.getById(id);

        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orders.getId());
        return OrderVO.builder()
                .orderDetailList(orderDetails)
                .build();
    }

    @Override
    public void userCancelById(Long id) {
        // 根据id查询出Order
        Orders orderDB = ordersMapper.getById(id);

        // 判断order是否为null
        if (orderDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        } else if (orderDB.getStatus() > Orders.TO_BE_CONFIRMED) {       // 骑手已经接单不支持用户取消订单
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = Orders.builder().build();
        // 如果已经支付则退款
        if (orderDB.getPayStatus().equals(Orders.PAID)) {
            orders.setPayStatus(Orders.REFUNDED);
        }

        orders.setStatus(Orders.CANCELLED)
                .setCancelReason("用户取消")
                .setId(orderDB.getId())
                .setCancelTime(LocalDateTime.now());
        ordersMapper.update(orders);
    }

    @Override
    public void repetition(Long id) {
        // 得到userId
        Long userId = BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID);

        // 根据订单id查询出订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        List<ShoppingCart> shoppingCartList = new ArrayList<>();

        if (orderDetailList != null && !orderDetailList.isEmpty()) {
            orderDetailList.forEach(orderDetail -> {
                ShoppingCart shoppingCart = ShoppingCart.builder()
                        .userId(userId)
                        .createTime(LocalDateTime.now())
                        .build();

                BeanUtils.copyProperties(orderDetail, shoppingCartList, "id");  // 第三个参数填的是在copy中忽略的属性
                shoppingCartList.add(shoppingCart);
            });
        }

        // TODO 这里可以将原先的单词插入直接删除，替换成这个批量插入
        shoppingCartMapping.insertBatch(shoppingCartList);
    }

}








