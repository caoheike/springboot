package com.reptile.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class updata {
public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
	WebClient webClient = new WebClient(BrowserVersion.CHROME);
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
	 HtmlPage page= webClient.getPage("https://uac.10010.com/cust/resetpwd/inputName");
	 HtmlTextInput htmlTextInput=page.getElementByName("resetpassBean.loginName");
	 HtmlTextInput htmlTextInput2=page.getElementByName("verifyCode");
	 HtmlImage htmlImage=(HtmlImage) page.getElementById("verifyCodeImg");
	 BufferedImage imgBufferedImage=htmlImage.getImageReader().read(0);
	 ImageIO.write(imgBufferedImage,"png", new File("c://weizia.png"));
	 Scanner scanner=new Scanner(System.in);
	 String code= scanner.next();
	 htmlTextInput.setValueAttribute("13201618083");
	 htmlTextInput2.setValueAttribute(code);
	HtmlPage pageino= (HtmlPage) page.executeJavaScript("subInputName();").getNewPage();
	Thread.sleep(4000);
	System.out.println(pageino.asXml());
	 
	 
	 
	 
	 
}
}
