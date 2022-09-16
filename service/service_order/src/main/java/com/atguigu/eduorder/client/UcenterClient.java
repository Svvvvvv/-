package com.atguigu.eduorder.client;

import com.atguigu.commonutils.vo.UcenterMemberOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "service-ucenter")
public interface UcenterClient {

    @PostMapping("/eduUcenter/ucenter-member/getUserInfoForOrder/{id}")
    UcenterMemberOrderVo getUserInfoForOrder(@PathVariable("id")String id);
}
