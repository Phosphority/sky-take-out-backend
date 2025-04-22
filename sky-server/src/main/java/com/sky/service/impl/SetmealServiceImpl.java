package com.sky.service.impl;

import com.sky.mapper.SetmealMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class SetmealServiceImpl {
    @Resource
    private SetmealMapper setmealMapper;


}
