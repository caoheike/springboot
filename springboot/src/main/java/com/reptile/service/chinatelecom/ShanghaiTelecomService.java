package com.reptile.service.chinatelecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
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

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 
 * @ClassName: ShanghaiTelecomService  
 * @Description: TODO  
 * @author: 111
 * @date 2018年1月2日  
 *
 */
@Service
public class ShanghaiTelecomService {
	private Logger logger= LoggerFactory.getLogger(ShanghaiTelecomService.class);
	@Autowired
	private application application;
	
	/**
	 * 获取验证码
	 * @return
	 */
	public  Map<String,Object> afterLogin(HttpServletRequest request){	
		 Map<String, Object> map = new HashMap<String, Object>(16);
		 //从session中获得webClient
	     Object attribute = request.getSession().getAttribute("GBmobile-webclient");
	     WebClient webClient=(WebClient)attribute;
	     if(webClient==null){
	    	 logger.warn("上海电信--请先登录!");
	    	 map.put("errorCode", "0001");
			map.put("errorInfo", "请先登录!");
			return map;
	     }
	     try {
	    	HtmlPage nextPage=webClient.getPage("http://www.189.cn/dqmh/ssoLink.do?method=skip&platNo=93507&toStUrl=http://service.sh.189.cn/service/query/bill");
   		    HtmlPage codePage=webClient.getPage("http://service.sh.189.cn/service/query/detail");
   		    //发送验证码
   		    HtmlPage sendCodePage=(HtmlPage) codePage.executeJavaScript("javascript:sendCode()").getNewPage() ;
   		    String step="step_hint";
   		    String str1="验证码已发送";
   		    if( sendCodePage.getElementById(step).asXml().contains(str1)){
   		     logger.warn("上海电信，验证码已经发送成功");
   		     map.put("errorInfo", "验证码已经发送成功");
    		 map.put("errorCode", "0000");
   		    }else{
   		     logger.warn("上海电信",sendCodePage.getElementById("step_hint").asText());
   		     map.put("errorInfo", sendCodePage.getElementById("step_hint").asText());
    		 map.put("errorCode", "0001");
   		    }
   		    //把页面放到session
   		    request.getSession().setAttribute("sendCodePage",sendCodePage );
   		    request.getSession().setAttribute("webClient", webClient);
   		   
	     } catch (Exception e) {
	    	 logger.warn("上海电信",e);
	    	map.put("errorCode", "0001");
			map.put("errorInfo", "网络异常!");
		}
		return map;
   }
	/**
	 * 获取数据
	 * @param phoneNumber
	 * @param request
	 * @param code
	 * @return
	 * @throws Exception 
	 */
	public Map<String,Object> getDetial(String phoneNumber,String servePwd,HttpServletRequest request,String code,String longitude,String latitude,String uuid){
		//定单详情查取方式1.选择月份 2.自定义时间
		 Map<String, Object> map = new HashMap<String, Object>(16);
		 PushSocket.pushnew(map, uuid, "1000","登录中");
		 PushState.state(phoneNumber, "callLog",100);
		 try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		 List<Map<String, Object>> dataList=new ArrayList<Map<String, Object>>();
		 //从session中获得webClient
		 WebClient  webClient = (WebClient)request.getSession().getAttribute("webClient");
		 if(webClient==null){
			 logger.warn("上海电信未获取验证码");
	    	 map.put("errorCode", "0001");
		     map.put("errorInfo", "请先获取验证码");	
		     PushState.state(phoneNumber, "callLog",200,"请先获取验证码");
		     PushSocket.pushnew(map, uuid, "3000","请先获取验证码");
			 return map;
	    }
	     HtmlPage sendCodePage=(HtmlPage) request.getSession().getAttribute("sendCodePage");
	     HtmlTextInput inputCode=(HtmlTextInput) sendCodePage.getElementById("input_code");
	     //输入验证码
		 inputCode.setAttribute("value", code);
		 
		  try {
			HtmlPage detailPage=webClient.getPage("http://service.sh.189.cn/service/service/authority/query/billdetail/validate.do?input_code="+code+"&selDevid="+phoneNumber+"&flag=nocw&checkCode=验证码");
			String a=detailPage.getWebResponse().getContentAsString();
			String str="ME10001";
			if(a.contains(str)){
				logger.warn("上海电信","输入的验证码错误！");
				map.put("errorCode", "0001");
		        map.put("errorInfo", "输入的验证码错误！");
		        PushState.state(phoneNumber, "callLog",200,"输入的验证码错误！");
		        PushSocket.pushnew(map, uuid, "3000","输入的验证码错误！");
		        return map;
			}else{
				  PushSocket.pushnew(map, uuid, "2000","登录成功");
				logger.warn("上海电信数据获取中...");
			  Thread.sleep(1000);
			  PushSocket.pushnew(map, uuid, "5000","获取数据中");
			 //1.选择月份
	   		    Date date=new Date();
	   		    //his时间格式
	   		    SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy/MM" );
	   		    //8月份
	   		    String month=sdf.format(date);
	   		    int year=new Integer(month.substring(0,4));
	   		    int mon=new Integer(month.substring(5));
		            int pageEndNum=10;
		            int num=6;
		            //循环遍历上六个月 	
				    for(int i=0;i<num;i++){ 
				    month=GetMonth.beforMonth(year,mon,i);  
			        List<NameValuePair> list = new ArrayList<NameValuePair>();
			        list.add(new NameValuePair("bill_type","SCP"));
			        list.add(new NameValuePair("begin","0"));
			        list.add(new NameValuePair("end","10"));
			        list.add(new NameValuePair("flag","1"));  
			        list.add(new NameValuePair("devNo",phoneNumber));
			        //定义为his 表示选择月份
			        list.add(new NameValuePair("dateType","his"));
			        list.add(new NameValuePair("moPingType","LOCAL"));
			        list.add(new NameValuePair("startDate",""));
			        list.add(new NameValuePair("endDate",""));
			        list.add(new NameValuePair("queryDate",month));
			        WebRequest webRequest = new WebRequest(new URL("http://service.sh.189.cn/service/service/authority/query/billdetailQuery.do?begin=0&end=10&flag=1&devNo="+phoneNumber+"&dateType=his&bill_type=SCP&moPingType=LOCAL&queryDate="+month+"&startDate=&endDate="));
			      
					Thread.sleep(2000);
					
			        webRequest.setRequestParameters(list);
					webRequest.setHttpMethod(HttpMethod.GET);
					//第一次请求目的是获取总条数
					HtmlPage click = webClient.getPage(webRequest);
					
						Thread.sleep(1000);
					if(click.asText().contains("sumRow")&&click.asText().contains("sumTime")){	
						
						//获取总条数
						pageEndNum=new Integer(click.asText().split("\",\"sumTime")[0].split("sumRow\":\"")[1]);
						List<NameValuePair> list1 = new ArrayList<NameValuePair>();
				        list1.add(new NameValuePair("bill_type","SCP"));
				        list1.add(new NameValuePair("begin","0"));
				        list1.add(new NameValuePair("end",""+pageEndNum));
				        list1.add(new NameValuePair("flag","1"));  
				        list1.add(new NameValuePair("devNo",phoneNumber));
				        //定义为his 表示选择月份
				        list1.add(new NameValuePair("dateType","his"));
				        list1.add(new NameValuePair("moPingType","LOCAL"));
				        list1.add(new NameValuePair("startDate",""));
				        list1.add(new NameValuePair("endDate",""));
				        list1.add(new NameValuePair("queryDate",month));
						
					    WebRequest webRequestResult = new WebRequest(new URL("http://service.sh.189.cn/service/service/authority/query/billdetailQuery.do?begin=0&end="+pageEndNum+"&flag=1&devNo="+phoneNumber+"&dateType=his&bill_type=SCP&moPingType=LOCAL&queryDate="+month+"&startDate=&endDate="));
					    webRequestResult.setRequestParameters(list1);
					    webRequestResult.setHttpMethod(HttpMethod.GET);
					   
						HtmlPage results = webClient.getPage(webRequestResult);  
						  Map<String, Object> dmap = new HashMap<String, Object>(16);
						  String resultsstr = results.asText().replace("\\\"", "\""); 
						 dmap.put("item",resultsstr);
						 dataList.add(dmap);
					}else{
						logger.warn("上海电信暂无数据");
						map.put("errorCode", "0001");
				        map.put("errorInfo", "暂无数据");
					}
						Thread.sleep(1000);
					
				 } 
		         
		        //2.自定义时间
		   	 //====================================== now===================================
				    //NOW时间格式
				    SimpleDateFormat sdfNow =  new SimpleDateFormat( "yyyy-MM-dd" );
				    //结束时间当前时间
			   	    String endDate=sdfNow.format(date);
			   	    //本月第一天
			   	    String startDate=endDate.substring(0,8)+"01";  
		        	List<NameValuePair> nowList = new ArrayList<NameValuePair>();
			        nowList.add(new NameValuePair("begin","0"));
			        nowList.add(new NameValuePair("end",pageEndNum+""));
			        nowList.add(new NameValuePair("flag","1"));  
			        nowList.add(new NameValuePair("devNo",phoneNumber));
			        //定义为his 表示选择月份
			        nowList.add(new NameValuePair("dateType","now"));
			        nowList.add(new NameValuePair("moPingType","LOCAL"));
			        nowList.add(new NameValuePair("queryDate",month));
			        nowList.add(new NameValuePair("startDate",startDate));
			        nowList.add(new NameValuePair("endDate",endDate));
			        nowList.add(new NameValuePair("bill_type","SCP"));
			        WebRequest webRequestnow = new WebRequest(new URL("http://service.sh.189.cn/service/service/authority/query/billdetailQuery.do?begin=0&end="+pageEndNum+"&flag=1&devNo="+phoneNumber+"&dateType=now&bill_type=SCP&moPingType=LOCAL&queryDate="+month+"&startDate="+startDate+"&endDate="+endDate));
			        webRequestnow.setRequestParameters(nowList);
			        webRequestnow.setHttpMethod(HttpMethod.GET);
			        //登陆成功 
					HtmlPage succ = webClient.getPage(webRequestnow);
					Thread.sleep(1000);
					String str2="sumRow";
					String str3="sumTime";
					if(succ.asText().contains(str2)&&succ.asText().contains(str3)){
						
				         pageEndNum=new Integer(succ.asText().split("\",\"sumTime")[0].split("sumRow\":\"")[1]);
				         List<NameValuePair> nowList1 = new ArrayList<NameValuePair>();
					        nowList1.add(new NameValuePair("begin","0"));
					        nowList1.add(new NameValuePair("end",pageEndNum+""));
					        nowList1.add(new NameValuePair("flag","1"));  
					        nowList1.add(new NameValuePair("devNo",phoneNumber));
					        //定义为his 表示选择月份
					        nowList1.add(new NameValuePair("dateType","now"));
					        nowList1.add(new NameValuePair("moPingType","LOCAL"));
					        nowList1.add(new NameValuePair("queryDate",month));
					        nowList1.add(new NameValuePair("startDate",startDate));
					        nowList1.add(new NameValuePair("endDate",endDate));
					        nowList1.add(new NameValuePair("bill_type","SCP"));
					        WebRequest webRequestnowSucc = new WebRequest(new URL("http://service.sh.189.cn/service/service/authority/query/billdetailQuery.do?begin=0&end="+pageEndNum+"&flag=1&devNo="+phoneNumber+"&dateType=now&bill_type=SCP&moPingType=LOCAL&queryDate="+month+"&startDate="+startDate+"&endDate="+endDate));
					        webRequestnowSucc.setRequestParameters(nowList1);
					        webRequestnowSucc.setHttpMethod(HttpMethod.GET);
							HtmlPage re = webClient.getPage(webRequestnowSucc);
							Map<String, Object> dmap1 = new HashMap<String, Object>(16);
							String restr = re.asText().replace("\\\"", "\"");
							Thread.sleep(500);
							dmap1.put("item",restr);
							dataList.add(dmap1);
					}else{
						logger.warn("上海电信暂无数据");
						map.put("errorCode", "0001");
				        map.put("errorInfo", "暂无数据");
					}
	                	PushSocket.pushnew(map, uuid, "6000","获取数据成功"); 
				logger.warn("上海电信数据获取成功");	
			   map.put("data", dataList);
			   map.put("UserPassword", servePwd);
	           map.put("UserIphone", phoneNumber);
	           map.put("longitude", longitude);
			   map.put("latitude", latitude);
	           map.put("flag", "11");  
	           map.put("errorCode", "0000");
	           map.put("errorInfo", "查询成功");
	           webClient.close();
	           Resttemplate resttemplate = new Resttemplate();
               map = resttemplate.SendMessage(map, application.getSendip()+"/HSDC/message/telecomCallRecord"); 		 
               String strr="0000";
               String strr1="errorCode";
               
               if(map.get(strr1).equals(strr)) {
					PushSocket.pushnew(map, uuid, "8000","认证成功");
					PushState.state(phoneNumber, "callLog",300);
				}else {
					PushSocket.pushnew(map, uuid, "9000",map.get(strr1).toString());
					PushState.state(phoneNumber, "callLog",200,map.get(strr1).toString());
				}
			}
		  } catch (Exception e) {
			  logger.warn("上海电信",e);
			  map.put("errorCode", "0001");
	          map.put("errorInfo", "网络连接异常!");
	          PushState.state(phoneNumber, "callLog",200,"网络连接异常!");
	          PushSocket.pushnew(map, uuid, "9000","网络连接异常!");
		}    
		return map;
	}
}