package com.reptile.contorller;

import java.util.Map;

import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.RenFaWangService;


/**
 * 人法网
 * 
 */
@Controller
@RequestMapping("practise")
public class RenFaWangController {

	@Resource
	private RenFaWangService service;

	@ApiOperation(value = "人法网获取验证码", notes = "无需参数")
	// 设置标题描述
	@ResponseBody
	@RequestMapping(value = "GetRFWCode", method = RequestMethod.POST)
	public Map<String, Object> getBank(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return service.getImageCode(request, response);
	}
	
	@ApiOperation(value = "人法网信息获取", notes = "参数：查询条件，证件号，验证码")
	// 设置标题描述
	@ResponseBody
	@RequestMapping(value = "getDeltailData", method = RequestMethod.POST)
	public Map<String, Object> getDeltailData(HttpServletRequest request,HttpServletResponse response,
			@RequestParam String userName,@RequestParam(required=false,defaultValue="") String idCard,@RequestParam("Code") String code) throws Exception{
		return service.getDeltailData(request,response,userName,idCard,code);
		
	}
	
	@ApiOperation(value = "重新获取验证码", notes = "无需参数")
	// 设置标题描述
	@ResponseBody
	@RequestMapping(value = "getNewImageCode", method = RequestMethod.POST)
	public Map<String, Object> getNewImageCode(HttpServletRequest request,HttpServletResponse response) throws Exception{
		return service.getNewImageCode(request, response);
	}
	
	

}
