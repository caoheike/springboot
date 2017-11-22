package com.reptile.service.ChinaTelecom;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Object;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.service.accumulationfund.QinZhouFundService;
import com.reptile.util.GetMonth;
import com.reptile.util.PushSocket;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;
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
		 Map<String, Object> map = new HashMap<String, Object>();
	     Object attribute = request.getSession().getAttribute("GBmobile-webclient");//从session中获得webClient
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
   		    HtmlPage sendCodePage=(HtmlPage) codePage.executeJavaScript("javascript:sendCode()").getNewPage() ;//发送验证码
   		    if( sendCodePage.getElementById("step_hint").asXml().contains("验证码已发送")){
   		     logger.warn("上海电信，验证码已经发送成功");
   		     map.put("errorInfo", "验证码已经发送成功");
    		 map.put("errorCode", "0000");
   		    }else{
   		     logger.warn("上海电信",sendCodePage.getElementById("step_hint").asText());
   		     map.put("errorInfo", sendCodePage.getElementById("step_hint").asText());
    		 map.put("errorCode", "0001");
   		    }
   		    request.getSession().setAttribute("sendCodePage",sendCodePage );//把页面放到session
   		    request.getSession().setAttribute("webClient", webClient);
   		   
	     } catch (Exception e) {
	    	 logger.warn("上海电信",e);
	    	map.put("errorCode", "0001");
			map.put("errorInfo", "网络异常!");
				
	    	
			//e.printStackTrace();
		}
		return map;
   }
	/**
	 * 获取数据
	 * @param phoneNumber
	 * @param request
	 * @param code
	 * @return
	 */
	public Map<String,Object> getDetial(String phoneNumber,String servePwd,HttpServletRequest request,String code,String longitude,String latitude,String UUID){
		//定单详情查取方式1.选择月份 2.自定义时间
		 Map<String, Object> map = new HashMap<String, Object>();
		 List<Map<String, Object>> dataList=new ArrayList<Map<String, Object>>();
		 WebClient  webClient = (WebClient)request.getSession().getAttribute("webClient");//从session中获得webClient
		 if(webClient==null){
			 PushSocket.push(map, UUID, "0001");
			 logger.warn("上海电信未获取验证码");
	    	 map.put("errorCode", "0001");
		     map.put("errorInfo", "请先获取验证码");	
			 return map;
	    }
	     HtmlPage sendCodePage=(HtmlPage) request.getSession().getAttribute("sendCodePage");
	     HtmlTextInput input_code=(HtmlTextInput) sendCodePage.getElementById("input_code");
		 input_code.setAttribute("value", code);//输入验证码
		 
		  try {
			HtmlPage detailPage=webClient.getPage("http://service.sh.189.cn/service/service/authority/query/billdetail/validate.do?input_code="+code+"&selDevid="+phoneNumber+"&flag=nocw&checkCode=验证码");
			String a=detailPage.getWebResponse().getContentAsString();
			if(a.contains("ME10001")){
				PushSocket.push(map, UUID, "0001");
				logger.warn("上海电信","输入的验证码错误！");
				map.put("errorCode", "0001");
		        map.put("errorInfo", "输入的验证码错误！");
		        return map;
			}else{
				PushSocket.push(map, UUID, "0000");
				logger.warn("上海电信数据获取中...");
			  Thread.sleep(1000);
			 //1.选择月份
	   		   //====================================== his===================================
	   		    Date date=new Date();
	   		    SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy/MM" );//his时间格式
	   		    String month=sdf.format(date);//8月份
	   		    int year=new Integer(month.substring(0,4));
	   		    int mon=new Integer(month.substring(5));
		            int pageEndNum=10;
				    for(int i=0;i<6;i++){ //循环遍历上六个月 	
				    month=GetMonth.beforMonth(year,mon,i);  
			        List<NameValuePair> list = new ArrayList<NameValuePair>();
			        list.add(new NameValuePair("bill_type","SCP"));
			        list.add(new NameValuePair("begin","0"));
			        list.add(new NameValuePair("end","10"));
			        list.add(new NameValuePair("flag","1"));  
			        list.add(new NameValuePair("devNo",phoneNumber));
			        list.add(new NameValuePair("dateType","his"));//定义为his 表示选择月份
			        list.add(new NameValuePair("moPingType","LOCAL"));
			        list.add(new NameValuePair("startDate",""));
			        list.add(new NameValuePair("endDate",""));
			        list.add(new NameValuePair("queryDate",month));
			        WebRequest webRequest = new WebRequest(new URL("http://service.sh.189.cn/service/service/authority/query/billdetailQuery.do?begin=0&end=10&flag=1&devNo="+phoneNumber+"&dateType=his&bill_type=SCP&moPingType=LOCAL&queryDate="+month+"&startDate=&endDate="));
			      
					Thread.sleep(2000);
					
			        webRequest.setRequestParameters(list);
					webRequest.setHttpMethod(HttpMethod.GET);
					HtmlPage click = webClient.getPage(webRequest);//第一次请求目的是获取总条数
					
						Thread.sleep(1000);
					if(click.asText().contains("sumRow")&&click.asText().contains("sumTime")){	
						
						pageEndNum=new Integer(click.asText().split("\",\"sumTime")[0].split("sumRow\":\"")[1]);//获取总条数
						List<NameValuePair> list1 = new ArrayList<NameValuePair>();
				        list1.add(new NameValuePair("bill_type","SCP"));
				        list1.add(new NameValuePair("begin","0"));
				        list1.add(new NameValuePair("end",""+pageEndNum));
				        list1.add(new NameValuePair("flag","1"));  
				        list1.add(new NameValuePair("devNo",phoneNumber));
				        list1.add(new NameValuePair("dateType","his"));//定义为his 表示选择月份
				        list1.add(new NameValuePair("moPingType","LOCAL"));
				        list1.add(new NameValuePair("startDate",""));
				        list1.add(new NameValuePair("endDate",""));
				        list1.add(new NameValuePair("queryDate",month));
						
					    WebRequest webRequestResult = new WebRequest(new URL("http://service.sh.189.cn/service/service/authority/query/billdetailQuery.do?begin=0&end="+pageEndNum+"&flag=1&devNo="+phoneNumber+"&dateType=his&bill_type=SCP&moPingType=LOCAL&queryDate="+month+"&startDate=&endDate="));
					    webRequestResult.setRequestParameters(list1);
					    webRequestResult.setHttpMethod(HttpMethod.GET);
					   
						HtmlPage results = webClient.getPage(webRequestResult);  
				        //System.out.println(month+"****"+ webClient.getPage(webRequestResult).getWebResponse().getContentAsString());
						  Map<String, Object> dmap = new HashMap<String, Object>();
						  String str = results.asText().replace("\\\"", "\""); 
						 dmap.put("item",str);
						 dataList.add(dmap);
						//map.put(GetMonth.beforMonth(year,mon,i+1),);
					}else{
						logger.warn("上海电信暂无数据");
						map.put("errorCode", "0001");
				        map.put("errorInfo", "暂无数据");
					}
						Thread.sleep(1000);
					
				 } 
		         
		        //2.自定义时间
		   	 //====================================== now===================================
				    SimpleDateFormat sdfNow =  new SimpleDateFormat( "yyyy-MM-dd" );//NOW时间格式
			   	    String endDate=sdfNow.format(date);//结束时间当前时间
			   	    String startDate=endDate.substring(0,8)+"01";  //本月第一天
		        	List<NameValuePair> nowList = new ArrayList<NameValuePair>();
			        nowList.add(new NameValuePair("begin","0"));
			        nowList.add(new NameValuePair("end",pageEndNum+""));
			        nowList.add(new NameValuePair("flag","1"));  
			        nowList.add(new NameValuePair("devNo",phoneNumber));
			        nowList.add(new NameValuePair("dateType","now"));//定义为his 表示选择月份
			        nowList.add(new NameValuePair("moPingType","LOCAL"));
			        nowList.add(new NameValuePair("queryDate",month));
			        nowList.add(new NameValuePair("startDate",startDate));
			        nowList.add(new NameValuePair("endDate",endDate));
			        nowList.add(new NameValuePair("bill_type","SCP"));
			        WebRequest webRequestnow = new WebRequest(new URL("http://service.sh.189.cn/service/service/authority/query/billdetailQuery.do?begin=0&end="+pageEndNum+"&flag=1&devNo="+phoneNumber+"&dateType=now&bill_type=SCP&moPingType=LOCAL&queryDate="+month+"&startDate="+startDate+"&endDate="+endDate));
			        webRequestnow.setRequestParameters(nowList);
			        webRequestnow.setHttpMethod(HttpMethod.GET);
					HtmlPage succ = webClient.getPage(webRequestnow);//登陆成功 
					Thread.sleep(1000);
					if(succ.asText().contains("sumRow")&&succ.asText().contains("sumTime")){
						
					     //System.out.println(click.asText());//获取成功
						
				         pageEndNum=new Integer(succ.asText().split("\",\"sumTime")[0].split("sumRow\":\"")[1]);
				         List<NameValuePair> nowList1 = new ArrayList<NameValuePair>();
					        nowList1.add(new NameValuePair("begin","0"));
					        nowList1.add(new NameValuePair("end",pageEndNum+""));
					        nowList1.add(new NameValuePair("flag","1"));  
					        nowList1.add(new NameValuePair("devNo",phoneNumber));
					        nowList1.add(new NameValuePair("dateType","now"));//定义为his 表示选择月份
					        nowList1.add(new NameValuePair("moPingType","LOCAL"));
					        nowList1.add(new NameValuePair("queryDate",month));
					        nowList1.add(new NameValuePair("startDate",startDate));
					        nowList1.add(new NameValuePair("endDate",endDate));
					        nowList1.add(new NameValuePair("bill_type","SCP"));
					        WebRequest webRequestnowSucc = new WebRequest(new URL("http://service.sh.189.cn/service/service/authority/query/billdetailQuery.do?begin=0&end="+pageEndNum+"&flag=1&devNo="+phoneNumber+"&dateType=now&bill_type=SCP&moPingType=LOCAL&queryDate="+month+"&startDate="+startDate+"&endDate="+endDate));
					        webRequestnowSucc.setRequestParameters(nowList1);
					        webRequestnowSucc.setHttpMethod(HttpMethod.GET);
							HtmlPage re = webClient.getPage(webRequestnowSucc);
							Map<String, Object> dmap1 = new HashMap<String, Object>();
							String str = re.asText().replace("\\\"", "\"");
							Thread.sleep(500);
							dmap1.put("item",str);
							dataList.add(dmap1);
					}else{
						logger.warn("上海电信暂无数据");
						map.put("errorCode", "0001");
				        map.put("errorInfo", "暂无数据");
					}
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
			}
		  } catch (Exception e) {
			  logger.warn("上海电信",e);
			  map.put("errorCode", "0001");
	          map.put("errorInfo", "网络连接异常!");
			//e.printStackTrace();
		}    
		return map;
	}
}