package com.atguigu.eduservice.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import com.atguigu.eduservice.entity.EduChapter;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2022-09-10
 */
@Mapper
public interface EduChapterMapper extends BaseMapper<EduChapter> {
}
