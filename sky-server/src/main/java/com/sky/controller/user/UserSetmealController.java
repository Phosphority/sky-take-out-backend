package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController("userSetmealController")
@Api(tags = "用户套餐浏览接口")
@Slf4j
@RequestMapping("/user/setmeal")
public class UserSetmealController {

    @Resource
    private SetmealService setmealService;

    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品")
    @Cacheable(cacheNames = "setmealCache",key = "#p0")
    public Result<List<DishItemVO>> getSetmealDish(@PathVariable long id) {
        log.info("套餐id为:{}",id);
        List<DishItemVO> dishItemVOS = setmealService.findDishesBySetmealId(id);
        return Result.success(dishItemVOS);
    }

    @GetMapping("list")
    @ApiOperation("根据分类id查询分类下所属的套餐")
    @Cacheable(cacheNames = "setmealCache",key = "#p0")
    public Result<List<Setmeal>> getSetmeal(@RequestParam long categoryId) {
        log.info("分类id为:{}", categoryId);
        List<Setmeal> setmealList = setmealService.findByCategoryId(categoryId);
        return Result.success(setmealList);
    }
}
