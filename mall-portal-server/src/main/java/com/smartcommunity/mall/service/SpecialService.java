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
        int maxItems = rule == null || rule.getMaxItems() == null ? 4 : Math.max(1, Math.min(rule.getMaxItems(), 20));

        return relations.stream()
            .map(item -> goodsMap.get(item.getGoodsNo()))
            .filter(Objects::nonNull)
            .limit(maxItems)
            .map(goods -> toGoodsView(goods, rule))
            .toList();
    }

    private PromotionGoodsView toGoodsView(Goods goods, SpecialRule rule) {
        PromotionGoodsView view = new PromotionGoodsView();
        BeanUtils.copyProperties(goods, view);
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

    private boolean isInTime(SpecialRule rule, LocalDateTime now) {
        if (rule == null) return true;
        return (rule.getStartTime() == null || !now.isBefore(rule.getStartTime()))
            && (rule.getEndTime() == null || !now.isAfter(rule.getEndTime()));
    }

    private SpecialRule findRule(Integer specialId) {
        return ruleMapper.selectOne(new LambdaQueryWrapper<SpecialRule>()
            .eq(SpecialRule::getSpecialId, specialId).last("LIMIT 1"));
    }
}
