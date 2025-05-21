package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface ReportMapper {

    Double sumByMap(Map<String, Object> map);
}
