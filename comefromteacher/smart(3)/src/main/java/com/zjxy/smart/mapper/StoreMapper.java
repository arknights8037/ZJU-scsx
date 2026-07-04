package com.zjxy.smart.mapper;

import com.zjxy.smart.model.Store;

import java.util.List;

public interface StoreMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Store record);

    int insertSelective(Store record);

/**
 * 根据主键查询Store记录
 * @param id 主键ID
 * @return 返回对应的Store对象，如果没有找到则返回null
 */
    Store selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Store record);

    int updateByPrimaryKey(Store record);

    List<Store> selAll(Store store);

    Store selByNo(String storeNo);
}