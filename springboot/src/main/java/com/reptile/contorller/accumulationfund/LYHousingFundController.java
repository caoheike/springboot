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

import com.reptile.service.accumulationfund.LYHousingFundService;

@Controller
@RequestMapping("linYiHouseFund")
public class LYHousingFundController {
	@Autowired
	private LYHousingFundService lyHouseFundService;
	
	
	/**
	 * 临沂市住房公积金登录
	 * @param request
	 * @param userCard 用户名
	 * @param passWord 密码
	 * @return
	 */
	@ApiOperation(value = "临沂市住房公积金：登陆",notes = "参数：用户名,密码")
	@ResponseBody
	@RequestMapping(value = "lyLogin", method = RequestMethod.POST)
	public  Map<String,Object> lyLogin(HttpServletRequest request,@RequestParam("userName")String userName,@RequestParam("passWord")String passWord){
		return lyHouseFundService.lyLogin(request, userName, passWord);
		
	}

	/**
	 * 临沂市住房公积金详情查询
	 * @param request
	 * @param idCard 身份证号
	 * @return
	 */
	@ApiOperation(value = "临沂市住房公积金：详情")
	@ResponseBody
	@RequestMapping(value = "lyGetDetail", method = RequestMethod.POST)
	public  Map<String,Object> lyGetDetail(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("cityCode")String cityCode){
		return lyHouseFundService.lyGetDetail(request,idCard,cityCode);
	}
}
