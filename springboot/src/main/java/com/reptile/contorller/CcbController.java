package com.reptile.contorller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.reptile.service.CcbService;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author liubin
 *
 */
@RestController
@RequestMapping("CCBController")
public class CcbController {
	
	@Autowired
	private CcbService ccb;
	
	@ApiOperation(value = "建设银行储蓄卡", notes = "参数：用户账号(身份证),银行账号,查询密码,UUID")
	@ResponseBody
	@RequestMapping(value="CCBBank",method=RequestMethod.POST)
	public Map<String,Object> ccbBank(HttpServletRequest request,@RequestParam("IDNumber") String iDNumber,@RequestParam("cardNumber") String cardNumber,@RequestParam("cardPass") String cardPass,@RequestParam("UUID")String uUID,
			@RequestParam("flag")boolean flag) throws Exception{
		System.out.println("建设银行储蓄卡");
		return ccb.ccbInformation(request, iDNumber, cardNumber, cardPass,uUID,flag);
				
	}
}
