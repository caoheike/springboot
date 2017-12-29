package com.reptile.util;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 
 * @ClassName: DriverUtil  
 * @Description: TODO (DriverUtil)
 * @author: xuesongcui
 * @date 2017年12月29日  
 *
 */
public class  DriverUtil{
	
	private static String ieBrowser = "ie";
	
	public static WebDriver getDriverInstance(String type){
		WebDriver driver = null;
		if(ieBrowser.equals(type)){
			System.setProperty("webdriver.ie.driver", "C:/ie/IEDriverServer.exe");
			DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
			ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
			ieCapabilities.setCapability(InternetExplorerDriver.BROWSER_ATTACH_TIMEOUT,15000);
			driver = new InternetExplorerDriver(ieCapabilities);
		}
		return driver;
	}
	
	/**
	 * 显示等待，通过title来判断元素是否存在
	 * @param title 网站标题
	 * @param driver 
	 * @param time 多长时间关闭 单位 秒
	 */
	public static boolean waitByTitle(String title,WebDriver driver,int time){
      WebDriverWait wite = new WebDriverWait(driver,time);
      try {
    	  wite.until(ExpectedConditions.titleContains(title));
      } catch (Exception e) {
		  return false;
      }
      return true;
    
	}
	
	
	/**
	 * 显示等待，通过className来判断元素是否存在
	 * @param className 
	 * @param driver 
	 * @param time 多长时间关闭 单位 秒
	 */
	public static boolean waitByClassName(String className,WebDriver driver,int time){
		WebDriverWait wite = new WebDriverWait(driver,time);
		try {
			wite.until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * 显示等待，通过id来判断元素是否存在
	 * @param className 
	 * @param driver 
	 * @param time 多长时间关闭 单位 秒
	 */
	public static boolean waitById(String id,WebDriver driver,int time){
		WebDriverWait wite = new WebDriverWait(driver,time);
		try {
			wite.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 显示等待，通过linkText来判断元素是否存在
	 * @param linkText 
	 * @param driver 
	 * @param time 多长时间关闭 单位 秒
	 */
	public static boolean waitByLinkText(String linkText,WebDriver driver,int time){
		WebDriverWait wite = new WebDriverWait(driver,time);
		try {
			wite.until(ExpectedConditions.presenceOfElementLocated(By.linkText(linkText)));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * 显示等待，通过ID判断元素是否可见，可见代表元素非隐藏，并且宽和高非0
	 * @param id 
	 * @param driver 
	 * @param time 多长时间关闭 单位 秒
	 */
	public static boolean visibilityById(String id,WebDriver driver,int time){
		WebDriverWait wite = new WebDriverWait(driver,time);
		try {
			wite.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * 显示等待，通过className判断元素是否可见，可见代表元素非隐藏，并且宽和高非0
	 * @param className 
	 * @param driver 
	 * @param time 多长时间关闭 单位 秒
	 */
	public static boolean visibilityByClassName(String className,WebDriver driver,int time){
		WebDriverWait wite = new WebDriverWait(driver,time);
		try {
			wite.until(ExpectedConditions.visibilityOfElementLocated(By.className(className)));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * 关闭所有进程(针对64位的)，仅支持同步
	 * @param driver
	 * @throws IOException 
	 */
	public static void close(WebDriver driver,String exec) throws IOException{
		if(driver != null){
			driver.close();
			Runtime.getRuntime().exec(exec);
		}
	}
	
	
	/**
	 * 关闭所有进程(针对32位的)
	 * @param driver
	 */
	public static void close(WebDriver driver){
		if(driver != null){
			driver.quit();
		}
	}
	

}
