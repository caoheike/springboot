package com.reptile.service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.ConstantInterface;
import com.reptile.util.Resttemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class SEOandCHSIService {
	
	//根据企业邮箱查询企业信息
	public Map<String,Object> SeoEmailFind(HttpServletRequest request,String UserEmail,String UserCard)  {
		System.out.println("email============="+UserEmail);
		Map<String, Object> map=new HashMap<String, Object>();
		if(UserEmail==null||UserEmail==""){
			map.put("ResultInfo", "企业邮箱信息为不正确，请确认后重写填写");
			map.put("ResultCode","0002");
		}else {
			try {
			String URL= "www"+"."+ UserEmail.substring(UserEmail.indexOf("@")+1);	
			System.out.println("URL===="+URL);
			@SuppressWarnings("resource")
			WebClient webclient=new WebClient(BrowserVersion.FIREFOX_45);
			webclient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
			webclient.getOptions().setTimeout(90000);
			webclient.getOptions().setCssEnabled(false);
			webclient.getOptions().setJavaScriptEnabled(false);
			webclient.setJavaScriptTimeout(40000);
			webclient.getOptions().setRedirectEnabled(true);
			webclient.getOptions().setThrowExceptionOnScriptError(false);
			webclient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webclient.setAjaxController(new NicelyResynchronizingAjaxController());
			webclient.getOptions().setCssEnabled(false);
			HtmlPage page= webclient.getPage("http://seo.chinaz.com/"+URL);
			HtmlDivision division= (HtmlDivision) page.querySelectorAll(".SeoMaWr01Right").get(4);
			Map<String,Object> seo=new HashMap<String, Object>();
			System.out.println(division.asText().trim());
			seo.put("cardNumber",UserCard);
			seo.put("email", UserEmail);
			seo.put("data", division.asText().trim());
			Resttemplate resttemplate = new Resttemplate();
			map=resttemplate.SendMessage(seo, ConstantInterface.port+"HSDC/authcode/companyEmail");
			}
			catch (Exception e) {
				 e.printStackTrace();
				 map.clear();
		         map.put("errorInfo","企业邮箱信息为不正确，请确认后重写填写!");
		         map.put("errorCode","0002");
	        }
		}
		
		System.out.println(map);
		return map;
	}
}
