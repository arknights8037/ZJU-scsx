package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.mall.entity.Store;
import com.smartcommunity.mall.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 门店服务
 */
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreMapper storeMapper;

    public List<Store> listByArea(Integer areaId) {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getAreaId, areaId).eq(Store::getStoreStatus, 1);
        return storeMapper.selectList(wrapper);
    }

    public List<Store> listAll() {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStoreStatus, 1);
        return storeMapper.selectList(wrapper);
    }

    public Store getByStoreNo(String storeNo) {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStoreNo, storeNo);
        return storeMapper.selectOne(wrapper);
    }
}
