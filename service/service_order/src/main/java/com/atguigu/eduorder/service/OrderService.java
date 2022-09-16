package com.atguigu.eduorder.service;

import com.atguigu.eduorder.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-15
 */
public interface OrderService extends IService<Order> {
    //根据课程id，用户id创建订单
    String createOrder(String courseId, String memberId);
}
