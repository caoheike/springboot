package com.reptile.contorller;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.reptile.service.SEOandCHSIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

@Controller
@RequestMapping("SEOandCHSI")
public class SEOandCHSIController {
	
	
	@Autowired
	private SEOandCHSIService SEOandCHSIService;
	
	
	//根据企业邮箱查询企业信息
	@ResponseBody
	@RequestMapping(value="SeoEmailFind",method=RequestMethod.POST)
	public Map<String,Object> SeoEmailFind(HttpServletRequest request,@RequestParam("UserEmail") String UserEmail,@RequestParam("UserCard") String UserCard) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		System.out.println("UserEmail==="+UserEmail);
		return SEOandCHSIService.SeoEmailFind(request, UserEmail,UserCard);

   	}
	
	
	/**
	 * 光大银行信用卡账单查询
	 * 1.光大银行页面图片验证码
	 * 2.获取传过来的数据，触发动态密码
	 * 3.获取信用卡账单
	 * @throws Exception 
	 */
	
	//获取登录页面的图像验证码没有参数
	@ResponseBody
	@RequestMapping(value="CabCardloginImage",method=RequestMethod.POST)
	public Map<String,Object> CabCardloginImage(HttpServletRequest request) throws Exception{
		return SEOandCHSIService.CabCardloginImage(request);
	}
	//获得得传过来的数据，触发动态密码，（需要传输身份证号，及图形验证码）
	@ResponseBody
	@RequestMapping(value="CabCardloginPass",method=RequestMethod.POST)
	public Map<String,Object> CabCardloginPass(HttpServletRequest request,@RequestParam("UserCard") String UserCard,@RequestParam("loginImage") String loginImage) throws Exception{
		return SEOandCHSIService.CabCardloginPass(request,UserCard,loginImage);
	}
	//获得信用卡账单 （需要 身份证号 ， 图形验证码信息，短信动态密码）
	@ResponseBody
	@RequestMapping(value="CabCardloginPage",method=RequestMethod.POST)
	public Map<String,Object> CabCardloginPage(HttpServletRequest request,@RequestParam("UserCard") String UserCard,@RequestParam("loginImage") String loginImage,@RequestParam("Password") String Password) throws Exception{
		return SEOandCHSIService.CabCardloginPage(request,UserCard,loginImage,Password);
	}
	
}
