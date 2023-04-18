package com.atguigu.eduservice.service.impl;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.client.VideoClient;
import com.atguigu.eduservice.entity.EduChapter;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.entity.vo.ChapterVo;
import com.atguigu.eduservice.entity.vo.VideoVo;
import com.atguigu.eduservice.mapper.EduChapterMapper;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduVideoService;
import com.atguigu.serviceBase.ExceptionHandler.SvvvvvException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-10
 */
@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {
    @Autowired
    private EduVideoService videoService;
    @Resource
    private VideoClient videoClient;


    //根据id查询章节和小节
    @Override
    public List<ChapterVo> getChapterVideoById(String courseId) {
        //查询eduChapter
        QueryWrapper<EduChapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq("course_id", courseId).orderByDesc("sort");
        List<EduChapter> eduChapters = baseMapper.selectList(chapterQueryWrapper);

        //查询所有eduVideo
        QueryWrapper<EduVideo> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("course_id", courseId);
        // 排序视频
        videoQueryWrapper.orderByDesc("sort");
        List<EduVideo> eduVideos = videoService.list(videoQueryWrapper);

        //创建最终返回list
        ArrayList<ChapterVo> finalChapterVo = new ArrayList<>();

        for (EduChapter eduChapter : eduChapters) {
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(eduChapter, chapterVo);
            finalChapterVo.add(chapterVo);

            ArrayList<VideoVo> finalVideoVo = new ArrayList<>();
            for (EduVideo eduVideo : eduVideos) {
                if (eduVideo.getChapterId().equals(eduChapter.getId())) {
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(eduVideo, videoVo);
                    finalVideoVo.add(videoVo);
                }
            }
            chapterVo.setChildren(finalVideoVo);
        }
        if (eduChapters == null) {
            ChapterVo chapterVo = new ChapterVo();
            finalChapterVo.add(chapterVo);
            ArrayList<VideoVo> finalVideoVo = new ArrayList<>();
            VideoVo videoVo = new VideoVo();
            finalVideoVo.add(videoVo);
            chapterVo.setChildren(finalVideoVo);
        }
        return finalChapterVo;
    }

    @Override
    public boolean deleteChapter(String chapterId) {
        QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("chapter_id", chapterId);
        int count = videoService.count(wrapper);
        if (count > 0) {
            throw new SvvvvvException(20001, "删除失败，因为章节下还有小节");
        } else {
            int res = baseMapper.deleteById(chapterId);
            return res > 0;
        }
    }

    @Override
    public void removeChapterByCourseId(String courseId) {
        QueryWrapper<EduChapter> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        baseMapper.delete(wrapper);
    }

    @Override
    public void removeChapter(String chapterId) {
        //根据章节得到视频id
        QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("chapter_id", chapterId);
        List<EduVideo> eduVideos = videoService.list(wrapper);
        if (eduVideos != null && eduVideos.size() > 0) {
            //转化video_source_id的集合
            List<String> videoIdList = eduVideos.stream().map(EduVideo::getVideoSourceId).filter(Objects::nonNull).collect(Collectors.toList());
            if (videoIdList.size() > 0) {
                R r = videoClient.removeBathVideo(videoIdList);
                if (r.getCode() == 20001){
                    throw new SvvvvvException(20001,"删除多个视频失败");
                }
            }
            videoService.remove(wrapper);
        }
        //删除章节
        baseMapper.deleteById(chapterId);

    }
}
