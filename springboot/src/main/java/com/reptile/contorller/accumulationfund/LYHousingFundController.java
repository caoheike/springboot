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
	 * 临沂市住房公积金详情查询
	 * @param request
	 * @param userName
	 * @param passWord
	 * @param idCard
	 * @param cityCode
	 * @return
	 */
	@ApiOperation(value = "临沂市住房公积金：详情",notes = "参数：用户名,密码,身份证号，城市编码")
	@ResponseBody
	@RequestMapping(value = "doGetDetail", method = RequestMethod.POST)
	public  Map<String,Object> doGetDetail(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("idCardNum")String idCardNum,@RequestParam("userName")String userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("fundCard")String fundCard){
		return lyHouseFundService.doLogin(request, userName, passWord, idCard, cityCode,idCardNum);
	}
}
