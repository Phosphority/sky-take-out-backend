package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface ShoppingCartMapping {
//    @Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time)" +
//            "VALUES(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor}, #{amount},#{createTime})")
//    void addShoppingCart(ShoppingCart shoppingCart);
//    void updateShoppingCart(ShoppingCart updateShoppingCart);
//    ShoppingCart findDataById(@Param("setmealId") Long setmealId, @Param("dishId")Long dishId,@Param("userId") Long userId);

    List<ShoppingCart> findCart(ShoppingCart shoppingCart);

    void updateCart(ShoppingCart cart);

    void addShoppingCart(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> listShoppingCart(Long aLong);

    @Delete("delete from shopping_cart where user_id =#{userId}")
    void cleanShoppingCart(Long aLong);

    void subShoppingCart(ShoppingCart shoppingCart);
}
