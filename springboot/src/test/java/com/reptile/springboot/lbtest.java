package com.reptile.springboot;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class lbtest {
	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		WebClient webClient= new WebClient(BrowserVersion.INTERNET_EXPLORER);
		HtmlPage page= webClient.getPage("http://www.baidu.com");
		//System.out.println(page);
		HtmlSubmitInput htmlTextInput= (HtmlSubmitInput) page.getElementById("su");
		System.out.println(htmlTextInput+"------");
		HtmlTextInput kw=(HtmlTextInput) page.getElementById("kw");
		kw.setValueAttribute("你好");
		HtmlPage pageinfo= htmlTextInput.click();
		Thread.sleep(5000);
		//System.out.println(pageinfo.asXml());
		
		
		
	
		//http://192.168.3.222:8080/swagger-ui.html#!/interface-controller/XuexinGetCodeUsingPOST
		
	}

}
