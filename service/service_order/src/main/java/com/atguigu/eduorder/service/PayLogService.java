package com.atguigu.eduorder.service;

import com.atguigu.eduorder.entity.PayLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 支付日志表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-15
 */
public interface PayLogService extends IService<PayLog> {

    //根据订单号生成二维码
    Map<String,Object> createNative(String orderNo);

    //根据订单号，查询支付的状态
    Map<String, String> queryPayStatus(String orderNo);

    //向支付表中添加支付记录，并更新订单表的订单状态
    void updateOrderStatus(Map<String, String> map);

}
