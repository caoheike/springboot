package com.reptile.service.socialSecurity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;

@Service
public class LYSocialSecurityService {
	private Logger logger= LoggerFactory.getLogger(LYSocialSecurityService.class);
	public final static String LINYI_CITY_CODE = "";
	
	
	/**
	 * 临沂社保登录
	 * @param request
	 * @param userName
	 * @param idCard
	 * @return
	 */
	public Map<String, Object> lyLogin(HttpServletRequest request,
			String userName, String idCard) {
		Map<String, Object> data = new HashMap<String, Object>();
		WebClient webClient = new WebClientFactory().getWebClient();
		try {
			
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
	        int checkcode = this.calResult(yzcon.asText());//返回计算结果
	        
	        HtmlTextInput  Checkcode = form.getInputByName("Checkcode");
	        Checkcode.setValueAttribute(checkcode + "");
	        
	        HtmlSubmitInput submit = form.getInputByName("xxcxsubmit");
	        HtmlPage nextPage = (HtmlPage) submit.click();
	        
	        if(alertList.size() > 0){
	        	data.put("errorCode", "0001");
	        	data.put("errorInfo", alertList.get(0));
	        }else{
	        	data.put("errorCode", "0000");
	        	data.put("errorInfo", "登录成功");
	        	request.getSession().setAttribute("detailPage",nextPage); 
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
	 * @return
	 */
	public Map<String, Object> lyGetDetail(HttpServletRequest request,
			String idCard,String cityCode) {
		Map<String, Object> data = new HashMap<String, Object>();
		
		HtmlPage  detailPage = (HtmlPage)request.getSession().getAttribute("detailPage");
		
		if(detailPage == null){
			data.put("errorInfo", "系统繁忙，请稍后再试！");
            data.put("errorCode", "0002");
            return data;
		}
		
		HtmlTable table = (HtmlTable) detailPage.getElementsByTagName("table").get(0);
		
		Map<String, Object> infoAll = new HashMap<String, Object>();
		infoAll.put("item", table.asXml());
		
		data.put("errorInfo", "查询成功");
        data.put("errorCode", "0000");
        data.put("data", infoAll);
        data.put("city", cityCode);
        data.put("userId", idCard);
//      data = new Resttemplate().SendMessage(data,application.getSendip()+"/HSDC/person/socialSecurity");//上线
        data = new Resttemplate().SendMessage(data,"http://192.168.3.16:8089/HSDC/person/socialSecurity");
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
		if(str.contains("+") && str.contains("=")){
			a = Integer.parseInt(str.substring(2,str.indexOf("+")).trim());
			b = Integer.parseInt(str.substring(str.indexOf("+")+1,str.indexOf("=")).trim());
			result = a + b;
		}else if(str.contains("-") && str.contains("=")){
			a = Integer.parseInt(str.substring(2,str.indexOf("-")).trim());
			b = Integer.parseInt(str.substring(str.indexOf("-")+1,str.indexOf("=")).trim());
			result = a - b;
		}else if(str.contains("*") && str.contains("=")){
			a = Integer.parseInt(str.substring(2,str.indexOf("*")).trim());
			b = Integer.parseInt(str.substring(str.indexOf("*")+1,str.indexOf("=")).trim());
			result = a * b;
		}
		return result;
	}
	
	
}