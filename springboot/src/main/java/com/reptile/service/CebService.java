package com.reptile.service;

import com.reptile.util.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.By.ByClassName;
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
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author liubin
 * @date 2017/12/29
 *
 */
@Service
public class CebService {
	@Autowired
	private application applications;
	private Logger logger = LoggerFactory.getLogger(CebService.class);

	public Map<String, Object> ceblogin1(HttpServletRequest request, String usercard, String userName) {
		// 创建map进行数据传输
		Map<String, Object> map = new HashMap<String, Object>(200);
		// 创建IE浏览器
		System.setProperty(ConstantInterface.ieDriverKey, ConstantInterface.ieDriverValue);
		WebDriver driver = new InternetExplorerDriver();
		try {
			// 隐式等待
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			// 光大银行信用卡登录页面地址
			driver.get("https://xyk.cebbank.com/mycard/bill/havingprintbill-query.htm");
			driver.navigate().refresh();
			
			// login
			WebElement loginform = driver.findElement(By.id("login"));
			// 输入用户名及密码
			loginform.findElement(By.id("userName")).sendKeys(userName);
			// 获取图片验证码进行打码
			List<WebElement> image = loginform.findElements(By.tagName("img"));
			WebElement captchaImg = image.get(0);
			// 通过driver获得图片信息
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			// 进行 图片的读写处理
			BufferedImage fullImg = ImageIO.read(screenshot);
			Point point = captchaImg.getLocation();
			int eleWidth = captchaImg.getSize().getWidth();
			int eleHeight = captchaImg.getSize().getHeight();
			BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
			ImageIO.write(eleScreenshot, "png", screenshot);
			// 将图片存在本地
			String filename = "CEB//" + System.currentTimeMillis() + ".png";
			File screenshotLocation = new File("C://" + filename);
			FileUtils.copyFile(screenshot, screenshotLocation);
			// 图片验证，打码平台
			Map<String, Object> map1 = MyCYDMDemo.Imagev("C://" + filename);
			String strResult = (String) map1.get("strResult");
			// 将图片打码结果放入输入框
			loginform.findElement(By.id("yzmcode")).sendKeys(strResult);
			Thread.sleep(3000);
			// 这一步走完后，图片验证码也输入完毕了，开始点击发送验证码
			// 通过定位获取获取验证码的按钮
			List<WebElement> button = loginform.findElements(By.tagName("button"));
			Thread.sleep(2000);
			// 点击获取获取验证码的按钮
			button.get(0).click();
			Thread.sleep(1000);
			// 定义popup-dialog-message
			String error = "popup-dialog-message";
			// 判断页面是否有这个标签从而验证信息
			if (driver.getPageSource().contains(error)) {
				driver.findElement(ByClassName.className(error));
				
				map.put("errorInfo", "光大银行登录时输入有误");
				map.put("errorCode", "0002");
				logger.warn("光大银行登录时输入有误开始" + usercard+map);
				try {
					driver.quit();
					// 关闭浏览器
					InvokeBat4.runbat();
				} catch (Exception e3) {
				}
				logger.warn("光大银行登录时输入有误" + usercard+map);
				return map;
//			if (driver.getPageSource().contains(error)) {
//				WebElement errordiv=  driver.findElement(ByClassName.className(error));
//				System.out.println(errordiv.getText()+"========");
//				System.out.println(errordiv.getText().contains("失败了")+"==========");
//					if(errordiv.getText().contains("失败了") ){
//						try {
//							driver.quit();
//							// 关闭浏览器
////							InvokeBat4.runbat();
//							Thread.sleep(3000);
//						} catch (Exception e3) {
//						}
//						ceblogin1(request, usercard, userName);
//					}
//					logger.info("光大银行登录时输入有误" + usercard);
//					map.put("errorInfo", "光大银行登录,"+errordiv.getText());
//					map.put("errorCode", "0002");
//					try {
//						driver.quit();
//						// 关闭浏览器
//						InvokeBat4.runbat();
//					} catch (Exception e3) {
//					}
//					return map;
			} else {
				// 没有popup-dialog-message，则输入的信息为正确
				// 获得session
				HttpSession session = request.getSession();
				// 将浏览器driver存到session中第二步请求再通过session获取
				session.setAttribute("sessionDriver-Ceb" + usercard, driver);
				Map<String, Object> data = new HashMap<String, Object>(200);
				data.put("driverName", "sessionDriver-Ceb" + usercard);
				// 向app传输状态
				map.put("errorInfo", "动态密码发送成功");
				map.put("errorCode", "0000");
				map.put("data", data);
			}
		} catch (Exception e) {
			// 在登陆过程中出现错误会到catch中
			e.printStackTrace();
			logger.warn("光大银行登录时发送验证码失败" + usercard);
			map.put("errorInfo", "服务繁忙！请稍后再试，验证码发送失败");
			map.put("errorCode", "0001");
			try {
				driver.quit();
				InvokeBat4.runbat();
			} catch (Exception e2) {
				
			}

		}
		logger.warn("光大银行登录时" + usercard+map);
		return map;

	}

	public Map<String, Object> ceblogin2(HttpServletRequest request, String userCard, String passWord,
			String userAccount, String uuid, String timeCnt) throws ParseException {
		boolean isok = CountTime.getCountTime(timeCnt);
		// 创建map进行数据存储
		Map<String, Object> map = new HashMap<String, Object>(200);
		// 向app推送登录中的状态
		PushSocket.pushnew(map, uuid, "1000", "光大银行登录中");
		logger.warn("光大信用卡第二步进入==========="+userCard+passWord+userAccount);
		String flag = "";
		if (isok == true) {
			PushState.state(userCard, "bankBillFlow", 100);
		}
		Map<String, Object> data = new HashMap<String, Object>(200);
		// 获得session
		HttpSession session = request.getSession();
		// 存在session 中的浏览器
		Object sessiondriver = session.getAttribute("sessionDriver-Ceb" + userCard);
		// 进行转化，将session中的driver转换成webDeriver类型
		final WebDriver driver = (WebDriver) sessiondriver;
		// 判断session是否为空
		if (sessiondriver == null) {
			if (isok == true) {
				PushState.state(userCard, "bankBillFlow", 200);
				logger.warn("光大信用卡第二步进入========200==="+userCard+passWord+userAccount);
			}else{
				PushState.statenew(userCard, "bankBillFlow", 200, "连接超时！请重新获取验证码");
				logger.warn("光大信用卡第二步进入======连接超时！请重新获取验证码==200==="+userCard+passWord+userAccount);
			}
			// 当session为空时，返回map状态
			logger.warn("连接超时！请重新获取验证码" + userCard);
			map.put("errorInfo", "连接超时！请重新获取验证码");
			map.put("errorCode", "0002");
			// 并推送3000状态
			logger.warn("光大信用卡第二步进入======连接超时！请重新获取验证码==3000==="+userCard+passWord+userAccount);
			PushSocket.pushnew(map, uuid, "3000", "连接超时！请重新获取验证码");
			try {
				// 并关闭浏览器
				driver.quit();
				// 关闭进程
				InvokeBat4.runbat();
			} catch (Exception e) {
			}
			logger.warn("光大信用卡第二步进入========200==="+userCard+passWord+userAccount+map);
			return map;
		}
		try {
			WebElement loginform = driver.findElement(By.id("login"));
			if (isok) {
				loginform.findElement(By.id("yzmcode"));
			} else {

			}
			//如果光大的验证码输入时间较晚，页面上的图片验证码会刷新，需要进行判断是否刷新，然后重新打码进行输入。
		    String 	yzmcode= loginform.findElement(By.id("yzmcode")).getText();
			logger.warn("光大信用卡第二步进入========	yzmcode==="+userCard+passWord+userAccount+map+"========="+yzmcode);
		    if (yzmcode.length()==0) {
		    	System.out.println(loginform.findElement(By.id("yzmcode")).getText()+"-*-*-*-*-*-*-");
		    	List<WebElement> image = loginform.findElements(By.tagName("img"));
				WebElement captchaImg = image.get(0);
				// 通过driver获得图片信息
				File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				// 进行 图片的读写处理
				BufferedImage fullImg = ImageIO.read(screenshot);
				Point point = captchaImg.getLocation();
				int eleWidth = captchaImg.getSize().getWidth();
				int eleHeight = captchaImg.getSize().getHeight();
				BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
				ImageIO.write(eleScreenshot, "png", screenshot);
				// 将图片存在本地
				String filename = "CEB//" + System.currentTimeMillis() + ".png";
				File screenshotLocation = new File("C://" + filename);
				FileUtils.copyFile(screenshot, screenshotLocation);
				// 图片验证，打码平台
				Map<String, Object> map1 = MyCYDMDemo.Imagev("C://" + filename);
				String strResult = (String) map1.get("strResult");
				// 将图片打码结果放入输入框
				loginform.findElement(By.id("yzmcode")).sendKeys(strResult);
			} 
		    
			// 输入app前端传输过来的短信验证码
			loginform.findElement(By.id("verification-code")).sendKeys(passWord);
			// 点击光大银行信用卡登录按钮
			loginform.findElement(ByClassName.className("login-style-bt")).click();
			loginform.findElement(ByClassName.className("login-style-bt")).click();
			// 判断验证码是否正确
			String error = "popup-dialog-message";
			logger.warn("光大信用卡第二步进入========	判断短信验证是否正确==="+userCard+passWord+userAccount+"========="+error);
			if (driver.getPageSource().contains(error)) {
				driver.findElement(ByClassName.className(error));
				Thread.sleep(2000);
				if (isok == true) {
					PushState.state(userCard, "bankBillFlow", 200, "短信验证码输入有误");
				}else{
					PushState.statenew(userCard, "bankBillFlow", 200, "短信验证码输入有误");
				}
				logger.warn("光大银行登录时发送验证码输入有误" + userCard);
				map.put("errorInfo", "短信验证码输入有误");
				map.put("errorCode", "0002");
				logger.warn("光大信用卡第二步进入========	判断短信验证是否正确==="+userCard+passWord+userAccount+"========="+error);
				try {
					driver.quit();
					InvokeBat4.runbat();
				} catch (Exception e) {
				}
				PushSocket.pushnew(map, uuid, "3000", "短信验证码输入有误");
				logger.warn("光大信用卡第二步进入========	短信验证码输入有误==="+userCard+passWord+userAccount+"========="+error);
				return map;
			} else {
				Thread.sleep(3000);
				System.out.println("点击成功");
				// 通过下面的网址进入个人中心
				driver.get("https://xyk.cebbank.com/mycard/bill/havingprintbill-query.htm");
				System.out.println("开始进入个人中心");
				// 获取到页面元素进行定位
				Thread.sleep(3000);
				WebElement table = driver.findElement(ByClassName.className("tab_one"));
				List<WebElement> tr = table.findElements(By.tagName("tr"));
				List<String> html = new ArrayList<String>();
				List<String> times = new ArrayList<String>();
				// 向app前端进行推送
				PushSocket.pushnew(map, uuid, "2000", "光大银行信用卡登录成功");
				logger.warn("光大信用卡第二步进入========	光大银行信用卡登录成功=2000=="+userCard+passWord+userAccount);
				flag = "2000";
				// 向app前端进行推送
				PushSocket.pushnew(map, uuid, "5000", "光大银行信用卡信息获取中");
				logger.warn("光大信用卡第二步进入========	光大银行信用卡信息获取中=5000=="+userCard+passWord+userAccount);
				flag = "5000";
				// 将月份进行加工
				for (int i = 1; i < tr.size(); i++) {
					WebElement tds = tr.get(i);
					List<WebElement> td = tds.findElements(By.tagName("td"));
					WebElement time = td.get(0);
					String month = time.getText().replace("/", "").trim();
					System.out.println(month);
					times.add(month);
				}
				// 获取到信用卡的详单
				for (int i = 0; i < times.size(); i++) {
					driver.get("https://xyk.cebbank.com/mycard/bill/billquerydetail.htm?statementDate=" + times.get(i));
					String detailedpage = driver.getPageSource();
					html.add(detailedpage);
				}
				// 详单获取成功，向数据中心推送数据
				PushSocket.pushnew(map, uuid, "6000", "光大银行信用卡信息获取成功");
				logger.warn("光大信用卡第二步进入========	光大银行信用卡信息获取成功=6000=="+userCard+passWord+userAccount);
				flag = "6000";
				Map<String, Object> seo = new HashMap<String, Object>(200);
				System.out.println("页面已经放置到html中");
				data.put("html", html);
				data.put("backtype", "CEB");
				data.put("idcard", userCard);
				data.put("userAccount", userAccount);
				seo.put("data", data);
				System.out.println(seo);
				Resttemplate resttemplate = new Resttemplate();
				map = resttemplate.SendMessage(seo, applications.getSendip() + "/HSDC/BillFlow/BillFlowByreditCard");
				try {
					driver.quit();
					InvokeBat4.runbat();
				} catch (Exception e1) {
				}
				// 判断数据传输后的结果，进行判断
				String errorCode = "errorCode";
				String state0 = "0000";
				if (map != null && state0.equals(map.get(errorCode).toString())) {
//					if (isok == true) {
//						PushState.state(userCard, "bankBillFlow", 300);
//					}
					PushState.stateByFlag(userCard, "savings",300,isok);
					map.put("errorInfo", "查询成功");
					map.put("errorCode", "0000");
					PushSocket.pushnew(map, uuid, "8000", "光大银行信用卡认证成功");
					logger.warn("光大信用卡第二步进入========	光大银行信用卡认证成功=8000=="+userCard+passWord+userAccount+map);
				} else {
					// --------------------数据中心推送状态----------------------
					if (isok == true) {
						PushState.state(userCard, "bankBillFlow", 200, "光大银行信用卡认证失败");
					}else{
						PushState.statenew(userCard, "bankBillFlow", 200, "光大银行信用卡认证失败");
					}
					// ---------------------数据中心推送状态----------------------
					logger.warn("光大银行账单推送失败" + userCard);
					PushSocket.pushnew(map, uuid, "9000", "光大银行信用卡认证失败");
					logger.warn("光大信用卡第二步进入========	光大银行信用卡认证失败=9000=="+userCard+passWord+userAccount+map);
				}
			}

		} catch (Exception e) {
			// 当页面获取的过程中
			try {
				driver.quit();
				InvokeBat4.runbat();
			} catch (Exception e3) {
			}
			// ---------------------------数据中心推送状态----------------------------------
			String state = "2000";
			String state1 = "5000";
			String state2 = "6000";
			if (isok == true) {
				if (state.equals(flag)) {
					PushSocket.pushnew(map, uuid, "7000", "光大银行账单获取失败");
					PushState.state(userCard, "bankBillFlow", 200, "光大银行账单获取失败");
				} else if (state1.equals(flag)) {
					PushSocket.pushnew(map, uuid, "7000", "光大银行账单获取失败");
					PushState.state(userCard, "bankBillFlow", 200, "光大银行账单获取失败");
				} else if (state2.equals(flag)) {
					PushSocket.pushnew(map, uuid, "9000", "认证失败");
					PushState.state(userCard, "bankBillFlow", 200,"认证失败");
				} else {
					PushSocket.pushnew(map, uuid, "3000", "登录失败，验证码错误");
					PushState.state(userCard, "bankBillFlow", 200, "登录失败，验证码错误");
				}
			}else{
				if (state.equals(flag)) {
					PushSocket.pushnew(map, uuid, "7000", "光大银行账单获取失败");
					PushState.statenew(userCard, "bankBillFlow", 200, "光大银行账单获取失败");
				} else if (state1.equals(flag)) {
					PushSocket.pushnew(map, uuid, "7000", "光大银行账单获取失败");
					PushState.statenew(userCard, "bankBillFlow", 200, "光大银行账单获取失败");
				} else if (state2.equals(flag)) {
					PushSocket.pushnew(map, uuid, "9000", "认证失败");
					PushState.statenew(userCard, "bankBillFlow", 200, "认证失败");
				} else {
					PushSocket.pushnew(map, uuid, "3000", "登录失败，验证码错误");
					PushState.statenew(userCard, "bankBillFlow", 200, "登录失败，验证码错误");
				}
			}
			
			// ---------------------------数据中心推送状态----------------------------------
			e.printStackTrace();
			map.clear();
			logger.warn("光大银行账单推送失败" + userCard+map);
			map.put("errorInfo", "获取账单失败");
			map.put("errorCode", "0002");
		}
		logger.warn("光大信用卡第二步进入========	"+userCard+passWord+userAccount+map);
		return map;

	}
}
