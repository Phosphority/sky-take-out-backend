package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService extends IService<Category> {
    void add(CategoryDTO categoryDTO);

    void update(CategoryDTO categoryDTO);

    void updateStatus(Integer status, long id);

    List<Category> findListByType(Integer type);

    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    boolean delete(long id);
}
