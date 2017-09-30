package com.reptile.springboot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.reptile.util.MyCYDMDemo;

public class lzgjj1 {
	 private final static String loginPage="http://cx.lzgjj.com/pernetface/login.jsp#per";//登录
	 private final static String ImagePage="http://cx.lzgjj.com/pernetface/image.jsp";//图片
	public static void main(String[] args) throws Exception {
		String usernum="620122199303012034";//身份证号
    	String userPass="123456";//身份证号
    	System.setProperty("webdriver.ie.driver", "F:\\ie\\IEDriverServer.exe");
		WebDriver driver = new InternetExplorerDriver();
		driver.get(loginPage);
	//	code pull-right
		Thread.sleep(3000);
		WebElement Type = driver.findElement(By.id("aType"));
		Type.sendKeys("4");
		WebElement j_username = driver.findElement(By.id("j_username"));
		j_username.sendKeys(usernum);
		WebElement j_password = driver.findElement(By.id("j_password"));
		j_password.sendKeys(userPass);
//----------------------------------------------------------------------------------
		WebElement captchaImg=	driver.findElement(By.id("codeimg"));
		
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
		File screenshotLocation = new File("H:\\lzgjj\\"+filename);
		Thread.sleep(5000);
		FileUtils.copyFile(screenshot, screenshotLocation);
		Map<String,Object> map=MyCYDMDemo.Imagev("H:\\lzgjj\\"+filename);//图片验证，打码平台
		String catph=(String) map.get("strResult");
		
//-----------------------------------------------------------------------------------
		WebElement num = driver.findElement(By.id("checkCode"));
		num.sendKeys(catph);
		WebElement login = driver.findElement(By.id("btn-login"));
       login.click();    
       driver.get("http://cx.lzgjj.com/pernetface/per/queryPerAccDetails.do?menuid=259597");
       Thread.sleep(3000);
       String year= new SimpleDateFormat("yyyy").format(date);
       System.out.println(year+"--year--");
    //  WebElement years= driver.findElement(By.id("year"));
       JavascriptExecutor jse = (JavascriptExecutor)driver;
       String s1="document.getElementById('year').value="+year;
       jse.executeScript(s1);
       driver.findElement(By.className("icon-search")).click();
       WebElement account=  driver.findElement(By.id("dataList"));
       List<WebElement> list = (List<WebElement>) account.findElements(By.tagName("div"));
       for (int i = 0; i < list.size(); i++) {
		System.out.println(list.get(i).getText()+"--------------");
	}

	}
}
