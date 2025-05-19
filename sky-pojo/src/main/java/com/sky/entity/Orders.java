package com.sky.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单
 */
// 标识该实体类对应数据库中的表名为 orders
@TableName("orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Orders implements Serializable {

    /**
     * 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     */
    public static final Integer PENDING_PAYMENT = 1;
    public static final Integer TO_BE_CONFIRMED = 2;
    public static final Integer CONFIRMED = 3;
    public static final Integer DELIVERY_IN_PROGRESS = 4;
    public static final Integer COMPLETED = 5;
    public static final Integer CANCELLED = 6;
    public static final Integer REFUNDED = 7;

    /**
     * 支付状态 0未支付 1已支付 2退款
     */
    public static final Integer UN_PAID = 0;
    public static final Integer PAID = 1;
    public static final Integer REFUND = 2;

    @Serial
    private static final long serialVersionUID = 1L;

    // 标识该字段为主键，且使用数据库自增策略
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 订单号
    @TableField("number")
    private String number;

    // 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
    @TableField("status")
    private Integer status;

    // 下单用户id
    @TableField("user_id")
    private Long userId;

    // 地址id
    @TableField("address_book_id")
    private Long addressBookId;

    // 下单时间
    @TableField("order_time")
    private LocalDateTime orderTime;

    // 结账时间
    @TableField("checkout_time")
    private LocalDateTime checkoutTime;

    // 支付方式 1微信，2支付宝
    @TableField("pay_method")
    private Integer payMethod;

    // 支付状态 0未支付 1已支付 2退款
    @TableField("pay_status")
    private Integer payStatus;

    // 实收金额
    @TableField("amount")
    private BigDecimal amount;

    // 备注
    @TableField("remark")
    private String remark;

    // 用户名
    @TableField("user_name")
    private String userName;

    // 手机号
    @TableField("phone")
    private String phone;

    // 地址
    @TableField("address")
    private String address;

    // 收货人
    @TableField("consignee")
    private String consignee;

    // 订单取消原因
    @TableField("cancel_reason")
    private String cancelReason;

    // 订单拒绝原因
    @TableField("rejection_reason")
    private String rejectionReason;

    // 订单取消时间
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    // 预计送达时间
    @TableField("estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    // 配送状态  1立即送出  0选择具体时间
    @TableField("delivery_status")
    private Integer deliveryStatus;

    // 送达时间
    @TableField("delivery_time")
    private LocalDateTime deliveryTime;

    // 打包费
    @TableField("pack_amount")
    private int packAmount;

    // 餐具数量
    @TableField("tableware_number")
    private int tablewareNumber;

    // 餐具数量状态  1按餐量提供  0选择具体数量
    @TableField("tableware_status")
    private Integer tablewareStatus;
}