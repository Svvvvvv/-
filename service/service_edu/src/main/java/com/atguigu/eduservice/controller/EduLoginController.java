package com.atguigu.eduservice.controller;

import com.atguigu.commonutils.R;
import io.swagger.annotations.Api;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eduservice/user")
@Api(tags = "登录管理")
@Transactional
public class EduLoginController {

    @PostMapping("/login")
    public R login(){
        return R.ok().data("token","helen");
    }

    @GetMapping("/info")
    public R getInfo(){
        return R.ok().data("roles","[admin]").data("name","helen").data("avatar","https://svvvvv-images-1311482589.cos.ap-chengdu.myqcloud.com/common/111.jpg");
    }

}
