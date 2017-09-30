package com.reptile.springboot;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class XNgjj {
	public static void main(String[] args) throws Exception {
		String Usernum="630102196401182910";
		String password="111111";
		WebClient webclient=new WebClient();
		webclient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
		webclient.getOptions().setTimeout(90000);
		webclient.getOptions().setCssEnabled(false);
		webclient.getOptions().setJavaScriptEnabled(true);
		webclient.setJavaScriptTimeout(40000);
		webclient.getOptions().setRedirectEnabled(true);
		webclient.getOptions().setThrowExceptionOnScriptError(false);
		webclient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webclient.setAjaxController(new NicelyResynchronizingAjaxController());
		webclient.getOptions().setCssEnabled(false);
		HtmlPage page= webclient.getPage(
				"http://xngjj.gov.cn/wscx/zfbzgl/zfbzsq/login_hidden.jsp?password="
				+ password
				+ "&sfzh="
				+ Usernum
				+ "&cxyd=&dbname=wasys350&dlfs=0");
		
		System.out.println(page.asXml());
		    	page.getElementByName("zgzh");//
				page.getElementByName("sfzh");//身份证号
				page.getElementByName("zgxm");//用户名
				page.getElementByName("dwbm");//
				page.getElementByName("zgzt");//状态
	}
}
