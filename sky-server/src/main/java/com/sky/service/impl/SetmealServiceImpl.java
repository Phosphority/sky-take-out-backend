package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Resource
    private SetmealMapper setmealMapper;
    @Resource
    private SetmealDishMapper setmealDishMapper;
    @Resource
    private DishMapper dishMapper;

    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = Setmeal.builder()
                .image(setmealDTO.getImage())
                .price(setmealDTO.getPrice())
                .name(setmealDTO.getName())
                .status(StatusConstant.DISABLE) // 默认为未起售
                .description(setmealDTO.getDescription())
                .categoryId(setmealDTO.getCategoryId())
                .build();
        // TODO 这里的逻辑还有问题，插入完setmeal之后需要返回插入成功的主键id作为setmeal_dish的setmeal_id
        Long setmealId = setmealMapper.addSetmeal(setmeal);
        // 判断setmealDishes是否有值
        if (setmealDTO.getSetmealDishes() != null && !setmealDTO.getSetmealDishes().isEmpty()) {
            // 将setmealId插入所有的SetmealDish中
            setmealDTO.getSetmealDishes().forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
            setmealDishMapper.addSetmealDish(setmealDTO.getSetmealDishes());
        }
    }

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Setmeal setmeal = Setmeal.builder()
                .name(setmealPageQueryDTO.getName())
                .status(setmealPageQueryDTO.getStatus())
                .categoryId(setmealPageQueryDTO.getCategoryId())
                .build();
        Page<Setmeal> setmealPage = setmealMapper.page(setmeal);
        return new PageResult(setmealPage.getTotal(), setmealPage.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        if (setmealMapper.findBatchStatus(ids) > 0) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        } else if (setmealMapper.deleteBatch(ids)) {
            setmealDishMapper.deleteBatch(ids);
        }
    }

    @Override
    public Setmeal findById(Long id) {
        return setmealMapper.findById(id);
    }


    @Override
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = Setmeal.builder()
                .image(setmealDTO.getImage())
                .price(setmealDTO.getPrice())
                .name(setmealDTO.getName())
                .description(setmealDTO.getDescription())
                .categoryId(setmealDTO.getCategoryId())
                .build();

        if (setmealMapper.updateSetmeal(setmeal) && setmealDTO.getSetmealDishes() != null && !setmealDTO.getSetmealDishes().isEmpty()) {
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(setmealDTO.getId());
            if (setmealDishMapper.deleteBatch(dishIds)) {
                setmealDishMapper.addSetmealDish(setmealDTO.getSetmealDishes());
            }
        }
    }

    @Override
    public void updateSetmealStatus(Integer status, long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        // 从菜品套餐表中查出dishIds
        List<Long> dishIds = setmealDishMapper.findDishIdBySetmealId(id);
        //
        if (dishMapper.findDishStatus(dishIds) > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        } else {
            setmealMapper.updateSetmealStatus(setmeal);
        }
    }
}












