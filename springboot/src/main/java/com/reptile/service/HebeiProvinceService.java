package com.reptile.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

@Service
public class HebeiProvinceService {
	
	public static Map<String,Object> HebeiUsercard(HttpServletRequest request,String UserNume,String UserPass){
		
		
		//http://he.189.cn/service/bill/feeQuery_iframe.jsp?SERV_NO=9A001&fastcode=00380406&cityCode=he
		
		return null;
	}
	public static Map<String,Object> HebeiUsercard1(HttpServletRequest request){
		System.out.println("进入河北电信");
		System.setProperty("webdriver.chrome.driver", "F:\\ie\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.get("http://login.189.cn/web/login");
		driver.navigate().refresh();
		try {
			Thread.sleep(5000);
			WebElement j_username = driver.findElement(By.id("txtAccount"));
			j_username.sendKeys("111111111111");
			Thread.sleep(2000);
			WebElement j_Password = driver.findElement(By.id("txtPassword"));
			j_Password.sendKeys("111111111111");
			Thread.sleep(2000);
			System.out.println("开始点击");
            driver.findElement(By.id("loginbtn")).click();
            Thread.sleep(2000);
            driver.get("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=00380407");
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
