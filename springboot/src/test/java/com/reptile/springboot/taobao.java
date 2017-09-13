package com.reptile.springboot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.http.ParseException;
import org.springframework.web.bind.annotation.RequestParam;

import net.sf.json.JSONObject;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.reptile.util.SimpleHttpClient;

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
//	}

	

//		 public static void main(String[] args) throws NotFoundException,IOException {
//
//		        MultiFormatReader formatReader=new MultiFormatReader();
//
//		        File file=new File("C://test.png");
//		        BufferedImage image=ImageIO.read(file);
//
//		        BinaryBitmap binaryBitmap=new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
//
//		        //定义二维码的参数:
//		        HashMap hints=new HashMap();
//		        hints.put(EncodeHintType.CHARACTER_SET,"utf-8");//定义字符集
//
//		        Result result=formatReader.decode(binaryBitmap,hints);//开始解析
//
//		        System.out.println("解析结果:"+result.toString());
//		        System.out.println("二维码的格式类型是:"+result.getBarcodeFormat());
//		        System.out.println("二维码的文本内容是:"+result.getText());
//	    }
		 
		 public static void main(String[] args) throws ParseException, IOException {
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
//			 webClient.addRequestHeader(":authority", "cf.aliyun.com");
//			 webClient.addRequestHeader(":method", "GET");
//			 webClient.addRequestHeader(":scheme", "https");
//			 webClient.addRequestHeader("accept", "cf.aliyun.com");
//			 webClient.addRequestHeader(":authority", "*/*");
//			 webClient.addRequestHeader(":authority", "cf.aliyun.com");
			 
			HtmlPage page= webClient.getPage("https://login.m.taobao.com/login.htm?_input_charset=utf-8");
			 System.out.println(page.asXml());
			 HtmlTextInput htmlTextInput= page.getElementByName("TPL_username");
			 htmlTextInput.setValueAttribute("qq1121212159");
			 HtmlPasswordInput htmlTextInput1= page.getElementByName("TPL_password");
			 htmlTextInput1.setValueAttribute("weizai@123");
			 HtmlButton  btn= (HtmlButton) page.getElementById("btn-submit");
			 page.executeJavaScript("var div=document.getElementsByClassName('icon nc-iconfont icon-notclick');div[0].click();");
			HtmlPage htmlPages= btn.click();
			 System.out.println(htmlPages.asXml());
			 
			 
//			 SimpleHttpClient test=new SimpleHttpClient();
//			 Map<String,String> headers=new HashMap<String,String>();
//			 Map<String,Object> data=new HashMap<String,Object>();
//					data.put("Usernumber", "12312");
//			 data.put("UserPwd", "UserPwd");
//			 data.put("Usernumber", "12312");
//			 data.put("Usercard", "Usercard");
//			 data.put("lt", "lt");
//			 data.put("code", "lt");
//			 
//	System.out.println(		 test.post("http://localhost:8080/interface/AcademicLogin", data, headers));
		}
//	}
//	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
//		WebClient webClient = new WebClient();
//		 webClient.getOptions().setUseInsecureSSL(true);
//		 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
//		 webClient.getOptions().setTimeout(100000);
//		 webClient.getOptions().setCssEnabled(true);
//		 webClient.getOptions().setJavaScriptEnabled(true);
//		 webClient.setJavaScriptTimeout(100000); 
//		 webClient.getOptions().setRedirectEnabled(true);
//		 webClient.getOptions().setThrowExceptionOnScriptError(false);
//		 webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
//		 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//		   HtmlPage page= webClient.getPage("asdasdas");
//		   HtmlDivision pages= (HtmlDivision) page.getElementById("asd");
//		
//		   
//	}
}
