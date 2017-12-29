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

import com.reptile.service.socialSecurity.LinYiSocialSecurityService;

/**
 * 
 * @ClassName: LinYiSocialSecurityController  
 * @Description: TODO (临沂社保)
 * @author: xuesongcui
 * @date 2017年12月29日  
 *
 */
@Controller
@RequestMapping("linYiSocialSecurity")
public class LinYiSocialSecurityController {
	@Autowired
	private LinYiSocialSecurityService lySocialSecurityService;
	
	
	/**
	 * 临沂市社保详情查询
	 * @param request
	 * @param idCard 身份证号
	 * @return
	 */
	@ApiOperation(value = "临沂市社保：详情")
	@ResponseBody
	@RequestMapping(value = "getDetail", method = RequestMethod.POST)
	public  Map<String,Object> getDetail(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("idCardNum")String idCardNum,@RequestParam("userName")String  userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("socialCard")String socialCard){
		return lySocialSecurityService.doLogin(request, userName, idCard, cityCode,idCardNum);
	}
}
