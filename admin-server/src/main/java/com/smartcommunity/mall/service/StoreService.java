package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.mall.entity.Store;
import com.smartcommunity.mall.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 居民端门店服务。
 * 提供按区域查询门店列表、查询全部可用门店、按门店编号获取门店详情等功能。
 */
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreMapper storeMapper;

    /**
     * 根据区域 ID 查询该区域下的可用门店列表。
     *
     * @param areaId 区域 ID
     * @return 门店列表
     */
    public List<Store> listByArea(Integer areaId) {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getAreaId, areaId).eq(Store::getStoreStatus, 1);
        return storeMapper.selectList(wrapper);
    }

    /**
     * 查询所有状态为可用的门店列表。
     *
     * @return 全部可用门店列表
     */
    public List<Store> listAll() {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStoreStatus, 1);
        return storeMapper.selectList(wrapper);
    }

    /**
     * 根据门店编号获取门店详情。
     *
     * @param storeNo 门店编号
     * @return 门店对象，不存在返回 null
     */
    public Store getByStoreNo(String storeNo) {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStoreNo, storeNo);
        return storeMapper.selectOne(wrapper);
    }
}
