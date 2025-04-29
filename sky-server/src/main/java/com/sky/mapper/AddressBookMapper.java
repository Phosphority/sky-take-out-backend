package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AddressBookMapper {
    @Insert("insert into address_book(user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label) " +
            "VALUES(#{userId},#{consignee},#{sex},#{phone},#{province_code},#{province_name},#{city_code},#{city_name},#{district_code},#{district_name},#{detail},#{label}) ")
    void add(AddressBook addressBook);

    @Select("select * from address_book where user_id = #{userId}")
    AddressBook list(Long userId);

    @Delete("delete from address_book where id = #{id}")
    void delete(Long id);

    @Update("update address_book set is_default = 1 where id = #{id} and user_id = #{userId}")
    void setDefault(Long id, Long userId);

    @Select("select * from address_book where user_id = #{userId} and is_default = 1")
    void getDefault(Long userId);
    @Update("update address_book " +
            "set consignee = #{consignee},sex = #{sex},phone = #{phone}," +
            "province_code = #{provinceCode},province_name = #{provinceName}," +
            "city_code = #{cityCode},city_name = #{cityName}," +
            "district_code = #{districtCode},district_name = #{districtName}," +
            "detail = #{detail},label = #{label},is_default = #{idDefault} where id = #{id}")
    void update(AddressBook addressBook);
}
