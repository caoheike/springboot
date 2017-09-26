package com.reptile.springboot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.ConstantInterface;
import com.reptile.util.Resttemplate;
import com.reptile.util.htmlUtil;

public class SEotest {
	public static void main(String[] args)  {
		String UserEmail="100900386@hoomsun"
				+ ".com";
		
		Map<String, Object> map=new HashMap<String, Object>();
		String URL= "www"+"."+ UserEmail.substring(UserEmail.indexOf("@")+1);	
		System.out.println("URL===="+URL);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new NameValuePair("host", URL+"/"));
		list.add(new NameValuePair("m", ""));
		if(UserEmail==null||UserEmail==""){
			map.put("ResultInfo", "企业邮箱信息为不正确，请确认后重写填写");
			map.put("ResultCode","0002");
			//设置参数
		}else {
			try {
				WebClient webclient=new WebClient();
				webclient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
				webclient.getOptions().setTimeout(90000);
				webclient.getOptions().setCssEnabled(false);
				webclient.getOptions().setJavaScriptEnabled(false);
				webclient.setJavaScriptTimeout(40000);
				webclient.getOptions().setRedirectEnabled(true);
				webclient.getOptions().setThrowExceptionOnScriptError(false);
				webclient.getOptions().setThrowExceptionOnFailingStatusCode(false);
				webclient.setAjaxController(new NicelyResynchronizingAjaxController());
				webclient.getOptions().setCssEnabled(false);
//						Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
//						Accept-Encoding:gzip, deflate
//						Accept-Language:zh-CN,zh;q=0.8
//						Cache-Control:max-age=0
//						Connection:keep-alive
//						Content-Length:26
//						Content-Type:application/x-www-form-urlencoded
//						Cookie:qHistory=aHR0cDovL3Nlby5jaGluYXouY29tK1NFT+e7vOWQiOafpeivonxodHRwOi8vdG9vbC5jaGluYXouY29tK+ermemVv+W3peWFtw==; UM_distinctid=15e3bf28b8cacb-08aea16ea60a9f-464c0328-1fa400-15e3bf28b8d8e9; CNZZDATA433095=cnzz_eid%3D1177189803-1504241300-http%253A%252F%252Ftool.chinaz.com%252F%26ntime%3D1504246700; CNZZDATA5082706=cnzz_eid%3D1374419188-1504244131-http%253A%252F%252Ftool.chinaz.com%252F%26ntime%3D1504249532
//						Host:seo.chinaz.com
//						Origin:http://seo.chinaz.com
//						Referer:http://seo.chinaz.com/www.hoomsun.com/
//						Upgrade-Insecure-Requests:1
//						User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
//				webclient.addRequestHeader("mtoken", "null");
//				webclient.addRequestHeader("Cookie", "qHistory=aHR0cDovL3Nlby5jaGluYXouY29tK1NFT+e7vOWQiOafpeivonxodHRwOi8vdG9vbC5jaGluYXouY29tK+ermemVv+W3peWFtw==; UM_distinctid=15e3bf28b8cacb-08aea16ea60a9f-464c0328-1fa400-15e3bf28b8d8e9; CNZZDATA433095=cnzz_eid%3D1177189803-1504241300-http%253A%252F%252Ftool.chinaz.com%252F%26ntime%3D1504246700; CNZZDATA5082706=cnzz_eid%3D1374419188-1504244131-http%253A%252F%252Ftool.chinaz.com%252F%26ntime%3D1504249532");
//				webclient.addRequestHeader("Content-Length", "26");
			
//				webclient.addRequestHeader("Cache-Control", "max-age=0");
//				webclient.addRequestHeader("Connection", "keep-alive");
//				webclient.addRequestHeader("Host", "seo.chinaz.com");
//				webclient.addRequestHeader("cebbank", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
//				webclient.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//				webclient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
//				webclient.addRequestHeader("Accept-Encoding", "gzip, deflate");
//				webclient.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
//				webclient.addRequestHeader("Referer", "http://seo.chinaz.com/"+URL+"/");
//				webclient.addRequestHeader("Origin", "http://seo.chinaz.com");
//				webclient.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
//				webclient.addRequestHeader("Upgrade-Insecure-Requests", "1");
//				WebRequest request1 =new WebRequest(new URL("http://seo.chinaz.com/"+URL));
//				request1.setHttpMethod(HttpMethod.POST);//提交方式
//				request1.setRequestParameters(list);
//				HtmlPage page= webclient.getPage(request1);
//				System.out.println(page.asText().trim());
				
				
//				HtmlPage page= webclient.getPage("http://seo.chinaz.com/"+URL+"/");
				//	System.out.println("http://seo.chinaz.com/"+URL);
				HtmlPage page= webclient.getPage("http://icp.chinaz.com/?s="+URL);
				Thread.sleep(2000);
				DomElement ul=  page.getElementById("first");
				System.out.println(ul.asText().trim());
				Map<String,Object> seo=new HashMap<String, Object>();
				seo.put("cardNumber","610122199302282540");
				seo.put("email", UserEmail);
				seo.put("data", ul.asText().trim());
				Resttemplate resttemplate = new Resttemplate();
				map=resttemplate.SendMessage(seo, ConstantInterface.port+"/HSDC/message/companyEmail");
				webclient.close();
				}
				catch (Exception e) {
					 e.printStackTrace();
					 map.clear();
			         map.put("errorInfo","企业邮箱信息为不正确，请确认后重写填写!");
			         map.put("errorCode","0002");
		        }
			}
			System.out.println(map);
	}
	
	
//	private static String Url="www"
//			+ ".youku.com";
//	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
//		WebClient webclient=new WebClient(BrowserVersion.CHROME);
//		webclient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
//		webclient.getOptions().setTimeout(90000);
//		webclient.getOptions().setCssEnabled(true);
//		webclient.getOptions().setJavaScriptEnabled(false);
//		webclient.setJavaScriptTimeout(40000);
//		webclient.getOptions().setRedirectEnabled(true);
//		webclient.getOptions().setThrowExceptionOnScriptError(false);
//		webclient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//		webclient.setAjaxController(new NicelyResynchronizingAjaxController());
//		webclient.getOptions().setCssEnabled(false);
//		HtmlPage page= webclient.getPage("http://seo.chinaz.com/"+Url);
//		HtmlDivision division= (HtmlDivision) page.querySelectorAll(".SeoMaWr01Right").get(4);
//		
//		System.out.println(page.asXml());
//		
//	}
}
	

