package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.mall.dto.PromotionGoodsView;
import com.smartcommunity.mall.dto.SpecialView;
import com.smartcommunity.mall.entity.Goods;
import com.smartcommunity.mall.entity.Special;
import com.smartcommunity.mall.entity.SpecialGoods;
import com.smartcommunity.mall.entity.SpecialRule;
import com.smartcommunity.mall.mapper.GoodsMapper;
import com.smartcommunity.mall.mapper.SpecialGoodsMapper;
import com.smartcommunity.mall.mapper.SpecialMapper;
import com.smartcommunity.mall.mapper.SpecialRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/** 居民端促销服务，只返回当前时间真正生效的活动。 */
@Service
@RequiredArgsConstructor
public class SpecialService {
    private static final String DISCOUNT = "DISCOUNT";
    private static final String REDUCE = "REDUCE";

    private final SpecialMapper specialMapper;
    private final SpecialRuleMapper ruleMapper;
    private final SpecialGoodsMapper specialGoodsMapper;
    private final GoodsMapper goodsMapper;

    /**
     * 获取当前时间范围内所有已启用的促销活动。
     * 会自动过滤掉未开始或已结束的活动。
     *
     * @return 生效中的促销活动列表
     */
    public List<SpecialView> listActive() {
        List<Special> specials = specialMapper.selectList(new LambdaQueryWrapper<Special>()
            .eq(Special::getSpecialStatus, 1));
        if (specials.isEmpty()) return Collections.emptyList();

        Set<Integer> ids = specials.stream().map(Special::getId).collect(Collectors.toSet());
        Map<Integer, SpecialRule> ruleMap = ruleMapper.selectList(new LambdaQueryWrapper<SpecialRule>()
                .in(SpecialRule::getSpecialId, ids))
            .stream().collect(Collectors.toMap(SpecialRule::getSpecialId, Function.identity()));
        LocalDateTime now = LocalDateTime.now();
        return specials.stream()
            .filter(special -> isInTime(ruleMap.get(special.getId()), now))
            .map(special -> toView(special, ruleMap.get(special.getId())))
            .sorted(Comparator.comparing(SpecialView::getSortOrder)
                .thenComparing(SpecialView::getId, Comparator.reverseOrder()))
            .toList();
    }

    /**
     * 获取指定促销活动中的商品列表（仅返回上架商品）。
     *
     * @param specialId 活动 ID
     * @return 促销商品视图列表
     */
    public List<PromotionGoodsView> getSpecialGoods(Integer specialId) {
        Special special = specialMapper.selectById(specialId);
        if (special == null || !Objects.equals(special.getSpecialStatus(), 1)) {
            return Collections.emptyList();
        }
        SpecialRule rule = findRule(specialId);
        if (!isInTime(rule, LocalDateTime.now())) {
            return Collections.emptyList();
        }

        List<SpecialGoods> relations = specialGoodsMapper.selectList(new LambdaQueryWrapper<SpecialGoods>()
            .eq(SpecialGoods::getSpecialId, specialId)
            .orderByAsc(SpecialGoods::getId));
        if (relations.isEmpty()) return Collections.emptyList();

        Set<String> goodsNos = relations.stream().map(SpecialGoods::getGoodsNo).collect(Collectors.toSet());
        Map<String, Goods> goodsMap = goodsMapper.selectList(new LambdaQueryWrapper<Goods>()
                .in(Goods::getGoodsNo, goodsNos)
                .eq(Goods::getGoodsState, 1))
            .stream().collect(Collectors.toMap(Goods::getGoodsNo, Function.identity(), (a, b) -> a));
        return relations.stream()
            .map(item -> goodsMap.get(item.getGoodsNo()))
            .filter(Objects::nonNull)
            .map(goods -> toGoodsView(goods, rule, special.getSpecialName()))
            .toList();
    }

    /**
     * 返回所有当前生效促销商品映射。
     * 同一商品参加多场活动时，保留到手价更低的那一场。
     *
     * @return 商品编号 -> 促销商品视图 映射
     */
    public Map<String, PromotionGoodsView> activePromotionMap() {
        Map<String, PromotionGoodsView> result = new LinkedHashMap<>();
        for (SpecialView special : listActive()) {
            for (PromotionGoodsView goods : getSpecialGoods(special.getId())) {
                result.merge(goods.getGoodsNo(), goods, (oldValue, newValue) -> {
                    if (oldValue.getPromotionPrice() == null) return newValue;
                    if (newValue.getPromotionPrice() == null) return oldValue;
                    return newValue.getPromotionPrice().compareTo(oldValue.getPromotionPrice()) < 0
                        ? newValue : oldValue;
                });
            }
        }
        return result;
    }

    /**
     * 为单个商品填充促销信息（促销价、标签、角标等）。
     *
     * @param goods 商品对象
     * @return 填充促销信息后的商品
     */
    public Goods enrichGoods(Goods goods) {
        if (goods == null || goods.getGoodsNo() == null) return goods;
        PromotionGoodsView promotion = activePromotionMap().get(goods.getGoodsNo());
        if (promotion != null) {
            goods.setPromotionPrice(promotion.getPromotionPrice());
            goods.setPromotionLabel(promotion.getPromotionLabel());
            goods.setBadgeText(promotion.getBadgeText());
            goods.setSpecialName(promotion.getSpecialName());
        }
        return goods;
    }

    /**
     * 为商品列表批量填充促销信息。
     *
     * @param goodsList 商品列表
     * @return 填充促销信息后的商品列表
     */
    public List<Goods> enrichGoods(List<Goods> goodsList) {
        if (goodsList == null || goodsList.isEmpty()) return goodsList;
        Map<String, PromotionGoodsView> promotionMap = activePromotionMap();
        for (Goods goods : goodsList) {
            PromotionGoodsView promotion = promotionMap.get(goods.getGoodsNo());
            if (promotion == null) continue;
            goods.setPromotionPrice(promotion.getPromotionPrice());
            goods.setPromotionLabel(promotion.getPromotionLabel());
            goods.setBadgeText(promotion.getBadgeText());
            goods.setSpecialName(promotion.getSpecialName());
        }
        return goodsList;
    }

    /**
     * 计算商品在促销活动中的实际到手价。
     * 有促销时取促销价与门店价中较低者；无促销时直接返回门店价。
     *
     * @param goodsNo   商品编号
     * @param storePrice 门店售价
     * @return 实际有效价格
     */
    public BigDecimal effectivePrice(String goodsNo, BigDecimal storePrice) {
        PromotionGoodsView promotion = activePromotionMap().get(goodsNo);
        if (promotion == null || promotion.getPromotionPrice() == null) return storePrice;
        if (storePrice == null) return promotion.getPromotionPrice();
        return promotion.getPromotionPrice().min(storePrice);
    }

    /**
     * 将商品 + 规则组装为促销商品视图。
     * 根据促销类型（立减/折扣）计算促销价。
     *
     * @param goods       商品对象
     * @param rule        活动规则
     * @param specialName 活动名称
     * @return 促销商品视图
     */
    private PromotionGoodsView toGoodsView(Goods goods, SpecialRule rule, String specialName) {
        PromotionGoodsView view = new PromotionGoodsView();
        BeanUtils.copyProperties(goods, view);
        view.setSpecialName(specialName);
        BigDecimal value = rule == null || rule.getDiscountValue() == null
            ? new BigDecimal("9.00") : rule.getDiscountValue();
        String type = rule == null ? DISCOUNT : rule.getPromotionType();
        BigDecimal original = goods.getGoodsMarketPrice();
        if (original == null || original.compareTo(BigDecimal.ZERO) <= 0) {
            // 老数据中有未定价商品，保持为空比计算成 0 元更符合业务含义。
            view.setPromotionPrice(null);
            view.setPromotionLabel(REDUCE.equals(type) ? "立减优惠" : value.stripTrailingZeros().toPlainString() + "折");
            view.setBadgeText(rule == null || rule.getBadgeText() == null ? "限时优惠" : rule.getBadgeText());
            return view;
        }
        BigDecimal promotionPrice;
        if (REDUCE.equals(type)) {
            promotionPrice = original.subtract(value).max(new BigDecimal("0.01"));
            view.setPromotionLabel("立减¥" + value.stripTrailingZeros().toPlainString());
        } else {
            promotionPrice = original.multiply(value).divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP);
            view.setPromotionLabel(value.stripTrailingZeros().toPlainString() + "折");
        }
        view.setPromotionPrice(promotionPrice.setScale(2, RoundingMode.HALF_UP));
        view.setBadgeText(rule == null || rule.getBadgeText() == null ? "限时优惠" : rule.getBadgeText());
        return view;
    }

    /**
     * 将活动主表和规则组装为前端使用的视图对象。
     *
     * @param special 活动主表
     * @param rule    活动规则（可能为 null）
     * @return 活动视图
     */
    private SpecialView toView(Special special, SpecialRule rule) {
        SpecialView view = new SpecialView();
        view.setId(special.getId());
        view.setSpecialName(special.getSpecialName());
        view.setSpecialSubtitle(rule == null || rule.getSpecialSubtitle() == null
            ? "社区精选优惠商品" : rule.getSpecialSubtitle());
        view.setBadgeText(rule == null || rule.getBadgeText() == null ? "限时优惠" : rule.getBadgeText());
        view.setPromotionType(rule == null || rule.getPromotionType() == null ? DISCOUNT : rule.getPromotionType());
        view.setDiscountValue(rule == null || rule.getDiscountValue() == null
            ? new BigDecimal("9.00") : rule.getDiscountValue());
        view.setStartTime(rule == null ? null : rule.getStartTime());
        view.setEndTime(rule == null ? null : rule.getEndTime());
        view.setSortOrder(rule == null || rule.getSortOrder() == null ? 0 : rule.getSortOrder());
        view.setMaxItems(rule == null || rule.getMaxItems() == null ? 4 : rule.getMaxItems());
        return view;
    }

    /**
     * 判断活动规则在当前时间是否处于生效时间范围内。
     * 如果规则为 null 或者起止时间为空，按"始终生效"处理。
     *
     * @param rule 活动规则
     * @param now  当前时间
     * @return true 表示在有效期内
     */
    private boolean isInTime(SpecialRule rule, LocalDateTime now) {
        if (rule == null) return true;
        return (rule.getStartTime() == null || !now.isBefore(rule.getStartTime()))
            && (rule.getEndTime() == null || !now.isAfter(rule.getEndTime()));
    }

    /**
     * 根据活动 ID 查询关联的活动规则。
     *
     * @param specialId 活动 ID
     * @return 活动规则（可能为 null）
     */
    private SpecialRule findRule(Integer specialId) {
        return ruleMapper.selectOne(new LambdaQueryWrapper<SpecialRule>()
            .eq(SpecialRule::getSpecialId, specialId).last("LIMIT 1"));
    }
}
