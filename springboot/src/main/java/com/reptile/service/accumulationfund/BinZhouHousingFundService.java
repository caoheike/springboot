package com.reptile.service.accumulationfund;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.*;
import com.reptile.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 滨州公积金
 * @ClassName: BinZhouHousingFundService  
 * @Description: TODO  
 * @author: xuesongcui
 * @date 2017年12月29日  
 *
 */
@Service
public class BinZhouHousingFundService {
	
	private Logger logger = LoggerFactory.getLogger(BinZhouHousingFundService.class);
	
	
	private static String codeStr = "验证码不正确，请重新输入";
	private static String success = "0000";
	private static String accumulationFund = "accumulationFund";
	private static String accumulationFundPath = "/HSDC/person/accumulationFund";
	private static String errorCode = "errorCode";

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
		//返回信息
		Map<String, Object> data = new HashMap<String, Object>(128);
		
		WebClient webClient = new WebClientFactory().getWebClient();
		try {
			HtmlPage loginPage=webClient.getPage("http://www.bzgjj.cn/");
			//获取验证码图片并识别
			HtmlImage imageField= (HtmlImage) loginPage.getElementByName("imageField");
			String verifyImagesPath = request.getSession().getServletContext().getRealPath("/verifyImages");
			String imgPath = ImgUtil.saveImg(imageField, "bz", verifyImagesPath, "png");
			Map<String,Object> result = MyCYDMDemo.Imagev(imgPath); 
			//转码后的动态码
			String checkCode =  (String) result.get("strResult");
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
	        	if(codeStr.equals((list.get(0).trim()))){
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
		Map<String, Object> data = new HashMap<String, Object>(16);
		  PushState.state(idCardNum, "accumulationFund", 100);
		try {
			WebRequest webRequest = new WebRequest(new URL("http://www.bzgjj.cn/usermain2.php"));
			webRequest.setHttpMethod(HttpMethod.GET);
			webRequest.setAdditionalHeader("Referer","http://www.bzgjj.cn/index.php");
			
		    HtmlPage detailPage = webClient.getPage(webRequest);
		    
		    HtmlTable table = (HtmlTable) detailPage.getElementsByTagName("table").get(1);
		    
		    Map<String,Object> map = new HashMap<String, Object>(16);
		    //基本数据
		    map.put("baseMes",table.asXml());
		    
		    data.put("errorCode", "0000");
		    data.put("errorInfo", "查询成功"); 
		    data.put("data", map);
		    data.put("city", cityCode);
		    data.put("userId", idCardNum);
		    //数据推送
		    data = new Resttemplate().SendMessage(data, ConstantInterface.port + accumulationFundPath);
		    
		    if(data != null && success.equals(data.get(errorCode).toString())){
           	 	PushState.state(idCardNum, accumulationFund, 300);
           	 	data.put("errorInfo","推送成功");
           	 	data.put("errorCode","0000");
		    }else{
	           	PushState.state(idCardNum, accumulationFund, 200);
	           	data.put("errorInfo","推送失败");
	           	data.put("errorCode","0001");
           }
		} catch (Exception e) {
			logger.error("滨州市公积金查询失败",e);
			PushState.state(idCardNum, accumulationFund, 200);
			data.put("errorCode", "0002");
			data.put("errorInfo", "网络繁忙，请重试");
		}
		return data;
	}

	
}
