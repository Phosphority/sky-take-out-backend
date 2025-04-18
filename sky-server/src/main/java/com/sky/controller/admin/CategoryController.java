package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import io.swagger.annotations.Api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@Api("菜品分类接口")
@RequestMapping("/admin/category")
public class CategoryController {

    @Resource
    private EmployeeService employeeService;

    @PostMapping
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("添加分类为,{}", categoryDTO);
        return Result.success();
    }


}












