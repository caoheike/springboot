package com.reptile.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;
@Service
public class QuitDriverService {
	public Map<String, String> driverClose(HttpServletRequest request,String driverName) {
		Map<String, String> map=new HashMap< String, String>();
		 HttpSession session = request.getSession();
		 Object drivers = session.getAttribute(driverName);
	     WebDriver driver =(WebDriver) drivers;
	     String dName=    driver.getClass().getName();
		if(driver!=null){
			try {
				if(dName.contains("ChromeDriver")){
					driver.close();
					Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
				}else{
					driver.quit();
					Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
				}
				map.put("errorCode", "0000");
				return map;
			} catch (IOException e) {
				System.out.println("driver关闭过程中出错");
				e.printStackTrace();
			}
		}
		map.put("errorCode", "0001");
	     return map;
}
}