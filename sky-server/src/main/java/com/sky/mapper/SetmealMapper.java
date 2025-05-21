package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Category;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;


@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
    @Select("select count(*) from setmeal where category_id = #{CategoryId}")
    Integer countByCategoryId(long CategoryId);

    Integer findDishByDishId(List<Long> dishIds);

    @AutoFill(OperationType.INSERT)
//  NOTICE @Options(useGeneratedKeys = true,keyColumn = "id",keyProperty = "id")  这里其实还可以使用注解的方式来简化操作
    void addSetmeal(Setmeal setmeal);

    Page<SetmealVO> page(Setmeal setmeal);

    Integer findBatchStatus(List<Long> ids);

    boolean deleteBatch(List<Long>  ids);

    SetmealVO findById(long id);

    @AutoFill(OperationType.UPDATE)
    @Update("update setmeal " +
            "set category_id = #{categoryId}, name = #{name}, price = #{price} , description = #{description}," +
            " image = #{image},update_time = #{updateTime},update_user = #{updateUser} " +
            "where id = #{id}")
    boolean updateSetmeal(Setmeal setmeal);

    @AutoFill(OperationType.UPDATE)
    @Update("update setmeal set status = #{status} where id = #{id}")
    void updateSetmealStatus(Setmeal setmeal);

    List<Setmeal> findByCategoryId(@Param("categoryId") Long categoryId,@Param("status") Integer status);

    @Select("select count(*) from setmeal where status = #{status}")
    Integer countByMap(Map<String, Object> map);
}








