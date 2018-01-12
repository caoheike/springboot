package com.reptile.service.chinatelecom;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.gargoylesoftware.htmlunit.*;
import com.reptile.util.*;
import net.sf.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

/**
 * 广西电信
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Service
public class GuangXiTelecomService {
    private Logger logger= LoggerFactory.getLogger(GuangXiTelecomService.class);

    
    private static final String ZORE_STR = "0";
    private static final String ERROR_STR = "错误";
    private static final String NO_CORRENT_STR = "不正确";
    /**
     * 第一次获取验证码
     * @param
     * @param phoneNumber
     * @return
     */
//    public Map<String, Object> sendPhoneCode1(HttpServletRequest req, String phoneNumber) {
//		logger.warn(phoneNumber+":用户开始发送第一次手机验证码--------------");
//    	Map<String, Object> map = new HashMap<String, Object>(16);
//    	//获取session
//    	Object attribute = req.getSession().getAttribute("GBmobile-webclient");
//    	if (attribute == null) {
//    		map.put("errorCode", "0001");
//    		map.put("errorInfo", "操作异常");
//    	} else {
//    		WebClient webClient = (WebClient) attribute;
//    		try {
//				HtmlPage page1 = webClient.getPage("http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10021&toStUrl=http://gx.189.cn/public/login_jt_sso.jsp%3FVISIT_URL=/chaxun/iframe/user_center.jsp?SERV_NO=FCX-4");
//				Thread.sleep(1000);
//				//发送验证码，请求地址
//    			WebRequest request = new WebRequest(new URL("http://gx.189.cn/service/bill/getRand.jsp"));
//    			request.setHttpMethod(HttpMethod.POST);
//    			//请求参数
//				List<NameValuePair> list = new ArrayList<NameValuePair>();
//				list.add(new NameValuePair("MOBILE_NAME", phoneNumber));
//				list.add(new NameValuePair("PRODTYPE", "2020966"));
//				list.add(new NameValuePair("RAND_TYPE", "025"));
//				list.add(new NameValuePair("OPER_TYPE", "CR1"));
//
//				//请求头
//				Map<String,String> headers = new HashMap<String, String>(16);
//				headers.put("Host", "gx.189.cn");
//				headers.put("Referer", "http://gx.189.cn/chaxun/iframe/user_center.jsp");
//				request.setAdditionalHeaders(headers);
//				request.setRequestParameters(list);
//				//发送请求
//				Page page = webClient.getPage(request);
//				//获取请求结果
//				String flag = this.getResult(page, "flag");
//
//				if(ZORE_STR.equals(flag)){
//					map.put("errorCode", "0000");
//    				map.put("errorInfo", "短信验证码已发送到您的手机，请注意查收！");
//				}else{
//					map.put("errorCode", "0002");
//    				map.put("errorInfo", this.getResult(page, "msg"));
//				}
//				//将webClient存入session中
//				req.getSession().setAttribute("oneWebClient", webClient);
//				logger.warn(phoneNumber+":用户开始发送第一次手机验证码完毕--------------map："+map.toString());
//    		} catch (Exception e) {
//    			logger.error("广西第一次发送手机验证码失败",e);
//    			map.put("errorCode", "0005");
//    			map.put("errorInfo", "网络异常");
//    		}
//    	}
//    	return map;
//    }
    
    
    
    /**
     * 输入第一次验证码，获取第二次验证码
     * @param
     * @param phoneNumber
     * @return
     * @throws IOException 
     * @throws MalformedURLException 
     * @throws FailingHttpStatusCodeException 
     */
    public Map<String, Object> sendPhoneCode2(HttpServletRequest req,String phoneNumber) {
    	logger.warn(phoneNumber+":用户开始发送二次身份验证码--------------");
    	Map<String, Object> map = new HashMap<String, Object>(16);
		//获取session
		Object attribute = req.getSession().getAttribute("GBmobile-webclient");
		if (attribute == null) {
			map.put("errorCode", "0001");
			map.put("errorInfo", "操作异常");
		} else {
			WebClient webClient = (WebClient) attribute;
			try {
				webClient.getPage("http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10021&toStUrl=http://gx.189.cn/public/login_jt_sso.jsp%3FVISIT_URL=/chaxun/iframe/user_center.jsp?SERV_NO=FCX-4");
				Thread.sleep(1000);
    			//校验第一次的手机验证码
//    			String checkInfo = this.cheakRealName(webClient, phoneNumber, phoneCode);
//				org.json.JSONObject jsonObject = XML.toJSONObject(checkInfo);
//				System.out.println(jsonObject.toString(2));
//				String flag1 = jsonObject.getJSONObject("CheckAcctInfo").get("PrivilegeLevel").toString();
//				if(flag1.equals("2")||flag1.equals("1")||flag1.equals("3A")){
					WebRequest webPost=new WebRequest(new URL("http://gx.189.cn/chaxun/iframe/user_center.jsp"));
					webPost.setHttpMethod(HttpMethod.POST);
					List<NameValuePair> list=new ArrayList<>();
					list.add(new NameValuePair("SERV_NO","FCX-4"));
					webPost.setRequestParameters(list);
					webPost.setAdditionalHeader("Host","gx.189.cn");
					webPost.setAdditionalHeader("Origin","http://gx.189.cn");
					webPost.setAdditionalHeader("Referer","http://gx.189.cn/chaxun/iframe/user_center.jsp?SERV_NO=FCX-4");
					webPost.setAdditionalHeader("Upgrade-Insecure-Requests","1");
					webClient.getPage(webPost);

					webPost=new WebRequest(new URL("http://gx.189.cn/chaxun/iframe/qdcx.jsp"));
					webPost.setHttpMethod(HttpMethod.POST);
					list.clear();
					list.add(new NameValuePair("ACC_NBR",phoneNumber));
					list.add(new NameValuePair("PROD_TYPE","2020966"));
					webPost.setAdditionalHeader("Referer","http://gx.189.cn/chaxun/iframe/user_center.jsp");
					webPost.setAdditionalHeader("X-Requested-With","XMLHttpRequest");

					webPost.setRequestParameters(list);
					Page page = webClient.getPage(webPost);
					System.out.println("-----------------------"+page.getWebResponse().getContentAsString());
//				}else{
//					map.put("errorCode", "0001");
//					map.put("errorInfo", jsonObject.getJSONObject("CheckAcctInfo").get("Tips").toString());
//					return map;
//				}
                //发送第二次手机验证码
                WebRequest request = new WebRequest(new URL("http://gx.189.cn/service/bill/getRand.jsp"));
				request.setHttpMethod(HttpMethod.POST);
				 list = new ArrayList<NameValuePair>();
				list.add(new NameValuePair("PRODTYPE", "2020966"));
				list.add(new NameValuePair("RAND_TYPE", "002"));
				list.add(new NameValuePair("BureauCode", "1200"));
				list.add(new NameValuePair("ACC_NBR", phoneNumber));
				list.add(new NameValuePair("PROD_TYPE", "2020966"));
				list.add(new NameValuePair("PROD_PWD", ""));
				list.add(new NameValuePair("REFRESH_FLAG", "1"));
				list.add(new NameValuePair("BEGIN_DATE", ""));
				list.add(new NameValuePair("END_DATE", ""));
				list.add(new NameValuePair("SERV_NO", ""));
				list.add(new NameValuePair("QRY_FLAG", "1"));
				list.add(new NameValuePair("MOBILE_NAME", phoneNumber));
				list.add(new NameValuePair("OPER_TYPE", "CR1"));

				//订单类型
				list.add(new NameValuePair("FIND_TYPE", "1031"));
				//查找类型 按日期查找 按时间查询
				list.add(new NameValuePair("radioQryType", "on"));
				//系统当前时间
				list.add(new NameValuePair("ACCT_DATE", Dates.getCurrentDate()));
				list.add(new NameValuePair("ACCT_DATE_1", Dates.getCurrentDate()));
				//服务密码
				list.add(new NameValuePair("PASSWORD", ""));
				//用户姓名
				list.add(new NameValuePair("CUST_NAME", ""));
				//证件类型
				list.add(new NameValuePair("CARD_TYPE", "1"));
				//身份证
				list.add(new NameValuePair("CARD_NO", ""));

				//请求头
				request.setAdditionalHeader("Host","gx.189.cn");
				request.setAdditionalHeader("Origin","http://gx.189.cn");
				request.setAdditionalHeader("Referer","http://gx.189.cn/chaxun/iframe/user_center.jsp");
				request.setAdditionalHeader("X-Requested-With","XMLHttpRequest");
				request.setRequestParameters(list);
				//page2
				Page page2 = webClient.getPage(request);
				String flag = this.getResult(page2, "flag");
				if(ZORE_STR.equals(flag)){
					map.put("errorCode", "0000");
    				map.put("errorInfo", "短信验证码已发送到您的手机，请注意查收！");
				}else{
					map.put("errorCode", "0002");
    				map.put("errorInfo", this.getResult(page2, "msg"));
				}
				req.getSession().setAttribute("twoWebClient", webClient);
				logger.warn(phoneNumber+":用户开始发送二次身份验证码完毕--------------map:"+map.toString());
    		} catch (Exception e) {
    			logger.error(" 广西第二次发送手机验证码失败",e);
    			map.put("errorCode", "0005");
    			map.put("errorInfo", "网络异常");
    		}
    	}
    	return map;
    }

    /**
     * 获取详情
     * @param req
     * @param phoneNumber
     * @param serverPwd
     * @param phoneCode
     * @param userName
     * @param userCard
     * @param longitude
     * @param latitude
     * @param uuid
     * @return
     */
    public Map<String, Object> getDetailMes(HttpServletRequest req,String phoneNumber, String serverPwd, String phoneCode, String userName,
    		String userCard,String longitude,String latitude,String uuid) {
    	Map<String, Object> map = new HashMap<String, Object>(16);
    	PushState.state(phoneNumber, "callLog",100);
    	PushSocket.pushnew(map, uuid, "1000","登录中");
    	String signle="1000";
    	
    	//获取session
    	Object attribute = req.getSession().getAttribute("twoWebClient");
    	
    	if (attribute == null) {
    		map.put("errorCode", "0001");
    		map.put("errorInfo", "操作异常");
    		PushState.state(phoneNumber, "callLog",200,"登录失败，操作异常");
    		PushSocket.pushnew(map, uuid, "3000","登录失败，操作异常");
    		return map;
    	} else {
    		WebClient webClient = (WebClient) attribute;
    		try {
    			//检验用户身份
    			String checkInfo = this.cheakRealName(webClient, phoneNumber, serverPwd, phoneCode, userName, userCard);

				org.json.JSONObject jsonObject = XML.toJSONObject(checkInfo);
				System.out.println(jsonObject.toString(2));
				String flag1 = jsonObject.getJSONObject("CheckAcctInfo").get("PrivilegeLevel").toString();
    			if(!"1".equals(flag1)){
					map.put("errorCode", "0001");
					map.put("errorInfo", jsonObject.getJSONObject("CheckAcctInfo").get("Tips").toString());
					PushState.state(phoneNumber, "callLog",200,jsonObject.getJSONObject("CheckAcctInfo").get("Tips").toString());
					PushSocket.pushnew(map, uuid, "3000",jsonObject.getJSONObject("CheckAcctInfo").get("Tips").toString());
					return map;
				}

    			PushSocket.pushnew(map, uuid, "2000","登录成功");
    			PushSocket.pushnew(map, uuid, "5000","获取数据中");
				signle="5000";
    			List<String> dataList = new ArrayList<String>();
    			//获取当月详单
    			String firstDetail = this.firstDetail(webClient, phoneNumber, serverPwd, phoneCode, userName, userCard);
    			dataList.add(firstDetail);
    			Thread.sleep(2000);
    			//获取前5个月的详单
    			int boundCount = 6;
    			for (int i = 1; i < boundCount; i++) {
    				WebRequest request = new WebRequest(new URL("http://gx.189.cn/chaxun/iframe/inxxall_new.jsp"));
    				request.setHttpMethod(HttpMethod.POST);
    				List<NameValuePair> list = new ArrayList<NameValuePair>();
    				list.add(new NameValuePair("ACC_NBR", phoneNumber));
    				list.add(new NameValuePair("PROD_TYPE", "2020966"));
    				list.add(new NameValuePair("BEGIN_DATE", ""));
    				list.add(new NameValuePair("END_DATE", ""));
    				list.add(new NameValuePair("REFRESH_FLAG", "1"));
    				list.add(new NameValuePair("QRY_FLAG", "1"));
    				list.add(new NameValuePair("FIND_TYPE", "1031"));
    				list.add(new NameValuePair("radioQryType", "on"));
    				list.add(new NameValuePair("ACCT_DATE", Dates.beforMonth(i)));
    				list.add(new NameValuePair("ACCT_DATE_1", Dates.beforMonth(i-1)));
    				//请求头
    				Map<String,String> headers = new HashMap<String, String>(16);
    				headers.put("Host", "gx.189.cn");
    				headers.put("Referer", "http://gx.189.cn/chaxun/iframe/user_center.jsp");
    				request.setAdditionalHeaders(headers);
    				request.setRequestParameters(list);
    				
    				HtmlPage page1 = webClient.getPage(request);
    				Thread.sleep(1000);
    				dataList.add(page1.asXml());
    			}
    			PushSocket.pushnew(map, uuid, "6000","获取数据成功");
				logger.warn("--------------------"+userCard+":用户 本次获取的数据为:"+dataList.toString());
				signle="4000";
    			map.put("data",dataList);
    			map.put("flag","7");
    			map.put("UserPassword",serverPwd);
    			map.put("UserIphone",phoneNumber);
    			//经度
    			map.put("longitude", longitude);
    			//纬度
    			map.put("latitude", latitude);
    			webClient.close();
    			Resttemplate resttemplate=new Resttemplate();
    			map = resttemplate.SendMessage(map, ConstantInterface.port+"/HSDC/message/telecomCallRecord");
    			String  resultCode="errorCode";
    			String resultNumber="0000";
    			if(map.get(resultCode).equals(resultNumber)) {
    				PushSocket.pushnew(map, uuid, "8000","认证成功");
    				PushState.state(phoneNumber, "callLog",300);
    			}else {
    				PushSocket.pushnew(map, uuid, "9000",map.get("errorInfo").toString());
    				PushState.state(phoneNumber, "callLog",200,map.get("errorInfo").toString());
    			}
    		} catch (Exception e) {
    			logger.error(userCard+"：--------------------广西获取详单信息失败----------------",e);
    			map.put("errorCode", "0005");
    			map.put("errorInfo", "网络异常");
    			PushState.state(phoneNumber, "callLog",200,"网络异常");
				DealExceptionSocketStatus.pushExceptionSocket(signle,map,uuid);
    		}
    	}
    	return map;
    }

    /**
     * 第一次检验用户身份，获取身份验证结果
     * @param webClient·
     * @param phoneNumber
     * @param phoneCode
     * @param
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    private String cheakRealName(WebClient webClient, String phoneNumber,String phoneCode) throws FailingHttpStatusCodeException, IOException{
    	
    	WebRequest request = new WebRequest(new URL("http://gx.189.cn/public/realname/checkRealName.jsp"));
    	request.setHttpMethod(HttpMethod.POST);
    	List<NameValuePair> list = new ArrayList<NameValuePair>();
    	list.add(new NameValuePair("NUM", phoneNumber));
    	list.add(new NameValuePair("V_PASSWORD", phoneCode));
    	list.add(new NameValuePair("RAND_TYPE", "025"));
    	request.setRequestParameters(list);

    	request.setAdditionalHeader("Host","gx.189.cn");
    	request.setAdditionalHeader("Origin","http://gx.189.cn");
    	request.setAdditionalHeader("Referer","http://gx.189.cn/chaxun/iframe/user_center.jsp?SERV_NO=FCX-4");

    	Page page = webClient.getPage(request);
    	logger.warn("------第一次身份验证结果-----"+page.getWebResponse().getContentAsString());
    	String tips = page.getWebResponse().getContentAsString();
    	return tips;
    }
    /**
     * 第二次检验用户身份
     * @param webClient
     * @param phoneNumber
     * @param serverPwd
     * @param phoneCode
     * @param userName
     * @param userCard
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    private String cheakRealName(WebClient webClient, String phoneNumber, String serverPwd, String phoneCode, String userName,
    		String userCard) throws FailingHttpStatusCodeException, IOException{
    	
    	WebRequest request = new WebRequest(new URL("http://gx.189.cn/public/realname/checkRealName.jsp?CUST_NAME="+URLEncoder.encode(userName,"utf-8")));
		request.setHttpMethod(HttpMethod.POST);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new NameValuePair("NUM", phoneNumber));
		list.add(new NameValuePair("V_PASSWORD", phoneCode));
//		list.add(new NameValuePair("CUST_NAME", userName));
		list.add(new NameValuePair("CARD_NO", userCard));
		list.add(new NameValuePair("CARD_TYPE", "1"));
		list.add(new NameValuePair("RAND_TYPE", "002"));
		request.setRequestParameters(list);

		request.setAdditionalHeader("Host","gx.189.cn");
		request.setAdditionalHeader("Referer","http://gx.189.cn/chaxun/iframe/user_center.jsp");

		Page page = webClient.getPage(request);
		logger.warn("------第二次身份验证结果-----"+page.getWebResponse().getContentAsString());
    	return page.getWebResponse().getContentAsString();
    }
    
    
    /**
     * 获取当月详单
     * @param webClient
     * @param phoneNumber
     * @param serverPwd
     * @param phoneCode
     * @param userName
     * @param userCard
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    private String firstDetail(WebClient webClient, String phoneNumber, String serverPwd, String phoneCode, String userName,
    		String userCard) throws FailingHttpStatusCodeException, IOException{
    	
    	WebRequest request = new WebRequest(new URL("http://gx.189.cn/chaxun/iframe/inxxall_new.jsp?CUST_NAME="+URLEncoder.encode(userName,"utf-8")));
		request.setHttpMethod(HttpMethod.POST);
		//请求参数
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new NameValuePair("PRODTYPE", "2020966"));
		list.add(new NameValuePair("RAND_TYPE", "002"));
		list.add(new NameValuePair("BureauCode", "1200"));
		list.add(new NameValuePair("ACC_NBR", phoneNumber));
		list.add(new NameValuePair("PROD_TYPE", "2020966"));
		list.add(new NameValuePair("PROD_PWD", ""));
		list.add(new NameValuePair("REFRESH_FLAG", "1"));
		list.add(new NameValuePair("BEGIN_DATE", ""));
		list.add(new NameValuePair("END_DATE", ""));
		list.add(new NameValuePair("SERV_NO", ""));
		list.add(new NameValuePair("QRY_FLAG", "1"));
		list.add(new NameValuePair("MOBILE_NAME", phoneNumber));
		list.add(new NameValuePair("OPER_TYPE", "CR1"));
		list.add(new NameValuePair("FIND_TYPE", "1031"));
		list.add(new NameValuePair("radioQryType", "on"));
		list.add(new NameValuePair("ACCT_DATE", Dates.getCurrentDate()));
		list.add(new NameValuePair("ACCT_DATE_1", Dates.getCurrentDate()));
		list.add(new NameValuePair("PASSWORD", phoneCode));
		list.add(new NameValuePair("CARD_TYPE", "1"));
		list.add(new NameValuePair("CARD_NO", userCard));

		request.setRequestParameters(list);
		//请求头
		Map<String,String> headers = new HashMap<String, String>(16);
		headers.put("Host", "gx.189.cn");
		headers.put("Referer", "http://gx.189.cn/chaxun/iframe/user_center.jsp");
		headers.put("X-Requested-With", "XMLHttpRequest");
		request.setAdditionalHeaders(headers);
		
		HtmlPage page = webClient.getPage(request);
		
		return page.asXml();
    }
    

	/**
	 * 获取cookie
	 * @param webClient
	 * @return
	 */
	private  String getCookie(WebClient webClient){
		  //获得cookie用于发包
		Set<Cookie> cookies = webClient.getCookieManager().getCookies();
		logger.warn("cookies.toString():"+cookies.toString());
	    StringBuffer tmpcookies = new StringBuffer();

	   	for (Cookie cookie : cookies) {
	   		String name = cookie.getName();
	   		String value = cookie.getValue();
   			tmpcookies.append(name + "="+ value + ";");
		}
	   	String str = tmpcookies.toString();
	   	if(!str.isEmpty()){
	   		str = str.substring(0,str.lastIndexOf(";"));
	   	}
		return str;
	}
    
	
	 /**
     * 获取xml某个标签的值
     * @param page
     * @param tagName
     * @return
     */
    private String getResult(Page page,String tagName){
    	Document  infotable=  Jsoup.parse(page.getWebResponse().getContentAsString());  
		Elements tags= infotable.getElementsByTag(tagName);
		return tags.get(0).text();
    }

}
