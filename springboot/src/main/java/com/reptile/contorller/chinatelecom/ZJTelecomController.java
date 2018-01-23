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

import com.reptile.service.chinatelecom.ZJTelecomService;

@Controller
@RequestMapping("ZJTelecomController")
public class ZJTelecomController {
	@Autowired
	private ZJTelecomService service;
	
	@ApiOperation(value = "0.1判断是否需要验证码", notes = "")
	@ResponseBody
	@RequestMapping(value = "isNeedCode", method = RequestMethod.POST)
	public  Map<String,Object> idNeedCode(HttpServletRequest request){
		
		return service.isNeedCode(request);
	}
	
	
	@ApiOperation(value = "0.2不需要验证码，获取详单", notes = "参数：手机号，服务密码，经度，纬度")
	@ResponseBody
	@RequestMapping(value = "getDetailNoCode", method = RequestMethod.POST)
	public  Map<String,Object> getDetailNoCode(HttpServletRequest request,@RequestParam("phoneNumber")String phoneNumber,@RequestParam("servePwd")String servePwd,@RequestParam("longitude")String longitude,@RequestParam("latitude")String latitude,@RequestParam("uuid")String uuid){
		
		return service.getDetailNoCode(request, phoneNumber, servePwd, longitude, latitude, uuid);
	}
	

	@ApiOperation(value = "0.2需要验证码，获取验证码", notes = "参数：手机号")
	@ResponseBody
	@RequestMapping(value = "getCode", method = RequestMethod.POST)
	public  Map<String,Object> getCode(HttpServletRequest request,@RequestParam("phoneNumber")String phoneNumber){
		
		return service.getCode(request, phoneNumber);
	}
	
	@ApiOperation(value = "0.3需要验证码，获取详单", notes = "参数：：手机号，服务密码，验证码，经度，纬度")
	@ResponseBody
	@RequestMapping(value = "getDetailNeedCode", method = RequestMethod.POST)
	public  Map<String,Object> getDetailNeedCode(HttpServletRequest request,@RequestParam("phoneNumber")String phoneNumber,@RequestParam("servePwd")String servePwd,@RequestParam("code")String code,@RequestParam("longitude")String longitude,@RequestParam("latitude")String latitude,@RequestParam("uuid")String uuid){
		
		return service.getDetailNeedCode(request, phoneNumber, servePwd, code, longitude, latitude, uuid);
	}
}
