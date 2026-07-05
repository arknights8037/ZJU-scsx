package com.smartcommunity.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.dto.AdminStoreView;
import com.smartcommunity.admin.dto.AdminStoreGoodsView;
import com.smartcommunity.admin.entity.Area;
import com.smartcommunity.admin.entity.Category;
import com.smartcommunity.admin.entity.Goods;
import com.smartcommunity.admin.entity.Store;
import com.smartcommunity.admin.mapper.AreaMapper;
import com.smartcommunity.admin.mapper.CategoryMapper;
import com.smartcommunity.admin.mapper.GoodsMapper;
import com.smartcommunity.admin.mapper.StoreMapper;
import com.smartcommunity.mall.entity.GoodsStore;
import com.smartcommunity.mall.mapper.GoodsStoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 管理端门店管理服务。
 * 提供门店的分页查询、新增/编辑、删除，以及门店商品关联管理等功能。
 */
@Service
@RequiredArgsConstructor
public class AdminStoreService {

    private final StoreMapper storeMapper;
    private final AreaMapper areaMapper;
    private final GoodsMapper goodsMapper;
    private final CategoryMapper categoryMapper;
    private final GoodsStoreMapper goodsStoreMapper;

    /**
     * 分页查询门店列表，支持按关键词、状态、区域筛选。
     * 同时统计每个门店的商品数量和总库存。
     *
     * @param page    页码
     * @param size    每页条数
     * @param keyword 关键词（匹配门店名称、地址、编号）
     * @param status  门店状态
     * @param areaId  区域 ID
     * @return 门店视图分页结果
     */
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
        Set<String> storeNos = sourcePage.getRecords().stream().map(Store::getStoreNo)
            .filter(StringUtils::hasText).collect(Collectors.toSet());
        Map<String, Long> goodsCountMap;
        Map<String, Long> stockMap;
        if (storeNos.isEmpty()) {
            goodsCountMap = Collections.emptyMap();
            stockMap = Collections.emptyMap();
        } else {
            List<GoodsStore> relations = goodsStoreMapper.selectList(new LambdaQueryWrapper<GoodsStore>()
                    .in(GoodsStore::getStoreNo, storeNos));
            goodsCountMap = relations.stream().collect(Collectors.groupingBy(GoodsStore::getStoreNo, Collectors.counting()));
            stockMap = relations.stream().collect(Collectors.groupingBy(GoodsStore::getStoreNo,
                    Collectors.summingLong(item -> item.getGoodsStock() == null ? 0 : item.getGoodsStock())));
        }

        Page<AdminStoreView> result = new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
        result.setRecords(sourcePage.getRecords().stream().map(store -> {
            AdminStoreView view = new AdminStoreView();
            BeanUtils.copyProperties(store, view);
            Area area = areaMap.get(store.getAreaId());
            view.setAreaName(area == null ? "未分配区域" : area.getAreaName());
            view.setGoodsCount(goodsCountMap.getOrDefault(store.getStoreNo(), 0L));
            view.setTotalStock(stockMap.getOrDefault(store.getStoreNo(), 0L));
            return view;
        }).toList());
        return result;
    }

    /**
     * 保存或更新门店信息。
     * 新门店自动生成 storeNo；门店编号不可重复。
     *
     * @param store 门店对象
     */
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
        // 校验门店编号唯一性（编辑时排除自身）
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

    /**
     * 删除门店及其关联的商品关系。
     *
     * @param id 门店 ID
     */
    public void delete(Integer id) {
        Store store = storeMapper.selectById(id);
        if (store != null && StringUtils.hasText(store.getStoreNo())) {
            goodsStoreMapper.delete(new LambdaQueryWrapper<GoodsStore>().eq(GoodsStore::getStoreNo, store.getStoreNo()));
        }
        storeMapper.deleteById(id);
    }

    /**
     * 获取门店的商品列表，包含商品信息和分类名称。
     *
     * @param storeNo 门店编号
     * @return 门店商品视图列表
     */
    public List<AdminStoreGoodsView> storeGoods(String storeNo) {
        Store store = requireStore(storeNo);
        List<GoodsStore> relations = goodsStoreMapper.selectList(new LambdaQueryWrapper<GoodsStore>()
                .eq(GoodsStore::getStoreNo, storeNo)
                .orderByDesc(GoodsStore::getId));
        if (relations.isEmpty()) return List.of();
        Set<String> goodsNos = relations.stream().map(GoodsStore::getGoodsNo)
                .filter(StringUtils::hasText).collect(Collectors.toSet());
        Map<String, Goods> goodsMap = goodsNos.isEmpty() ? Collections.emptyMap()
                : goodsMapper.selectList(new LambdaQueryWrapper<Goods>().in(Goods::getGoodsNo, goodsNos))
                    .stream().collect(Collectors.toMap(Goods::getGoodsNo, Function.identity(), (a, b) -> a));
        Set<Integer> categoryIds = goodsMap.values().stream().map(Goods::getCategoryId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Integer, Category> categoryMap = categoryIds.isEmpty() ? Collections.emptyMap()
                : categoryMapper.selectList(new LambdaQueryWrapper<Category>().in(Category::getId, categoryIds))
                    .stream().collect(Collectors.toMap(Category::getId, Function.identity(), (a, b) -> a));
        return relations.stream().map(relation -> toStoreGoodsView(store, relation, goodsMap, categoryMap)).toList();
    }

    /**
     * 门店新增或更新商品关联。若商品未设置价格则使用商品市场价。
     * 一个门店不能重复关联同一商品。
     *
     * @param storeNo  门店编号
     * @param relation 门店商品关联关系
     * @return 门店商品视图
     */
    public AdminStoreGoodsView saveStoreGoods(String storeNo, GoodsStore relation) {
        Store store = requireStore(storeNo);
        if (relation == null || !StringUtils.hasText(relation.getGoodsNo())) {
            throw new RuntimeException("请选择商品");
        }
        Goods goods = goodsMapper.selectOne(new LambdaQueryWrapper<Goods>()
                .eq(Goods::getGoodsNo, relation.getGoodsNo()).last("limit 1"));
        if (goods == null) throw new RuntimeException("商品不存在");
        relation.setStoreNo(storeNo);
        if (relation.getGoodsPrice() == null || relation.getGoodsPrice().compareTo(BigDecimal.ZERO) <= 0) {
            relation.setGoodsPrice(goods.getGoodsMarketPrice() == null ? BigDecimal.ONE : goods.getGoodsMarketPrice());
        }
        if (relation.getGoodsStock() == null || relation.getGoodsStock() < 0) relation.setGoodsStock(0);
        if (relation.getGoodsType() == null) relation.setGoodsType(1);
        LambdaQueryWrapper<GoodsStore> duplicateWrapper = new LambdaQueryWrapper<GoodsStore>()
                .eq(GoodsStore::getStoreNo, storeNo)
                .eq(GoodsStore::getGoodsNo, relation.getGoodsNo());
        if (relation.getId() != null) duplicateWrapper.ne(GoodsStore::getId, relation.getId());
        if (goodsStoreMapper.selectCount(duplicateWrapper) > 0) {
            throw new RuntimeException("该门店已经关联该商品，请直接编辑价格或库存");
        }
        goodsStoreMapper.insertOrUpdate(relation);
        Map<String, Goods> goodsMap = Map.of(goods.getGoodsNo(), goods);
        Map<Integer, Category> categoryMap = goods.getCategoryId() == null ? Collections.emptyMap()
                : categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getId, goods.getCategoryId()))
                    .stream().collect(Collectors.toMap(Category::getId, Function.identity(), (a, b) -> a));
        return toStoreGoodsView(store, relation, goodsMap, categoryMap);
    }

    /**
     * 删除门店的商品关联关系。
     *
     * @param storeNo 门店编号
     * @param id      关联关系 ID
     */
    public void deleteStoreGoods(String storeNo, Integer id) {
        requireStore(storeNo);
        GoodsStore relation = goodsStoreMapper.selectOne(new LambdaQueryWrapper<GoodsStore>()
                .eq(GoodsStore::getId, id)
                .eq(GoodsStore::getStoreNo, storeNo)
                .last("limit 1"));
        if (relation == null) throw new RuntimeException("门店商品关系不存在");
        goodsStoreMapper.deleteById(id);
    }

    /**
     * 根据门店编号获取门店，不存在则抛异常。
     *
     * @param storeNo 门店编号
     * @return Store 对象
     */
    private Store requireStore(String storeNo) {
        Store store = storeMapper.selectOne(new LambdaQueryWrapper<Store>()
                .eq(Store::getStoreNo, storeNo)
                .last("limit 1"));
        if (store == null) throw new RuntimeException("门店不存在");
        return store;
    }

    /**
     * 将门店、门店商品关系、商品、分类组装为门店商品视图。
     *
     * @param store        门店对象
     * @param relation     门店商品关联
     * @param goodsMap     商品编号 -> 商品对象 映射
     * @param categoryMap  分类 ID -> 分类对象 映射
     * @return 门店商品视图
     */
    private AdminStoreGoodsView toStoreGoodsView(Store store, GoodsStore relation,
            Map<String, Goods> goodsMap, Map<Integer, Category> categoryMap) {
        Goods goods = goodsMap.get(relation.getGoodsNo());
        Category category = goods == null ? null : categoryMap.get(goods.getCategoryId());
        AdminStoreGoodsView view = new AdminStoreGoodsView();
        view.setId(relation.getId());
        view.setStoreNo(relation.getStoreNo());
        view.setStoreName(store.getStoreName());
        view.setGoodsNo(relation.getGoodsNo());
        view.setGoodsName(goods == null ? relation.getGoodsNo() : goods.getGoodsName());
        view.setGoodsPicture(goods == null ? null : goods.getGoodsPicture());
        view.setCategoryName(category == null ? "未分类" : category.getCategoryName());
        view.setGoodsState(goods == null ? null : goods.getGoodsState());
        view.setGoodsMarketPrice(goods == null ? null : goods.getGoodsMarketPrice());
        view.setGoodsPrice(relation.getGoodsPrice());
        view.setGoodsStock(relation.getGoodsStock());
        view.setGoodsType(relation.getGoodsType());
        view.setCreateTime(relation.getCreateTime());
        view.setUpdateTime(relation.getUpdateTime());
        return view;
    }
}
