package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void addSetmeal(SetmealDTO setmealDTO);

    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteBatch(List<Long> ids);

    SetmealVO findById(long id);

    void updateSetmeal(SetmealDTO setmealDTO);

    void updateSetmealStatus(Integer status, long id);

    List<Setmeal> findByCategoryId(long categoryId);

    List<DishItemVO> findDishesBySetmealId(long id);
}
