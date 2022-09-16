package com.atguigu.eduservice.client;

import com.atguigu.commonutils.R;
import com.atguigu.eduservice.client.impl.VideoClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "service-vod",fallback = VideoClientImpl.class)
public interface VideoClient {
    //删除视频
    @DeleteMapping("/eduvod/video/removeVideo/{videoId}")
    R removeVideo(@PathVariable("videoId") String videoId);

    @DeleteMapping("/eduvod/video/removeBathVideo")
    R removeBathVideo(@RequestParam("videoIdList") List<String> videoIdList);
}
