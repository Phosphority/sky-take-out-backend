package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.io.Serializable;

/**
 * C端用户登录
 */
@Data
@ApiModel("用户登录请求参数模型")
public class UserLoginDTO implements Serializable {

    @ApiParam("微信用户授权码")
    private String code;

}
