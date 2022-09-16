package com.atguigu.eduorder.client;

import com.atguigu.commonutils.vo.CourseOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "service-edu")
public interface EduClient {
    //根据课程id获取订单需要的课程信息
    @PostMapping("/eduservice/course/getCourseInfoForOrder/{courseId}")
    CourseOrderVo getCourseInfoForOrder(@PathVariable("courseId") String courseId);
}
