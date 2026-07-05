package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.dto.AdminGoodsView;
import com.smartcommunity.admin.entity.Category;
import com.smartcommunity.admin.entity.Goods;
import com.smartcommunity.admin.mapper.CategoryMapper;
import com.smartcommunity.admin.mapper.GoodsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商品管理控制器，提供商品分页查询、新增/编辑及删除功能。
 * 请求路径前缀：/api/admin/goods
 */
@RestController
@RequestMapping("/api/admin/goods")
@RequiredArgsConstructor
public class GoodsController {
    private final GoodsMapper goodsMapper;
    private final CategoryMapper categoryMapper;

    /**
     * 分页查询商品列表，返回带类别名称的视图对象。
     * 按商品 ID 降序排列，新发布的商品优先显示。
     *
     * @param page 页码，默认 1
     * @param size 每页条数，默认 10，最大 100
     * @return 商品分页数据（含类别名称）
     */
    @GetMapping("/page")
    public Result<Page<AdminGoodsView>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(1, Math.min(size, 100));
        // 新增商品应立即出现在第一页，方便管理员确认图片和商品信息。
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
            .orderByDesc(Goods::getId);
        Page<Goods> sourcePage = goodsMapper.selectPage(new Page<>(safePage, safeSize), wrapper);
        Set<Integer> categoryIds = sourcePage.getRecords().stream().map(Goods::getCategoryId)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Integer, Category> categoryMap = categoryIds.isEmpty() ? Collections.emptyMap()
            : categoryMapper.selectList(new LambdaQueryWrapper<Category>().in(Category::getId, categoryIds))
                .stream().collect(Collectors.toMap(Category::getId, Function.identity(), (a, b) -> a));

        Page<AdminGoodsView> result = new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
        result.setRecords(sourcePage.getRecords().stream().map(goods -> {
            AdminGoodsView view = new AdminGoodsView();
            BeanUtils.copyProperties(goods, view);
            Category category = categoryMap.get(goods.getCategoryId());
            view.setCategoryName(category == null ? "未分类" : category.getCategoryName());
            return view;
        }).toList());
        return Result.ok(result);
    }

    /**
     * 新增或编辑商品。
     * 校验商品名称、类别必填；商品编号为空时自动生成；检查编号唯一性。
     *
     * @param g 商品实体
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> save(@RequestBody Goods g) {
        if (!StringUtils.hasText(g.getGoodsName())) {
            throw new RuntimeException("请填写商品名称");
        }
        if (!StringUtils.hasText(g.getGoodsNo())) {
            // 编号属于系统字段，新增时允许后端生成，减少手工录入错误。
            g.setGoodsNo(UUID.randomUUID().toString().replace("-", ""));
        }
        if (g.getGoodsState() == null) {
            g.setGoodsState(1);
        }
        if (g.getCategoryId() == null || categoryMapper.selectById(g.getCategoryId()) == null) {
            throw new RuntimeException("请选择有效的商品类别");
        }

        LambdaQueryWrapper<Goods> duplicateWrapper = new LambdaQueryWrapper<Goods>()
            .eq(Goods::getGoodsNo, g.getGoodsNo());
        if (g.getId() != null) {
            duplicateWrapper.ne(Goods::getId, g.getId());
        }
        if (goodsMapper.selectCount(duplicateWrapper) > 0) {
            throw new RuntimeException("商品编号已存在");
        }

        goodsMapper.insertOrUpdate(g);
        return Result.ok();
    }

    /**
     * 根据 ID 删除商品。
     *
     * @param id 商品 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) { goodsMapper.deleteById(id); return Result.ok(); }
}
