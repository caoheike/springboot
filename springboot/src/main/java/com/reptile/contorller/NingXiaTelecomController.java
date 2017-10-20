package com.reptile.contorller;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.NingXiaTelecomService;
import com.reptile.util.CustomAnnotation;

@Controller
@RequestMapping("ningXiaTelecom")
public class NingXiaTelecomController {
	
		@Autowired	
		private NingXiaTelecomService ningxiaTelecomService;
		
		@ApiOperation(value = "0.1登陆",notes = "参数：手机号,服务密码")
		@ResponseBody
		@RequestMapping(value = "ningXiaLogin", method = RequestMethod.POST)
		public  Map<String,Object> ningXiaLogin(HttpServletRequest request, @RequestParam("phoneNumber")String phoneNumber, @RequestParam("servePwd")String servePwd){
			
			return ningxiaTelecomService.ningXiaLogin(request, phoneNumber, servePwd);
		}
		@ApiOperation(value = "0.2获取验证码",notes = "参数：手机号")
		@ResponseBody
		@RequestMapping(value = "ningXiaGetcode", method = RequestMethod.POST)
		public  Map<String,Object> ningXiaGetcode(HttpServletRequest request,@RequestParam("phoneNumber")String phoneNumber){
			return ningxiaTelecomService.ningXiaGetcode(request, phoneNumber);
		}
		@ApiOperation(value = "0.3获取详细信息",notes = "参数：手机号,服务密码,验证码")
		@ResponseBody
		@CustomAnnotation
		@RequestMapping(value = "ningXiaDetil", method = RequestMethod.POST)
		public Map<String,Object> ningXiaDetil(HttpServletRequest request,@RequestParam("phoneNumber")String phoneNumber,@RequestParam("servePwd") String servePwd,@RequestParam("code")String code,@RequestParam("longitude")String longitude,@RequestParam("latitude")String latitude){
			
			return ningxiaTelecomService.ningXiaDetial(request, phoneNumber, servePwd, code,longitude,latitude);
		}
}
