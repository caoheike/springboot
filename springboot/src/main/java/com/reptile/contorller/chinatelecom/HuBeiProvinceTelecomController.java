package com.reptile.contorller.chinatelecom;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.chinatelecom.HuBeiProviceService;

import io.swagger.annotations.ApiOperation;
/**
 * 
 * @author liubin
 *
 */

@Controller
@RequestMapping("HuBeiProvinceTelecomController")
public class HuBeiProvinceTelecomController {
		@Autowired
		private HuBeiProviceService huebi;
		
		@ApiOperation(value = "1.发送短信验证码", notes = "参数：手机号,查询密码")
	  	@ResponseBody
	    @RequestMapping(value = "HubeiPhoneCode", method = RequestMethod.POST)
	    public Map<String, Object> hubeiPhoneCode(HttpServletRequest request,@RequestParam("PhoneCode") String phoneCode,@RequestParam("PhonePass") String phonePass) {
	        return huebi.hubeicode(request,phoneCode,phonePass);
	    }
		@ApiOperation(value = "2.获取详单", notes = "参数：短信验证码,手机号,服务密码")
	  	@ResponseBody
	    @RequestMapping(value = "HubeiPhone", method = RequestMethod.POST)
	    public Map<String, Object> hubeiPhone(HttpServletRequest request,
	    		@RequestParam("PassCode") String passCode,
	    		@RequestParam("PhoneNum") String phoneNum,
	    		@RequestParam("PhonePass") String phonePass,
	    		@RequestParam("longitude")String longitude,
	    		@RequestParam("latitude")String latitude,
	    		@RequestParam("UUID")String uuid) {
	        
			return huebi.hubeiphone(request,passCode,phoneNum,phonePass,longitude,latitude,uuid);
	    }
}
