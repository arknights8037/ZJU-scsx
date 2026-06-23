package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.entity.*;
import com.smartcommunity.mall.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        wrapper.eq(Goods::getGoodsNo, goodsNo);
        return goodsMapper.selectOne(wrapper);
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
