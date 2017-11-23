package com.reptile.service.socialSecurity;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class TaiAnSocialSecurityService {
	private Logger logger= LoggerFactory.getLogger(TaiAnSocialSecurityService.class);
	 @Autowired
	  private application applications;
	 public Map<String, Object> getImageCode(HttpServletRequest request,String idCard,String passWord,String cityCode,String idCardNum){
		  Map<String, Object> map = new HashMap<String, Object>();
	      Map<String, Object> dateMap = new HashMap<String, Object>();
		  List<Object> dataList = new ArrayList<Object>();
		 
	        HttpSession session = request.getSession();
	        
	        WebClient webClient=new WebClientFactory().getWebClient();
	        //=============图形验证码=====================
	        try {
	        	
				HtmlPage page=webClient.getPage(new URL("http://124.130.146.14:8002/hso/logon_370900.jsp"));	
				Thread.sleep(200);
				//监控alert弹窗
	 			List<String> alertList = new ArrayList<String>();
	 			CollectingAlertHandler alert = new CollectingAlertHandler(alertList);
	 			webClient.setAlertHandler(alert);
					 
						page.executeJavaScript("alert(hex_md5('"+passWord+"'))");
						
						String password="";
						if(alertList.size() > 0){
							password = alertList.get(0).toString();//加密后的密码
							
						}else{
							logger.warn("泰安市社保--系统繁忙，请稍后再试！");
							map.put("errorInfo", "系统繁忙，请稍后再试！");
							map.put("errorCode", "0001");
				            return map;
						}
					  
			 UnexpectedPage page7 = webClient.getPage("http://124.130.146.14:8002/hso/genAuthCode2?_="+Math.random());

            BufferedImage bi = ImageIO.read(page7.getInputStream());
            String findImage = "ta" + System.currentTimeMillis() + ".png";
			ImageIO.write(bi, "png", new File("C:\\Shimage", findImage));
			//2.转码
			Map<String, Object> imagev = MyCYDMDemo.Imagev("C:\\Shimage\\" + findImage);
			String catpy1 = (String) imagev.get("strResult");//转码后的动态码

			
			  try {
				  PushState.state(idCardNum, "socialSecurity", 100);
				  
				    List<NameValuePair> list1=new ArrayList<NameValuePair>();
		            list1.add(new NameValuePair("method","doLogonAllowRepeat"));
		            list1.add(new NameValuePair("usertype","1"));
		           
		            list1.add(new NameValuePair("username",idCard));
		            list1.add(new NameValuePair("password",password));
		            list1.add(new NameValuePair("validatecode",catpy1));
		            list1.add(new NameValuePair("appversion","1.1.84"));
		            HtmlPage page2 =this.getPages(webClient, "http://124.130.146.14:8002/hso/logon.do", list1, HttpMethod.POST);
					Thread.sleep(100);
				  //  System.out.println(page2.asXml());	
					if(page2.asText().contains("true")){
						logger.warn("泰安市社保基本信息获取中");
						//=================获取基本信息=================
				  HtmlPage page3=webClient.getPage("http://124.130.146.14:8002/hso/hsoPer.do?method=enterPerHome");
						
						HtmlElement nameTip=(HtmlElement) page3.getElementById("announcementBody");
						String name=nameTip.asText().split("您")[0].split(" ")[1].replace(",", "");
						logger.warn("泰安市社保基本信息获取完成");
						//=================详细信息============
						logger.warn("泰安市社保详细信息获取中");
						    List<NameValuePair> list2=new ArrayList<NameValuePair>();
				            list2.add(new NameValuePair("method","queryZgYanglaozh"));
				            list2.add(new NameValuePair("__logon_ticket","null"));
						    HtmlPage detail=this.getPages(webClient, "http://124.130.146.14:8002/hso/persi.do", list2, HttpMethod.POST);
						    if(detail.asXml().contains("select")){
						    	HtmlSelect select= (HtmlSelect) detail.getElementById("nd");
						    	int num=select.getChildElementCount();//获取年份
						    	String nd="";
						    	String result="";
						    	int tableNum=0;
						    	HtmlElement table1=null;
						    	for (int i = 0; i < num-1; i++) {
						    		nd=select.getOption(1+i).getAttribute("value");
						    		List<NameValuePair> list3=new ArrayList<NameValuePair>();
						            list3.add(new NameValuePair("method","queryZgYanglaozh"));
						            list3.add(new NameValuePair("_xmlString","<?xml version='1.0' encoding='UTF-8'?><p><s nd='"+nd+"'/></p>"));
						            list3.add(new NameValuePair("__logon_ticket","null"));
						            detail=this.getPages(webClient, "http://124.130.146.14:8002/hso/persi.do", list3, HttpMethod.POST);
						    		System.out.println(detail.asXml());
						    	   tableNum=detail.getElementsByTagName("table").size();
						    		
						    	   table1= (HtmlElement) detail.getElementsByTagName("table").get(0);
						           HtmlElement table3= (HtmlElement) detail.getElementsByTagName("table").get(1);
						           String tabl2="";
						           for (int j = tableNum-1; j>0; j--) {
						        	   HtmlElement table2= (HtmlElement) detail.getElementsByTagName("table").get(j);
						        	   if(table2.asXml().contains("dataTableCell")){
						        		
						        		   tabl2=table2.asXml()+tabl2;
						        	   }
								   }
						           
						        
						           if(!tabl2.equals("")){
						        	   dataList.add(tabl2);   
						           }
						           
								}
						    	logger.warn("泰安市社保信息获取完成");
						    	dataList.add(table1.asXml());
						    	dateMap.put("item", dataList);
						    	map.put("name", name);//TODO
						    	map.put("errorInfo", "查询成功");
								map.put("errorCode", "0000");
								map.put("data", dateMap);
								map.put("city", cityCode);//007
								map.put("userId", idCardNum);//TODO
								map = new Resttemplate().SendMessage(map,applications.getSendip()+"/HSDC/person/socialSecurity");
								if(map!=null&&"0000".equals(map.get("errorCode").toString())){
						          	PushState.state(idCardNum, "socialSecurity", 300);
						          	map.put("errorInfo","推送成功");
						          	map.put("errorCode","0000");
						          }else{
						          	PushState.state(idCardNum, "socialSecurity", 200);
						          	map.put("errorInfo","推送失败");
						          	map.put("errorCode","0001");
						          }
						    }else{
						    	logger.warn("泰安市社保信息获取工程中出错");
						    	  map.put("errorCode", "0001");
						          map.put("errorInfo", "网络连接异常!");
						          return map;
						    }		
						    
					}else{
						logger.warn("泰安市社保信息获取登陆失败");
						 map.put("errorCode", "0001");
						 if(page2.asText().contains("未生成验证码")){
					     map.put("errorInfo", "网路繁忙，请重试");	 
						 }else{
							 map.put("errorInfo", page2.asText()); 
						 }
						 return map;	   	
					}
				  
			 }catch (Exception e) {
				 logger.warn("泰安市社保",e); 
				map.put("errorCode", "0001");
	           map.put("errorInfo", "网络连接异常!");
				e.printStackTrace();
			 }
		    	
//            map.put("errorCode", "0000");
//            map.put("errorInfo", "验证码获取成功");
//            session.setAttribute("TA-WebClient", webClient);
//            session.setAttribute("GMpassWord", password);
	        }catch (Exception e) {
	        	logger.warn("泰安市社保",e);
        		map.put("errorCode", "0001");
               map.put("errorInfo", "网络连接异常!");
   			e.printStackTrace();
			}
		return map; 
	  }
	 
//	 public  Map<String, Object> getDetails(HttpServletRequest request,String idCard,String catpy){
//		
//		  Map<String, Object> map = new HashMap<String, Object>();
//		  Map<String, Object> dateMap = new HashMap<String, Object>();
//		  List<Object> dataList = new ArrayList<Object>();
//		 
//		  HttpSession session = request.getSession();
//
//	        Object client = session.getAttribute("TA-WebClient");
//	        if (client == null) {
//	        	logger.warn("泰安市社保,请先获取图形验证码");
//	            map.put("errorCode", "0001");
//	            map.put("errorInfo", "请先获取图形验证码");
//	            return map;
//	        } else { 
//		  try {
//			
//			  WebClient webClient = (WebClient) client;
//			
//			   String password=(String) session.getAttribute("GMpassWord");
//			    List<NameValuePair> list1=new ArrayList<NameValuePair>();
//	            list1.add(new NameValuePair("method","doLogonAllowRepeat"));
//	            list1.add(new NameValuePair("usertype","1"));
//	           
//	            list1.add(new NameValuePair("username",idCard));
//	            list1.add(new NameValuePair("password",password));
//	            list1.add(new NameValuePair("validatecode",catpy));
//	            list1.add(new NameValuePair("appversion","1.1.84"));
//	            HtmlPage page2 =this.getPages(webClient, "http://124.130.146.14:8002/hso/logon.do", list1, HttpMethod.POST);
//				Thread.sleep(100);
//			  //  System.out.println(page2.asXml());	
//				if(page2.asText().contains("true")){
//					logger.warn("泰安市社保基本信息获取中");
//					//=================获取基本信息=================
//			  HtmlPage page3=webClient.getPage("http://124.130.146.14:8002/hso/hsoPer.do?method=enterPerHome");
//					
//					HtmlElement nameTip=(HtmlElement) page3.getElementById("announcementBody");
//					String name=nameTip.asText().split("您")[0].split(" ")[1].replace(",", "");
//					logger.warn("泰安市社保基本信息获取完成");
//					//=================详细信息============
//					logger.warn("泰安市社保详细信息获取中");
//					    List<NameValuePair> list2=new ArrayList<NameValuePair>();
//			            list2.add(new NameValuePair("method","queryZgYanglaozh"));
//			            list2.add(new NameValuePair("__logon_ticket","null"));
//					    HtmlPage detail=this.getPages(webClient, "http://124.130.146.14:8002/hso/persi.do", list2, HttpMethod.POST);
//					    if(detail.asXml().contains("select")){
//					    	HtmlSelect select= (HtmlSelect) detail.getElementById("nd");
//					    	int num=select.getChildElementCount();//获取年份
//					    	String nd="";
//					    	String result="";
//					    	int tableNum=0;
//					    	HtmlElement table1=null;
//					    	for (int i = 0; i < num-1; i++) {
//					    		nd=select.getOption(1+i).getAttribute("value");
//					    		List<NameValuePair> list3=new ArrayList<NameValuePair>();
//					            list3.add(new NameValuePair("method","queryZgYanglaozh"));
//					            list3.add(new NameValuePair("_xmlString","<?xml version='1.0' encoding='UTF-8'?><p><s nd='"+nd+"'/></p>"));
//					            list3.add(new NameValuePair("__logon_ticket","null"));
//					            detail=this.getPages(webClient, "http://124.130.146.14:8002/hso/persi.do", list3, HttpMethod.POST);
//					    		System.out.println(detail.asXml());
//					    	   tableNum=detail.getElementsByTagName("table").size();
//					    		
//					    	   table1= (HtmlElement) detail.getElementsByTagName("table").get(0);
//					           HtmlElement table3= (HtmlElement) detail.getElementsByTagName("table").get(1);
//					           String tabl2="";
//					           for (int j = tableNum-1; j>0; j--) {
//					        	   HtmlElement table2= (HtmlElement) detail.getElementsByTagName("table").get(j);
//					        	   if(table2.asXml().contains("dataTableCell")){
//					        		
//					        		   tabl2=table2.asXml()+tabl2;
//					        	   }
//							   }
//					           
//					        
//					           if(!tabl2.equals("")){
//					        	   dataList.add(tabl2);   
//					           }
//					           
//							}
//					    	logger.warn("泰安市社保信息获取完成");
//					    	dataList.add(table1.asXml());
//					    	dateMap.put("item", dataList);
//					    	map.put("name", name);//TODO
//					    	map.put("errorInfo", "查询成功");
//							map.put("errorCode", "0000");
//							map.put("data", dateMap);
//							map.put("city", "007");
//							map.put("userId", idCard);//TODO
//							map = new Resttemplate().SendMessage(map,applications.getSendip()+"/HSDC/person/socialSecurity");
//							//map = new Resttemplate().SendMessage(map,"http://192.168.3.16:8089/HSDC/person/socialSecurity");
//					  	
//					    }else{
//					    	logger.warn("泰安市社保信息获取工程中出错");
//					    	  map.put("errorCode", "0001");
//					          map.put("errorInfo", "网络连接异常!");
//					          return map;
//					    }		
//					    
//				}else{
//					logger.warn("泰安市社保信息获取登陆失败");
//					 map.put("errorCode", "0001");
//					 if(page2.asText().contains("未生成验证码")){
//				     map.put("errorInfo", "请重新获取图形验证码");	 
//					 }else{
//						 map.put("errorInfo", page2.asText()); 
//					 }
//					 return map;	   	
//				}
//			  
//		 }catch (Exception e) {
//			 logger.warn("泰安市社保",e); 
//			map.put("errorCode", "0001");
//           map.put("errorInfo", "网络连接异常!");
//			e.printStackTrace();
//		 }
//	        }	
//		return map;
//	
//	 } 
	/***
	 * 获得HtmlPage  
	 * @param webClient
	 * @param url 要访问的url
	 * @param list  参数
	 * @param methot  post、get
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws IOException
	 */
	 public HtmlPage getPages(WebClient webClient,String url,List<NameValuePair> list,HttpMethod methot) throws FailingHttpStatusCodeException, IOException{
		
		    WebRequest requests = new WebRequest(new URL(url));
            requests.setRequestParameters(list);
			requests.setHttpMethod(methot);
			
		  return webClient.getPage(requests);
		 
	 }
}
