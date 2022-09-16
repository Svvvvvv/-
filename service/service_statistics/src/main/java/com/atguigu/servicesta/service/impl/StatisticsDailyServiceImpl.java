package com.atguigu.servicesta.service.impl;

import com.atguigu.commonutils.R;
import com.atguigu.servicesta.client.UcenterClient;
import com.atguigu.servicesta.entity.StatisticsDaily;
import com.atguigu.servicesta.mapper.StatisticsDailyMapper;
import com.atguigu.servicesta.service.StatisticsDailyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-16
 */
@Service
public class StatisticsDailyServiceImpl extends ServiceImpl<StatisticsDailyMapper, StatisticsDaily> implements StatisticsDailyService {

    @Resource
    private UcenterClient ucenterClient;

    //创建统计信息
    @Override
    public void createStatisticsByDay(String day) {
        //先删除原有统计信息
        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.eq("date_calculated", day);
        baseMapper.delete(wrapper);

        //调用Ucenter服务获取信息
        R registerR = ucenterClient.countRegister(day);
        Integer countRegister = (Integer) registerR.getData().get("countRegister");

        //把获取到的数据封装对象
        StatisticsDaily daily = new StatisticsDaily();
        daily.setRegisterNum(countRegister);//注册人数
        daily.setCourseNum(RandomUtils.nextInt(100, 200));
        daily.setLoginNum(RandomUtils.nextInt(200, 300));//登录数
        daily.setVideoViewNum(RandomUtils.nextInt(200, 300));//视频流量数
        daily.setDateCalculated(day);//统计日期

        //添加信息
        baseMapper.insert(daily);
    }


    //图表显示，返回两部分数据，日期json数组，数量json数组
    @Override
    public Map<String, Object> getShowData(String type, String begin, String end) {
        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.between("date_calculated", begin, end);
        wrapper.select("date_calculated", type);

        ArrayList<String> xlist = new ArrayList<>();
        ArrayList<Integer> ylist = new ArrayList<>();

        for (StatisticsDaily daily : baseMapper.selectList(wrapper)) {
            xlist.add(daily.getDateCalculated());
            switch (type) {
                case "register_num":
                    ylist.add(daily.getRegisterNum());
                    break;
                case "login_num":
                    ylist.add(daily.getLoginNum());
                    break;
                case "video_view_num":
                    ylist.add(daily.getVideoViewNum());
                    break;
                case "course_num":
                    ylist.add(daily.getCourseNum());
                    break;
                default:
                    ylist.add(daily.getRegisterNum());
            }
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("xlist", xlist);
        map.put("ylist", ylist);
        return map;
    }
}
