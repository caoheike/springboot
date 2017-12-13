package com.reptile.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.Dates;
import com.reptile.util.JsonUtil;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.Resttemplate;
import com.reptile.util.SimpleHttpClient;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;

@Service
public class JingDService {
	private Logger logger= LoggerFactory.getLogger(JingDService.class);
	
	@Autowired
	private application application;
	
	private static String loadUrl = "https://passport.jd.com/new/login.aspx";

	public Map<String, Object> toLogin(HttpServletRequest request, String trim, String trim2, String trim3) {
		// TODO Auto-generated method stub
		WebClient webClient = new WebClientFactory().getWebClient();
        try {
			HtmlPage page = webClient.getPage(loadUrl);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		WebClient webClient = new WebClientFactory().getWebClient();
		try {
			HtmlPage page = webClient.getPage("https://passport.jd.com/new/login.aspx");
			DomNodeList<DomElement> a = page.getElementsByTagName("a");
            HtmlPage loginpage = a.get(4).click();
			Thread.sleep(3000);
			String uuid=page.getElementById("uuid").getAttribute("value");
			String eid=page.getElementById("eid").getAttribute("value");
			String fp=page.getElementById("sessionId").getAttribute("value");
			String _t=page.getElementById("token").getAttribute("value");
			String pubKey=page.getElementById("pubKey").getAttribute("value");
			String sa_token=page.getElementById("sa_token").getAttribute("value");
			page.getElementById("loginname").setAttribute("value", "13649291630");
			String loginname=page.getElementById("loginname").getAttribute("value");
			page.getElementById("nloginpwd").setAttribute("value", "081921.");
			//验证码
			String src= page.getElementById("JD_Verification1").getAttribute("src");
			String authcode="";
			if(src.length()<2) {
				authcode="";
			}else {
				HtmlImage unitVerifyCode1=(HtmlImage) page.getElementById("JD_Verification1");
				BufferedImage read = unitVerifyCode1.getImageReader().read(0);
				ImageIO.write(read, "png", new File("C://aa.png"));
				Map<String, Object> code = MyCYDMDemo.getCode("C://aa.png");
				authcode=code.get("strResult").toString();
				System.out.println("有验证码");
			}
			String nloginpwd=page.getElementById("nloginpwd").getAttribute("value");
                String url="https://passport.jd.com/uc/loginService?uuid=e3a48662-ff59-480b"+
	            "-a5fd-b3d364cf0843&ltype=logout&r=0.3254369612498569&version=2015";
	            WebRequest post = new WebRequest(new URL(url));
	            post.setHttpMethod(HttpMethod.POST);
	            List<NameValuePair> list = new ArrayList<>();
	            list.add(new NameValuePair("uuid", uuid));
	            list.add(new NameValuePair("eid", eid));
	            list.add(new NameValuePair("fp", fp));
	            list.add(new NameValuePair("_t", _t));
	            list.add(new NameValuePair("loginType", "f"));
	            list.add(new NameValuePair("loginname", loginname));
	            list.add(new NameValuePair("nloginpwd", nloginpwd));
	            list.add(new NameValuePair("chkRememberMe", ""));  
	            list.add(new NameValuePair("authcode", authcode));
	            list.add(new NameValuePair("pubKey", pubKey));
	            list.add(new NameValuePair("sa_token", sa_token));
	            
	            post.setRequestParameters(list);
	            HtmlPage page1 = webClient.getPage(post);
	            Thread.sleep(3000);
	            
	            //验证码
	            
	            if(page1.asText().contains("success")) {
	            	System.out.println("登录成功");
	            	HtmlPage personpage = webClient.getPage("https://www.jd.com/");
	            	DomNodeList<DomElement> persona = personpage.getElementsByTagName("a");
	            	//我的京东
	            	 HtmlPage minepage=persona.get(36).click();
	            	 
	            	Map<String, Object> data = new HashMap<String, Object>();
	            	Map<String,Object> map = new HashMap<String, Object>();
					map.put("purchaseRecord", getOrderInfo(webClient)); //购买记录
					map.put("shippingAddress", getAddressInfo(webClient,minepage)); //收获地址
					map.put("creditData", getBasicInfo(webClient)); //资产总览
					map.put("baiTiaoInfo",getBaiTiaoInfo(webClient)); //白条信息
					map.put("jinKuInfo", getJinKuInfo(webClient)); //小金库
					map.put("jtDetailList", getJtDetailList(webClient)); //金条明细
					data.put("data", map);
					//数据推送
                    System.out.println("----"+map);
					//data = new Resttemplate().SendMessage(data,"http://117.34.70.217:8089/HSDC/savings/eastOfBeijing");
	            }else {
	            	System.out.println("登录失败");
	            }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * 获取订单信息
	 * @param webClient 
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static List<Map<String,Object>> getOrderInfo(WebClient webClient) throws ParseException, IOException{
		Map<String,String> headers = new HashMap<String, String>();
		//近两年的购买记录
		List<Map<String,Object>> info = new ArrayList<Map<String,Object>>();
		//请求近一年内的购买记录
		int i = 1;
		boolean flag = true;
		while(flag){
		         
			String url="https://order.jd.com/center/list.action";
			WebRequest post = new WebRequest(new URL(url));
			post.setHttpMethod(HttpMethod.POST);
			List<NameValuePair> list = new ArrayList<>();
			list.add(new NameValuePair("page", i+""));
			list.add(new NameValuePair("d", "2"));
			list.add(new NameValuePair("s", "4096"));
            
            post.setRequestParameters(list);
            HtmlPage page1 = webClient.getPage(post);
			String result=page1.asXml();		
			List<Map<String,Object>> item = parseOrderInfo(result);//解析每一页的数据
			if(item != null && item.size() > 0){
				//循环将数据
				for (Map<String,Object> map : item) {
					info.add(map);
				}
			}else{
				flag = false;
			}
			i++;
		}
		
		//请求去年的购买记录
		flag = true;
		i = 1;
		while(flag){
			String url="https://order.jd.com/center/list.action";
			WebRequest post = new WebRequest(new URL(url));
			post.setHttpMethod(HttpMethod.POST);
			List<NameValuePair> list1 = new ArrayList<>();
			list1.add(new NameValuePair("page", i+""));
			list1.add(new NameValuePair("d", Dates.beforeYear(1)));
			list1.add(new NameValuePair("s", "4096"));
            
            post.setRequestParameters(list1);
            HtmlPage page1 = webClient.getPage(post);
			String result=page1.asXml();
			
			
			List<Map<String,Object>> item = parseOrderInfo(result);//解析每一页的数据
			if(item != null && item.size() > 0){
				//循环将数据
				for (Map<String,Object> map : item) {
					info.add(map);
				}
			}else{
				flag = false;
			}
			i++;
		}
		return info;
	}
	
	/**
	 * 获取京东白条信息
	 * @param webClient
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static Map<String,Object> getBaiTiaoInfo(WebClient webClient) throws ParseException, IOException{
		Map<String,Object> datas = new HashMap<String,Object>();
		datas.put("billList", getBaiTiaoBillList(webClient));//已出账单
		datas.put("notOutAccount", getBaiTiaoNotOutAccount(webClient));//未出账单
		datas.put("billRepayment", getBaiTiaoBillRepayment(webClient));//还款流水
		datas.put("refundList", getBaiTiaoRefundList(webClient));//退款记录
		datas.put("billConsumeList", getBaiTiaoBillConsumeList(webClient));//消费明细
		datas.put("scoreInfo", getBaiTiaoScoreInfo(webClient));//白条信用分
 		return datas;
	}
	
		
	 /**
	 * 获取白条已出账单
	 * @param webClient
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static List<String> getBaiTiaoBillList(WebClient webClient) throws ParseException, IOException{
		
		String url = "https://baitiao.jd.com/v3/ious/getBillList";//请求地址
		
		Map<String,String> headers = new HashMap<String, String>();
		headers.put("Host", "baitiao.jd.com");
		
		WebRequest post = new WebRequest(new URL(url));
		post.setHttpMethod(HttpMethod.POST);
		List<NameValuePair> Paramlist = new ArrayList<>();
		Paramlist.add(new NameValuePair("pageNum", "1"));
		Paramlist.add(new NameValuePair("pageSize", "10"));
		post.setRequestParameters(Paramlist);
		UnexpectedPage page1 = webClient.getPage(post);
		
		String result = page1.toString();
		String isSuccess = JsonUtil.getJsonValue1(result, "isSuccess").toString();
		
		List<String> list = new ArrayList<String>();
		
		if(isSuccess.equals("true")){
			
			list.add(result);
			
			if(JsonUtil.getJsonValue1(result, "totalCount") != null){
				//获取账单总条数
				int totalCount = (int) JsonUtil.getJsonValue1(result, "totalCount");
				//若账单总条数大于10，需要拿到其余几页的信息
				if(totalCount > 10){
					
					int totalPage = totalCount/10 + 1;
					
					for (int i = 2; i <= totalPage; i++) {
						
						Paramlist.clear();
						Paramlist.add(new NameValuePair("pageNum", i+""));
						Paramlist.add(new NameValuePair("pageSize", "10"));
						post.setRequestParameters(Paramlist);
						page1 = webClient.getPage(post);
						
						result = page1.toString();
						list.add(result);
					}
				}
			}
		} 
		return list;
	}
	
	/**
	 * 获取白条未出账单
	 * @param webClient
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static String getBaiTiaoNotOutAccount(WebClient webClient) throws ParseException, IOException{
		
		String url = "https://baitiao.jd.com/v3/ious/queryNotOutAccount";//请求地址

		WebRequest post = new WebRequest(new URL(url));
		post.setHttpMethod(HttpMethod.POST);
		List<NameValuePair> Paramlist = new ArrayList<>();
		post.setRequestParameters(Paramlist);
		HtmlPage page1 = webClient.getPage(post);
		
		String result = page1.asXml();
		
		return result;
	}
	
	/**
	 * 获取白条未出账单明细
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public String getBaiTiaoOrderDetail(String cookie) throws ParseException, IOException{
		
		String url = "https://baitiao.jd.com/v3/ious/getBillOrderDetail";//请求地址
		
		Map<String,String> headers = new HashMap<String, String>();
		headers.put("Cookie", cookie);//cookie加入请求头中
		headers.put("Host", "baitiao.jd.com");
		
		//请求入参
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("billId", "");
		params.put("billType", "0");
		params.put("isNotAcount", "1");
		
		String result = SimpleHttpClient.post(url,params , headers);
		return result;
	}
	
	
	
	
	/**
	 * 获取白条还款流水
	 * @param webClient
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static List<String> getBaiTiaoBillRepayment(WebClient webClient) throws ParseException, IOException{
		
		String url = "https://baitiao.jd.com/v3/ious/billRepayment";//请求地址
		 
		WebRequest post = new WebRequest(new URL(url));
		post.setHttpMethod(HttpMethod.POST);
		List<NameValuePair> Paramlist = new ArrayList<>();
		Paramlist.add(new NameValuePair("pageNum", "1"));
		Paramlist.add(new NameValuePair("pageSize", "10"));
		post.setRequestParameters(Paramlist);
		HtmlPage page1 = webClient.getPage(post);
		
		String result = page1.asXml();
		
		String isSuccess = JsonUtil.getJsonValue1(result, "isSuccess").toString();
		
		List<String> list = new ArrayList<String>();
		
		if(isSuccess.equals("true")){
			
			list.add(result);
			
			if(JsonUtil.getJsonValue1(result, "totalPage") != null){
				//获取账单总条数
				int totalPage = (int) JsonUtil.getJsonValue1(result, "totalPage");
				//若账单总条数大于10，需要拿到其余几页的信息
				if(totalPage > 1){
					
					for (int i = 2; i <= totalPage; i++) {
						
						Paramlist.clear();
						Paramlist.add(new NameValuePair("pageNum", i+""));
						Paramlist.add(new NameValuePair("pageSize", "10"));
						post.setRequestParameters(Paramlist);
						page1 = webClient.getPage(post);
						result = page1.asXml();
						
						list.add(result);
					}
				}
			}
		} 
		return list;
	}
	
	
	/**
	 * 获取白条退款记录
	 * @param webClient
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static List<String> getBaiTiaoRefundList(WebClient webClient) throws ParseException, IOException{
		
		String url = "https://baitiao.jd.com/v3/ious/queryRefundList";//请求地址
		
		WebRequest post = new WebRequest(new URL(url));
		post.setHttpMethod(HttpMethod.POST);
		List<NameValuePair> Paramlist = new ArrayList<>();
		Paramlist.add(new NameValuePair("pageNum", "1"));
		Paramlist.add(new NameValuePair("pageSize", "10"));
		post.setRequestParameters(Paramlist);
		HtmlPage page1 = webClient.getPage(post);
		String result =page1.asXml();
				
		String isSuccess = JsonUtil.getJsonValue1(result, "isSuccess").toString();
		
		List<String> list = new ArrayList<String>();
		if(isSuccess.equals("true")){
			
			list.add(result);
			
			if(JsonUtil.getJsonValue1(result, "totalPage") != null){
				//获取账单总条数
				int totalPage = (int) JsonUtil.getJsonValue1(result, "totalPage");
				//若账单总条数大于10，需要拿到其余几页的信息
				if(totalPage > 1){
					
					for (int i = 2; i <= totalPage; i++) {
						
						Paramlist.clear();
						Paramlist.add(new NameValuePair("pageNum", i+""));
						Paramlist.add(new NameValuePair("pageSize", "10"));
						post.setRequestParameters(Paramlist);
						page1 = webClient.getPage(post);
						result =page1.asXml();
						
						list.add(result);
					}
				}
			}
		}
		
		return list;
	}
	
	
	
	/**
	 * 获取白条消费明细
	 * @param webClient
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static List<String> getBaiTiaoBillConsumeList(WebClient webClient) throws ParseException, IOException{
		
		String url = "https://baitiao.jd.com/v3/ious/billConsumeList";//请求地址
		
		WebRequest post = new WebRequest(new URL(url));
		post.setHttpMethod(HttpMethod.POST);
		List<NameValuePair> Paramlist = new ArrayList<>();
		Paramlist.add(new NameValuePair("pageNum", "1"));
		Paramlist.add(new NameValuePair("pageSize", "10"));
		post.setRequestParameters(Paramlist);
		HtmlPage page1 = webClient.getPage(post);
		String result =page1.asXml();
		
		String isSuccess = JsonUtil.getJsonValue1(result, "isSuccess").toString();
		
		List<String> list = new ArrayList<String>();
		
		if(isSuccess.equals("true")){
			
			list.add(result);
			
			if(JsonUtil.getJsonValue1(result, "pageCount") != null){
				//获取账单总条数
				int pageCount = (int) JsonUtil.getJsonValue1(result, "pageCount");
				//若账单总条数大于10，需要拿到其余几页的信息
				if(pageCount > 1){
					
					for (int i = 2; i <= pageCount; i++) {
						
						Paramlist.clear();
						Paramlist.add(new NameValuePair("pageNum", i+""));
						Paramlist.add(new NameValuePair("pageSize", "10"));
						post.setRequestParameters(Paramlist);
						page1 = webClient.getPage(post);
						list.add(page1.asXml());
					}
				}
			}
		}
		
		return list;
	}
	
	
	
	
	
	/**
	 * 获取小白信用分
	 * @param webClient
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static String getBaiTiaoScoreInfo(WebClient webClient) throws ParseException, IOException{
		
		String url="https://baitiao.jd.com/v3/ious/score_getScoreInfo";
		WebRequest post = new WebRequest(new URL(url));
		post.setHttpMethod(HttpMethod.POST);
		List<NameValuePair> Paramlist = new ArrayList<>();
		post.setRequestParameters(Paramlist);
		HtmlPage page1 = webClient.getPage(post);
		String result =page1.asXml();
		
		return result;
	}
	
	
	
	
	/**
	 * 获取收货地址
	 * @param cookie
	 * @param webClient 
	 * @param minepage 
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static List<String> getAddressInfo(WebClient webClient, HtmlPage minepage) throws ParseException, IOException{
		Map<String,String> headers = new HashMap<String, String>();
		 
		
		String url="https://easybuy.jd.com/address/getEasyBuyList.action";
        WebRequest get = new WebRequest(new URL(url));
        get.setHttpMethod(HttpMethod.GET);
        get.setAdditionalHeader("Referer","https://home.jd.com/");
        get.setAdditionalHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
        
        HtmlPage page1 = webClient.getPage(get);
        
        String result=page1.asXml();
		return parseAddressInfo(result);
	}
 
	/**
	 * 根据参数获取请求结果
	 * @param url 请求地址
	 * @param list 请求参数
	 * @param httpMethod 请求方式 get或post
	 * @param webClient 请求的webClient
	 * @return 请求结果
	 * @throws FailingHttpStatusCodeException
	 * @throws IOException
	 */
	public String webRequest(String url,List<NameValuePair> list,HttpMethod httpMethod,WebClient webClient) throws FailingHttpStatusCodeException, IOException{
		WebRequest webRequest = new WebRequest(new URL(url));
		
		webRequest.setRequestParameters(list);
		webRequest.setHttpMethod(httpMethod);
		
		HtmlPage page =	webClient.getPage(webRequest);
		String response = page.getWebResponse().getContentAsString();
		
		return response;
	}
	
	
	
	
	/**
	 * 获取小金库信息（小金库额度）昨日收益
	 * @param webClient
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static String getJinKuInfo(WebClient webClient) throws ParseException, IOException{
		
		String url="https://jinku.jd.com/xjk/account";
		WebRequest post = new WebRequest(new URL(url));
		post.setHttpMethod(HttpMethod.POST);
		List<NameValuePair> Paramlist = new ArrayList<>();
		post.setRequestParameters(Paramlist);
		HtmlPage page1 = webClient.getPage(post);
		String result =page1.asXml(); 
		return result;
	}
	/**
	 * 获取金条明细
	 * @param webClient
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static List<String> getJtDetailList(WebClient webClient) throws ParseException, IOException{
		
		String url = "https://baitiao.jd.com/v3/ious/getJtDetailList";//请求地址
		WebRequest post = new WebRequest(new URL(url));
		post.setHttpMethod(HttpMethod.POST);
		List<NameValuePair> Paramlist = new ArrayList<>();
		Paramlist.add(new NameValuePair("pageNum", "1"));
		Paramlist.add(new NameValuePair("pageSize", "10"));
		Paramlist.add(new NameValuePair("funCode", "ALL"));
		post.setRequestParameters(Paramlist);
		HtmlPage page1 = webClient.getPage(post);
		String result =page1.asXml();
		
		String isSuccess = JsonUtil.getJsonValue1(result, "isSuccess").toString();
		
		List<String> list = new ArrayList<String>();
		
		if(isSuccess.equals("true")){
			
			list.add(result);
			
			if(JsonUtil.getJsonValue1(result, "pageCount") != null){
				//获取账单总条数
				int pageCount = (int) JsonUtil.getJsonValue1(result, "pageCount");
				//若账单总条数大于10，需要拿到其余几页的信息
				if(pageCount > 1){
					
					for (int i = 2; i <= pageCount; i++) {
						
						Paramlist.clear();
						Paramlist.add(new NameValuePair("pageNum", i+""));
						Paramlist.add(new NameValuePair("pageSize", "10"));
						Paramlist.add(new NameValuePair("funCode", "ALL"));
						post.setRequestParameters(Paramlist);
						page1 = webClient.getPage(post);
						list.add(page1.asXml());
					}
				}
			}
		}
		return list;
	}
	
	
	/**
	 * 获取白条基本信息和金条基本信息（totalDebt：总负债，creditLimit ：白条总额度，availableLimit：京东白条可用额度；
	 *	creditWaitPay：白条待还款，creditWaitPayPercent ：占负债比重，creditWaitPaySeven ：近七日还款）
	 * @param webClient
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static String getBasicInfo(WebClient webClient) throws ParseException, IOException{
		
		String url="https://trade.jr.jd.com/async/creditData.action";
        WebRequest get = new WebRequest(new URL(url));
        get.setHttpMethod(HttpMethod.GET);
        //get.setAdditionalHeader("Referer","https://trade.jr.jd.com/centre/browse.action");
        //get.setAdditionalHeader("Host","trade.jr.jd.com");
        
        UnexpectedPage page1 = webClient.getPage(get);
        
        String result=page1.toString();
        
		return result;
	}
	
	/**
	 * 获取请求
	 * @param url 地址
	 * @param headers 请求头 
	 * @param params 参数
	 * @throws ParseException
	 * @throws IOException
	 */
	public static String request(String url,Map<String,String> headers,Map<String,String> params) throws ParseException, IOException{
		
		StringBuffer str = new StringBuffer();
		if( params != null ){
			for(Map.Entry<String, String> entry : params.entrySet()){
				if(!str.toString().isEmpty()){
					str.append("&");
				}
				str.append(entry.getKey());
				str.append("=");
				str.append(entry.getValue());
	        }
		}
		url = url + "?" + str.toString();
		String result = SimpleHttpClient.get(url,headers);
		return result;
	}
	/**
	 * 解析购买信息
	 * @param xml
	 * @return
	 */
	 private static  List<Map<String,Object>>  parseOrderInfo(String xml){ 
		 
		 List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		 if(!xml.contains("没有下过订单")){
			 Document doc = Jsoup.parse(xml);
			 Elements tbodys = doc.select("table").select("tbody");  
			 
			 for (Element item : tbodys) {
				 
				 String text = item.text();
				 if(text.contains("订单金额")){
					 continue;
				 }else{
					 String status = item.getElementsByClass("status").get(0).getElementsByTag("span").get(0).text();
					 if(!status.isEmpty() && status.equals("已完成")){
						 Map<String,Object> map = new HashMap<String, Object>();
						 
						 String recordTime = item.getElementsByClass("dealtime").get(0).text();
						 String recordAmount = item.getElementsByClass("amount").get(0).getElementsByTag("span").get(0).text();
						 String recordRemark = item.getElementsByClass("amount").get(0).getElementsByTag("span").get(1).text();
						 
						 map.put("recordTime", recordTime);
						 map.put("recordAmount", recordAmount.substring(4));
						 map.put("recordRemark", recordRemark);
						 list.add(map);
					 }
				 }
			 }
		 }
		 
		return list;	
    }
	
	 
	 
	 
	 
	 /**
	  * 解析收货地址
	  * @param xml
	  * @return
	  */
	 private static   List<String> parseAddressInfo(String xml){ 
		 
		 Document doc = Jsoup.parse(xml);
		 Elements div = doc.getElementsByClass("item-lcol"); 
		 
		 List<String> list = new ArrayList<String>();
		 for (Element itemLcol :  div) {
			 Elements divs = itemLcol.getElementsByClass("item"); 	
			 String address = "";
			 for (Element item : divs) {
				if(item.text().contains("所在地区") || item.text().contains("地址")){
					address += item.getElementsByClass("fl").get(0).text();
				}
			}
			 if(!address.isEmpty()){
				 list.add(address);
			 }
		 }
		 return list;	
	 } 
	 
	 
	/**
	 * 判断页面是否有某个标签
	 * @param driver
	 * @param type id或者class，通过ID或者CLASS来判断
	 * @param eleName
	 * @return
	 */
	/*public static Boolean elementFlag(WebDriver driver,String type,String eleName){
		try {
			if(type.equals("id")){
				driver.findElement(By.id(eleName));
			}else if(type.equals("class")){
				driver.findElement(By.className(eleName));
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}*/
	
}
