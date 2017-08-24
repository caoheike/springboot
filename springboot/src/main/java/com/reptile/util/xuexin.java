package com.reptile.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
/**
 * 完美的学信网爬去
 * @author Administrator
 *
 */
public class xuexin {
	//private static String username="513721199510106811";
	//private static String userpwd="598415805";
	private static String username="123";
	private static String userpwd="123";
//	
public static void main(String[] args) throws FailingHttpStatusCodeException, IOException {
				Map<String,Object> map=new HashMap<String, Object>();
				WebClient webClient = new WebClient();
				webClient.getOptions().setUseInsecureSSL(true);
				webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
				webClient.getOptions().setTimeout(100000);
				webClient.getOptions().setCssEnabled(false);
				webClient.getOptions().setJavaScriptEnabled(true);
				webClient.setJavaScriptTimeout(100000); 
				webClient.getOptions().setRedirectEnabled(true);
				webClient.getOptions().setThrowExceptionOnScriptError(false);
				webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
				webClient.setAjaxController(new NicelyResynchronizingAjaxController());
				HtmlPage pagelt= webClient.getPage("https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check");
				HtmlHiddenInput hiddenInput= pagelt.getElementByName("lt");
				String lt=hiddenInput.getValueAttribute();
				UnexpectedPage pageimg=webClient.getPage("https://account.chsi.com.cn/passport/captcha.image?id=68.95757530327288");
				BufferedImage img=ImageIO.read(pageimg.getInputStream());
				 ImageIO.write(img,"png", new File("F:/Test.png"));
				 Scanner scanner =new Scanner(System.in);
				 String ycm=scanner.next();
				 System.out.println(ycm);
				WebRequest webRequest=new  WebRequest(new java.net.URL("https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check"));

				List<NameValuePair> list=new ArrayList<NameValuePair>();
				list.add(new NameValuePair("username",username));
				list.add(new NameValuePair("password",userpwd));
				list.add(new NameValuePair("captcha", ycm));
	
				list.add(new NameValuePair("lt", lt));
				list.add(new NameValuePair("_eventId","submit"));
				list.add(new NameValuePair("submit","登  录"));
				
				webRequest.setHttpMethod(HttpMethod.POST);
				webRequest.setRequestParameters(list);
				HtmlPage pages= webClient.getPage(webRequest);
				//HtmlDivision Logindiv= (HtmlDivision) pages.getElementById("status");
				System.out.print(pages.asText());
	
	
}
	
//	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
//		WebClient webClient = new WebClient();
//		webClient.getOptions().setUseInsecureSSL(true);
//		webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
//		webClient.getOptions().setTimeout(100000);
//		webClient.getOptions().setCssEnabled(false);
//		webClient.getOptions().setJavaScriptEnabled(true);
//		webClient.setJavaScriptTimeout(100000); 
//		webClient.getOptions().setRedirectEnabled(true);
//		webClient.getOptions().setThrowExceptionOnScriptError(false);
//		webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
//		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//		UnexpectedPage pageimg= webClient.getPage("https://account.chsi.com.cn/passport/captcha.image?id=5975.389368330657");
//	
//		BufferedImage img=ImageIO.read(pageimg.getInputStream());
//			 ImageIO.write(img,"png", new File("F:/Test.png"));
//		 Scanner scanner =new Scanner(System.in);
//		HtmlPage page= webClient.getPage("https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check");
//		HtmlTextInput htmlTextInput= (HtmlTextInput) page.getElementById("username");
//		HtmlPasswordInput htmlPasswordInput= (HtmlPasswordInput) page.getElementById("password");
//		HtmlTextInput texxxx=(HtmlTextInput) page.getElementById("captcha");
//		texxxx.setValueAttribute(scanner.next());
//		
//		htmlTextInput.setValueAttribute("513721199510106811");
//		htmlPasswordInput.setValueAttribute("5984158051");
//        HtmlSubmitInput submit=page.getElementByName("submit"); 
//        HtmlPage pages=  submit.click();
//		//https://account.chsi.com.cn/passport/captcha.image?id=5975.389368330657
//	}
}
