package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.dto.PromotionGoodsView;
import com.smartcommunity.mall.dto.SpecialView;
import com.smartcommunity.mall.service.SpecialService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/special")
@RequiredArgsConstructor
public class SpecialController {
    private final SpecialService specialService;

    @GetMapping("/list")
    public Result<List<SpecialView>> list() {
        return Result.ok(specialService.listActive());
    }

    @GetMapping("/{specialId}/goods")
    public Result<List<PromotionGoodsView>> goods(@PathVariable Integer specialId) {
        return Result.ok(specialService.getSpecialGoods(specialId));
    }
}
