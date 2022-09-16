package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.client.VideoClient;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.service.EduVideoService;
import com.atguigu.serviceBase.ExceptionHandler.GuliException;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2022-09-10
 */
@RestController
@RequestMapping("/eduservice/video")
@Api(tags = "小节管理")
@Transactional
public class EduVideoController {

    @Autowired
    private EduVideoService videoService;

    @Resource
    private VideoClient videoClient;


    //添加小节
    @PostMapping("/addVideo")
    public R addVideo(@RequestBody EduVideo video) {
        videoService.save(video);
        return R.ok();
    }

    //根据id修改小节
    @PostMapping("/updateVideo")
    public R updateVideo(@RequestBody EduVideo video) {
        videoService.updateById(video);
        return R.ok();
    }

    //删除小节根据id 删除阿里云视频
    @DeleteMapping("/{id}")
    public R deleteVideo(@PathVariable String id) {
        //根据videoId得到阿里云视频id
        EduVideo eduVideo = videoService.getById(id);
        String videoSourceId = eduVideo.getVideoSourceId();
        if (videoSourceId != null) {//小节存在视频
            R r = videoClient.removeVideo(videoSourceId);
            if (r.getCode() == 20001){
                throw new GuliException(20001,"删除视频失败");
            }
        }

        //先删视屏再小节注意顺序
        videoService.removeById(id);
        return R.ok();
    }

    //根据id获取小节
    @GetMapping("/getVideoById/{id}")
    public R getVideoById(@PathVariable String id) {
        EduVideo video = videoService.getById(id);
        return R.ok().data("video",video);
    }

}

