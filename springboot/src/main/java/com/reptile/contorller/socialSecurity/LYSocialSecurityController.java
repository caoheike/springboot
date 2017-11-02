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

import com.reptile.service.socialSecurity.LYSocialSecurityService;

@Controller
@RequestMapping("linYiSocialSecurity")
public class LYSocialSecurityController {
	@Autowired
	private LYSocialSecurityService lySocialSecurityService;
	
	
	/**
	 * 临沂市社保登录
	 * @param request
	 * @param userName 姓名
	 * @param idCard 身份证号码
	 * @return
	 */
	@ApiOperation(value = "临沂市社保：登陆",notes = "参数：姓名，身份证号码")
	@ResponseBody
	@RequestMapping(value = "lyLogin", method = RequestMethod.POST)
	public  Map<String,Object> lyLogin(HttpServletRequest request,@RequestParam("userName")String userName,@RequestParam("idCard")String idCard){
		return lySocialSecurityService.lyLogin(request,userName,idCard);
		
	}
	
	
	/**
	 * 临沂市社保详情查询
	 * @param request
	 * @param idCard 身份证号
	 * @return
	 */
	@ApiOperation(value = "临沂市社保：详情")
	@ResponseBody
	@RequestMapping(value = "lyGetDetail", method = RequestMethod.POST)
	public  Map<String,Object> lyGetDetail(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("cityCode")String cityCode){
		return lySocialSecurityService.lyGetDetail(request,idCard,cityCode);
	}
}
