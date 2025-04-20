package com.sky.mapper;

import com.alibaba.druid.filter.AutoLoad;
import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {
    @Select("select count(*) from dish where category_id = #{CategoryId}")
    Integer findDishMapperByCategoryId(long CategoryId);

    @AutoFill(OperationType.INSERT)
    void addDish(Dish dish);
}
