package com.reptile.service.accumulationfund;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.htmlparser.tags.Html;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.gson.JsonObject;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;

@Service
public class KunMingFundService {
	  private Logger logger= LoggerFactory.getLogger(KunMingFundService.class);
	  @Autowired
	  private application applications; 
	  /**
	   * 获取图形验证码
	   * */
	  public Map<String, Object> getImageCode(HttpServletRequest request){
		  Map<String, Object> map = new HashMap<String, Object>();
		  Map<String,String> mapPath=new HashMap<String, String>();
	        HttpSession session = request.getSession();
	        
	        WebClient webClient=new WebClientFactory().getWebClient();
	        //=============图形验证码=====================
	        try {
				HtmlPage page=webClient.getPage(new URL("http://222.172.223.90:8081/kmnbp/"));
		
			Thread.sleep(500);
              
			 UnexpectedPage page7 = webClient.getPage("http://222.172.223.90:8081/kmnbp/vericode.jsp");

             String path = request.getServletContext().getRealPath("/vecImageCode");
             File file = new File(path);
             if (!file.exists()) {
                 file.mkdirs();
             }
             String fileName = "XZCode" + System.currentTimeMillis() + ".png";
             BufferedImage bi = ImageIO.read(page7.getInputStream());
             ImageIO.write(bi, "png", new File(file, fileName));
             
             mapPath.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/vecImageCode/" + fileName);
             map.put("data",mapPath);
             map.put("errorCode", "0000");
             map.put("errorInfo", "验证码获取成功");
             session.setAttribute("KM-WebClient", webClient);
            /// session.setAttribute("XZ-page", page);
	        }catch (Exception e) {
	        	logger.warn("昆明住房公积金",e);
         		map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
    			e.printStackTrace();
			}
		return map; 
		  
		
	}
	  
	  /**
	   * 获取详单
	   */
	  
	  public  Map<String, Object> getDetail(HttpServletRequest request,String idCard,String passWord,String catpy,String cityCode){
		  Map<String, Object> map = new HashMap<String, Object>();
		  Map<String, Object> dateMap = new HashMap<String, Object>();
		  List<Object> dataList = new ArrayList<Object>();
		  List<Object> dataL = new ArrayList<Object>();
		  HttpSession session = request.getSession();

	        Object client = session.getAttribute("KM-WebClient");
	        if (client == null) {
	        	logger.warn("昆明住房公积金未获取图形验证码");
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "请先获取图形验证码");
	            return map;
	        } else { 
		  try {
			 
			  WebClient webClient = (WebClient) client;
			 String url="http://222.172.223.90:8081/kmnbp/per.login";
			 WebRequest requests = new WebRequest(new URL(url));
        	 requests.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        	 requests.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");
        	 requests.setAdditionalHeader("Origin", "http://222.172.223.90:8081");
        	 requests.setAdditionalHeader("Referer", "http://222.172.223.90:8081/kmnbp/");
             List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new NameValuePair("certinum",idCard));
            list.add(new NameValuePair("unitcode",""));
            list.add(new NameValuePair("devcode",""));
            list.add(new NameValuePair("pwd",passWord));
            list.add(new NameValuePair("vericode",catpy));//图形验证码
            requests.setRequestParameters(list);
			requests.setHttpMethod(HttpMethod.POST);
			HtmlPage page1 = webClient.getPage(requests);
			Thread.sleep(300);
			//System.out.println(page1.asXml());
			String tip=page1.asXml();
			System.out.println(tip);
			if(tip.contains("操作失败")){
				logger.warn("昆明住房公积金获取失败--"+page1.executeJavaScript("$('.text').text()").getJavaScriptResult());
				map.put("errorCode", "0001");
	            map.put("errorInfo", page1.executeJavaScript("$('.text').text()").getJavaScriptResult());
	            //System.out.println();
			}else{
				if(tip.contains("公积金基本信息查询")){
					  logger.warn("昆明住房公积金基本信息获取中");
					   WebRequest  request1 = new WebRequest(new URL("http://222.172.223.90:8081/kmnbp/init.summer?_PROCID=70000013"));
						request1.setHttpMethod(HttpMethod.GET);
		                HtmlPage pages = webClient.getPage(request1);
		                Thread.sleep(100);
		                HtmlTable table=   (HtmlTable) pages.getElementById("ct_form");
		                dataList.add(table.asXml());//基本数据
					    System.out.println(table.asXml());
		                dateMap.put("base", dataList);//基本信息
					//=====================明细======================
		                logger.warn("昆明住房公积金明细信息获取中");
	                WebRequest  request2 = new WebRequest(new URL("http://222.172.223.90:8081/kmnbp/init.summer?_PROCID=70000002"));
					request2.setHttpMethod(HttpMethod.GET);
	                HtmlPage page2 = webClient.getPage(request2);
	                Thread.sleep(300); 
		             HtmlSelect select=(HtmlSelect) page2.getElementById("year");
	                 int years=select.getChildElementCount();
	                
		                String paramer=  page2.asXml().split("var poolSelect =")[1].split("\\}")[0]+"}";
		                JSONObject json=new JSONObject().fromObject(paramer);
		                HtmlElement host=  (HtmlElement) page2.getElementsByTagName("textarea").get(0);
		               String hosts=host.asText();
		                HtmlElement pool=  (HtmlElement) page2.getElementsByTagName("textarea").get(1);
		               String pools=pool.asText();
		                
		               
		               List<String>  alertList=new ArrayList<String>();   
		  		     CollectingAlertHandler head=new CollectingAlertHandler(alertList);
		  		     webClient.setAlertHandler(head);
		  		   Date date=new Date();
		       		  SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy" );
		       		  String nowYear=sdf.format(date);
		       		  int nYear=new Integer(nowYear);
		       		  String begdate=nYear+"-01-01";
		       		  String enddate=nYear+"-12-31";
		       		  String instancenum="";
		       		  String result="";
		       		 for (int i = 0; i < years; i++) {  
		              String url1="http://222.172.223.90:8081/kmnbp/command.summer?uuid="+System.currentTimeMillis();
		   			 WebRequest request3 = new WebRequest(new URL(url1));
		   			request3.setAdditionalHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		   			request3.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		   			request3.setAdditionalHeader("Origin", "http://222.172.223.90:8081");
		   			request3.setAdditionalHeader("Referer", "http://222.172.223.90:8081/kmnbp/init.summer?_PROCID=70000002");
		               List<NameValuePair> lists=new ArrayList<NameValuePair>();
		               lists.add(new NameValuePair("$page",json.getString("$page")));
		               lists.add(new NameValuePair("_ACCNUM",json.getString("_ACCNUM")));
		          
		               lists.add(new NameValuePair("_RW",json.getString("_RW")));
		               lists.add(new NameValuePair("_PAGEID",json.getString("_PAGEID")));
		               lists.add(new NameValuePair("_IS",json.getString("_IS")));
		               lists.add(new NameValuePair("_UNITACCNAME",json.getString("_UNITACCNAME")));
		               lists.add(new NameValuePair("_LOGIP",json.getString("_LOGIP")));
		               lists.add(new NameValuePair("_ACCNAME",json.getString("_ACCNAME")));
		               lists.add(new NameValuePair("isSamePer",json.getString("isSamePer")));
		               lists.add(new NameValuePair("_PROCID",json.getString("_PROCID")));
		               
		               lists.add(new NameValuePair("_SENDOPERID",json.getString("_SENDOPERID")));
		               lists.add(new NameValuePair("_DEPUTYIDCARDNUM",json.getString("_DEPUTYIDCARDNUM")));
		               
		               lists.add(new NameValuePair("_SENDTIME",json.getString("_SENDTIME")));
		               lists.add(new NameValuePair("_BRANCHKIND",json.getString("_BRANCHKIND")));
		               lists.add(new NameValuePair("_SENDDATE",json.getString("_SENDDATE")));
		               lists.add(new NameValuePair("CURRENT_SYSTEM_DATE",json.getString("CURRENT_SYSTEM_DATE")));
		               
		               lists.add(new NameValuePair("_TYPE",json.getString("_TYPE")));
		               lists.add(new NameValuePair("_ISCROP",json.getString("_ISCROP")));
		               lists.add(new NameValuePair("_PORCNAME",json.getString("_PORCNAME")));
		               lists.add(new NameValuePair("_WITHKEY",json.getString("_WITHKEY")));
		               lists.add(new NameValuePair("accnum",json.getString("_ACCNUM")));
		               
		               
		              
		               lists.add(new NameValuePair("begdate",begdate));
		               lists.add(new NameValuePair("enddate",enddate));
		               lists.add(new NameValuePair("year",""+nYear));
		               if(i!=0){
		            	   lists.add(new NameValuePair("dynamicTable_flag","0"));
		            	   lists.add(new NameValuePair("instancenum",instancenum));
		              }
		               request3.setRequestParameters(lists);
		               request3.setHttpMethod(HttpMethod.POST);
		   			 UnexpectedPage pages1 = webClient.getPage(request3);	
		   			
		   			 if(alertList!=null&&alertList.size()>0){
		   				logger.warn("昆明住房公积金获取过程中失败--",alertList.get(0)); 
				        	map.put("errorCode", "0001");
				            map.put("errorInfo", alertList.get(0));
				            return map;
				        }else{
	                    // System.out.println(pages1.getWebResponse().getContentAsString());
	                     
	                     String time=""+System.currentTimeMillis();
	                     String url2="http://222.172.223.90:8081/kmnbp/dynamictable?uuid="+time;
	        			 WebRequest requests1 = new WebRequest(new URL(url2));
	        			 requests1.setAdditionalHeader("Accept", "application/json, text/javascript, */*; q=0.01");
	        			 requests1.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	        			 requests1.setAdditionalHeader("Origin", "http://222.172.223.90:8081");
	        			 requests1.setAdditionalHeader("Referer", "http://222.172.223.90:8081/kmnbp/init.summer?_PROCID=70000002");
	                    List<NameValuePair> list2=new ArrayList<NameValuePair>();
	                    list2.add(new NameValuePair("uuid",time));
	                    list2.add(new NameValuePair("dynamicTable_id","datalist2"));
	                    if(i==0){
	                    	list2.add(new NameValuePair("dynamicTable_currentPage","0"));
	                    }else{
	                    	list2.add(new NameValuePair("dynamicTable_currentPage","1"));
	                    }
	                    
	                    list2.add(new NameValuePair("dynamicTable_pageSize","100"));
	                    list2.add(new NameValuePair("dynamicTable_nextPage","1"));
	                    
	                    list2.add(new NameValuePair("dynamicTable_page","/ydpx/70000002/700002_01.ydpx"));
	                    list2.add(new NameValuePair("dynamicTable_paging","true"));
	                    list2.add(new NameValuePair("dynamicTable_configSqlCheck","0"));
	                    
	                    list2.add(new NameValuePair("errorFilter","1=1"));
	                    
	                    list2.add(new NameValuePair("begdate",begdate));
	                    list2.add(new NameValuePair("enddate",enddate));
	                    list2.add(new NameValuePair("year",""+nYear));
	                    list2.add(new NameValuePair("accnum",json.getString("_ACCNUM")));
	               
	                    list2.add(new NameValuePair("_APPLY","0"));
	                    list2.add(new NameValuePair("_CHANNEL","1"));
	                    list2.add(new NameValuePair("PROCID","70000002"));
	                    		
	                    list2.add(new NameValuePair("DATAlISTGHOST",hosts));
	                    list2.add(new NameValuePair("_DATAPOOL_",pools));
	                   
	                    requests1.setRequestParameters(list2);
	                    requests1.setHttpMethod(HttpMethod.POST);
	                    UnexpectedPage page21 = webClient.getPage(requests1);
	        			Thread.sleep(300);                     
	        		 // System.out.println(begdate+"********"+enddate);
	        		 
	        		  result=page21.getWebResponse().getContentAsString();
	        		  JSONObject res=new JSONObject().fromObject(result);
	        		Map maps=  (Map) res.get("data");
	        		Integer count=	 (Integer) maps.get("totalCount");
	        	   if(count!=0){
	        		   List<Map>  listMap=	(List<Map>) maps.get("data");  
	        		   dataL.add(listMap);
	        	    }
				        } 
		   			 if(i==0){
		   				instancenum=json.getString("_IS");
		   			 }
		   		   nYear--;
	       		   begdate=nYear+"-01-01";
	       		   enddate=nYear+"-12-31";
	       		   Thread.sleep(100);
		               }  
		       	   logger.warn("昆明住房公积金获取成功");     
	               dateMap.put("item", dataL);
  	               map.put("data", dateMap);
		          
	               map.put("userId", idCard);
                   map.put("city", cityCode);//004
                   map.put("errorCode", "0000");
	               map.put("errorInfo", "查询成功");
		           Resttemplate resttemplate = new Resttemplate();
	              // map=resttemplate.SendMessage(map, "http://192.168.3.16:8089/HSDC/person/accumulationFund");//张浩敏
		           map=resttemplate.SendMessage(map,applications.getSendip()+ "/HSDC/person/accumulationFund");//张浩敏
		            	
		               
				}else{
					logger.warn("昆明住房公积金获取失败");
					map.put("errorCode", "0001");
		            map.put("errorInfo", "网络连接异常!");
		            return map;
				}
				
			}
			
			
			
		}catch (Exception e) {
			
			logger.warn("昆明住房公积金获取失败",e);
			map.put("errorCode", "0001");
            map.put("errorInfo", "网络连接异常!");
			e.printStackTrace();
		}
	        }	
		return map;
		  
	  }


}
