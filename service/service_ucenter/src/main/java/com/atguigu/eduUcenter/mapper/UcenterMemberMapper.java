package com.atguigu.eduUcenter.mapper;

import com.atguigu.eduUcenter.entity.UcenterMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2022-09-14
 */
public interface UcenterMemberMapper extends BaseMapper<UcenterMember> {

    //统计当天注册人数
    Integer countRegister(@Param("day") String day);
}
