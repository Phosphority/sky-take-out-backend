package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishFlavorMapper dishFlavorMapper;
    @Resource
    private SetmealMapper setmealMapper;
    @Resource
    private RedisTemplate<String, DishVO> redisTemplate;

    // NOTICE 添加操作不需要将缓存清除，因为添加操作默认是未启用
    @Override
    @Transactional
    public void addDish(DishDTO dishDTO) {
        Dish dish = Dish.builder().status(StatusConstant.DISABLE).build();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.addDish(dish);
        // 获取插入后的主键ID
        long dishId = dish.getId();

        if (dishDTO.getFlavors() != null && !dishDTO.getFlavors().isEmpty()) {
            // 将每个菜品口味所需要的菜品id使用forEach注入
            dishDTO.getFlavors().forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.addBatch(dishDTO.getFlavors());
        }
    }

    // 查询操作也不需要清除缓存
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Dish dish = Dish.builder()
                .categoryId(dishPageQueryDTO.getCategoryId())
                .name(dishPageQueryDTO.getName())
                .status(dishPageQueryDTO.getStatus())
                .build();
        Page<DishVO> dishPage = dishMapper.page(dish);
        return new PageResult(dishPage.getTotal(), dishPage.getResult());
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> dishIds) {
        Integer status = StatusConstant.ENABLE;
        // 1.判断是否有套餐包含了该菜品
        if (setmealMapper.findDishByDishId(dishIds) > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            // 2.判断菜品状态是否是在售状态
        } else if ((status = dishMapper.findOnSaleDishStatus(dishIds)) > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            // 3.判断是否删除成功
        } else if (dishMapper.deleteBatch(dishIds)) {
            dishFlavorMapper.deleteBatch(dishIds);
            // 判断是否起售，在售状态才需要删除缓存
            if (status.equals(StatusConstant.ENABLE)) {
                // 删除成功后清除缓存,不删除所有的，精确删除
                for (Long dishId : dishIds) {
                    redisTemplate.delete("dish_" + dishId);
                }
            }
        }
    }

    @Override
    @Transactional
    public void updateDishWithFlavor(DishDTO dishDTO) {
        System.out.println(dishDTO.getFlavors());
        Dish dish = Dish.builder().build();
        BeanUtils.copyProperties(dishDTO, dish);
        if (dishMapper.updateDish(dish)) {
            List<Long> dishId = new ArrayList<>();
            dishId.add(dish.getId());
            dishFlavorMapper.deleteBatch(dishId);
            if (dishDTO.getFlavors() != null && !dishDTO.getFlavors().isEmpty()) {
                dishFlavorMapper.insertDishFlavor(dishDTO.getFlavors(), dish.getId());
                if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                    // TODO 这里其实是可以优化的，如果想要精确删除的话，需要原先的categoryId，如果与原先的categoryId相同
                    // TODO 则只需删除当前修改的分类缓存，如果不同则删除这两个categoryId的缓存
                    // 修改完成后删除所有的缓存
                    Set<String> keys = redisTemplate.keys("dish_*");
                    for (String key : keys) {
                        redisTemplate.delete(key);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public DishVO findById(Long dishId) {
        DishVO dishVO = dishMapper.findById(dishId);
        if (dishVO != null) {
            dishVO.setFlavors(dishFlavorMapper.findFlavorByDishId(dishId));
        }
        return dishVO;
    }

    @Override
    public void updateStatus(Integer status, long id) {
        Dish dish = Dish.builder().build();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.updateStatus(dish);
        // 修改完成后删除所有的缓存
        if (Objects.equals(status, StatusConstant.ENABLE)) {
            Set<String> keys = redisTemplate.keys("dish_*");
            for (String key : keys) {
                redisTemplate.delete(key);
            }
        }
    }

    @Override
    public List<DishVO> findByCategoryId(long categoryId) {
        Set<String> keySet = BaseContext.getCurrentId().keySet();
        String valueName = null;
        for (String key : keySet) {
            valueName = key;
        }
        if (valueName != null && valueName.equals(JwtClaimsConstant.USER_ID)) {
            // 如果是用户，则只查询在售状态的菜品
            return dishMapper.findByCategoryId(categoryId, 1);
        } else {
            // 如果是管理员，则status为null查询所有的菜品
            return dishMapper.findByCategoryId(categoryId, null);
        }
    }

}





















