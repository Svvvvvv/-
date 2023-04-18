package com.atguigu.eduorder.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.commonutils.vo.CourseOrderVo;
import com.atguigu.eduorder.entity.Order;
import com.atguigu.eduorder.service.OrderService;
import com.atguigu.serviceBase.ExceptionHandler.SvvvvvException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2022-09-15
 */
@RestController
@RequestMapping("/eduorder/order")

public class OrderController {
    @Autowired
    private OrderService orderService;


    //根据课程id 和token获取的用户id 创建订单
    @PostMapping("/createOrder/{courseId}")
    public R createOrder(@PathVariable("courseId") String courseId,
                         HttpServletRequest request){
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        if (StringUtils.isEmpty(memberId)) {
            throw new SvvvvvException(20001,"用户未登录");
        }

        String orderNo = orderService.createOrder(courseId, memberId);
        return R.ok().data("orderId",orderNo);
    }


    //根据订单号查询订单
    @GetMapping("/getOrder/{orderId}")
    public R getOrder(@PathVariable String orderId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderId);
        Order order = orderService.getOne(wrapper);
        return R.ok().data("item",order);
    }

    //根据【用户id、课程id】查询订单表中的状态
    @GetMapping("/isBuyCourse/{memberId}/{courseId}")
    public boolean isBuyCourse(@PathVariable("memberId") String memberId, @PathVariable("courseId") String courseId){
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        wrapper.eq("member_id",memberId);
        wrapper.eq("status",1);

        int count = orderService.count(wrapper);
        return count > 0;
    }

    // 获取最近购买的五条历史课程id记录
    @GetMapping("/getPayOrderFive/{memberId}")
    public List<String> getPayOrderFive(@PathVariable("memberId") String memberId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",memberId);
        wrapper.eq("status",1);
        wrapper.orderByDesc("gmt_modified");
        wrapper.last("limit 5");
        List<Order> orders = orderService.list(wrapper);
        List<String> courseIds = null;
        if (orders != null && orders.size() > 0) {
            courseIds = orders.stream().map(Order::getCourseId).collect(Collectors.toList());
        }
        return courseIds;
    }
}

