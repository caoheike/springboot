package com.reptile.contorller;


import com.reptile.service.ShanDongTelecomService;
import com.reptile.util.CustomAnnotation;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("ShanDongTelecomController")
public class ShanDongTelecomController {

    @Autowired
    private ShanDongTelecomService service;

    @ApiOperation(value = "1.获取图片验证码", notes = "参数：无")
    @ResponseBody
    @RequestMapping(value = "getImageCode", method = RequestMethod.POST)
    public Map<String, Object> getImageCode(HttpServletRequest request) {
        return service.getImageCode(request);
    }

//    @ApiOperation(value = "2.重新获取图片验证码", notes = "参数：无")
//    @ResponseBody
//    @RequestMapping(value = "reGetImageCode", method = RequestMethod.POST)
//    public Map<String,Object> reGetImageCode(HttpServletRequest request){
//        return service.reGetImageCode(request);
//    }

    @ApiOperation(value = "3.发送短信验证码", notes = "参数：无,eg:发送验证码前必须保证图片验证码不为空")
    @ResponseBody
    @RequestMapping(value = "sendPhoneCode", method = RequestMethod.POST)
    public  Map<String,Object> sendPhoneCode(HttpServletRequest request, @RequestParam("imageCode") String imageCode){
        return service.sendPhoneCode(request,imageCode);
    }

    @ApiOperation(value = "4.获取通话详单", notes = "参数：图片验证码，用户名姓名，用户身份证，手机验证码")
    @ResponseBody
    @CustomAnnotation
    @RequestMapping(value = "getDetailMes", method = RequestMethod.POST)
    public Map<String, Object> getDetailMes(HttpServletRequest request,@RequestParam("userIphone")String userIphone, @RequestParam("imageCode") String imageCode,
                                            @RequestParam("userName")String userName, @RequestParam("userCard") String userCard,
                                            @RequestParam("phoneCode")String phoneCode,@RequestParam("userPassword")String userPassword,
                                            @RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude){
        return service.getDetailMes(request,userIphone,imageCode,userName,userCard,phoneCode,userPassword,longitude,latitude);
    }
}
