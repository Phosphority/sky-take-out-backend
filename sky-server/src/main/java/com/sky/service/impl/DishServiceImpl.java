package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishFlavorMapper dishFlavorMapper;
    @Resource
    private SetmealMapper setmealMapper;

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
        // 1.判断是否有套餐包含了该菜品
        if (setmealMapper.findDishByDishId(dishIds) > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            // 2.判断菜品状态是否是在售状态
        } else if (dishMapper.findDishStatus(dishIds) > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            // 3.判断是否删除成功
        } else if (dishMapper.deleteBatch(dishIds)) {
            dishFlavorMapper.deleteBatch(dishIds);
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
    }

}





















