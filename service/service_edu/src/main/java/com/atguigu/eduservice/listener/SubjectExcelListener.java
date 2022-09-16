package com.atguigu.eduservice.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.excel.SubjectData;
import com.atguigu.eduservice.service.EduSubjectService;
import com.atguigu.serviceBase.ExceptionHandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public class SubjectExcelListener extends AnalysisEventListener<SubjectData> {

    private EduSubjectService subjectService;

    public SubjectExcelListener() {
    }

    public SubjectExcelListener(EduSubjectService subjectService) {
        this.subjectService = subjectService;
    }



    @Override
    public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
        if (subjectData == null) {
            throw new GuliException(20001, "excel没有数据");
        }

        //判断数据库中是否有一级分类
        EduSubject oneSubject = existOneSubject(subjectData.getOneSubjectName());
        if (oneSubject == null) {
            EduSubject eduSubject = new EduSubject();
            eduSubject.setTitle(subjectData.getOneSubjectName());
            eduSubject.setParentId("0");
            boolean save = subjectService.save(eduSubject);
            if (!save) {
                throw new GuliException(20001,"一级分类保存失败");
            }
        }

        //获取一级分类的id充当二级分类的parent_id
        String pid = oneSubject.getId();
        EduSubject twoSubject = existTwoSubject(subjectData.getTwoSubjectName(), pid);
        if (twoSubject == null) {
            EduSubject eduSubject = new EduSubject();
            eduSubject.setTitle(subjectData.getTwoSubjectName());
            eduSubject.setParentId(pid);
            boolean save = subjectService.save(eduSubject);
            if (!save) {
                throw new GuliException(20001,"二级分类保存失败");
            }
        }

    }

    //读取数据库一级分类是否已有
    private EduSubject existOneSubject(String name) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title", name)
                .eq("parent_id","0");
        return subjectService.getOne(wrapper);
    }

    private EduSubject existTwoSubject(String name,String pid) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title", name)
                .eq("parent_id",pid);
        return subjectService.getOne(wrapper);
    }


    //读取数据库二级分类是否已有

    //读取后
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
