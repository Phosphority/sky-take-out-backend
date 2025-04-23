package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface SetmealMapper {
    @Select("select count(*) from setmeal where category_id = #{CategoryId}")
    Integer findByCategoryId(long CategoryId);

    Integer findDishByDishId(List<Long> dishIds);

    @AutoFill(OperationType.INSERT)
    Long addSetmeal(Setmeal setmeal);

    Page<Setmeal> page(Setmeal setmeal);

    Integer findBatchStatus(List<Long> ids);

    boolean deleteBatch(List<Long>  ids);

    @Select("select * from setmeal where id = #{id}")
    Setmeal findById(Long id);

    @AutoFill(OperationType.UPDATE)
    @Update("update setmeal " +
            "set category_id = #{categoryId}, name = #{name}, price = #{price} , description = #{description}," +
            " image = #{image},update_time = #{updateTime},update_user = #{updateUser} " +
            "where id = #{id}")
    boolean updateSetmeal(Setmeal setmeal);

    @AutoFill(OperationType.UPDATE)
    @Update("update setmeal set status = #{status} where id = #{id}")
    void updateSetmealStatus(Setmeal setmeal);
}








