package com.sky.service.impl;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapping;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Resource
    private ShoppingCartMapping shoppingCartMapping;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private SetmealMapper setmealMapper;

    // NOTICE
    @Override
    @Transactional
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 现构建好购物车对象
        ShoppingCart shoppingCart = ShoppingCart.builder().build();
        // 先将userId赋值给shoppingCart
        Long userId = BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID);
        shoppingCart.setUserId(userId);
        // 判断添加进购物车的对象是Setmeal还是Dish,由此获取Setmeal还是Dish
        if(shoppingCartDTO.getSetmealId() != null && shoppingCartDTO.getSetmealId() > 0) {
            // select:判断该用户是否是第一次添加，如果是同样的商品，update:只需要修改购物车中的商品，将数量加一以及修改价格
            SetmealVO setmealVO = SetmealVO.builder().build();
            ShoppingCart updateShoppingCart = ShoppingCart.builder().build();
            if( (updateShoppingCart = shoppingCartMapping.findDataById(shoppingCartDTO.getSetmealId(),null,userId)) != null){
                // 商品数量
                Integer setmealNumber = updateShoppingCart.getNumber();
                // 商品总价
                BigDecimal amount = updateShoppingCart.getAmount();
                // 商品单价
                BigDecimal setmealSinglePrice = amount.divide(BigDecimal.valueOf(setmealNumber),2, RoundingMode.HALF_UP);
                updateShoppingCart.setNumber(setmealNumber + 1);
                updateShoppingCart.setUserId(userId);
                updateShoppingCart.setAmount(setmealSinglePrice.multiply(BigDecimal.valueOf(setmealNumber)));
                // 需要修改数量和该套餐在购物车中总价
                shoppingCartMapping.updateShoppingCart(updateShoppingCart);
                // 对数据库的操作已经完成，直接返回
                return;
            }

            // 查询setmeal的数据
            setmealVO = setmealMapper.findById(shoppingCartDTO.getSetmealId());
            // 如果是第一次添加到购物车，设置数据
            shoppingCart.setSetmealId(setmealVO.getId())
                    .setName(setmealVO.getName())
                    .setAmount(setmealVO.getPrice())
                    .setImage(setmealVO.getImage());
        }else{
            DishVO dishVO = DishVO.builder().build();
            ShoppingCart updateShoppingCart = ShoppingCart.builder().build();
            if((updateShoppingCart = shoppingCartMapping.findDataById(null,shoppingCartDTO.getDishId(),userId)) != null){

                // 得到需要更新的购物车数据
                ShoppingCart updateData = getUpdateShoppingCart(updateShoppingCart, userId);
                // 需要修改数量和该套餐在购物车中总价
                shoppingCartMapping.updateShoppingCart(updateData);
                return;
            }

            dishVO = dishMapper.findById(shoppingCartDTO.getDishId());
            shoppingCart.setDishId(shoppingCartDTO.getDishId())
                    .setName(dishVO.getName())
                    .setAmount(dishVO.getPrice())
                    .setImage(dishVO.getImage());
        }

        // 最后无论是查Setmeal还是Dish其实都是使用这些方法
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartMapping.addShoppingCart(shoppingCart);
    }


    public ShoppingCart getUpdateShoppingCart(ShoppingCart updateShoppingCart,Long userId) {
        Integer setmealNumber = updateShoppingCart.getNumber();
        // 商品总价
        BigDecimal amount = updateShoppingCart.getAmount();
        // 商品单价
        BigDecimal setmealSinglePrice = amount.divide(BigDecimal.valueOf(setmealNumber),2, RoundingMode.HALF_UP);
        updateShoppingCart.setNumber(setmealNumber + 1);
        updateShoppingCart.setUserId(userId);
        updateShoppingCart.setAmount(setmealSinglePrice.multiply(BigDecimal.valueOf(setmealNumber)));
        return updateShoppingCart;
    }
}





