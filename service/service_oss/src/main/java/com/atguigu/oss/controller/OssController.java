package com.atguigu.oss.controller;

import com.atguigu.commonutils.R;
import com.atguigu.oss.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags="阿里云文件管理")
@RestController
@RequestMapping("/eduoss/fileoss")
public class OssController {
    @Autowired
    OssService ossService;

    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    public R uploadOssFile(@RequestParam("file") MultipartFile file){

        //返回上传到oss的路径
        String url = ossService.uploadFileAvatar(file);

        //返回r对象
        return R.ok().data("url",url).message("文件上传成功");
    }
}
