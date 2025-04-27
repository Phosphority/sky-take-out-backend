package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {


    @AutoFill(OperationType.INSERT)
    @Insert("insert into category(type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void add(Category category);

    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    List<Category> findListByType(Integer type, Integer status);

    Page<Category> page(@Param("name") String name,@Param("type") Integer type);

    @Delete("delete from category where id = #{id}")
    boolean delete(long id);
}

