package com.atguigu.eduservice.client.impl;

import com.atguigu.commonutils.R;
import com.atguigu.eduservice.client.VideoClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VideoClientImpl implements VideoClient {
    @Override
    public R removeVideo(String videoId) {
        return R.error().message("删除视频出错了");
    }

    @Override
    public R removeBathVideo(List<String> videoIdList) {
        return R.error().message("删除多个视频出错了");
    }
}
