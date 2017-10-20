package com.reptile.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;

@Service
public class GuiZhouTelecomService {
	@Autowired
	private application application;
	/**
	 * 登陆成功之后
	 * @param request
	 * @return
	 */
	public  Map<String,Object> guiZhouLogin(HttpServletRequest request){
		 Map<String, Object> map = new HashMap<String, Object>(); 
	     Object attribute = request.getSession().getAttribute("GBmobile-webclient");//从session中获得webClient
	     WebClient webClient=(WebClient)attribute; 
	     if(webClient==null){
	    	 map.put("errorCode", "0001");
			 map.put("errorInfo", "请先登录!");
			 return map;
	     }
	     try {
			 HtmlPage nextPage=webClient.getPage("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=00320353");
			//HtmlPage ifrPage=webClient.getPage("http://service.gz.189.cn/web/query.php?action=call&fastcode=00320353&cityCode=gz");
	    	 HtmlPage ifrPage=webClient.getPage("http://www.189.cn//dqmh/ssoLink.do?method=linkTo&platNo=10024&toStUrl=http://service.gz.189.cn/web/query.php?action=call&fastcode=00320353&cityCode=gz");	
	    	
				Thread.sleep(1000);
			
			//System.out.println(P.asXml());
	    	 HtmlButtonInput button= (HtmlButtonInput) ifrPage.getElementById("getcheckcode");
	    	 button.click();//发送验证码
	    	 Thread.sleep(500);
	    	 if(button.getAttribute("value").contains("发送次数过多")){	
				 map.put("errorCode", "0001");
				 map.put("errorInfo", "发送次数过多，请稍后重试");
				 return map;
			   }else if(button.getAttribute("value").contains("秒后可重新获取")){
	    		 map.put("errorCode", "0000");
	             map.put("errorInfo", "验证码已发送!");
			 }else{
				 map.put("errorCode", "0001");
		         map.put("errorInfo", "网络连接异常");
			 }
			request.getSession().setAttribute("ifrPage",ifrPage );//把页面放到session
   		    request.getSession().setAttribute("webClient", webClient);//把webClient放到session
			
			
		} catch (Exception e) {
			map.put("errorCode", "0001");
            map.put("errorInfo", "网络连接异常");
		}
		return map;	
	}
	
	/**
	 * 获取数据
	 * @param cade
	 * @param request
	 * @return
	 */
	
	public Map<String,Object> guiZhouDetial(String code,HttpServletRequest request,String phoneNumber,String servePwd,String longitude,String latitude) {
		    Map<String, Object> map = new HashMap<String, Object>();
	        WebClient webClient=(WebClient)request.getSession().getAttribute("webClient");//从session中获得webClient;
	        if(webClient==null){
		    	 map.put("errorCode", "0001");
			     map.put("errorInfo", "请先获取验证码");	
				 return map;
		    }
		      
	        HtmlPage ifrPage=(HtmlPage) request.getSession().getAttribute("ifrPage");
	        List<Map<String, Object>> dataList=new ArrayList<Map<String, Object>>();
	        
			ifrPage.getElementById("checkcode").setAttribute("value", code);//输入验证码
			List<DomElement>  radio=ifrPage.getElementsByName("ACCTMONTH");//获取月份复选框//System.out.println(radio.get(0).asXml());
			HtmlButtonInput button= ifrPage.getElementByName("input");
			
             for(int i=0;i<radio.size();i++){//六个月数据
            	 Map<String, Object> dmap = new HashMap<String, Object>();
        	 try {
				 radio.get(i).click(); 
				 HtmlPage detial= button.click();
				 Thread.sleep(1000);
			//======================验证码正确性验证===============================	
				 if(code==null||code.equals("")){
					 map.put("errorCode", "0001");
 					 map.put("errorInfo", "验证码不能为空");
            		 return map; 
				 }
				 
				 if( detial.getElementById("tilte").asXml().contains("验证码错误")){
            		 map.put("errorCode", "0001");
 					 map.put("errorInfo", "验证码错误");
            		 return map; 
            	 }else{		 
				 //System.out.println(detial.getWebResponse().getContentAsString());
					
		        	dmap.put("item", detial.asXml());
		         // System.out.println(detial.asXml());
		      	    dataList.add(dmap);
            	 }
			} catch (Exception e) {
				map.put("errorCode", "0001");
	            map.put("errorInfo", "网络连接异常");
				e.printStackTrace();
				return map;
			}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
       }        
           map.put("data", dataList);
           map.put("UserPassword",servePwd );
           map.put("UserIphone", phoneNumber);
           map.put("flag", "10");
           map.put("errorCode", "0000");
           map.put("errorInfo", "查询成功");
           Resttemplate resttemplate = new Resttemplate();
           map = resttemplate.SendMessage(map, application.getSendip()+"/HSDC/message/telecomCallRecord");
           
          // map = resttemplate.SendMessage(map, "http://192.168.3.4:8081/HSDC/message/telecomCallRecord"); 
           
           System.out.println(map);
           if(map.get("errorCode").equals("0003")){
        	   map.put("errorCode", "0001");
        	   map.put("errorInfo", "查询失败，稍后再试！！");
           }
           
		return map;
	}
}
