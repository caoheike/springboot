package com.reptile.contorller;

import com.reptile.service.XiNingTelecomService;
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
@RequestMapping("XiNingTelecomController")
public class XiNingTelecomController {

    @Autowired
    private XiNingTelecomService service;

    @ApiOperation(value = "1.获得通话详情", notes = "参数：手机号")
    @ResponseBody
    @CustomAnnotation
    @RequestMapping(value = "getDetailMes", method = RequestMethod.POST)
    public Map<String, Object> getDetailMes(HttpServletRequest request, @RequestParam("phoneNumber")String phoneNumber,
                                            @RequestParam("serverPwd") String serverPwd,@RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude) {
        return service.getDetailMes(request,phoneNumber,serverPwd,longitude,latitude);
    }

//    @ApiOperation(value = "3.获取详单信息", notes = "参数：手机验证码,手机号，服务密码")
//    @ResponseBody
//    @RequestMapping(value = "getDetailMes", method = RequestMethod.POST)
//    public Map<String, Object> getDetailMes(HttpServletRequest request, @RequestParam("phoneCode") String phoneCode,
//                                            @RequestParam("phoneNumber") String phoneNumber, @RequestParam("servePwd") String servePwd) {
//        return service.getDetailMes(request, phoneCode, phoneNumber, servePwd);
//    }
}
