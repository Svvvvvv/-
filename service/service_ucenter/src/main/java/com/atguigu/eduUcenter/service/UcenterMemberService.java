package com.atguigu.eduUcenter.service;

import com.atguigu.commonutils.R;
import com.atguigu.eduUcenter.entity.UcenterMember;
import com.atguigu.eduUcenter.entity.vo.LoginVo;
import com.atguigu.eduUcenter.entity.vo.RegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-14
 */
public interface UcenterMemberService extends IService<UcenterMember> {

    String login(LoginVo loginVo);

    void registr(RegisterVo registerVo);

    UcenterMember getByOpenid(String openid);

    //统计当天注册人数
    Integer countRegister(String day);
}
