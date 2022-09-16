package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;

import com.atguigu.commonutils.vo.CourseOrderVo;
import com.atguigu.eduservice.client.OrderClient;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.frontVo.CourseWebVo;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQuery;
import com.atguigu.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2022-09-10
 */
@RestController
@RequestMapping("/eduservice/course")
@Api(tags = "课程管理")
@Transactional
public class EduCourseController {

    @Resource
    private EduCourseService eduCourseService;



    //添加课程
    @ApiOperation(value = "添加课程")
    @PostMapping("/addCourse")
    public R saveCourse(@RequestBody CourseInfoVo courseInfoVo) {
        String cid = eduCourseService.saveCourse(courseInfoVo);
        return R.ok().data("courseId", cid);
    }

    //根据id查询课程信息返回前端
    @ApiOperation(value = "查询课程信息")
    @GetMapping(value = "/getCourseInfo/{courseId}")
    public R getCourseInfo(@PathVariable("courseId") String courseId) {
        //查询课程信息
        CourseInfoVo courseInfo = eduCourseService.getCourseInfo(courseId);

        return R.ok().data("courseInfo", courseInfo);
    }

    //修改课程信息
    @ApiOperation(value = "修改课程信息")
    @PostMapping("/updateCourseInfo")
    public R updateCourse(@RequestBody CourseInfoVo courseInfoVo) {
        eduCourseService.updateCourse(courseInfoVo);
        return R.ok();
    }

    //根据id查询课程确认信息
    @GetMapping("/getPublishCourseInfo/{id}")
    public R getPublishCourseInfo(@PathVariable String id) {
        CoursePublishVo publishCourseInfo = eduCourseService.getPublishCourseInfo(id);
        return R.ok().data("publishCourse", publishCourseInfo);
    }

    //课程最终发布修改状态
    @PostMapping("/publishCourse/{courseId}")
    public R publishCourse(@PathVariable String courseId) {
        EduCourse eduCourse = new EduCourse();
        eduCourse.setId(courseId);
        eduCourse.setStatus("Normal");
        eduCourseService.updateById(eduCourse);
        return R.ok();
    }


    @ApiOperation(value = "条件分页课程列表")
    @PostMapping("/pageCondition/{page}/{limit}")
    public R pageQuery(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(name = "teacherQuery", value = "查询对象", required = false)
            @RequestBody(required = false) CourseQuery courseQuery) {

        Page<EduCourse> pageParam = new Page<>(page, limit);
        eduCourseService.pageQuery(pageParam, courseQuery);
        Long total = pageParam.getTotal();
        List<EduCourse> records = pageParam.getRecords();
        return R.ok().data("total", total).data("records", records);
    }

    //根据id删除课程（包括课程的章节小节）
    @DeleteMapping("/deleteCourse/{courseId}")
    public R deleteCourse(@PathVariable String courseId) {
        eduCourseService.deleteCourse(courseId);
        return R.ok();
    }

    //根据课程id获取订单需要的课程信息
    @PostMapping("/getCourseInfoForOrder/{courseId}")
    public CourseOrderVo getCourseInfoForOrder(@PathVariable("courseId") String courseId) {
        CourseWebVo courseWebVo = eduCourseService.getCourseFrontInfo(courseId);
        CourseOrderVo courseOrderVo = new CourseOrderVo();
        BeanUtils.copyProperties(courseWebVo, courseOrderVo);
        return courseOrderVo;
    }
}

