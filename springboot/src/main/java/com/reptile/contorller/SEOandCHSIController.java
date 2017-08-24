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
}
