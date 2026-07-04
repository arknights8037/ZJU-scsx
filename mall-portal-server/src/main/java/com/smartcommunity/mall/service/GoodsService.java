package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.dto.GoodsClickStat;
import com.smartcommunity.mall.entity.*;
import com.smartcommunity.mall.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商品服务
 */
@Service
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsMapper goodsMapper;
    private final GoodsCategoryMapper categoryMapper;
    private final GoodsPictureMapper pictureMapper;
    private final GoodsStoreMapper goodsStoreMapper;
    private final GoodsFavoritesMapper favoritesMapper;
    private final GoodsClickRecordMapper clickRecordMapper;

    public Page<Goods> page(Integer pageNo, Integer pageSize, Integer categoryId, String keyword) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getGoodsState, 1);
        if (categoryId != null) {
            wrapper.eq(Goods::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Goods::getGoodsName, keyword);
        }
        wrapper.orderByDesc(Goods::getCreateTime);
        return goodsMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
    }

    public Goods getByGoodsNo(String goodsNo) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getGoodsNo, goodsNo).eq(Goods::getGoodsState, 1);
        return goodsMapper.selectOne(wrapper);
    }

    /**
     * 记录一次有效点击。userId 只能来自 JWT，不能相信前端自己传来的用户编号。
     */
    public void recordClick(Integer userId, String goodsNo) {
        if (userId == null) {
            return;
        }
        if (getByGoodsNo(goodsNo) == null) {
            throw new RuntimeException("商品不存在或已下架");
        }
        clickRecordMapper.increaseClick(userId, goodsNo);
    }

    /**
     * 简单推荐算法：
     * 1. 用户点击过哪个分类，就提高该分类下商品的分数；
     * 2. 再加上全站点击热度，避免只看个人数据；
     * 3. 没有点击数据时，自然退化为热门商品和新商品推荐。
     *
     * 这里在 Java 内存中排序，当前商品量只有几百条，逻辑直观，适合课程答辩。
     */
    public List<Goods> recommend(Integer userId, String excludeGoodsNo, Integer requestedSize) {
        int size = Math.max(1, Math.min(requestedSize == null ? 6 : requestedSize, 12));
        List<Goods> goodsList = goodsMapper.selectList(new LambdaQueryWrapper<Goods>()
            .eq(Goods::getGoodsState, 1));
        Map<String, Goods> goodsMap = goodsList.stream()
            .collect(Collectors.toMap(Goods::getGoodsNo, Function.identity(), (a, b) -> a));

        Map<String, Long> globalClicks = clickRecordMapper.sumClicksByGoods().stream()
            .collect(Collectors.toMap(GoodsClickStat::getGoodsNo, GoodsClickStat::getClickCount));
        Map<Integer, Long> categoryPreference = new HashMap<>();
        Map<String, Integer> ownClicks = new HashMap<>();

        if (userId != null) {
            List<GoodsClickRecord> userRecords = clickRecordMapper.selectList(
                new LambdaQueryWrapper<GoodsClickRecord>().eq(GoodsClickRecord::getUserId, userId));
            for (GoodsClickRecord record : userRecords) {
                Goods clickedGoods = goodsMap.get(record.getGoodsNo());
                int clickCount = record.getClickCount() == null ? 0 : record.getClickCount();
                ownClicks.put(record.getGoodsNo(), clickCount);
                if (clickedGoods != null && clickedGoods.getCategoryId() != null) {
                    categoryPreference.merge(clickedGoods.getCategoryId(), (long) clickCount, Long::sum);
                }
            }
        }

        Comparator<Goods> comparator = Comparator
            .comparingLong((Goods goods) -> recommendationScore(
                goods, categoryPreference, globalClicks, ownClicks))
            .reversed()
            .thenComparing(Goods::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder()));

        return goodsList.stream()
            .filter(goods -> !Objects.equals(goods.getGoodsNo(), excludeGoodsNo))
            .sorted(comparator)
            .limit(size)
            .toList();
    }

    private long recommendationScore(
            Goods goods,
            Map<Integer, Long> categoryPreference,
            Map<String, Long> globalClicks,
            Map<String, Integer> ownClicks) {
        long categoryScore = goods.getCategoryId() == null
            ? 0L : categoryPreference.getOrDefault(goods.getCategoryId(), 0L) * 100L;
        long popularScore = globalClicks.getOrDefault(goods.getGoodsNo(), 0L);
        // 轻微降低已经反复看过的单品分数，让同分类的新商品有机会排在前面。
        long repeatedViewPenalty = ownClicks.getOrDefault(goods.getGoodsNo(), 0) * 5L;
        return categoryScore + popularScore - repeatedViewPenalty;
    }

    public List<GoodsCategory> getAllCategories() {
        LambdaQueryWrapper<GoodsCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(GoodsCategory::getCategoryType, GoodsCategory::getId);
        return categoryMapper.selectList(wrapper);
    }

    public List<GoodsCategory> getSubCategories(Integer parentId) {
        LambdaQueryWrapper<GoodsCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsCategory::getParentId, parentId);
        return categoryMapper.selectList(wrapper);
    }

    public List<GoodsPicture> getPictures(String goodsNo) {
        LambdaQueryWrapper<GoodsPicture> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsPicture::getGoodsNo, goodsNo);
        return pictureMapper.selectList(wrapper);
    }

    public List<GoodsStore> getStoreGoods(String goodsNo) {
        LambdaQueryWrapper<GoodsStore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsStore::getGoodsNo, goodsNo);
        return goodsStoreMapper.selectList(wrapper);
    }

    public void addFavorite(Integer userId, String goodsNo, String storeNo) {
        GoodsFavorites favorite = new GoodsFavorites();
        favorite.setUserId(userId);
        favorite.setGoodsNo(goodsNo);
        favorite.setStoreNo(storeNo);
        favoritesMapper.insert(favorite);
    }

    public void removeFavorite(Integer userId, String goodsNo) {
        LambdaQueryWrapper<GoodsFavorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsFavorites::getUserId, userId).eq(GoodsFavorites::getGoodsNo, goodsNo);
        favoritesMapper.delete(wrapper);
    }

    public List<GoodsFavorites> getUserFavorites(Integer userId) {
        LambdaQueryWrapper<GoodsFavorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsFavorites::getUserId, userId);
        return favoritesMapper.selectList(wrapper);
    }
}
