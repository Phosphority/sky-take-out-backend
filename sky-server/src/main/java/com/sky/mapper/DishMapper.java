package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {
    @Select("select count(*) from dish where category_id = #{CategoryId}")
    Integer findDishMapperByCategoryId(long CategoryId);

    @AutoFill(OperationType.INSERT)
    void addDish(Dish dish);

    Page<DishVO> page(Dish dish);

    boolean deleteBatch(List<Long> dishIds);

    Integer findOnSaleDishStatus(List<Long> dishIds);

    @AutoFill(OperationType.UPDATE)
    boolean updateDish(Dish dish);

    DishVO findById(Long id);

    @AutoFill(OperationType.UPDATE)
    @Update("update dish set status = #{status},update_time = #{updateTime},update_user = #{updateUser} where id = #{id}")
    void updateStatus(Dish dish);

    List<DishVO> findByCategoryId(@Param("categoryId") Long categoryId,@Param("status") Integer status);

    Integer countByMap(Map<String, Object> map);
}
