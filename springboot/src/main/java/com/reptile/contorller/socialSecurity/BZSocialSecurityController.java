package com.reptile.contorller.socialSecurity;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.socialSecurity.BZSocialSecurityService;


@Controller
@RequestMapping("binZhouSocialSecurity")
public class BZSocialSecurityController {
	@Autowired
	private BZSocialSecurityService bzSocialSecurityService;
	
	
	@ApiOperation(value = "滨州市社保：获取验证码",notes = "参数：无")
	@ResponseBody
	@RequestMapping(value = "bzGetVerifyImg", method = RequestMethod.POST)
	public  Map<String,Object> getVerifyImg(HttpServletRequest request){
		return bzSocialSecurityService.getVerifyImg(request);
		
	}
	
	/**
	 * 滨州市社保登录
	 * @param request
	 * @param userCard 用户名
	 * @param passWord 密码
	 * @return
	 */
	@ApiOperation(value = "滨州市社保：登陆",notes = "参数：身份证,密码,验证码")
	@ResponseBody
	@RequestMapping(value = "bzLogin", method = RequestMethod.POST)
	public  Map<String,Object> bzLogin(HttpServletRequest request,@RequestParam("userCard")String userCard,@RequestParam("passWord")String passWord,@RequestParam("catpy")String userCode){
		return bzSocialSecurityService.bzLogin(request, userCard, passWord,userCode);
		
	}
	
	
	/**
	 * 滨州市社保详情查询
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "滨州市社保：详情")
	@ResponseBody
	@RequestMapping(value = "bzGetDetail", method = RequestMethod.POST)
	public  Map<String,Object> bzGetDetail(HttpServletRequest request,@RequestParam("userCard")String userCard,@RequestParam("cityCode")String cityCode){
		return bzSocialSecurityService.bzGetDetail(request,userCard,cityCode);
	}
}
