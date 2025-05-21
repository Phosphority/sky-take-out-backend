package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user/user")
@Api(tags = "用户接口")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @ApiOperation("登录")
    public Result<UserLoginVO> wxLogin(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户一次性授权码{}", userLoginDTO);
        UserLoginVO userLoginVO = userService.wxLogin(userLoginDTO);
        // 生成jwt令牌并且返回userLoginVO
        UserLoginVO loginVO = getToken(userLoginVO);
        // 返回userLoginVO
        return Result.success(loginVO);
    }

    public UserLoginVO getToken(UserLoginVO userLoginVO) {
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,userLoginVO.getId());
        // 生成jwtToken令牌
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        userLoginVO.setToken(token);
        return userLoginVO;
    }
}























