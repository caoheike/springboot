package com.reptile.springboot;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class hebei {
	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "F:\\ie\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.get("http://login.189.cn/web/login");
		driver.navigate().refresh();
		try {
			Thread.sleep(5000);
			WebElement j_username = driver.findElement(By.id("txtAccount"));
			j_username.sendKeys("111111111111");
			Thread.sleep(2000);
			System.out.println("进入河北电信");
			WebElement j_Password = driver.findElement(By.name("Password"));
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
		
	}
}
