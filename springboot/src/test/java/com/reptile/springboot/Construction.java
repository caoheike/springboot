package com.reptile.springboot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Construction {
	public static void main(String[] args) {
		
		System.out.println(Thread.currentThread().getName());  
		System.setProperty("webdriver.chrome.driver", "F:/ie/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.get("http://creditcard.ccb.com/tran/WCCMainPlatV5?CCB_IBSVersion=V5&SERVLET_NAME=WCCMainPlatV5&TXCODE=NE3050");
		driver.switchTo().frame("itemiframe");
		try {
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			Thread.sleep(2000);
			WebElement Acc_no_temp= driver.findElement(By.id("ACC_NO_temp"));
			Acc_no_temp.click();
			Acc_no_temp.sendKeys("6259655770061071");
			driver.findElement(By.id("LOGPASS")).sendKeys("246421");
//			Thread.sleep(1000);
			driver.findElement(By.className("btn_blue")).click();//点击登录按钮
//			Thread.sleep(1000);
			System.out.println(driver.findElement(By.className("crd_box")).getText()) ;//账单日，可用额度，信用额度，取款额度信息
			driver.switchTo().frame("result1");
//			Thread.sleep(1000);
			WebElement table= driver.findElement(By.className("pbd_table_form"));//获取table ,再获取其中a标签
			WebElement a= table.findElement(By.tagName("a"));
			for (int i = 1; i < 9; i++) {
				Map<String,Object>map1=new HashMap<String,Object>();
//				Thread.sleep(2000);
				driver.findElement(By.className("select_value")).click();
//				driver.findElement(By.xpath("//div[@class='select_ul']/li["+i+"]")).click(); 
				driver.findElement(By.xpath("//*[@id='jqueryFrom']/div/table/tbody/tr/td[3]/div/div/ul/li["+i+"]")).click(); 
//				Thread.sleep(2000);
				a.click();
//				Thread.sleep(2000);
				driver.switchTo().frame("result2");
				
					driver.findElement(By.id("page"));
					String text= driver.getPageSource();
						map1.put("items", driver.getPageSource());
						System.out.println("-----------"+i+"------------");
						System.out.println(driver.getPageSource());
						driver.switchTo().parentFrame();
						
			}
		} catch (InterruptedException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
