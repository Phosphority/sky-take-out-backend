package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Resource
    private SetmealMapper setmealMapper;
    @Resource
    private SetmealDishMapper setmealDishMapper;
    @Resource
    private DishMapper dishMapper;

    @Transactional
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
        // NOTICE 这里返回的意思不是直接从数据库中返回数据，然后直接接收，而是mybatis将数据利用反射set到entity(Setmeal)的对象中
        setmealMapper.addSetmeal(setmeal);
        // 插入之后再从setmeal中获取setmealId
        long setmealId = setmeal.getId();
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
        Page<SetmealVO> setmealPage = setmealMapper.page(setmeal);
        return new PageResult(setmealPage.getTotal(), setmealPage.getResult());
    }

    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        if (setmealMapper.findBatchStatus(ids) > 0) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        } else if (setmealMapper.deleteBatch(ids)) {
            setmealDishMapper.deleteBatch(ids);
        }
    }

    @Override
    public SetmealVO findById(long id) {
        return setmealMapper.findById(id);
    }


    @Transactional
    @Override
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = Setmeal.builder().build();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        if (setmealMapper.updateSetmeal(setmeal) && setmealDishes != null && !setmealDishes.isEmpty()) {
            // 批量删除套餐菜品的套餐的id，仅因为套餐删除默认是批量删除，所以说将套餐id封装成List集合
            List<Long> dishId = new ArrayList<>();
            dishId.add(setmealDTO.getId());
            // NOTICE 这里是前端的问题，tmd为什么你前端不能将套餐id设好再发过来啊？前端不是有套餐id吗？？？
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmeal.getId());
            }
            // 这里不需要做判断
            setmealDishMapper.deleteBatch(dishId);
            setmealDishMapper.addSetmealDish(setmealDishes);
        }
    }

    @Transactional
    @Override
    public void updateSetmealStatus(Integer status, long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        // 1.从套餐菜品表中查出dishIds
        List<Long> dishIds = setmealDishMapper.findDishIdBySetmealId(id);
        // 2.从菜品表中查出在售商品的数量
        Integer onSaleDishCount = dishMapper.findOnSaleDishStatus(dishIds);
        // NOTICE 3.如果在售商品的数量和套餐菜品中的数量不一致，那就说明套餐菜品中既有在售中的菜品，又有未售中的菜品
        boolean dishStatus = dishIds.size() == onSaleDishCount;
        // 起售与停售分开判断:1为起售，0为停售
        if (status == 1 && !dishStatus) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
        } else {
            setmealMapper.updateSetmealStatus(setmeal);
        }
    }

    @Override
    public List<Setmeal> findByCategoryId(long categoryId) {
        Set<String> keySet = BaseContext.getCurrentId().keySet();
        String valueName = null;
        for (String key : keySet) {
            valueName = key;
        }

        if (valueName != null && valueName.equals(JwtClaimsConstant.USER_ID)) {
            return setmealMapper.findByCategoryId(categoryId,1);
        }else {
            return setmealMapper.findByCategoryId(categoryId,null);
        }
    }

    @Override
    public List<DishItemVO> findDishesBySetmealId(long id) {
        return setmealDishMapper.findDishesBySetmealId(id);
    }
}












