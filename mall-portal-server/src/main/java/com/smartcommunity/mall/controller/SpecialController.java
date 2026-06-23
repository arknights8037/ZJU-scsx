package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.entity.Special;
import com.smartcommunity.mall.entity.SpecialGoods;
import com.smartcommunity.mall.service.SpecialService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 促销控制器
 */
@RestController
@RequestMapping("/api/special")
@RequiredArgsConstructor
public class SpecialController {

    private final SpecialService specialService;

    @GetMapping("/list")
    public Result<List<Special>> list() {
        return Result.ok(specialService.listActive());
    }

    @GetMapping("/{specialId}/goods")
    public Result<List<SpecialGoods>> goods(@PathVariable Integer specialId) {
        return Result.ok(specialService.getSpecialGoods(specialId));
    }
}
