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
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Resource
    private ShoppingCartMapping shoppingCartMapping;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private SetmealMapper setmealMapper;

    @Override
    @Transactional
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = ShoppingCart.builder().build();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID);
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapping.findCart(shoppingCart);

        // 判断购物车中是否已经有该商品
        if (list != null && !list.isEmpty()) {
            ShoppingCart cart = list.get(0);
            Integer oldNumber = cart.getNumber();
            BigDecimal singlePrice = cart.getAmount().divide(BigDecimal.valueOf(oldNumber), 2, RoundingMode.HALF_UP);
            cart.setUserId(userId);
            cart.setNumber(oldNumber + 1);
            cart.setAmount(singlePrice.multiply(BigDecimal.valueOf(oldNumber + 1)));
            shoppingCartMapping.updateCart(cart);
            // 添加完直接返回即可
            return;
        }

        // 如果没有则直接添加，但是要判断是哪一种是setmeal还是dish
        if (shoppingCart.getSetmealId() != null && shoppingCart.getSetmealId() > 0) {
            SetmealVO setmealVO = setmealMapper.findById(shoppingCart.getSetmealId());
            shoppingCart.setImage(setmealVO.getImage())
                    .setAmount(setmealVO.getPrice())  // NOTICE 这里忘记设Amount了，导致插入失败，原因为这个值不能为空
                    .setName(setmealVO.getName())
                    .setCreateTime(LocalDateTime.now());
        } else if (shoppingCart.getDishId() != null && shoppingCart.getDishId() > 0) {
            DishVO dishVO = dishMapper.findById(shoppingCart.getDishId());
            // 不需要考虑设置菜品口味，如果有，再BeanCopy的时候就已经set了，如果没有那么就让它为null即可
            shoppingCart.setImage(dishVO.getImage())
                    .setName(dishVO.getName())
                    .setCreateTime(LocalDateTime.now())
                    // 设置初始价格
                    .setAmount(dishVO.getPrice());
        }
        // 最后添加
        shoppingCart.setNumber(1);
        // 设置初始数量
        shoppingCartMapping.addShoppingCart(shoppingCart);
    }

    @Override
    public List<ShoppingCart> listShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID))
                .build();
        return shoppingCartMapping.findCart(shoppingCart);
    }

    @Override
    public void cleanShoppingCart() {
        shoppingCartMapping.cleanShoppingCart(BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID));
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = ShoppingCart.builder().build();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID);
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapping.findCart(shoppingCart);

        // 如果有的话就将数量减一
        if (list != null && !list.isEmpty()) {
            ShoppingCart cart = list.get(0);
            Integer oldNumber = cart.getNumber();
            BigDecimal singlePrice = cart.getAmount().divide(BigDecimal.valueOf(oldNumber), 2, RoundingMode.HALF_UP);
            cart.setNumber(oldNumber - 1)
                .setAmount(singlePrice.multiply(BigDecimal.valueOf(oldNumber - 1)));  //将价格也改变
            shoppingCartMapping.updateCart(cart);
        }

        // 没有的话就直接删除
        shoppingCartMapping.subShoppingCart(shoppingCart);
    }


    // TODO
//    // NOTICE
//    @Override
//    @Transactional
//    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
//        // 现构建好购物车对象
//        ShoppingCart shoppingCart = ShoppingCart.builder().build();
//        // 先将userId赋值给shoppingCart
//        Long userId = BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID);
//        shoppingCart.setUserId(userId);
//        // 判断添加进购物车的对象是Setmeal还是Dish,由此获取Setmeal还是Dish
//        if (shoppingCartDTO.getSetmealId() != null && shoppingCartDTO.getSetmealId() > 0) {
//            // select:判断该用户是否是第一次添加，如果是同样的商品，update:只需要修改购物车中的商品，将数量加一以及修改价格
//            SetmealVO setmealVO = SetmealVO.builder().build();
//            ShoppingCart updateShoppingCart = ShoppingCart.builder().build();
//            if ((updateShoppingCart = shoppingCartMapping.findDataById(shoppingCartDTO.getSetmealId(), null, userId)) != null) {
//
//                // 得到需要更新的购物车数据
//                ShoppingCart updateData = getUpdateShoppingCart(updateShoppingCart, userId);
//                // 需要修改数量和该套餐在购物车中总价
//                shoppingCartMapping.updateShoppingCart(updateData);
//                // 对数据库的操作已经完成，直接返回
//                return;
//            }
//
//            // 查询setmeal的数据
//            setmealVO = setmealMapper.findById(shoppingCartDTO.getSetmealId());
//            // 如果是第一次添加到购物车，设置数据
//            shoppingCart.setSetmealId(setmealVO.getId())
//                    .setName(setmealVO.getName())
//                    .setAmount(setmealVO.getPrice())
//                    .setImage(setmealVO.getImage());
//        } else {
//            DishVO dishVO = DishVO.builder().build();
//            ShoppingCart updateShoppingCart = ShoppingCart.builder().build();
//            if ((updateShoppingCart = shoppingCartMapping.findDataById(null, shoppingCartDTO.getDishId(), userId)) != null) {
//
//                // 得到需要更新的购物车数据
//                ShoppingCart updateData = getUpdateShoppingCart(updateShoppingCart, userId);
//                // 需要修改数量和该套餐在购物车中总价
//                shoppingCartMapping.updateShoppingCart(updateData);
//                return;
//            }
//
//            dishVO = dishMapper.findById(shoppingCartDTO.getDishId());
//            shoppingCart.setDishId(shoppingCartDTO.getDishId())
//                    .setName(dishVO.getName())
//                    .setAmount(dishVO.getPrice())
//                    .setImage(dishVO.getImage());
//        }
//
//        // 最后无论是查Setmeal还是Dish其实都是使用这些方法
//        shoppingCart.setCreateTime(LocalDateTime.now());
//        // 初始值都为1份
//        shoppingCart.setNumber(1);
//        shoppingCartMapping.addShoppingCart(shoppingCart);
//    }

    public ShoppingCart getUpdateShoppingCart(ShoppingCart updateShoppingCart, Long userId) {
        Integer oldNumber = updateShoppingCart.getNumber();
        int newNumber = oldNumber + 1;
        // 商品总价
        BigDecimal amount = updateShoppingCart.getAmount();
        // 商品单价
        BigDecimal setmealSinglePrice = amount.divide(BigDecimal.valueOf(oldNumber), 2, RoundingMode.HALF_UP);
        updateShoppingCart.setNumber(newNumber);
        updateShoppingCart.setUserId(userId);
        updateShoppingCart.setAmount(setmealSinglePrice.multiply(BigDecimal.valueOf(newNumber)));
        return updateShoppingCart;
    }
}





