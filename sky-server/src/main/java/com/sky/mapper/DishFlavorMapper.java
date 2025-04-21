package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void addBatch(List<DishFlavor> flavors);

    void deleteBatch(List<Long> dishIds);

    void insertDishFlavor(List<DishFlavor> flavors, Long dishId);

    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> findFlavorByDishId(Long dishId);
}
