package com.reptile.contorller.chinatelecom;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.chinatelecom.GansuProvinceService;

import io.swagger.annotations.ApiOperation;
/**
 * 
 * @author liubin
 *
 */
@Controller
@RequestMapping("GansuProvinceTelecomController")
public class GansuProvinceTelecomController {
	@Autowired
	private GansuProvinceService gansu;
//    @ResponseBody
//    @ApiOperation(value = "1.短信验证码", notes = "参数：手机号")
//    @RequestMapping(value = "GansuPhoneCode", method = RequestMethod.POST)
//    public Map<String, Object> gansuPhoneCode(HttpServletRequest request,@RequestParam("UserNum") String userNum) {
//        return gansu.gansuPhone(request,userNum);
//    }
    @ApiOperation(value = "2.获取详单信息", notes = "参数：身份证,手机号,服务密码")
    @ResponseBody
    @RequestMapping(value = "GansuPhone", method = RequestMethod.POST)
    public Map<String, Object> gansuPhone(HttpServletRequest request,@RequestParam("Usercard") String usercard,@RequestParam("UserNum") String userNum,@RequestParam("UserPass") String userPass,@RequestParam("longitude")String longitude,@RequestParam("latitude")String latitude,@RequestParam("UUID")String uuid) {
        
    	return gansu.gansuPhone1(request,usercard,userNum,userPass,longitude,latitude,uuid);
    }
	
	
}
