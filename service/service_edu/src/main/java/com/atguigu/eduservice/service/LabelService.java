package com.atguigu.eduservice.service;

import com.atguigu.eduservice.entity.Label;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Svvvvv
 * @since 2023-04-18
 */
public interface LabelService extends IService<Label> {

    void removeByCourseId(String courseId);
}
