package com.reptile.service.chinatelecom;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.analysis.ChinaTelecomAnalysisInterface;
import com.reptile.analysis.ZJTelecomAnalysisImp;
import com.reptile.constants.MessageConstamts;
import com.reptile.util.GetMonth;
import com.reptile.util.HttpURLConection;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.SimpleHttpClient;
import com.reptile.util.application;
/**
 * 浙江电信发包方式
 * @author cui
 *
 */
@Service
public class ZJTelecomService {
	private Logger logger = LoggerFactory.getLogger(ZJTelecomService.class);
	@Autowired
	private application application;
    private static String chooseUrl="http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10012&toStUrl=http://zj.189.cn/service/queryorder/";
    private static String iframeUrl="http://zj.189.cn/zjpr/service/query/query_order.html?menuFlag=1";
    private static String sendCodeUrl="http://zj.189.cn/bfapp/buffalo/VCodeOperation";
    private static String judgeUrl="http://zj.189.cn/bfapp/buffalo/cdrService";
    private static String getDetailUrl="http://zj.189.cn/zjpr/cdr/getCdrDetail.htm";
   /**
    * 判断是否需要验证码
    * @param request
    * @return
    */
    public Map<String, Object>  isNeedCode(HttpServletRequest request){
    	Map<String, Object> map = new HashMap<String, Object>(16);

		// 从session中获得webClient
		Object attribute = request.getSession().getAttribute("GBmobile-webclient");
		WebClient webClient = (WebClient) attribute;
		if (webClient == null) {
			logger.warn("-----------------------浙江电信，请先登录!-----------------");
			map.put("errorCode", "0001");
			map.put("errorInfo", "请先登录!");
			return map;
		}
		
		try {
		    //打开语音详单查询页面
			HtmlPage choosePage =choosePage = webClient.getPage(chooseUrl);
		  	Thread.sleep(1000);
		} catch (Exception e) {
			logger.error("-----------------------浙江电信，网络异常!-----------------",e);
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络异常!");
			return map;
		}

  	     String cookies=  this.getCookies(webClient);
		 try {
			 HttpClient httpClient = new HttpClient();
			//判断是否需要获取短信验证码
			boolean flag=this.isNeedCode(httpClient, judgeUrl, cookies);
			if (flag) {
				logger.warn("-----------------------浙江电信，需要短信验证码!-----------------");
				map.put("errorCode", "0000");
				map.put("errorInfo", "需要短信验证码");
			}else {
				logger.warn("-----------------------浙江电信，不需要短信验证码!-----------------");
				map.put("errorCode", "0001");
				map.put("errorInfo", "不需要短信验证码");
			}
			request.getSession().setAttribute("judgeIsNeedCode-WebClient", webClient);
			request.getSession().setAttribute("judgeIsNeedCode-httpClient", httpClient);
		} catch (Exception e) {
			logger.error("-----------------------浙江电信，网络异常!-----------------",e);
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络异常");
			return map;
		}
		
		return map;
    	
    }
    /**
     * 获取详单，无需验证码
     * @param request
     * @return
     */
    public Map<String, Object> getDetailNoCode(HttpServletRequest request,String phoneNumber,String servePwd,String longitude,String latitude,String uuid) {
    	Map<String, Object> map = new HashMap<String, Object>(16);
    	// 从session中获得webClient
        Object attribute = request.getSession().getAttribute("judgeIsNeedCode-WebClient");
        Object attribute1 = request.getSession().getAttribute("judgeIsNeedCode-httpClient");
        WebClient webClient = (WebClient) attribute;
        HttpClient httpClient =(HttpClient) attribute1;
		if (webClient == null||httpClient==null) {
			logger.warn("-----------------------浙江电信"+phoneNumber+"，操作异常!-----------------");
			map.put("errorCode", "0001");
			map.put("errorInfo", "请先登录!");
			return map;
		}
		HtmlPage page=null;
		try {
			//进到查询详单ifram
			page=this.getPages(webClient, iframeUrl, HttpMethod.GET);
			Thread.sleep(2000);
			//查询详情
			logger.warn("-----------------------浙江电信"+phoneNumber+"，数据获取中!-----------------");
			//List<Map<String, Object>> data=this.getDetails(webClient, page, phoneNumber);
			List<String> data=this.getDetails(webClient, page, phoneNumber, "1", "", "", "");
			if (data==null) {
				logger.warn("-----------------------浙江电信"+phoneNumber+"，暂无数据!-----------------");	
			}else {
				logger.warn("-----------------------浙江电信"+phoneNumber+"，数据获取完成!数据："+data.toString()+"-----------------");
			}
			//解析
			logger.warn("-----------------------浙江电信"+phoneNumber+"，数据解析中...-----------------");
			ChinaTelecomAnalysisInterface analysis=new ZJTelecomAnalysisImp();
			map=analysis.analysisHtml(data, phoneNumber,servePwd,longitude,latitude);
			
  		    //推送数据
			map=this.pushData(webClient, map, phoneNumber, uuid,logger);
		} catch (Exception e) {
			logger.error("-----------------------浙江电信，网络异常!-----------------",e);
			map.clear();
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络异常!");
			return map;
		}
		return map;
		
	}
    
    /**
	 * 获取短信验证码，需要验证码
	 * @param request
	 * @param phoneNumber 手机号
	 * @return
	 */
	public Map<String, Object> getCode(HttpServletRequest request, String phoneNumber) {
		Map<String, Object> map = new HashMap<String, Object>(16);

		// 从session中获得webClient
		Object attribute = request.getSession().getAttribute("judgeIsNeedCode-WebClient");
		WebClient webClient = (WebClient) attribute;
		if (webClient == null) {
			logger.warn("-----------------------浙江电信，"+phoneNumber+"操作异常!-----------------");
			map.put("errorCode", "0001");
			map.put("errorInfo", "请先登录!");
			return map;
		} 
		  //发送验证码
		  Object attribute1 = request.getSession().getAttribute("judgeIsNeedCode-httpClient");
    	  HttpClient httpClient =(HttpClient) attribute1;
    	  boolean flag=false;
          try {
        	  flag=this.sendCode(webClient,httpClient, sendCodeUrl, phoneNumber);
        	  if (flag) {
        		  request.getSession().setAttribute("getCode-zheJing", webClient);
        		  request.getSession().setAttribute("getCode-zheJingHttp", httpClient);
        		  map.put("errorCode", "0000");
      			  map.put("errorInfo", "验证码发送成功");
			}else {
				map.put("errorCode", "0001");
    			map.put("errorInfo", "验证码发送失败!");
			}
		} catch (Exception e) {
			logger.error("-----------------------浙江电信，验证码发送失败!-----------------",e);
			map.put("errorCode", "0001");
			map.put("errorInfo", "验证码发送失败!");
			return map;
		}
		return map;

	}
	/**
	 * 获取详单，需要验证码
	 * @param request
	 * @param phoneNumber
	 * @param servePwd
	 * @param code
	 * @param longitude
	 * @param latitude
	 * @param uuid
	 * @return
	 */
	 public Map<String, Object> getDetailNeedCode(HttpServletRequest request,String phoneNumber,String servePwd,String code,String longitude,String latitude,String uuid) {
		 Map<String, Object> map = new HashMap<String, Object>(16);
		// 从session中获得webClient
			Object attribute = request.getSession().getAttribute("getCode-zheJing");
			WebClient webClient = (WebClient) attribute;
			if (webClient == null) {
				logger.warn("-----------------------浙江电信，"+phoneNumber+"请先获取验证码!-----------------");
				map.put("errorCode", "0001");
				map.put("errorInfo", "请先登录!");
				return map;
			} 
			Object attribute1 = request.getSession().getAttribute("getCode-zheJingHttp");
			HttpClient httpClient =(HttpClient) attribute1;
			//获取个人信息，用于发包
			try {
			String personInfo=	this.getInfo(httpClient, judgeUrl, this.getCookies(webClient));
			String idCard=personInfo.split("cust_reg_nbr</string><string>")[1].split("</string><string>serv_type_id")[0];
			String name=personInfo.split("cust_name</string><string>")[1].split("</string><string>serv_type_name")[0];
			
			//进到查询详单ifram
			HtmlPage page=this.getPages(webClient, iframeUrl, HttpMethod.GET);
			Thread.sleep(1000);
			//校验验证码
		    boolean flag=	this.checkCode(webClient, page, phoneNumber, name, idCard, code);
		    if (flag) {
		    	//查询详情
				logger.warn("-----------------------浙江电信"+phoneNumber+"，数据获取中!-----------------");
				List<String> data=this.getDetails(webClient, page, phoneNumber, "2", name, idCard, code);
				if (data==null) {
					logger.warn("-----------------------浙江电信"+phoneNumber+"，暂无数据!-----------------");	
				}else {
					logger.warn("-----------------------浙江电信"+phoneNumber+"，数据获取完成!-----------------");
				}
				//解析
				logger.warn("-----------------------浙江电信"+phoneNumber+"，数据解析中...-----------------");
				ChinaTelecomAnalysisInterface analysis=new ZJTelecomAnalysisImp();
				map=analysis.analysisHtml(data, phoneNumber,servePwd,longitude,latitude);
				
	  		    //推送数据
				map=this.pushData(webClient, map, phoneNumber, uuid,logger);
		    	
			}else {
				logger.warn("-----------------------浙江电信，"+phoneNumber+"验证码错误!-----------------");
				map.put("errorCode", "0001");
    			map.put("errorInfo", "验证码错误!");
			}
			}catch (Exception e) {
				logger.error("-----------------------浙江电信，"+phoneNumber+"网络异常!-----------------");
				map.clear();
				map.put("errorCode", "0001");
    			map.put("errorInfo", "网络异常!");
			}
		 return map; 
	 }
	
/**
 * 推送数据
 * @param webClient
 * @param map
 * @param data
 * @param servePwd
 * @param phoneNumber
 * @param longitude
 * @param latitude
 * @param uuid
 * @param logger
 * @return
 * @throws IOException 
 * @throws ParseException 
 */
	public Map<String, Object> pushData(WebClient webClient,Map<String, Object> map,String phoneNumber,String uuid,Logger logger) throws ParseException, IOException{
		webClient.close();
		Map<String, String> pushMap=new HashMap<>();
		pushMap.put("data", net.sf.json.JSONObject.fromObject(map).toString());
		String tip=HttpURLConection.sendPost(pushMap, "http://192.168.3.4:8088/HSDC-Oracle/message/operator");
		Map<String,Object> result=net.sf.json.JSONObject.fromObject(tip);
		 if(tip.contains("0000")){
			 logger.warn("------------------------浙江电信"+phoneNumber+"，认证成功----------------------");
				PushSocket.pushnew(result, uuid, "8000", "认证成功");
				PushState.state(phoneNumber, "callLog", 300);
		 }else{
			  logger.warn("------------------------浙江电信"+phoneNumber+"，认证失败----------------------");
				PushSocket.pushnew(result, uuid, "9000", result.get("errorInfo").toString());
				PushState.state(phoneNumber, "callLog", 200, result.get("errorInfo").toString());
		 }
		
		return result;
	}
    /**
     * 获取cookies
     * @param webClient
     * @return
     */
    public String getCookies(WebClient webClient){
      Set<Cookie>  cookie=	webClient.getCookieManager().getCookies();
      StringBuffer cookies=new StringBuffer();
      for (Cookie c : cookie) {
    	  cookies.append(c.getName()+"="+c.getValue()+";");
	   }
      System.out.println(cookies.toString());
	return cookies.toString();
    	
    }
   /**
    * 获得页面
    * @param webClient
    * @param url
    * @param method
    * @return
    * @throws FailingHttpStatusCodeException
    * @throws IOException
    * @throws InterruptedException
    */
    public HtmlPage getPages(WebClient webClient, String url, HttpMethod method) throws FailingHttpStatusCodeException, IOException, InterruptedException {
       WebRequest requests = new WebRequest(new URL(url));
       requests.setHttpMethod(method);
       HtmlPage page = webClient.getPage(requests);
       //Thread.sleep(2000);
       return page;
   }
    /**
     * 判断是否需要验证码
     * @param httpClient
     * @param url
     * @param cookie
     * @return  true需要      false不需要
     * @throws HttpException
     * @throws IOException
     * @throws JSONException
     */
    public boolean isNeedCode(HttpClient httpClient,String url,String cookie) throws HttpException, IOException, JSONException{
    	String result =getInfo(httpClient, url, cookie);
		return getLevel(result)==2?true:false;
    }
    /**
     * 获取要判断的信息
     * @param httpClient
     * @param url
     * @param cookie
     * @return
     * @throws HttpException
     * @throws IOException
     */
   public String getInfo(HttpClient httpClient,String url,String cookie) throws HttpException, IOException{
	       PostMethod post = new PostMethod(url);
	      post.setRequestHeader("Cookie", cookie);
	      post.setRequestHeader("Content-Type", "text/xml;charset=UTF-8");
	      post.setRequestHeader("Host", "zj.189.cn");
	      post.setRequestHeader("Origin", "http://zj.189.cn");
	      post.setRequestHeader("Referer", "http://zj.189.cn/zjpr/service/query/query_order.html?menuFlag=1");
	      RequestEntity entity = new StringRequestEntity("<buffalo-call><method>querycdrasset</method></buffalo-call>", "text/html", "utf-8");
	      post.setRequestEntity(entity);
	      httpClient.executeMethod(post);
	return post.getResponseBodyAsString();
	   
   } 
    /**
     * 获取要判断的值
     * @param result 
     * @return
     * @throws JSONException
     */
    public int  getLevel(String result) throws JSONException {
    	 org.json.JSONObject json=  XML.toJSONObject(result);
		 System.out.println(json.toString());
		JSONObject	buffalo= (JSONObject) json.get("buffalo-reply");
		JSONObject	map= (JSONObject) buffalo.get("map");
		JSONArray string= (JSONArray) map.get("string");
		return new Integer(string.get(1).toString());
	}
    /**
     * 从页面获取发包需要的部分信息
     * @param page
     * @return
     * @throws InterruptedException 
     */
    public Map<String , String> getInfo(HtmlPage page,Map<String , String> param) throws InterruptedException{
    	Thread.sleep(2000);
   	   //  System.out.println(page.asXml());
   	    param.put("pagenum", page.getElementByName("cdrCondition.pagenum").getAttribute("value").toString());
   	    param.put("areaid", page.getElementByName("cdrCondition.areaid").getAttribute("value").toString());
   	    param.put("productid", page.getElementByName("cdrCondition.productid").getAttribute("value"));
	   	param.put("cdrtype", page.getElementByName("cdrCondition.cdrtype").getAttribute("value"));
	   	param.put("flag",page.getElementByName("flag").getAttribute("value"));
	   	param.put("recievenbr",page.getElementByName("cdrCondition.recievenbr").getAttribute("value"));
    	return param;
    }
    /***
     * 获得HtmlPage
     * @param webClient
     * @param url
     * @param list  参数
     * @param  header  请求头
     * @param method post或者get
     * @return HtmlPage
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    public HtmlPage getPages(WebClient webClient, String url, List<NameValuePair> list, Map<String, String>  header,HttpMethod method) throws FailingHttpStatusCodeException, IOException, InterruptedException {
         if (header!=null) {
        	 for (Map.Entry<String, String> entry : header.entrySet()) {
        		    System.out.println(entry.getKey() + ":" + entry.getValue());
        		    webClient.addRequestHeader(entry.getKey(), entry.getValue());
        		}
		  }
        WebRequest requests = new WebRequest(new URL(url));
        requests.setRequestParameters(list);
        requests.setHttpMethod(method);
        HtmlPage page = webClient.getPage(requests);
        return page;
    }
    /**
     * 获取详单
     * @param webClient
     * @param page
     * @param phoneNumber
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     * @throws InterruptedException
     */
   public  List<String> getDetails(WebClient webClient,HtmlPage page,String phoneNumber,String cdrlevel,String username,String idcard,String randpsw) throws FailingHttpStatusCodeException, IOException, InterruptedException{
	   Map<String,String> param=new HashMap<String, String>();
	   List<String>  datas=new ArrayList<>();
		//获取发包需要的信息
		param=getInfo(page,param);
		   int[] yearAndMonth=GetMonth.nowYearMonth();
		   int year=yearAndMonth[0];
		   int month=yearAndMonth[1];
		   String cdrmonth="";
       for (int j = 0; j < 6; j++) {
    	   cdrmonth= GetMonth.beforMon(year, month, j);
	     for (int i = 1; i < 100; i++) {
				Map<String, Object> dataMap = new HashMap<String, Object>(16);
	    	 //所有参数
	 		List<NameValuePair> list=new ArrayList<NameValuePair>();
	 		setParameter(list, param, i+"", cdrlevel, cdrmonth, phoneNumber, username, idcard, randpsw);
	 		 //获取信息
	 	     HtmlPage page1=getPages(webClient, getDetailUrl, list, null, HttpMethod.POST);
	 	    // System.out.println(page1.asXml());
	 	     //中国电信网上营业厅·浙江      错误页面
	 	     if (page1.getTitleText().equals("错误页面")) {
				break;
			  }
	 	   // dataMap.put("item", page1.asXml());
	 	   datas.add(page1.asXml()); 
		  }
	    
	    // System.out.println(cdrmonth+"-----------------");
       } 
       System.out.println(datas.toString()+"-----------------");
       List<String>  res=new ArrayList<String>();
      for (int i = 0; i < 3; i++) {
    	  res.add(datas.get(i));
    	  System.out.println(res);
	  }
      System.out.println(res);
	return datas;
   }
   
   /**
    * 设置参数
    * @param list
    * @param param
    * @param pagenum
    * @param cdrmonth
    * @param phoneNumber
    * @return
    */
    
   public List<NameValuePair> setParameter(List<NameValuePair> list,Map<String,String> param,String pagenum,String cdrlevel,String cdrmonth,String phoneNumber,String username,String idcard,String randpsw){
	   list.add(new NameValuePair("cdrCondition.pagenum",pagenum));
		list.add(new NameValuePair("cdrCondition.productnbr",phoneNumber));
		list.add(new NameValuePair("countValue","0"));//
		list.add(new NameValuePair("tiaozhuan","1"));
		list.add(new NameValuePair("cdrCondition.productid",param.get("productid")));
		list.add(new NameValuePair("cdrCondition.areaid",param.get("areaid")));
		list.add(new NameValuePair("cdrCondition.cdrtype",param.get("cdrtype")));
		list.add(new NameValuePair("cdrCondition.cdrmonth",cdrmonth));
		list.add(new NameValuePair("cdrCondition.numtype","11"));
		list.add(new NameValuePair("cdrCondition.cdrlevel",cdrlevel));
		list.add(new NameValuePair("cdrCondition.randpsw",randpsw));//
		list.add(new NameValuePair("cdrCondition.product_servtype","18"));
		list.add(new NameValuePair("cdrCondition.recievenbr",param.get("recievenbr")));
		list.add(new NameValuePair("username",username));//
		list.add(new NameValuePair("idcard",idcard));//
		list.add(new NameValuePair("flag",param.get("flag")));
	   
	return list;
	   
   }
    /**
     * 发送短信验证码
     * @param webClient
     * @param url
     * @param phonNumber
     * @param method
     * @return
     * @throws IOException 
     * @throws HttpException 
     */
     public boolean sendCode(WebClient webClient,HttpClient httpClient,String url,String phonNumber) throws HttpException, IOException{
    	 //获取cookies
   	      String cookies=  getCookies(webClient);
 	      PostMethod post = new PostMethod(url);
 	      post.setRequestHeader("Cookie", cookies);
 	      post.setRequestHeader("bfw-ctrl", "json");
 	      post.setRequestHeader("Connection", "keep-alive");
 	      post.setRequestHeader("Content-Type", "text/json");
 	      post.setRequestHeader("Host", "ebsnew.boc.cn");
 	      post.setRequestHeader("Origin", "https://ebsnew.boc.cn");
 	      post.setRequestHeader("Referer", "https://ebsnew.boc.cn/boc15/welcome_ele.html?v=20171227064943706&locale=zh&login=card&segment=1");
 	      post.setRequestHeader("X-Requested-With", "XMLHttpRequest");
 	      RequestEntity entity = new StringRequestEntity("<buffalo-call><method>SendVCodeByNbr</method><string>"+phonNumber+"</string></buffalo-call>", "text/html", "utf-8");
 	      post.setRequestEntity(entity);
 	      httpClient.executeMethod(post);
 	      String result = post.getResponseBodyAsString();
 	      System.out.println(result);
 		return result.contains("成功")?true:false;
     }
     /**
      * 
      * @param webClient
      * @param page
      * @param phoneNumber 电话号码
      * @param username 用户名
      * @param idcard 身份证
      * @param randpsw 短信验证码
      * @return
      * @throws FailingHttpStatusCodeException
      * @throws IOException
      * @throws InterruptedException
      */
    public boolean checkCode(WebClient webClient,HtmlPage page,String phoneNumber,String username,String idcard,String randpsw) throws FailingHttpStatusCodeException, IOException, InterruptedException {
    	 Map<String,String> param=new HashMap<String, String>();
          //获取发包需要的信息
  		  param=getInfo(page,param);
  		   int[] yearAndMonth=GetMonth.nowYearMonth();
  		   int year=yearAndMonth[0];
  		   int month=yearAndMonth[1];
  		   String cdrmonth="";
      	   cdrmonth= GetMonth.beforMon(year, month, 0);
  	        //所有参数
  	 		List<NameValuePair> list=new ArrayList<NameValuePair>();
  	 		setParameter(list, param, "1", "2", cdrmonth, phoneNumber, username, idcard, randpsw);
  	 		 //获取信息
  	 	     HtmlPage page1=getPages(webClient, getDetailUrl, list, null, HttpMethod.POST);
  	 	     System.out.println(page1.asXml());
  	 	     if (page1.getTitleText().equals("中国电信网上营业厅·浙江")) {
  	 	    	return false;
  			  }
		return true;
	}
    
    
}
