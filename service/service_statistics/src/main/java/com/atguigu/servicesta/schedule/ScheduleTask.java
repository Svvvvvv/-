package com.atguigu.servicesta.schedule;

import com.atguigu.servicesta.service.StatisticsDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ScheduleTask {

    @Autowired
    private StatisticsDailyService statisticsDailyService;
    //每天零点生成前一天的统计信息
    @Scheduled(cron = "0 0 0 * * ? " )
    public void  task1(){
        statisticsDailyService.createStatisticsByDay(LocalDate.now().plusDays(-1).toString());
    }
}
