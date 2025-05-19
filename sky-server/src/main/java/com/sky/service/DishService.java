package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService extends IService<Dish> {
    void addDish(DishDTO dishDTO);

    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(List<Long> dishIds);

    void updateDishWithFlavor(DishDTO dishDTO);

    DishVO findById(Long id);

    void updateStatus(Integer status, long id);

    List<DishVO> findByCategoryId(long categoryId);
}
