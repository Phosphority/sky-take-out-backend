package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    void add(CategoryDTO categoryDTO);

    void update(CategoryDTO categoryDTO);

    void updateStatus(Integer status, long id);

    List<Category> findListByType(Integer type);

    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    boolean delete(long id);
}
