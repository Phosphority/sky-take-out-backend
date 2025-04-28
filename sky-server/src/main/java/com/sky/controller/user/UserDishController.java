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
public class UserDishController {

    @Resource
    private DishService dishService;
    @Resource
    private RedisTemplate<String,DishVO> redisTemplate;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(@RequestParam long categoryId) {
        // NOTICE 使用redis将数据缓存到内存中
        // 判断缓存中是否有该分类id的数据
        ListOperations<String,DishVO> listOperations = redisTemplate.opsForList();
        List<DishVO> list = listOperations.range("dish_" + categoryId, 0, -1);
        if(list != null && !list.isEmpty()) {
            return Result.success(list);
        }
        // 如果没有缓存的话就从数据库中查询
        log.info("查询分类id为:{}", categoryId);
        list = dishService.findByCategoryId(categoryId);
        // 将数据库中查询到的数据存入缓存
        listOperations.leftPushAll("dish_" + categoryId, list);
        return Result.success(list);
    }
}
