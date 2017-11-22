package com.reptile.service.ChinaTelecom;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reptile.util.GetMonth;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.PushSocket;
import com.reptile.util.Resttemplate;
import com.reptile.util.RobotUntil;
import com.reptile.util.application;
import com.reptile.util.talkFrame;

@Service
public class NingXiaTelecomService {
	private Logger logger= LoggerFactory.getLogger(NingXiaTelecomService.class);
	@Autowired
	private application application;
	/**
	 * 登陆
	 * @param request
	 * @param phoneNumber
	 * @param servePwd
	 * @return
	 * @throws IOException 
	 */

	 public Map<String, Object> ningXiaLogin(HttpServletRequest request, String phoneNumber, String servePwd) {
		 
			Map<String, Object> map = new HashMap<String, Object>();
			System.setProperty("webdriver.chrome.driver",
					"D:\\ie\\chromedriver.exe");
			//C:\\Program Files\\iedriver\\chromedriver.exe  正式上用这个
			ChromeOptions options = new ChromeOptions();
	        options.addArguments("start-maximized");
			WebDriver driver = new ChromeDriver(options);
			driver.get("http://login.189.cn/web/login");	
			driver.navigate().refresh();
			try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.presenceOfElementLocated(By.id("loginForm")));
			WebElement form = driver.findElement(By.id("loginForm"));

			WebElement account = form.findElement(By.id("txtAccount"));
			// account.clear();
			account.sendKeys(phoneNumber);
			new WebDriverWait(driver, 15).until(ExpectedConditions.presenceOfElementLocated(By.id("txtShowPwd")));
			//Thread.sleep(500);
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
		      Point point = captchaImg.getLocation();//坐标
		      int eleWidth = captchaImg.getSize().getWidth();//宽
		      int eleHeight = captchaImg.getSize().getHeight();//高
		      BufferedImage eleScreenshot= fullImg.getSubimage(point.getX(), point.getY(),
		          eleWidth, eleHeight);
		      ImageIO.write(eleScreenshot, "png", screenshot);
		     /* Date date=new Date();
		      SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddhhmmss");*/
		      String filename=System.currentTimeMillis()+".png";
		      File screenshotLocation = new File("C:\\images\\"+filename);
		      Thread.sleep(2000);
		      FileUtils.copyFile(screenshot, screenshotLocation);
		      
		      Map<String,Object> map1=MyCYDMDemo.Imagev("C:\\images\\"+filename);//图片验证，打码平台
		      System.out.println(map1);
		      String catph= (String) map1.get("strResult");
		      new WebDriverWait(driver, 15).until(ExpectedConditions.presenceOfElementLocated(By.id("txtCaptcha")));
		      //================================
		        WebElement loginBtn = driver.findElement(By.id("txtCaptcha"));
		          loginBtn.sendKeys(catph);
		          new WebDriverWait(driver, 15).until(ExpectedConditions.presenceOfElementLocated(By.id("loginbtn")));
		          WebElement loginBtns = driver.findElement(By.id("loginbtn"));
		          loginBtns.click();
		          //Thread.sleep(2000);
		          driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			if(driver.getPageSource().contains("详单查询")){
				logger.warn("宁夏电信，登陆成功");
				map.put("errorCode", "0000");
				map.put("errorInfo", "登陆成功");
				}else{
					//new WebDriverWait(driver, 15).until(ExpectedConditions.presenceOfElementLocated(By.id("loginbtn")));
					String divErr = driver.findElement(By.id("divErr")).getText();
					logger.warn("宁夏电信",divErr);
					map.put("errorCode", "0001");
					map.put("errorInfo", divErr);
					driver.close();
				}
		 
			} catch (Exception e) {
				logger.warn("宁夏电信",e);
				//e.printStackTrace();
				map.put("errorCode", "0001");
				map.put("errorInfo", "网络连接异常");
				
				driver.close();
				try {
					Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}	
		    request.getSession().setAttribute("driver", driver);//把driver放到session 
		// driver.close();
	return map;	 
	}
	 /**
	  * 获取验证码
	  * @param request
	  * @return
	 * @throws IOException 
	  */
	 public  Map<String,Object> ningXiaGetcode(HttpServletRequest request,String phoneNumber){
		  
		 WebDriver driver =  (WebDriver) request.getSession().getAttribute("driver");//从session中获得driver
		 
		 Map<String, Object> map = new HashMap<String, Object>();
		 if(driver==null){
			 logger.warn("宁夏电信未登录");
	    	 map.put("errorCode", "0001");
			 map.put("errorInfo", "请先登录!");
			return map;
	     }
		
		 driver.get("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000501");	
		 try {
			 driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			 //Thread.sleep(3000);
		 	 driver.get("http://nx.189.cn/jt/bill/xd/?fastcode=10000501&cityCode=nx");	
			 //Thread.sleep(3000);
		 	new WebDriverWait(driver, 15).until(ExpectedConditions.presenceOfElementLocated(By.id("hqyzm")));
			 driver.findElement(By.id("hqyzm")).click();//获取验证码
			// Thread.sleep(500);
			 new WebDriverWait(driver, 15).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='myAlert3']/div[2]/div[1]"))); 
			 String sendInfo =driver.findElement(By.xpath("//*[@id='myAlert3']/div[2]/div[1]")).getText();
			 if(sendInfo.contains(phoneNumber)){
				 logger.warn("宁夏电信第二次发送验证码成功");
				map.put("errorCode", "0000");
				map.put("errorInfo", "验证码发送成功");
			  driver.findElement(By.id("btn_xieyi")).click();
			  Thread.sleep(500);
			 }else{
				 logger.warn("宁夏电信第二次发送验证码失败");
				 map.put("errorCode", "0001");
				 map.put("errorInfo", "验证码发送失败"); 
			 }
		} catch (InterruptedException e) {
			logger.warn("宁夏电信网络异常，请稍后");
			 map.put("errorCode", "0001");
			 map.put("errorInfo", "网络异常，请稍后"); 
			e.printStackTrace();
//			try {
//				Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
//			} catch (IOException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			}
			//driver.close();
			try {
				Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
	 
	 public Map<String,Object> ningXiaDetial(HttpServletRequest request,String phoneNumber,String servePwd,String code,String longitude,String latitude,String UUID){
		 
		 WebDriver driver =  (WebDriver) request.getSession().getAttribute("driver1");//从session中获得driver
		 Map<String, Object> map = new HashMap<String, Object>();
		 if(driver==null){
			 logger.warn("宁夏电信请先获取验证码");
			 map.put("errorCode", "0001");
		     map.put("errorInfo", "请先获取验证码");	
			 return map; 
		 }
		 List<Map<String, Object>> dataList=new ArrayList<Map<String, Object>>();
           if(code==null||code.equals("")){
        	   logger.warn("宁夏电信验证码不能为空");
        	   map.put("errorCode", "0001");
 			   map.put("errorInfo", "验证码不能为空");
 			  return map;
              }
		  driver.findElement(By.id("yzm")).sendKeys(code);
		  String tipInfo =driver.findElement(By.xpath("//*[@id='myAlert3']/div[2]/div[1]")).getText();
		  if(tipInfo.contains("验证失败")){
			  logger.warn("宁夏电信验证码错误");
			//---------------推-------------------
			  PushSocket.push(map, UUID, "0001");
					//---------------推-------------------
			  map.put("errorCode", "0001");
			  map.put("errorInfo", "验证码错误");
			  return map;
		  }
		
		  logger.warn("宁夏电信数据获取中...");
		//---------------推-------------------
		  PushSocket.push(map, UUID, "0000");
		//---------------推-------------------
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
					 logger.warn("宁夏电信",e);
					map.put("errorCode", "0001");
					map.put("errorInfo", "网络连接异常");
				} 
				}
				   map.put("data", dataList);
		           map.put("UserPassword",servePwd );
		           map.put("UserIphone", phoneNumber);
		           map.put("longitude", longitude);
					map.put("latitude", latitude);
		           map.put("flag", "13");
		           map.put("errorCode", "0000");
		           map.put("errorInfo", "查询成功");
		           logger.warn("宁夏电信数据查询成功");
		           Resttemplate resttemplate = new Resttemplate();
		           map = resttemplate.SendMessage(map, application.getSendip()+"/HSDC/message/telecomCallRecord"); 
		           try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.warn("宁夏电信",e);
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}finally{
					 driver.close();	
					 try {
						Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
					} catch (IOException e) {
						logger.warn("宁夏电信",e);
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
		           
		return map;
	 }

}
