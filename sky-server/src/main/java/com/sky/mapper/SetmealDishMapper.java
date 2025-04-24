package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    void addSetmealDish(List<SetmealDish> setmealDishes);

    boolean deleteBatch(List<Long> setmealIds);

    @Select("select dish_id from setmeal_dish where setmeal_id = #{setmealId}")
    List<Long> findDishIdBySetmealId(long setmealId);
}
