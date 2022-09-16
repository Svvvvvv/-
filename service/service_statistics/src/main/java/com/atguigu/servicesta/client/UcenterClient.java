package com.atguigu.servicesta.client;

import com.atguigu.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("service-ucenter")
public interface UcenterClient {

    //统计当天注册人数
    @PostMapping("/eduUcenter/ucenter-member/countRegister/{day}")
    public R countRegister(@PathVariable("day") String day);
}
