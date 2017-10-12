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
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;

@Service
public class HebeiProvinceService {
	 @Autowired
	  private application applications;
	 /**
		 * 
		 * @param 需要手机号，查询密码，手机号实名认证的姓名，手机号绑定 的身份证号
		 * @param session 中存当前浏览器，通过手机号区分session 
		 * @return 返回推送状态
		 */
	public static Map<String,Object> HebeiUsercard1(HttpServletRequest request,String Usernum,String UserPass,String Username,String Usercode){
		Map<String,Object> map=new HashMap<String,Object>();
//		System.setProperty("webdriver.ie.driver", "F:\\ie\\IEDriverServer.exe");
//		WebDriver driver = new InternetExplorerDriver();
		System.setProperty("webdriver.chrome.driver", "D:\\ie\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		
		driver.get("http://login.189.cn/web/login");//电信登录地址
		driver.navigate().refresh();
		try {
				Thread.sleep(3000);
				WebElement form = driver.findElement(By.id("loginForm"));
				Thread.sleep(500);
				WebElement account = form.findElement(By.id("txtAccount"));
				account.sendKeys(Usernum);//手机号
				Thread.sleep(500);
				WebElement passWord = form.findElement(By.id("txtShowPwd"));
				passWord.click();
				WebElement passWord1 = form.findElement(By.id("txtPassword"));
				passWord1.sendKeys(UserPass);//查询密码
				WebElement loginBtn = driver.findElement(By.id("loginbtn"));
				loginBtn.click();
				Thread.sleep(5000);
				driver.get("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=00380407");//详单
				Thread.sleep(3000);
				driver.get("http://he.189.cn/service/bill/feeQuery_iframe.jsp?SERV_NO=SHQD1&fastcode=00380407&cityCode=he");//详单iframe
				Thread.sleep(3000);
				driver.findElement(By.id("CustName")).sendKeys(Username);//姓名
				Thread.sleep(2000);
				driver.findElement(By.id("IdentityCode")).sendKeys(Usercode);//身份证号
				Thread.sleep(2000);
				driver.findElement(ByClassName.className("pub_btn_s")).click();
				Thread.sleep(1000);
				WebElement a= driver.findElement(By.id("MotoText"));
				a.findElement(By.tagName("a")).click();//短信验证码发送成功
		  		HttpSession session=request.getSession();//获得session
		  		session.setAttribute("sessionDriver-Hebei"+Usernum, driver);//session中存浏览器
		  		map.put("errorCode", "0000");
     			map.put("errorInfo", "验证码发送成功!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
				map.put("errorCode", "0001");
				map.put("errorInfo", "服务异常！请重新尝试发送验证码");
				driver.close();
		}
		return map;
		
	}
	//河北电信获取通话详单
	public Map<String, Object> HebeiUsercard2(HttpServletRequest request, String Usernum, String UserPass, String capth) {
		 Map<String,Object> map = new HashMap<String,Object>();
		// TODO Auto-generated method stub
		System.out.println("进入短信提交页面");
		HttpSession session=request.getSession();//获得session
		Object sessiondriver = session.getAttribute("sessionDriver-Hebei"+Usernum);
		   if (sessiondriver == null) {
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "操作异常!");
	            return map;
	        } else {
		final WebDriver driver = (WebDriver) sessiondriver;
		WebElement _FUNC_ID_=driver.findElement(By.id("_FUNC_ID_"));
		String a= _FUNC_ID_.getAttribute("value");
		WebElement qryList=driver.findElement(By.id("qryList"));
		WebElement CITY_CODE= qryList.findElement(By.name("CITY_CODE"));
		String CITYid= CITY_CODE.getAttribute("value");
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
				PushState.state(Usernum, "callLog",100);
				List<Map<String,Object>> datalist=new ArrayList<Map<String,Object>>();
				System.out.println("开始获取详单");
				for (int i = 0; i < 6; i++) {
					Map<String,Object>map1=new HashMap<String,Object>();
					Thread.sleep(3000);
					Select userSelect=new Select(driver.findElement(By.id("ACCT_DATE")));
					userSelect.selectByIndex(i);
					Thread.sleep(3000);
					as.get(2).click();
					Thread.sleep(3000);
					driver.switchTo().frame("iFrmMain");
					map1.put("items", driver.getPageSource());
					Thread.sleep(3000);
					datalist.add(map1);
					driver.switchTo().parentFrame();
					System.out.println(i+"--通话详单"+i+"----");
					}
				HEBEI.put("data", datalist);
				HEBEI.put("UserIphone", Usernum);
				HEBEI.put("flag", 14);
				HEBEI.put("UserPassword", UserPass);
				Resttemplate resttemplate = new Resttemplate();
				map=resttemplate.SendMessage(HEBEI,applications.getSendip()+"/HSDC/message/telecomCallRecord");
			
				if(map!=null&&"0000".equals(map.get("errorCode").toString())){
			    	PushState.state(Usernum, "callLog",300);
	                map.put("errorInfo","查询成功");
	                map.put("errorCode","0000");
	                driver.close();
	         }else{
	            	//--------------------数据中心推送状态----------------------
	            	PushState.state(Usernum, "callLog",200);
	            	//---------------------数据中心推送状态----------------------
	                map.put("errorInfo","响应异常,请重试");
	                map.put("errorCode","0001");
	                driver.close();
	          }
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			 e1.printStackTrace();
			 map.clear();
			 map.put("errorInfo","服务繁忙，请稍后再试");
			 map.put("errorCode","0002");
			 driver.close();
		}
		}
		return map;
	}
	public Map<String,Object> xingdan(HttpServletRequest request,int i){
		Map<String,Object>map=new HashMap<String,Object>();
		HttpSession session=request.getSession();//获得session
		Object sessiondriver = session.getAttribute("sessionDriver-Hebei"+"18033850229"+i);
		final WebDriver driver = (WebDriver) sessiondriver;
		Select userSelect=new Select(driver.findElement(By.id("ACCT_DATE")));
		userSelect.selectByIndex(i);
		try {
			List<WebElement> as= driver.findElements(By.className("pub_btn_s"));
			Thread.sleep(5000);
			as.get(2).click();
			Thread.sleep(5000);
			driver.switchTo().frame("iFrmMain");
			System.out.println(driver.getPageSource());
			map.put("items", driver.getPageSource());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
		
	}
}
