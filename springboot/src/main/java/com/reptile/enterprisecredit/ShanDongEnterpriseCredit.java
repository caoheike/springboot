package com.reptile.enterprisecredit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.UrlEncoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.WebClientFactory;

import net.sf.json.JSONObject;

/**
 * 
* @ClassName: ShanDongEnterpriseCredit
* @Description: 山东工商网获取信息
* @author 刘柯森
* @date 2017年12月25日 下午4:43:24
*
 */
public class ShanDongEnterpriseCredit {
	private static Logger logger=  LoggerFactory.getLogger(ShanDongEnterpriseCredit.class);
	
	
	/**
	 * 
	* @Title: shandong
	* @Description: 山东工商网信息提取
	* @param @param content
	* @param @return
	* @param @throws Exception    设定文件
	* @return Map<String,Object>    返回类型
	* @throws
	 */
	public static Map<String, Object> shandong(String  content) throws Exception{
		
		HashMap<String, Object>  dataMap  = new HashMap<String, Object>();
		
		
		WebClient webClient = new WebClientFactory().getWebClient();
		// 打开山东工商网
		HtmlPage page = webClient.getPage("http://sd.gsxt.gov.cn");
		
		
	
		UnexpectedPage page2=null;
		try {
			page2 = webClient.getPage("http://sd.gsxt.gov.cn/pub/geetest/register/1513932195937?_="+System.currentTimeMillis());
			System.out.println(page2.getWebResponse().getContentAsString()+"&&&&&");
			JSONObject json = JSONObject.fromObject(page2.getWebResponse().getContentAsString());
			
			
			WebRequest webRequest=null;
			if(json.get("sucess").toString().equals("1")) {
				
				// 向第三方发送关键字
					webRequest = new WebRequest(new URL("http://jiyanapi.c2567.com/shibie?user=caoheike&pass=598415805&return=json&ip=&gt="
																+json.get("gt").toString()
																+"&challenge="
																+json.get("challenge").toString()));
					// 判断返回码是否为{success:0}
				}else if(json.get("sucess").toString().equals("0")){
					
					webRequest = new WebRequest(new URL("http://jiyanapi.c2567.com/shibie?user=caoheike&pass=598415805&return=json&ip=&gt="
														+json.get("gt").toString().toString()
														+"&challenge="
														+json.get("challenge").toString()
														+"&model=1"));
						
				}else {
					logger.info("获取验证信息失败，重新获取~~~");
					// 重新请求
					shandong(content);
				}
		} catch (Exception e) {
			logger.info("获取验证信息失败",e);
			dataMap.put("errorCode", "0001");
			dataMap.put("errorInfo", "当前网络繁忙，验证信息失败");
			
		}
		
		
		
		
		// 获取页面数据
		try {
			JSONObject fromObject = JSONObject.fromObject(page2.getWebResponse().getContentAsString());
			
			String attribute = page.getElementByName("_csrf").getAttribute("value");
			
			WebRequest post=new WebRequest(new URL("http://sd.gsxt.gov.cn/pub/search/index?keyword="+URLEncoder.encode(content,"UTF-8")));
			
			List<NameValuePair> list=new ArrayList<>();
			list.add(new NameValuePair("isjyyc", "0"));
			list.add(new NameValuePair("isyzwf", "0"));
			list.add(new NameValuePair("challenge", fromObject.getString("challenge")));
			list.add(new NameValuePair("validate", "__"));
			list.add(new NameValuePair("seccode", "__|jordan"));
			//list.add(new NameValuePair("keyword", URLEncoder.encode(content,"utf-8")));
			list.add(new NameValuePair("_csrf", attribute));
			post.setHttpMethod(HttpMethod.POST);
			post.setRequestParameters(list);
			
			HtmlPage page3 = webClient.getPage(post);
			System.out.println(page3.asText());
			
			dataMap.put("dataInfo", page3);

		} catch (Exception e) {
			logger.info("获取页面信息失败",e);
			dataMap.put("errorCode", "0002");
			dataMap.put("errorInfo", "当前网络繁忙");
			
		}
		return dataMap;
		
	}
	
	
	
	
	public static void main(String[] args) throws Exception {
		
			new ShanDongEnterpriseCredit().shandong("山东山");
	}
	
	
	

}
