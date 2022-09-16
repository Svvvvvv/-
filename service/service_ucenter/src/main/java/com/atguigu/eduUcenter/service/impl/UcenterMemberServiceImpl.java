package com.atguigu.eduUcenter.service.impl;

import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.MD5;
import com.atguigu.eduUcenter.entity.UcenterMember;
import com.atguigu.eduUcenter.entity.vo.LoginVo;
import com.atguigu.eduUcenter.entity.vo.RegisterVo;
import com.atguigu.eduUcenter.mapper.UcenterMemberMapper;
import com.atguigu.eduUcenter.service.UcenterMemberService;
import com.atguigu.serviceBase.ExceptionHandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-14
 */
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public UcenterMember getByOpenid(String openid) {
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openid);
        UcenterMember ucenterMember = baseMapper.selectOne(wrapper);
        return ucenterMember;
    }

    //统计当天注册人数
    @Override
    public Integer countRegister(String day) {
        return baseMapper.countRegister(day);
    }

    @Override
    public String login(LoginVo loginVo) {
        String phone = loginVo.getMobile();
        String password = loginVo.getPassword();
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) {
            throw new GuliException(20001, "手机号或密码为空");
        }
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", phone);
        UcenterMember ucenterMember = baseMapper.selectOne(wrapper);

        if (ucenterMember == null) {
            throw new GuliException(20001, "不存在该用户");
        }

        //将获取到的密码经过MD5加密与数据库比较
        if (StringUtils.isEmpty(ucenterMember.getPassword())
                || !MD5.encrypt(password).equals(ucenterMember.getPassword())) {
            throw new GuliException(20001, "密码错误");
        }

        if (ucenterMember.getIsDisabled()){
            throw new GuliException(20001,"用户被禁用");
        }

        String token = JwtUtils.getJwtToken(ucenterMember.getId(), ucenterMember.getNickname());
        return token;
    }

    @Override
    public void registr(RegisterVo registerVo) {
        String code = registerVo.getCode();
        String mobile = registerVo.getMobile();
        String nickname = registerVo.getNickname();
        String password = registerVo.getPassword();

        if (StringUtils.isEmpty(code)
                || StringUtils.isEmpty(mobile)
                || StringUtils.isEmpty(nickname)
                || StringUtils.isEmpty(password)){
            throw new GuliException(20001,"输入信息不完全");
        }
        String redisCode = redisTemplate.opsForValue().get(mobile);
        if (!code.equals(redisCode)){
            throw new GuliException(20001,"验证码不正确");
        }

        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0 ){
            throw new GuliException(20001,"该手机号已被注册");
        }

        UcenterMember ucenterMember = new UcenterMember();
        ucenterMember.setAvatar("https://edu-guli-sxk.oss-cn-hangzhou.aliyuncs.com/default.jpg");
        ucenterMember.setMobile(mobile);
        ucenterMember.setNickname(nickname);
        //密码MD5加密
        ucenterMember.setPassword(MD5.encrypt(password));
        int insert = baseMapper.insert(ucenterMember);
        if (!(insert > 0)) {
            throw new GuliException(20001,"注册失败");
        }
    }
}
