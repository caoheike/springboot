package com.reptile.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;

@Service
public class HebeiProvinceService {
	 @Autowired
	  private application applications;
	
	public static Map<String,Object> HebeiUsercard(HttpServletRequest request,String UserNume,String UserPass){
		
		
		//http://he.189.cn/service/bill/feeQuery_iframe.jsp?SERV_NO=9A001&fastcode=00380406&cityCode=he
		
		return null;
	}
	public static Map<String,Object> HebeiUsercard1(HttpServletRequest request){
		Map<String,Object> map=new HashMap<String,Object>();
//		System.setProperty("webdriver.ie.driver", "F:\\ie\\IEDriverServer.exe");
//		WebDriver driver = new InternetExplorerDriver();
		System.setProperty("webdriver.chrome.driver", "F:\\ie\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		
		driver.get("http://login.189.cn/web/login");
		driver.navigate().refresh();
		try {
				Thread.sleep(5000);
				WebElement form = driver.findElement(By.id("loginForm"));

				Thread.sleep(500);
				WebElement account = form.findElement(By.id("txtAccount"));
				account.sendKeys("18033850229");
				Thread.sleep(500);
				WebElement passWord = form.findElement(By.id("txtShowPwd"));
				passWord.click();
				WebElement passWord1 = form.findElement(By.id("txtPassword"));
				passWord1.sendKeys("010300");
				WebElement loginBtn = driver.findElement(By.id("loginbtn"));
				loginBtn.click();
				Thread.sleep(5000);
				driver.get("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=00380407");
				Thread.sleep(5000);
				driver.get("http://he.189.cn/service/bill/feeQuery_iframe.jsp?SERV_NO=SHQD1&fastcode=00380407&cityCode=he");
				Thread.sleep(5000);
				driver.findElement(By.id("CustName")).sendKeys("杨琪");
				Thread.sleep(2000);
				driver.findElement(By.id("IdentityCode")).sendKeys("130184199412020028");
				Thread.sleep(2000);
				driver.findElement(ByClassName.className("pub_btn_s")).click();
				Thread.sleep(1000);
				WebElement a= driver.findElement(By.id("MotoText"));
				a.findElement(By.tagName("a")).click();//短信验证码发送成功
		  		HttpSession session=request.getSession();//获得session
		  		session.setAttribute("sessionDriver-Hebei"+"18033850229", driver);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
		
	}
	//河北电信获取通话详单
	public Map<String, Object> HebeiUsercard2(HttpServletRequest request, String capth) {
		 Map<String,Object> map = new HashMap<String,Object>();
		// TODO Auto-generated method stub
		System.out.println("进入短信提交页面");
		HttpSession session=request.getSession();//获得session
		Object sessiondriver = session.getAttribute("sessionDriver-Hebei"+"18033850229");
		final WebDriver driver = (WebDriver) sessiondriver;
		
		WebElement _FUNC_ID_=driver.findElement(By.id("_FUNC_ID_"));
		String a= _FUNC_ID_.getAttribute("value");
		System.out.println(a);
		WebElement qryList=driver.findElement(By.id("qryList"));
		WebElement CITY_CODE= qryList.findElement(By.name("CITY_CODE"));
		String CITYid= CITY_CODE.getAttribute("value");
		System.out.println(CITYid);
		WebClient webClient = new WebClient ();
		List<NameValuePair> list4 = new ArrayList<NameValuePair>();
		WebRequest requests4;
		driver.findElement(By.id("MOBILE_CODE")).sendKeys(capth);
		  Map<String,Object>HEBEI=new HashMap<String,Object>();
		try {
				Thread.sleep(3000);
				List<WebElement> as= driver.findElements(By.className("pub_btn_s"));
				as.get(1).click();
				Thread.sleep(5000);
				Select userSelect=new Select(driver.findElement(By.id("ACCT_DATE")));
				for (int i = 0; i < 6; i++) {
					userSelect.selectByIndex(i);
					Thread.sleep(3000);
					as.get(2).click();
					Thread.sleep(5000);
				    driver.switchTo().frame("iFrmMain");
				    System.out.println(driver.getPageSource());
					HEBEI.put("data", driver.getPageSource());
//				    Set<String> handles = driver.getWindowHandles();    //得到所有窗口句柄
//		               Iterator<String> it = handles.iterator();
//		               String next = it.next();                            //此处是第一个窗口句柄
//		               System.out.println("第一个窗口句柄："+next);
//		               WebDriver  window = driver.switchTo().window(it.next());  //跳转第二个窗口
//		               Thread.sleep(1900);
//		               window.close();                                           //关闭第二个窗口
//		               Thread.sleep(1900);
//		               window = driver.switchTo().window(next);         //跳转第一个窗口
				  
				}
			
				HEBEI.put("UserIphone", "18033850229");
				HEBEI.put("flag", 14);
				HEBEI.put("UserPassword", "测试密码");
				Resttemplate resttemplate = new Resttemplate();
				map=resttemplate.SendMessage(HEBEI,"http://192.168.3.35:8080"+"/HSDC/message/telecomCallRecord");
		    
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return null;
	}
}
