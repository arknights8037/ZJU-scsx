package com.smartcommunity.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.admin.dto.AdminSpecialView;
import com.smartcommunity.admin.dto.SpecialSaveRequest;
import com.smartcommunity.admin.entity.Goods;
import com.smartcommunity.admin.entity.Special;
import com.smartcommunity.admin.entity.SpecialGoods;
import com.smartcommunity.admin.entity.SpecialRule;
import com.smartcommunity.admin.mapper.GoodsMapper;
import com.smartcommunity.admin.mapper.SpecialGoodsMapper;
import com.smartcommunity.admin.mapper.SpecialMapper;
import com.smartcommunity.admin.mapper.SpecialRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSpecialService {
    private static final String DISCOUNT = "DISCOUNT";
    private static final String REDUCE = "REDUCE";

    private final SpecialMapper specialMapper;
    private final SpecialRuleMapper ruleMapper;
    private final SpecialGoodsMapper specialGoodsMapper;
    private final GoodsMapper goodsMapper;

    public List<AdminSpecialView> list() {
        List<Special> specials = specialMapper.selectList(new LambdaQueryWrapper<Special>()
            .orderByDesc(Special::getId));
        if (specials.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Integer> ids = specials.stream().map(Special::getId).collect(Collectors.toSet());
        Map<Integer, SpecialRule> ruleMap = ruleMapper.selectList(new LambdaQueryWrapper<SpecialRule>()
                .in(SpecialRule::getSpecialId, ids))
            .stream().collect(Collectors.toMap(SpecialRule::getSpecialId, Function.identity()));
        Map<Integer, Long> countMap = specialGoodsMapper.selectList(new LambdaQueryWrapper<SpecialGoods>()
                .in(SpecialGoods::getSpecialId, ids))
            .stream().collect(Collectors.groupingBy(SpecialGoods::getSpecialId, Collectors.counting()));

        return specials.stream()
            .map(item -> toView(item, ruleMap.get(item.getId()), countMap.getOrDefault(item.getId(), 0L), null))
            .sorted(Comparator.comparing(AdminSpecialView::getSortOrder)
                .thenComparing(AdminSpecialView::getId, Comparator.reverseOrder()))
            .toList();
    }

    public AdminSpecialView detail(Integer id) {
        Special special = requireSpecial(id);
        SpecialRule rule = findRule(id);
        List<String> goodsNos = specialGoodsMapper.selectList(new LambdaQueryWrapper<SpecialGoods>()
                .eq(SpecialGoods::getSpecialId, id)
                .orderByAsc(SpecialGoods::getId))
            .stream().map(SpecialGoods::getGoodsNo).toList();
        return toView(special, rule, goodsNos.size(), goodsNos);
    }

    @Transactional
    public Integer save(SpecialSaveRequest request) {
        validate(request);
        Special special = request.getId() == null ? new Special() : requireSpecial(request.getId());
        special.setSpecialName(request.getSpecialName().trim());
        special.setSpecialStatus(Objects.equals(request.getSpecialStatus(), 1) ? 1 : 2);
        if (special.getId() == null) {
            specialMapper.insert(special);
        } else {
            specialMapper.updateById(special);
        }

        SpecialRule rule = findRule(special.getId());
        if (rule == null) {
            rule = new SpecialRule();
            rule.setSpecialId(special.getId());
        }
        rule.setSpecialSubtitle(trimToNull(request.getSpecialSubtitle()));
        rule.setBadgeText(trimToNull(request.getBadgeText()));
        rule.setPromotionType(normalizeType(request.getPromotionType()));
        rule.setDiscountValue(request.getDiscountValue());
        rule.setStartTime(request.getStartTime());
        rule.setEndTime(request.getEndTime());
        rule.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        rule.setMaxItems(request.getMaxItems() == null ? 4 : request.getMaxItems());
        if (rule.getId() == null) {
            ruleMapper.insert(rule);
        } else {
            ruleMapper.updateById(rule);
        }

        // 先去重再重建关联，列表顺序会按管理员选择顺序保存。
        LinkedHashSet<String> goodsNos = request.getGoodsNos() == null
            ? new LinkedHashSet<>() : request.getGoodsNos().stream()
                .filter(StringUtils::hasText).collect(Collectors.toCollection(LinkedHashSet::new));
        validateGoods(goodsNos);
        specialGoodsMapper.delete(new LambdaQueryWrapper<SpecialGoods>()
            .eq(SpecialGoods::getSpecialId, special.getId()));
        for (String goodsNo : goodsNos) {
            SpecialGoods relation = new SpecialGoods();
            relation.setSpecialId(special.getId());
            relation.setGoodsNo(goodsNo);
            specialGoodsMapper.insert(relation);
        }
        return special.getId();
    }

    @Transactional
    public void delete(Integer id) {
        requireSpecial(id);
        specialGoodsMapper.delete(new LambdaQueryWrapper<SpecialGoods>()
            .eq(SpecialGoods::getSpecialId, id));
        ruleMapper.delete(new LambdaQueryWrapper<SpecialRule>().eq(SpecialRule::getSpecialId, id));
        specialMapper.deleteById(id);
    }

    private AdminSpecialView toView(Special special, SpecialRule rule, long goodsCount, List<String> goodsNos) {
        AdminSpecialView view = new AdminSpecialView();
        view.setId(special.getId());
        view.setSpecialName(special.getSpecialName());
        view.setSpecialStatus(special.getSpecialStatus());
        view.setCreateTime(special.getCreateTime());
        view.setUpdateTime(special.getUpdateTime());
        applyRuleDefaults(view, rule);
        view.setGoodsCount((int) goodsCount);
        view.setGoodsNos(goodsNos);
        view.setEffectiveStatus(effectiveStatus(special, view.getStartTime(), view.getEndTime()));
        return view;
    }

    /** 老活动没有 special_rule 记录时使用默认值，保证升级后仍能展示。 */
    private void applyRuleDefaults(AdminSpecialView view, SpecialRule rule) {
        view.setSpecialSubtitle(rule == null ? "社区精选优惠商品" : rule.getSpecialSubtitle());
        view.setBadgeText(rule == null ? "限时优惠" : rule.getBadgeText());
        view.setPromotionType(rule == null ? DISCOUNT : rule.getPromotionType());
        view.setDiscountValue(rule == null ? new BigDecimal("9.00") : rule.getDiscountValue());
        view.setStartTime(rule == null ? null : rule.getStartTime());
        view.setEndTime(rule == null ? null : rule.getEndTime());
        view.setSortOrder(rule == null || rule.getSortOrder() == null ? 0 : rule.getSortOrder());
        view.setMaxItems(rule == null || rule.getMaxItems() == null ? 4 : rule.getMaxItems());
    }

    private String effectiveStatus(Special special, LocalDateTime startTime, LocalDateTime endTime) {
        if (!Objects.equals(special.getSpecialStatus(), 1)) return "STOPPED";
        LocalDateTime now = LocalDateTime.now();
        if (startTime != null && now.isBefore(startTime)) return "NOT_STARTED";
        if (endTime != null && now.isAfter(endTime)) return "ENDED";
        return "ACTIVE";
    }

    private void validate(SpecialSaveRequest request) {
        if (!StringUtils.hasText(request.getSpecialName())) {
            throw new RuntimeException("请填写促销名称");
        }
        if (request.getStartTime() != null && request.getEndTime() != null
                && !request.getStartTime().isBefore(request.getEndTime())) {
            throw new RuntimeException("结束时间必须晚于开始时间");
        }
        String type = normalizeType(request.getPromotionType());
        BigDecimal value = request.getDiscountValue();
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("优惠值必须大于0");
        }
        if (DISCOUNT.equals(type) && value.compareTo(BigDecimal.TEN) >= 0) {
            throw new RuntimeException("折扣请输入0到10之间的数值，例如8.8表示八八折");
        }
        if (request.getMaxItems() != null && (request.getMaxItems() < 1 || request.getMaxItems() > 20)) {
            throw new RuntimeException("展示商品数必须在1到20之间");
        }
    }

    private void validateGoods(Set<String> goodsNos) {
        if (goodsNos.isEmpty()) return;
        long count = goodsMapper.selectCount(new LambdaQueryWrapper<Goods>()
            .in(Goods::getGoodsNo, goodsNos));
        if (count != goodsNos.size()) {
            throw new RuntimeException("选中的商品中存在已删除或无效数据，请重新选择");
        }
    }

    private Special requireSpecial(Integer id) {
        Special special = id == null ? null : specialMapper.selectById(id);
        if (special == null) throw new RuntimeException("促销活动不存在");
        return special;
    }

    private SpecialRule findRule(Integer specialId) {
        return ruleMapper.selectOne(new LambdaQueryWrapper<SpecialRule>()
            .eq(SpecialRule::getSpecialId, specialId).last("LIMIT 1"));
    }

    private String normalizeType(String type) {
        return REDUCE.equalsIgnoreCase(type) ? REDUCE : DISCOUNT;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
