package com.atguigu.eduservice.client;

import com.atguigu.eduservice.client.impl.OrderClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "service-order",fallback = OrderClientImpl.class)
public interface OrderClient {
    //根据【用户id、课程id】查询是否购买课程
    @GetMapping("/eduorder/order/isBuyCourse/{memberId}/{courseId}")
    boolean isBuyCourse(@PathVariable("memberId") String memberId, @PathVariable("courseId") String courseId);

    //根据用户id获取最近购买的五条课程id记录
    @GetMapping("/eduorder/order/getPayOrderFive/{memberId}")
    List<String> getPayOrderFive(@PathVariable("memberId") String memberId);
}
