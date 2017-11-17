package com.reptile.service.socialSecurity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;

@Service
public class ZiBoSocialSecuritySercice {
	private Logger logger= LoggerFactory.getLogger(ZiBoSocialSecuritySercice.class);
	 @Autowired
	  private application applications;
	 /**
	   * 获取图形验证码
	   * */
	  public Map<String, Object> getImageCode(HttpServletRequest request,String passWord){
		  Map<String, Object> map = new HashMap<String, Object>();
		  Map<String,String> mapPath=new HashMap<String, String>();
	        HttpSession session = request.getSession();
	        
	        WebClient webClient=new WebClientFactory().getWebClient();
	        //=============图形验证码=====================
	        try {
				HtmlPage page=webClient.getPage(new URL("http://sdzb.hrss.gov.cn:8001/logonDialog.jsp"));	
				Thread.sleep(200);

				//监控alert弹窗
	 			List<String> alertList = new ArrayList<String>();
	 			CollectingAlertHandler alert = new CollectingAlertHandler(alertList);
	 			webClient.setAlertHandler(alert);
				page.executeJavaScript("alert($.md5('"+passWord+"'))");
				
	 			String usermm= "";
				if(alertList.size() > 0){
					usermm = alertList.get(0).toString();//加密后的密码
					
				}else{
					logger.warn("淄博市社保--系统繁忙，请稍后再试！");
					map.put("errorInfo", "系统繁忙，请稍后再试！");
					map.put("errorCode", "0002");
		            return map;
				}
			Thread.sleep(500);
              
			 UnexpectedPage page7 = webClient.getPage("http://sdzb.hrss.gov.cn:8001/authcode");

             String path = request.getServletContext().getRealPath("/vecImageCode");
             File file = new File(path);
             if (!file.exists()) {
                 file.mkdirs();
             }
             String fileName = "Code" + System.currentTimeMillis() + ".png";
             BufferedImage bi = ImageIO.read(page7.getInputStream());
             ImageIO.write(bi, "png", new File(file, fileName));
             
             mapPath.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/vecImageCode/" + fileName);
             map.put("data",mapPath);
             map.put("errorCode", "0000");
             map.put("errorInfo", "验证码获取成功");
             session.setAttribute("ZB-WebClient", webClient);
             session.setAttribute("GMpassWord",usermm); 
             session.setAttribute("XZ-page", page);
	        }catch (Exception e) {
	        	logger.warn("淄博市社保",e);
         		map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
    			e.printStackTrace();
			}
		return map; 
	  }
	 
	  
	  
	  public  Map<String, Object> getDetails(HttpServletRequest request,String idCard,String catpy,String idCardNum){
		  Map<String, Object> map = new HashMap<String, Object>();
		  Map<String, Object> dateMap = new HashMap<String, Object>();
		  List<Object> dataList = new ArrayList<Object>();
		  List<Object> dataL = new ArrayList<Object>();
		  HttpSession session = request.getSession();

	        Object client = session.getAttribute("ZB-WebClient");
	        if (client == null) {
	        	logger.warn("淄博市社保,请先获取图形验证码");
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "请先获取图形验证码");
	            return map;
	        } else { 
		  try {
				PushState.state(idCardNum, "socialSecurity", 100);
			  WebClient webClient = (WebClient) client;
			 String usermm = (String) session.getAttribute("GMpassWord");//从session中获得GMpassWord
          
			 String url1="http://sdzb.hrss.gov.cn:8001/logon.do";
			 WebRequest requests1 = new WebRequest(new URL(url1));
        	 requests1.setAdditionalHeader("Accept", "*/*");
        	 requests1.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        	 requests1.setAdditionalHeader("Origin", "http://sdzb.hrss.gov.cn:8001");
        	 requests1.setAdditionalHeader("Referer", "http://sdzb.hrss.gov.cn:8001/logonDialog.jsp");
        	
			    List<NameValuePair> list1=new ArrayList<NameValuePair>();
	            list1.add(new NameValuePair("method","doLogon"));
	            list1.add(new NameValuePair("_xmlString","<?xml version='1.0' encoding='UTF-8'?><p><s userid='"+idCard+"'/><s usermm='"+usermm+"'/><s authcode='"+catpy+"'/><s yxzjlx='A'/><s appversion='1.0.53'/></p>"));
	            list1.add(new NameValuePair("_random",Math.random()+""));
	           
	            requests1.setRequestParameters(list1);
				requests1.setHttpMethod(HttpMethod.POST);
				HtmlPage page2 = webClient.getPage(requests1);
				Thread.sleep(100);
			   // System.out.println(page2.asXml());	
			
			    String tip=page2.asText();
			    if(tip.contains("__usersession_uuid")){
			    	logger.warn("淄博市社保基本信息获取中");
			    	String userSessionUuid = (String) JSONObject.fromObject(tip).get("__usersession_uuid");
		        	System.out.println("登陆成功");
		        	String laneID = UUID.randomUUID().toString();
		        	//基本人信息
		        	String baseInfo = this.getInfo("http://sdzb.hrss.gov.cn:8001/hspUser.do", laneID, userSessionUuid, "fwdQueryPerInfo", true, webClient).get(0);
					
		        	//System.out.println(baseInfo);
		        	dateMap.put("base", baseInfo);
		        	logger.warn("淄博市社保基本信息获取完成");
					//获取职工养老保险
		        	logger.warn("淄博市社保详情获取获取中");
					List<String> agedPayHisInfo  = this.getInfo("http://222.134.45.172:8002/hsp/siAd.do", laneID, userSessionUuid, "queryAgedPayHis", false, webClient);
					logger.warn("淄博市社保详情获取完成");
					dateMap.put("item", agedPayHisInfo);
//					//获取职工医疗保险
//					List<String> mediPayHisInfo  = this.getInfo("http://222.134.45.172:8002/hsp/siMedi.do", laneID, userSessionUuid, "queryMediPayHis", false, webClient);
//					dateMap.put("mediPayHisInfo", mediPayHisInfo);
//					//获取失业保险
//					List<String> lostPayHisInfo  = this.getInfo("http://222.134.45.172:8002/hsp/siLost.do", laneID, userSessionUuid, "queryLostPayHis", false, webClient);
//					dateMap.put("lostPayHisInfo", lostPayHisInfo);
//					//获取工伤保险
//					List<String> harmPayHisInfo  = this.getInfo("http://222.134.45.172:8002/hsp/siHarm.do", laneID, userSessionUuid, "queryHarmPayHis", false, webClient);
//					dateMap.put("harmPayHisInfo", harmPayHisInfo);
//					//获取生育保险
//					List<String> birthPayHisInfo  = this.getInfo("http://222.134.45.172:8002/hsp/siBirth.do", laneID, userSessionUuid, "queryBirthPayHis", false, webClient);
//					dateMap.put("birthPayHisInfo", birthPayHisInfo);
		        	
					map.put("errorInfo", "查询成功");
					map.put("errorCode", "0000");
					map.put("data", dateMap);
					map.put("city", "");
					map.put("userId", idCardNum);//TODO
	                 map = new Resttemplate().SendMessage(map,applications.getSendip()+"/HSDC/person/socialSecurity");
					//map = new Resttemplate().SendMessage(map,"http://192.168.3.16:8089/HSDC/person/socialSecurity");
		        	Thread.sleep(200);
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
			    	logger.warn("淄博市社保"+tip);
			    	map.put("errorCode", "0001");
		        	map.put("errorInfo",tip);
		        	return map;
			    }
			    
			    
			    
		}catch (Exception e) {
			logger.warn("淄博市社保",e);
			map.put("errorCode", "0001");
            map.put("errorInfo", "网络连接异常!");
			e.printStackTrace();
		}
	        }	
		return map;
	  }
	  
	  /**
		 * 是否为基本信息，true：是，false：否，是保险信息
		 * @param url
		 * @param laneID
		 * @param userSessionUuid
		 * @param method
		 * @param isFlag
		 * @param webClient
		 * @return
		 * @throws IOException 
		 * @throws FailingHttpStatusCodeException 
		 */
		public List<String>  getInfo(String url,String laneID,String userSessionUuid,String method,boolean isFlag,WebClient webClient) throws FailingHttpStatusCodeException, IOException{
			//封装请求参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new NameValuePair("method", method));
			list.add(new NameValuePair("_random",""+Math.random()));
			list.add(new NameValuePair("__usersession_uuid", userSessionUuid));
			list.add(new NameValuePair("_laneID", laneID));
			
			String response = webRequest(url, list, HttpMethod.POST, webClient);
			
			List<String> responseInfo = new ArrayList<String>();
			if(isFlag){
				if(response.contains("table")){
					responseInfo.add(response.substring(response.indexOf("table")-1,response.lastIndexOf("table")+6));
				}
			}else{
				if(response.contains("select")){
					
					String selectInfo = response.substring(response.indexOf("select")+6,response.lastIndexOf("select")+6);
					//获取可查到的所有年限
					Set<String> years = new HashSet<String>();
					for (int i = 0; i < selectInfo.length()-4; i++) {
						String str = selectInfo.substring(i, i+4);
						if(str.matches("^2[0-9]{3}")){
							years.add(str);
						}
					}
					//获取每年的社保信息,养老保险年限用ny，其余保险年限为year
					for (String item : years) {
						if(method.equals("queryAgedPayHis")){
							if(list.size() == 4){
								list.add(4,new NameValuePair("nd", item));
							}else{
								list.set(4,new NameValuePair("nd", item));
							}
						}else{
							if(list.size() == 4){
								list.add(4,new NameValuePair("year", item));
							}else{
								list.set(4,new NameValuePair("year", item));
							}
						}
						response = webRequest(url, list, HttpMethod.POST, webClient);
						responseInfo.add(response.substring(response.indexOf("</style><div")+8,response.lastIndexOf("</div><script")));
					}
				}
				
				
			}
			return responseInfo;
		}
		
		/**
		 * 根据参数获取请求结果
		 * @param url 请求地址
		 * @param list 请求参数
		 * @param httpMethod 请求方式 get或post
		 * @param webClient 请求的webClient
		 * @return 请求结果
		 * @throws FailingHttpStatusCodeException
		 * @throws IOException
		 */
		public String webRequest(String url,List<NameValuePair> list,HttpMethod httpMethod,WebClient webClient) throws FailingHttpStatusCodeException, IOException{
			WebRequest webRequest = new WebRequest(new URL(url));
			
			webRequest.setRequestParameters(list);
			webRequest.setHttpMethod(httpMethod);
			
			HtmlPage page =	webClient.getPage(webRequest);
			String response = page.getWebResponse().getContentAsString();
			
			return response;
		}
}
