package com.reptile.springboot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlFrame;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.WebClientFactory;

public class baoji {
	public static void main(String[] args) {
		WebClient webclient=new WebClientFactory().getWebClient();
		 Map<String, Object> map = new HashMap<String, Object>();
		 Map<String, Object> dateMap = new HashMap<String, Object>();
		
		try {
			HtmlPage loginPage= webclient.getPage("http://61.134.23.147:7004/wscx/zfbzgl/zfbzsq/index.jsp");
			WebRequest request1 =new WebRequest(new URL("http://61.134.23.147:7004/wscx/zfbzgl/zfbzsq/login_hidden.jsp?pass=111111&zh=610323198712200942"));
			request1.setHttpMethod(HttpMethod.GET);//提交方式
		 HtmlPage page1=	webclient.getPage(request1);
		  if(page1.asXml().contains("alert")){
			  String tip=page1.asXml().split("alert")[1].split("\\(")[1].split("\\)")[0];
			  map.put("errorCode", "0001");
			  map.put("errorInfo", tip);
			  
		  }else{
			 
			  System.out.println("登陆成功");
			  String zgzh=page1.getElementByName("zgzh").getAttribute("value");
			  String sfzh=page1.getElementByName("sfzh").getAttribute("value");
			  String zgxm=page1.getElementByName("zgxm").getAttribute("value");
			  String dwbm=page1.getElementByName("dwbm").getAttribute("value");
//			  String zgzt=page1.getElementByName("zgzt").getAttribute("value");
			 // http://61.134.23.147:7004/wscx/zfbzgl/gjjxxcx/gjjxx_cx.jsp?zgzh=01000158000020&sfzh=610323198712200942&zgxm=%C2%DE*&dwbm=01000158&cxyd=%B5%B1%C7%B0%C4%EA%B6%C8&dbname=gjjmx9
			  String url1="http://61.134.23.147:7004/wscx/zfbzgl/gjjxxcx/gjjxx_cx.jsp?zgzh="+zgzh+"&sfzh="+sfzh+"&zgxm="+URLEncoder.encode(zgxm, "gb2312")+"&dwbm="+dwbm+"&cxyd="+URLEncoder.encode("当前年度", "gb2312")+"&dbname=gjjmx9";
			  WebRequest requests1 = new WebRequest(new URL(url1));
			  requests1.setHttpMethod(HttpMethod.GET);
			  HtmlPage page2=webclient.getPage(requests1);	
			 // System.out.println(page2.asXml());//基本信息
			  HtmlTable table= (HtmlTable) page2.getByXPath("/html/body/table[2]/tbody/tr[1]/td[2]/table[4]/tbody/tr/td/table/tbody/tr/td/table[1]").get(0);
			  System.out.println(table.asXml());//宝鸡公积金基本信息
			  List<NameValuePair> list = new ArrayList<NameValuePair>();
				//设置参数
				list.add(new NameValuePair("zgzh", zgzh));
				list.add(new NameValuePair("sfzh", sfzh));
				list.add(new NameValuePair("zgxm", URLEncoder.encode(zgxm, "gb2312")));
				list.add(new NameValuePair("dwbm", dwbm));
				list.add(new NameValuePair("cxyd", URLEncoder.encode("当前年度", "gb2312")));
				list.add(new NameValuePair("zgzt", null));
			  WebRequest request2 =new WebRequest(new URL("http://61.134.23.147:7004/wscx/zfbzgl/gjjmxcx/gjjmx_cx.jsp"));
			request2.setHttpMethod(HttpMethod.POST);//提交方式
			request2.setRequestParameters(list);
			HtmlPage page3=webclient.getPage(request2); //当前年度详单
			
		//	HtmlPage page3= page2.getElementByName("button5").click();
			  
			  Thread.sleep(3000);
			  System.out.println(page3.asText()+"-------------"); 
			  //==============宝鸡所有明细==================
			HtmlSelect select=  page3.getElementByName("cxydone");
			if(select!=null){
				int num=select.getChildElementCount();
				for(int i=0;i<num-1;i++){
					
					
					
					
					
				}
			}else{
				System.out.println("暂无数据");
			}
			  
			 
			  
			  
			  
			  
		  }
			  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
}
