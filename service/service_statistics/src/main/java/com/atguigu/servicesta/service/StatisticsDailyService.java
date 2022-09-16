package com.atguigu.servicesta.service;

import com.atguigu.servicesta.entity.StatisticsDaily;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-16
 */
public interface StatisticsDailyService extends IService<StatisticsDaily> {
    //创建统计信息
    void createStatisticsByDay(String day);
    //图表显示，返回两部分数据，日期json数组，数量json数组
    Map<String,Object> getShowData(String type, String begin, String end);
}
