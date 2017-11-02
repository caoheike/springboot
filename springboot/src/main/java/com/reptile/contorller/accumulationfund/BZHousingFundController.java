package com.reptile.contorller.accumulationfund;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.accumulationfund.BZHousingFundService;


@Controller
@RequestMapping("binZhouHouseFund")
public class BZHousingFundController {
	@Autowired
	private BZHousingFundService bzHouseFundService;
	
	
	/**
	 * 滨州市住房公积金登录
	 * @param request
	 * @param userCard 用户名
	 * @param passWord 密码
	 * @return
	 */
	@ApiOperation(value = "滨州市住房公积金：登陆",notes = "参数：用户名,密码")
	@ResponseBody
	@RequestMapping(value = "bzLogin", method = RequestMethod.POST)
	public  Map<String,Object> bzLogin(HttpServletRequest request,@RequestParam("userName")String userName,@RequestParam("passWord")String passWord){
		return bzHouseFundService.bzLogin(request, userName, passWord);
		
	}

	/**
	 * 滨州市住房公积金详情查询
	 * @param request
	 * @param idCard 身份号
	 * @return
	 */
	@ApiOperation(value = "滨州市住房公积金：详情")
	@ResponseBody
	@RequestMapping(value = "bzGetDetail", method = RequestMethod.POST)
	public  Map<String,Object> bzGetDetail(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("cityCode")String cityCode){
		return bzHouseFundService.bzGetDetail(request,idCard,cityCode);
	}
}
