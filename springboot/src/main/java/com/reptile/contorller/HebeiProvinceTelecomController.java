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
	
	 @ApiOperation(value = "1.发送短信验证码", notes = "参数：手机号,查询密码,姓名,身份证号")
	 @ResponseBody
	 @RequestMapping(value = "HebeiUsercard1", method = RequestMethod.POST)
	 //河北电信发送短信验证码
	 public  Map<String,Object> HebeiUsercard1(HttpServletRequest request ,@RequestParam ("Usernum") String Usernum,@RequestParam ("UserPass") String UserPass,@RequestParam ("Username") String Username,@RequestParam ("Usercode") String Usercode){
		return hebei.HebeiUsercard1(request,Usernum,UserPass,Username,Usercode);
	 }
	 @ApiOperation(value = "2.获得通话详单", notes = "参数：手机号,查询密码,短信验证码")
	 @ResponseBody
	 @RequestMapping(value = "HebeiUsercard2", method = RequestMethod.POST)
	 public  Map<String,Object> HebeiUsercard2(HttpServletRequest request,@RequestParam ("Usernum") String Usernum,@RequestParam ("UserPass") String UserPass,@RequestParam ("Capth") String Capth  ){
		return hebei.HebeiUsercard2(request,Usernum,UserPass,Capth);
	 }
}
