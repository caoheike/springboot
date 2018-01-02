package com.reptile.contorller.accumulationfund;

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
/**
 * 
 * @ClassName: KunMingFundController  
 * @Description: TODO  
 * @author: 111
 * @date 2018年1月2日  
 *
 */
@Controller
@RequestMapping("KunMingFundController")
public class KunMingFundController {
	@Autowired
    private KunMingFundService service;
	@ApiOperation(value = "1.昆明获取图形验证码", notes = "参数：")
    @ResponseBody
    @RequestMapping(value = "KMFImageCode", method = RequestMethod.POST)
	 public Map<String, Object> getImageCode(HttpServletRequest request){
		return service.getImageCode(request);
	}
	@ApiOperation(value = "1.昆明获取详单", notes = "参数：身份证，密码，图形验证码")
    @ResponseBody
    @RequestMapping(value = "KMFDetail", method = RequestMethod.POST)
	public  Map<String, Object> getDetail(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("idCardNum")String idCardNum,@RequestParam("userName")String userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("fundCard")String fundCard){
		
		return service.getDetail(request, idCard, passWord, catpy,cityCode,idCardNum);
		
	}
    

}
