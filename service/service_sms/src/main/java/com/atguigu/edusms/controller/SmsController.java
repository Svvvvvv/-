package com.atguigu.edusms.controller;

import com.atguigu.commonutils.R;
import com.atguigu.edusms.service.SmsService;
import com.atguigu.edusms.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/edusms/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @GetMapping("/send/{phone}")
    public R  sendSms(@PathVariable("phone") String phone){
        // 先看redis中尝试获取有无验证码
        String code = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)){
            return R.ok();
        }

        // 如果redis中没有手机号对应验证码，生成
        String fourBitRandom = RandomUtil.getFourBitRandom();
        boolean send = smsService.send(fourBitRandom,phone);
        if (send){
            //生成成功，放入redis中  设置ttl5分钟
            redisTemplate.opsForValue().set(phone,fourBitRandom,5, TimeUnit.MINUTES);
            return R.ok();
        }else {
            return R.error().message("发送短信失败");
        }
    }

}
