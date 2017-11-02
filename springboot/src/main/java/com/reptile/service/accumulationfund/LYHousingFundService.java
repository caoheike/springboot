package com.reptile.service.accumulationfund;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;
@Service
public class LYHousingFundService {
	private Logger logger = LoggerFactory.getLogger(LYHousingFundService.class);
	private final static  String IMG_PATH = "E:\\lyImg\\";
	public final static String LINYI_CITY_CODE = "";
	
	@Autowired
	private application application;
	
	/**
	 * 临沂公积金登录
	 * @param request
	 * @param userName 用户名
	 * @param passWord 密码
	 * @return
	 */
	public Map<String, Object> lyLogin(HttpServletRequest request,
			String userName, String passWord) {
		
		Map<String, Object> data = new HashMap<String, Object>();//返回信息封装
		
		WebClient webClient = new WebClientFactory().getWebClient();
		try {
			HtmlPage loginPage=webClient.getPage("http://www.lyzfgjj.gov.cn/abc/login.html");
			//获取登录表单
			HtmlForm form = (HtmlForm) loginPage.getElementByName("Login");
			//用户名
	        HtmlTextInput  username = form.getInputByName("zfz");
	        username.setValueAttribute(userName);
	        //密码
	        HtmlPasswordInput password = form.getInputByName("zh");
	        password.setValueAttribute(passWord);
	        //对alert弹框进行监听
	        List<String> list = new ArrayList<String>();
	        CollectingAlertHandler alert = new CollectingAlertHandler(list);
	        webClient.setAlertHandler(alert);
	        
	        HtmlSubmitInput submit = form.getInputByValue("提交");
	        submit.click();
	        
	        if(list.size() > 0 && list.get(0).length() < 16){
	        	data.put("errorCode", "0001");
	        	data.put("errorInfo", "用户名或密码错误");
	        }else{
	        	data.put("errorCode", "0000");
	        	data.put("errorInfo", "登录成功");
	        	request.getSession().setAttribute("webClient",webClient); 
	        	request.getSession().setAttribute("passWord",passWord); 
	        }
	        
		} catch (Exception e) {
			logger.error("临沂市公积金登录失败，用户名为："+userName,e);
			data.put("errorCode", "0002");
        	data.put("errorInfo", "系统繁忙，请重试！");
		}
		return data;
	}

	/**
	 * 临沂公积金信息查询
	 * @param request
	 * @return
	 */
	public Map<String, Object> lyGetDetail(HttpServletRequest request,String idCard,String cityCode) {
		WebClient webClient = (WebClient)request.getSession().getAttribute("webClient");//从session中获得webClient
		Map<String, Object> data = new HashMap<String, Object>();//返回信息封装
		
		try {
			HtmlPage page = webClient.getPage("http://www.lyzfgjj.gov.cn/abc/index.asp");
			Thread.sleep(500);
			//获取登录表单
			HtmlForm form = (HtmlForm) page.getElementByName("form1");
			//获取验证码图片并识别
			HtmlImage imageField= (HtmlImage) page.getElementsByTagName("img").get(0);
			String imgPath = this.saveImg(imageField, "gjj", request);
			Map<String,Object> result = MyCYDMDemo.Imagev(imgPath);
			String checkCode =  (String) result.get("strResult");//转码后的动态码
			//填写验证码
			HtmlTextInput verifycode = form.getInputByName("verifycode");
			verifycode.setValueAttribute(checkCode);
			//填写查询密码
	        HtmlTextInput zh = form.getInputByName("zh");
	        zh.setValueAttribute((String)request.getSession().getAttribute("passWord"));
	        
	        HtmlSelect x = page.getElementByName("x");
	        x.setSelectedIndex(3);
	        //点击提交按钮
	        HtmlSubmitInput submit = form.getInputByName("submit2");
	        HtmlPage nextPage = submit.click();
	        
	        Map<String,Object> map = new HashMap<String, Object>();
		    map.put("detailMes",nextPage.getElementsByTagName("table").get(0).asXml());//基本数据
	        
	        data.put("errorCode", "0000");
		    data.put("errorInfo", "查询成功"); 
		    data.put("userId", idCard);
		    data.put("city", cityCode);
	        data.put("data", map);
	        
	        //数据推送
//		    data = new Resttemplate().SendMessage(data,application.getSendip()+"/HSDC/person/accumulationFund");
		    data = new Resttemplate().SendMessage(data,"http://192.168.3.16:8089/HSDC/person/accumulationFund");
		} catch (Exception e) {
			logger.error("临沂市公积金查询失败",e);
			data.put("errorCode", "0002");
			data.put("errorInfo", "网络繁忙，请稍后重试！");
		}finally{
			if(webClient != null){
				webClient.close();
			}
		}
		return data;
	}
	
	
	/**
	 * 将验证码图片保存到本地
	 * @param htmlImg
	 * @param type
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public String saveImg(HtmlImage htmlImg,String type,HttpServletRequest request) throws IOException{
		ImageReader imgReader = htmlImg.getImageReader();
	    BufferedImage bufferedImage  = ImageIO.read((ImageInputStream)imgReader.getInput());
		String fileName = type+System.currentTimeMillis()+".png";
		File destF = new File(IMG_PATH);
		if (!destF.exists()) {
			destF.mkdirs();
		}
		ImageIO.write(bufferedImage, "png", new File(IMG_PATH,fileName));
		String filePath = IMG_PATH + fileName;
		return filePath;
	}

}
