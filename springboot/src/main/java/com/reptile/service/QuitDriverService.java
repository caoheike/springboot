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
	     if(driver!=null){
	    	 driver.close();
	            try {
					Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
					return true;
				} catch (IOException e) {
					 
					e.printStackTrace();
				} 
	     }
		return false;
		
	} 
}
