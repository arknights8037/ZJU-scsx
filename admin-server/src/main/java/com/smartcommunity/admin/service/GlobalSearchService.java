package com.smartcommunity.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.admin.dto.GlobalSearchItem;
import com.smartcommunity.admin.entity.AdminUser;
import com.smartcommunity.admin.entity.Complaint;
import com.smartcommunity.admin.entity.Goods;
import com.smartcommunity.admin.entity.Menu;
import com.smartcommunity.admin.entity.Notice;
import com.smartcommunity.admin.entity.Orders;
import com.smartcommunity.admin.entity.Store;
import com.smartcommunity.admin.mapper.AdminUserMapper;
import com.smartcommunity.admin.mapper.ComplaintMapper;
import com.smartcommunity.admin.mapper.GoodsMapper;
import com.smartcommunity.admin.mapper.MenuMapper;
import com.smartcommunity.admin.mapper.NoticeMapper;
import com.smartcommunity.admin.mapper.OrdersMapper;
import com.smartcommunity.admin.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理端全局搜索服务。
 * 在用户、商品、订单、报修、公告、门店、菜单等多个业务表中并行搜索，
 * 每种类型最多返回 5 条结果，减轻数据库压力。
 */
@Service
@RequiredArgsConstructor
public class GlobalSearchService {

    private final AdminUserMapper userMapper;
    private final GoodsMapper goodsMapper;
    private final OrdersMapper ordersMapper;
    private final ComplaintMapper complaintMapper;
    private final NoticeMapper noticeMapper;
    private final StoreMapper storeMapper;
    private final MenuMapper menuMapper;

    /**
     * 全局搜索：在用户、商品、订单、报修、公告、门店、菜单等表中并行搜索。
     * 每种类型最多返回 5 条，避免一次搜索产生过多数据库查询。
     *
     * @param keyword 搜索关键词
     * @return 搜索结果条目列表
     */
    public List<GlobalSearchItem> search(String keyword) {
        if (!StringUtils.hasText(keyword)) return List.of();
        String value = keyword.trim();
        List<GlobalSearchItem> result = new ArrayList<>();

        // 全局搜索采用"多表各取前 5 条"的轻量策略，避免一次关键词搜索拖慢后台首页。
        // 每条结果都带 targetPath，前端可以直接跳转到对应业务页面继续处理。
        userMapper.selectList(new LambdaQueryWrapper<AdminUser>()
                .and(item -> item.like(AdminUser::getUserName, value)
                    .or().like(AdminUser::getPhone, value)
                    .or().like(AdminUser::getMail, value))
                .orderByDesc(AdminUser::getId).last("limit 5"))
            .forEach(item -> result.add(new GlobalSearchItem(
                "USER", "用户", text(item.getUserName(), "未命名用户"),
                text(item.getPhone(), "无手机号"), "/admin/auth/user", value)));

        goodsMapper.selectList(new LambdaQueryWrapper<Goods>()
                .and(item -> item.like(Goods::getGoodsName, value)
                    .or().like(Goods::getGoodsNo, value)
                    .or().like(Goods::getGoodsIntroduce, value))
                .orderByDesc(Goods::getId).last("limit 5"))
            .forEach(item -> result.add(new GlobalSearchItem(
                "GOODS", "商品", text(item.getGoodsName(), item.getGoodsNo()),
                "商品编号 " + text(item.getGoodsNo(), "--"), "/admin/goods/index", value)));

        ordersMapper.selectList(new LambdaQueryWrapper<Orders>()
                .like(Orders::getOrderNo, value)
                .orderByDesc(Orders::getCreateTime).last("limit 5"))
            .forEach(item -> result.add(new GlobalSearchItem(
                "ORDER", "订单", "订单 " + item.getOrderNo(),
                "金额 ¥" + String.format("%.2f", item.getTotalPrice() == null ? 0D : item.getTotalPrice()),
                "/admin/order/index", item.getOrderNo())));

        complaintMapper.selectList(new LambdaQueryWrapper<Complaint>()
                .like(Complaint::getComplaintContent, value)
                .orderByDesc(Complaint::getCreateTime).last("limit 5"))
            .forEach(item -> result.add(new GlobalSearchItem(
                "COMPLAINT", "报修", "报修工单 #" + item.getId(),
                excerpt(item.getComplaintContent()), "/admin/community/complaint", value)));

        noticeMapper.selectList(new LambdaQueryWrapper<Notice>()
                .and(item -> item.like(Notice::getNoticeTitle, value)
                    .or().like(Notice::getNoticeContent, value))
                .orderByDesc(Notice::getCreateTime).last("limit 5"))
            .forEach(item -> result.add(new GlobalSearchItem(
                "NOTICE", "公告", text(item.getNoticeTitle(), "未命名公告"),
                excerpt(item.getNoticeContent()), "/admin/community/notice", value)));

        storeMapper.selectList(new LambdaQueryWrapper<Store>()
                .and(item -> item.like(Store::getStoreName, value)
                    .or().like(Store::getStoreNo, value)
                    .or().like(Store::getStoreAddress, value))
                .orderByDesc(Store::getId).last("limit 5"))
            .forEach(item -> result.add(new GlobalSearchItem(
                "STORE", "门店", text(item.getStoreName(), item.getStoreNo()),
                text(item.getStoreAddress(), "地址未填写"), "/admin/area/store", value)));

        // 菜单搜索：只查已启用且有有效路径的菜单项
        menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .like(Menu::getMenuName, value)
                .eq(Menu::getMenuState, 1)
                .isNotNull(Menu::getMenuPath)
                .ne(Menu::getMenuPath, "")
                .orderByAsc(Menu::getId).last("limit 5"))
            .forEach(item -> result.add(new GlobalSearchItem(
                "MENU", "功能", item.getMenuName(), "打开后台功能页面",
                normalizePath(item.getMenuPath()), value)));
        return result;
    }

    /**
     * 标准化菜单路径：如果是子路径则补全为 /admin 开头的完整路由。
     *
     * @param path 原始菜单路径
     * @return 标准化后的完整路径
     */
    private String normalizePath(String path) {
        // 菜单表里有的路径只存子路径，这里统一补成管理端绝对路由。
        if (!StringUtils.hasText(path)) return "/admin/dashboard";
        return path.startsWith("/admin") ? path : "/admin" + (path.startsWith("/") ? path : "/" + path);
    }

    /**
     * 截取文本前 70 个字符作为摘要，超出部分用省略号代替。
     *
     * @param value 原始文本
     * @return 摘要文本
     */
    private String excerpt(String value) {
        if (!StringUtils.hasText(value)) return "暂无摘要";
        String plain = value.replaceAll("[\\r\\n]+", " ").trim();
        return plain.length() > 70 ? plain.substring(0, 70) + "…" : plain;
    }

    /**
     * 取第一个非空字符串，为空时使用 fallback。
     *
     * @param value    首选值
     * @param fallback 备选值
     * @return 非空字符串
     */
    private String text(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }
}
