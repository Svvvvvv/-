package com.atguigu.educms.controller;


import com.atguigu.commonutils.R;
import com.atguigu.educms.entity.CrmBanner;
import com.atguigu.educms.entity.vo.BannerQuery;
import com.atguigu.educms.service.CrmBannerService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 首页banner表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2022-09-13
 */
@RestController
@RequestMapping("/educms/bannerAdmin")
@Api(tags = "轮播图后台")
public class BannerAdminController {


    @Autowired
    private CrmBannerService crmBannerService;


    //条件分页查询banner
    @PostMapping("/pageBanner/{page}/{limit}")
    public R getPageBanner(@PathVariable("page") Long page, @PathVariable("limit") Long limit, @RequestBody(required = false) BannerQuery bannerQuery) {
        Page<CrmBanner> crmBannerPage = new Page<>(page, limit);
        crmBannerService.pageQuery(crmBannerPage, bannerQuery);
        List<CrmBanner> records = crmBannerPage.getRecords();
        long total = crmBannerPage.getTotal();
        return R.ok().data("items", records).data("total", total);
    }

    //添加banner
    @PostMapping("/addBanner")
    public R addBanner(@RequestBody CrmBanner crmBanner) {
        crmBannerService.saveBanner(crmBanner);
        return R.ok();
    }

    //修改banner
    @PostMapping("/updateBanner")
    public R updateBanner(@RequestBody CrmBanner crmBanner) {
        crmBannerService.updateBannerById(crmBanner);
        return R.ok();
    }

    //根据id删除banner
    @DeleteMapping("/deleteBannerById/{id}")
    public R deleteBannerById(@PathVariable("id") String id) {
        crmBannerService.removeBannerById(id);
        return R.ok();
    }

    //根据id查询banner
    @GetMapping("/getBannerById/{id}")
    public R getBannerById(@PathVariable("id") String id) {
        CrmBanner crmBanner = crmBannerService.getBannerById(id);
        return R.ok().data("item", crmBanner);
    }

}

