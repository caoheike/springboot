package com.reptile.springboot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.MyCYDMDemo;

import net.minidev.json.JSONObject;

public class lzgjj {
	 private final static String loginPage="http://cx.lzgjj.com/pernetface/login.jsp#per";//登录
	 private final static String ImagePage="http://cx.lzgjj.com/pernetface/image.jsp";//图片
			
	 public static void main(String[] args) throws Exception {
				//http://cx.lzgjj.com/pernetface/login?r=0.11459229792863024
				System.out.println("===============");
				String usernum="620122199303012034";//身份证号
				String userPass="123456";//身份证号
				//j_username:{"j_username":"610424199309067615","j_password":"132456","loginType":4}
				//j_password:132456
				//checkCode:ta6B
				//bsr:chrome/50.0.2661.102
				WebClient webclient=new WebClient(BrowserVersion.CHROME);
				webclient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
				webclient.getOptions().setTimeout(90000);
				webclient.getOptions().setCssEnabled(false);
				webclient.getOptions().setJavaScriptEnabled(false);
				webclient.setJavaScriptTimeout(40000);
				webclient.getOptions().setRedirectEnabled(true);
				webclient.getOptions().setThrowExceptionOnScriptError(false);
				webclient.getOptions().setThrowExceptionOnFailingStatusCode(true);
				webclient.setAjaxController(new NicelyResynchronizingAjaxController());
				webclient.getOptions().setCssEnabled(true);
				HtmlPage loginPage=  webclient.getPage("http://cx.lzgjj.com/pernetface/login.jsp#per");
				UnexpectedPage Imagepage=  webclient.getPage(ImagePage);
				
				BufferedImage bufferedImage  = ImageIO.read(Imagepage.getInputStream());
				System.out.println(bufferedImage);
				Date datas=	new Date();
				String time=new SimpleDateFormat("yyyyMMddhhmmssSSS").format(datas);
				String ImageName="lzgjjImger"+time+".png" ;
				
				File file=new File("");
				if(!file.exists()){
					file.mkdirs();
				}
				ImageIO.write(bufferedImage , "png", new File("H:\\lzgjj\\"+ImageName));
				Map<String,Object> map=MyCYDMDemo.Imagev("H:\\lzgjj\\"+ImageName);//图片验证，打码平台
				String catph=(String) map.get("strResult");
				List<NameValuePair> list = new ArrayList<NameValuePair>();
			//	设置参数
		        Map<String,Object> j_username=new HashMap<String,Object>();
		        int type=4;//身份证号
		        j_username.put("j_username", usernum);
		        j_username.put("j_password", userPass);
		        j_username.put("loginType", type);
		        System.out.println(j_username);
		        String json=new JSONObject(j_username).toString();
		        System.out.println(json);
		    	list.add(new NameValuePair("j_username", json));//身份证号
				list.add(new NameValuePair("j_password", userPass));//密码
				list.add(new NameValuePair("checkCode", catph));//验证码
				list.add(new NameValuePair("bsr", "chrome/60.0.3112.113"));
				WebRequest requests =new WebRequest(new URL("http://cx.lzgjj.com/pernetface/login?r=0.45156752294541813"));
				requests.setHttpMethod(HttpMethod.POST);
				requests.setRequestParameters(list);
				webclient.getPage(requests);
				
				HtmlPage Page= webclient.getPage("http://cx.lzgjj.com/pernetface/per/queryPerAccDetails.do?menuid=259597");
				Page.getElementById("");
				System.out.println(Page.asXml());
//				System.out.println(page.getContent());
//				
//				 Date date=new Date();
//			    String year= new SimpleDateFormat("yyyy").format(date);
//			 	List<NameValuePair> list1 = new ArrayList<NameValuePair>();
//			 	list1.add(new NameValuePair("dto['year']", year));//身份证号
//				WebRequest request =new WebRequest(new URL("http://cx.lzgjj.com/pernetface/per/queryPerAccDetails.do?menuid=259597"));
//				request.setHttpMethod(HttpMethod.POST);
//				request.setRequestParameters(list1);
//			    	HtmlPage page2 = webclient.getPage(request);
//			    	System.out.println(page2.asText()+"dsa");
			 
			
			}
}
