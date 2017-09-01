package com.reptile.springboot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import net.sf.json.JSONObject;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class taobao {

//	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
//WebClient webClient = new WebClient();
// webClient.getOptions().setUseInsecureSSL(true);
// webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
// webClient.getOptions().setTimeout(100000);
// webClient.getOptions().setCssEnabled(true);
// webClient.getOptions().setJavaScriptEnabled(true);
// webClient.setJavaScriptTimeout(100000); 
// webClient.getOptions().setRedirectEnabled(true);
// webClient.getOptions().setThrowExceptionOnScriptError(false);
// webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
// webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//  TextPage page= webClient.getPage("https://qrlogin.taobao.com/qrcodelogin/generateQRCode4Login.do");
//  JSONObject jsonObject=JSONObject.fromObject(page.getContent());
//  UnexpectedPage paerwm= webClient.getPage("https:"+jsonObject.get("url"));
//  System.out.println(jsonObject.get("url"));
//  BufferedImage img=ImageIO.read(paerwm.getInputStream());
//	ImageIO.write(img,"png", new File("C://test.png"));
//	Scanner scanner=new Scanner(System.in);
//	System.out.println(scanner.next());
//	TextPage pages= webClient.getPage("https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do?lgToken="+jsonObject.get("lgToken")+"&defaulturl=https%3A%2F%2Fwww.taobao.com%2F");
//	System.out.println(pages.getContent());
//	JSONObject jsonObject2=JSONObject.fromObject(pages.getContent());
//	if(jsonObject2.get("code").equals("10006")){
//	HtmlPage pageinfo= webClient.getPage(jsonObject2.getString("url"));
//	System.out.println(pageinfo.asXml());
//	}else if(jsonObject2.get("code").equals("10004")){
//		System.out.println("二维码过期");
//	}else if(jsonObject2.get("code").equals("10001")) {
//		System.out.println("扫码陈功");
//	}else if(jsonObject2.get("code").equals("10000")){
//		System.out.println("等待扫码");
//		
//	}
// 
// 
//	}]
	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient webClient = new WebClient();
		 webClient.getOptions().setUseInsecureSSL(true);
		 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
		 webClient.getOptions().setTimeout(100000);
		 webClient.getOptions().setCssEnabled(true);
		 webClient.getOptions().setJavaScriptEnabled(true);
		 webClient.setJavaScriptTimeout(100000); 
		 webClient.getOptions().setRedirectEnabled(true);
		 webClient.getOptions().setThrowExceptionOnScriptError(false);
		 webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
		 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		   HtmlPage page= webClient.getPage("asdasdas");
		   HtmlDivision pages= (HtmlDivision) page.getElementById("asd");
		
		   
	}

}
