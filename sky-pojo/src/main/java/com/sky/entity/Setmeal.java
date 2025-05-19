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
import java.time.LocalDateTime;

/**
 * 套餐
 */
// 指定对应数据库表名为 setmeal
@TableName("setmeal")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Setmeal implements Serializable {

    // 添加 @Serial 注解解决提示问题
    @Serial
    private static final long serialVersionUID = 1L;

    // 标记为主键，采用数据库自增策略
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 分类id
    @TableField("category_id")
    private Long categoryId;

    // 套餐名称
    @TableField("name")
    private String name;

    // 套餐价格
    @TableField("price")
    private BigDecimal price;

    // 状态 0:停用 1:启用
    @TableField("status")
    private Integer status;

    // 描述信息
    @TableField("description")
    private String description;

    // 图片
    @TableField("image")
    private String image;

    // 创建时间
    @TableField("create_time")
    private LocalDateTime createTime;

    // 更新时间
    @TableField("update_time")
    private LocalDateTime updateTime;

    // 创建人
    @TableField("create_user")
    private Long createUser;

    // 修改人
    @TableField("update_user")
    private Long updateUser;
}