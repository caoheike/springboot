package com.reptile.contorller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.GansuProvinceService;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("GansuProvinceTelecomController")
public class GansuProvinceTelecomController {
	@Autowired
	private GansuProvinceService gansu;
		

    @ResponseBody
    @ApiOperation(value = "1.短信验证码", notes = "参数：手机号")
    @RequestMapping(value = "GansuPhoneCode", method = RequestMethod.POST)
    //甘肃电信发送手机验证码
    public Map<String, Object> GansuPhoneCode(HttpServletRequest request,@RequestParam("UserNum") String UserNum) {
        return gansu.GansuPhone(request,UserNum);
    }
    @ApiOperation(value = "2.获取详单信息", notes = "参数：身份证,手机号,服务密码,短信验证码")
    @ResponseBody
    @RequestMapping(value = "GansuPhone", method = RequestMethod.POST)
    //甘肃电信获取通话详单
    public Map<String, Object> GansuPhone(HttpServletRequest request,@RequestParam("Usercard") String Usercard,@RequestParam("UserNum") String UserNum,@RequestParam("UserPass") String UserPass,@RequestParam("catph") String catph,@RequestParam("longitude")String longitude,@RequestParam("latitude")String latitude) {
        
    	return gansu.GansuPhone1(request,Usercard,UserNum,UserPass,catph,longitude,latitude);
    }
	
	
}
