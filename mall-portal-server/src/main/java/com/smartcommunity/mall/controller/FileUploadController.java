package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.dto.FileUploadResult;
import com.smartcommunity.mall.service.LocalFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 居民端文件上传接口，目前用于个人头像。
 */
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileUploadController {

    private final LocalFileService localFileService;

    @PostMapping("/image")
    public Result<FileUploadResult> uploadImage(@RequestParam("file") MultipartFile file) {
        return Result.ok(localFileService.uploadImage(file));
    }
}
