package com.sky.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 套餐菜品关系
 */
// 表明该实体类对应数据库中的表名为 setmeal_dish
@TableName("setmeal_dish")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetmealDish implements Serializable {

    // 使用 @Serial 注解解决 serialVersionUID 提示问题
    @Serial
    private static final long serialVersionUID = 1L;

    // 标记该字段为主键，使用数据库自增策略
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 套餐id
    @TableField("setmeal_id")
    private Long setmealId;

    // 菜品id
    @TableField("dish_id")
    private Long dishId;

    // 菜品名称 （冗余字段）
    @TableField("name")
    private String name;

    // 菜品原价
    @TableField("price")
    private BigDecimal price;

    // 份数
    @TableField("copies")
    private Integer copies;
}