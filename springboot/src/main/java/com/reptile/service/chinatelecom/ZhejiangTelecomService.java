package com.reptile.service.chinatelecom;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.reptile.util.GetMonth;
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
 * @ClassName: ZhejiangTelecomService  
 * @Description: TODO  
 * @author: lusiqin
 * @date 2018年1月2日  
 *
 */
@Service
public class ZhejiangTelecomService {
	 private Logger logger= LoggerFactory.getLogger(ZhejiangTelecomService.class);
	@Autowired
	private application application;
	/**
	 * 登陆成功之后
	 * @param request
	 * @return
	 */
	public  Map<String,Object> zheJiangLogin(HttpServletRequest request,String phoneNumber){
		 Map<String, Object> map = new HashMap<String, Object>(16);
		 
		 //从session中获得webClient
	     Object attribute = request.getSession().getAttribute("GBmobile-webclient");
	     
	     WebClient webClient=(WebClient)attribute;
	     if(webClient==null){
	    	 logger.warn("浙江电信","请先登录!");
	    	 map.put("errorCode", "0001");
			map.put("errorInfo", "请先登录!");
			return map;
	     } 
	     //获得当前时间
			String nowDate=GetMonth.nowMonth();
			int nowYear=new Integer(nowDate.substring(0,4));
			int nowMonth=new Integer(nowDate.substring(4));
			try {
				    List<String>  alertList=new ArrayList<String>(); 
			        CollectingAlertHandler head=new CollectingAlertHandler(alertList);
			        webClient.setAlertHandler(head);
				HtmlPage choosePage = webClient
						.getPage("http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10012&toStUrl=http://zj.189.cn/zjpr/cdr/getCdrDetailInput.htm");
				choosePage.executeJavaScript("$j('#cdrmonth').append(\"<option value= "+nowDate+">"+nowDate+"</option>\")").getNewPage();
				choosePage.executeJavaScript("$j('#cdrmonth').append(\"<option value= "+GetMonth.beforMon(nowYear,nowMonth,1)+">"+GetMonth.beforMon(nowYear,nowMonth,1)+"</option>\")");
				choosePage.executeJavaScript("$j('#cdrmonth').append(\"<option value= "+GetMonth.beforMon(nowYear,nowMonth,2)+">"+GetMonth.beforMon(nowYear,nowMonth,2)+"</option>\")");
				choosePage.executeJavaScript("$j('#cdrmonth').append(\"<option value= "+GetMonth.beforMon(nowYear,nowMonth,3)+">"+GetMonth.beforMon(nowYear,nowMonth,3)+"</option>\")");
				choosePage.executeJavaScript("$j('#cdrmonth').append(\"<option value= "+GetMonth.beforMon(nowYear,nowMonth,4)+">"+GetMonth.beforMon(nowYear,nowMonth,4)+"</option>\")");
				choosePage.executeJavaScript("$j('#cdrmonth').append(\"<option value= "+GetMonth.beforMon(nowYear,nowMonth,5)+">"+GetMonth.beforMon(nowYear,nowMonth,5)+"</option>\")");
				
				Thread.sleep(4000);	
				//月份select
				HtmlSelect selectMonth=(HtmlSelect) choosePage.getElementById("cdrmonth");
				//选中第一个月
				selectMonth.setSelectedIndex(0);
				Thread.sleep(500);
				//System.out.println(choosePage.asXml());			
				//获取验证码
				choosePage.executeJavaScript("javascript:getcode("+phoneNumber+")");
				Thread.sleep(1000);
				String str="成功";
				if(alertList.size()>0){
					if(alertList.get(0).toString().contains(str)){
						logger.warn("浙江电信","验证码发送成功!");
						 map.put("errorCode", "0000");
				         map.put("errorInfo", "验证码发送成功!");
					}else{
						logger.warn("浙江电信",alertList.get(0).toString());
						map.put("errorCode", "0001");
				        map.put("errorInfo", alertList.get(0).toString());
					}		
				}	
				//把页面放到session
				request.getSession().setAttribute("choosePage",choosePage );
				//把webClient放到session
	   		    request.getSession().setAttribute("webClient", webClient);
	   		    //把selectMonth放到session
	   		    request.getSession().setAttribute("selectMonth",selectMonth );
	   		   	
			} catch (Exception e) {
				 logger.warn("浙江电信",e);
				map.put("errorCode", "0001");
				map.put("errorInfo", "网络连接异常!");
			}
	
		return map;
		
	}
/**
 * 获取数据
 * @param code
 * @param request
 * @return
 * @throws Exception 
 */
	
	public Map<String,Object> zheJiangDetial(HttpServletRequest request,String phoneNumber,String servePwd,String name,String idCard,String code,String longitude,String latitude,String uuid) {
		    Map<String, Object> map = new HashMap<String, Object>(16);
            //验证码	判断	=========================================  
		    //从session中获得webClient
		    WebClient webClient = (WebClient)request.getSession().getAttribute("webClient");
		    PushSocket.pushnew(map, uuid, "1000","登录中");
		    PushState.state(phoneNumber, "callLog",100);
		    try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		    if(webClient==null){
		    	logger.warn("浙江电信，请先获取验证码");
		    	 map.put("errorCode", "0001");
			     map.put("errorInfo", "请先获取验证码");
			     PushState.state(phoneNumber, "callLog",200,"请先获取验证码");
			     PushSocket.pushnew(map, uuid, "3000","请先获取验证码");
				 return map;
		    }
		    try {  
	        List<String>  alertList=new ArrayList<String>(); 
	        CollectingAlertHandler head=new CollectingAlertHandler(alertList);
	        webClient.setAlertHandler(head);
	        
	        HtmlPage choosePage=(HtmlPage) request.getSession().getAttribute("choosePage");
	        HtmlSelect selectMonth=(HtmlSelect) request.getSession().getAttribute("selectMonth");
	        List<Map<String, Object>> dlist=new ArrayList<Map<String, Object>>();
	      
			HtmlPage newPage = (HtmlPage) choosePage
					.executeJavaScript(
							"generateCdrType(2,\"18\","+phoneNumber+",\"574\",\"移动电话\",\"4-3LL8EAT\")")
					.getNewPage();
			
				Thread.sleep(5000);
			
			HtmlInput randpsw = (HtmlInput) choosePage
					.getElementByName("cdrCondition.randpsw");	
			//3.
			randpsw.setValueAttribute(code);
			
				Thread.sleep(1000);
			
			HtmlTextInput username = choosePage.getElementByName("username");
			HtmlTextInput idcard = choosePage.getElementByName("idcard");
			//1.
			username.setValueAttribute(name);
			//2.
			idcard.setValueAttribute(idCard);
			int intz=6;
			//六个月数据
			for(int i=0;i<intz;i++){
				//选择对应月份
				selectMonth.setSelectedIndex(i);
				HtmlPage firstPage = (HtmlPage) choosePage.executeJavaScript(
						// 点击查询
						"cdrSubmit(2)").getNewPage();
				 //=============================浙江验证码校验===================================================	
				 if(alertList.size()>0){
					 logger.warn("浙江电信",alertList.get(0).toString());
					 PushSocket.pushnew(map, uuid, "3000",alertList.get(0).toString());
		             map.put("errorCode", "0001");
				     map.put("errorInfo", alertList.get(0).toString());	
				     PushState.state(phoneNumber, "callLog",200,alertList.get(0).toString());
					 return map;
				 }else if(i==0){
					 PushSocket.pushnew(map, uuid, "2000","登录成功");
				 }
				if (firstPage.asText().contains("我的清单详情")) {
					Thread.sleep(2000);
					PushSocket.pushnew(map, uuid, "5000","数据获取中");
				    HtmlSpan span=	(HtmlSpan) firstPage.getElementById("id1");
				    
				    String[] num=span.getLastElementChild().asXml().split("\\(");
				    String[] num2=num[1].split("\\)");
				    //总页数
				    int pageNum= new Integer(num2[0]);	
				    
				    //遍历总页数
					for (int j = 1; j < pageNum+1; j++) {
						Map<String, Object> dataMap = new HashMap<String, Object>(16);
						HtmlPage result = (HtmlPage) firstPage
								.executeJavaScript("javascript:sub(" + j + ")")
								.getNewPage();
						//第j页  
						dataMap.put("item", result.asXml());	
						
						dlist.add(dataMap);
					}
				} else { 
					logger.warn("浙江电信","暂无数据");
					map.put("errorCode", "0001");
					map.put("errorInfo", "暂无数据");
					PushSocket.pushnew(map, uuid, "7000","暂无数据");
					PushState.state(phoneNumber, "callLog",200,"暂无数据");
					return map;
				}
		}
			 
            PushSocket.pushnew(map, uuid, "6000","获取数据成功");
		    map.put("data", dlist);
            map.put("UserPassword",servePwd);
            map.put("UserIphone", phoneNumber);
            map.put("longitude", longitude);
			map.put("latitude", latitude);
            map.put("flag", "9");
			map.put("errorCode", "0000");
			map.put("errorInfo", "查询成功");
			logger.warn("浙江电信","查询成功");
			webClient.close();
			Resttemplate resttemplate = new Resttemplate();
	        map = resttemplate.SendMessage(map, application.getSendip()+"/HSDC/message/telecomCallRecord"); 	
	        String str1="0000";
	        String erro="errorCode";
	        if(map.get(erro).equals(str1)) {
				PushSocket.pushnew(map, uuid, "8000","认证成功");
				 PushState.state(phoneNumber, "callLog",300);
			}else {
				PushSocket.pushnew(map, uuid, "9000",map.get("errorInfo").toString());
				PushState.state(phoneNumber, "callLog",200,map.get("errorInfo").toString());
			}
		    } catch (InterruptedException e) {
			logger.warn("浙江电信",e);
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络异常！");
			PushSocket.pushnew(map, uuid, "9000","网络异常！");
			PushState.state(phoneNumber, "callLog",200,"网络异常！");
			}
	        return map;
	}
}
