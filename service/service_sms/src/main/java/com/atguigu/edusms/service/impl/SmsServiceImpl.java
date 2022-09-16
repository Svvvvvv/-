package com.atguigu.edusms.service.impl;

import com.atguigu.edusms.service.SmsService;
import com.atguigu.edusms.utils.SmsUtil;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public boolean send(String fourBitRandom,String phone) {
        if (phone == null) {
            return false;
        }
        return SmsUtil.senMessage(fourBitRandom, phone);
    }
}
