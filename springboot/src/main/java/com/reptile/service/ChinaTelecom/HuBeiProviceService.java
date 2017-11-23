package com.reptile.service.ChinaTelecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.Dates;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HuBeiProviceService {
	 @Autowired
	  private application applications;
	public static Map<String,Object> hubeicode(HttpServletRequest request,String PhoneCode,String PassPhone){
		System.out.println("湖北电信");
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
		            WebRequest requests = new WebRequest(new URL("http://hb.189.cn/pages/selfservice/order/orderIndex_new.jsp?trackPath=shouyezuodao-feiyong-dingdanchaxun"));
		            requests.setHttpMethod(HttpMethod.GET);
		            HtmlPage page = webClient.getPage(requests);
		            Thread.sleep(4000);
		            WebRequest request1 = new WebRequest(new URL("http://hb.189.cn/pages/selfservice/feesquery/detailListQuery.jsp"));
		            request1.setHttpMethod(HttpMethod.GET);
		            HtmlPage page1 = webClient.getPage(request1);
		            Thread.sleep(4000);
		            page1.getElementById("txtAccount").setAttribute("value", PhoneCode);
		            page1.getElementById("txtPassword").setAttribute("value", PassPhone);
		            HtmlPage page2 = page1.getElementById("loginbtn").click();
		            HtmlInput hiiden=  (HtmlInput) page2.getElementById("CITYCODE");
		            String citycode= hiiden.getAttribute("value");
		            Thread.sleep(4000);
		            WebRequest requests2 =new WebRequest(new URL("http://hb.189.cn/feesquery_toListQuery.action"));
		            requests2.setHttpMethod(HttpMethod.POST);
		         	HtmlPage note =webClient.getPage(requests2);
		         	Thread.sleep(5000);
		            WebRequest request2 =new WebRequest(new URL("http://hb.189.cn/feesquery_PhoneIsDX.action"));
		     		request2.setHttpMethod(HttpMethod.POST);//提交方式
		     		List<NameValuePair> list = new ArrayList<NameValuePair>();
		     		list.add(new NameValuePair("productNumber",PhoneCode));
		     		list.add(new NameValuePair("cityCode",citycode));
		     		list.add(new NameValuePair("sentType","C"));
		     		list.add(new NameValuePair("ip","0"));
		     		request2.setRequestParameters(list);
		     		HtmlPage backtrack =webClient.getPage(request2);
		     		Thread.sleep(4000);
		     		if(backtrack.asText().indexOf(PhoneCode)!=-1){
		     			System.out.println(backtrack.asText()+"--成功--");
		     			session.setAttribute("sessionWebClient-HUBEI", webClient);
		     			map.put("errorCode", "0000");
		     			map.put("errorInfo", "验证码发送成功!");
		     		}else{
		     			System.out.println(backtrack.asText()+"-----失败-----");
		     			map.put("errorCode", "0001");
		     			map.put("errorInfo", "服务器异常!");
		     		}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					   e.printStackTrace();
		               map.put("errorCode", "0001");
		               map.put("errorInfo", "请再尝试发送验证码");
				}
	        }
		return map;
	}
	public Map<String,Object> hubeiphone(HttpServletRequest request, String PhoneCode,String PhoneNume,String PhonePass,String longitude,String latitude){
		 Map<String,Object> map = new HashMap<String,Object>();
		  HttpSession session = request.getSession();
	        Object attribute = session.getAttribute("sessionWebClient-HUBEI");
	        if (attribute == null) {
	        	
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "操作异常!");
	            return map;
	        } else {
	        try {
	        	
	        	PushState.state(PhoneNume, "callLog",100);
	        	WebClient webClient = (WebClient) attribute;
				WebRequest requests=new WebRequest(new URL("http://hb.189.cn/validateWhiteList.action"));
				requests.setHttpMethod(HttpMethod.POST);//提交方式
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new NameValuePair("accnbr",PhoneNume));
				requests.setRequestParameters(list);
				HtmlPage back1=webClient.getPage(requests);
				String stat=back1.asText();
				System.out.println(stat);
				Thread.sleep(3000);
				WebRequest requests2=new WebRequest(new URL("http://hb.189.cn/feesquery_checkCDMAFindWeb.action"));
				requests2.setHttpMethod(HttpMethod.POST);//提交方式
				List<NameValuePair> list2 = new ArrayList<NameValuePair>();
				list2.add(new NameValuePair("random",PhoneCode));
				list2.add(new NameValuePair("sentType","C"));
				requests2.setRequestParameters(list2);
				HtmlPage back2=webClient.getPage(requests2);
				String stat2=back2.asText();
				System.out.println(stat2);				
				  Map<String,Object>HUBEI=new HashMap<String,Object>();
				  List<Map<String,Object>> datalist=new ArrayList<Map<String,Object>>();
				for (int i = 0; i < 3; i++) {
					
					Map<String,Object> detailed=new HashMap<String,Object>();
					List<Map<String ,Object>> eachMonthList =new ArrayList<Map<String ,Object>>();					
					List<NameValuePair> list3 = new ArrayList<NameValuePair>();
					WebRequest requests3=new WebRequest(new URL("http://hb.189.cn/feesquery_querylist.action"));
					requests3.setHttpMethod(HttpMethod.POST);//提交方式
					String Month=Dates.beforMonth(i)+"0000";
					System.out.println(Month+"----------------------");
					list3.add(new NameValuePair("startMonth",Month));
					list3.add(new NameValuePair("type",stat2));
					list3.add(new NameValuePair("random",PhoneCode));
					requests3.setRequestParameters(list3);
					HtmlPage back3=webClient.getPage(requests3);
					String Phonedetailed=back3.asText();
					if(Phonedetailed.indexOf(PhoneCode)!=-1){
		     			map.put("errorCode", "0002");
		     			map.put("errorInfo", "获取数据为空!");
		     			return map;
		     			
		     		}
					System.out.println(Phonedetailed+"----------------------");
					Map<String ,Object> pageMap=new HashMap();
//					pageMap.put("pageData", Phonedetailed);
//					eachMonthList.add(pageMap);
					//detailed.put("item", Phonedetailed);
					Thread.sleep(3000);
					System.out.println("-------------获取下一页的数值------------------");
					for (int j = 1; j < 4; j++) {
						Map<String ,Object> eachpageMap=new HashMap();
						Thread.sleep(2000);
						List<NameValuePair> list4 = new ArrayList<NameValuePair>();
						WebRequest requests4=new WebRequest(new URL("http://hb.189.cn/feesquery_pageQuery.action"));
						requests4.setHttpMethod(HttpMethod.POST);//提交方式
						list4.add(new NameValuePair("page",j+""));
						list4.add(new NameValuePair("showCount","100"));
						requests4.setRequestParameters(list4);
						HtmlPage back4=webClient.getPage(requests4);
						String Phonedetailed2=back4.asText();
						eachpageMap.put("pageData", Phonedetailed2);
						eachMonthList.add(eachpageMap);
						System.out.println("--------第"+j+"页------------"+Phonedetailed2+"----------------------");
//						detailed.put("Month"+"--"+j, Phonedetailed2);
					}
//					eachMonthList.add(detailed);					
					detailed.put("item", eachMonthList);
					datalist.add(detailed);
				}
				
				System.out.println(datalist);
				HUBEI.put("data", datalist);
				HUBEI.put("UserIphone", PhoneNume);
				map.put("longitude", longitude);
				map.put("latitude", latitude);
				HUBEI.put("flag", 12);
				HUBEI.put("UserPassword", PhonePass);
				Resttemplate resttemplate = new Resttemplate();
				map=resttemplate.SendMessage(HUBEI, applications.getSendip()+"/HSDC/message/telecomCallRecord");
				if(map!=null&&"0000".equals(map.get("errorCode").toString())){
					
			    	PushState.state(PhoneNume, "callLog",300);
	                map.put("errorInfo","查询成功");
	                map.put("errorCode","0000");
	         }else{
	            	//--------------------数据中心推送状态----------------------
	            	PushState.state(PhoneNume, "callLog",200);
	            	//---------------------数据中心推送状态----------------------
	                map.put("errorInfo","响应异常,请重试");
	                map.put("errorCode","0001");
	          }
				webClient.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
				PushState.state(PhoneNume, "callLog",200);
				//---------------------------数据中心推送状态----------------------------------
				 map.clear();
				 map.put("errorInfo","服务繁忙，请稍后再试");
				 map.put("errorCode","0002");
			}
	        }
	        
		return map;
	}
}
