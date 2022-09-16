package com.atguigu.eduorder.service.impl;

import com.atguigu.commonutils.vo.CourseOrderVo;
import com.atguigu.commonutils.vo.UcenterMemberOrderVo;
import com.atguigu.eduorder.client.EduClient;
import com.atguigu.eduorder.client.UcenterClient;
import com.atguigu.eduorder.entity.Order;
import com.atguigu.eduorder.mapper.OrderMapper;
import com.atguigu.eduorder.service.OrderService;
import com.atguigu.eduorder.utils.OrderNoUtil;
import com.atguigu.serviceBase.ExceptionHandler.GuliException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-15
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private EduClient eduClient;

    @Autowired
    private UcenterClient ucenterClient;


    //根据课程id，用户id创建订单
    @Override
    public String createOrder(String courseId, String memberId) {
        //远程调用edu获取课程信息
        CourseOrderVo courseInfoForOrder = eduClient.getCourseInfoForOrder(courseId);

        //远程调用ucnnter获取用户信息
        UcenterMemberOrderVo userInfoForOrder = ucenterClient.getUserInfoForOrder(memberId);

        Order order = new Order();
        order.setOrderNo(OrderNoUtil.getOrderNo());
        order.setCourseId(courseId);
        order.setCourseTitle(courseInfoForOrder.getTitle());
        order.setCourseCover(courseInfoForOrder.getCover());
        order.setTeacherName(courseInfoForOrder.getTeacherName());
        order.setTotalFee(courseInfoForOrder.getPrice());
        order.setMemberId(memberId);
        order.setMobile(userInfoForOrder.getMobile());
        order.setNickname(userInfoForOrder.getNickname());
        order.setStatus(0);
        order.setPayType(1);
        int insert = baseMapper.insert(order);
        if (insert == 0) {
            throw new GuliException(20001,"创建订单失败");
        }

        return order.getOrderNo();
    }
}
