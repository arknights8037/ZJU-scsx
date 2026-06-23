package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.mall.entity.Special;
import com.smartcommunity.mall.entity.SpecialGoods;
import com.smartcommunity.mall.mapper.SpecialGoodsMapper;
import com.smartcommunity.mall.mapper.SpecialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 促销服务
 */
@Service
@RequiredArgsConstructor
public class SpecialService {

    private final SpecialMapper specialMapper;
    private final SpecialGoodsMapper specialGoodsMapper;

    public List<Special> listActive() {
        LambdaQueryWrapper<Special> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Special::getSpecialStatus, 1);
        return specialMapper.selectList(wrapper);
    }

    public List<SpecialGoods> getSpecialGoods(Integer specialId) {
        LambdaQueryWrapper<SpecialGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialGoods::getSpecialId, specialId);
        return specialGoodsMapper.selectList(wrapper);
    }
}
