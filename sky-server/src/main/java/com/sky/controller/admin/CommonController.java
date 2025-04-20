package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import com.sky.utils.UUIDUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {
    @Resource
    private AliOssUtil ossUtil;

    @PostMapping("/upload")
    @ApiOperation("上传文件接口")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        log.info("上传文件接口:{}", file.getOriginalFilename());
        try {
            String fileName = file.getOriginalFilename();
            if (fileName != null) {
                String pathUrl = ossUtil.upload(file.getBytes(), UUIDUtil.getUUID(fileName));
                return Result.success(pathUrl);
            }
        } catch (IOException e) {
            log.error(MessageConstant.UPLOAD_FAILED+":{}",e.getMessage());
            throw new RuntimeException(e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}















