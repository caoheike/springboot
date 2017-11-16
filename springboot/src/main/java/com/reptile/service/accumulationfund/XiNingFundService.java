package com.reptile.service.accumulationfund;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;

@Service
public class XiNingFundService {
	  private Logger logger= LoggerFactory.getLogger(XiNingFundService.class);
	  @Autowired
	  private application applications;
	  
	  public Map<String, Object> getImageCode(HttpServletRequest request){
		  Map<String, Object> map = new HashMap<String, Object>();
		  Map<String,String> mapPath=new HashMap<String, String>();
		  WebClient webClient=new WebClientFactory().getWebClient();
	        HttpSession session = request.getSession();
	        
	       // WebClient webClient=new WebClientFactory().getWebClient();
	        //=============图形验证码=====================
	        try {
	        	HtmlPage page=webClient.getPage(new URL("https://www.qhgjj.gov.cn/"));
		
			Thread.sleep(500);
              
			TextPage page7 = webClient.getPage("https://www.qhgjj.gov.cn/jcaptcha?onlynum=true");

             String path = request.getServletContext().getRealPath("/vecImageCode");
             File file = new File(path);
             if (!file.exists()) {
                 file.mkdirs();
             }
             String fileName = "XZCode" + System.currentTimeMillis() + ".png";
             BufferedImage bi = ImageIO.read(page7.getWebResponse().getContentAsStream());
             ImageIO.write(bi, "png", new File(file, fileName));
             
             mapPath.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/vecImageCode/" + fileName);
             map.put("data",mapPath);
             map.put("errorCode", "0000");
             map.put("errorInfo", "验证码获取成功");
             session.setAttribute("XnF-WebClient", webClient);
            /// session.setAttribute("XZ-page", page);
	        }catch (Exception e) {
	        	logger.warn("西宁住房公积金",e);
         		map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
    			e.printStackTrace();
			}
		return map;
		  
	  }
	  public  Map<String, Object> login(HttpServletRequest request,String idCard,String passWord,String catpy,String cityCode,String idCardNum){
		  Map<String, Object> map = new HashMap<String, Object>();
		  Map<String, Object> dateMap = new HashMap<String, Object>();
		  List<Object> dataList = new ArrayList<Object>();
		  List<Object> dataL = new ArrayList< Object>();
		  HttpSession session = request.getSession();

	        Object client = session.getAttribute("XnF-WebClient");
	        if (client == null) {
	        	logger.warn("西宁住房公积金查询，未获取图形验证码");
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "请先获取图形验证码");
	            return map;
	        } else { 
		  try {
			  WebClient webClient = (WebClient) client;
				
			 Thread.sleep(100);
		
			 List<String>  alertList=new ArrayList<String>();   
		     CollectingAlertHandler head=new CollectingAlertHandler(alertList);
		     webClient.setAlertHandler(head);
			String time=""+System.currentTimeMillis();
			String url="https://www.qhgjj.gov.cn/searchPersonLogon.do?logon="+time;
			 WebRequest requests = new WebRequest(new URL(url));
        	 requests.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        	 requests.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");
        	 requests.setAdditionalHeader("Origin", "https://www.qhgjj.gov.cn");
        	 requests.setAdditionalHeader("Referer", "https://www.qhgjj.gov.cn/");
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new NameValuePair("logon",time));
            list.add(new NameValuePair("type","gjjcx"));
            list.add(new NameValuePair("select","2"));
            list.add(new NameValuePair("gjjszd","1"));
            list.add(new NameValuePair("spcode",idCard));
            list.add(new NameValuePair("sppassword",passWord));
            list.add(new NameValuePair("rand",catpy));//图形验证码
            requests.setRequestParameters(list);
			requests.setHttpMethod(HttpMethod.POST);
			HtmlPage page1 = webClient.getPage(requests);
			System.out.println(page1.asXml());
			Thread.sleep(300);
			 if(alertList!=null&&alertList.size()>0){
				 logger.warn("西宁住房公积金查询--"+alertList.get(0));
		        	map.put("errorCode", "0001");
		            map.put("errorInfo", alertList.get(0));
		            return map;
		        }else{
		        	//System.out.println(page1.asXml());
		        	    WebRequest  request1 = new WebRequest(new URL("https://www.qhgjj.gov.cn/searchGrye.do"));
						request1.setHttpMethod(HttpMethod.GET);
		                HtmlPage pages = webClient.getPage(request1);
		                HtmlElement tables=(HtmlElement)pages.getElementsByTagName("table").get(0);
		               // System.out.println(pages.asXml());
		                dataList.add(tables.asXml());
		                dateMap.put("base", dataList);//基本信息
		                logger.warn("西宁住房公积金查询基本信息获取完成");
		                
		        	//===============个人明细============================
		                
		                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		      		    Date date=new Date();  
		                String paramer=sdf.format(date);
		                WebRequest  request3 = new WebRequest(new URL("https://www.qhgjj.gov.cn/searchGrye.do?logon="+paramer));
		                request3.setHttpMethod(HttpMethod.GET);
		                HtmlPage detailPage = webClient.getPage(request3);
		               // HtmlSelect  select=  detailPage.getElementByName("select");
		               
		                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		                int nowYear=new Integer(sdf1.format(date).substring(0,4));
		                
		                WebRequest  request2 =null;
		                logger.warn("西宁住房公积金明细信息获取中");
		                for(int i=0;i<5;i++){
		                	
		                	request2= new WebRequest(new URL("https://www.qhgjj.gov.cn/searchGrmx.do?year="+nowYear));
		                	request2.setHttpMethod(HttpMethod.GET);
			                HtmlPage details = webClient.getPage(request2);
			                HtmlElement table=(HtmlElement) details.getElementsByTagName("table").get(0);
			                System.out.println(nowYear+"*****");
			                System.out.println(table.asXml());
			                nowYear--;
			                dataL.add(table.asXml());
			             
		                }
		                
		                //===================推数据=====================   
		                logger.warn("西宁住房公积金信息获取完成");
		                   dateMap.put("item", dataL);
		  	               map.put("data", dateMap);
				          
			               map.put("userId", idCardNum);
                           map.put("city", cityCode);//002
//				           map.put("errorInfo", "查询成功");
				           Resttemplate resttemplate = new Resttemplate();
//				           //
			             map=resttemplate.SendMessage(map, "http://192.168.3.16:8089/HSDC/person/accumulationFund");//张海敏
						    
			              // map=resttemplate.SendMessage(map, applications.getSendip()+"/HSDC/person/accumulationFund");
						    
			  	         //===================推数据=====================    
		 
		        }
	
	        
		} catch (Exception e) {
			 logger.warn("西宁住房公积金",e);
			map.put("errorCode", "0001");
            map.put("errorInfo", "网络连接异常!");
			e.printStackTrace();
		}
	        }
		return map;
		  
	  }
}
