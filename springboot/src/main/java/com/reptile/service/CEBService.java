package com.reptile.service;

import com.reptile.util.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.os.WindowsUtils;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CEBService {
	 @Autowired
	  private application applications;
	  private Logger logger= LoggerFactory.getLogger(CEBService.class);
	  private final static String CabCardIndexpage="https://xyk.cebbank.com/mycard/bill/havingprintbill-query.htm";//光大银行信用卡个人中心
	  private final static String CabCardloginUrl="https://xyk.cebbank.com/mall/login";//光大银行信用卡登录页面地址
		public Map<String,Object> CEBlogin1(HttpServletRequest request,String Usercard,String UserName){
			 Map<String, Object> map=new HashMap<String, Object>();
			 System.setProperty(ConstantInterface.ieDriverKey, ConstantInterface.ieDriverValue);

			 WebDriver driver = new InternetExplorerDriver();
			try {
				driver.get(CabCardloginUrl);
				driver.navigate().refresh();
				 driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);//隐式等待
				//login
			
				WebElement loginform= driver.findElement(By.id("login"));
				
				loginform.findElement(By.id("userName")).sendKeys(UserName);//输入用户名及密码
					//获取图片验证码进行打码
				List<WebElement> Image =	loginform.findElements(By.tagName("img"));
				WebElement captchaImg=Image.get(0);
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
				String filename="CEB//"+System.currentTimeMillis()+".png";
				File screenshotLocation = new File("C://"+filename);
				FileUtils.copyFile(screenshot, screenshotLocation);
				System.out.println(screenshotLocation);
				MyCYDMDemo dem=new MyCYDMDemo();
				Map<String,Object> map1=dem.Imagev("C://"+filename);//图片验证，打码平台
				System.out.println(map1);
				String strResult= (String) map1.get("strResult");
//					拿到打码平台返回的值
//				String catph= dem.getcode(filename);
				loginform.findElement(By.id("yzmcode")).sendKeys(strResult);
					//这一步走完后，图片验证码也输入完毕了，开始点击发送验证码
				List<WebElement> button =	loginform.findElements(By.tagName("button"));
				button.get(0).click();
				Thread.sleep(1000);
				try{
					driver.findElement(ByClassName.className("popup-dialog-message"));
					System.out.println("报错！！！");
					logger.info("光大银行登录时输入有误"+Usercard);
					map.put("errorInfo","异常服务请重新尝试");
					map.put("errorCode","0002");
					System.out.println(map);
					driver.close();
					 Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
				}catch(Exception e){
					logger.warn("光大银行出错啦",e);
					HttpSession session=request.getSession();//获得session
					session.setAttribute("sessionDriver-Ceb"+Usercard, driver);
					Map<String,Object> data=new HashMap<String,Object>();
					data.put("driverName", "sessionDriver-Ceb"+Usercard);
					map.put("errorInfo","动态密码发送成功");
					map.put("errorCode","0000");
					map.put("data",data);
					System.out.println(map);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("光大银行登录时发送验证码失败"+Usercard);
				map.put("errorInfo","服务繁忙！请稍后再试");
				map.put("errorCode","0001");
				driver.close();
			}
			return map;
			
		}
		public Map<String,Object> CEBlogin2(HttpServletRequest request,String UserCard,String Password,String UUID,String timeCnt) throws ParseException{
			boolean isok = CountTime.getCountTime(timeCnt);
			
			 Map<String, Object> map=new HashMap<String, Object>();
			 PushSocket.pushnew(map, UUID, "1000","光大银行登录中");
			 Map<String,Object> data=new HashMap<String,Object>();
				HttpSession session=request.getSession();//获得session
				Object sessiondriver = session.getAttribute("sessionDriver-Ceb"+UserCard);//存在session 中的浏览器
				final WebDriver driver = (WebDriver) sessiondriver;
				if(sessiondriver==null){					
					PushSocket.push(map, UUID, "0001");
					if(isok==true) {
						PushState.state(UserCard, "bankBillFlow",200);
					}
					logger.warn("连接超时！请重新获取验证码"+UserCard);
					map.put("errorInfo","连接超时！请重新获取验证码");
					map.put("errorCode","0002");
					PushSocket.pushnew(map, UUID, "3000","连接超时！请重新获取验证码");
					driver.close();
					 try {
						Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return map;
				}
			try {
				WebElement loginform=	driver.findElement(By.id("login"));
				loginform.findElement(By.id("verification-code")).sendKeys(Password);
				loginform.findElement(ByClassName.className("login-style-bt")).click();
				try {
					driver.findElement(ByClassName.className("popup-dialog-message"));
					Thread.sleep(2000);
					PushSocket.push(map, UUID, "0001");
					if(isok==true) {
						PushState.state(UserCard, "bankBillFlow",200);
					}					logger.warn("光大银行登录时发送验证码输入有误"+UserCard);
					map.put("errorInfo","短信验证码输入有误");
					map.put("errorCode","0002");
					driver.close();
					 Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
					 PushSocket.pushnew(map, UUID, "3000","短信验证码输入有误");
				} catch (Exception e) {
					if(isok==true) {
						PushState.state(UserCard, "bankBillFlow",100);
					}
					
					// TODO: handle exception
					System.out.println("点击成功");
					driver.get(CabCardIndexpage);
					System.out.println("开始进入个人中心");
					PushSocket.pushnew(map, UUID, "2000","光大银行信用卡登录成功");
					WebElement table=  driver.findElement(ByClassName.className("tab_one"));
					PushSocket.push(map, UUID, "0000");
					List<WebElement> tr	=table.findElements(By.tagName("tr"));
					List<String> html =new ArrayList<String>();
					List<String> times=new ArrayList<String>();
					PushSocket.pushnew(map, UUID, "5000","光大银行信用卡信息获取中");
					for (int i = 1; i < tr.size(); i++) {
						WebElement tds=	 tr.get(i);
						List<WebElement> td=tds.findElements(By.tagName("td"));
						WebElement time=td.get(0);
						String month= time.getText().replace("/", "").trim();
						System.out.println(month);
						times.add(month);
					}
					for (int i = 0; i < times.size(); i++) {
						driver.get("https://xyk.cebbank.com/mycard/bill/billquerydetail.htm?statementDate="+times.get(i));
						String detailedpage= driver.getPageSource();
						html.add(detailedpage);
					}
					PushSocket.pushnew(map, UUID, "6000","光大银行信用卡信息获取成功");
					Map<String,Object> seo=new HashMap<String, Object>();
				  	System.out.println("页面已经放置到html中");
				  	data.put("html", html);
				  	data.put("backtype","CEB");
				  	data.put("idcard",UserCard);
				  	seo.put("data", data);
				  	System.out.println(seo);
				  	Resttemplate resttemplate = new Resttemplate();
					map=resttemplate.SendMessage(seo,applications.getSendip()+"/HSDC/BillFlow/BillFlowByreditCard");
					driver.close();
				    if(map!=null&&"0000".equals(map.get("errorCode").toString())){
				    	if(isok==true) {
				    		PushState.state(UserCard, "bankBillFlow",300);
				    	}
		                map.put("errorInfo","查询成功");
		                map.put("errorCode","0000");
		                PushSocket.pushnew(map, UUID, "8000","光大银行信用卡认证成功");
		                Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
		            }else{
		            	//--------------------数据中心推送状态----------------------
		            	if(isok==true) {
							PushState.state(UserCard, "bankBillFlow",200);
						}		            	//---------------------数据中心推送状态----------------------
		            	logger.warn("光大银行账单推送失败"+UserCard);
		            	PushSocket.pushnew(map, UUID, "9000","光大银行信用卡认证失败");
		            	 Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
		            }
				}
				
			} catch (Exception e) {
				driver.close();
				 try {
					Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//---------------------------数据中心推送状态----------------------------------
				 if(isok==true) {
						PushState.state(UserCard, "bankBillFlow",200);
				 }
				//---------------------------数据中心推送状态----------------------------------
				 e.printStackTrace();
				 map.clear();
				 logger.warn("光大银行账单推送失败"+UserCard);
				 map.put("errorInfo","获取账单失败");
				 map.put("errorCode","0002");
				 PushSocket.pushnew(map, UUID, "7000","光大银行信用卡信息获取失败");
			}
			
			return map;
		

		}
}
