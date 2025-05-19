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

/**
 * 菜品口味
 */
// 标识该实体类对应数据库中的表名为 dish_flavor
@TableName("dish_flavor")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishFlavor implements Serializable {

    // 使用 @Serial 注解解决 serialVersionUID 提示问题
    @Serial
    private static final long serialVersionUID = 1L;

    // 标识该字段为主键，且使用数据库自增策略
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 菜品id
    @TableField("dish_id")
    private Long dishId;

    // 口味名称
    @TableField("name")
    private String name;

    // 口味数据list
    @TableField("value")
    private String value;
}