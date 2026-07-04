package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.dto.FileUploadResult;
import com.smartcommunity.admin.service.LocalFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理端图片上传接口，目前用于商品主图。
 */
@RestController
@RequestMapping("/api/admin/file")
@RequiredArgsConstructor
public class FileUploadController {

    private final LocalFileService localFileService;

    @PostMapping("/image")
    public Result<FileUploadResult> uploadImage(@RequestParam("file") MultipartFile file) {
        return Result.ok(localFileService.uploadImage(file));
    }
}
