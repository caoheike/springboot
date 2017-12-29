package com.reptile.service.chinatelecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @author liubin
 *
 */
@Service
public class GansuProvinceService {
	 private Logger logger= LoggerFactory.getLogger(ChengduTelecomService.class);
	 public  Map<String,Object> gansuPhone(HttpServletRequest request,String userNum){
		   Map<String,Object> map = new HashMap<String,Object>(200);
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
	                 String erro="获取验证码";
	                 if (!page1.asText().contains(erro)) {
	                     map.put("errorCode", "0007");
	                     map.put("errorInfo", "操作异常！");
	                     return map;
	                 }
	                WebRequest request1 =new WebRequest(new URL("http://gs.189.cn/web/json/sendSMSRandomNumSMZ.action"));
	                //提交方式
	                request1.setHttpMethod(HttpMethod.POST);
	     			List<NameValuePair> list = new ArrayList<NameValuePair>();
	     			String num="4:"+userNum;
	     			System.out.println(num);
	     			list.add(new NameValuePair("productGroup",num));
	     			request1.setRequestParameters(list);
	     			UnexpectedPage page =	webClient.getPage(request1);
	     			System.out.println(page.getWebResponse().getContentAsString()+"------------");
	     			String one="1";
	     			if(page.getWebResponse().getContentAsString().indexOf(one)!=-1){
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
	 public  Map<String,Object> gansuPhone1(HttpServletRequest request,String userCard,String userNum,String userPass,String catpy,String longitude,String latitude,String uuid){
		   Map<String,Object> map = new HashMap<String,Object>(200);
		   PushState.state(userNum, "callLog",100);
		   PushSocket.pushnew(map, uuid, "1000","登录中");
		   HttpSession session = request.getSession();
	        Object attribute = session.getAttribute("sessionWebClient-GANSU");
	        if (attribute == null) {
	        	PushSocket.pushnew(map, uuid, "3000","登录失败,操作异常!");
	        	PushState.state(userNum, "callLog",200);
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "操作异常!");
	            return map;
	        } else {
	        	try {
	        	PushState.state(userNum, "bankBillFlow",100);
	        	PushSocket.pushnew(map, uuid, "2000","登录成功");
	        	Thread.sleep(2000);
	        	PushSocket.pushnew(map, uuid, "5000","数据获取中");
	        	WebClient webClient = (WebClient) attribute;
	        	WebRequest request1;
	            Map<String,Object>data=new HashMap<String,Object>(200);
	        	Map<String,Object>gansu=new HashMap<String,Object>(200);
	        	List<Map<String,Object>> datalist=new ArrayList<Map<String,Object>>();
					
					String num="4:"+userNum;
					Date date=new Date();
				    String year= new SimpleDateFormat("yyyyMM").format(date);
				    System.out.println(year+"-------------");
				    int num3=3;
					for (int i = 0; i < num3; i++) {
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
					PushSocket.pushnew(map, uuid, "6000","数据获取成功");
					gansu.put("data", datalist);
					gansu.put("UserIphone", userNum);
					map.put("longitude", longitude);
					map.put("latitude", latitude);
					gansu.put("flag", 6);
					gansu.put("userPassword", userPass);
					System.out.println(gansu);
					Resttemplate resttemplate = new Resttemplate();
					map=resttemplate.SendMessage(gansu, ConstantInterface.port+"/HSDC/message/telecomCallRecord");
					String errorCode = "errorCode";
    				String state0 = "0000";
					if(map!=null&&state0.equals(map.get(errorCode).toString())){
					    	PushState.state(userNum, "callLog",300);
			                map.put("errorInfo","查询成功");
			                map.put("errorCode","0000");
			                PushSocket.pushnew(map, uuid, "8000","认证成功");
			         }else{
			        	 PushState.state(userNum, "callLog",200);
			        	 PushSocket.pushnew(map, uuid, "9000",map.get("errorInfo").toString());
			            	//--------------------数据中心推送状态----------------------
			            	//---------------------数据中心推送状态----------------------
			          }
					webClient.close();
					} catch (Exception e) {
						e.printStackTrace();
						PushState.state(userNum, "callLog",200);
						PushSocket.pushnew(map, uuid, "9000","服务繁忙，请稍后再试");
						//---------------------------数据中心推送状态----------------------------------
						 map.clear();
						 map.put("errorInfo","服务繁忙，请稍后再试");
						 map.put("errorCode","0002");
				}
	        }
		 return map;
	 }
}
