package com.reptile.contorller.chinatelecom;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.chinatelecom.HebeiProvinceService;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * 
 * @author liubin
 *
 */
@Controller
@RequestMapping("HebeiProvinceTelecomController")
public class HebeiProvinceTelecomController {
	@Autowired
	private  HebeiProvinceService hebei;
	
	 @ApiOperation(value = "1.发送短信验证码", notes = "参数：手机号,查询密码,姓名,身份证号")
	 @ResponseBody
	 @RequestMapping(value = "HebeiUsercard1", method = RequestMethod.POST)
	 public  Map<String,Object> hebeiUsercard1(HttpServletRequest request ,@RequestParam ("Usernum") String userNum,@RequestParam ("UserPass") String userPass,@RequestParam ("Username") String userName,@RequestParam ("Usercode") String userCode){
		return hebei.hebeiUsercard1(request,userNum,userPass,userName,userCode);
	 }
	 @ApiOperation(value = "2.获得通话详单", notes = "参数：手机号,查询密码,短信验证码")
	 @ResponseBody
	 @RequestMapping(value = "HebeiUsercard2", method = RequestMethod.POST)
	 public  Map<String,Object> hebeiUsercard2(HttpServletRequest request,@RequestParam ("Usernum") String userNum,@RequestParam ("UserPass") String userPass,@RequestParam ("Capth") String capth,@RequestParam("longitude")String longitude,@RequestParam("latitude")String latitude){
		
		 return hebei.hebeiUsercard2(request,userNum,userPass,capth,longitude,latitude);
	 }
}
