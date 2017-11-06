package com.reptile.contorller.ChinaTelecom;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.ChinaTelecom.ChongQingTelecomService;
import com.reptile.util.CustomAnnotation;

@Controller
@RequestMapping("ChongQingTelecomController")
public class ChongQingTelecomController {
	@Autowired
	private ChongQingTelecomService chongQingService;
	 @ApiOperation(value = "1.登陆", notes = "参数：手机号，服务密码")
     @ResponseBody
     @RequestMapping(value = "CQLogin", method = RequestMethod.POST)
	 public Map<String, Object> chongQingLogin(HttpServletRequest request, @RequestParam("phoneNumber")String phoneNumber, @RequestParam("passWord")String servePwd){
		return chongQingService.chongQingLogin(request, phoneNumber, servePwd);
		 
	 }
	 @ApiOperation(value = "2.发送短信验证码", notes = "参数：姓名后两位，身份证后六位")
     @ResponseBody
     @RequestMapping(value = "CQSendCode", method = RequestMethod.POST)
	public Map<String, Object> sendCode(HttpServletRequest request,@RequestParam("userName")String userName,@RequestParam("idCard")String idCard) {
		
	return chongQingService.sendCode(request,userName,idCard);
	}
	 
	 
	 @ApiOperation(value = "3.获取详单", notes = "参数：手机号，服务密码，验证码，经度，纬度")
     @ResponseBody
     @CustomAnnotation
     @RequestMapping(value = "CQGetDetail", method = RequestMethod.POST)
	 public Map<String, Object> getDetail(HttpServletRequest request, @RequestParam("phoneNumber")String phoneNumber,@RequestParam("passWord")String passWord,@RequestParam("code")String code,@RequestParam("longitude")String longitude,@RequestParam("latitude")String latitude){
		return chongQingService.getDetail(request, phoneNumber, passWord,code, longitude, latitude);
			 
	 }
}


