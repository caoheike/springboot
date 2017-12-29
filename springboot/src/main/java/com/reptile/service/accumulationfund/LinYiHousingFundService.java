package com.reptile.service.accumulationfund;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.reptile.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: LinYiHousingFundService  
 * @Description: TODO (临沂公积金)
 * @author: xuesongcui
 * @date 2017年12月29日  
 *
 */
@Service
public class LinYiHousingFundService {
	private Logger logger = LoggerFactory.getLogger(LinYiHousingFundService.class);
	
	
	private static int max = 16;
	private static int min = 0;
	private static String errorCode = "errorCode";
	private static String success = "0000";
	
	/**
	 * 临沂公积金登录并获取详情
	 * @param request
	 * @param userName
	 * @param passWord
	 * @param idCard
	 * @param cityCode
	 * @return
	 */
	public Map<String, Object> doLogin(HttpServletRequest request,
			String userName, String passWord,String idCard,String cityCode,String idCardNum) {
		//返回信息封装
		Map<String, Object> data = new HashMap<String, Object>(16);
		
		WebClient webClient = new WebClientFactory().getWebClient();
		try {
			HtmlPage loginPage=webClient.getPage("http://www.lyzfgjj.gov.cn/abc/login.html");
			//获取登录表单
			HtmlForm form = (HtmlForm) loginPage.getElementByName("Login");
			//用户名
	        HtmlTextInput  username = form.getInputByName("zfz");
	        username.setValueAttribute(idCard);
	        //密码
	        HtmlPasswordInput password = form.getInputByName("zh");
	        password.setValueAttribute(passWord);
	        //对alert弹框进行监听
	        List<String> list = new ArrayList<String>();
	        CollectingAlertHandler alert = new CollectingAlertHandler(list);
	        webClient.setAlertHandler(alert);
	        
	        HtmlSubmitInput submit = form.getInputByValue("提交");
	        submit.click();
	        
	        if(list.size() > 0 && list.get(0).length() < max){
	        	data.put("errorCode", "0001");
	        	data.put("errorInfo", "用户名或密码错误");
	        }else{
	        	data = this.doGetDetail(request, idCard, cityCode, passWord, idCardNum,webClient);
	        }
	        
		} catch (Exception e) {
			logger.error("临沂市公积金登录失败，用户名为："+userName,e);
			data.put("errorCode", "0002");
        	data.put("errorInfo", "系统繁忙，请重试！");
		}finally{
			if(webClient != null){
				webClient.close();
			}
		}
		return data;
	}

	/**
	 * 临沂公积金信息查询
	 * @param request
	 * @param idCard
	 * @param cityCode
	 * @param webClient
	 * @return
	 */
	public Map<String, Object> doGetDetail(HttpServletRequest request,String idCard,String cityCode,String passWord,String idCardNum,WebClient webClient) {
		//返回信息封装
		Map<String, Object> data = new HashMap<String, Object>(16);
		try {
			PushState.state(idCardNum, "accumulationFund",100);
			HtmlPage page = webClient.getPage("http://www.lyzfgjj.gov.cn/abc/index.asp");
			Thread.sleep(500);
			//获取登录表单
			HtmlForm form = (HtmlForm) page.getElementByName("form1");
			//获取验证码图片并识别
			HtmlImage imageField= (HtmlImage) page.getElementsByTagName("img").get(0);
			String verifyImagesPath = request.getSession().getServletContext().getRealPath("/verifyImages");
			String imgPath = ImgUtil.saveImg(imageField, "ly", verifyImagesPath, "png");
			Map<String,Object> result = MyCYDMDemo.Imagev(imgPath);
			//转码后的动态码
			String checkCode =  (String) result.get("strResult");
			//填写验证码
			HtmlTextInput verifycode = form.getInputByName("verifycode");
			verifycode.setValueAttribute(checkCode);
			//填写查询密码
	        HtmlTextInput zh = form.getInputByName("zh");
	        zh.setValueAttribute(passWord);
	        
	        HtmlSelect x = page.getElementByName("x");
	        x.setSelectedIndex(3);
	        
	        //对alert弹框进行监听
	        List<String> list = new ArrayList<String>();
	        CollectingAlertHandler alert = new CollectingAlertHandler(list);
	        webClient.setAlertHandler(alert);
	        
	        //点击提交按钮
	        HtmlSubmitInput submit = form.getInputByName("submit2");
	        HtmlPage nextPage = submit.click();
	        
	        Map<String,Object> map = new HashMap<String, Object>(16);
	        //基本数据
		    map.put("detailMes",nextPage.getElementsByTagName("table").get(min).asXml());
		    
		    if(list.size() > 0){
		    	data.put("errorCode", "0002");
				data.put("errorInfo", "网络繁忙，请稍后重试！");
	        }else{
	        	data.put("errorCode", "0000");
	        	data.put("errorInfo", "查询成功"); 
	        	data.put("userId", idCardNum);
	        	data.put("city", cityCode);
	        	data.put("data", map);
	        }
	        
	        //数据推送
		    data = new Resttemplate().SendMessage(data, ConstantInterface.port+"/HSDC/person/accumulationFund");
		   
		    if(data != null && success.equals(data.get(errorCode).toString())){
		    	PushState.state(idCardNum, "accumulationFund",300);
		    	data.put("errorInfo","查询成功");
		    	data.put("errorCode","0000");
              
            }else{
            	//--------------------数据中心推送状态----------------------
            	PushState.state(idCardNum, "accumulationFund",200);
            	//---------------------数据中心推送状态----------------------
            	
            	data.put("errorInfo","查询失败");
            	data.put("errorCode","0001");
            	
            }
		    
		    
		} catch (Exception e) {
			logger.error("临沂市公积金查询失败",e);
			data.put("errorCode", "0002");
			data.put("errorInfo", "网络繁忙，请稍后重试！");
		}
		return data;
	}

}
