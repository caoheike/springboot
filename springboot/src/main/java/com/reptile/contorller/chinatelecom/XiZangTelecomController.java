package com.reptile.contorller.chinatelecom;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.ChinaTelecom.XiZangTelecomService;

@Controller
@RequestMapping("XiZangTelecomController")
public class XiZangTelecomController {
	 @Autowired
	    private XiZangTelecomService service;
	 @ApiOperation(value = "1.西藏电信 获取图片验证码", notes = "参数：")
     @ResponseBody
     @RequestMapping(value = "XZImageCode", method = RequestMethod.POST)
	 public Map<String, Object> getImageCode(HttpServletRequest request){
	 return service.getImageCode(request); 
	 }
	 @ApiOperation(value = "2.西藏电信 获取短信验证码", notes = "参数：图形验证码")
     @ResponseBody
     @RequestMapping(value = "XZGetCode", method = RequestMethod.POST)
	 public Map<String, Object> getSMCode(HttpServletRequest request,@RequestParam("phoneNumber")String phoneNumber,@RequestParam("imageCode")String imageCode){
		return service.getSMCode(request, phoneNumber,imageCode);
	 }
	 @ApiOperation(value = "3.西藏电信 获取通话详单", notes = "参数：手机号，服务密码，短信验证码，经度，纬度")
     @ResponseBody
     @RequestMapping(value = "XZGetDetail", method = RequestMethod.POST)
	 public Map<String, Object> getDetail(HttpServletRequest request,@RequestParam("phoneNumber")String phoneNumber, @RequestParam("serverPwd")String serverPwd,@RequestParam("code")String code,@RequestParam("longitude")String longitude,@RequestParam("latitude")String latitude){
		return service.getDetail(request, phoneNumber, serverPwd, code, longitude, latitude);
		 
	 }
}
