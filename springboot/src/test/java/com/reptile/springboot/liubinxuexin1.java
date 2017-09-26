package com.reptile.springboot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlEmailInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLDivElement;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.mysql.fabric.xmlrpc.base.Data;
import com.sun.java_cup.internal.runtime.Symbol;

public class liubinxuexin1 {
		public static void main(String[] args) throws Exception {
			
		}
		public static Map<String,Object> xuexin1() throws Exception{
			String ImageUrl="https://account.chsi.com.cn/account/captchimagecreateaction.action?time=1503297613840";//验证码地址
			String PageUrl="https://account.chsi.com.cn/account/password!retrive.action;jsessionid=199D057E29A5E7EA9466A43105DB2EB6";//找回密码页面地址
			String loginNames = "610424199309067615";//从客户端获取到的身份证号
			WebClient webclient=new WebClient(BrowserVersion.FIREFOX_45);

			
			webclient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
			webclient.getOptions().setTimeout(90000);
			webclient.getOptions().setCssEnabled(false);
			webclient.getOptions().setJavaScriptEnabled(true);
			webclient.setJavaScriptTimeout(40000);
			webclient.getOptions().setRedirectEnabled(true);
			webclient.getOptions().setThrowExceptionOnScriptError(false);
			webclient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webclient.setAjaxController(new NicelyResynchronizingAjaxController());
			webclient.getOptions().setCssEnabled(false);
			
			//获取学信网忘记密码页面的验证码
			UnexpectedPage Imagepage=  webclient.getPage(ImageUrl);
			BufferedImage bufferedImage  = ImageIO.read(Imagepage.getInputStream());
		    Date data=	new Date();
		    String time=new SimpleDateFormat("yyyyMMddhhmmssSSS").format(data);
			String ImageName="CHSIFINDPASSWORD"+time ;
			ImageIO.write(bufferedImage , "png", new File("D://CHSIFINDPASSWORD"+ImageName+".png"));
			Scanner scanner=new Scanner(System.in);
			//System.out.println(scanner.next());
			String capths=scanner.next();
			System.out.println("sssssss");
			HtmlPage htmlpage=  webclient.getPage(PageUrl);
			HtmlTextInput  loginName= (HtmlTextInput) htmlpage.getElementById("loginName");
			HtmlTextInput capth=   (HtmlTextInput) htmlpage.getElementById("captch");
			loginName.setValueAttribute(loginNames);
			capth.setValueAttribute(capths);
			HtmlButtonInput  htmlbutton=  (HtmlButtonInput) htmlpage.getElementById("newbutton");
			HtmlPage click = htmlbutton.click();
			//HtmlPage pageRest= (HtmlPage) htmlpage.executeJavaScript("$('#newbutton').click();").getNewPage();
			Thread.sleep(300);
			Map<String,Object> map=new HashMap<String, Object>();
			HtmlDivision Restcaptch= (HtmlDivision) click.getElementById("captch_tip_msg_area4");
			if(Restcaptch==null||Restcaptch.equals("")){
				HtmlDivision RestLoginName=(HtmlDivision) click.getElementById("loginName_tip_msg_area4");
				if(RestLoginName==null||RestLoginName.equals("")){
					System.out.println("成功！！！");
						map.put("xuexin1", click);   
						map.put("webclient", webclient);
						System.out.println("-------------------------------"+webclient);
						return map;
				}else{
					System.out.println("用户名错误"); 
				}
			}else{
				System.out.println("验证码错误");
			}
			return null;
		}
}
