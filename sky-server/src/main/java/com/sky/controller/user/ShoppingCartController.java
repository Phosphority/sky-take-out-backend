package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/shoppingCart")
@Api(tags = "用户端购物车接口")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加的购物车项目信息为:{}", shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> listShoppingCart() {
        log.info("用户id为:{}", BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID));
        List<ShoppingCart> list = shoppingCartService.listShoppingCart();
        return Result.success(list);
    }

    @DeleteMapping("/clean")
    @ApiOperation("清除购物车")
    public Result cleanShoppingCart() {
        log.info("清除该用户购物车中所有的商品");
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    @PostMapping("/sub")
    @ApiOperation("删除购物车中的一个商品")
    public Result subShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("删除购物车数据为{}", shoppingCartDTO);
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }


}










