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

    /**
     * 上传图片文件（目前用于商品主图上传）。
     *
     * @param file 上传的图片文件
     * @return 上传结果，包含文件访问 URL
     */
    @PostMapping("/image")
    public Result<FileUploadResult> uploadImage(@RequestParam("file") MultipartFile file) {
        return Result.ok(localFileService.uploadImage(file));
    }
}
