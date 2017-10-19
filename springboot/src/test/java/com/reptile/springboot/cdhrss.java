package com.reptile.springboot;

import com.reptile.util.ConstantInterface;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.Resttemplate;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class cdhrss {
	 private final static String Cdhrsslogin="http://jypt.cdhrss.gov.cn:8048/portal.php?id=1";//发送短信密码
				//成都社保
				public static void main(String[] args) throws Exception  {
					//---------------------------------------------------------------------
					String User="019213070";
					String pass="19870127";
					System.setProperty("webdriver.ie.driver", "F:\\ie\\IEDriverServer.exe");
					WebDriver driver = new InternetExplorerDriver();
					driver.get(Cdhrsslogin);
					driver.navigate().refresh();
					Thread.sleep(3000);
					WebElement loginform=   driver.findElement(By.id("login_form2"));
					List<WebElement> buttons =loginform.findElements(By.tagName("Input"));
					buttons.get(2).sendKeys(User);
					buttons.get(3).sendKeys(pass);
					//获取图片
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
					String filename=System.currentTimeMillis()+".png";
					File screenshotLocation = new File("C:\\images\\"+filename);
					Thread.sleep(5000);
					FileUtils.copyFile(screenshot, screenshotLocation);
					Map<String,Object> map1=MyCYDMDemo.Imagev("C:\\images\\"+filename);//图片验证，打码平台
					System.out.println(map1);
					String catph= (String) map1.get("strResult");
					buttons.get(4).sendKeys(catph);
					loginform.findElement(By.className("btn")).click();
					Thread.sleep(4000);
					Map<String,Object> data=new HashMap<String,Object>();
					Map<String,Object> seo=new HashMap<String, Object>();
					for (int i = 3; i < 9; i++) {
					    driver.get("http://insurance.cdhrss.gov.cn/QueryInsuranceInfo.do?flag="+i);
					    String a= driver.getPageSource();
					    System.out.println(a);
						data.put("html"+i, a);
					    Thread.sleep(5000);
					}
					seo.put("city","成都");
					seo.put("userId","11111111111111111");
//					seo.put("idcard",UserCard);
					seo.put("data", data);
//					seo.put("backtype","CEB" );
					System.out.println(seo);
				    Map<String, Object> map=new HashMap<String, Object>();
				    Resttemplate resttemplate = new Resttemplate();

                 ///http://192.168.3.16:8089/HSDC/person/socialSecurity
					map=resttemplate.SendMessage(seo, ConstantInterface.port+"/HSDC/person/socialSecurity");
					System.out.println(map+"--------------");
			}

		}
