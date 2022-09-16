package com.atguigu.educms.service;

import com.atguigu.educms.entity.CrmBanner;
import com.atguigu.educms.entity.vo.BannerQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 首页banner表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-13
 */
public interface CrmBannerService extends IService<CrmBanner> {

    List<CrmBanner> getAll();

    void pageQuery(Page<CrmBanner> bannerPage, BannerQuery bannerQuery);

    void saveBanner(CrmBanner crmBanner);

    void updateBannerById(CrmBanner crmBanner);

    void removeBannerById(String id);

    CrmBanner getBannerById(String id);
}
