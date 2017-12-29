package com.reptile.contorller.chinatelecom;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.ChinaTelecom.ZhejiangTelecomService;
import com.reptile.util.CustomAnnotation;

@Controller
@RequestMapping("zheJiangTelecom")
public class ZhejiangTelecomController {
	@Autowired
	private ZhejiangTelecomService zheJiangTelecomService;
	
	@ApiOperation(value = "0.1获取验证码", notes = "参数：手机号")
	@ResponseBody
	@RequestMapping(value = "zheJiangLogin", method = RequestMethod.POST)
	public  Map<String,Object> zheJiangLogin(HttpServletRequest request,@RequestParam("phoneNumber")String phoneNumber){
		
		return zheJiangTelecomService.zheJiangLogin(request,phoneNumber);
	}
	
	@ApiOperation(value = "0.2获取详单", notes = "参数：手机号，服务密码，姓名，身份证号,验证码")
	@ResponseBody
	@RequestMapping(value = "zheJiangDetial", method = RequestMethod.POST)
	@CustomAnnotation
	public Map<String,Object> zheJiangDetial(HttpServletRequest request,@RequestParam("phoneNumber")String phoneNumber,@RequestParam("servePwd")String servePwd,@RequestParam("name")String name,@RequestParam("idCard")String idCard,@RequestParam("code")String code,@RequestParam("longitude")String longitude,@RequestParam("latitude")String latitude,@RequestParam("UUID")String UUID){
		
		return zheJiangTelecomService.zheJiangDetial(request,phoneNumber,servePwd,name,idCard,code,longitude,latitude,UUID);
	}
}
