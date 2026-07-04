package com.smartcommunity.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.dto.AdminStoreView;
import com.smartcommunity.admin.entity.Area;
import com.smartcommunity.admin.entity.Store;
import com.smartcommunity.admin.mapper.AreaMapper;
import com.smartcommunity.admin.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStoreService {

    private final StoreMapper storeMapper;
    private final AreaMapper areaMapper;

    public Page<AdminStoreView> page(
            int page, int size, String keyword, Integer status, Integer areaId) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(1, Math.min(size, 100));
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String value = keyword.trim();
            wrapper.and(condition -> condition
                .like(Store::getStoreName, value)
                .or().like(Store::getStoreAddress, value)
                .or().like(Store::getStoreNo, value));
        }
        wrapper.eq(status != null, Store::getStoreStatus, status)
            .eq(areaId != null, Store::getAreaId, areaId)
            .orderByDesc(Store::getId);

        Page<Store> sourcePage = storeMapper.selectPage(new Page<>(safePage, safeSize), wrapper);
        Set<Integer> areaIds = sourcePage.getRecords().stream().map(Store::getAreaId)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Integer, Area> areaMap = areaIds.isEmpty() ? Collections.emptyMap()
            : areaMapper.selectList(new LambdaQueryWrapper<Area>().in(Area::getId, areaIds))
                .stream().collect(Collectors.toMap(Area::getId, Function.identity(), (a, b) -> a));

        Page<AdminStoreView> result = new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
        result.setRecords(sourcePage.getRecords().stream().map(store -> {
            AdminStoreView view = new AdminStoreView();
            BeanUtils.copyProperties(store, view);
            Area area = areaMap.get(store.getAreaId());
            view.setAreaName(area == null ? "未分配区域" : area.getAreaName());
            return view;
        }).toList());
        return result;
    }

    public void save(Store store) {
        if (!StringUtils.hasText(store.getStoreName())) {
            throw new RuntimeException("请填写门店名称");
        }
        if (!StringUtils.hasText(store.getStoreNo())) {
            store.setStoreNo(UUID.randomUUID().toString().replace("-", ""));
        }
        if (store.getStoreStatus() == null) {
            store.setStoreStatus(1);
        }
        LambdaQueryWrapper<Store> duplicateWrapper = new LambdaQueryWrapper<Store>()
            .eq(Store::getStoreNo, store.getStoreNo());
        if (store.getId() != null) {
            duplicateWrapper.ne(Store::getId, store.getId());
        }
        if (storeMapper.selectCount(duplicateWrapper) > 0) {
            throw new RuntimeException("门店编号已存在");
        }
        storeMapper.insertOrUpdate(store);
    }

    public void delete(Integer id) {
        storeMapper.deleteById(id);
    }
}
