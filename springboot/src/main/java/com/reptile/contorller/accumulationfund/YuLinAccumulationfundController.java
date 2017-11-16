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

import com.reptile.service.accumulationfund.YuLinAccumulationfundService;
@Controller
@RequestMapping("yuLinHouseFund")
public class YuLinAccumulationfundController {

	@Autowired
	private YuLinAccumulationfundService yuLinAccumulationfundService;

	/**
	 * 玉林市住房公积金详情查询
	 * @param request
	 * @param passWord 密码
	 * @param idCard 身份证号
	 * @param cityCode 城市编码
	 * @return
	 */
	@ApiOperation(value = "玉林市住房公积金：详情",notes = "参数：身份证号,密码,城市编码")
	@ResponseBody
	@RequestMapping(value = "doGetDetail", method = RequestMethod.POST)
	public  Map<String,Object> doGetDetail(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("idCardNum")String idCardNum,@RequestParam("userName")String userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("fundCard")String fundCard){
		return yuLinAccumulationfundService.doGetDetail(request, idCard, passWord, cityCode,idCardNum);
	}

}
