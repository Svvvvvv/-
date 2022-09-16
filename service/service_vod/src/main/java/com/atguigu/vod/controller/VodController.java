package com.atguigu.vod.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.atguigu.commonutils.R;
import com.atguigu.vod.service.VodService;
import com.atguigu.vod.util.AliyunVodSDKUtil;
import com.atguigu.vod.util.ConstantPropertiesUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "阿里云视频点播")
@RestController
@RequestMapping("/eduvod/video")
public class VodController {

    @Autowired
    private VodService vodService;


    //上传视频
    @PostMapping("/upload")
    public R uploadVideo(MultipartFile file) {
        String videoId = vodService.uploadVideo(file);
        return R.ok().data("videoId",videoId);
    }

    //删除视频
    @DeleteMapping("/removeVideo/{videoId}")
    public R removeVideo(@PathVariable String videoId) {
        vodService.removeVideo(videoId);
        return R.ok();
    }

    //批量删除视频
    @DeleteMapping("/removeBathVideo")
    public R removeBathVideo(@RequestParam("videoIdList") List<String> videoIdList) {
        vodService.removeBathVideo(videoIdList);
        return R.ok();
    }


    //根据视频id获取视频凭证
    @GetMapping("/getPlayAuth/{videoId}")
    public R getPlayAuth(@PathVariable String videoId){
        //获取阿里云存储相关常量
        String accessKeyId = ConstantPropertiesUtil.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtil.ACCESS_KEY_SECRET;
        //初始化
        DefaultAcsClient client = AliyunVodSDKUtil.initVodClient(accessKeyId,
                accessKeySecret);
        //请求
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        request.setVideoId(videoId);
        //响应
        GetVideoPlayAuthResponse response = null;
        try {
            response = client.getAcsResponse(request);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
        //得到播放凭证
        String playAuth = response.getPlayAuth();
        //返回结果
        return R.ok().message("获取凭证成功").data("playAuth", playAuth);
    }

}
