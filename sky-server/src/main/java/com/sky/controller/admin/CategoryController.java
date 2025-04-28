package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@Api(tags = "菜品分类接口")
@RequestMapping("/admin/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @PostMapping
    @ApiOperation("新增分类")
    public Result add(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类为,{}", categoryDTO);
        categoryService.add(categoryDTO);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改分类")
    public Result update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类为,{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改分类状态")
    public Result updateStatus(@PathVariable("status") Integer status,long id) {
        log.info("更新分类状态:{}", status);
        categoryService.updateStatus(status,id);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类分页查询:{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result delete(@RequestParam long id) {
        log.info("根据id:{}删除分类",id);
        if(categoryService.delete(id)){
            return Result.success();
        }
        return Result.error("fail");
    }

    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> findListByType(@RequestParam("type") Integer type) {
        log.info("根据类型:{} 查询类型", type);
        List<Category> categoryList = categoryService.findListByType(type);
        return Result.success(categoryList);
    }

}












