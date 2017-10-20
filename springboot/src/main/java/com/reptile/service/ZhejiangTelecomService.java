package com.reptile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.reptile.util.GetMonth;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;

@Service
public class ZhejiangTelecomService {
	@Autowired
	private application application;
	/**
	 * 登陆成功之后
	 * @param request
	 * @return
	 */
	public  Map<String,Object> zheJiangLogin(HttpServletRequest request,String phoneNumber){
		 Map<String, Object> map = new HashMap<String, Object>();
		 
	     Object attribute = request.getSession().getAttribute("GBmobile-webclient");//从session中获得webClient
	     
	     WebClient webClient=(WebClient)attribute;
	     if(webClient==null){
	    	 map.put("errorCode", "0001");
			map.put("errorInfo", "请先登录!");
			return map;
	     } 
			String nowDate=GetMonth.nowMonth();//获得当前时间
			int nowYear=new Integer(nowDate.substring(0,4));
			int nowMonth=new Integer(nowDate.substring(4));
			try {
				    List<String>  alertList=new ArrayList<String>(); 
			        CollectingAlertHandler head=new CollectingAlertHandler(alertList);
			        webClient.setAlertHandler(head);
				HtmlPage choosePage = webClient
						.getPage("http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10012&toStUrl=http://zj.189.cn/zjpr/cdr/getCdrDetailInput.htm");
				//choosePage.executeJavaScript("$j('#cdrmonth').append('<option value=201709 selected=selected>201709</option>')").getNewPage();
				choosePage.executeJavaScript("$j('#cdrmonth').append(\"<option value= "+nowDate+">"+nowDate+"</option>\")").getNewPage();
				choosePage.executeJavaScript("$j('#cdrmonth').append(\"<option value= "+GetMonth.beforMon(nowYear,nowMonth,1)+">"+GetMonth.beforMon(nowYear,nowMonth,1)+"</option>\")");
				choosePage.executeJavaScript("$j('#cdrmonth').append(\"<option value= "+GetMonth.beforMon(nowYear,nowMonth,2)+">"+GetMonth.beforMon(nowYear,nowMonth,2)+"</option>\")");
				choosePage.executeJavaScript("$j('#cdrmonth').append(\"<option value= "+GetMonth.beforMon(nowYear,nowMonth,3)+">"+GetMonth.beforMon(nowYear,nowMonth,3)+"</option>\")");
				choosePage.executeJavaScript("$j('#cdrmonth').append(\"<option value= "+GetMonth.beforMon(nowYear,nowMonth,4)+">"+GetMonth.beforMon(nowYear,nowMonth,4)+"</option>\")");
				choosePage.executeJavaScript("$j('#cdrmonth').append(\"<option value= "+GetMonth.beforMon(nowYear,nowMonth,5)+">"+GetMonth.beforMon(nowYear,nowMonth,5)+"</option>\")");
				
				Thread.sleep(4000);	
				HtmlSelect selectMonth=(HtmlSelect) choosePage.getElementById("cdrmonth");//月份select
				selectMonth.setSelectedIndex(0);//选中第一个月
				Thread.sleep(500);
				//System.out.println(choosePage.asXml());			
				choosePage.executeJavaScript("javascript:getcode("+phoneNumber+")");//获取验证码
				Thread.sleep(1000);
				if(alertList.size()>0){
					if(alertList.get(0).toString().contains("成功")){
						 map.put("errorCode", "0000");
				         map.put("errorInfo", "验证码发送成功!");
					}else{
						map.put("errorCode", "0001");
				        map.put("errorInfo", alertList.get(0).toString());
					}		
				}	
				request.getSession().setAttribute("choosePage",choosePage );//把页面放到session
	   		    request.getSession().setAttribute("webClient", webClient);//把webClient放到session
	   		    request.getSession().setAttribute("selectMonth",selectMonth );//把selectMonth放到session
	   		   	
			} catch (Exception e) {
				map.put("errorCode", "0001");
				map.put("errorInfo", "网络连接异常!");
				e.printStackTrace();
			}
	
		return map;
		
	}
/**
 * 获取数据
 * @param code
 * @param request
 * @return
 */
	
	public Map<String,Object> zheJiangDetial(HttpServletRequest request,String phoneNumber,String servePwd,String name,String idCard,String code,String longitude,String latitude){
		    Map<String, Object> map = new HashMap<String, Object>();
            //验证码	判断	=========================================  
		    WebClient webClient = (WebClient)request.getSession().getAttribute("webClient");//从session中获得webClient
		    if(webClient==null){
		    	 map.put("errorCode", "0001");
			     map.put("errorInfo", "请先获取验证码");	
				 return map;
		    }
		      
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
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(choosePage.asXml());
			HtmlInput randpsw = (HtmlInput) choosePage
					.getElementByName("cdrCondition.randpsw");	
			randpsw.setValueAttribute(code);//3.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HtmlTextInput username = choosePage.getElementByName("username");
			HtmlTextInput idcard = choosePage.getElementByName("idcard");
			username.setValueAttribute(name);//1.
			idcard.setValueAttribute(idCard);//2.
		  
			for(int i=0;i<6;i++){//六个月数据
				selectMonth.setSelectedIndex(i);//选择对应月份
				HtmlPage firstPage = (HtmlPage) choosePage.executeJavaScript(
						"cdrSubmit(2)").getNewPage();// 点击查询
				 //=============================浙江验证码校验===================================================	
				 if(alertList.size()>0){
		             map.put("errorCode", "0001");
				     map.put("errorInfo", alertList.get(0).toString());	
					 return map;
				 }
				if (firstPage.asText().contains("我的清单详情")) {
					
				    HtmlSpan span=	(HtmlSpan) firstPage.getElementById("id1");
				    
				   /* if(span==null){
				    	Map<String, Object> dataMap = new HashMap<String, Object>();
				    	dataMap.put("item", firstPage.asXml());
				    	dlist.add(dataMap);
				    }else{*/
				    
				    String[] num=span.getLastElementChild().asXml().split("\\(");
				    String[] num2=num[1].split("\\)");
				    int pageNum= new Integer(num2[0]);	//总页数
					//System.out.println("查询成功！！" + firstPage.asXml());
				    
					for (int j = 1; j < pageNum+1; j++) {//遍历总页数
						Map<String, Object> dataMap = new HashMap<String, Object>();
						HtmlPage result = (HtmlPage) firstPage
								.executeJavaScript("javascript:sub(" + j + ")")
								.getNewPage();
						dataMap.put("item", result.asXml());	//第j页  
						  //System.out.println("前" +i+"月date"+j+"   "+result.asXml());//结
						
						dlist.add(dataMap);
					}
				    //}
				} else { 
					map.put("errorCode", "0001");
					map.put("errorInfo", "暂无数据");
					return map;
				}
		}
			
		    map.put("data", dlist);
            map.put("UserPassword",servePwd);
            map.put("UserIphone", phoneNumber);
            map.put("longitude", longitude);
			map.put("latitude", latitude);
            map.put("flag", "9");
			map.put("errorCode", "0000");
			map.put("errorInfo", "查询成功");
			Resttemplate resttemplate = new Resttemplate();
	        map = resttemplate.SendMessage(map, application.getSendip()+"/HSDC/message/telecomCallRecord"); 	
		return map;
		
	}
	
	
}
