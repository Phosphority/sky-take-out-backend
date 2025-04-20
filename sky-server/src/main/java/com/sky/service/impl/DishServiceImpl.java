package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishFlavorMapper dishFlavorMapper;

    @Override
    public void addDish(DishDTO dishDTO) {
        Dish dish = Dish.builder().status(StatusConstant.DISABLE).build();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.addDish(dish);
        // 获取插入后的主键ID
        long dishId = dish.getId();

        if (dishDTO.getFlavors() != null && !dishDTO.getFlavors().isEmpty()) {
            // 将每个菜品口味所需要的菜品id使用forEach注入
            dishDTO.getFlavors().forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.addBatch(dishDTO.getFlavors());
        }
    }

}





















