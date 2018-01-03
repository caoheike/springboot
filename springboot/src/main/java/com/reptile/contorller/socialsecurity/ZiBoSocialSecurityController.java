package com.reptile.contorller.socialsecurity;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.accumulationfund.KunMingFundService;
import com.reptile.service.socialsecurity.ZiBoSocialSecuritySercice;
/**
 * 
 * @ClassName: ZiBoSocialSecurityController  
 * @Description: TODO  
 * @author: 111
 * @date 2018年1月2日  
 *
 */
@Controller
@RequestMapping("ZiBoSocialSecurityController")
public class ZiBoSocialSecurityController {
	@Autowired
    private ZiBoSocialSecuritySercice service;
	@ApiOperation(value = "1.淄博社保获取图形验证码", notes = "参数：")
    @ResponseBody
    @RequestMapping(value = "ZBSImageCode", method = RequestMethod.POST)
	 public Map<String, Object> getImageCode(HttpServletRequest request,@RequestParam("passWord")String passWord){
		return service.getImageCode(request,passWord);
	}
	@ApiOperation(value = "1.淄博社保获取详单", notes = "参数：身份证，密码，图形验证码")
    @ResponseBody
    @RequestMapping(value = "ZBSDetail", method = RequestMethod.POST)
	public  Map<String, Object> getDetail(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("idCardNum")String idCardNum,@RequestParam("userName")String  userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("socialCard")String socialCard){
		
		return service.getDetails(request, idCard,catpy,idCardNum);
		
	}
}
