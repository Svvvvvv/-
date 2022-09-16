package com.atguigu.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.atguigu.oss.service.OssService;
import com.atguigu.oss.utils.ConstantPropertiesUtil;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
    @Override
    public String uploadFileAvatar(MultipartFile file) {
        String endPoint = ConstantPropertiesUtil.END_POINT;
        String keyId = ConstantPropertiesUtil.ACCESS_KEY_ID;
        String secret = ConstantPropertiesUtil.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtil.BUCKET_NAME;
       // String fileHost = ConstantPropertiesUtil.FILE_HOST;

        InputStream inputStream = null;
        try {
            // 创建OSS实例。
            OSS ossClient = new OSSClientBuilder().build(endPoint, keyId, secret);

            // 获取上传文件的输入流
            inputStream = file.getInputStream();

            //获取文件名称
            String fileName = file.getOriginalFilename();

            //防止文件名重复
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");

            //创建日期用于分组文件
            String dataPath = new DateTime().toString("yyyy-MM-dd");

            fileName = dataPath+"/"+uuid+fileName;

            //调用oss实例中的方法实现上传
            //参数1： Bucket名称
            //参数2： 上传到oss文件路径和文件名称 /aa/bb/1.jpg
            //参数3： 上传文件的输入流
            ossClient.putObject(bucketName, fileName, inputStream);
            // 关闭OSSClient。
            ossClient.shutdown();

            //把上传后文件路径返回
            //需要把上传到阿里云oss路径手动拼接出来
            // https://edu-guli-sxk.oss-cn-hangzhou.aliyuncs.com/default.jpg
            String url = "https://"+bucketName+"."+endPoint+"/"+fileName ;

            return url;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
