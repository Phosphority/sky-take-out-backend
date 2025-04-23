package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Resource
    private SetmealService setmealService;

    @PostMapping
    @ApiOperation("新增套餐")
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐信息:{}", setmealDTO);
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(@ModelAttribute SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询参数:{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("套餐批量删除")
    public Result deleteBatch(@RequestParam List<Long> ids) {
        log.info("批量删除的套餐id为:{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改套餐")
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐信息:{}", setmealDTO);
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<Setmeal> findById(@PathVariable Long id) {
        log.info("查询套餐Id为");
        Setmeal setmeal = setmealService.findById(id);
        return Result.success(setmeal);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改套餐的起售状态")
    public Result updateSetmealStatus(@PathVariable Integer status, @RequestParam long id) {
        log.info("修改id为{}套餐的起售状态为{}", id, status);
        setmealService.updateSetmealStatus(status,id);
        return Result.success();
    }
}














