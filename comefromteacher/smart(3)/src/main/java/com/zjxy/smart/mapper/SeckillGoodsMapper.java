package com.zjxy.smart.mapper;

import com.zjxy.smart.model.SeckillGoods;

public interface SeckillGoodsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SeckillGoods record);

    int insertSelective(SeckillGoods record);

    SeckillGoods selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SeckillGoods record);

    int updateByPrimaryKey(SeckillGoods record);
}