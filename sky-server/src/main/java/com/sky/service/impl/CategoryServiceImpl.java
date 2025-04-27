package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private SetmealMapper setmealMapper;

    @Override
    public void add(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .sort(categoryDTO.getSort())
                .type(categoryDTO.getType())
                .name(categoryDTO.getName())
                .status(StatusConstant.DISABLE)
                .build();
        categoryMapper.add(category);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .id(categoryDTO.getId())
                .sort(categoryDTO.getSort())
                .type(categoryDTO.getType())
                .name(categoryDTO.getName())
                .build();
        categoryMapper.update(category);
    }

    @Override
    public void updateStatus(Integer status, long id) {
        Category category = Category.builder()
                .status(status)
                .id(id)
                .build();
        categoryMapper.update(category);
    }

    @Override
    public List<Category> findListByType(Integer type) {
        return categoryMapper.findListByType(type);
    }

    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        Page<Category> categoryPage = categoryMapper.page(categoryPageQueryDTO.getName(),categoryPageQueryDTO.getType());
        return new PageResult(categoryPage.getTotal(),categoryPage.getResult());
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        int count = dishMapper.findDishMapperByCategoryId(id);
        if( count > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        count = setmealMapper.countByCategoryId(id);
        if( count <= 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        return categoryMapper.delete(id);
    }
}











