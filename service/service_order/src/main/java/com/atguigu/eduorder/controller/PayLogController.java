package com.atguigu.eduorder.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduorder.service.PayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2022-09-15
 */
@RestController
@RequestMapping("/eduorder/paylog")

public class PayLogController {

    @Autowired
    private PayLogService payLogService;

    //生成二维码
    @GetMapping("/createNatvie/{orderNo}")
    public R createNatvie(@PathVariable String orderNo){
        Map<String, Object> map = payLogService.createNative(orderNo);
        return R.ok().data(map);
    }


    //根据订单号查询订单支付状态，如果已支付更改订单状态
    @GetMapping("/queryPayStatus/{orderNo}")
    public R queryPayStatus(@PathVariable String orderNo) {
        Map<String, String> map = payLogService.queryPayStatus(orderNo);
        if (map == null) {
            return R.error().message("支付失败");
        }
        //如果第三方数据支付状态是成功,更改订单状态
        if ("SUCCESS".equals(map.get("trade_state"))) {
            payLogService.updateOrderStatus(map);
            return R.ok();
        }
        //25000配合前端拦截器
        return R.ok().code(25000).message("支付中");
    }
}

