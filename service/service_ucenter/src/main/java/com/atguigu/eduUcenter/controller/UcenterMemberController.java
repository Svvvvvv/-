package com.atguigu.eduUcenter.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.commonutils.vo.UcenterMemberOrderVo;
import com.atguigu.eduUcenter.entity.UcenterMember;
import com.atguigu.eduUcenter.entity.vo.LoginVo;
import com.atguigu.eduUcenter.entity.vo.RegisterVo;
import com.atguigu.eduUcenter.service.UcenterMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2022-09-14
 */
@RestController
@RequestMapping("/eduUcenter/ucenter-member")
public class UcenterMemberController {

    @Autowired
    private UcenterMemberService ucenterMemberService;

    //用户登录
    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginVo){
        String token = ucenterMemberService.login(loginVo);
        return R.ok().data("token", token);
    }

    //注册用户
    @PostMapping("/register")
    public R register(@RequestBody RegisterVo registerVo){
        try {
            ucenterMemberService.registr(registerVo);
        } catch (Exception e) {
            return R.error();
        }
        return R.ok();
    }
    //根据token获取用户信息
    @GetMapping("/getUserInfoForToken")
    public R getUserInfoForToken(HttpServletRequest request){
        //使用工具类JwtUtils获取 id
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        UcenterMember member = ucenterMemberService.getById(memberId);
        return  R.ok().data("userInfo",member);
    }

    //根据id获取订单需要的用户信息
    @PostMapping("/getUserInfoForOrder/{id}")
    public UcenterMemberOrderVo getUserInfoForOrder(@PathVariable("id")String id) {
        UcenterMember member = ucenterMemberService.getById(id);
        UcenterMemberOrderVo memberOrderVo = new UcenterMemberOrderVo();
        BeanUtils.copyProperties(member, memberOrderVo);
        return memberOrderVo;
    }


    //统计当天注册人数
    @PostMapping("/countRegister/{day}")
    public R countRegister(@PathVariable("day") String day){
        Integer countRegister = ucenterMemberService.countRegister(day);
        return R.ok().data("countRegister", countRegister);
    }

}

