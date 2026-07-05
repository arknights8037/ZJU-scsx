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
 * 居民端文件上传控制器，目前用于个人头像上传。
 * 请求路径前缀：/api/file
 */
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileUploadController {

    private final LocalFileService localFileService;

    /**
     * 上传图片文件，返回访问URL。
     *
     * @param file 上传的图片文件（multipart/form-data）
     * @return 包含上传结果（文件URL等）的通用响应
     */
    @PostMapping("/image")
    public Result<FileUploadResult> uploadImage(@RequestParam("file") MultipartFile file) {
        return Result.ok(localFileService.uploadImage(file));
    }
}
