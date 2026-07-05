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

/**
 * 管理端促销活动管理服务。
 * 提供促销活动的增删改查、商品关联、状态管理等功能。
 */
@Service
@RequiredArgsConstructor
public class AdminSpecialService {
    private static final String DISCOUNT = "DISCOUNT";
    private static final String REDUCE = "REDUCE";

    private final SpecialMapper specialMapper;
    private final SpecialRuleMapper ruleMapper;
    private final SpecialGoodsMapper specialGoodsMapper;
    private final GoodsMapper goodsMapper;

    /**
     * 获取促销活动列表，按 sortOrder 升序、id 降序排列。
     *
     * @return 活动视图列表，包含规则和商品数量
     */
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

    /**
     * 获取促销活动详情，包含关联的商品编号列表。
     *
     * @param id 活动 ID
     * @return 活动视图对象
     */
    public AdminSpecialView detail(Integer id) {
        Special special = requireSpecial(id);
        SpecialRule rule = findRule(id);
        List<String> goodsNos = specialGoodsMapper.selectList(new LambdaQueryWrapper<SpecialGoods>()
                .eq(SpecialGoods::getSpecialId, id)
                .orderByAsc(SpecialGoods::getId))
            .stream().map(SpecialGoods::getGoodsNo).toList();
        return toView(special, rule, goodsNos.size(), goodsNos);
    }

    /**
     * 保存或更新促销活动（包含规则和商品关联）。
     * 如果 request.id 为空则新建，否则更新已有活动。
     * 整体逻辑：先保存活动主表，再保存规则，最后清除旧商品关联并重新插入。
     *
     * @param request 活动保存请求
     * @return 活动 ID
     */
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

    /**
     * 删除促销活动，同时级联删除关联的规则和商品关系。
     *
     * @param id 活动 ID
     */
    @Transactional
    public void delete(Integer id) {
        requireSpecial(id);
        specialGoodsMapper.delete(new LambdaQueryWrapper<SpecialGoods>()
            .eq(SpecialGoods::getSpecialId, id));
        ruleMapper.delete(new LambdaQueryWrapper<SpecialRule>().eq(SpecialRule::getSpecialId, id));
        specialMapper.deleteById(id);
    }

    /**
     * 将 Special + SpecialRule 组装为 AdminSpecialView。
     * 同时计算活动当前生效状态（ACTIVE / NOT_STARTED / ENDED / STOPPED）。
     *
     * @param special    活动主表
     * @param rule       活动规则（可能为 null，使用默认值）
     * @param goodsCount 关联商品数量
     * @param goodsNos   商品编号列表（仅详情页传入）
     * @return 活动视图
     */
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

    /**
     * 当活动没有关联的 special_rule 记录时，使用合理的默认值。
     * 保证数据库中只有 special 表的老数据升级后仍能正常展示。
     *
     * @param view 活动视图（会被直接修改）
     * @param rule 活动规则（可能为 null）
     */
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

    /**
     * 根据活动状态和时间范围计算当前生效状态。
     *
     * @param special   活动主表对象
     * @param startTime 活动开始时间
     * @param endTime   活动结束时间
     * @return STOPPED（已停止）/ NOT_STARTED（未开始）/ ENDED（已结束）/ ACTIVE（进行中）
     */
    private String effectiveStatus(Special special, LocalDateTime startTime, LocalDateTime endTime) {
        if (!Objects.equals(special.getSpecialStatus(), 1)) return "STOPPED";
        LocalDateTime now = LocalDateTime.now();
        if (startTime != null && now.isBefore(startTime)) return "NOT_STARTED";
        if (endTime != null && now.isAfter(endTime)) return "ENDED";
        return "ACTIVE";
    }

    /**
     * 校验促销活动保存请求的必填字段和业务规则。
     *
     * @param request 活动保存请求
     */
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

    /**
     * 校验所选商品编号在数据库中是否存在且未删除。
     *
     * @param goodsNos 商品编号集合
     */
    private void validateGoods(Set<String> goodsNos) {
        if (goodsNos.isEmpty()) return;
        long count = goodsMapper.selectCount(new LambdaQueryWrapper<Goods>()
            .in(Goods::getGoodsNo, goodsNos));
        if (count != goodsNos.size()) {
            throw new RuntimeException("选中的商品中存在已删除或无效数据，请重新选择");
        }
    }

    /**
     * 根据 ID 获取活动，不存在则抛出异常。
     *
     * @param id 活动 ID
     * @return Special 对象
     */
    private Special requireSpecial(Integer id) {
        Special special = id == null ? null : specialMapper.selectById(id);
        if (special == null) throw new RuntimeException("促销活动不存在");
        return special;
    }

    /**
     * 根据活动 ID 查询关联的规则。
     *
     * @param specialId 活动 ID
     * @return SpecialRule（可能为 null）
     */
    private SpecialRule findRule(Integer specialId) {
        return ruleMapper.selectOne(new LambdaQueryWrapper<SpecialRule>()
            .eq(SpecialRule::getSpecialId, specialId).last("LIMIT 1"));
    }

    /**
     * 标准化促销类型：非 REDUCE（立减）则视为 DISCOUNT（折扣）。
     *
     * @param type 原始类型字符串
     * @return 标准化后的类型
     */
    private String normalizeType(String type) {
        return REDUCE.equalsIgnoreCase(type) ? REDUCE : DISCOUNT;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
