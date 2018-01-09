package com.reptile.service.chinatelecom;

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
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reptile.util.GetMonth;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;
/**
 * NingXiaTelecomService
 * @ClassName: NingXiaTelecomService  
 * @Description: TODO  
 * @author: cuiyongjuan
 * @date 2017年11月26日  
 *
 */
@Service
public class NingXiaTelecomService {
	private Logger logger = LoggerFactory
			.getLogger(NingXiaTelecomService.class);
	@Autowired
	private application application;
	
	private static String detailStr = "详单查询";
	private static String viFail = "验证失败";
	private static String viCodeStr = "验证码";
	private static String success = "0000";
	private static String errorCode = "errorCode";

	/**
	 * 登陆
	 * 
	 * @param request
	 * @param phoneNumber
	 * @param servePwd
	 * @return
	 * @throws IOException
	 */
	public  Map<String, Object> ningXiaLogin(HttpServletRequest request,
			String phoneNumber, String servePwd) {

		Map<String, Object> map = new HashMap<String, Object>(16);
		System.setProperty("phantomjs.binary.path",
				"C:/phantomjs-2.1.1-windows/bin/phantomjs.exe");
		WebDriver driver = new PhantomJSDriver();
		driver.manage().window().maximize();
		try {
			driver.get("http://login.189.cn/web/login");
			new WebDriverWait(driver, 15).until(ExpectedConditions
					.presenceOfElementLocated(By.id("loginForm")));
			WebElement form = driver.findElement(By.id("loginForm"));

			WebElement account = form.findElement(By.id("txtAccount"));
			account.sendKeys(phoneNumber);
			new WebDriverWait(driver, 15).until(ExpectedConditions
					.presenceOfElementLocated(By.id("txtShowPwd")));
			WebElement passWord = form.findElement(By.id("txtShowPwd"));
			passWord.click();
			Thread.sleep(2000);
			WebElement passWord1 = form.findElement(By.id("txtPassword"));
			passWord1.sendKeys(servePwd);
			// ===========图形验证==========================
			String path = request.getServletContext().getRealPath(
					"/vecImageCode");
			System.setProperty("java.awt.headless", "true");
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			WebElement captchaImg = form.findElement(By.id("imgCaptcha"));
			File screenshot = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);
			BufferedImage fullImg = ImageIO.read(screenshot);
			// 坐标
			Point point = captchaImg.getLocation();
			// 宽
			int eleWidth = captchaImg.getSize().getWidth();
			// 高
			int eleHeight = captchaImg.getSize().getHeight();
			BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(),
					point.getY(), eleWidth, eleHeight);
			ImageIO.write(eleScreenshot, "png", screenshot);
			String filename = System.currentTimeMillis() + ".png";
			File screenshotLocation = new File("C:\\images\\" + filename);
			Thread.sleep(2000);
			FileUtils.copyFile(screenshot, screenshotLocation);
			// 图片验证，打码平台
			Map<String, Object> map1 = MyCYDMDemo.Imagev("C:\\images\\"
					+ filename);
			String catph = (String) map1.get("strResult");
			new WebDriverWait(driver, 15).until(ExpectedConditions
					.presenceOfElementLocated(By.id("txtCaptcha")));
			// ================================
			WebElement loginBtn = driver.findElement(By.id("txtCaptcha"));
			loginBtn.sendKeys(catph);
			new WebDriverWait(driver, 15).until(ExpectedConditions
					.presenceOfElementLocated(By.id("loginbtn")));
			WebElement loginBtns = driver.findElement(By.id("loginbtn"));
			loginBtns.click();
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			if (driver.getPageSource().contains(detailStr)) {
				logger.warn("宁夏电信，登陆成功");
				map.put("errorCode", "0000");
				map.put("errorInfo", "登陆成功");
			} else {
				String divErr = driver.findElement(By.id("divErr")).getText();
				logger.warn("宁夏电信", divErr);
				map.put("errorCode", "0001");
				if (divErr.contains(viCodeStr)) {
					map.put("errorInfo", "网络繁忙，请稍后再试");
				} else {
					map.put("errorInfo", divErr);
				}
			}
		} catch (Exception e) {
			logger.error("---------宁夏电信登录失败-----------", e);
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络连接异常");
			driver.quit();
		}
		// 把driver放到session
		request.getSession().setAttribute("driver", driver);
		return map;
	}

	/**
	 * 获取验证码
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public Map<String, Object> ningXiaGetcode(HttpServletRequest request,
			String phoneNumber) {
		// 从session中获得driver
		WebDriver driver = (WebDriver) request.getSession().getAttribute(
				"driver");

		Map<String, Object> map = new HashMap<String, Object>(16);
		if (driver == null) {
			logger.warn("宁夏电信未登录");
			map.put("errorCode", "0001");
			map.put("errorInfo", "请先登录!");
			return map;
		}
		try {
			driver.get("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000501");
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			driver.get("http://nx.189.cn/jt/bill/xd/?fastcode=10000501&cityCode=nx");
			new WebDriverWait(driver, 15).until(ExpectedConditions
					.presenceOfElementLocated(By.id("hqyzm")));
			// 获取验证码
			driver.findElement(By.id("hqyzm")).click();
			Thread.sleep(1000);
			String sendInfo = driver.findElement(
					By.xpath("//*[@id='myAlert3']/div[2]/div[1]")).getText();
			if (sendInfo.contains(phoneNumber)) {
				logger.warn("宁夏电信第二次发送验证码成功");
				map.put("errorCode", "0000");
				map.put("errorInfo", "验证码发送成功");
				driver.findElement(By.id("btn_xieyi")).click();
				Thread.sleep(500);
			} else {
				logger.warn("宁夏电信第二次发送验证码失败");
				map.put("errorCode", "0001");
				map.put("errorInfo", "验证码发送失败");
			}
		} catch (Exception e) {
			logger.error("---------宁夏电信网络异常-------",e);
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络异常，请稍后");
			driver.quit();
		}
		// 把driver放到session
		request.getSession().setAttribute("driver1", driver);
		return map;

	}

	/**
	 * 获取详单
	 * 
	 * @param request
	 * @param phoneNumber
	 * @param servePwd
	 * @param code
	 * @return
	 */

	public Map<String, Object> ningXiaDetial(HttpServletRequest request,
			String phoneNumber, String servePwd, String code, String longitude,
			String latitude, String uuid) {
		// 从session中获得driver
		WebDriver driver = (WebDriver) request.getSession().getAttribute(
				"driver1");
		Map<String, Object> map = new HashMap<String, Object>(16);
		PushSocket.pushnew(map, uuid, "1000", "登录中");
		PushState.state(phoneNumber, "callLog", 100);
		if (driver == null) {
			logger.warn("宁夏电信请先获取验证码");
			map.put("errorCode", "0001");
			map.put("errorInfo", "请先获取验证码");
			PushState.state(phoneNumber, "callLog", 200, "请先获取验证码");
			PushSocket.pushnew(map, uuid, "3000", "请先获取验证码");
			return map;
		}
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		if (code == null || code.equals("")) {
			logger.warn("宁夏电信验证码不能为空");
			map.put("errorCode", "0001");
			map.put("errorInfo", "验证码不能为空");
			PushState.state(phoneNumber, "callLog", 200, "验证码不能为空");
			PushSocket.pushnew(map, uuid, "3000", "验证码不能为空");
			return map;
		}
		driver.findElement(By.id("yzm")).sendKeys(code);
		String tipInfo = driver.findElement(
				By.xpath("//*[@id='myAlert3']/div[2]/div[1]")).getText();
		if (tipInfo.contains(viFail)) {
			logger.warn("宁夏电信验证码错误");
			map.put("errorCode", "0001");
			map.put("errorInfo", "验证码错误");
			PushState.state(phoneNumber, "callLog", 200, "验证码错误");
			PushSocket.pushnew(map, uuid, "3000", "验证码错误");
			return map;
		}
		PushSocket.pushnew(map, uuid, "2000", "登录成功");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PushSocket.pushnew(map, uuid, "5000", "数据获取中");

		logger.warn("宁夏电信数据获取中...");
		// ==================获取cookie==========================================
		Set<Cookie> cookie = driver.manage().getCookies();
		StringBuffer cookies = new StringBuffer();
		for (Cookie c : cookie) {
			cookies.append(c.toString() + ";");
		}

		HttpClient httpClient = new HttpClient();
		String loginUrl = "http://nx.189.cn/bfapp/buffalo/CtQryService";
		PostMethod post = new PostMethod(loginUrl);
		post.setRequestHeader("Accept", "*/*");
		post.setRequestHeader("Accept-Encoding", "gzip, deflate");
		post.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
		post.setRequestHeader("Connection", "keep-alive");
		post.setRequestHeader("Content-Length", "122");
		post.setRequestHeader("Content-Type", "text/plain;charset=UTF-8");
		post.setRequestHeader("Host", "nx.189.cn");
		post.setRequestHeader("Origin", "http://nx.189.cn");
		post.setRequestHeader("Referer",
				"http://nx.189.cn/jt/bill/xd/?fastcode=20000776&cityCode=nx");
		post.setRequestHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36");
		post.setRequestHeader("X-Buffalo-Version", "2.0");
		post.setRequestHeader("Cookie", cookies.toString());
		// 获取六个月的数据
		Date date = new Date();
		SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd");
		String nowDate = sdfNow.format(date);
		int year = new Integer(nowDate.substring(0, 4));
		// 获取月 作为获取每个月的最后一天的参数
		int month = new Integer(nowDate.substring(4, 6));

		int nowYear = year;
		int nowMonth = month;
		// 开始时间
		String start = "";
		// 结束时间
		String end = "";
		// post的参数
		String str = "";
		// ==============取数据=================// 六个月
		int count = 6;
		for (int i = 0; i < count; i++) {
			Map<String, Object> dmap = new HashMap<String, Object>(16);
			if (i == 0) {
				start = GetMonth.firstDate(year, month);
				end = nowDate;
			} else {
				start = GetMonth.firstDate(year, month);
				end = GetMonth.lastDate(year, month);
			}
			str = "<buffalo-call>" + "\n"
					+ "<method>qry_sj_yuyinfeiqingdan</method>" + "\n"
					+ "<string>" + start + "</string>" + "\n" + "<string>"
					+ end + "</string>" + "\n" + "</buffalo-call>" + "\n";

			try {
				RequestEntity entity = new StringRequestEntity(str,
						"text/html", "utf-8");
				post.setRequestEntity(entity);
				httpClient.executeMethod(post);
				Thread.sleep(5000);
				String html = post.getResponseBodyAsString();
				if (html.contains("糟糕...出错了")) {
					map.put("errorCode", "0001");
					map.put("errorInfo", "糟糕...出错了");
					PushState.state(phoneNumber, "callLog", 200);

					return map;
				} else if (html.contains("二次短信验证失败")) {
					map.put("errorCode", "0001");
					map.put("errorInfo", "验证码错误");
					PushState.state(phoneNumber, "callLog", 200, "验证码错误");

					return map;
				} else {
					// 上个月包括年和月 获取年
					nowYear = new Integer(GetMonth.beforMon(year, month, 1)
							.substring(0, 4));
					// 获取上个月
					nowMonth = new Integer(GetMonth.beforMon(year, month, 1)
							.substring(4));
					year = nowYear;
					month = nowMonth;
					// ===============================数据=======================
					dmap.put("item", html);
					dataList.add(dmap);
				}
				Thread.sleep(500);
			} catch (Exception e) {
				logger.error("-------宁夏电信------------", e);
				map.put("errorCode", "0001");
				map.put("errorInfo", "网络连接异常");
				PushState.state(phoneNumber, "callLog", 200, "获取数据失败，网络连接异常");
				PushSocket.pushnew(map, uuid, "7000", "获取数据失败，网络连接异常");
				return map;
			}
		}
		PushSocket.pushnew(map, uuid, "6000", "获取数据成功");
		map.put("data", dataList);
		map.put("UserPassword", servePwd);
		map.put("UserIphone", phoneNumber);
		map.put("longitude", longitude);
		map.put("latitude", latitude);
		map.put("flag", "13");
		map.put("errorCode", "0000");
		map.put("errorInfo", "查询成功");
		logger.warn("宁夏电信数据查询成功");
		Resttemplate resttemplate = new Resttemplate();
		map = resttemplate.SendMessage(map, application.getSendip()
				+ "/HSDC/message/telecomCallRecord");
		if (map.get(errorCode).equals(success)) {
			PushSocket.pushnew(map, uuid, "8000", "认证成功");
			PushState.state(phoneNumber, "callLog", 300);
		} else {
			PushSocket.pushnew(map, uuid, "9000", map.get("errorInfo").toString());
			PushState.state(phoneNumber, "callLog", 200, map.get("errorInfo").toString());
		}
		driver.quit();
		return map;
	}
	
}
