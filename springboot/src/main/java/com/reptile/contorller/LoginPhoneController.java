package com.reptile.contorller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.PhoneloginService;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("LoginPhoneController")
public class LoginPhoneController {
	
	 @Autowired
	private PhoneloginService phonelogin;
	
	    @ApiOperation(value = "1.登录全国电信网上营业厅", notes = "参数：手机号，服务密码")
	    @ResponseBody
	    @RequestMapping(value = "loginPhone", method = RequestMethod.POST)
	    public Map<String, Object> loadGlobalDX(HttpServletRequest request, @RequestParam("userName") String userName,
	                                           @RequestParam("servePwd") String servePwd) {
	        return phonelogin.login(request, userName, servePwd);
	    }
}
