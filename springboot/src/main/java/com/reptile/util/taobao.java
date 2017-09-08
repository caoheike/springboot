package com.reptile.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



import com.gargoylesoftware.htmlunit.BrowserVersion;	
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

public class taobao {
private static String username="qq1121212159";
//private static String username="13649291630";
private static String userpwd="weizai..";
//private static String username="tb783371_00";
 //private static String userpwd="w369852";
// private static String username="mahongni323";
// private static String userpwd="zhanghuanbin520*";

	private static CrawlerUtil crawlerUtil = new CrawlerUtil();
public static void main(String[] args) throws FailingHttpStatusCodeException, IOException, InterruptedException {
	Resttemplate resttemplate=new Resttemplate();
				Map<String,Object> map=new HashMap<String, Object>();
				Map<String,Object> data=new HashMap<String, Object>();
				boolean flg=false;
					int count=0;
			WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);
			 webClient.getOptions().setUseInsecureSSL(true);
			 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
			 webClient.getOptions().setTimeout(100000);
			 webClient.getOptions().setCssEnabled(true);
			 webClient.getOptions().setJavaScriptEnabled(true);
			 webClient.setJavaScriptTimeout(100000); 
			 webClient.getOptions().setRedirectEnabled(true);
			    webClient.waitForBackgroundJavaScript(10000);
			 webClient.getOptions().setThrowExceptionOnScriptError(false);
			 webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
			 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			 webClient.getOptions().setActiveXNative(true);
			 Set<Cookie> cookies = webClient.getCookieManager().getCookies();
		 for (Cookie c : cookies) {
		 webClient.getCookieManager().addCookie(c);
		 System.out.println(c);
		 }
			 WebRequest webRequests=new  WebRequest(new java.net.URL("https://login.taobao.com/member/request_nick_check.do?_input_charset=utf-8"));
		
			 List<NameValuePair> lists=new ArrayList<NameValuePair>();
			 lists.add(new NameValuePair("username","qq1121212159"));
			 webRequests.setHttpMethod(HttpMethod.POST);
			 webRequests.setRequestParameters(lists);
			 UnexpectedPage pagetxt= webClient.getPage(webRequests);

			 if(!pagetxt.getWebResponse().getContentAsString().contains("false")){
				 
		
						 WebRequest webRequest=new  WebRequest(new java.net.URL("https://login.taobao.com/member/login.jhtml"));
						 webClient.addRequestHeader("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:30.0) Gecko/20100101 Firefox/30.0");
						 webClient.addRequestHeader("accept", "*/*");
						 webClient.addRequestHeader("accept-language", "zh-CN");
						 webClient.addRequestHeader("Accept-Encoding", "gzip, deflate");
						 List<NameValuePair> list=new ArrayList<NameValuePair>();
						 list.add(new NameValuePair("TPL_password",userpwd));
						 list.add(new NameValuePair("TPL_username",username));
						 list.add(new NameValuePair("newlogin", "1"));
						 list.add(new NameValuePair("callback", "1"));
					
						 webRequest.setHttpMethod(HttpMethod.POST);
						 webRequest.setRequestParameters(list);
						 HtmlPage pagess=webClient.getPage(webRequest);
	 					 if(pagess.getTitleText().equals("页面跳转中")){
							 System.out.println("进来了");
							 System.out.println(pagess.asXml());
						 }else{

							 	if(pagess.asXml().contains("请按住滑块，拖动到最右边")){
							 		System.out.println("可以破解");
							 		//执行滑动JS
							 		HtmlPage result= (HtmlPage) pagess.executeJavaScript("setTimeout(function () {  var event = document.createEvent('MouseEvents');event.initMouseEvent('mousedown', true, true, document.defaultView,0,0,0,0,0, false, false, false, false, 11 ,null); nc_1_n1z.dispatchEvent(event);document.getElementById('nc_1__bg').style.width='258px';document.getElementById('nc_1_n1z').style.left='258px';var event = document.createEvent('MouseEvents');event.initMouseEvent('mousemove', true, true, document.defaultView, 0,0,0, 290,290, false, false, false, false,0,null);nc_1_n1z.dispatchEvent(event);}, 5000);").getNewPage();
									 webClient.setJavaScriptTimeout(100000); 
							 		
							 		Thread.sleep(10000);
							 		System.out.println(result.asXml());
							 		System.out.println(result.getUrl());
							 
								 //获得密码狂输入
//									HtmlPasswordInput pagesy= (HtmlPasswordInput) pageyz.getElementById("TPL_password_1");
//							 		pagesy.setValueAttribute("weizai..");
//							 		//获得按钮提交
//									HtmlButton  button=(HtmlButton) pageyz.getElementById("J_SubmitStatic");
//									HtmlPage pagev= button.click();
//									Thread.sleep(3000);
//									System.out.println(pagev.asXml());
//									
							 		
							 	}else{
							 		System.out.println("找不到拖动验证码");
							 	}
							
								 
							 }

							 if(count==3&&flg==true){
								 System.out.println("读取出错");
							 }
		
						 
			 }else{
				 System.out.println("需要验证码");
			 }
			
				 
		
			
			
 
}
// 
// public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
//		
//	 Resttemplate resttemplate=new Resttemplate();
//	 	CrawlerUtil craw=new CrawlerUtil();
//	 	Map<String,Object> map=new HashMap<String, Object>();
//	 	Map<String,Object> data=new HashMap<String, Object>();
//	 	WebClient webClient = new WebClient();
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
//		HtmlPage page= webClient.getPage("http://login.189.cn/login");
//		HtmlTextInput txe=(HtmlTextInput) page.getElementById("txtAccount");
//		HtmlPasswordInput txepasswprd=(HtmlPasswordInput) page.getElementById("txtPassword");
//		//txe.setValueAttribute("17791309689");
//		txe.setValueAttribute("17791309686");
//		txepasswprd.setValueAttribute("585819");
//		HtmlPage loginpage=(HtmlPage) page.executeJavaScript("$('#loginbtn').click();").getNewPage();
//		Thread.sleep(7000);
//	    HtmlDivision htmlform=  (HtmlDivision) loginpage.getElementById("divErr");
//		System.out.println(htmlform.asText());
//		if(htmlform.asText().contains("请输入验证码")){
//    		map.put("errorCode","0001");
//	    	map.put("errorInfo","该帐号已被锁定，请您明天再来尝试");
//    	}else{
//    		map.put("errorCode","0001");
//	    	map.put("errorInfo","帐号或密码错误");
//    	}
//		System.out.println(map.toString());
////		
////		
////		   HtmlPage logi=webClient.getPage("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000202");
////			  
////		 	WebRequest webRequest=new WebRequest(new URL("http://sn.189.cn/service/bill/feeDetailrecordList.action"));
////		 	 List<NameValuePair> reqParamsinfo = new ArrayList<NameValuePair>();  
////		 	reqParamsinfo.add(new NameValuePair("currentPage","1"));
////		 	reqParamsinfo.add(new NameValuePair("pageSize","10"));
////		 	reqParamsinfo.add(new NameValuePair("effDate","2017-05-01"));
////		 	reqParamsinfo.add(new NameValuePair("expDate","2017-07-07"));
////		 	reqParamsinfo.add(new NameValuePair("serviceNbr","17791309689"));
////		 	reqParamsinfo.add(new NameValuePair("operListID","1"));
////		 	reqParamsinfo.add(new NameValuePair("isPrepay","0"));
////		 	reqParamsinfo.add(new NameValuePair("pOffrType","481"));
////		    webRequest.setHttpMethod(HttpMethod.POST);
////		    webRequest.setRequestParameters(reqParamsinfo);
////		    List<String> list=new ArrayList<String>();
////		    
////		    HtmlPage  Infopage=webClient.getPage(webRequest);
////		    HtmlTable htmlTable=(HtmlTable) Infopage.getByXPath("//table").get(0);
////		   Document doc = Jsoup.parse(htmlTable.asXml());
//////	        Elements trs = doc.select("table").select("tr");
//////	        for(int i = 0;i<trs.size();i++){
//////	            Elements tds = trs.get(i).select("td");
//////	            for(int j = 0;j<tds.size();j++){
//////	                String text = tds.get(j).text();
//////	                System.out.println(text);
//////	               list.add(text);
//////	         
//////	            }
//////	        }
////	    
////	        // data.put("info", htmlTable.asXml().replace("100%","50%").replace("mt10 transact_tab","testv"));
////		   data.put("info", htmlTable.asXml());
////	        map.put("data", data);
//////		   	map.put("errorCode","0000");
//////	    	map.put("errorInfo","成功");
////	       	map.put("UserIphone","17791309689");
////	    	map.put("UserPassword","585819");
////	    	map=resttemplate.SendMessage(map, "http://124.89.33.70:8082/HSDC/authcode/callRecordTelecom");
////	    	System.out.println(map.toString());
////		
//}
 
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
	
//	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
////		System.setProperty("webdriver.firefox.bin", "C:/chme/chromedriver_x64.exe");  
//		WebDriver driver=new HtmlUnitDriver();
//		 driver.get("http://www.baidu.com");
//		<dependency>
//    <groupId>org.seleniumhq.selenium</groupId>
//    <artifactId>selenium-java</artifactId>
//    <version>2.44.0</version>
//</dependency>  
//	}
}
