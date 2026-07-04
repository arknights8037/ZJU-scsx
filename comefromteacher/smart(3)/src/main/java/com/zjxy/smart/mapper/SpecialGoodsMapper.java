package com.zjxy.smart.mapper;

import com.zjxy.smart.model.SpecialGoods;

public interface SpecialGoodsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SpecialGoods record);

    int insertSelective(SpecialGoods record);

    SpecialGoods selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SpecialGoods record);

    int updateByPrimaryKey(SpecialGoods record);
}