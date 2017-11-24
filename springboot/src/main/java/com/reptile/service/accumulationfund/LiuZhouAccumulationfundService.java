package com.reptile.service.accumulationfund;

import com.reptile.util.ConstantInterface;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LiuZhouAccumulationfundService {
	
	 private Logger logger = LoggerFactory.getLogger(LiuZhouAccumulationfundService.class);
	 @Autowired
	 private application applications;
	 private PushState PushState;
	  
	 public Map<String,Object> loginImage(HttpServletRequest request){
		 	logger.warn("获取柳州公积金图片验证码");
			System.setProperty(ConstantInterface.ieDriverKey, ConstantInterface.ieDriverValue);
			WebDriver driver = new InternetExplorerDriver();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			driver.get("http://www.lzzfgjj.com/login.jspx");
			driver.navigate().refresh();
			WebElement loginform= driver.findElement(By.id("jvForm"));
			WebElement Image=	loginform.findElement(ByXPath.xpath("//*[@id='jvForm']/table[1]/tbody/tr[7]/td/img"));
			WebElement captchaImg=Image;
			 Map<String,Object>map=new HashMap<String,Object>();
			 HttpSession session = request.getSession();
			 Map<String,Object> data=new HashMap<String,Object>();
			BufferedImage fullImg;
			
			try {
				Point point = captchaImg.getLocation();
				int eleWidth = captchaImg.getSize().getWidth();
				int eleHeight = captchaImg.getSize().getHeight();
				File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
				fullImg = ImageIO.read(screenshot);
				BufferedImage eleScreenshot= fullImg.getSubimage(point.getX()-6, point.getY(),
						eleWidth, eleHeight);
				ImageIO.write(eleScreenshot, "png", screenshot);
				String filename="lz"+System.currentTimeMillis()+".png";
				File screenshotLocation =  new File(request.getSession().getServletContext().getRealPath("/upload") + "/"+filename);
				System.out.println(screenshot);
				System.out.println(screenshotLocation);
				FileUtils.copyFile(screenshot, screenshotLocation);
				session.setAttribute("sessionWebDriver-liuzhou", driver);
				session.setAttribute("htmlPage-liuzhouloginform", loginform);
				data.put("imagePath",request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/upload/" + filename);
				//data.put("imagePath", "http://192.168.3.38:8080/upload/"+filename);
	            map.put("errorCode", "0000");
	            map.put("errorInfo", "加载验证码成功");
	            map.put("driverName", "sessionWebDriver-liuzhou");
	            map.put("data", data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				 	logger.warn("柳州住房公积金 ", e);
		            map.put("errorCode", "0001");
		            map.put("errorInfo", "当前网络繁忙，请刷新后重试");
		            e.printStackTrace();
		            driver.close();
			}
			
		return map;
	 }
	 
	 public Map<String,Object> getDeatilMes(HttpServletRequest request,String idCard,String catpy,String fundCard,String passWord,String cityCode,String idCardNum ){
		 System.out.println("欢迎使用柳州公积金");
		 	Map<String, Object> map = new HashMap<>();
	        Map<String, Object> dataMap = new HashMap<>();
	        HttpSession session = request.getSession();
	        Object drivers = session.getAttribute("sessionWebDriver-liuzhou");
	        WebDriver driver =(WebDriver) drivers;
	        Object loginforms = session.getAttribute("htmlPage-liuzhouloginform");
	        WebElement loginform=(WebElement) loginforms;
	        if (drivers != null && loginforms != null) {
	        	try{
	        		loginform.findElement(By.id("username")).sendKeys(idCard);
					loginform.findElement(By.id("password")).sendKeys(passWord);
					loginform.findElement(By.id("captcha")).sendKeys(catpy);
					loginform.findElement(By.className("dengluanniu")).click();
					driver.get("http://www.lzzfgjj.com/grcx/grcx_grjbqk.jspx");
					WebElement table= driver.findElement(By.tagName("table"));
					System.out.println(table.getText()); //公积金基本信息
					if(table.getText().contains("还没有帐号？去注册")){
						map.put("errorInfo","您的输入有误，请正确输入");
		                map.put("errorCode","0002");
		                driver.close();
		                Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
		                return map;
					}
					String html1= driver.getPageSource();
					Map<String,Object> data=new HashMap<String,Object>();
					driver.get("http://www.lzzfgjj.com/grcx/grcx_grzmmx.jspx");
					WebElement table1= driver.findElement(By.xpath("/html/body/div/div[1]/div[5]/div[2]/div/div[3]/table[2]"));
					System.out.println(table1.getText());
					String html2= driver.getPageSource();
					Map<String,Object> lz=new HashMap<String, Object>();
					data.put("item",html2);
					data.put("base",html1);
					lz.put("city", cityCode);
					lz.put("userId", idCardNum);
					lz.put("data", data);	
					Resttemplate resttemplate = new Resttemplate();
					PushState.state(idCardNum, "accumulationFund",100);
					map=resttemplate.SendMessage(lz,ConstantInterface.port+"/HSDC/person/accumulationFund");
					  if(map!=null&&"0000".equals(map.get("errorCode").toString())){
					    	PushState.state(idCardNum, "accumulationFund",300);
			                map.put("errorInfo","查询成功");
			                map.put("errorCode","0000");
			                driver.close();
			                Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
			            }else{
			            	//--------------------数据中心推送状态----------------------
			            	PushState.state(idCardNum, "accumulationFund",200);
			            	//---------------------数据中心推送状态----------------------
			            	logger.warn("柳州公积金推送失败"+idCard);
			            	driver.close();
			            	Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
			            }
	        	}catch(Exception e){
	        			driver.close();
	        			try {
							Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
							logger.warn("柳州公积金登录失败",e);
			                 e.printStackTrace();
			                 map.put("errorCode", "0001");
			                 map.put("errorInfo", "登录失败，请正确输入");
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	        		 
	        	}
	        }else {
	        	
	            logger.warn("柳州住房公积金登录过程中出错session异常 ");
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "非法操作！");
	            driver.close();
	            try {
					Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } 
		return map;
		 
	 }
}
