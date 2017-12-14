package com.reptile.contorller.ChinaTelecom;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.ChinaTelecom.HuBeiProviceService;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("HuBeiProvinceTelecomController")
public class HuBeiProvinceTelecomController {
		@Autowired
		private HuBeiProviceService huebi;
		
		@ApiOperation(value = "1.发送短信验证码", notes = "参数：手机号,查询密码")
	  	@ResponseBody
	    @RequestMapping(value = "HubeiPhoneCode", method = RequestMethod.POST)
	    //湖北电信发送手机验证码
	    public Map<String, Object> HubeiPhoneCode(HttpServletRequest request,@RequestParam("PhoneCode") String PhoneCode,@RequestParam("PhonePass") String PhonePass) {
	        return huebi.hubeicode(request,PhoneCode,PhonePass);
	    }
		@ApiOperation(value = "2.获取详单", notes = "参数：短信验证码,手机号,服务密码")
	  	@ResponseBody
	    @RequestMapping(value = "HubeiPhone", method = RequestMethod.POST)
	    //湖北电信获取通话详单
	    public Map<String, Object> HubeiPhone(HttpServletRequest request,
	    		@RequestParam("PassCode") String PassCode,
	    		@RequestParam("PhoneNum") String PhoneNum,
	    		@RequestParam("PhonePass") String PhonePass,
	    		@RequestParam("longitude")String longitude,
	    		@RequestParam("latitude")String latitude,
	    		@RequestParam("UUID")String UUID) {
	        
			return huebi.hubeiphone(request,PassCode,PhoneNum,PhonePass,longitude,latitude,UUID);
	    }
}
