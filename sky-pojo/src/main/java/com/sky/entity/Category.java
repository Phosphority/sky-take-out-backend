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
import java.time.LocalDateTime;

// 标识该实体类对应数据库中的表名为 category
@TableName("category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {

    // 使用 @Serial 注解解决 serialVersionUID 提示问题
    @Serial
    private static final long serialVersionUID = 1L;

    // 标识该字段为主键，且使用数据库自增策略
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 类型: 1菜品分类 2套餐分类
    @TableField("type")
    private Integer type;

    // 分类名称
    @TableField("name")
    private String name;

    // 顺序
    @TableField("sort")
    private Integer sort;

    // 分类状态 0标识禁用 1表示启用
    @TableField("status")
    private Integer status;

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