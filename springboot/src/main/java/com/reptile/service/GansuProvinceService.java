package com.reptile.service;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.Dates;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;



@Service
public class GansuProvinceService {
	 private Logger logger= LoggerFactory.getLogger(ChengduTelecomService.class);
	 //甘肃电信发送短信
	 public static Map<String,Object> GansuPhone(HttpServletRequest request,String UserNum){
		   Map<String,Object> map = new HashMap<String,Object>();
	        HttpSession session = request.getSession();
	        Object attribute = session.getAttribute("GBmobile-webclient");
	        if (attribute == null) {
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "操作异常!");
	            return map;
	        } else {
	        	
	        	 try {
	                 WebClient webClient = (WebClient) attribute;
	                 WebRequest requests = new WebRequest(new URL("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000600"));
	                 requests.setHttpMethod(HttpMethod.GET);
	                 HtmlPage page1 = webClient.getPage(requests);
	                 if (!page1.asText().contains("获取验证码")) {
	                     map.put("errorCode", "0007");
	                     map.put("errorInfo", "操作异常！");
	                     return map;
	                 }
	                WebRequest request1 =new WebRequest(new URL("http://gs.189.cn/web/json/sendSMSRandomNumSMZ.action"));
	     			request1.setHttpMethod(HttpMethod.POST);//提交方式
	     			List<NameValuePair> list = new ArrayList<NameValuePair>();
	     			String num="4:"+UserNum;
	     			System.out.println(num);
	     			list.add(new NameValuePair("productGroup",num));
	     			request1.setRequestParameters(list);
	     			UnexpectedPage page =	webClient.getPage(request1);
	     			System.out.println(page.getWebResponse().getContentAsString()+"------------");
	     			if(page.getWebResponse().getContentAsString().indexOf("1")!=-1){
	     				session.setAttribute("sessionWebClient-GANSU", webClient);
	     				map.put("errorCode", "0000");
	     				map.put("errorInfo", "验证码发送成功!");
	     			}else{
	     				  map.put("errorCode", "0001");
	 	                 map.put("errorInfo", "电信返回值错误");
	     			}
	             } catch (Exception e) {
	                 e.printStackTrace();
	                 map.put("errorCode", "0002");
	                 map.put("errorInfo", "请再次尝试发送验证码");
	             }
	 	       
	        }
		return map;
	 }
	 public static Map<String,Object> GansuPhone1(HttpServletRequest request,String Usercard,String UserNum,String UserPass,String catpy,String longitude,String latitude){
		   Map<String,Object> map = new HashMap<String,Object>();
		   HttpSession session = request.getSession();
		   
	        Object attribute = session.getAttribute("sessionWebClient-GANSU");
	        if (attribute == null) {
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "操作异常!");
	            return map;
	        } else {
	        	PushState.state(UserNum, "bankBillFlow",100);
	        	  WebClient webClient = (WebClient) attribute;
	        	  WebRequest request1;
	          Map<String,Object>data=new HashMap<String,Object>();
	        	  Map<String,Object>Gansu=new HashMap<String,Object>();
	        	  List<Map<String,Object>> datalist=new ArrayList<Map<String,Object>>();
				try {
					PushState.state(UserNum, "callLog",100);
					String num="4:"+UserNum;
					Date date=new Date();
				    String year= new SimpleDateFormat("yyyyMM").format(date);
				    System.out.println(year+"-------------");
					for (int i = 0; i < 3; i++) {
						Thread.sleep(3000);
						int ye=Integer.parseInt(year);;
						int month=ye-i;
						String months=Dates.beforMonth(i);
						UnexpectedPage page= webClient.getPage("http://gs.189.cn/web/json/searchDetailedFee.action?randT="+ catpy+"&productGroup="+num+"&orderDetailType=6&queryMonth="+months);
						Thread.sleep(3000);
						System.out.println(page+"-------"+i+"-------"+months);
						String a=page.getWebResponse().getContentAsString();
						//JSONObject aa = new JSONObject(a); 
					
						data.put("items",a.replaceAll("\\\"", "\""));
						datalist.add(data);
						Thread.sleep(5000);
					}
					Gansu.put("data", datalist);
					Gansu.put("UserIphone", UserNum);
					Gansu.put("flag", 6);
					Gansu.put("UserPassword", UserPass);
					System.out.println(Gansu);
					//http://192.168.3.35:8080/HSDC/message/telecomCallRecord
					Resttemplate resttemplate = new Resttemplate();
					map=resttemplate.SendMessage(Gansu,"http://192.168.3.35:8080/HSDC/message/telecomCallRecord");
					 
					if(map!=null&&"0000".equals(map.get("errorCode").toString())){
					    	PushState.state(UserNum, "callLog",300);
			                map.put("errorInfo","查询成功");
			                map.put("errorCode","0000");
			         }else{
			            	//--------------------数据中心推送状态----------------------
			            	PushState.state(UserNum, "callLog",200);
			            	//---------------------数据中心推送状态----------------------
			                map.put("errorInfo","服务器跑偏了");
			                map.put("errorCode","0001");
			          }
					webClient.close();
					} catch (Exception e) {
						e.printStackTrace();
						PushState.state(UserNum, "callLog",200);
						//---------------------------数据中心推送状态----------------------------------
						 map.clear();
						 map.put("errorInfo","服务繁忙，请稍后再试");
						 map.put("errorCode","0002");
				}
		
				
	        }
	      
		 return map;
		 
	 }
}
