package com.atguigu.eduUcenter.controller;

import com.atguigu.commonutils.JwtUtils;
import com.atguigu.eduUcenter.entity.UcenterMember;
import com.atguigu.eduUcenter.service.UcenterMemberService;
import com.atguigu.eduUcenter.util.ConstantPropertiesUtil;
import com.atguigu.eduUcenter.util.HttpClientUtils;
import com.atguigu.serviceBase.ExceptionHandler.SvvvvvException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * 微信扫码登录
 */

@Controller
@RequestMapping(value = "/api/ucenter/wx")
public class WxApiController {

    @Autowired
    private UcenterMemberService ucenterMemberService;

    @GetMapping("callback")
    public String callback(String code, String state) {
        //拿着code，去请求微信固定的地址，得到两个值 access_token 和 openid
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";

        //向认证服务器发送请求换取access_token
        String accessTokenUrl = String.format(baseAccessTokenUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);
        String result = null;
        try {
            result = HttpClientUtils.get(accessTokenUrl);
        } catch (Exception e) {
            throw new SvvvvvException(20001,"获取access_token失败");
        }

        Gson gson = new Gson();
        HashMap<String,Object> map = gson.fromJson(result, HashMap.class);
        String accessToken =(String) map.get("access_token");
        String openid =(String) map.get("openid");

        //根据openidq去数据库中查询是否已经存在该用户
        UcenterMember member = ucenterMemberService.getByOpenid(openid);

        //不存在就注册数据库
        if (member == null) {
            //访问微信的资源服务器，获取用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
            String reultUserInfo = null;
            try {
                reultUserInfo = HttpClientUtils.get(userInfoUrl);
            } catch (Exception e) {
                throw new SvvvvvException(20001,"获取用户信息失败");
            }
            HashMap<String,Object> userInfoMap = gson.fromJson(reultUserInfo, HashMap.class);
            String nickname =(String) userInfoMap.get("nickname");
            String headimgurl =(String) userInfoMap.get("headimgurl");

            member = new UcenterMember();
            member.setNickname(nickname);
            member.setAvatar(headimgurl);
            member.setOpenid(openid);
            ucenterMemberService.save(member);
        }

        String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());

        return "redirect:http://localhost:3000?token="+jwtToken;
    }

    @GetMapping("/login")
    public String getQrConnect() {
        // 微信开放平台授权baseUrl
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        // 回调地址
        String redirectUrl = ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL; //获取业务服务器重定向地址
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8"); //url编码
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        //设置 %s 占位符的参数，上面有3处
        String url = String.format(baseUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                redirectUrl,
                "Svvvvv");

        return "redirect:" + url;
    }
}
