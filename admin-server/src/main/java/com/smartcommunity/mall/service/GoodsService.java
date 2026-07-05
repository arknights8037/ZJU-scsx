package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.dto.FavoriteGoodsView;
import com.smartcommunity.mall.dto.GoodsClickStat;
import com.smartcommunity.mall.entity.*;
import com.smartcommunity.mall.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 居民端商品服务。
 *
 * 商品浏览接口对游客开放，收藏、点击记录等个性化能力需要登录。
 * 这里保留了一个轻量推荐算法，避免课程项目引入复杂推荐系统。
 *
 * 主要功能：
 * - 商品分页浏览，支持分类、关键词、价格区间、库存、排序等筛选；
 * - 商品详情、轮播图、门店价格查询；
 * - 用户收藏管理；
 * - 基于点击行为的轻量商品推荐。
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
    private final SpecialService specialService;

    public Page<Goods> page(Integer pageNo, Integer pageSize, Integer categoryId, String keyword) {
        return page(pageNo, pageSize, categoryId, keyword, null, null, null, null);
    }

    public Page<Goods> page(
            Integer pageNo,
            Integer pageSize,
            Integer categoryId,
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean hasStock,
            String sort) {
        // 居民端只展示上架商品，后台下架商品不会出现在商城列表中。
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getGoodsState, 1);
        if (categoryId != null) {
            wrapper.eq(Goods::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Goods::getGoodsName, keyword);
        }
        if (minPrice != null) {
            wrapper.ge(Goods::getGoodsMarketPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Goods::getGoodsMarketPrice, maxPrice);
        }
        if (Boolean.TRUE.equals(hasStock)) {
            wrapper.exists("select 1 from goods_store gs where gs.goods_no = goods.goods_no and gs.goods_stock > 0");
        }
        if ("priceAsc".equals(sort)) {
            wrapper.orderByAsc(Goods::getGoodsMarketPrice);
        } else if ("priceDesc".equals(sort)) {
            wrapper.orderByDesc(Goods::getGoodsMarketPrice);
        } else {
            wrapper.orderByDesc(Goods::getCreateTime);
        }
        Page<Goods> result = goodsMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        specialService.enrichGoods(result.getRecords());
        return result;
    }

    public Goods getByGoodsNo(String goodsNo) {
        // 详情页通过 goodsNo 访问商品，而不是数据库自增 id，便于和旧商城接口兼容。
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getGoodsNo, goodsNo).eq(Goods::getGoodsState, 1);
        return specialService.enrichGoods(goodsMapper.selectOne(wrapper));
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

        List<Goods> result = goodsList.stream()
            .filter(goods -> !Objects.equals(goods.getGoodsNo(), excludeGoodsNo))
            .sorted(comparator)
            .limit(size)
            .toList();
        return specialService.enrichGoods(result);
    }

    /**
     * 计算单个商品的推荐分数：
     * 用户对该分类的偏好 * 100（权重最高）+ 全站点击热度 - 已反复浏览的扣分。
     *
     * @param goods               商品对象
     * @param categoryPreference  用户分类偏好映射
     * @param globalClicks        全站点击热度映射
     * @param ownClicks           用户自身点击记录映射
     * @return 推荐分数（越高越优先推荐）
     */
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

    /**
     * 获取所有商品分类，按 categoryType 和 id 升序排列。
     *
     * @return 商品分类列表
     */
    public List<GoodsCategory> getAllCategories() {
        // 按 categoryType + id 排序，保证首页分类展示稳定。
        LambdaQueryWrapper<GoodsCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(GoodsCategory::getCategoryType, GoodsCategory::getId);
        return categoryMapper.selectList(wrapper);
    }

    /**
     * 根据父分类 ID 获取子分类列表。
     *
     * @param parentId 父分类 ID
     * @return 子分类列表
     */
    public List<GoodsCategory> getSubCategories(Integer parentId) {
        LambdaQueryWrapper<GoodsCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsCategory::getParentId, parentId);
        return categoryMapper.selectList(wrapper);
    }

    public List<GoodsPicture> getPictures(String goodsNo) {
        // 商品详情页的轮播图来自 goods_picture 表；没有额外图片时前端会退回商品主图。
        LambdaQueryWrapper<GoodsPicture> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsPicture::getGoodsNo, goodsNo);
        return pictureMapper.selectList(wrapper);
    }

    public List<GoodsStore> getStoreGoods(String goodsNo) {
        // goods_store 保存同一商品在不同门店的价格和库存，详情页选择门店时会用到。
        LambdaQueryWrapper<GoodsStore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsStore::getGoodsNo, goodsNo);
        List<GoodsStore> stores = goodsStoreMapper.selectList(wrapper);
        stores.forEach(item -> item.setGoodsPrice(specialService.effectivePrice(goodsNo, item.getGoodsPrice())));
        return stores;
    }

    public void addFavorite(Integer userId, String goodsNo, String storeNo) {
        // 收藏前确认商品仍然上架，避免收藏已删除或已下架商品。
        if (getByGoodsNo(goodsNo) == null) {
            throw new RuntimeException("商品不存在或已下架");
        }
        // 同一用户同一商品只保留一条收藏；再次收藏时更新偏好的门店编号。
        LambdaQueryWrapper<GoodsFavorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsFavorites::getUserId, userId).eq(GoodsFavorites::getGoodsNo, goodsNo).last("limit 1");
        GoodsFavorites existing = favoritesMapper.selectOne(wrapper);
        if (existing != null) {
            existing.setStoreNo(storeNo);
            favoritesMapper.updateById(existing);
            return;
        }
        GoodsFavorites favorite = new GoodsFavorites();
        favorite.setUserId(userId);
        favorite.setGoodsNo(goodsNo);
        favorite.setStoreNo(storeNo);
        favoritesMapper.insert(favorite);
    }

    public void removeFavorite(Integer userId, String goodsNo) {
        // 取消收藏按 userId + goodsNo 精确删除，不影响其他用户收藏。
        LambdaQueryWrapper<GoodsFavorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsFavorites::getUserId, userId).eq(GoodsFavorites::getGoodsNo, goodsNo);
        favoritesMapper.delete(wrapper);
    }

    public List<FavoriteGoodsView> getUserFavorites(Integer userId) {
        // 收藏列表直接返回商品展示信息，居民端页面不需要再逐条查商品详情。
        LambdaQueryWrapper<GoodsFavorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsFavorites::getUserId, userId).orderByDesc(GoodsFavorites::getCreateTime);
        List<GoodsFavorites> favorites = favoritesMapper.selectList(wrapper);
        if (favorites.isEmpty()) {
            return List.of();
        }
        List<String> goodsNos = favorites.stream().map(GoodsFavorites::getGoodsNo)
            .filter(Objects::nonNull).distinct().toList();
        List<Goods> favoriteGoods = goodsNos.isEmpty() ? List.of()
            : goodsMapper.selectList(new LambdaQueryWrapper<Goods>().in(Goods::getGoodsNo, goodsNos));
        specialService.enrichGoods(favoriteGoods);
        Map<String, Goods> goodsMap = favoriteGoods.stream()
            .collect(Collectors.toMap(Goods::getGoodsNo, Function.identity(), (a, b) -> a));
        Map<String, GoodsStore> storeMap = goodsNos.isEmpty() ? Map.of()
            : goodsStoreMapper.selectList(new LambdaQueryWrapper<GoodsStore>()
                    .in(GoodsStore::getGoodsNo, goodsNos))
                .stream().collect(Collectors.toMap(
                    item -> item.getGoodsNo() + "::" + item.getStoreNo(),
                    Function.identity(),
                    (a, b) -> a));

        return favorites.stream().map(favorite -> {
            Goods goods = goodsMap.get(favorite.getGoodsNo());
            GoodsStore goodsStore = storeMap.get(favorite.getGoodsNo() + "::" + favorite.getStoreNo());
            if (goodsStore == null) {
                goodsStore = storeMap.values().stream()
                    .filter(item -> Objects.equals(item.getGoodsNo(), favorite.getGoodsNo()))
                    .findFirst().orElse(null);
            }
            FavoriteGoodsView view = new FavoriteGoodsView();
            view.setId(favorite.getId());
            view.setGoodsNo(favorite.getGoodsNo());
            view.setStoreNo(favorite.getStoreNo());
            view.setCreateTime(favorite.getCreateTime());
            if (goods != null) {
                view.setGoodsName(goods.getGoodsName());
                view.setGoodsPicture(goods.getGoodsPicture());
                view.setGoodsMarketPrice(goods.getGoodsMarketPrice());
                view.setPromotionPrice(goods.getPromotionPrice());
                view.setPromotionLabel(goods.getPromotionLabel());
                view.setBadgeText(goods.getBadgeText());
                view.setSpecialName(goods.getSpecialName());
                view.setGoodsState(goods.getGoodsState());
            }
            if (goodsStore != null) {
                view.setStoreNo(goodsStore.getStoreNo());
                view.setGoodsPrice(specialService.effectivePrice(favorite.getGoodsNo(), goodsStore.getGoodsPrice()));
                view.setGoodsStock(goodsStore.getGoodsStock());
            }
            return view;
        }).toList();
    }
}
