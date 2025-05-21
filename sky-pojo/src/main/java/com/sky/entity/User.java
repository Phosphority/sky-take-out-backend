package com.sky.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User implements Serializable {

    // 使用 @Serial 注解
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 微信用户唯一标识
    @TableField("openid")
    private String openid;

    // 姓名
    @TableField("name")
    private String name;

    // 手机号
    @TableField("phone")
    private String phone;

    // 性别 0 女 1 男
    @TableField("sex")
    private String sex;

    // 身份证号，使用 @JsonIgnore 避免序列化
    @JsonIgnore
    @TableField("id_number")
    private String idNumber;

    // 头像
    @TableField("avatar")
    private String avatar;

    // 注册时间
    @TableField("create_time")
    private LocalDateTime createTime;
}