package com.reptile.contorller.chinatelecom;

import com.reptile.service.chinatelecom.XiNingTelecomService;
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

/**
 * 西宁电信
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Controller
@RequestMapping("XiNingTelecomController")
public class XiNingTelecomController {

    @Autowired
    private XiNingTelecomService service;
    
    
    

    @ApiOperation(value = "1.西宁登录", notes = "参数：手机号")
    @ResponseBody
    @CustomAnnotation
    @RequestMapping(value = "doLogin", method = RequestMethod.POST)
    public Map<String, Object> doLogin(HttpServletRequest request, @RequestParam("userName") String userName,
    		@RequestParam("servePwd") String servePwd){
    	return service.doLogin(request, userName, servePwd);
    }
    
    @ApiOperation(value = "2.获得通话详情", notes = "参数：手机号")
    @ResponseBody
    @CustomAnnotation
    @RequestMapping(value = "getDetailMes", method = RequestMethod.POST)
    public Map<String, Object> getDetailMes(HttpServletRequest request, @RequestParam("phoneNumber")String phoneNumber,
                                            @RequestParam("serverPwd") String serverPwd,@RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude,@RequestParam("UUID")String uuid) {
        return service.getDetailMes(request,phoneNumber,serverPwd,longitude,latitude,uuid);
    }

//    @ApiOperation(value = "3.获取详单信息", notes = "参数：手机验证码,手机号，服务密码")
//    @ResponseBody
//    @RequestMapping(value = "getDetailMes", method = RequestMethod.POST)
//    public Map<String, Object> getDetailMes(HttpServletRequest request, @RequestParam("phoneCode") String phoneCode,
//                                            @RequestParam("phoneNumber") String phoneNumber, @RequestParam("servePwd") String servePwd) {
//        return service.getDetailMes(request, phoneCode, phoneNumber, servePwd);
//    }
}
