package com.reptile.util;

import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class  DriverUtil{
	
	/**
	 * 显示等待，通过title来判断
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
