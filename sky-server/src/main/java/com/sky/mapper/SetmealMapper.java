package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface SetmealMapper {
    @Select("select count(*) from setmeal where category_id = #{CategoryId}")
    Integer findByCategoryId(long CategoryId);

    Boolean findDishByDishId(List<Long> dishIds);
}
