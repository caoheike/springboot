package com.reptile.service.socialSecurity;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: LinYiSocialSecurityService  
 * @Description: TODO (临沂社保)
 * @author: xuesongcui
 * @date 2017年12月29日  
 *
 */
@Service
public class LinYiSocialSecurityService {
	private Logger logger= LoggerFactory.getLogger(LinYiSocialSecurityService.class);
	
	@Autowired 
	private application application;
	
	private static String errorCode = "errorCode";
	private static String success = "0000";
	private static String add = "+";
	private static String delete = "-";
	private static String multiplication = "*";
	private static String isEqual = "=";
	
	/**
	 * 临沂社保登录并获取详情
	 * @param request
	 * @param userName
	 * @param idCard
	 * @param cityCode
	 * @return
	 */
	public Map<String, Object> doLogin(HttpServletRequest request,
			String userName, String idCard,String cityCode,String idCardNum) {
		Map<String, Object> data = new HashMap<String, Object>(16);
		WebClient webClient = new WebClientFactory().getWebClient();
		try {
			PushState.state(idCardNum, "socialSecurity", 100);
			//监控alert弹窗
			List<String> alertList = new ArrayList<String>();
			CollectingAlertHandler alert = new CollectingAlertHandler(alertList);
			webClient.setAlertHandler(alert);
			
			HtmlPage loginPage=webClient.getPage("http://www.lyrs.gov.cn/default/xxcx/2/");
			//获取登录表单
			HtmlForm form = (HtmlForm) loginPage.getElementByName("xxcxform");
			
	        HtmlTextInput  username = form.getInputByName("name");
	        username.setValueAttribute(userName);
	        HtmlTextInput identycode = form.getInputByName("identycode");
	        identycode.setValueAttribute(idCard.substring(idCard.length()-8));
	        
	        HtmlTableCell yzcon = (HtmlTableCell) loginPage.getElementById("yzcon");
	        //返回计算结果
	        int checkCode = this.calResult(yzcon.asText());
	        
	        HtmlTextInput  checkCodeInput = form.getInputByName("Checkcode");
	        checkCodeInput.setValueAttribute(checkCode + "");
	        
	        HtmlSubmitInput submit = form.getInputByName("xxcxsubmit");
	        HtmlPage nextPage = (HtmlPage) submit.click();
	        
	        if(alertList.size() > 0){
	        	data.put("errorCode", "0001");
	        	data.put("errorInfo", alertList.get(0));
	        }else{
	        	data.put("errorCode", "0000");
	        	data.put("errorInfo", "登录成功");
	        	data = this.doGetDetail(request, idCard,idCardNum,cityCode,nextPage);
	        }
			
		} catch (Exception e) {
			logger.error("临沂市社保登录失败",e);
			data.put("errorInfo", "系统繁忙，请稍后再试！");
            data.put("errorCode", "0002");
		}finally{
			if(webClient != null){
				webClient.close();
			}
		}
		return data;
	}
	
	
	/**
	 * 临沂社保查询
	 * @param request
	 * @param idCard
	 * @param cityCode
	 * @param nextPage
	 * @return
	 */
	public Map<String, Object> doGetDetail(HttpServletRequest request,
			String idCard,String idCardNum,String cityCode,HtmlPage nextPage) {
		Map<String, Object> data = new HashMap<String, Object>(16);
		HtmlTable table = (HtmlTable) nextPage.getElementsByTagName("table").get(0);
		
		Map<String, Object> infoAll = new HashMap<String, Object>(16);
		infoAll.put("item", table.asXml());
		
		data.put("errorInfo", "查询成功");
        data.put("errorCode", "0000");
        data.put("data", infoAll);
        data.put("city", cityCode);
        data.put("userId", idCardNum);
        data = new Resttemplate().SendMessage(data,application.getSendip()+"/HSDC/person/socialSecurity");
        
        if( data != null && success.equals(data.get(errorCode).toString())){
        	PushState.state(idCardNum, "socialSecurity", 300);
        	data.put("errorInfo","推送成功");
        	data.put("errorCode","0000");
        }else{
        	PushState.state(idCardNum, "socialSecurity", 200);
        	data.put("errorInfo","推送失败");
        	data.put("errorCode","0001");
        }
		return data;
	}
	
	
	/**
	 * 计算字符串的运算结果
	 * @param str
	 * @return
	 */
	public  int calResult(String str){
		str = str.trim().replace(" ", "");
		int a,b,result = 0;
		if(str.contains(add) && str.contains(isEqual)){
			a = Integer.parseInt(str.substring(2,str.indexOf(add)).trim());
			b = Integer.parseInt(str.substring(str.indexOf(add)+1,str.indexOf(isEqual)).trim());
			result = a + b;
		}else if(str.contains(delete) && str.contains(isEqual)){
			a = Integer.parseInt(str.substring(2,str.indexOf(delete)).trim());
			b = Integer.parseInt(str.substring(str.indexOf(delete)+1,str.indexOf(isEqual)).trim());
			result = a - b;
		}else if(str.contains(multiplication) && str.contains(isEqual)){
			a = Integer.parseInt(str.substring(2,str.indexOf(multiplication)).trim());
			b = Integer.parseInt(str.substring(str.indexOf(multiplication)+1,str.indexOf(isEqual)).trim());
			result = a * b;
		}
		return result;
	}
	
	
}
