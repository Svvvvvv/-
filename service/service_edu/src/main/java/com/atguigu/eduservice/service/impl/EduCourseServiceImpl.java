package com.atguigu.eduservice.service.impl;


import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduCourseDescription;
import com.atguigu.eduservice.entity.frontVo.CourseFrontVo;
import com.atguigu.eduservice.entity.frontVo.CourseWebVo;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQuery;
import com.atguigu.eduservice.mapper.EduCourseMapper;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduCourseDescriptionService;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduVideoService;
import com.atguigu.serviceBase.ExceptionHandler.GuliException;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-10
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    private EduCourseDescriptionService descriptionService;
    @Autowired
    private EduChapterService chapterService;
    @Autowired
    private EduVideoService videoService;

    @Override
    public String saveCourse(CourseInfoVo courseInfoVo) {
        if (courseInfoVo == null) {
            throw new GuliException(20001, "课程信息为空");
        }
        //添加课程表信息
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo, eduCourse);
        int insert = baseMapper.insert(eduCourse);
        if (insert == 0) {
            throw new GuliException(20001, "添加课程信息失败");
        }

        //添加课程简介表信息
        String id = eduCourse.getId();
        EduCourseDescription description = new EduCourseDescription();
        description.setId(id);
        description.setDescription(courseInfoVo.getDescription());
        boolean save = descriptionService.save(description);
        if (!save) {
            throw new GuliException(20001, "添加课程简介失败");
        }

        return eduCourse.getId();
    }

    @Override
    public CourseInfoVo getCourseInfo(String courseId) {
        //查询课程表
        EduCourse eduCourse = baseMapper.selectById(courseId);
        CourseInfoVo courseInfoVo = new CourseInfoVo();
        BeanUtils.copyProperties(eduCourse, courseInfoVo);
        //查询简介表
        EduCourseDescription courseDescription = descriptionService.getById(courseId);
        if (courseDescription == null) {
            courseInfoVo.setDescription("");
            return courseInfoVo;
        }
        courseInfoVo.setDescription(courseDescription.getDescription());

        return courseInfoVo;
    }

    @Override
    public void updateCourse(CourseInfoVo courseInfoVo) {
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo, eduCourse);
        //修改课程表
        int i = baseMapper.updateById(eduCourse);
        if (i == 0) {
            throw new GuliException(20001, "修改课程信息失败(课程表)");
        }
        //修改借鉴表
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        eduCourseDescription.setId(courseInfoVo.getId());
        eduCourseDescription.setDescription(courseInfoVo.getDescription());
        boolean update = descriptionService.updateById(eduCourseDescription);
        if (!update) {
            throw new GuliException(20001, "修改课程信息失败(简介表)");
        }
    }

    @Override
    public CoursePublishVo getPublishCourseInfo(String id) {
        CoursePublishVo publishCourseInfo = baseMapper.getPublishCourseInfo(id);
        return publishCourseInfo;
    }

    @Override
    public void pageQuery(Page<EduCourse> pageParam, CourseQuery courseQuery) {
        if (courseQuery == null) {
            baseMapper.selectPage(pageParam, null);
            return;
        }

        String title = courseQuery.getTitle();
        String status = courseQuery.getStatus();
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.like(!StringUtils.isEmpty(title),"title",title)
                .eq(!StringUtils.isEmpty(status),"status",status)
                .orderByDesc("gmt_modified");
        baseMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void deleteCourse(String courseId) {
        //删除video表
        videoService.removeVideoByCourseId(courseId);

        //删除chapter表
        chapterService.removeChapterByCourseId(courseId);

        //删除简介表
        descriptionService.removeById(courseId);

        //删除课程表
        baseMapper.deleteById(courseId);

    }

    @Override
    public CourseWebVo getCourseFrontInfo(String courseId) {
        CourseWebVo courseWebVo = baseMapper.getCourseFrontInfo(courseId);
        return courseWebVo;
    }

    @Override
    public Map<String, Object> pageCourseFront(Page<EduCourse> pageCourse, CourseFrontVo courseQuery) {
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("status","Normal");
        wrapper.eq(!StringUtils.isEmpty(courseQuery.getSubjectParentId()), "subject_parent_id",courseQuery.getSubjectParentId());
        wrapper.eq(!StringUtils.isEmpty(courseQuery.getSubjectId()), "subject_id",courseQuery.getSubjectId());
        wrapper.orderByDesc(!StringUtils.isEmpty(courseQuery.getBuyCountSort()),"buy_count");
        wrapper.orderByDesc(!StringUtils.isEmpty(courseQuery.getGmtCreateSort()),"gmt_create");
        wrapper.orderByDesc(!StringUtils.isEmpty(courseQuery.getPriceSort()),"price");

        baseMapper.selectPage(pageCourse, wrapper);

        List<EduCourse> records = pageCourse.getRecords();
        long current = pageCourse.getCurrent();
        long pages = pageCourse.getPages();
        long size = pageCourse.getSize();
        long total = pageCourse.getTotal();
        boolean hasNext = pageCourse.hasNext();
        boolean hasPrevious = pageCourse.hasPrevious();

        HashMap<String, Object> map = new HashMap<>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return map;
    }

    @Override
    @Cacheable(value = "course",key = "'selectHotCourse'")
    public List<EduCourse> getIndexCourse() {
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("view_count");
        wrapper.last("limit 8");
        List<EduCourse> courseList = baseMapper.selectList(wrapper);
        return courseList;
    }
}
