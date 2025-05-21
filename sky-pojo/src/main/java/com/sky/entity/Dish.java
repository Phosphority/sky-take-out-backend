package com.sky.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品
 */
@TableName("dish")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dish implements Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 菜品名称
    @TableField("name")
    private String name;

    // 菜品分类id
    @TableField("category_id")
    private Long categoryId;

    // 菜品价格
    @TableField("price")
    private BigDecimal price;

    // 图片
    @TableField("image")
    private String image;

    // 描述信息
    @TableField("description")
    private String description;

    // 0 停售 1 起售
    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("create_user")
    private Long createUser;

    @TableField("update_user")
    private Long updateUser;
}