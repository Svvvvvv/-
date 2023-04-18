package com.atguigu.eduservice.controller.front;

import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "前台讲师")
@RestController
@RequestMapping("/eduservice/teacherFront")
public class TeacherFrontController {

    @Autowired
    private EduTeacherService teacherService;
    @Autowired
    private EduCourseService courseService;

    //分页查询讲师前台信息
    @PostMapping("/getTeacherPageList/{page}/{limit}")
    public R getTeacherPageList(@PathVariable("page") Long page,@PathVariable("limit") Long limit){
        Page<EduTeacher> teacherPage = new Page<EduTeacher>(page, limit);
        Map<String, Object> map = teacherService.getTeacherPageList(teacherPage);
        return R.ok().data(map);
    }

    //查询对应id讲师信息和课程
    @GetMapping("/getTeacherInfo/{id}")
    public R getTeacherInfo(@PathVariable("id") String id){
        EduTeacher teacher = teacherService.getById(id);
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id",id);
        List<EduCourse> courseList = courseService.list(wrapper);
        return R.ok().data("teacher",teacher).data("courseList",courseList);
    }
}
