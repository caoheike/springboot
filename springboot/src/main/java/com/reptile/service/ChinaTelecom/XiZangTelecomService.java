package com.reptile.service.ChinaTelecom;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.GetMonth;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.*;

@Service
public class XiZangTelecomService {
	private Logger logger= LoggerFactory.getLogger(XiZangTelecomService.class);
	 @Autowired
	  private application applications;
	 /**
	  * 西藏电信 获取图片验证码l
	  * @param request
	  * @return
	  */
	  public Map<String, Object> getImageCode(HttpServletRequest request){
		  Map<String, Object> map = new HashMap<String, Object>();
		  Map<String,String> mapPath=new HashMap<String, String>();
	        HttpSession session = request.getSession();
	        Object attribute = session.getAttribute("GBmobile-webclient");
	        if (attribute == null) {
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "操作异常!");
	            return map;
	        } else {
	        	 WebClient webClient = (WebClient) attribute;
					try {
						WebRequest  requests = new WebRequest(new URL("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000434"));
						requests.setHttpMethod(HttpMethod.GET);
		                HtmlPage page1 = webClient.getPage(requests);
		                Thread.sleep(500);
		                String page=page1.asXml();
		                //System.out.println(page);
		                if(page.contains("语音清单查询")){
		                	WebRequest  request1 = new WebRequest(new URL("http://xz.189.cn/service/bill/xz/initQueryTicket.action?fastcode=10000436&cityCode=xz"));
							requests.setHttpMethod(HttpMethod.GET);
			                HtmlPage pages = webClient.getPage(request1);
			                Thread.sleep(500);
			              //System.out.println(pages.asXml());
		                //=============图形验证码=====================
			             /* HtmlTextInput randomCode=  (HtmlTextInput) pages.getElementById("randomcode");  
			              randomCode.click();*/
			              Thread.sleep(200);
			              Random random = new Random();  
			              //random必须要做为成员变量或者静态变量，不能每次都new一个，否则就不具有随机性了。  
			               int s = random.nextInt(9000) + 1000;  
			              //这样的话s的范围一定是[1000,9999]  
			                UnexpectedPage page7 = webClient.getPage("http://xz.189.cn/VImage?random="+ s);

			                String path = request.getServletContext().getRealPath("/vecImageCode");
			                File file = new File(path);
			                if (!file.exists()) {
			                    file.mkdirs();
			                }
			                String fileName = "XZCode" + System.currentTimeMillis() + ".png";
			                BufferedImage bi = ImageIO.read(page7.getInputStream());
			                ImageIO.write(bi, "png", new File(file, fileName));
			                
			                mapPath.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/vecImageCode/" + fileName);
			                map.put("data",mapPath);
			                map.put("errorCode", "0000");
			                map.put("errorInfo", "验证码获取成功");
	                        session.setAttribute("XZ-WebClient", webClient);
			                session.setAttribute("XZ-page", pages);
			              
		                }else{
		                	map.put("errorCode", "0001");
			                map.put("errorInfo", "网络连接异常!");	
		                }
		         
					} catch (Exception e) {
						map.put("errorCode", "0001");
		                map.put("errorInfo", "网络连接异常!");
						e.printStackTrace();
					}  
	        }
		  
		 
		return map; 
	 }
/**
 * 西藏电信 获取短信验证码
 * @param request
 * @return
 */
	  public Map<String, Object> getSMCode(HttpServletRequest request,String phoneNumber,String imageCode){

	        Map<String, Object> map = new HashMap<String, Object>();
	        HttpSession session = request.getSession();

	        Object client = session.getAttribute("XZ-WebClient");
	        if (client == null) {
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "请先获取图形验证码");
	            return map;
	        } else { 
	        	WebClient webClient = (WebClient) client;
	        	List<String>  alertList=new ArrayList<String>();   
		        CollectingAlertHandler head=new CollectingAlertHandler(alertList);
		        webClient.setAlertHandler(head);
	           //HtmlPage page=(HtmlPage) session.getAttribute("XZ-page");
	          
	           try { 
	        	 String url="http://xz.189.cn/service/bill/sendValidReq.action"; 
	        	 WebRequest requests = new WebRequest(new URL(url));
	        	 requests.setAdditionalHeader("Accept", "application/json, text/javascript, */*; q=0.01");
	        	 requests.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	        	 requests.setAdditionalHeader("Origin", "http://xz.189.cn");
	        	 requests.setAdditionalHeader("Referer", "http://xz.189.cn/service/bill/xz/initQueryTicket.action?fastcode=10000436&cityCode=xz");
	            List<NameValuePair> list=new ArrayList<NameValuePair>();
	            list.add(new NameValuePair("mobileNum",phoneNumber));
	            list.add(new NameValuePair("randomcode",imageCode));
	            
	              requests.setRequestParameters(list);
				  requests.setHttpMethod(HttpMethod.POST);
	              TextPage page1 = webClient.getPage(requests);  
	              String result=   page1.getWebResponse().getContentAsString();
	               JSONObject json=new JSONObject().fromObject(result);
	               System.out.println(json);
	           if(json!=null&&json.get("success").toString().contains("true")){
	        	   map.put("errorCode", "0000");
	               map.put("errorInfo","验证码发送成功" );
	               session.setAttribute("XZC-WebClient", webClient);
	        	   
	           }else{
	        	   map.put("errorCode", "0001");
	                map.put("errorInfo",json.get("tipValue").toString() );
	           }
        	} catch (Exception e) {
					map.put("errorCode", "0001");
	                map.put("errorInfo", "网络连接异常!");
					e.printStackTrace();
				}
	        	
		  
	        }
		  
		return map;
		  
	  }  
	  
	  public Map<String, Object> getDetail(HttpServletRequest request,String phoneNumber, String serverPwd,String code,String longitude,String latitude){
		  Map<String, Object> map = new HashMap<String, Object>();
		  Map<String, Object> mapDate = new HashMap<String, Object>();
		  List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	        HttpSession session = request.getSession();

	        Object client = session.getAttribute("XZC-WebClient");
	        if (client == null) {
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "请先获取短信证码");
	            return map;
	        } else { 
	        	WebClient webClient = (WebClient) client;
//	        	
				try {
					String url="http://xz.189.cn/service/bill/validCDMANum.action?mobileNum="+phoneNumber+"&rondomCode="+code+"&_="+System.currentTimeMillis();
					WebRequest requests = new WebRequest(new URL(url));
					requests.setHttpMethod(HttpMethod.GET);
					requests.setAdditionalHeader("Origin", "xz.189.cn");
		        	requests.setAdditionalHeader("Referer", "http://xz.189.cn/service/bill/xz/initQueryTicket.action?fastcode=10000436&cityCode=xz");
		           
	                TextPage page1 = webClient.getPage(requests);  
	               String result=   page1.getWebResponse().getContentAsString();
	               JSONObject json=new JSONObject().fromObject(result);
	               System.out.println(json);
	               String tip=  json.get("success").toString();
	               if(tip!=null&&tip.contains("true")){
	            	  System.out.println("校验成功！");
	            	  //=====================获取详单=======================
	            	  String detailUrl=	"http://xz.189.cn/service/bill/xz/feeDetailrecordList.action";
	            	  WebRequest request1 = new WebRequest(new URL(url));
	            	  request1.setAdditionalHeader("Accept", "text/html, */*; q=0.01");
	 	        	  request1.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	 	        	  request1.setAdditionalHeader("Origin", "http://xz.189.cn");
	 	        	  request1.setAdditionalHeader("Referer", "http://xz.189.cn/service/bill/xz/initQueryTicket.action?fastcode=10000436&cityCode=xz");
	            	 
	            	 List<NameValuePair> list=new ArrayList<NameValuePair>();
	  	             list.add(new NameValuePair("currentPage","1"));
	  	             list.add(new NameValuePair("pageSize","2000"));
	  	            
	  	             list.add(new NameValuePair("operListID","1"));//语音详单
	  	             list.add(new NameValuePair("isPrepay","0"));//手机号第0个
	  	             list.add(new NameValuePair("pOffrType","481"));
	  	             int[] nowYearMonth=GetMonth.nowYearMonth();
	  	             int year=nowYearMonth[0];
	  	             int month=nowYearMonth[1];
	  	             String effDate=GetMonth.firstDateOfMonth(year,month);
	  	             String expDate=GetMonth.lastDateOfMonth(year,month);
	  	             list.add(new NameValuePair("serviceNbr",phoneNumber));
	  	             for(int i=1;i<7;i++){
	  	            	
	  	               list.add(new NameValuePair("effDate",effDate));//开始时间
	  	               list.add(new NameValuePair("expDate",expDate));//结束时间
	  	             
	  	              request1.setRequestParameters(list);
					  request1.setHttpMethod(HttpMethod.POST);
		              HtmlPage detail = webClient.getPage(request1);
		              Thread.sleep(500);
		              String results=detail.asXml();
		              System.out.println(results);
	  	            if(results.contains("错误")){
	  	            	//System.out.println(year+"*****"+month);
	  	            	//System.out.println(results);
	  	            	mapDate.put("item", "暂无法获取详单，您可以通过手机发送短信代码 103至 10001，查询上月账单；104 #查询年月（如： 104#201201）至 10001，查询前6个月的账单。"); 
	  	            	dataList.add(mapDate);
	  	            }else{
	  	            	System.out.println(year+"*****"+month);
	  	            	System.out.println(results);
	  	            	mapDate.put("item",results); 
	  	            	dataList.add(mapDate);
	  	            }
	  	            
	  	            String beforMonth=GetMonth.beforMon(year, month, i);
	  	            year=new Integer(beforMonth.substring(0, 4));
	  	            month=new Integer(beforMonth.substring(4));
	  	            effDate=beforMonth+"-01";
	  	            expDate=GetMonth.lastDateOfMonth(year,month); 
	  	             
	  	            }
	  	          //===================推数据=====================   
	  	           map.put("data", dataList);
		           map.put("UserPassword",serverPwd );
		           map.put("UserIphone", phoneNumber);
		           map.put("longitude", longitude);
				   map.put("latitude", latitude);
		           map.put("flag", " ");
		           map.put("errorCode", "0000");
		           map.put("errorInfo", "查询成功");
		           webClient.close();
		           Resttemplate resttemplate = new Resttemplate();
		          map = resttemplate.SendMessage(map, applications.getSendip()+"/HSDC/message/telecomCallRecord"); 
	  	         //===================推数据=====================   
	  	             
	  	             
	              }else if(tip!=null&&tip.contains("false")){
	            	  map.put("errorCode", "0001");
		              map.put("errorInfo", json.get("tipValue").toString());
		              return map;
	              }else{
	            	String tips=  json.get("flag").toString();
	            	if(tips.equals("2")){
	            		 map.put("errorCode", "0001");
			             map.put("errorInfo", "验证码失效");
			             return map;
	            	}
	              }
	                
				} catch (Exception e) {
					map.put("errorCode", "0001");
	                map.put("errorInfo", "网络连接异常!");
					e.printStackTrace();
				}
			}
		  
		  
		return map;
		  
	  }  
	  
}
