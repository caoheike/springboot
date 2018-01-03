package com.reptile.service.chinatelecom;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.*;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @author liubin
 *
 */
@Service
public class HebeiProvinceService {
	@Autowired
	private application applications;

	/**
	 * 
	 * @param 需要手机号，查询密码，手机号实名认证的姓名，手机号绑定
	 *            的身份证号
	 * @param session
	 *            中存当前浏览器，通过手机号区分session
	 * @return 返回推送状态
	 */
	public Map<String, Object> hebeiUsercard1(HttpServletRequest request, String userNum, String userPass,
			String userName, String userCode) {
		Map<String, Object> map = new HashMap<String, Object>(200);
		System.setProperty(ConstantInterface.ieDriverKey, ConstantInterface.ieDriverValue);
		WebDriver driver = new InternetExplorerDriver();
		driver.get("http://login.189.cn/web/login");
		driver.navigate().refresh();
		try {
			Thread.sleep(3000);
			WebElement form = driver.findElement(By.id("loginForm"));
			Thread.sleep(500);
			WebElement account = form.findElement(By.id("txtAccount"));
			// 手机号
			account.sendKeys(userNum);
			Thread.sleep(500);
			WebElement passWord = form.findElement(By.id("txtShowPwd"));
			passWord.click();
			WebElement passWord1 = form.findElement(By.id("txtPassword"));
			// 查询密码
			passWord1.sendKeys(userPass);
			// 河北新增加的图片验证码
			WebElement f = driver.findElement(By.id("loginForm"));
			WebElement captchaImg = f.findElement(By.id("imgCaptcha"));
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage fullImg = ImageIO.read(screenshot);
			Point point = captchaImg.getLocation();
			int eleWidth = captchaImg.getSize().getWidth();
			int eleHeight = captchaImg.getSize().getHeight();
			BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
			ImageIO.write(eleScreenshot, "png", screenshot);
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			String filename = System.currentTimeMillis() + ".png";
			File screenshotLocation = new File("C:\\images\\" + filename);
			Thread.sleep(2000);
			FileUtils.copyFile(screenshot, screenshotLocation);
			// 图片验证，打码平台
			Map<String, Object> map1 = MyCYDMDemo.Imagev("C:\\images\\" + filename);
			System.out.println(map1);
			String catph = (String) map1.get("strResult");
			Thread.sleep(2000);
			WebElement tup = driver.findElement(By.id("txtCaptcha"));
			tup.sendKeys(catph);
			WebElement loginBtn = driver.findElement(By.id("loginbtn"));
			loginBtn.click();
			Thread.sleep(5000);
			// 详单
			driver.get("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=00380407");
			Thread.sleep(3000);
			// 详单iframe
			driver.get("http://he.189.cn/service/bill/feeQuery_iframe.jsp?SERV_NO=SHQD1&fastcode=00380407&cityCode=he");
			Thread.sleep(3000);
			// 姓名
			driver.findElement(By.id("CustName")).sendKeys(userName);
			Thread.sleep(2000);
			// 身份证号
			driver.findElement(By.id("IdentityCode")).sendKeys(userCode);
			Thread.sleep(2000);
			driver.findElement(ByClassName.className("pub_btn_s")).click();
			Thread.sleep(1000);
			WebElement a = driver.findElement(By.id("MotoText"));
			// 短信验证码发送成功
			a.findElement(By.tagName("a")).click();
			// 获得session
			HttpSession session = request.getSession();
			// session中存浏览器
			session.setAttribute("sessionDriver-hebei" + userNum, driver);
			map.put("errorCode", "0000");
			map.put("errorInfo", "验证码发送成功!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("errorCode", "0001");
			map.put("errorInfo", "服务异常！请重新尝试发送验证码");
			driver.close();
			try {
				Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return map;

	}

	public Map<String, Object> hebeiUsercard2(HttpServletRequest request, String userNum, String userPass, String capth,
			String longitude, String latitude) {
		Map<String, Object> map = new HashMap<String, Object>(200);
		PushState.state(userNum, "callLog", 100);
		System.out.println("进入短信提交页面");
		// 获得session
		HttpSession session = request.getSession();
		Object sessiondriver = session.getAttribute("sessionDriver-hebei" + userNum);
		if (sessiondriver == null) {

			map.put("errorCode", "0001");
			map.put("errorInfo", "操作异常!");
			return map;
		} else {
			final WebDriver driver = (WebDriver) sessiondriver;
			WebElement funcid = driver.findElement(By.id("_FUNC_ID_"));
			String a = funcid.getAttribute("value");
			WebElement qryList = driver.findElement(By.id("qryList"));
			WebElement citycode = qryList.findElement(By.name("CITY_CODE"));
			String cityid = citycode.getAttribute("value");
			WebClient webClient = new WebClient();
			List<NameValuePair> list4 = new ArrayList<NameValuePair>();
			WebRequest requests4;
			driver.findElement(By.id("MOBILE_CODE")).sendKeys(capth);
			Map<String, Object> hebei = new HashMap<String, Object>(200);
			try {
				Thread.sleep(3000);
				List<WebElement> as = driver.findElements(By.className("pub_btn_s"));
				as.get(1).click();
				Thread.sleep(5000);

				PushState.state(userNum, "callLog", 100);
				List<Map<String, Object>> datalist = new ArrayList<Map<String, Object>>();
				System.out.println("开始获取详单");
				int num = 6;
				for (int i = 0; i < num; i++) {
					Map<String, Object> map1 = new HashMap<String, Object>(200);
					Thread.sleep(3000);
					Select userSelect = new Select(driver.findElement(By.id("ACCT_DATE")));
					userSelect.selectByIndex(i);
					Thread.sleep(3000);
					as.get(2).click();
					Thread.sleep(3000);
					driver.switchTo().frame("iFrmMain");
					map1.put("items", driver.getPageSource());
					Thread.sleep(3000);
					datalist.add(map1);
					driver.switchTo().parentFrame();
					System.out.println(i + "--通话详单" + i + "----");
				}
				hebei.put("data", datalist);
				hebei.put("UserIphone", userNum);
				map.put("longitude", longitude);
				map.put("latitude", latitude);
				hebei.put("flag", 14);
				hebei.put("userPassword", userPass);
				Resttemplate resttemplate = new Resttemplate();
				map = resttemplate.SendMessage(hebei, applications.getSendip() + "/HSDC/message/telecomCallRecord");
				String errorCode = "errorCode";
				String state0 = "0000";
				if (map != null && state0.equals(map.get(errorCode).toString())) {
					PushState.state(userNum, "callLog", 300);
					map.put("errorInfo", "查询成功");
					map.put("errorCode", "0000");
					driver.close();
					Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
				} else {
					// --------------------数据中心推送状态----------------------
					PushState.state(userNum, "callLog", 200);
					// ---------------------数据中心推送状态----------------------
					driver.close();
					Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				map.clear();
				map.put("errorInfo", "服务繁忙，请稍后再试");
				map.put("errorCode", "0002");
				driver.close();
				try {
					Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	public Map<String, Object> xingdan(HttpServletRequest request, int i) {
		Map<String, Object> map = new HashMap<String, Object>(200);
		// 获得session
		HttpSession session = request.getSession();
		Object sessiondriver = session.getAttribute("sessionDriver-hebei" + "18033850229" + i);
		final WebDriver driver = (WebDriver) sessiondriver;
		Select userSelect = new Select(driver.findElement(By.id("ACCT_DATE")));
		userSelect.selectByIndex(i);
		try {
			List<WebElement> as = driver.findElements(By.className("pub_btn_s"));
			Thread.sleep(5000);
			as.get(2).click();
			Thread.sleep(5000);
			driver.switchTo().frame("iFrmMain");
			System.out.println(driver.getPageSource());
			map.put("items", driver.getPageSource());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return map;

	}
}
