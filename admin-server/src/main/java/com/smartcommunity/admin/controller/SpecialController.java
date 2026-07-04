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

@RestController
@RequestMapping("/api/admin/special")
@RequiredArgsConstructor
public class SpecialController {
    private final AdminSpecialService specialService;

    @GetMapping("/list")
    public Result<List<AdminSpecialView>> list() {
        return Result.ok(specialService.list());
    }

    @GetMapping("/{id}")
    public Result<AdminSpecialView> detail(@PathVariable Integer id) {
        return Result.ok(specialService.detail(id));
    }

    @PostMapping
    public Result<Integer> save(@RequestBody SpecialSaveRequest request) {
        return Result.ok(specialService.save(request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        specialService.delete(id);
        return Result.ok();
    }
}
