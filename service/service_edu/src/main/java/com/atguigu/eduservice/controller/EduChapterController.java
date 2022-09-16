package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;

import com.atguigu.eduservice.client.VideoClient;
import com.atguigu.eduservice.entity.EduChapter;
import com.atguigu.eduservice.entity.vo.ChapterVo;
import com.atguigu.eduservice.service.EduChapterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2022-09-10
 */
@Api(tags = "课程chapter")
@RestController
@RequestMapping("/eduservice/chapter")
@Transactional
public class EduChapterController {
    @Autowired
    private EduChapterService chapterService;


    @ApiOperation("根据ID查询章节小节")
    @GetMapping(value = "/getChapterVideo/{courseId}")
    public R getChapterVideoById(@PathVariable String courseId){
        List<ChapterVo> chapterVoList = chapterService.getChapterVideoById(courseId);
        return R.ok().data("list", chapterVoList);
    }

    //根据id查询章节
    @GetMapping(value = "/getChapter/{chapterId}")
    public R getChapterById(@PathVariable String chapterId){
        EduChapter chapter = chapterService.getById(chapterId);
        return R.ok().data("chapter", chapter);
    }

    //添加章节
    @PostMapping("/addChapter")
    public R addChapter(@RequestBody EduChapter eduChapter){
        boolean save = chapterService.save(eduChapter);
        return save ? R.ok() : R.error();
    }

    //修改章节
    @PostMapping("/updateChapter")
    public R updateChapter(@RequestBody EduChapter eduChapter){
        boolean b = chapterService.updateById(eduChapter);
        return b ? R.ok() : R.error();
    }

    //删除章节
    @DeleteMapping("/deleteChapter/{chapterId}")
    public R deleteChapterById(@PathVariable  String chapterId){
        chapterService.removeChapter(chapterId);
        return R.ok();
    }

}

