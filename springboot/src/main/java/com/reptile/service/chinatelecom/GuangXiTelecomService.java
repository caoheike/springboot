package com.reptile.service.chinatelecom;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.ConstantInterface;
import com.reptile.util.Dates;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;

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
    public Map<String, Object> sendPhoneCode1(HttpServletRequest req, String phoneNumber) {
    	Map<String, Object> map = new HashMap<String, Object>(16);
    	//获取session
    	Object attribute = req.getSession().getAttribute("GBmobile-webclient");
    	if (attribute == null) {
    		map.put("errorCode", "0001");
    		map.put("errorInfo", "操作异常");
    	} else {
    		WebClient webClient = (WebClient) attribute;
    		try {
    			//发送验证码，请求地址
    			WebRequest request = new WebRequest(new URL("http://gx.189.cn/service/bill/getRand.jsp"));
    			request.setHttpMethod(HttpMethod.POST);
    			//请求参数
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new NameValuePair("MOBILE_NAME", phoneNumber));
				list.add(new NameValuePair("PRODTYPE", "2020966"));
				list.add(new NameValuePair("RAND_TYPE", "025"));
				list.add(new NameValuePair("OPER_TYPE", "CR1"));
				
				//请求头
				Map<String,String> headers = new HashMap<String, String>(16);
				headers.put("Cookie", this.getCookie(webClient));
				headers.put("Host", "gx.189.cn");
				headers.put("Referer", "http://gx.189.cn/chaxun/iframe/user_center.jsp");
				request.setAdditionalHeaders(headers);
				request.setRequestParameters(list);
				//发送请求
				Page page = webClient.getPage(request);
				//获取请求结果
				String flag = this.getResult(page, "flag");
				
				if(ZORE_STR.equals(flag)){
					map.put("errorCode", "0000");
    				map.put("errorInfo", "短信验证码已发送到您的手机，请注意查收！");
				}else{
					map.put("errorCode", "0002");
    				map.put("errorInfo", this.getResult(page, "msg"));
				}
				//验证码发送成功后调用该方法
				this.recordAjax(webClient);
				//将webClient存入session中
				req.getSession().setAttribute("oneWebClient", webClient);
    		} catch (Exception e) {
    			logger.error("广西第一次发送手机验证码失败",e);
    			map.put("errorCode", "0005");
    			map.put("errorInfo", "网络异常");
    		}
    	}
    	return map;
    }
    
    
    
    /**
     * 输入第一次验证码，获取第二次验证码
     * @param
     * @param phoneNumber
     * @return
     * @throws IOException 
     * @throws MalformedURLException 
     * @throws FailingHttpStatusCodeException 
     */
    public Map<String, Object> sendPhoneCode2(HttpServletRequest req, String phoneCode,String phoneNumber) {
    	Map<String, Object> map = new HashMap<String, Object>(16);
    	
    	//获取session
    	Object attribute = req.getSession().getAttribute("oneWebClient");
    	if (attribute == null) {
    		map.put("errorCode", "0001");
    		map.put("errorInfo", "操作异常");
    	} else {
    		WebClient webClient = (WebClient) attribute;
    		try {
    			//发送
    			String checkInfo = this.cheakRealName(webClient, phoneNumber, phoneCode);
    			if(checkInfo.contains(NO_CORRENT_STR) || checkInfo.contains(ERROR_STR)){
    				map.put("errorCode", "0003");
                    map.put("errorInfo", checkInfo);
                    return map;
    			}
                
                //发送第二次手机验证码
                WebRequest request = new WebRequest(new URL("http://gx.189.cn/service/bill/getRand.jsp"));
				request.setHttpMethod(HttpMethod.POST);
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
				list.add(new NameValuePair("PASSWORD", ""));
				list.add(new NameValuePair("CUST_NAME", ""));
				list.add(new NameValuePair("CARD_TYPE", ""));
				list.add(new NameValuePair("CARD_NO", ""));
				
				//请求头
				Map<String,String> headers = new HashMap<String, String>(16);
				headers.put("Cookie", this.getCookie(webClient));
				headers.put("Host", "gx.189.cn");
				headers.put("Referer", "http://gx.189.cn/chaxun/iframe/user_center.jsp");
				request.setAdditionalHeaders(headers);
				request.setRequestParameters(list);
				
				Page page = webClient.getPage(request);
				String flag = this.getResult(page, "flag");
				if(ZORE_STR.equals(flag)){
					map.put("errorCode", "0000");
    				map.put("errorInfo", "短信验证码已发送到您的手机，请注意查收！");
				}else{
					map.put("errorCode", "0002");
    				map.put("errorInfo", this.getResult(page, "msg"));
				}
    			
				req.getSession().setAttribute("twoWebClient", webClient);
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
    			if(checkInfo.contains(NO_CORRENT_STR) || checkInfo.contains(ERROR_STR)){
    				 map.put("errorCode", "0003");
                     map.put("errorInfo", checkInfo);
                 	 PushState.state(phoneNumber, "callLog",200,checkInfo);
        			 PushSocket.pushnew(map, uuid, "9000",checkInfo);
                     return map;
    			}
    			
    			PushSocket.pushnew(map, uuid, "2000","登录成功");
    			PushSocket.pushnew(map, uuid, "5000","获取数据中");
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
    				list.add(new NameValuePair("ACCT_DATE", Dates.beforMonth(i-1)));
    				list.add(new NameValuePair("ACCT_DATE_1", Dates.beforMonth(i)));
    				
    				//请求头
    				Map<String,String> headers = new HashMap<String, String>(16);
    				headers.put("Cookie", this.getCookie(webClient));
    				headers.put("Host", "gx.189.cn");
    				headers.put("Referer", "http://gx.189.cn/chaxun/iframe/user_center.jsp");
    				request.setAdditionalHeaders(headers);
    				request.setRequestParameters(list);
    				
    				HtmlPage page1 = webClient.getPage(request);
    				Thread.sleep(1000);
    				dataList.add(page1.asXml());
    			}
    			PushSocket.pushnew(map, uuid, "6000","获取数据成功");
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
    			logger.error("广西获取详单信息失败",e);
    			map.put("errorCode", "0005");
    			map.put("errorInfo", "网络异常");
    			PushState.state(phoneNumber, "callLog",200,"网络异常");
    			PushSocket.pushnew(map, uuid, "9000","网络异常");
    		}
    	}
    	return map;
    }
    
    
    /**
     * ajax请求，第一次发送验证码成功后需调用该方法
     * @param webClient
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    public void recordAjax(WebClient webClient) throws FailingHttpStatusCodeException, IOException{
    	//请求地址
    	WebRequest request = new WebRequest(new URL("http://gx.189.cn/public/recordajax.jsp"));
		request.setHttpMethod(HttpMethod.POST);
		//请求参数
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new NameValuePair("POST_URL", "http://gx.189.cn/chaxun/iframe/user_center.jsp?SERV_NO=FCX-3"));
		list.add(new NameValuePair("SOURCE_URL", "http://gx.189.cn/chaxun/iframe/user_center.jsp?SERV_NO=FCX-3"));
		request.setRequestParameters(list);
		//请求头
		Map<String,String> headers = new HashMap<String, String>(16);
		headers.put("Cookie", this.getCookie(webClient));
		headers.put("Host", "gx.189.cn");
		headers.put("Referer", "http://gx.189.cn/chaxun/iframe/user_center.jsp");
		request.setAdditionalHeaders(headers);
		
		webClient.getPage(request);
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
    	
    	Map<String,String> headers = new HashMap<String, String>(16);
    	headers.put("Cookie", this.getCookie(webClient));
    	headers.put("Host", "gx.189.cn");
    	headers.put("Origin", "http://gx.189.cn");
    	request.setAdditionalHeaders(headers);
    	
    	Page page = webClient.getPage(request);
    	logger.warn("------第一次身份验证结果-----"+page.getWebResponse().getContentAsString());
    	String tips = this.getResult(page, "Tips");
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
    	
    	WebRequest request = new WebRequest(new URL("http://gx.189.cn/public/realname/checkRealName.jsp"));
		request.setHttpMethod(HttpMethod.POST);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new NameValuePair("NUM", phoneNumber));
		list.add(new NameValuePair("V_PASSWORD", phoneCode));
		list.add(new NameValuePair("CUST_NAME", userName));
		list.add(new NameValuePair("CARD_NO", userCard));
		list.add(new NameValuePair("CARD_TYPE", "1"));
		list.add(new NameValuePair("RAND_TYPE", "002"));
		request.setRequestParameters(list);
		
		Map<String,String> headers = new HashMap<String, String>(16);
		headers.put("Cookie", this.getCookie(webClient));
		headers.put("Host", "gx.189.cn");
		headers.put("Referer", "http://gx.189.cn/chaxun/iframe/user_center.jsp");
		request.setAdditionalHeaders(headers);
		Page page = webClient.getPage(request);
		logger.warn("------第二次身份验证结果-----"+page.getWebResponse().getContentAsString());
		String tips = this.getResult(page, "Tips");
    	return tips;
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
    	
    	WebRequest request = new WebRequest(new URL("http://gx.189.cn/chaxun/iframe/inxxall_new.jsp"));
		request.setHttpMethod(HttpMethod.POST);
		//请求参数
		List<NameValuePair> list = new ArrayList<NameValuePair>();
//		list.add(new NameValuePair("PRODTYPE", "2020966"));
//		list.add(new NameValuePair("RAND_TYPE", "002"));
//		list.add(new NameValuePair("BureauCode", "1500"));
//		list.add(new NameValuePair("ACC_NBR", phoneNumber));
//		list.add(new NameValuePair("PROD_TYPE", "2020966"));
//		list.add(new NameValuePair("PROD_PWD", ""));
//		list.add(new NameValuePair("REFRESH_FLAG", "1"));
//		list.add(new NameValuePair("BEGIN_DATE", ""));
//		list.add(new NameValuePair("END_DATE", ""));
//		list.add(new NameValuePair("SERV_NO", ""));
//		list.add(new NameValuePair("QRY_FLAG", "1"));
//		list.add(new NameValuePair("MOBILE_NAME", phoneNumber));
//		list.add(new NameValuePair("OPER_TYPE", "CR1"));
//		list.add(new NameValuePair("FIND_TYPE", "1031"));
//		list.add(new NameValuePair("radioQryType", "on"));
//		list.add(new NameValuePair("ACCT_DATE", Dates.getCurrentDate()));
//		list.add(new NameValuePair("ACCT_DATE_1", Dates.getCurrentDate()));
//		list.add(new NameValuePair("PASSWORD", phoneCode));
//		list.add(new NameValuePair("CUST_NAME", userName));
//		list.add(new NameValuePair("CARD_TYPE", "1"));
//		list.add(new NameValuePair("CARD_NO", userCard));
		
		list.add(new NameValuePair("ACC_NBR", phoneNumber));
		list.add(new NameValuePair("PROD_TYPE", "2020966"));
		list.add(new NameValuePair("BEGIN_DATE", ""));
		list.add(new NameValuePair("END_DATE", ""));
		list.add(new NameValuePair("REFRESH_FLAG", "1"));
		list.add(new NameValuePair("QRY_FLAG", "1"));
		list.add(new NameValuePair("FIND_TYPE", "1031"));
		list.add(new NameValuePair("radioQryType", "on"));
		list.add(new NameValuePair("ACCT_DATE", Dates.getCurrentDate()));
		list.add(new NameValuePair("ACCT_DATE_1", Dates.getCurrentDate()));
	
		request.setRequestParameters(list);
		//请求头
		Map<String,String> headers = new HashMap<String, String>(16);
		headers.put("Cookie", this.getCookie(webClient));
		headers.put("Host", "gx.189.cn");
		headers.put("Referer", "http://gx.189.cn/chaxun/iframe/user_center.jsp");
		request.setAdditionalHeaders(headers);
		
		HtmlPage page = webClient.getPage(request);
		
		return page.asXml();
    }
    
    /**
     * 获取cookie
     * @param
     * @return
     */
    private  String getCookie1(WebClient webClient)		{
    	//获得cookie用于发包
    	Set<Cookie> cookies = webClient.getCookieManager().getCookies();
    	logger.warn("cookies.toString():"+cookies.toString());
    	StringBuffer tmpcookies = new StringBuffer();
    	
    	for (Cookie cookie : cookies) {
    		String name = cookie.getName();
    		String value = cookie.getValue();
    		if("loginStatus".equals(name)){
    			value = "non-logined";
    		}
    		if(!"trkId".equals(name)){
    			tmpcookies.append(name + "="+ value + ";");
    		}
    	}
    	tmpcookies.append("trkId=E524C273-01D2-440B-9935-9058CBEAFC77; pgv_pvi=4034977792; citrix_ns_id_.189.cn_%2F_wlf=TlNDX3h1LTIyMi42OC4xODUuMjI5?sBn7JRm5ImeTI832903604H90UcA&; _gscu_1708861450=15476233zi4c1p32;pgv_si=s6546911232;Hm_lvt_8b5c429f5193dc4a670ff0814155f3fe=1515464913,1515474260,1515547649,1515550588;");
    	String str = tmpcookies.toString();
    	if(!str.isEmpty()){
    		str = str.substring(0,str.lastIndexOf(";"));
    	}
    	return str; 	
    }
	/**
	 * 获取cookie
	 * @param webClient
	 * @return
	 */
	private  String getCookie(WebClient webClient)		{
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
