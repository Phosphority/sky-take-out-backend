package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.WeChatLoginConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.BaseException;
import com.sky.exception.RequestException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    // 用户登录通用请求接口
    private static final String USER_GET_OPENID = "https://api.weixin.qq.com/sns/jscode2session";

    @Resource
    private UserMapper userMapper;
    @Resource
    private WeChatProperties weChatProperties;


    @Override
    public UserLoginVO wxLogin(UserLoginDTO userLoginDTO) {
        Map<String, String> params = new HashMap<>();
        params.put(WeChatLoginConstant.APPID, weChatProperties.getAppid());
        params.put(WeChatLoginConstant.SECRET, weChatProperties.getSecret());
        // 用户一次性授权码
        params.put(WeChatLoginConstant.JS_CODE, userLoginDTO.getCode());
        params.put(WeChatLoginConstant.GRANT_TYPE, "authorization_code");
        String jsonString = (String) HttpClientUtil.doGet(USER_GET_OPENID, params);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        // 判断请求是否成功
        if (jsonObject.getString("errcode") == null) {
            String openid = jsonObject.getString("openid");
            // 判断是否是新用户,返回的openid是用户持久使用的唯一标识
            User user = userMapper.findByOpenid(openid);
            if (user == null) {
                user = User.builder()
                        .openid(openid)
                        .createTime(LocalDateTime.now())
                        .build();
                // 添加新用户
                userMapper.addUser(user);
            }
            // 返回userLoginVo
            return UserLoginVO.builder()
                    .openid(openid)
                    .id(user.getId())
                    .build();
        }else{
            throw new RequestException("请求成功，返回结果失败");
        }
    }
}

















