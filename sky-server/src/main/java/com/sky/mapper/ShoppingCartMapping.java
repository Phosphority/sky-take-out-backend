package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ShoppingCartMapping {
    @Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time)" +
            "VALUES(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor}, #{amount},#{createTime})")
    void addShoppingCart(ShoppingCart shoppingCart);

    void updateShoppingCart(ShoppingCart updateShoppingCart);


    ShoppingCart findDataById(Long setmealID,Long dishId, Long userId);
}
