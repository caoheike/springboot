package com.reptile.contorller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.reptile.service.CCBService;
import com.reptile.service.ConstructionService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("CCBController")
public class CCBController {
	
	@Autowired
	private CCBService ccb;
	
	@ApiOperation(value = "建设银行储蓄卡", notes = "参数：用户账号(身份证),银行账号,查询密码,UUID")
	@ResponseBody
	@RequestMapping(value="CCBBank",method=RequestMethod.POST)
	public Map<String,Object> CCBBank(HttpServletRequest request,@RequestParam("IDNumber") String IDNumber,@RequestParam("cardNumber") String cardNumber,@RequestParam("cardPass") String cardPass,@RequestParam("UUID")String UUID) throws Exception{
		System.out.println("建设银行储蓄卡");
		return ccb.ccbInformation(request, IDNumber, cardNumber, cardPass,UUID);
				
	}
	@RequestMapping(value="ttt",method=RequestMethod.GET)
	public Map<String,Object> ttt(HttpServletRequest request) throws Exception{
		System.out.println("asd");
		return null;
				
	}
}
