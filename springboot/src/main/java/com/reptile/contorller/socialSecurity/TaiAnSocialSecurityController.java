package com.reptile.contorller.socialSecurity;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.socialSecurity.TaiAnSocialSecurityService;

@Controller
@RequestMapping("TaiAnSocialSecurityController")
public class TaiAnSocialSecurityController {
	@Autowired
    private TaiAnSocialSecurityService service;
	@ApiOperation(value = "1.泰安社保获取图形验证码", notes = "参数：")
    @ResponseBody
    @RequestMapping(value = "TASImageCode", method = RequestMethod.POST)
	 public Map<String, Object> getImageCode(HttpServletRequest request,@RequestParam("passWord")String passWord){
		return service.getImageCode(request,passWord);
	}
	@ApiOperation(value = "1.泰安社保获取详单", notes = "参数：身份证，密码，图形验证码")
    @ResponseBody
    @RequestMapping(value = "TASDetail", method = RequestMethod.POST)
	public  Map<String, Object> getDetail(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("catpy")String catpy){
		
		return service.getDetails(request, idCard,catpy);
		
	}

}
