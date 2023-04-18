package com.atguigu.eduservice.service;


import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.entity.frontVo.CourseFrontVo;
import com.atguigu.eduservice.entity.frontVo.CourseWebVo;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-10
 */
public interface EduCourseService extends IService<EduCourse> {

    String saveCourse(CourseInfoVo courseInfoVo);

    CourseInfoVo getCourseInfo(String courseId);


    void updateCourse(CourseInfoVo courseInfoVo);

    CoursePublishVo getPublishCourseInfo(String id);

    void pageQuery(Page<EduCourse> pageParam, CourseQuery courseQuery);

    void deleteCourse(String courseId);

    List<EduCourse> getIndexCourse(HttpServletRequest request);

    Map<String,Object> pageCourseFront(Page<EduCourse> pageCourse, CourseFrontVo courseQuery);

    CourseWebVo getCourseFrontInfo(String courseId);
}
