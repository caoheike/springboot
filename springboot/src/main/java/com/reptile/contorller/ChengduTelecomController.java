package com.reptile.contorller;

import com.reptile.service.ChengduTelecomService;
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
@RequestMapping("ChengduTelecomController")
public class ChengduTelecomController {
    @Autowired
    private ChengduTelecomService service;



//    @ApiOperation(value = "0.3.如需要验证码则先获取验证码", notes = "参数：无")
//    @ResponseBody
//    @RequestMapping(value = "getImageCode", method = RequestMethod.POST)
//    public Map<String, Object> getImageCode(HttpServletRequest request) {
//        return service.getImageCode(request);
//    }



    @ApiOperation(value = "2.发送手机验证码", notes = "参数：无")
    @ResponseBody
    @RequestMapping(value = "sendPhoneCode", method = RequestMethod.POST)
    public Map<String, String> sendPhoneCode(HttpServletRequest request) {
        return service.sendPhoneCode(request);
    }

    @ApiOperation(value = "3.获取详单信息", notes = "参数：手机验证码,手机号，服务密码")
    @ResponseBody
    @CustomAnnotation
    @RequestMapping(value = "getDetailMes", method = RequestMethod.POST)
    public Map<String, Object> getDetailMes(HttpServletRequest request,@RequestParam("phoneNumber") String phoneNumber, @RequestParam("phoneCode") String phoneCode,
                                             @RequestParam("servePwd") String servePwd,@RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude) {
        return service.getDetailMes(request,phoneNumber, phoneCode,servePwd,longitude,latitude);
    }


}
