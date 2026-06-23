package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.Goods;
import com.smartcommunity.admin.mapper.GoodsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/goods")
@RequiredArgsConstructor
public class GoodsController {
    private final GoodsMapper goodsMapper;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Goods> p = goodsMapper.selectPage(new Page<>(page, size), null);
        return Result.ok(p);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Goods g) { goodsMapper.insertOrUpdate(g); return Result.ok(); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) { goodsMapper.deleteById(id); return Result.ok(); }
}
