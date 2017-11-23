package com.reptile.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;
@Service
public class QuitDriverService {
	public boolean driverClose(HttpServletRequest request,String driverName) {
		 HttpSession session = request.getSession();
		 Object drivers = session.getAttribute(driverName);
	     WebDriver driver =(WebDriver) drivers;
	     String dName=    driver.getClass().getName();
	     if(driver!=null){
	    	 driver.close();
	    	 try {
	    	 if(dName.contains("ChromeDriver")){
	    		Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");		
	 	     }else{
				Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");   
	         }
	    	 return true;
	    	 } catch (IOException e) {
					System.out.println("driver关闭过程中出错");
					e.printStackTrace();
		     }
	     } 
	     return false;
}
}