package com.sky.controller.user;

import com.sky.constant.ShopStatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/user/shop")
@Api(tags = "用户店铺接口")
public class userShopController {

    @Resource
    private RedisTemplate<String,Integer> redisTemplate;

    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public Result<Integer> getStatus() {
        Integer shopStatus = redisTemplate.opsForValue().get(ShopStatusConstant.SHOP_STATUS);
        log.info("店铺当前的营业状态为:{}", shopStatus == 1 ? ShopStatusConstant.STATUS_OPEN :ShopStatusConstant.STATUS_CLOSE);
        return Result.success(shopStatus);
    }
}
