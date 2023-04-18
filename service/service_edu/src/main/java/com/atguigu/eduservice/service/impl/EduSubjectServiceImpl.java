package com.atguigu.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.excel.SubjectData;
import com.atguigu.eduservice.entity.subject.OneSubject;
import com.atguigu.eduservice.entity.subject.TwoSubject;
import com.atguigu.eduservice.listener.SubjectExcelListener;
import com.atguigu.eduservice.mapper.EduSubjectMapper;
import com.atguigu.eduservice.service.EduSubjectService;
import com.atguigu.serviceBase.ExceptionHandler.SvvvvvException;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-10
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    @Override
    public void saveSubject(MultipartFile file, EduSubjectService subjectService) {
        try {
            InputStream is = file.getInputStream();
            EasyExcel.read(is, SubjectData.class,new SubjectExcelListener(subjectService)).sheet().doRead();
        } catch (IOException e) {
            throw new SvvvvvException(20001,"添加课程分类获取输入流失败");
        }

    }

    @Override
    public List<OneSubject> getAllSubject() {
        //得到所有一级分类
        QueryWrapper<EduSubject> oneWrapper = new QueryWrapper<>();
        oneWrapper.eq("parent_id","0");
        List<EduSubject> oSubjects = baseMapper.selectList(oneWrapper);

        //得到所有二级分类
        QueryWrapper<EduSubject> twoWrapper = new QueryWrapper<>();
        twoWrapper.ne("parent_id","0");
        List<EduSubject> tSubjects = baseMapper.selectList(twoWrapper);

        //创建最终返回的sucjectList
        List<OneSubject> finalSubjects = new ArrayList<>();

        //封装fianlSubjects
        for (EduSubject oSubject : oSubjects) {
            OneSubject oneSubject = new OneSubject();
            BeanUtils.copyProperties(oSubject,oneSubject);
            finalSubjects.add(oneSubject);

            List<TwoSubject> finalTwoSubjects = new ArrayList<>();
            //封装二级
            for (EduSubject tSubject : tSubjects) {
                if (tSubject.getParentId().equals(oSubject.getId())){
                    TwoSubject twoSubject = new TwoSubject();
                    BeanUtils.copyProperties(tSubject,twoSubject);
                    finalTwoSubjects.add(twoSubject);
                }
            }
            oneSubject.setChildren(finalTwoSubjects);
        }
        return finalSubjects;
    }
}
