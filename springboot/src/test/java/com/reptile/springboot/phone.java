package com.reptile.springboot;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class phone {
	public static void main(String[] args) throws Exception {
		String ImageURL="https://login.10086.cn/html/login/login.html";
		WebClient webclient=new WebClient();
		webclient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
		webclient.getOptions().setTimeout(90000);
		webclient.getOptions().setCssEnabled(false);
		webclient.getOptions().setJavaScriptEnabled(true);
		webclient.setJavaScriptTimeout(40000);
		webclient.getOptions().setRedirectEnabled(true);//重定向
		webclient.getOptions().setThrowExceptionOnScriptError(false);//js 异常
		webclient.getOptions().setThrowExceptionOnFailingStatusCode(true);//返回码
		webclient.setAjaxController(new NicelyResynchronizingAjaxController());//ajax
		HtmlPage PhonePage= webclient.getPage(ImageURL);
		
	}
}
