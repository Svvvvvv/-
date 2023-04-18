package com.atguigu.eduservice.service.impl;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.eduservice.client.OrderClient;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduCourseDescription;
import com.atguigu.eduservice.entity.Label;
import com.atguigu.eduservice.entity.LabelCourse;
import com.atguigu.eduservice.entity.frontVo.CourseFrontVo;
import com.atguigu.eduservice.entity.frontVo.CourseWebVo;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQuery;
import com.atguigu.eduservice.mapper.EduCourseMapper;
import com.atguigu.eduservice.service.*;
import com.atguigu.serviceBase.ExceptionHandler.SvvvvvException;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-10
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    private EduCourseDescriptionService descriptionService;
    @Autowired
    private EduChapterService chapterService;
    @Autowired
    private EduVideoService videoService;
    @Autowired
    private LabelService labelService;
    @Autowired
    private LabelCourseService labelCourseService;
    @Resource
    private OrderClient orderClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveCourse(CourseInfoVo courseInfoVo) {
        if (courseInfoVo == null) {
            throw new SvvvvvException(20001, "课程信息为空");
        }
        //添加课程表信息
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo, eduCourse);
        int insert = baseMapper.insert(eduCourse);
        if (insert == 0) {
            throw new SvvvvvException(20001, "添加课程信息失败");
        }

        //添加课程简介表信息
        String id = eduCourse.getId();
        EduCourseDescription description = new EduCourseDescription();
        description.setId(id);
        description.setDescription(courseInfoVo.getDescription());
        boolean save = descriptionService.save(description);
        if (!save) {
            throw new SvvvvvException(20001, "添加课程简介失败");
        }

        // 添加标签
        List<Label> labels = new ArrayList<>();
        if (courseInfoVo.getLabel1() != null && !courseInfoVo.getLabel1().trim().equals("")) {
            Label label = new Label();
            label.setName(courseInfoVo.getLabel1());
            labels.add(label);
        }

        if (courseInfoVo.getLabel2() != null && !courseInfoVo.getLabel2().trim().equals("")) {
            Label label = new Label();
            label.setName(courseInfoVo.getLabel2());
            labels.add(label);
        }

        if (courseInfoVo.getLabel3() != null && !courseInfoVo.getLabel3().trim().equals("")) {
            Label label = new Label();
            label.setName(courseInfoVo.getLabel3());
            labels.add(label);
        }
        if (!labels.isEmpty()) {
            boolean saveLabels = labelService.saveBatch(labels);
            if (!saveLabels) {
                throw new SvvvvvException(20001, "添加课程标签失败");
            }
            List<LabelCourse> labelCourses = new ArrayList<>();
            for (Label label : labels) {
                LabelCourse labelCourse = new LabelCourse();
                labelCourse.setCourseId(eduCourse.getId());
                labelCourse.setLabelId(label.getId());
                labelCourses.add(labelCourse);
            }
            boolean saveLabelCourses = labelCourseService.saveBatch(labelCourses);
            if (!saveLabelCourses) {
                throw new SvvvvvException(20001, "添加课程标签关系失败");
            }
        }

        return eduCourse.getId();
    }

    @Override
    public CourseInfoVo getCourseInfo(String courseId) {
        //查询课程表
        EduCourse eduCourse = baseMapper.selectById(courseId);
        CourseInfoVo courseInfoVo = new CourseInfoVo();
        BeanUtils.copyProperties(eduCourse, courseInfoVo);
        //查询简介表
        EduCourseDescription courseDescription = descriptionService.getById(courseId);
        if (courseDescription == null) {
            courseInfoVo.setDescription("");
            return courseInfoVo;
        }
        courseInfoVo.setDescription(courseDescription.getDescription());

        // 查询标签
        QueryWrapper<LabelCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        wrapper.orderByDesc("gmt_create");
        List<LabelCourse> labelCourses = labelCourseService.list(wrapper);
        if (labelCourses != null && !labelCourses.isEmpty()) {
            int size = labelCourses.size();
            while (size > 0) {
                switch (size) {
                    case 3:
                        Label label3 = labelService.getById(labelCourses.get(size-1).getLabelId());
                        courseInfoVo.setLabel3(label3.getName());
                        size--;
                        break;
                    case 2:
                        Label label2 = labelService.getById(labelCourses.get(size-1).getLabelId());
                        courseInfoVo.setLabel2(label2.getName());
                        size--;
                        break;
                    case 1:
                        Label label1 = labelService.getById(labelCourses.get(size-1).getLabelId());
                        courseInfoVo.setLabel1(label1.getName());
                        size--;
                        break;
                }
            }
        }

        return courseInfoVo;
    }

    @Override
    public void updateCourse(CourseInfoVo courseInfoVo) {
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo, eduCourse);
        //修改课程表
        int i = baseMapper.updateById(eduCourse);
        if (i == 0) {
            throw new SvvvvvException(20001, "修改课程信息失败(课程表)");
        }
        //修改借鉴表
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        eduCourseDescription.setId(courseInfoVo.getId());
        eduCourseDescription.setDescription(courseInfoVo.getDescription());
        boolean update = descriptionService.updateById(eduCourseDescription);
        if (!update) {
            throw new SvvvvvException(20001, "修改课程信息失败(简介表)");
        }

        //删除标签
        labelService.removeByCourseId(eduCourse.getId());

        // 添加标签
        List<Label> labels = new ArrayList<>();
        if (courseInfoVo.getLabel1() != null && !courseInfoVo.getLabel1().trim().equals("")) {
            Label label = new Label();
            label.setName(courseInfoVo.getLabel1());
            labels.add(label);
        }

        if (courseInfoVo.getLabel2() != null && !courseInfoVo.getLabel2().trim().equals("")) {
            Label label = new Label();
            label.setName(courseInfoVo.getLabel2());
            labels.add(label);
        }

        if (courseInfoVo.getLabel3() != null && !courseInfoVo.getLabel3().trim().equals("")) {
            Label label = new Label();
            label.setName(courseInfoVo.getLabel3());
            labels.add(label);
        }
        if (!labels.isEmpty()) {
            boolean saveLabels = labelService.saveBatch(labels);
            if (!saveLabels) {
                throw new SvvvvvException(20001, "添加课程标签失败");
            }
            List<LabelCourse> labelCourses = new ArrayList<>();
            for (Label label : labels) {
                LabelCourse labelCourse = new LabelCourse();
                labelCourse.setCourseId(eduCourse.getId());
                labelCourse.setLabelId(label.getId());
                labelCourses.add(labelCourse);
            }
            boolean saveLabelCourses = labelCourseService.saveBatch(labelCourses);
            if (!saveLabelCourses) {
                throw new SvvvvvException(20001, "添加课程标签关系失败");
            }
        }

    }

    @Override
    public CoursePublishVo getPublishCourseInfo(String id) {
        CoursePublishVo publishCourseInfo = baseMapper.getPublishCourseInfo(id);
        return publishCourseInfo;
    }

    @Override
    public void pageQuery(Page<EduCourse> pageParam, CourseQuery courseQuery) {
        if (courseQuery == null) {
            baseMapper.selectPage(pageParam, null);
            return;
        }

        String title = courseQuery.getTitle();
        String status = courseQuery.getStatus();
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.like(!StringUtils.isEmpty(title),"title",title)
                .eq(!StringUtils.isEmpty(status),"status",status)
                .orderByDesc("gmt_modified");
        baseMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void deleteCourse(String courseId) {
        //删除video表
        videoService.removeVideoByCourseId(courseId);

        //删除chapter表
        chapterService.removeChapterByCourseId(courseId);

        //删除简介表
        descriptionService.removeById(courseId);

        //删除标签
        labelService.removeByCourseId(courseId);

        //删除课程表
        baseMapper.deleteById(courseId);

    }

    @Override
    public CourseWebVo getCourseFrontInfo(String courseId) {
        CourseWebVo courseWebVo = baseMapper.getCourseFrontInfo(courseId);
        return courseWebVo;
    }

    @Override
    public Map<String, Object> pageCourseFront(Page<EduCourse> pageCourse, CourseFrontVo courseQuery) {
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("status","Normal");
        wrapper.like(!StringUtils.isEmpty(courseQuery.getTitle()),"title", courseQuery.getTitle());
        wrapper.eq(!StringUtils.isEmpty(courseQuery.getSubjectParentId()), "subject_parent_id",courseQuery.getSubjectParentId());
        wrapper.eq(!StringUtils.isEmpty(courseQuery.getSubjectId()), "subject_id",courseQuery.getSubjectId());
        wrapper.orderByDesc(!StringUtils.isEmpty(courseQuery.getBuyCountSort()),"buy_count");
        wrapper.orderByDesc(!StringUtils.isEmpty(courseQuery.getGmtCreateSort()),"gmt_create");
        wrapper.orderByDesc(!StringUtils.isEmpty(courseQuery.getPriceSort()),"price");

        baseMapper.selectPage(pageCourse, wrapper);

        List<EduCourse> records = pageCourse.getRecords();
        long current = pageCourse.getCurrent();
        long pages = pageCourse.getPages();
        long size = pageCourse.getSize();
        long total = pageCourse.getTotal();
        boolean hasNext = pageCourse.hasNext();
        boolean hasPrevious = pageCourse.hasPrevious();

        HashMap<String, Object> map = new HashMap<>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return map;
    }

    @Override
    public List<EduCourse> getIndexCourse(HttpServletRequest request) {
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        List<EduCourse> courseList = new ArrayList<>();
        // 已经登陆
        if (!StringUtils.isEmpty(memberId)) {
            // 获取最近五条购买记录
            List<String> payOrderFive = orderClient.getPayOrderFive(memberId);
            if (payOrderFive != null && !payOrderFive.isEmpty()) {
                Queue<CourseToSimilarity> heap = new PriorityQueue<>((o1, o2) -> {
                    if (o1.similarity - o2.similarity == 0) return 0;
                    return o1.similarity - o2.similarity > 0.0 ? 1 : -1;
                });

                QueryWrapper<EduCourse> payWrapper = new QueryWrapper<>();
                payWrapper.in("id", payOrderFive);
                List<EduCourse> payCourses = baseMapper.selectList(payWrapper);
                double[] weightList = new double[]{0.5, 0.3, 0.2};
                double[][] sim = new double[3][2];
                // 根据时间排序获取发布的课程
                QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
                wrapper.eq("status", "Normal");
                wrapper.orderByDesc("gmt_create");
                List<EduCourse> eduCourses = baseMapper.selectList(wrapper);
                for (EduCourse eduCourse : eduCourses) {
                    double similaritySingle = 0.0;
                    //两个课程的相似度计算
                    for (EduCourse payCours : payCourses) {
                        // 获取课程的标签
                        QueryWrapper<LabelCourse> eduCourseLabelCourseQueryWrapper = new QueryWrapper<>();
                        eduCourseLabelCourseQueryWrapper.eq("course_id",eduCourse.getId());
                        List<LabelCourse> eduCourseLabelCourses = labelCourseService.list(eduCourseLabelCourseQueryWrapper);
                        List<String> eduLabels = null;
                        if (eduCourseLabelCourses != null && !eduCourseLabelCourses.isEmpty()) {
                            QueryWrapper<Label> eduLabelQueryWrapper = new QueryWrapper<>();
                            eduLabelQueryWrapper.in("id",eduCourseLabelCourses.stream().map(LabelCourse::getLabelId).collect(Collectors.toList()));
                            List<Label> eduLabelList = labelService.list(eduLabelQueryWrapper);
                            eduLabels = eduLabelList.stream().map(Label::getName).collect(Collectors.toList());
                        }

                        QueryWrapper<LabelCourse> payCourseLabelQueryWrapper = new QueryWrapper<>();
                        payCourseLabelQueryWrapper.eq("course_id",payCours.getId());
                        List<LabelCourse> payCourseLabelCourses = labelCourseService.list(payCourseLabelQueryWrapper);
                        List<String> payLabels = null;
                        if (payCourseLabelCourses != null && !payCourseLabelCourses.isEmpty()) {
                            QueryWrapper<Label> payLabelQueryWrapper = new QueryWrapper<>();
                            payLabelQueryWrapper.in("id",payCourseLabelCourses.stream().map(LabelCourse::getLabelId).collect(Collectors.toList()));
                            List<Label> payLabelList = labelService.list(payLabelQueryWrapper);
                            payLabels =  payLabelList.stream().map(Label::getName).collect(Collectors.toList());
                        }

                        if (eduLabels != null && payLabels != null) {
                            int index = 0;
                            for (String labelDTO : eduLabels) {
                                double w_ti = weightList[index];
                                if (payLabels.contains(labelDTO)) {
                                    double w_tj = weightList[payLabels.indexOf(labelDTO)];
                                    sim[index] = new double[]{w_ti, w_tj};
                                }
                                index++;
                            }
                            //余弦相似度计算
                            similaritySingle += cosOperate(sim);
                        }
                    }
                    // 最近的一个课程与所有历史购买中的课程相似度之和 similaritySingle;
                    heap.offer(new CourseToSimilarity(eduCourse, similaritySingle));
                }
                int cnt = 0;
                while (!heap.isEmpty() && cnt < 8) {
                    CourseToSimilarity poll = heap.poll();
                    if (poll == null) {
                        break;
                    }
                    courseList.add(poll.eduCourse);
                    cnt++;
                }
                return courseList;
            }
        }
        // 前面没查询到冷启动
        if (courseList.isEmpty()) {
            QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("view_count");
            wrapper.last("limit 8");
            courseList = baseMapper.selectList(wrapper);
        }

        return courseList;
    }




    public double cosOperate(double[][] sim) {
        double numerator = 0.0;
        double denominator = 0.0;
        for (int i = 2; i >= 0; i--) {
            double w_ti = sim[i][0];
            double w_tj = sim[i][1];
            numerator += w_ti * w_tj;
            denominator += Math.sqrt(Math.pow(w_ti, 2) * Math.pow(w_tj, 2));
        }
        return numerator / denominator;
    }




    static class CourseToSimilarity {
        EduCourse eduCourse;
        Double similarity;

        public CourseToSimilarity(EduCourse eduCourse, Double similarity) {
            this.eduCourse = eduCourse;
            this.similarity = similarity;
        }
    }
}


