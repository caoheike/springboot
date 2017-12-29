package com.reptile.contorller.socialsecurity;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.socialsecurity.LiuZhouSocialSecurityService;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("LiuZhouSocialSecurityController")
public class LiuZhouSocialSecurityController {
	 	
		@Autowired
	    private LiuZhouSocialSecurityService service;
	 
		
	  	//柳州社保图片验证码
	    @RequestMapping(value = "loadImageCode",method = RequestMethod.POST)
	    @ResponseBody
	    @ApiOperation(value = "加载图片验证码",notes = "参数：无")
	    public Map<String,Object> loadImageCode(HttpServletRequest request){

	        return service.loginImage(request);
	    }
	    @RequestMapping(value = "getDeatilMes",method = RequestMethod.POST)
	    @ResponseBody
	    @ApiOperation(value = "柳州社保",notes = "参数：身份证,姓名，密码，图片验证码,城市编号")
	    public Map<String,Object> getDeatilMes(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("userName")String  userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("socialCard")String socialCard,@RequestParam("idCardNum") String idCardNum){

	        return service.getDeatilMes(request, idCard,catpy, userName,passWord,cityCode,idCardNum);
	    }
}
