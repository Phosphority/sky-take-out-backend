package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapping {

    List<ShoppingCart> list(ShoppingCart shoppingCart);

    void updateCart(ShoppingCart cart);

    void addShoppingCart(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id =#{userId}")
    void cleanShoppingCart(Long userId);

    void subShoppingCart(ShoppingCart shoppingCart);

    void insertBatch(List<ShoppingCart> shoppingCartList);
}
