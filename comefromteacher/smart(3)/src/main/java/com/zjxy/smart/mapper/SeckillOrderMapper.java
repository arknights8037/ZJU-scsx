package com.zjxy.smart.mapper;

import com.zjxy.smart.model.SeckillOrder;

public interface SeckillOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SeckillOrder record);

    int insertSelective(SeckillOrder record);

    SeckillOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SeckillOrder record);

    int updateByPrimaryKey(SeckillOrder record);
}