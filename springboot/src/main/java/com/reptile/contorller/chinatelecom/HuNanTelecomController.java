package com.reptile.contorller.chinatelecom;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.chinatelecom.HuNanTelecomService;

@Controller
@RequestMapping("HuNanTelecomController")
public class HuNanTelecomController {
	@Autowired
	private HuNanTelecomService hunan;
	
	
	@ResponseBody
	
    @ApiOperation(value = "1.获取图片验证码", notes = "参数：身份证,用户名")
    @RequestMapping(value = "HuNanimgeCode", method = RequestMethod.POST)
	
    //湖南电信获取图片验证码
    public Map<String, Object> HuNanimgeCode(HttpServletRequest request,@RequestParam("idCard") String idCard,
    																	@RequestParam("name") String name) {
        return hunan.hunanimgeCode(request,idCard,name);
    }
	
	 @ApiOperation(value = "2.发送短信验证码", notes = "参数：图片验证码")
	    @RequestMapping(value = "huNanPhoneCode", method = RequestMethod.POST)
		
	    //湖南电信获取图片验证码
	    public Map<String, Object> huNanPhoneCode(HttpServletRequest request,@RequestParam("imageCode") String imageCode) {
	        return hunan.huNanPhoneCode(request,imageCode);
	    }
	
    @ApiOperation(value = "3.获取详单信息", notes = "参数：手机号,短信验证码,图片验证码,服务密码")
    @ResponseBody
    @RequestMapping(value = "huNanPhoneDetail", method = RequestMethod.POST)
    
    //天津电信获取通话详单
    public Map<String, Object> huNanPhoneDetail(HttpServletRequest request,@RequestParam("phoneNumber") String phoneNumber,@RequestParam("PassCode") String PassCode,@RequestParam("imageCode") String imageCode,@RequestParam("servicepwd") String servicepwd,@RequestParam("longitude")String longitude,@RequestParam("latitude")String latitude) {
        
    	return hunan.huNanPhoneDetail(request,PassCode,imageCode,longitude,latitude,phoneNumber,servicepwd);
    }
}
