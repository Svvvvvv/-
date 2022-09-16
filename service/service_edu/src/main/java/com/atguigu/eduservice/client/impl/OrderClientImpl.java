package com.atguigu.eduservice.client.impl;

import com.atguigu.eduservice.client.OrderClient;
import com.atguigu.serviceBase.ExceptionHandler.GuliException;
import org.springframework.stereotype.Component;


@Component
public class OrderClientImpl implements OrderClient {
    @Override
    public boolean isBuyCourse(String memberId, String courseId) {
        throw new GuliException(20001,"获取用户是否购买信息失败");
    }
}
