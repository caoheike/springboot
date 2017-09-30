package com.reptile.springboot;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.sun.jna.NativeLibrary;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
public class Chrome {





		public static void main(String args[]) throws Exception{ 
		    Map<String,Object> head=new HashMap<String, Object>();//参数
		    Map<String,Object> paramse=new HashMap<String, Object>();//参数
//			SimpleHttpClient httclien=new SimpleHttpClient();
		      System.out.println(Thread.currentThread().getName());  
				System.setProperty("webdriver.chrome.driver", "F:/ie/chromedriver.exe");
				WebDriver driver = new ChromeDriver();
				driver.get("https://login.taobao.com/member/login.jhtml");
		
				  driver.navigate().refresh();
				WebElement txtbtnVerifyCode = driver.findElement(By.id("J_Quick2Static"));
				txtbtnVerifyCode.click();
//				WEBELEMENT XX = DRIVER.FINDELEMENT(BY.ID("J_NICKX1504506261424"));
//				XX.CLICK();
				WebElement TPL_username_1 = driver.findElement(By.id("TPL_username_1"));
				TPL_username_1.sendKeys("qq1121212159");
				WebElement password = driver.findElement(By.id("TPL_password_1"));
				password.sendKeys("weizai..");
//			       Actions action = new Actions(driver); 
//	               //获取滑动滑块的标签元素
//		       WebElement source = driver.findElement(By.id("nc_1__bg"));
//		       action.clickAndHold(source).moveByOffset((int)(Math.random()*200)+80, 0);
//	           Thread.sleep(2000);
//	           action.clickAndHold(source).moveByOffset((int)(Math.random()*200)+80, 0);
//	           Thread.sleep(2000);
//	           action.clickAndHold(source).moveByOffset((int)(Math.random()*200)+80, 0);
//	           Thread.sleep(2000);
//	           action.clickAndHold(source).moveByOffset((int)(Math.random()*200)+80, 0);
//	           Thread.sleep(2000);
//	           //拖动完释放鼠标
//	           action.moveToElement(source).release();
//	       //组织完这些一系列的步骤，然后开始真实执行操作
//	       Action actions = action.build();
//	       actions.perform();
				String pageSource = driver.getPageSource();
	       org.openqa.selenium.JavascriptExecutor executor = (org.openqa.selenium.JavascriptExecutor)driver;
	       boolean equals = executor.executeScript("return document.readyState").equals("complete");
	       int moveX =129;//移动位置
	       if (equals) {
	           WebElement element = driver.findElement(By.id("nc_1__bg"));//(".gt_slider_knob"));
	           Point location = element.getLocation();
	           element.getSize();
	           Actions action = new Actions(driver); 
	           //             action.clickAndHold().perform();// 鼠标在当前位置点击后不释放
//	            action.clickAndHold(element).perform();// 鼠标在 onElement 元素的位置点击后不释放
//	            action.clickAndHold(element).moveByOffset(location.x+99,location.y).release().perform(); //选中source元素->拖放到（xOffset,yOffset）位置->释放左键
	           
	           action.dragAndDropBy(element, location.x+moveX,location.y).perform();
	      
	        
//	           action.dragAndDrop(element,newelement).perform();
	           pageSource = driver.getPageSource();
	  
	       }
//				WebElement button = driver.findElement(By.id("J_SubmitStatic"));
//				button.click();
//				Thread.sleep(3000);
//				System.out.println(driver.getTitle());
//				if(driver.getTitle().contains("身份验证")){
//					Set<Cookie> cookies = driver.manage().getCookies();  
//					StringBuffer tmpcookies = new StringBuffer();
//			        for (Cookie cookie : cookies) {
//			      	   	tmpcookies.append(cookie.toString()+";");	 			
//					}
//			        head.put("Accept", "application/json, text/javascript, */*; q=0.01");
//			        head.put("Accept-Encoding", "gzip, deflate");	
//			        head.put("Accept-Language", "zh-CN");
//			        head.put("Cache-Control", "no-cache");
//			        head.put("Connection", "Keep-Alive");
//			        head.put("Host", "passport.taobao.com");
//			        head.put("Referer", "https://passport.taobao.com/iv/identity_verify.htm?tag=8&htoken=YSJvkNaBSP6Y3Vk-nopnUAQAI1-Cy0rffcLn9EMcThULLlJLlgwEo-pXi45mx9xX&appName=");
//			        head.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
//			        head.put("X-Requested-With", "XMLHttpRequest");
			        
			        

//			               System.out.println(tmpcookies+"----");
//			 
//			        
//			    
	//	
//			    // String rest1=httclien.post(" https://passport.taobao.com/iv/phone/send_code.do?htoken=YSJvkNaBSP6Y3Vk-nopnUAQAI1-Cy0rffcLn9EMcThULLlJLlgwEo-pXi45mx9xX&phone=*******4780&type=phone&area=86&tag=86&_=1504503601917", params, headers);//开始发包
//				}else{
//					
//				}
//				
				
				
				
				
				
				
				
			

	     }
		
		
		
		
		

	}


