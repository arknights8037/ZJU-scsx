package com.smartcommunity.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.dto.AdminOrderDetailView;
import com.smartcommunity.admin.dto.AdminOrderView;
import com.smartcommunity.admin.entity.AdminUser;
import com.smartcommunity.admin.entity.Goods;
import com.smartcommunity.admin.entity.OrderDetail;
import com.smartcommunity.admin.entity.Orders;
import com.smartcommunity.admin.entity.Store;
import com.smartcommunity.admin.mapper.AdminUserMapper;
import com.smartcommunity.admin.mapper.GoodsMapper;
import com.smartcommunity.admin.mapper.OrderDetailMapper;
import com.smartcommunity.admin.mapper.OrdersMapper;
import com.smartcommunity.admin.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrdersMapper ordersMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AdminUserMapper userMapper;
    private final GoodsMapper goodsMapper;
    private final StoreMapper storeMapper;

    public Page<AdminOrderView> page(int page, int size, String keyword, Integer orderState) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(1, Math.min(size, 100));
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(orderState != null, Orders::getOrderState, orderState);

        if (StringUtils.hasText(keyword)) {
            String value = keyword.trim();
            List<Integer> userIds = userMapper.selectList(new LambdaQueryWrapper<AdminUser>()
                    .like(AdminUser::getUserName, value)
                    .or()
                    .like(AdminUser::getPhone, value))
                .stream().map(AdminUser::getId).toList();
            wrapper.and(condition -> {
                condition.like(Orders::getOrderNo, value);
                if (!userIds.isEmpty()) {
                    condition.or().in(Orders::getUserId, userIds);
                }
            });
        }
        wrapper.orderByDesc(Orders::getCreateTime).orderByDesc(Orders::getId);

        Page<Orders> sourcePage = ordersMapper.selectPage(new Page<>(safePage, safeSize), wrapper);
        List<AdminOrderView> records = fillOrderViews(sourcePage.getRecords());
        Page<AdminOrderView> result = new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
        result.setRecords(records);
        return result;
    }

    public List<AdminOrderDetailView> details(String orderNo) {
        List<OrderDetail> details = orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>()
            .eq(OrderDetail::getOrderNo, orderNo)
            .orderByAsc(OrderDetail::getId));
        if (details.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Goods> goodsMap = goodsMap(details);
        Map<String, Store> storeMap = storeMap(details);
        return details.stream().map(detail -> {
            AdminOrderDetailView view = new AdminOrderDetailView();
            BeanUtils.copyProperties(detail, view);
            Goods goods = goodsMap.get(detail.getGoodsNo());
            Store store = storeMap.get(detail.getStoreNo());
            view.setGoodsName(goods == null ? "商品已删除" : goods.getGoodsName());
            view.setGoodsPicture(goods == null ? null : goods.getGoodsPicture());
            view.setStoreName(store == null ? "门店已删除" : store.getStoreName());
            return view;
        }).toList();
    }

    /**
     * 先批量查关联数据，再在内存中组装。新人可以重点对比“循环里查数据库”的 N+1 写法。
     */
    private List<AdminOrderView> fillOrderViews(List<Orders> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Integer> userIds = orders.stream().map(Orders::getUserId)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> orderNos = orders.stream().map(Orders::getOrderNo)
            .filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Integer, AdminUser> userMap = userIds.isEmpty() ? Collections.emptyMap()
            : userMapper.selectList(new LambdaQueryWrapper<AdminUser>().in(AdminUser::getId, userIds))
                .stream().collect(Collectors.toMap(AdminUser::getId, Function.identity(), (a, b) -> a));
        List<OrderDetail> allDetails = orderNos.isEmpty() ? Collections.emptyList()
            : orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>()
                .in(OrderDetail::getOrderNo, orderNos));
        Map<String, List<OrderDetail>> detailMap = allDetails.stream()
            .collect(Collectors.groupingBy(OrderDetail::getOrderNo));
        Map<String, Goods> goodsMap = goodsMap(allDetails);
        Map<String, Store> storeMap = storeMap(allDetails);

        return orders.stream().map(order -> {
            AdminOrderView view = new AdminOrderView();
            BeanUtils.copyProperties(order, view);
            AdminUser user = userMap.get(order.getUserId());
            view.setUserName(user == null ? "未知用户" : user.getUserName());
            view.setUserPhone(user == null ? "" : user.getPhone());

            List<OrderDetail> details = detailMap.getOrDefault(order.getOrderNo(), Collections.emptyList());
            view.setItemCount(details.stream().map(OrderDetail::getGoodsAmount)
                .filter(Objects::nonNull).mapToInt(Integer::intValue).sum());
            view.setGoodsSummary(summary(details.stream()
                .map(item -> goodsMap.get(item.getGoodsNo()))
                .filter(Objects::nonNull).map(Goods::getGoodsName).toList()));
            view.setStoreSummary(summary(details.stream()
                .map(item -> storeMap.get(item.getStoreNo()))
                .filter(Objects::nonNull).map(Store::getStoreName).toList()));
            return view;
        }).toList();
    }

    private Map<String, Goods> goodsMap(List<OrderDetail> details) {
        Set<String> goodsNos = details.stream().map(OrderDetail::getGoodsNo)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        if (goodsNos.isEmpty()) {
            return Collections.emptyMap();
        }
        return goodsMapper.selectList(new LambdaQueryWrapper<Goods>().in(Goods::getGoodsNo, goodsNos))
            .stream().collect(Collectors.toMap(Goods::getGoodsNo, Function.identity(), (a, b) -> a));
    }

    private Map<String, Store> storeMap(List<OrderDetail> details) {
        Set<String> storeNos = details.stream().map(OrderDetail::getStoreNo)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        if (storeNos.isEmpty()) {
            return Collections.emptyMap();
        }
        return storeMapper.selectList(new LambdaQueryWrapper<Store>().in(Store::getStoreNo, storeNos))
            .stream().collect(Collectors.toMap(Store::getStoreNo, Function.identity(), (a, b) -> a));
    }

    private String summary(List<String> names) {
        LinkedHashSet<String> distinctNames = names.stream()
            .filter(StringUtils::hasText).collect(Collectors.toCollection(LinkedHashSet::new));
        if (distinctNames.isEmpty()) {
            return "暂无信息";
        }
        List<String> firstNames = distinctNames.stream().limit(2).toList();
        String result = String.join("、", firstNames);
        return distinctNames.size() > 2 ? result + " 等" + distinctNames.size() + "种" : result;
    }
}
