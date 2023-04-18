package com.atguigu.eduservice.client.impl;

import com.atguigu.eduservice.client.OrderClient;
import com.atguigu.serviceBase.ExceptionHandler.SvvvvvException;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class OrderClientImpl implements OrderClient {
    @Override
    public boolean isBuyCourse(String memberId, String courseId) {
        throw new SvvvvvException(20001,"获取用户是否购买信息失败");
    }

    @Override
    public List<String> getPayOrderFive(String memberId) {
        throw new SvvvvvException(20001,"获取最近五条购买记录失败");
    }
}
