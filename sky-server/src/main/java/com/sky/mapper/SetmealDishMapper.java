package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.annotation.AutoFill;
import com.sky.entity.Category;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {

    void addSetmealDish(List<SetmealDish> setmealDishes);

    boolean deleteBatch(List<Long> setmealIds);

    @Select("select dish_id from setmeal_dish where setmeal_id = #{setmealId}")
    List<Long> findDishIdBySetmealId(long setmealId);

    List<DishItemVO> findDishesBySetmealId(long id);
}
