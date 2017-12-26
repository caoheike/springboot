package com.reptile.enterprisecredit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import net.sf.json.JSONObject;

/**
 * 
* @ClassName: JiangSu
* @Description: 江苏工商网获取信息
* @author 刘柯森
* @date 2017年12月25日 下午4:42:03
*
* @param <E>
 */
public class JiangSu<E> {
	
		private Logger logger=  LoggerFactory.getLogger(JiangSu.class);
		
		
		// 设置条件
	public static WebClient setClient() {
				WebClient client = new WebClient(BrowserVersion.CHROME);
				client.setJavaScriptTimeout(5000);	
				client.getCookieManager().setCookiesEnabled(true);
				client.getOptions().setTimeout(100000);
				client.getOptions().setCssEnabled(true);
				client.getOptions().setJavaScriptEnabled(true);
				client.setJavaScriptTimeout(100000);
				client.getOptions().setRedirectEnabled(true);
				client.getOptions().setThrowExceptionOnScriptError(false);
				client.getOptions().setThrowExceptionOnFailingStatusCode(false);
				client.setAjaxController(new NicelyResynchronizingAjaxController());
				return client;

			}
		
		
		/**
		 * 
		* @Title: jiangsuEnterprise
		* @Description: 
		* @param @param content
		* @param @return
		* @param @throws Exception
		* @param @throws MalformedURLException
		* @param @throws IOException    设定文件
		* @return List<E>    返回类型
		* @throws
		 */
		public Map<String, Object> jiangsuEnterprise(String  content ) throws Exception, MalformedURLException, IOException {
			logger.info("------开始查询----搜索内容:");
			WebClient client= JiangSu.setClient();
			
			Map<String, Object> dataMap = new HashMap<String, Object>();
			
			
			// 打开陕西工商网
			WebRequest requests = new WebRequest(new URL("http://js.gsxt.gov.cn"));
			HtmlPage page = client.getPage(requests);
				
			//获取验证码参数  git ，challenge
			WebRequest requests1 = new WebRequest(new URL("http://www.jsgsj.gov.cn:58888/province/geetestViladateServlet.json?register=true"
															+"&t="+System.currentTimeMillis()));
				
			
			HtmlPage text = client.getPage(requests1);
			// 获取json串
			JSONObject  json  = JSONObject.fromObject(text.asText());
			
			WebRequest webRequest=null;
			// 判断返回码是否为{success:1}
			if(json.get("success").toString().equals("1")) {
				
				// 向第三方发送关键字
					webRequest = new WebRequest(new URL("http://jiyanapi.c2567.com/shibie?user=caoheike&pass=598415805&return=json&ip=&gt="
																+json.get("gt").toString()
																+"&challenge="
																+json.get("challenge").toString()));
					// 判断返回码是否为{success:0}
				}else if(json.get("success").toString().equals("0")){
					
					webRequest = new WebRequest(new URL("http://jiyanapi.c2567.com/shibie?user=caoheike&pass=598415805&return=json&ip=&gt="
														+json.get("gt").toString().toString()
														+"&challenge="
														+json.get("challenge").toString()
														+"&model=1"));
						
				}else {
					logger.info("获取验证信息失败，重新获取~~~");
					// 重新请求
					jiangsuEnterprise(content);
				}
			// 得到返回json串
			TextPage text1 = client.getPage(webRequest);
			System.out.println("********"+text1.getContent());
			
			
			
			
			
			HtmlPage text2=null;
			try {
				// 验证信息
				JSONObject	verification = JSONObject.fromObject(text1.getContent());
				
				WebRequest requests02 = new WebRequest(new URL("http://www.jsgsj.gov.cn:58888/province/geetestViladateServlet.json?validate=true&name="
																+URLEncoder.encode(content,"utf-8")));
				List<NameValuePair> valuepair = new ArrayList<NameValuePair>();
				valuepair.add(new NameValuePair("type","search"));	
				valuepair.add(new NameValuePair("geetest_challenge",verification.get("challenge").toString()));
				valuepair.add(new NameValuePair("geetest_validate",verification.get("validate").toString()));
				valuepair.add(new NameValuePair("geetest_seccode",verification.get("validate").toString()+"|jordan"));	

				
				// 请求类型
				requests02.setHttpMethod(HttpMethod.POST);
				// 发送请求数据)
				requests02.setRequestParameters(valuepair);	
				// 得到返回的验证数据
				text2 = client.getPage(requests02);
				 Thread.sleep(3000);
				System.out.println("&&&&&&&&&&&&"+text2.asText());
			} catch (Exception e) {
				
				 logger.warn("验证码验证失败",e);
				 dataMap.put("errorCode", "0001");
				 dataMap.put("errorInfo", "网络异常");
	            return dataMap;
			}
			
			
			
			
			try {
				// 抓取页面数据	
				JSONObject	getData = JSONObject.fromObject(text2.asText());
				String url="http://www.jsgsj.gov.cn:58888/province/infoQueryServlet.json?queryCinfo=true"
						+"&name="+getData.getString("name")
						+"&searchType=qyxx"
						+"&pageNo=1"
						+"&pageSize=10";
				System.out.println(url);
				WebRequest requests03 = new WebRequest(new URL(url));
															
				requests03.setHttpMethod(HttpMethod.GET);	

				HtmlPage page2 = client.getPage(requests03);
				 Thread.sleep(3000);
				System.out.println(page2.asText()+"%%%%%");		
				dataMap.put("errorCode","1000");
				dataMap.put("errorInfo","成功获取匹配的所有企业");
				dataMap.put("GSHtmlPage",page2);
			} catch (Exception e) {
				logger.warn("获取页面数据失败",e);
				dataMap.put("errorCode", "0002");
				dataMap.put("errorInfo", "网络异常");
			}

			return dataMap;
		}
		
		
		public static void main(String[] args) throws Exception {
			
			new JiangSu().jiangsuEnterprise("红上");
			
		}

}
