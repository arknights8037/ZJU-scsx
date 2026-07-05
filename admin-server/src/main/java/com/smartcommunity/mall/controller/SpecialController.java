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

/**
 * 专题活动控制器，提供专题活动列表查询和专题下商品查询功能。
 * 请求路径前缀：/api/special
 */
@RestController
@RequestMapping("/api/special")
@RequiredArgsConstructor
public class SpecialController {
    private final SpecialService specialService;

    /**
     * 获取当前正在进行的专题活动列表。
     *
     * @return 专题活动列表的通用响应
     */
    @GetMapping("/list")
    public Result<List<SpecialView>> list() {
        return Result.ok(specialService.listActive());
    }

    /**
     * 获取指定专题活动下的参与商品列表。
     *
     * @param specialId 专题活动ID
     * @return 促销商品视图列表的通用响应
     */
    @GetMapping("/{specialId}/goods")
    public Result<List<PromotionGoodsView>> goods(@PathVariable Integer specialId) {
        return Result.ok(specialService.getSpecialGoods(specialId));
    }
}
