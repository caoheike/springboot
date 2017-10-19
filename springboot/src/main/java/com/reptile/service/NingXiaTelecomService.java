package com.reptile.service;

import com.reptile.util.GetMonth;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class NingXiaTelecomService {
	@Autowired
	private application application;
	/**
	 * 登陆
	 * @param request
	 * @param phoneNumber
	 * @param servePwd
	 * @return
	 */
	 public Map<String, Object> ningXiaLogin(HttpServletRequest request, String phoneNumber, String servePwd){Map<String, Object> map = new HashMap<String, Object>();
		System.setProperty("webdriver.chrome.driver",
				"C:\\Program Files\\iedriver\\chromedriver.exe");
		//C:\\Program Files\\iedriver\\chromedriver.exe  正式上用这个
		ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
		WebDriver driver = new ChromeDriver(options);
		driver.get("http://login.189.cn/web/login");
			
		driver.navigate().refresh();
		try {
			Thread.sleep(500);
		WebElement form = driver.findElement(By.id("loginForm"));
		Thread.sleep(500);
		WebElement account = form.findElement(By.id("txtAccount"));
		// account.clear();
		account.sendKeys(phoneNumber);
		Thread.sleep(500);
			
		//driver.switchTo().frame("name");
		WebElement passWord = form.findElement(By.id("txtShowPwd"));
		passWord.click();
		WebElement passWord1 = form.findElement(By.id("txtPassword"));
		passWord1.sendKeys(servePwd);
//===========图形验证==========================
		String path=request.getServletContext().getRealPath("/vecImageCode");
        System.setProperty("java.awt.headless", "true");
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
		WebElement captchaImg = form.findElement(By.id("imgCaptcha"));
	      File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
	      BufferedImage  fullImg = ImageIO.read(screenshot);
	      Point point = captchaImg.getLocation();
	      int eleWidth = captchaImg.getSize().getWidth();
	      int eleHeight = captchaImg.getSize().getHeight();
	      BufferedImage eleScreenshot= fullImg.getSubimage(point.getX(), point.getY(),
	          eleWidth, eleHeight);
	      ImageIO.write(eleScreenshot, "png", screenshot);
	      Date date=new Date();
	      SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddhhmmss");
	      String filename=System.currentTimeMillis()+".png";
	      File screenshotLocation = new File("C:\\images\\"+filename);
	      Thread.sleep(2000);
	      FileUtils.copyFile(screenshot, screenshotLocation);
	      Map<String,Object> map1=MyCYDMDemo.Imagev("C:\\images\\"+filename);//图片验证，打码平台
	      System.out.println(map1);
	      String catph= (String) map1.get("strResult");
	      Thread.sleep(2000);
	            //
	        WebElement loginBtn = driver.findElement(By.id("txtCaptcha"));
	          loginBtn.sendKeys(catph);
	        Thread.sleep(2000);
	          WebElement loginBtns = driver.findElement(By.id("loginbtn"));
	        loginBtns.click();
	          Thread.sleep(2000);
		
		
		
		if(driver.getPageSource().contains("详单查询")){
			map.put("errorCode", "0000");
			map.put("errorInfo", "登陆成功");
			}else{
				String divErr = driver.findElement(By.id("divErr")).getText();
				
				map.put("errorCode", "0007");
				map.put("errorInfo", divErr);
			}
	 
		} catch (Exception e) {
			e.printStackTrace();
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络连接异常");
		}		
	
		    request.getSession().setAttribute("driver", driver);//把driver放到session 
		// driver.close();
	return map;	 
	}
	 /**
	  * 获取验证码
	  * @param request
	  * @return
	  */
	 public  Map<String,Object> ningXiaGetcode(HttpServletRequest request,String phoneNumber){
		  
		 WebDriver driver =  (WebDriver) request.getSession().getAttribute("driver");//从session中获得driver
		 
		 Map<String, Object> map = new HashMap<String, Object>();
		 if(driver==null){
	    	 map.put("errorCode", "0001");
			 map.put("errorInfo", "请先登录!");
			return map;
	     }
		
		 driver.get("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000501");	
		 try {
			 Thread.sleep(3000);
		 	 driver.get("http://nx.189.cn/jt/bill/xd/?fastcode=10000501&cityCode=nx");	
			 Thread.sleep(3000);
			 
			 driver.findElement(By.id("hqyzm")).click();//获取验证码
			 Thread.sleep(500);
			 
			 String sendInfo =driver.findElement(By.xpath("//*[@id='myAlert3']/div[2]/div[1]")).getText();
			 if(sendInfo.contains(phoneNumber)){
				map.put("errorCode", "0000");
				map.put("errorInfo", "验证码发送成功");
			  driver.findElement(By.id("btn_xieyi")).click();
			  Thread.sleep(500);
			 }else{
				 map.put("errorCode", "0001");
				 map.put("errorInfo", "验证码发送失败"); 
			 }
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 request.getSession().setAttribute("driver1", driver);//把driver放到session
		return map;
		 
	 }
	 /**
	  * 获取详单
	  * @param request
	  * @param phoneNumber
	  * @param servePwd
	  * @param code
	  * @return
	  */
	 
	 public Map<String,Object> ningXiaDetial(HttpServletRequest request,String phoneNumber,String servePwd,String code){
		 
		 WebDriver driver =  (WebDriver) request.getSession().getAttribute("driver1");//从session中获得driver
		 Map<String, Object> map = new HashMap<String, Object>();
		 if(driver==null){
			 map.put("errorCode", "0001");
		     map.put("errorInfo", "请先获取验证码");	
			 return map; 
		 }
		 
		 List<Map<String, Object>> dataList=new ArrayList<Map<String, Object>>();
           if(code==null||code.equals("")){
        	   map.put("errorCode", "0001");
 			   map.put("errorInfo", "验证码不能为空");
 			  return map;
              }
		  driver.findElement(By.id("yzm")).sendKeys(code);
		  String tipInfo =driver.findElement(By.xpath("//*[@id='myAlert3']/div[2]/div[1]")).getText();
		  if(tipInfo.contains("验证失败")){
			  map.put("errorCode", "0001");
			  map.put("errorInfo", "验证码错误");
			  return map;
		  }
		
		//==================获取cookie==========================================  
		 Set<Cookie> cookie=driver.manage().getCookies();
		 StringBuffer cookies=new StringBuffer();
		 for (Cookie c : cookie) {   
			 cookies.append(c.toString()+";");
		 } 
		 
		  HttpClient httpClient = new HttpClient(); 
		       String loginUrl =
				 "http://nx.189.cn/bfapp/buffalo/CtQryService";
				 PostMethod post = new PostMethod(loginUrl);
				 post.setRequestHeader("Accept","*/*");
				 post.setRequestHeader("Accept-Encoding","gzip, deflate");
				 post.setRequestHeader("Accept-Language","zh-CN,zh;q=0.8");
				 post.setRequestHeader("Connection","keep-alive");
				 post.setRequestHeader("Content-Length","122");
				 post.setRequestHeader("Content-Type","text/plain;charset=UTF-8");
				 post.setRequestHeader("Host","nx.189.cn");
				 post.setRequestHeader("Origin","http://nx.189.cn");
				 post.setRequestHeader("Referer","http://nx.189.cn/jt/bill/xd/?fastcode=20000776&cityCode=nx");
				 post.setRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36");
				 post.setRequestHeader("X-Buffalo-Version","2.0");
				 post.setRequestHeader("Cookie",cookies.toString());
				 Date date=new Date();//获取六个月的数据
				 SimpleDateFormat sdfNow=new SimpleDateFormat( "yyyyMMdd" );
				 String nowDate=sdfNow.format(date);
				 int year=new Integer(nowDate.substring(0, 4));
				 int month=new Integer(nowDate.substring(4,
				 6));//获取月  作为获取每个月的最后一天的参数
				
				 int nowYear=year;
				 int nowMonth=month;
				 String start="";//开始时间
				 String end="";//结束时间
				 String str="";//post的参数
				//==============取数据=================
				 for(int i=0;i<6;i++){//六个月
					 Map<String, Object> dmap = new HashMap<String, Object>();  
					 if(i==0){
					 start=GetMonth.firstDate(year,month);
					 end=nowDate;
					 }else {
					 start=GetMonth.firstDate(year,month);
					 end=GetMonth.lastDate(year,month);
					 }
					// System.out.println(start+"***********"+end);
					 str = "<buffalo-call>"+"\n"
					 +"<method>qry_sj_yuyinfeiqingdan</method>"+"\n"
					 +"<string>"+start+"</string>"+"\n"
					 +"<string>"+end+"</string>"+"\n"
					 +"</buffalo-call>"+"\n";
					
					try {
						 RequestEntity entity = new StringRequestEntity(str,
						 "text/html", "utf-8");
						post.setRequestEntity(entity);
						 httpClient.executeMethod(post);
						 Thread.sleep(5000);
						 String html = post.getResponseBodyAsString();
						 //System.out.println(html);
						 if(html.contains("糟糕...出错了")){
						 map.put("errorCode", "0001");
						 map.put("errorInfo", "糟糕...出错了");
						 return map;
						 }else if(html.contains("二次短信验证失败")){
					     map.put("errorCode", "0001");
						 map.put("errorInfo", "验证码错误"); 
						 return map;
						 }else{
						 nowYear=new Integer(GetMonth.beforMon(year,month,1).substring(0,
						 4));//上个月包括年和月 获取年
						 nowMonth=new
						 Integer(GetMonth.beforMon(year,month,1).substring(4));//获取上个月
						 year=nowYear;
						 month=nowMonth;
				//===============================数据=======================		 
						 dmap.put("item",html);
						 dataList.add(dmap);
						 }
						Thread.sleep(500);					 
				} catch (Exception e) {
					map.put("errorCode", "0001");
					map.put("errorInfo", "网络连接异常");
				} 
				}
				   map.put("data", dataList);
		           map.put("UserPassword",servePwd );
		           map.put("UserIphone", phoneNumber);
		           map.put("flag", "13");
		           map.put("errorCode", "0000");
		           map.put("errorInfo", "查询成功");
		           Resttemplate resttemplate = new Resttemplate();
		           map = resttemplate.SendMessage(map, application.getSendip()+"/HSDC/message/telecomCallRecord"); 
		           try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		           driver.close();	 
		return map;
	 }

}
