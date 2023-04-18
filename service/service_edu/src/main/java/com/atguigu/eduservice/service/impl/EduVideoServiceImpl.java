package com.atguigu.eduservice.service.impl;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.client.VideoClient;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.mapper.EduVideoMapper;
import com.atguigu.eduservice.service.EduVideoService;
import com.atguigu.serviceBase.ExceptionHandler.SvvvvvException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-10
 */
@Service
public class EduVideoServiceImpl extends ServiceImpl<EduVideoMapper, EduVideo> implements EduVideoService {

    @Resource
    private VideoClient videoClient;
    @Override
    public void removeVideoByCourseId(String courseId) {
        QueryWrapper<EduVideo> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("course_id",courseId);
        //指定只查询video_source_id
        wrapper1.select("video_source_id");
        List<EduVideo> eduVideos = baseMapper.selectList(wrapper1);
        //转化video_source_id的集合
        List<String> videoIdList = eduVideos.stream().map(EduVideo::getVideoSourceId).filter(Objects::nonNull).collect(Collectors.toList());
        if (videoIdList.size() > 0) {
            R r = videoClient.removeBathVideo(videoIdList);
            if (r.getCode() == 20001){
                throw new SvvvvvException(20001,"删除多个视频失败");
            }
        }
        QueryWrapper<EduVideo> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("course_id",courseId);
        baseMapper.delete(wrapper2);
    }
}
