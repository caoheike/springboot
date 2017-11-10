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

import com.reptile.service.socialSecurity.QinZhouSocialSecurityService;

@Controller
@RequestMapping("QinZhouSocialSecurity")
public class QinZhouSocialSecurityController {
	
	@Autowired
    private QinZhouSocialSecurityService qinZhouSocialSecurityService;
	
	@ApiOperation(value = "钦州社保：获取验证码",notes = "参数：无")
	@ResponseBody
	@RequestMapping(value = "doGetVerifyImg", method = RequestMethod.POST)
	public  Map<String,Object> doGetVerifyImg(HttpServletRequest request){
		return qinZhouSocialSecurityService.doGetVerifyImg(request);
		
	}
	
	
	/**
	 * 钦州社保详情查询
	 * @param request
	 * @param idCard 身份证
	 * @param passWord 密码
	 * @param catpy 验证码
	 * @param cityCode 城市编码
	 * @return
	 */
	@ApiOperation(value = "钦州社保：详情",notes = "参数：身份证,密码,验证码,城市编码")
	@ResponseBody
	@RequestMapping(value = "doGetDetail", method = RequestMethod.POST)
	public  Map<String,Object> doGetDetail(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("userName")String  userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("socialCard")String socialCard){
		return qinZhouSocialSecurityService.doGetDetail(request, idCard, passWord, catpy, cityCode);
	}
}
