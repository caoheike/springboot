package com.reptile.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.springboot.Scheduler;
import com.reptile.util.WebClientFactory;

@Service
public class PhoneloginService {
 
	public  Map<String,Object> login(HttpServletRequest request, String userName, String servePwd){
		 Map<String, Object> map = new HashMap<String, Object>();
	        HttpSession session = request.getSession();
	    
	        return map;
		
	}
}
