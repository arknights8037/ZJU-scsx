package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.Goods;
import com.smartcommunity.admin.mapper.GoodsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/goods")
@RequiredArgsConstructor
public class GoodsController {
    private final GoodsMapper goodsMapper;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(1, Math.min(size, 100));
        // 新增商品应立即出现在第一页，方便管理员确认图片和商品信息。
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
            .orderByDesc(Goods::getId);
        Page<Goods> p = goodsMapper.selectPage(new Page<>(safePage, safeSize), wrapper);
        return Result.ok(p);
    }

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

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) { goodsMapper.deleteById(id); return Result.ok(); }
}
