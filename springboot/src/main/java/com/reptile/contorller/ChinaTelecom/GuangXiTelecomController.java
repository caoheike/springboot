package com.reptile.contorller.ChinaTelecom;

import com.reptile.service.ChinaTelecom.GuangXiTelecomService;
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
@RequestMapping("GuangXiTelecomController")
public class GuangXiTelecomController {
    @Autowired
    private GuangXiTelecomService service;

    @ApiOperation(value = "发送手机验证码", notes = "参数")
    @RequestMapping(value = "sendPhoneCode", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> sendPhoneCode(HttpServletRequest request, @RequestParam("phoneNumber") String phoneNumber) {
        return service.sendPhoneCode(request, phoneNumber);
    }

    @ApiOperation(value = "获取详单信息", notes = "参数")
    @RequestMapping(value = "getDetailMes", method = RequestMethod.POST)
    @CustomAnnotation
    @ResponseBody
    public Map<String, Object> getDetailMes(HttpServletRequest request, @RequestParam("phoneNumber") String phoneNumber, @RequestParam("serverPwd") String serverPwd,
                                            @RequestParam("phoneCode") String phoneCode, @RequestParam("userName") String userName,
                                            @RequestParam("userCard") String userCard,@RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude) {
        return service.getDetailMes(request, phoneNumber,serverPwd,phoneCode,userName,userCard,longitude,latitude);
    }
}
