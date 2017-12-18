package com.reptile.contorller.socialSecurity;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.socialSecurity.ZhenJiangSocialSecurityService;

import io.swagger.annotations.ApiOperation;


@Controller
@RequestMapping("ZhenJiangSocialSecurity")
public class ZhenJiangSocialSecurityController {
	@Autowired
	private ZhenJiangSocialSecurityService Service;
 
	
	/**
	 * 镇江市社保详情查询
	 * @param request
	 * @param idCard 账户身份证
	 * @param passWord 密码
	 * @param cityCode 城市编码
	 * @param idCardNum 身份证
	 * @return
	 */
	@ApiOperation(value = "镇江市社保：详情",notes = "参数：账号,密码,城市编码,身份证")
	@ResponseBody
	@RequestMapping(value = "doGetDetail", method = RequestMethod.POST)
	public  Map<String,Object> doGetDetail(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("passWord")String passWord,@RequestParam("cityCode")String cityCode,@RequestParam("idCardNum")String idCardNum){
		return Service.doLogin(request, idCard, passWord,cityCode,idCardNum);
	}
}
