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

/**
 * 订单明细
 */
// 标识该实体类对应数据库中的表名为 order_detail
@TableName("order_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class OrderDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 标识该字段为主键，且使用数据库自增策略
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 名称
    @TableField("name")
    private String name;

    // 订单id
    @TableField("order_id")
    private Long orderId;

    // 菜品id
    @TableField("dish_id")
    private Long dishId;

    // 套餐id
    @TableField("setmeal_id")
    private Long setmealId;

    // 口味
    @TableField("dish_flavor")
    private String dishFlavor;

    // 数量
    @TableField("number")
    private Integer number;

    // 金额
    @TableField("amount")
    private BigDecimal amount;

    // 图片
    @TableField("image")
    private String image;
}