package com.atguigu.eduservice.controller.front;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.eduservice.client.OrderClient;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.frontVo.CourseFrontVo;
import com.atguigu.eduservice.entity.frontVo.CourseWebVo;
import com.atguigu.eduservice.entity.vo.ChapterVo;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(tags = "前台课程")
@RestController
@RequestMapping("/eduservice/courseFront")
public class CourseFrontController {

    @Autowired
    private EduCourseService eduCourseService;
    
    @Autowired
    private EduChapterService chapterService;


    @Resource
    private OrderClient orderClient;

    //条件分页查询前端课程信息
    @PostMapping("/getFrontCourseList/{page}/{limit}")
    public R getCourseFront(@PathVariable Long page, @PathVariable Long limit,
                            @RequestBody(required = false)CourseFrontVo courseQuery){
        Page<EduCourse> pageCourse = new Page<>(page,limit);
        Map<String, Object> pageCourseFront = eduCourseService.pageCourseFront(pageCourse, courseQuery);
        return R.ok().data(pageCourseFront);
    }


    //根据courseId查询前端课程详情页
    @GetMapping("/getFrontCourseInfo/{courseId}")
    public R getFrontCourseInfo(@PathVariable("courseId") String courseId, HttpServletRequest request){
        CourseWebVo courseWebVo = eduCourseService.getCourseFrontInfo(courseId);

        List<ChapterVo> chapterVoList = chapterService.getChapterVideoById(courseId);

        //默认没购买
        boolean isBuyCourse = false;

        //查询是否购买
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        if (!StringUtils.isEmpty(memberId)) {
            isBuyCourse = orderClient.isBuyCourse(memberId,courseId);
        }


        return R.ok().data("course", courseWebVo).data("chapterVoList",chapterVoList).data("isBuy", isBuyCourse);
    }
}
