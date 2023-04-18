package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.entity.EduChapter;
import com.atguigu.eduservice.entity.Label;
import com.atguigu.eduservice.entity.LabelCourse;
import com.atguigu.eduservice.mapper.LabelMapper;
import com.atguigu.eduservice.service.LabelCourseService;
import com.atguigu.eduservice.service.LabelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Svvvvv
 * @since 2023-04-18
 */
@Service
public class LabelServiceImpl extends ServiceImpl<LabelMapper, Label> implements LabelService {
    @Autowired
    private LabelCourseService labelCourseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByCourseId(String courseId) {
        // 得到关系记录
        QueryWrapper<LabelCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        List<LabelCourse> labels = labelCourseService.list(wrapper);
        if (labels != null && !labels.isEmpty()) {
            // 删除关系记录
            labelCourseService.remove(wrapper);
            List<String> labelIds = labels.stream().map(LabelCourse::getLabelId).collect(Collectors.toList());
            // 删除标签
            baseMapper.deleteBatchIds(labelIds);
        }
    }
}
