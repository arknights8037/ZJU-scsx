package com.zjxy.smart.mapper;

import com.zjxy.smart.model.SeckillActivity;

public interface SeckillActivityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SeckillActivity record);

    int insertSelective(SeckillActivity record);

    SeckillActivity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SeckillActivity record);

    int updateByPrimaryKey(SeckillActivity record);
}