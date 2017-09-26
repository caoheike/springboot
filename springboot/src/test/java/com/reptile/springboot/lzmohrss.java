package com.reptile.springboot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.application;

public class lzmohrss {
	 @Autowired
	  private application applications;
	//兰州社保
				public static void main(String[] args) throws Exception {
					WebClient webclient=new WebClient(BrowserVersion.INTERNET_EXPLORER);
					webclient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
					webclient.getOptions().setTimeout(90000);
					webclient.getOptions().setCssEnabled(true);
					webclient.getOptions().setJavaScriptEnabled(true);
					webclient.setJavaScriptTimeout(40000);
					webclient.getOptions().setRedirectEnabled(true);
					webclient.getOptions().setThrowExceptionOnScriptError(false);
					webclient.getOptions().setThrowExceptionOnFailingStatusCode(true);
					webclient.setAjaxController(new NicelyResynchronizingAjaxController());
					webclient.getOptions().setCssEnabled(true);
					//---------------------------------------通过页面元素获取地址--------------------------------------------------
//					HtmlPage loginPage= webclient.getPage("http://wssb.lzmohrss.org.cn/siweb/login.do?method=begin");//兰州社保登录地址
//					HtmlImage Image= (HtmlImage) loginPage.getElementById("jcaptcha");
//					BufferedImage bi=Image.getImageReader().read(0);
//					Date datas=	new Date();
//					String time=new SimpleDateFormat("yyyyMMddhhmmssSSS").format(datas);
//					String ImageName="lzmohrss"+time+".png" ;
//					
//					File file=new File("");
//					if(!file.exists()){
//						file.mkdirs();
//					}
//					ImageIO.write(bi , "png", new File("H:\\lzmohrss\\"+ImageName));
//					
					
				//	------------------------直接通过地址获取图片(可用)--------------------------------------------------------
					
					UnexpectedPage Imagepage=  webclient.getPage("http://wssb.lzmohrss.org.cn//siweb/jcaptcha/");
					Thread.sleep(5000);
					BufferedImage bufferedImage  = ImageIO.read(Imagepage.getInputStream());//getInputStream()
					System.out.println(bufferedImage);
					Date datas=	new Date();
					String time=new SimpleDateFormat("yyyyMMddhhmmssSSS").format(datas);
					String ImageName="lzmohrss"+time+".png" ;
					
					File file=new File("");
					if(!file.exists()){
						file.mkdirs();
					}
					ImageIO.write(bufferedImage , "png", new File("H:\\lzmohrss\\"+ImageName));
					Map<String,Object> map=MyCYDMDemo.Imagev("H:\\lzmohrss\\"+ImageName);//图片验证，打码平台
					String catph=(String) map.get("strResult");
					List<NameValuePair> list = new ArrayList<NameValuePair>();
					list.add(new NameValuePair("j_username", "620102198908233913"));//身份证号
					list.add(new NameValuePair("j_password", "zjc123456"));//密码
					list.add(new NameValuePair("jcaptcha_response", catph));//验证码
					WebRequest requests =new WebRequest(new URL("http://wssb.lzmohrss.org.cn/siweb/j_unieap_security_check.do?logtype=1"));
					requests.setHttpMethod(HttpMethod.POST);
					requests.setRequestParameters(list);
					webclient.getPage(requests);
					HtmlPage page=webclient.getPage("http://wssb.lzmohrss.org.cn/siweb/gg_emp_cb.do?method=begin");
					Thread.sleep(12000);
					System.out.println(page.asText().trim());
				}
}
