package com.reptile.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.HtmlVariable;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

public class taobao {
// private static String username="qq1121212159";
// private static String userpwd="weizai..";
 private static String username="318917723@qq.com";
 private static String userpwd="weizai..";
// private static String username="mahongni323";
// private static String userpwd="zhanghuanbin520*";
 
//public static void main(String[] args) throws FailingHttpStatusCodeException, IOException {
//				Map<String,Object> map=new HashMap<String, Object>();
//					WebClient webClient = new WebClient();
//			 webClient.getOptions().setUseInsecureSSL(true);
//			 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
//			 webClient.getOptions().setTimeout(100000);
//			 webClient.getOptions().setCssEnabled(false);
//			 webClient.getOptions().setJavaScriptEnabled(true);
//			 webClient.setJavaScriptTimeout(100000); 
//			 webClient.getOptions().setRedirectEnabled(true);
//			 webClient.getOptions().setThrowExceptionOnScriptError(false);
//			 webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
//			 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//			 WebRequest webRequest=new  WebRequest(new java.net.URL("https://login.taobao.com/member/login.jhtml"));
//			 webClient.addRequestHeader("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:30.0) Gecko/20100101 Firefox/30.0");
//			 webClient.addRequestHeader("accept", "*/*");
//			 webClient.addRequestHeader("accept-language", "zh-CN");
//			 webClient.addRequestHeader("Accept-Encoding", "gzip, deflate");
//			 List<NameValuePair> list=new ArrayList<NameValuePair>();
//			 list.add(new NameValuePair("TPL_password",userpwd));
//			 list.add(new NameValuePair("TPL_username",username));
//			 list.add(new NameValuePair("newlogin", "1"));
//			 list.add(new NameValuePair("callback", "1"));
//		
//			 webRequest.setHttpMethod(HttpMethod.POST);
//			 webRequest.setRequestParameters(list);
//			 HtmlPage pagess=webClient.getPage(webRequest);
//			  System.out.println(pagess.asXml());
//				 String token=pagess.asXml().substring(pagess.asXml().indexOf("token")).split("&")[0].toString().replaceAll("token=", "");
//				 String token2=pagess.asXml().substring(pagess.asXml().indexOf("token",pagess.asXml().indexOf("token")+1)).split("&")[0].toString().replaceAll("token=", "");
//				 HtmlPage page= webClient.getPage("https://passport.alibaba.com/mini_apply_st.js?site=0&token="+token+"&callback=callback");
//				 System.out.println(page.asXml());
//				 HtmlPage page2= webClient.getPage("https://passport.alibaba.com/mini_apply_st.js?site=0&token="+token2+"&callback=callback");
//				 System.out.println(page2.asXml());
//				 HtmlPage pagev=webClient.getPage("https://login.taobao.com/member/login.jhtml?redirectURL=http%3A%2F%2Fwww.taobao.com%2F");
//				 System.out.println(pagev.asXml());
//				webClient.getPage("https://login.taobao.com/member/vst.htm?st="+""+"&params=style%3Dminisimple%26sub%3Dtrue%26TPL_username%3D"+"胡献根"+"%26loginsite%3D0%26from_encoding%3D%26not_duplite_str%3D%26guf%3D%26full_redirect%3D%26isIgnore%3D%26need_sign%3D%26sign%3D%26from%3Ddatacube%26TPL_redirect_url%3Dhttp%25253A%25252F%25252Fmofang.taobao.com%25252Fs%25252Flogin%26css_style%3D%26allp%3D&_ksTS=1404787873165_78&callback=jsonp79");
//				HtmlPage enpage=webClient.getPage("http://trade.taobao.com/trade/itemlist/list_bought_items.htm?spm=1.7274553.1997525045.2.C6QtVd");
//				 HtmlPage pagea= webClient.getPage("https://member1.taobao.com/member/fresh/deliver_address.htm?addrId=5874841844");
//				 HtmlTable table=(HtmlTable) pagea.querySelectorAll(".tbl-main").get(0);
//			System.out.println(table.asXml());
//			
// 
//}
 
 public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		
	 Resttemplate resttemplate=new Resttemplate();
	 	CrawlerUtil craw=new CrawlerUtil();
	 	Map<String,Object> map=new HashMap<String, Object>();
	 	Map<String,Object> data=new HashMap<String, Object>();
	 	WebClient webClient = new WebClient();
		 webClient.getOptions().setUseInsecureSSL(true);
		 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
		 webClient.getOptions().setTimeout(100000);
		 webClient.getOptions().setCssEnabled(false);
		 webClient.getOptions().setJavaScriptEnabled(true);
		 webClient.setJavaScriptTimeout(100000); 
		 webClient.getOptions().setRedirectEnabled(true);
		 webClient.getOptions().setThrowExceptionOnScriptError(false);
		 webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
		 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		HtmlPage page= webClient.getPage("http://login.189.cn/login");
		HtmlTextInput txe=(HtmlTextInput) page.getElementById("txtAccount");
		HtmlPasswordInput txepasswprd=(HtmlPasswordInput) page.getElementById("txtPassword");
		//txe.setValueAttribute("17791309689");
		txe.setValueAttribute("17791309686");
		txepasswprd.setValueAttribute("585819");
		HtmlPage loginpage=(HtmlPage) page.executeJavaScript("$('#loginbtn').click();").getNewPage();
		Thread.sleep(7000);
	    HtmlDivision htmlform=  (HtmlDivision) loginpage.getElementById("divErr");
		System.out.println(htmlform.asText());
		if(htmlform.asText().contains("请输入验证码")){
    		map.put("errorCode","0001");
	    	map.put("errorInfo","该帐号已被锁定，请您明天再来尝试");
    	}else{
    		map.put("errorCode","0001");
	    	map.put("errorInfo","帐号或密码错误");
    	}
		System.out.println(map.toString());
//		
//		
//		   HtmlPage logi=webClient.getPage("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000202");
//			  
//		 	WebRequest webRequest=new WebRequest(new URL("http://sn.189.cn/service/bill/feeDetailrecordList.action"));
//		 	 List<NameValuePair> reqParamsinfo = new ArrayList<NameValuePair>();  
//		 	reqParamsinfo.add(new NameValuePair("currentPage","1"));
//		 	reqParamsinfo.add(new NameValuePair("pageSize","10"));
//		 	reqParamsinfo.add(new NameValuePair("effDate","2017-05-01"));
//		 	reqParamsinfo.add(new NameValuePair("expDate","2017-07-07"));
//		 	reqParamsinfo.add(new NameValuePair("serviceNbr","17791309689"));
//		 	reqParamsinfo.add(new NameValuePair("operListID","1"));
//		 	reqParamsinfo.add(new NameValuePair("isPrepay","0"));
//		 	reqParamsinfo.add(new NameValuePair("pOffrType","481"));
//		    webRequest.setHttpMethod(HttpMethod.POST);
//		    webRequest.setRequestParameters(reqParamsinfo);
//		    List<String> list=new ArrayList<String>();
//		    
//		    HtmlPage  Infopage=webClient.getPage(webRequest);
//		    HtmlTable htmlTable=(HtmlTable) Infopage.getByXPath("//table").get(0);
//		   Document doc = Jsoup.parse(htmlTable.asXml());
////	        Elements trs = doc.select("table").select("tr");
////	        for(int i = 0;i<trs.size();i++){
////	            Elements tds = trs.get(i).select("td");
////	            for(int j = 0;j<tds.size();j++){
////	                String text = tds.get(j).text();
////	                System.out.println(text);
////	               list.add(text);
////	         
////	            }
////	        }
//	    
//	        // data.put("info", htmlTable.asXml().replace("100%","50%").replace("mt10 transact_tab","testv"));
//		   data.put("info", htmlTable.asXml());
//	        map.put("data", data);
////		   	map.put("errorCode","0000");
////	    	map.put("errorInfo","成功");
//	       	map.put("UserIphone","17791309689");
//	    	map.put("UserPassword","585819");
//	    	map=resttemplate.SendMessage(map, "http://124.89.33.70:8082/HSDC/authcode/callRecordTelecom");
//	    	System.out.println(map.toString());
//		
}
 
// public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
//		WebClient webClient = new WebClient();
//		 webClient.getOptions().setUseInsecureSSL(true);
//		 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
//		 webClient.getOptions().setTimeout(100000);
//		 webClient.getOptions().setCssEnabled(false);
//		 webClient.getOptions().setJavaScriptEnabled(true);
//		 webClient.setJavaScriptTimeout(100000); 
//		 webClient.getOptions().setRedirectEnabled(true);
//		 webClient.getOptions().setThrowExceptionOnScriptError(false);
//		 webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
//		 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//		 HtmlPage page= webClient.getPage("http://www.gsxt.gov.cn/index.html");
//		 HtmlTextInput htmlTextInput= (HtmlTextInput) page.getElementById("keyword");
//		 htmlTextInput.setValueAttribute("asd");
//		 HtmlButton button=(HtmlButton) page.getElementById("btn_query");
//		 HtmlPage pageinfo= button.click();
//		 Thread.sleep(4000);
//		 System.out.println(pageinfo.asText());
//		 
//}
}
