package com.reptile.service.chinatelecom;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
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
 * @ClassName: GuiZhouTelecomService  
 * @Description: TODO  
 * @author: 111
 * @date 2018年1月2日  
 *
 */
@Service
public class GuiZhouTelecomService {
	 private Logger logger= LoggerFactory.getLogger(GuiZhouTelecomService.class);
	@Autowired
	private application application;
	/**
	 * 登陆成功之后
	 * @param request
	 * @return
	 */
	public  Map<String,Object> guiZhouLogin(HttpServletRequest request){
		 Map<String, Object> map = new HashMap<String, Object>(16); 
		 //从session中获得webClient
	     Object attribute = request.getSession().getAttribute("GBmobile-webclient");
	     WebClient webClient=(WebClient)attribute; 
	     if(webClient==null){
	    	 logger.warn("贵州电信","请先登录!");
	    	 map.put("errorCode", "0001");
			 map.put("errorInfo", "请先登录!");
			 return map;
	     }
	     try {
			 HtmlPage nextPage=webClient.getPage("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=00320353");
	    	 HtmlPage ifrPage=webClient.getPage("http://www.189.cn//dqmh/ssoLink.do?method=linkTo&platNo=10024&toStUrl=http://service.gz.189.cn/web/query.php?action=call&fastcode=00320353&cityCode=gz");	
	    	
				Thread.sleep(1000);
			
	    	 HtmlButtonInput button= (HtmlButtonInput) ifrPage.getElementById("getcheckcode");
	    	 //发送验证码
	    	 button.click();
	    	 Thread.sleep(500);
	    	 String valu="value";
	    	 String fs="发送次数过多";
	    	 String miaoh="秒后可重新获取";
	    	 if(button.getAttribute(valu).contains(fs)){
	    		 logger.warn("贵州电信","发送次数过多，请稍后重试");
				 map.put("errorCode", "0001");
				 map.put("errorInfo", "发送次数过多，请稍后重试");
				 return map;
			   }else if(button.getAttribute(valu).contains(miaoh)){
				   logger.warn("贵州电信","验证码已发送!");
	    		 map.put("errorCode", "0000");
	             map.put("errorInfo", "验证码已发送!");
			 }else{
				 logger.warn("贵州电信","网络连接异常");
				 map.put("errorCode", "0001");
		         map.put("errorInfo", "网络连接异常");
			 }
	    	 //把页面放到session
			request.getSession().setAttribute("ifrPage",ifrPage );
			//把webClient放到session
   		    request.getSession().setAttribute("webClient", webClient);
			
			
		} catch (Exception e) {
			logger.warn("贵州电信",e);
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
	
	public Map<String,Object> guiZhouDetial(String code,HttpServletRequest request,String phoneNumber,String servePwd,String longitude,String latitude,String uuid) {
		    Map<String, Object> map = new HashMap<String, Object>(16);
		    PushState.state(phoneNumber, "callLog",100);
		    PushSocket.pushnew(map, uuid, "1000","登录中");
		    try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    //从session中获得webClient;
		    WebClient webClient=(WebClient)request.getSession().getAttribute("webClient");
	        if(webClient==null){
	        	logger.warn("贵州电信","请先获取验证码");
		    	 map.put("errorCode", "0001");
			     map.put("errorInfo", "请先获取验证码");	
			     PushState.state(phoneNumber, "callLog",200,"请先获取验证码");
			     PushSocket.pushnew(map, uuid, "3000","请先获取验证码");
				 return map;
		    }
		      
	        HtmlPage ifrPage=(HtmlPage) request.getSession().getAttribute("ifrPage");
	        List<Map<String, Object>> dataList=new ArrayList<Map<String, Object>>();
	        
	        //输入验证码
			ifrPage.getElementById("checkcode").setAttribute("value", code);
			List<DomElement>  radio=ifrPage.getElementsByName("ACCTMONTH");
			HtmlButtonInput button= ifrPage.getElementByName("input");
			logger.warn("贵州电信数据获取中...");
			//六个月数据
             for(int i=0;i<radio.size();i++){
            	 Map<String, Object> dmap = new HashMap<String, Object>(16);
        	 try {
				 radio.get(i).click(); 
				 HtmlPage detial= button.click();
				 Thread.sleep(1000);
			//======================验证码正确性验证===============================	
				 if(code==null||code.equals("")){
					 PushSocket.pushnew(map, uuid, "3000","验证码不能为空");
					 PushState.state(phoneNumber, "callLog",200,"验证码不能为空");
					 map.put("errorCode", "0001");
 					 map.put("errorInfo", "验证码不能为空");
            		 return map; 
				 }
				 
				 if( detial.getElementById("tilte").asXml().contains("验证码错误")){
            		 map.put("errorCode", "0001");
 					 map.put("errorInfo", "验证码错误");
 					PushState.state(phoneNumber, "callLog",200,"验证码错误");
 					PushSocket.pushnew(map, uuid, "3000","验证码错误");
            		 return map; 
            	 }else{		 
            		 PushSocket.pushnew(map, uuid, "5000","获取数据中");
		        	dmap.put("item", detial.asXml());
		      	    dataList.add(dmap);
		      	    
            	 }
			} catch (Exception e) {
				logger.warn("贵州电信",e);
				map.put("errorCode", "0001");
	            map.put("errorInfo", "网络连接异常");
	            PushState.state(phoneNumber, "callLog",200,"网络连接异常");
	            PushSocket.pushnew(map, uuid, "3000","网络连接异常");
				return map;
			}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						logger.warn("贵州电信",e);
					}
       }    
             PushSocket.pushnew(map, uuid, "6000","获取数据成功");
             
           map.put("data", dataList);
           map.put("UserPassword",servePwd );
           map.put("UserIphone", phoneNumber);
           map.put("longitude", longitude);
			map.put("latitude", latitude);
           map.put("flag", "10");
           map.put("errorCode", "0000");
           map.put("errorInfo", "查询成功");
           logger.warn("贵州电信数据获取成功");
           webClient.close();
           Resttemplate resttemplate = new Resttemplate();
           map = resttemplate.SendMessage(map, application.getSendip()+"/HSDC/message/telecomCallRecord");
 
           String sss="0000";
           String errorCode="errorCode";
           String sanl="0003";
           if(map.get(errorCode).equals(sss)) {
        	   PushSocket.pushnew(map, uuid, "8000","认证成功");
        	   PushState.state(phoneNumber, "callLog",300);
           }else {
        	   PushState.state(phoneNumber, "callLog",200,map.get("errorInfo").toString());
        	   PushSocket.pushnew(map, uuid, "9000",map.get("errorInfo").toString());
           }
           if(map.get(errorCode).equals(sanl)){
        	   logger.warn("贵州电信查询失败，稍后再试！！");
        	   map.put("errorCode", "0001");
        	   map.put("errorInfo", "查询失败，稍后再试！！");
        	   PushState.state(phoneNumber, "callLog",200,"查询失败，稍后再试！！");
        	   PushSocket.pushnew(map, uuid, "9000","查询失败，稍后再试！！");
           }
		return map;
	}
}
