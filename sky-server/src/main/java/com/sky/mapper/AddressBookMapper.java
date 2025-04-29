package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.DeleteMapping;

@Mapper
public interface AddressBookMapper {
    @Insert("insert into address_book(user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label) " +
            "VALUES(#{userId},#{consignee},#{sex},#{phone},#{province_code},#{province_name},#{city_code},#{city_name},#{district_code},#{district_name},#{detail},#{label}) ")
    void add(AddressBook addressBook);

    @Select("select * from address_book where user_id = #{userId}")
    AddressBook list(Long userId);

    @Delete("delete from address_book where user_id = #{userId}")
    void delete(Long userId);
}
