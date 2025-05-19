package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Category;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user where openid = #{openid}")
    User findByOpenid(String openid);

    void addUser(User user);

    @Select("select * from user where id = #{id}")
    User getById(Long id);

    Integer countByMap(Map<String, Object> map);
}
