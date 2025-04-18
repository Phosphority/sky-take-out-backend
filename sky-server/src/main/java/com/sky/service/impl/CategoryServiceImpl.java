package com.sky.service.impl;

import com.sky.mapper.CategoryMapper;
import com.sky.service.CategoryService;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Log4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;
}
