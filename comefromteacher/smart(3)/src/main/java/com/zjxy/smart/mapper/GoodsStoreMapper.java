package com.zjxy.smart.mapper;

import com.zjxy.smart.form.GoodsStoreForm;
import com.zjxy.smart.model.GoodsStore;

import java.util.List;

public interface GoodsStoreMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GoodsStore record);

    int insertSelective(GoodsStore record);

    GoodsStore selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GoodsStore record);

    int updateByPrimaryKey(GoodsStore record);

    GoodsStore selByNo(GoodsStore goodsStore);

    List<GoodsStore> selByStore(GoodsStoreForm goodsStoreForm);

    List<GoodsStore> selAll(GoodsStoreForm goodsStoreForm);
}