package com.sky.controller.admin;

import com.sky.constant.ShopStatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class adminShopController {

    @Resource
    private RedisTemplate<String, Integer> redisTemplate;

    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result updateStatus(@PathVariable Integer status) {
        log.info("将营业状态设置为:{}", status == 1 ? ShopStatusConstant.STATUS_OPEN : ShopStatusConstant.STATUS_CLOSE);
        redisTemplate.opsForValue().set(ShopStatusConstant.SHOP_STATUS, status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public Result<Integer> getStatus() {
        Integer shopStatus = redisTemplate.opsForValue().get(ShopStatusConstant.SHOP_STATUS);
        if (shopStatus == null) {
            shopStatus = 0;
        }
        log.info("店铺当前的营业状态为:{}", shopStatus == 1 ? ShopStatusConstant.STATUS_OPEN : ShopStatusConstant.STATUS_CLOSE);
        return Result.success(shopStatus);
    }
}
