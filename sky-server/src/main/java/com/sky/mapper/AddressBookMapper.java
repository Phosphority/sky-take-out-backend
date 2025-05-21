package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
    @Insert("insert into address_book(user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label) " +
            "VALUES(#{userId},#{consignee},#{sex},#{phone},#{provinceCode},#{provinceName},#{cityCode},#{cityName},#{districtCode},#{districtName},#{detail},#{label}) ")
    void add(AddressBook addressBook);

    @Select("select * from address_book where user_id = #{userId}")
    List<AddressBook> list(Long userId);

    @Delete("delete from address_book where id = #{id}")
    void delete(Long id);

    @Select("select * from address_book where user_id = #{userId} and is_default = 1")
    AddressBook getDefault(Long userId);

    void update(AddressBook addressBook);

    @Select("select * from address_book where id = #{id}")
    AddressBook findById(Long id);
}
