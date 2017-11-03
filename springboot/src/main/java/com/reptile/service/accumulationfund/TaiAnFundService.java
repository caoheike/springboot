package com.reptile.service.accumulationfund;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.util.UrlEncoded;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.UriEncoder;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFrame;
import com.gargoylesoftware.htmlunit.html.HtmlFrameSet;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLIFrameElement;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;

@Service
public class TaiAnFundService {
	 private Logger logger= LoggerFactory.getLogger(TaiAnFundService.class);
	 @Autowired
	  private application applications;
	  private final static String detailUrl=" http://tagjj.com:7001/wscx/zfbzgl/gjjmxcx/gjjmx_cx.jsp";
	public  Map<String, Object> login(HttpServletRequest request,String idCard,String passWord){
		 Map<String, Object> map = new HashMap<String, Object>();
		 Map<String, Object> dateMap = new HashMap<String, Object>();
		 List<String>  dataList=new ArrayList<String>();
         WebClient webClient=new WebClientFactory().getWebClient();
         
		  try {
				HtmlPage page=webClient.getPage(new URL("http://tagjj.com:7001/wscx/"));
               String url="http://tagjj.com:7001/wscx/zfbzgl/zfbzsq/login_hidden.jsp?password="+passWord+"&sfzh="+idCard+"&cxyd="+URLEncoder.encode("当前年度", "gb2312")+"&dbname=gjjmx9&dlfs=0";
			  WebRequest requests = new WebRequest(new URL(url));
			  requests.setHttpMethod(HttpMethod.GET);
			  HtmlPage page1=webClient.getPage(requests);
			  Thread.sleep(3000);
			  if(page1.asXml().contains("alert")){
				  String tip=page1.asXml().split("alert")[1].split("\\(")[1].split("\\)")[0];
				  logger.warn("泰安市公积金"+tip);
				  map.put("errorCode", "0001");
				  map.put("errorInfo", tip);
				  
			  }else{
				  logger.warn("泰安市公积金基本数据获取中");
				  //System.out.println("登陆成功");
				  String zgzh=page1.getElementByName("zgzh").getAttribute("value");
				  String sfzh=page1.getElementByName("sfzh").getAttribute("value");
				  String zgxm=page1.getElementByName("zgxm").getAttribute("value");
				  String dwbm=page1.getElementByName("dwbm").getAttribute("value");
				  String zgzt=page1.getElementByName("zgzt").getAttribute("value");
				  
				  String url1="http://tagjj.com:7001/wscx/zfbzgl/zfbzsq/main_menu.jsp?zgzh="+zgzh+"&sfzh="+sfzh+"&zgxm="+URLEncoder.encode(zgxm, "gb2312")+"&dwbm="+dwbm+"&zgzt="+URLEncoder.encode(zgzt, "gb2312")+"&cxyd="+URLEncoder.encode("当前年度", "gb2312")+"&dbname=gjjmx9";
				  WebRequest requests1 = new WebRequest(new URL(url1));
				  requests1.setHttpMethod(HttpMethod.GET);
				  HtmlPage page2=webClient.getPage(requests1);	  
				  HtmlTable table= (HtmlTable) page2.getByXPath("/html/body/table[2]/tbody/tr[1]/td[2]/table[4]/tbody/tr/td/table/tbody/tr/td/table[1]").get(0); 
				//  System.out.println(table.asXml());
				  
				  dateMap.put("base", table.asXml());//基本数据
				  logger.warn("泰安市公积金基本数据获取完成");
				//=============================明细========================
				  logger.warn("泰安市公积金数据明细获取中");
				  List<NameValuePair> list=new ArrayList<NameValuePair>();
				  list.add(new NameValuePair("sfzh",idCard ));
				  list.add(new NameValuePair("zgxm", URLEncoder.encode(zgxm, "gb2312")));
				  list.add(new NameValuePair("zgzh",zgzh ));

				  list.add(new NameValuePair("dwbm",dwbm ));
				  list.add(new NameValuePair("cxyd",URLEncoder.encode("当前年度", "gb2312") ));
				  list.add(new NameValuePair("zgzt",URLEncoder.encode(zgzt, "gb2312")));
				
				  HtmlPage detailPage=getPages(webClient,detailUrl, list, HttpMethod.POST);
				 
				  HtmlTable tables=   (HtmlTable) detailPage.getByXPath("/html/body/table[2]/tbody/tr[1]/td[2]/table[5]/tbody/tr[1]/td/table").get(0);
				  HtmlElement totalPage =(HtmlElement) detailPage.getByXPath("/html/body/table[2]/tbody/tr[1]/td[2]/table[5]/tbody/tr[2]/td").get(0);
				  String details=totalPage.asText().split("共")[1].split("页")[0];
				  int num=new Integer(details);
				  if(num==1&&tables.asXml().contains("jtpsoft")){
					  dataList.add(tables.asXml());//当前年度
					  //System.out.println(tables.asXml());
				  }
				//=========================  其余年度明细==============
				  HtmlSelect select=detailPage.getElementByName("cxydone");
				  int seleNum=select.getChildElementCount();
				  
				  for (int i = 1; i < seleNum-1; i++) {
					   String da="";
					   detailPage=getPages2(webClient, detailUrl, select.getOption(i).asText(), select.getOption(i).asText(), "1", details, "当前年度", zgzh, sfzh, zgxm, dwbm, HttpMethod.POST);
					   Thread.sleep(500);
					   tables=   (HtmlTable) detailPage.getByXPath("/html/body/table[2]/tbody/tr[1]/td[2]/table[5]/tbody/tr[1]/td/table").get(0);
					   totalPage =(HtmlElement) detailPage.getByXPath("/html/body/table[2]/tbody/tr[1]/td[2]/table[5]/tbody/tr[2]/td").get(0);
					   
					   details=totalPage.asText().split("共")[1].split("页")[0];
					   num=new Integer(details);
					  if(num==1&&tables.asXml().contains("jtpsoft")){
						  dataList.add(tables.asXml());//当前年度
						  System.out.println(tables.asXml());
					  }else if(num>1){
						  for (int j = 1; j <= num; j++) {
							  detailPage=getPages2(webClient, detailUrl, select.getOption(i).asText(), select.getOption(i).asText(), j+"", details, "当前年度", zgzh, sfzh, zgxm, dwbm, HttpMethod.POST);
							  tables=   (HtmlTable) detailPage.getByXPath("/html/body/table[2]/tbody/tr[1]/td[2]/table[5]/tbody/tr[1]/td/table").get(0);
							  da=da+tables.asXml();
							 
						  }
						  dataList.add(da);//当前年度
						  System.out.println(da);
					  }  
				}
				  logger.warn("泰安市公积金数据明细获取完成"); 
			    	dateMap.put("item", dataList);
			    	map.put("errorInfo", "查询成功");
					map.put("errorCode", "0000");
					map.put("data", dateMap);
					map.put("city", "007");
					map.put("userId", idCard);//TODO
				    //map = new Resttemplate().SendMessage(map,"http://192.168.3.16:8089/HSDC/person/accumulationFund");
					map = new Resttemplate().SendMessage(map,applications.getSendip()+"/HSDC/person/accumulationFund");
			 
			  }
		  }catch (Exception e) {
			  logger.warn("泰安市公积金",e);
			  map.put("errorCode", "0001");
              map.put("errorInfo", "网络连接异常!");
			e.printStackTrace();
		}
		
		return map;
		
	}
	
	/***
	 * 获得HtmlPage  
	 * @param webClient
	 * @param url 要访问的url
	 * @param list  参数
	 * @param methot  post、get
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws IOException
	 */
	 public   HtmlPage getPages(WebClient webClient,String url,List<NameValuePair> list,HttpMethod methot) throws FailingHttpStatusCodeException, IOException{
		
		    WebRequest requests = new WebRequest(new URL(url));
            requests.setRequestParameters(list);
			requests.setHttpMethod(methot);
			
		  return webClient.getPage(requests);
		 
	 }
	 /**
	  * 查询其余年度的详单
	  * @param webClient
	  * @param url
	  * @param cxydone
	  * @param cxydtwo
	  * @param yss
	  * @param totalpages
	  * @param cxyd
	  * @param zgzh
	  * @param sfzh
	  * @param zgxm
	  * @param dwbm
	  * @param methot
	  * @return
	  * @throws FailingHttpStatusCodeException
	  * @throws IOException
	  */
	 public  HtmlPage getPages2(WebClient webClient,String url,String cxydone,String cxydtwo,String yss,String totalpages,String cxyd,String zgzh,String sfzh,String zgxm,String dwbm,HttpMethod methot) throws FailingHttpStatusCodeException, IOException{
		   List<NameValuePair> list=new ArrayList<NameValuePair>();
		  list.add(new NameValuePair("cxydone",cxydone ));
		  list.add(new NameValuePair("cxydtwo", cxydtwo));
		  list.add(new NameValuePair("yss",yss ));

		  list.add(new NameValuePair("totalpages",totalpages ));
		  list.add(new NameValuePair("cxyd",URLEncoder.encode(cxyd, "gb2312") ));
		  list.add(new NameValuePair("zgzh",zgzh));
		  
		  
		  list.add(new NameValuePair("sfzh",sfzh ));
		  list.add(new NameValuePair("zgxm",URLEncoder.encode(zgxm, "gb2312") ));
		  list.add(new NameValuePair("dwbm",dwbm));
	
		  return getPages(webClient, url, list, methot);
		 
	 } 
	 
	 
	 
}
