package com.reptile.contorller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.HebeiProvinceService;
import com.sun.net.httpserver.HttpServer;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("HebeiProvinceTelecomController")
public class HebeiProvinceTelecomController {
	@Autowired
	private  HebeiProvinceService hebei;
	
	 @ApiOperation(value = "1.身份证验证", notes = "参数：手机号，服务密码")
	 @ResponseBody
	 @RequestMapping(value = "HebeiUsercard", method = RequestMethod.POST)
	 public  Map<String,Object> HebeiUsercard(HttpServletRequest request,@RequestParam ("UserNume")String UserNume,@RequestParam("UserPass") String UserPass ){
		return hebei.HebeiUsercard(request, UserNume, UserPass);
		
		 
	 }
	 @ResponseBody
	 @RequestMapping(value = "HebeiUsercard1", method = RequestMethod.POST)
	 public  Map<String,Object> HebeiUsercard1(HttpServletRequest request ){
		return hebei.HebeiUsercard1(request);
		
		 
	 }
}
