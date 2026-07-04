package com.zjxy.smart.control;

import com.zjxy.smart.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${web.upload.path}")
    private String path;

    @RequestMapping("upload")
    public Result upload(MultipartFile file){
        //System.out.println(path);
        //System.out.println(file.getOriginalFilename());

        //原文件名
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        long time = System.currentTimeMillis();
        //System.out.println(time);
        //新文件名
        String newName = time + suffixName;
        //System.out.println(newName);

        try {
            FileOutputStream os = new FileOutputStream(path + "\\" + newName);
            FileCopyUtils.copy(file.getInputStream(),os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Result result = new Result();
        result.setMsg(newName);
        return result;
    }
}
