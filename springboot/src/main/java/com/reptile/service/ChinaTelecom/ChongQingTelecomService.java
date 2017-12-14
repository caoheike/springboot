package com.reptile.service.ChinaTelecom;

import com.reptile.util.ConstantInterface;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.PushSocket;
import com.reptile.util.Resttemplate;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;


@Service
public class ChongQingTelecomService {
	private Logger logger= LoggerFactory.getLogger(ChongQingTelecomService.class);
	   /**
		 * 重庆电信获取验证码
		 * @param request
		 * @return
		 */ 
	  public Map<String, Object> sendCode(HttpServletRequest request,String userName,String idCard) {
		 Map<String, Object> map = new HashMap<String, Object>();
	        HttpSession session = request.getSession();
	        Object attribute = session.getAttribute("driverCQ");

	        if (attribute == null) {
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "操作异常!");
	            return map;
	        } else {
	        	WebDriver driver =  (WebDriver)attribute;
	        	try {
	        	
	        	WebElement name=driver.findElement(By.id("tname"));
	        	Thread.sleep(200);
	        	name.sendKeys(userName);
	        	WebElement id_card=driver.findElement(By.id("id_card"));
	        	Thread.sleep(200);
	        	id_card.sendKeys(idCard);
	        	
	        	WebElement checkName=driver.findElement(By.id("xingm"));
	        	Thread.sleep(200);
    			WebElement card=driver.findElement(By.id("sfzhm"));
    			card.click();
    			Thread.sleep(200);
    			if(checkName.getText().contains("不正确")||card.getText().contains("不正确")){
    				 map.put("errorCode", "0001");
			         map.put("errorInfo", "信息不正确，请仔细核对后再输入");
			         return map;
			    }
	        	
	        	driver.findElement(By.id("send_sms")).click();
	        	Thread.sleep(500);
	            String tip=	driver.findElement(By.id("send_sms")).getText();
	        	if(tip.contains("秒后")){
	        		 map.put("errorCode", "0000");
			         map.put("errorInfo", "验证码发送成功");
			         session.setAttribute("driverGT", driver);   
	        	}else{
        		Alert alert = driver.switchTo().alert();
	        		if(alert!=null){
	        			map.put("errorCode", "0001");
			        map.put("errorInfo", alert.getText());
	        		}
	        	}
	        	
	        	
	        	} catch (Exception e) {
					 map.put("errorCode", "0001");
			         map.put("errorInfo", "网络异常!");
			         //driver.close();
					e.printStackTrace();
				}
	        } 
		return map;
		}
	 /**
		 * 重庆电信获取详单
		 * @param request
		 * @param phoneNumber
		 * @param passWord
		 * @param
		 * @param
		 * @param code
		 * @param longitude
		 * @param latitude
		 * @return
		 */
	 
	 public Map<String, Object> getDetail(HttpServletRequest request, String phoneNumber,String passWord,String code,String longitude,String latitude,String UUID){
		 Map<String, Object> map = new HashMap<String, Object>();
		 PushSocket.pushnew(map, UUID, "1000","登录中");  
	        List<Map<String, Object>> arrayList=new ArrayList<Map<String,Object>>();
	        HttpSession session = request.getSession();
	        Object attribute = session.getAttribute("driverGT");

	        if (attribute == null) {
	        	PushSocket.push(map, UUID, "0001");
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "请先获取短信验证码!");
	            PushSocket.pushnew(map, UUID, "3000","请先获取短信验证码!");  
	            return map;
		        } else {
		        	WebDriver driver =  (WebDriver)attribute;
		        	try {
		        		
		        	WebElement sms=driver.findElement(By.id("sms_input"));
		        	if(sms!=null){
		        		sms.clear();
			        	sms.sendKeys(code);//验证码
						Thread.sleep(500);
		        	}	
	//===========================获取数据=========================================
					 JavascriptExecutor js = (JavascriptExecutor) driver;
	                 js.executeScript("$('#xdcx_tr,#time_tr,#qd_xdcx_div').show();");
	                 Thread.sleep(500);
					WebElement selsct=	null;
					WebElement click=driver.findElement(By.xpath("//*[@id='time_div']/span[2]"));
					for(int i=1;i<8;i++){
						 Map<String, Object> dataMap = new HashMap<String, Object>();
						click.click();
						Thread.sleep(500);
						selsct=	driver.findElement(By.xpath("//*[@id='time_div']/p/a["+i+"]"));
						Thread.sleep(500);
						selsct.click();
						Thread.sleep(500);
						WebElement check=driver.findElement(By.id("qd_xdcx"));
						try{
							 check.click();
							 Thread.sleep(1000);
						}catch (Exception e){
							//PushSocket.push(map, UUID, "0001");
								 map.put("errorCode", "0001");
	    				         map.put("errorInfo", "验证码错误");	
	    				         PushSocket.pushnew(map, UUID, "3000","验证码错误");  
	    				         return map;
						}
					    
					if(driver.getPageSource().contains("使用地点")){
						PushSocket.pushnew(map, UUID, "2000","登录成功");
						//PushSocket.push(map, UUID, "0000");
						Thread.sleep(2000);
						PushSocket.pushnew(map, UUID, "5000","数据获取中");
						HttpClient httpClient = new HttpClient(); 
	        				//==================获取cookie==========================================  
	        				 Set<Cookie> cookie=driver.manage().getCookies();
	        				 StringBuffer cookies=new StringBuffer();
	        				 for (Cookie c : cookie) {   
	        					 cookies.append(c.toString()+";");
	        				 } 
	        				 
	        				 PostMethod post = new PostMethod("http://cq.189.cn/new-bill/bill_XDCX_Page"); 
	        				 post.setRequestHeader("Accept","application/json, text/javascript, */*; q=0.01");
	        				 
	        				 post.setRequestHeader("Connection","keep-alive");
	        				 post.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
	        				 post.setRequestHeader("Origin","http://nx.189.cn");
	        				 post.setRequestHeader("Referer","http://cq.189.cn/new-bill/bill_xd?fastcode=02031273&cityCode=cq");
	        				 post.setRequestHeader("Cookie",cookies.toString());
	        				 post.setParameter("page","1");
	        				 post.setParameter("rows","2000");
	        				 httpClient.executeMethod(post);
	        				 String html = post.getResponseBodyAsString();
	        				 System.out.println(html);
	        				 dataMap.put("item", html);
	 				      	 arrayList.add(dataMap);		 
	 				      	PushSocket.pushnew(map, UUID, "6000","数据获取成功");
	        			}else{
	        				 map.put("errorCode", "0001");
					         map.put("errorInfo", "系统繁忙!");
					         PushSocket.pushnew(map, UUID, "3000","登录失败");
	        			}
					}
				//------------推数据------------------------
					map.put("errorCode", "0000");
		            map.put("errorInfo", "查询成功!");
			        map.put("data", arrayList);
			        map.put("UserIphone", phoneNumber);
			        map.put("UserPassword", passWord);
			        map.put("longitude", longitude);//经度
			        map.put("latitude", latitude);//纬度
			        map.put("flag","15");
	                Resttemplate resttemplate=new Resttemplate();
	                map = resttemplate.SendMessage(map, ConstantInterface.port+"/HSDC/message/telecomCallRecord");
				 if(map.get("errorCode").equals("0000")) {
					 PushSocket.pushnew(map, UUID, "8000","认证成功");
				 }else {
					 PushSocket.pushnew(map, UUID, "9000","认证失败");
				 }
	                //------------推数据------------------------
	                //driver.close();
					
					} catch (Exception e) {
						map.put("errorCode", "0001");
				         map.put("errorInfo", "网络异常!");
				       
						e.printStackTrace();
					}finally{
						 driver.close();
						 try {
								Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
							} catch (IOException e) {
								logger.warn("宁夏电信",e);
								// TODO Auto-generated catch block
								//e.printStackTrace();
							}
					}
		        	
		        }
		 
		 return map;
		 
	 }
	
	/**
	 * 登陆
	 * @param request
	 * @param phoneNumber
	 * @param servePwd
	 * @return
	 */
	 public Map<String, Object> chongQingLogin(HttpServletRequest request, String phoneNumber, String servePwd){
			Map<String, Object> map = new HashMap<String, Object>();
			System.setProperty(ConstantInterface.chromeDriverKey,ConstantInterface.chromeDriverValue);
			//C:\\Program Files\\iedriver\\chromedriver.exe  正式上用这个
			ChromeOptions options = new ChromeOptions();
	        options.addArguments("start-maximized");
			WebDriver driver = new ChromeDriver(options);
			driver.get("http://login.189.cn/web/login");	
			driver.navigate().refresh();
			try {
				Thread.sleep(500);
			WebElement form = driver.findElement(By.id("loginForm"));

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
		      Thread.sleep(2000);
		      //================================
		        WebElement loginBtn = driver.findElement(By.id("txtCaptcha"));
		          loginBtn.sendKeys(catph);
		        Thread.sleep(2000);
		          WebElement loginBtns = driver.findElement(By.id("loginbtn"));
		          loginBtns.click();
		          Thread.sleep(2000);
			   if(driver.getPageSource().contains("详单查询")){
				driver.get("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=02031273");
	        	Thread.sleep(1000);
	        	driver.get("http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10004&toStUrl=http://cq.189.cn/new-bill/bill_xd?fastcode=02031273&cityCode=cq");
	        	Thread.sleep(1000);
				WebElement	name=driver.findElement(By.xpath("/html/body/div/div/table[2]/tbody/tr[1]/td/span[1]"));
				
				map.put("name", name.getText());
				map.put("errorCode", "0000");
				map.put("errorInfo", "登陆成功");
				
				}else{
					String divErr = driver.findElement(By.id("divErr")).getText();
					map.put("errorCode", "0001");
					if(divErr.contains("验证码不正确")){
						map.put("errorInfo", "网络异常，请稍后再试");
					}else{
						map.put("errorInfo", divErr);	
					}
					
					
					driver.close();
				}
		 
			} catch (Exception e) {
				e.printStackTrace();
				map.put("errorCode", "0001");
				map.put("errorInfo", "网络连接异常");
				driver.close();
			}	
			request.getSession().setAttribute("driverCQ", driver);//把driver放到session 
		// driver.close();
	return map;	 
	}
}
