package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.entity.vo.TeacherQuery;
import com.atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2022-09-07
 */
@Api(tags = "讲师管理")
@RestController
@RequestMapping("/eduservice/teacher")
@Transactional
public class EduTeacherController {
    @Autowired
    private EduTeacherService teacherService;

    @ApiOperation("所有讲师列表")
    @GetMapping("/findAll")
    public R findAllTeachers() {
        List<EduTeacher> list = teacherService.list(null);
        return R.ok().data("items", list);
    }

    @ApiOperation("根据ID删除讲师")
    @DeleteMapping("/{id}")
    public R removeById(@PathVariable String id) {
        boolean flag = teacherService.removeById(id);
        return flag == true ? R.ok() : R.error();
    }

    @ApiOperation(value = "分页讲师列表")
    @GetMapping("/pageList/{page}/{limit}")
    public R pageList(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<EduTeacher> pageParam = new Page<>(page, limit);
        teacherService.page(pageParam, null);
        Long total = pageParam.getTotal();
        List<EduTeacher> records = pageParam.getRecords();
        return R.ok().data("total", total).data("records", records);
    }

    @ApiOperation(value = "条件分页讲师列表")
    @PostMapping("/pageCondition/{page}/{limit}")
    public R pageQuery(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(name = "teacherQuery", value = "查询对象", required = false)
            @RequestBody(required = false) TeacherQuery teacherQuery) {

        Page<EduTeacher> pageParam = new Page<>(page, limit);
        teacherService.pageQuery(pageParam, teacherQuery);
        Long total = pageParam.getTotal();
        List<EduTeacher> records = pageParam.getRecords();
        return R.ok().data("total", total).data("records", records);
    }


    @ApiOperation(value = "添加讲师")
    @PostMapping("/addTeacher")
    public R addTeacher(@RequestBody EduTeacher teacher) {
        boolean save = teacherService.save(teacher);
        return save == true ? R.ok() : R.error();
    }


    @ApiOperation(value = "根据ID得到讲师")
    @GetMapping("/{id}")
    public R getTeacherById(@PathVariable String id) {
        EduTeacher teacher = teacherService.getById(id);
        return teacher != null ? R.ok().data("teacher", teacher) : R.error();
    }

    @ApiOperation(value = "根据id修改讲师")
    @PostMapping("/updateTeacher")
    public R updateTeacher(@RequestBody EduTeacher teacher) {
        boolean res = teacherService.updateById(teacher);
        return res == true ? R.ok() : R.error();
    }

}

