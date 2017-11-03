package com.reptile.service.socialSecurity;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.ImgUtil;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;
@Service
public class BZSocialSecurityService {
	private Logger logger= LoggerFactory.getLogger(BZSocialSecurityService.class);
	
	@Autowired
	private application application;
	
	/**
	 * 获取登录验证码图片
	 * @param request
	 * @return
	 */
	public Map<String, Object> doGetVerifyImg(HttpServletRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		WebClient webClient = new WebClientFactory().getWebClient();
		try {
			HtmlPage loginPage=webClient.getPage("http://222.134.45.172:8002/hsp/logonDialog_withE.jsp");
			Thread.sleep(1000);
			//读取页面验证码图片到本地
			HtmlImage authcode_numleft= (HtmlImage) loginPage.getElementById("authcode_numleft");
			HtmlImage authcode_operator= (HtmlImage) loginPage.getElementById("authcode_operator");
			HtmlImage authcode_numright= (HtmlImage) loginPage.getElementById("authcode_numright");
			
			data.put("leftImgPath", ImgUtil.saveImg(authcode_numleft, "left", "/verifyImages", "gif", request));
			data.put("operatorImgPath", ImgUtil.saveImg(authcode_operator, "operator", "/verifyImages", "gif", request));
			data.put("rightImgPath", ImgUtil.saveImg(authcode_numright, "right", "/verifyImages", "gif", request));
			
			request.getSession().setAttribute("binZhouWebClient",webClient); 
			request.getSession().setAttribute("binZhouLoginPage",loginPage); 
			data.put("errorInfo", "获取验证码成功");
            data.put("errorCode", "0000");
            
		} catch (Exception e) {
			logger.error("获取验证码失败！",e);
			data.put("errorInfo", "系统繁忙，请稍后再试！");
            data.put("errorCode", "0002");
		}
		return data;
	}
	
	/**
	 * 登录并获取详情
	 * @param request
	 * @param userCard 
	 * @param passWord
	 * @param userCode
	 * @param cityCode
	 * @return
	 */
	public Map<String, Object> doLogin(HttpServletRequest request,
			String userCard, String passWord,String userCode,String cityCode) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			WebClient webClient = (WebClient)request.getSession().getAttribute("binZhouWebClient");//从session中获得webClient
			HtmlPage loginPage = (HtmlPage)request.getSession().getAttribute("binZhouLoginPage");//从session中获得loginPage
			
			if(webClient == null || loginPage == null){
				data.put("errorInfo", "系统繁忙，请稍后再试！");
	            data.put("errorCode", "0002");
	            return data;
			}
			//监控alert弹窗
			List<String> alertList = new ArrayList<String>();
			CollectingAlertHandler alert = new CollectingAlertHandler(alertList);
			webClient.setAlertHandler(alert);
			
			loginPage.executeJavaScript("alert($.md5('"+passWord+"'))");
			loginPage.executeJavaScript("alert(abcMd5('1.0.72','"+userCard+"'))");
			
			String usermm,appversion = "";
			if(alertList.size() > 0){
				usermm = alertList.get(0);//加密后的密码
				appversion = alertList.get(1);//加密后的appversion值
			}else{
				data.put("errorInfo", "系统繁忙，请稍后再试！");
	            data.put("errorCode", "0002");
	            return data;
			}
			//封装请求参数
			List<NameValuePair> ValueList = new ArrayList<NameValuePair>();
			ValueList.add(new NameValuePair("method", "doLogon"));
			//dlfs值为1时为本地登录，值为2时为e账户登录
			ValueList.add(new NameValuePair("_xmlString","<?xml version='1.0' encoding='UTF-8'?><p><s userid='"+userCard+"'/><s usermm='"+usermm+"'/><s authcode='"+userCode+"'/><s yxzjlx='A'/><s dlfs='1'/><s appversion='"+appversion+"'/></p>"));
			ValueList.add(new NameValuePair("_random", ""+Math.random()));
			
			String response = webRequest("http://222.134.45.172:8002/hsp/logon.do", ValueList, HttpMethod.POST, webClient);
			if(response.contains("__usersession_uuid")){
				data = this.doGetDetail(request, userCard, cityCode, webClient);
			}else{
				data.put("errorInfo", response);
	            data.put("errorCode", "0001");
			}
			
		} catch (Exception e) {
			logger.error("滨州市社保登录失败",e);
			data.put("errorInfo", "系统繁忙，请稍后再试！");
            data.put("errorCode", "0002");
		}
		return data;
	}

	/**
	 * 获取社保详情信息
	 * @param request
	 * @param userCard 
	 * @param cityCode
	 * @param webClient
	 * @return
	 */
	public Map<String, Object> doGetDetail(HttpServletRequest request,String userCard,String cityCode,WebClient webClient)  {
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> infoAll = new HashMap<String, Object>();
		
		try {
			String userSessionUuid = (String)request.getSession().getAttribute("userSessionUuid");
			String laneID = UUID.randomUUID().toString();
			//获取社保基本信息
			String baseInfo = this.getInfo("http://222.134.45.172:8002/hsp/systemOSP.do", laneID, userSessionUuid, "returnMain", true, webClient).get(0);
			infoAll.put("base", baseInfo);
			//获取职工养老保险
			List<String> agedPayHisInfo  = this.getInfo("http://222.134.45.172:8002/hsp/siAd.do", laneID, userSessionUuid, "queryAgedPayHis", false, webClient);
			infoAll.put("item", agedPayHisInfo);
			/*//获取职工医疗保险
			List<String> mediPayHisInfo  = this.getInfo("http://222.134.45.172:8002/hsp/siMedi.do", laneID, userSessionUuid, "queryMediPayHis", false, webClient);
			infoAll.put("mediPayHisInfo", mediPayHisInfo);
			//获取失业保险
			List<String> lostPayHisInfo  = this.getInfo("http://222.134.45.172:8002/hsp/siLost.do", laneID, userSessionUuid, "queryLostPayHis", false, webClient);
			infoAll.put("lostPayHisInfo", lostPayHisInfo);
			//获取工伤保险
			List<String> harmPayHisInfo  = this.getInfo("http://222.134.45.172:8002/hsp/siHarm.do", laneID, userSessionUuid, "queryHarmPayHis", false, webClient);
			infoAll.put("harmPayHisInfo", harmPayHisInfo);
			//获取生育保险
			List<String> birthPayHisInfo  = this.getInfo("http://222.134.45.172:8002/hsp/siBirth.do", laneID, userSessionUuid, "queryBirthPayHis", false, webClient);
			infoAll.put("birthPayHisInfo", birthPayHisInfo);*/
			
			data.put("errorInfo", "查询成功");
            data.put("errorCode", "0000");
            data.put("data", infoAll);
            data.put("city", cityCode);
            data.put("userId", userCard);
//            data = new Resttemplate().SendMessage(data,application.getSendip()+"/HSDC/person/socialSecurity");//上线
            data = new Resttemplate().SendMessage(data,"http://192.168.3.16:8089/HSDC/person/socialSecurity");
		} catch (Exception e) {
			logger.error("获取滨州市社保详情失败",e);
			data.put("errorInfo", "系统繁忙，请稍后再试！");
            data.put("errorCode", "0002");
		}
		
		return data;
	}
	
	/**
	 * 是否为基本信息，true：是，false：否，是保险信息
	 * @param url
	 * @param laneID
	 * @param userSessionUuid
	 * @param method
	 * @param isFlag
	 * @param webClient
	 * @return
	 * @throws IOException 
	 * @throws FailingHttpStatusCodeException 
	 */
	public List<String>  getInfo(String url,String laneID,String userSessionUuid,String method,boolean isFlag,WebClient webClient) throws FailingHttpStatusCodeException, IOException{
		//封装请求参数
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new NameValuePair("method", method));
		list.add(new NameValuePair("_random",""+Math.random()));
		list.add(new NameValuePair("__usersession_uuid", userSessionUuid));
		list.add(new NameValuePair("_laneID", laneID));
		
		String response = webRequest(url, list, HttpMethod.POST, webClient);
		
		List<String> responseInfo = new ArrayList<String>();
		if(isFlag){
			responseInfo.add(response);
		}else{
			if(response.contains("select")){
				
				String selectInfo = response.substring(response.indexOf("select")+6,response.lastIndexOf("select")+6);
				//获取可查到的所有年限
				Set<String> years = new HashSet<String>();
				for (int i = 0; i < selectInfo.length()-4; i++) {
					String str = selectInfo.substring(i, i+4);
					if(str.matches("^2[0-9]{3}")){
						years.add(str);
					}
				}
				//获取每年的社保信息,养老保险年限用ny，其余保险年限为year
				for (String item : years) {
					if(method.equals("queryAgedPayHis")){
						if(list.size() == 4){
							list.add(4,new NameValuePair("nd", item));
						}else{
							list.set(4,new NameValuePair("nd", item));
						}
					}else{
						if(list.size() == 4){
							list.add(4,new NameValuePair("year", item));
						}else{
							list.set(4,new NameValuePair("year", item));
						}
					}
					response = webRequest(url, list, HttpMethod.POST, webClient);
					responseInfo.add(response.substring(response.indexOf("</style><div")+8,response.lastIndexOf("</div><script")));
				}
			}
			
			
		}
		return responseInfo;
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
	


}
