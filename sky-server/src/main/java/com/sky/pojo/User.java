package com.sky.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")  //对应的表名
public class User {
    @TableId(type = IdType.AUTO)   //对应的主键,
    int id;
    @TableField("name")   //对应的字段
    String name;
    @TableField("email")
    String email;
    @TableField("password")
    String password;
}