package com.atguigu.eduservice.client;

import com.atguigu.eduservice.client.impl.OrderClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-order",fallback = OrderClientImpl.class)
public interface OrderClient {

    //根据【用户id、课程id】查询订单表中的状态
    @GetMapping("/eduorder/order/isBuyCourse/{memberId}/{courseId}")
    boolean isBuyCourse(@PathVariable("memberId") String memberId, @PathVariable("courseId") String courseId);
}
