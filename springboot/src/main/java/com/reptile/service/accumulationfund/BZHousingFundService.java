package com.reptile.service.accumulationfund;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.reptile.util.ImgUtil;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;

@Service
public class BZHousingFundService {
	
	private Logger logger = LoggerFactory.getLogger(BZHousingFundService.class);
	
	@Autowired
	private application application;
	
	/**
	 * 登录并获取详情
	 * @param request
	 * @param userName 
	 * @param passWord
	 * @param idCard
	 * @param cityCode
	 * @return
	 */
	public Map<String, Object> doLogin(HttpServletRequest request,
			String userName, String passWord,String idCard,String cityCode,String idCardNum) {
		
		Map<String, Object> data = new HashMap<String, Object>();//返回信息
		
		WebClient webClient = new WebClientFactory().getWebClient();
		try {
			HtmlPage loginPage=webClient.getPage("http://www.bzgjj.cn/");
			//获取验证码图片并识别
			HtmlImage imageField= (HtmlImage) loginPage.getElementByName("imageField");
			String verifyImagesPath = request.getSession().getServletContext().getRealPath("/verifyImages");
			String imgPath = ImgUtil.saveImg(imageField, "bz", verifyImagesPath, "png");
			Map<String,Object> result = MyCYDMDemo.Imagev(imgPath);
			String checkCode =  (String) result.get("strResult");//转码后的动态码
			//获取登录表单
			HtmlForm form = (HtmlForm) loginPage.getElementByName("ilogin");
			
	        HtmlTextInput  username = form.getInputByName("username");
	        username.setValueAttribute(userName);
	        HtmlPasswordInput password = form.getInputByName("password");
	        password.setValueAttribute(passWord);
	        HtmlTextInput checkcode = form.getInputByName("checkcode");
	        checkcode.setValueAttribute(checkCode);
	        //对alert弹框进行监听
	        List<String> list = new ArrayList<String>();
	        CollectingAlertHandler alert = new CollectingAlertHandler(list);
	        webClient.setAlertHandler(alert);
	        
	        HtmlImageInput submit = form.getInputByName("Submit");
	        submit.click();
	        
	        if(list.size() > 0){
	        	if((list.get(0).trim()).equals("验证码不正确，请重新输入")){
	        		data.put("errorCode", "0002");
	        		data.put("errorInfo", "系统繁忙，请重试！");
	        	}else{
	        		data.put("errorCode", "0001");
	        		data.put("errorInfo", list.get(0));
	        	}
	        }else{
	        	data = this.doGetDetail(request, idCard, cityCode,idCardNum,webClient);
	        }
	        
		} catch (Exception e) {
			logger.error("滨州市公积金登录失败，用户名为："+userName,e);
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
	 * 获取公积金详情
	 * @param request
	 * @param idCard
	 * @param cityCode
	 * @param webClient
	 * @return
	 */
	public Map<String, Object> doGetDetail(HttpServletRequest request,String idCard,String cityCode,String idCardNum,WebClient webClient) {
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			WebRequest webRequest = new WebRequest(new URL("http://www.bzgjj.cn/usermain2.php"));
			webRequest.setHttpMethod(HttpMethod.GET);
			webRequest.setAdditionalHeader("Referer","http://www.bzgjj.cn/index.php");
			
		    HtmlPage detailPage = webClient.getPage(webRequest);
		    
		    HtmlTable table = (HtmlTable) detailPage.getElementsByTagName("table").get(1);
		    
		    Map<String,Object> map = new HashMap<String, Object>();
		    map.put("baseMes",table.asXml());//基本数据
		    
		    data.put("errorCode", "0000");
		    data.put("errorInfo", "查询成功"); 
		    data.put("data", map);
		    data.put("city", cityCode);
		    data.put("userId", idCardNum);
		    //数据推送
		    data = new Resttemplate().SendMessage(data,application.getSendip()+"/HSDC/person/accumulationFund");
		} catch (Exception e) {
			logger.error("滨州市公积金查询失败",e);
			data.put("errorCode", "0002");
			data.put("errorInfo", "网络繁忙，请重试");
		}
		return data;
	}

	
}
