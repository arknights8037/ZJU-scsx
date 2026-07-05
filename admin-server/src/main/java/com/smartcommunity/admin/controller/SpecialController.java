package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.dto.AdminSpecialView;
import com.smartcommunity.admin.dto.SpecialSaveRequest;
import com.smartcommunity.admin.service.AdminSpecialService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 专题活动管理控制器，提供专题列表查询、详情查看、新增/编辑和删除功能。
 * 请求路径前缀：/api/admin/special
 */
@RestController
@RequestMapping("/api/admin/special")
@RequiredArgsConstructor
public class SpecialController {
    private final AdminSpecialService specialService;

    /**
     * 获取所有专题活动列表。
     *
     * @return 专题视图列表
     */
    @GetMapping("/list")
    public Result<List<AdminSpecialView>> list() {
        return Result.ok(specialService.list());
    }

    /**
     * 查询专题活动的详细信息。
     *
     * @param id 专题 ID
     * @return 专题视图详情
     */
    @GetMapping("/{id}")
    public Result<AdminSpecialView> detail(@PathVariable Integer id) {
        return Result.ok(specialService.detail(id));
    }

    /**
     * 新增或编辑专题活动。
     *
     * @param request 专题保存请求体
     * @return 新增/编辑后的专题 ID
     */
    @PostMapping
    public Result<Integer> save(@RequestBody SpecialSaveRequest request) {
        return Result.ok(specialService.save(request));
    }

    /**
     * 根据 ID 删除专题活动。
     *
     * @param id 专题 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        specialService.delete(id);
        return Result.ok();
    }
}
