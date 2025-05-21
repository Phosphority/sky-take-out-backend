package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController("userDishController")
@Api(tags = "用户菜品浏览接口")
@Slf4j
@RequestMapping("/user/dish")
public class DishController {

    @Resource
    private DishService dishService;
    @Resource
    private RedisTemplate<String,DishVO> redisTemplate;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(@RequestParam long categoryId) {
        log.info("查询分类id为:{}", categoryId);
        List<DishVO> list = dishService.findByCategoryId(categoryId);
        return Result.success(list);
    }
}
