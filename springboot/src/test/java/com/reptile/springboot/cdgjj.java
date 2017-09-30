package com.reptile.springboot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import com.reptile.util.SimpleHttpClient;

import scala.sys.process.ProcessBuilderImpl.Simple;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;


public class cdgjj  {



	//成都公积金（登录页面需要修改）
	public static void main(String args[]) throws Exception{ 
					      	System.out.println(Thread.currentThread().getName());  
							System.setProperty("webdriver.chrome.driver", "F:\\ie\\chromedriver.exe");
							WebDriver driver = new ChromeDriver();
							driver.get("https://www.cdzfgjj.gov.cn:9801/cdnt/login.jsp#corp");
							driver.navigate().refresh();
							Thread.sleep(5000);
							//gr
							WebElement gr = driver.findElement(By.id("gr"));
							gr.click();
//							 JavascriptExecutor jse = (JavascriptExecutor)driver;
//							   String s="document.getElementById('aType').value="+4;
//						       jse.executeScript(s);
							WebElement Type = driver.findElement(By.id("aType"));
							Type.sendKeys("4");
							WebElement j_username = driver.findElement(By.id("j_username"));
							j_username.sendKeys("220174544252");
							WebElement j_password = driver.findElement(By.id("j_password"));
							j_password.sendKeys("880205");
							Actions action = new Actions(driver); 
					       List<WebElement> buttons = driver.findElements(By.tagName("div"));
					       WebElement source=	    buttons.get(36);
					       action.clickAndHold(source).moveByOffset((int)(Math.random()*200)+80, 0);
					       Thread.sleep(2000);
					       action.clickAndHold(source).moveByOffset((int)(Math.random()*200)+80, 0);
					       Thread.sleep(2000);
					       action.clickAndHold(source).moveByOffset((int)(Math.random()*200)+80, 0);
					       Thread.sleep(2000);
					       action.clickAndHold(source).moveByOffset((int)(Math.random()*200)+80, 0);
					       Thread.sleep(2000);
	     		//拖动完释放鼠标
					       action.moveToElement(source).release();
	     		//  组织完这些一系列的步骤，然后开始真实执行操作
					       Action actions = action.build();
					       actions.perform();
					       WebElement login = driver.findElement(By.id("btn-login"));
					       login.click();    
					       Thread.sleep(5000);
					       driver.get("https://www.cdzfgjj.gov.cn:9802/cdnt/per/depositRecordQueryAction.do?menuid=259597");
					       Thread.sleep(5000);
					       Date date=new Date();
					       String year= new SimpleDateFormat("yyyy").format(date);
					       System.out.println(year+"--year--");
					    //  WebElement years= driver.findElement(By.id("year"));
					       JavascriptExecutor jse1 = (JavascriptExecutor)driver;
					       String s1="document.getElementById('year').value="+year;
					       jse1.executeScript(s1);
					       driver.findElement(By.className("icon-search")).click();
					       WebElement account=  driver.findElement(By.id("dataList"));
					       List<WebElement> list = (List<WebElement>) account.findElements(By.tagName("div"));
					       for (int i = 0; i < list.size(); i++) {
							System.out.println(list.get(i).getText()+"--------------");
						}
					       
	  }
	
}

