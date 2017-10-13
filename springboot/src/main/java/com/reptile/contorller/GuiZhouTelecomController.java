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

import com.reptile.service.GuiZhouTelecomService;
import com.reptile.util.CustomAnnotation;

@Controller
@RequestMapping("guizhouTelecom")
public class GuiZhouTelecomController {
	
	@Autowired	
	private GuiZhouTelecomService guizhouTelecomService;
	@ApiOperation(value = "0.1获取验证码")
	@ResponseBody
	@RequestMapping(value = "guiZhouLogin", method = RequestMethod.POST)
	public  Map<String,Object> guiZhouLogin(HttpServletRequest request){
		
		return guizhouTelecomService.guiZhouLogin(request);
	}
	@ApiOperation(value = "0.2获取内容",notes = "参数：手机号,服务密码,验证码")
	@ResponseBody
	@CustomAnnotation
	@RequestMapping(value = "guiZhouDetial", method = RequestMethod.POST)
	public  Map<String,Object> guiZhouDetial(HttpServletRequest request,@RequestParam("phoneNumber")String phoneNumber,@RequestParam("servePwd") String servePwd,@RequestParam("code")String code){
		return guizhouTelecomService.guiZhouDetial(code, request, phoneNumber,servePwd);
	}
	
}
