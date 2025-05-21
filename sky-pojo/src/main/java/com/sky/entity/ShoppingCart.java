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
 * 购物车
 */
// 标识该实体类对应数据库中的表名为 shopping_cart
@TableName("shopping_cart")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ShoppingCart implements Serializable {

    // 使用 @Serial 注解解决 serialVersionUID 提示问题
    @Serial
    private static final long serialVersionUID = 1L;

    // 标识该字段为主键，且使用数据库自增策略
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 名称
    @TableField("name")
    private String name;

    // 用户id
    @TableField("user_id")
    private Long userId;

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

    // 购物车中的菜品创建时间
    @TableField("create_time")
    private LocalDateTime createTime;
}