package com.reptile.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reptile.util.ConstantInterface;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;



@Service
public class CCBService {
	@Autowired
	  private application applications;
	  private PushState PushState;
	  private Logger logger= LoggerFactory.getLogger(CCBService.class);
	  private final static String CEBlogin="https://ibsbjstar.ccb.com.cn/CCBIS/B2CMainPlat_06?SERVLET_NAME=B2CMainPlat_06&CCB_IBSVersion=V6&PT_STYLE=1&CUSTYPE=0&TXCODE=CLOGIN&DESKTOP=0&EXIT_PAGE=login.jsp&WANGZHANGLOGIN=&FORMEPAY=2";//建设银行登录页面
	  public Map<String,Object> ccbInformation(HttpServletRequest request,String IDNumber,String cardNumber,String cardPass,String UUID){
			Map<String, Object> map=new HashMap<String, Object>();
			Map<String,Object> data=new HashMap<String,Object>();
			
			System.setProperty(ConstantInterface.chromeDriverKey, ConstantInterface.chromeDriverValue);
			WebDriver driver = new ChromeDriver();

			driver.get(CEBlogin);
			driver.navigate().refresh();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);//隐式等待
			PushSocket.pushnew(map, UUID, "1000","建设银行储蓄卡登录中");
			driver.findElement(By.id("USERID")).sendKeys(cardNumber);
			
			Actions action = new Actions(driver);
	        action.sendKeys(Keys.TAB).build().perform();
	        
	        driver.findElement(By.id("LOGPASS")).sendKeys(cardPass);
	        driver.findElement(By.id("loginButton")).click();
	        String dpage1=driver.getPageSource();
	        System.out.println("aaa----------"+dpage1+"aaaa-----------");
	        System.out.println(dpage1.indexOf("您输入的登录密码"));
	        System.out.println(dpage1.indexOf("您输入的登录密码")==1);
	        if(dpage1.indexOf("您输入的登录密码")!=-1){
	        	logger.warn("登录密码错误"+IDNumber+driver.getPageSource());
	        	map.put("errorInfo","您输入的登录密码错误");
	            map.put("errorCode","0003");
	            driver.close();
	        	PushSocket.pushnew(map, UUID, "3000","建设银行储蓄卡输入的登录密码错误");
	            return map;  
	        }
	        System.out.println(dpage1.indexOf("您输入的信息有误")!=-1);
	        if(dpage1.indexOf("您输入的信息有误")!=-1){
	        	logger.warn("登录信息填写有误"+IDNumber+driver.getPageSource());
	        	  driver.close();
	        	map.put("errorInfo","您输入的信息有误");
	            map.put("errorCode","0003");
	            PushSocket.pushnew(map, UUID, "3000","建设银行储蓄卡输入的信息有误");
	        	return map;  
	        }
	        PushState.state(IDNumber, "savings",100);

	        driver.get("https://ibsbjstar.ccb.com.cn/CCBIS/B2CMainPlat_06?SERVLET_NAME=B2CMainPlat_06&CCB_IBSVersion=V6&PT_STYLE=1#");
		  WebElement name= driver.findElement(ByClassName.className("msg_welcome"));//用户名
		  String userName = name.getText().substring(0, name.getText().lastIndexOf("，"));//户名
		  System.out.println(userName);
		  driver.findElement(By.id("per1")).click();//点击账户查询
		  try {
			  Thread.sleep(2000);
		  } catch (InterruptedException e) {
			  e.printStackTrace();
		  }
		  String accountType = null;//开户状态
		  String openBranch = null;//开户网点
		  String openTime = null;//开户时间
		  PushSocket.pushnew(map, UUID, "2000","建设银行储蓄卡登录成功");
		  try {
			  PushSocket.push(map, UUID, "0000");
			  driver.switchTo().frame("txmainfrm");//第一级的frame
				 driver.switchTo().frame("result");//第2级的frame
				 driver.switchTo().frame("result");//第3级的frame,可点击页面元素
				 Document page= Jsoup.parse(driver.getPageSource());
				 accountType	=page.getElementsByClass("mt_10").text();//开户状态
				 PushSocket.pushnew(map, UUID, "5000","建设银行储蓄卡获取中");
				 System.out.println(accountType);
				 page.getElementsByClass("pl_10 pt_10").text();
				 String open= page.getElementsByClass("mb_20").text();
				 openBranch=open.substring(open.indexOf("签约分行")+5, open.lastIndexOf("签约状态"));//开户网点
				 openTime =open.substring(open.indexOf("可用余额 操作")+9, open.lastIndexOf("活期储蓄"));//开户时间
				 System.out.println(open);
				 driver.findElement(ByXPath.xpath("/html/body/div/div[2]/table/tbody/tr/td[6]/a")).click();//点击查看明细
				 Thread.sleep(5000);
				} catch (Exception e) {
					PushState.state(IDNumber, "savings",200);
	            	logger.warn("已登录在获取基本信息时报错！建设银行页面数据加载缓慢"+IDNumber);
	            	 PushSocket.pushnew(map, UUID, "7000","建设银行储蓄卡获取失败");
	            	  driver.close();
		        	map.put("errorInfo","网络异常！数据加载过慢");
		            map.put("errorCode","0004");
		        	return map;  
					
				}
				//---------------------------------------------------------------
			    try {  
			        String currentHandle = driver.getWindowHandle();  
			        Set<String> handles = driver.getWindowHandles();  
			        System.out.println("currentHandle:"+currentHandle+"handles:"+handles.toString()+"driver.getTitle():"+driver.getTitle());
			        for (String s : handles) {  
			            if (s.equals(currentHandle))  
			                continue;  
			            else {  
			                driver.switchTo().window(s);  
			                if (driver.getTitle().contains("中国建设银行个人网上银行")) {  
			                	driver.switchTo().frame("sear");
			                	//------------------------------------------------------------------
			                	Date date=new Date();
			                	String time= new SimpleDateFormat("yyyyMMdd").format(date);
			     			    Calendar c = Calendar.getInstance();//获得一个日历的实例
			     			    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			     			    date = sdf.parse(time);//初始日期
			     			    c.setTime(date);//设置日历时间
			     			    c.add(Calendar.MONTH,-10);//在日历的月份上减少10个月
			     			    System.out.println(sdf.format(c.getTime()));//得到6个月后的日期
			     			    //-------------------------------------------------------------------
			     			    JavascriptExecutor jss = (JavascriptExecutor) driver;
			     			    String jsv =" $('#START_DATE').val('"+sdf.format(c.getTime())+"');";
			     			    jss.executeScript(jsv, "");
			     			    Thread.sleep(2000);
			     			    driver.findElement(By.id("qd1")).click();
			                    driver.switchTo().frame("result1");//第一页
			                    Document page1= Jsoup.parse(driver.getPageSource()); 
			                    Element table = page1.getElementById("result");
			                    Elements tr =table.getElementsByTag("tr");
			                    List<Map<String,Object>> trs = new ArrayList<Map<String,Object>>();
			                    for (int i = 1; i < tr.size(); i++) {
			                	 	Elements td =  tr.get(i).select("td");
									Map<String,Object> tds=new HashMap<String,Object>();
								for (int n = 0; n< td.size(); n++) {
									    if(n==1){
										    tds.put("dealTime", td.get(n).text());
										 }
										if(n==2){
											tds.put("expendMoney", td.get(n).text());
											}
										if(n==3){
											tds.put("incomeMoney",td.get(n).text());
											}
										if(n==4){
											tds.put("balanceAmount", td.get(n).text());
											}
										if(n==5){
											tds.put("oppositeSideNumber",td.get(n).text());
											}
										if(n==6){
											tds.put("oppositeSideName",td.get(n).text());
											}
										if(n==7){
											tds.put("currency", td.get(n).text());
											}
										if(n==8){
											tds.put("dealReferral", td.get(n).text());
											}
										if(n==9){
											tds.put("dealDitch", td.get(n).text());
											}
								}
									trs.add(tds);
								}
			                    driver.switchTo().parentFrame();
		                    for (int j = 2; j < 9; j++) {
			                    	JavascriptExecutor j1 = (JavascriptExecutor) driver;
					     			String js1 ="TxtSubmit2("+j+")";
					     			j1.executeScript(js1, "");
					     			Thread.sleep(2000);
									driver.switchTo().frame("result1");
									System.out.println("----第----"+j+"---页--");
									driver.getPageSource();
									Document pagej= Jsoup.parse(driver.getPageSource()); 
				                    Element tablej = pagej.getElementById("result");
				                    Elements trj =tablej.getElementsByTag("tr");
				                    for ( int i=1; i<trj.size(); i++){  
										Elements tdj =  trj.get(i).select("td");  
										Map<String,Object> tdsj=new HashMap<String,Object>();
										for (int n = 0; n< tdj.size(); n++) {
											if(n==1){
												tdsj.put("dealTime", tdj.get(n).text());
											}
											if(n==2){
												tdsj.put("expendMoney", tdj.get(n).text());
												}
											if(n==3){
												tdsj.put("incomeMoney", tdj.get(n).text());
												}
											if(n==4){
												tdsj.put("balanceAmount", tdj.get(n).text());
												}
											if(n==5){
												tdsj.put("oppositeSideNumber", tdj.get(n).text());
												}
											if(n==6){
												tdsj.put("oppositeSideName",tdj.get(n).text());
												}
											if(n==7){
												tdsj.put("currency", tdj.get(n).text());
												}
											if(n==8){
												tdsj.put("dealReferral", tdj.get(n).text());
												}
											if(n==9){
												tdsj.put("dealDitch", tdj.get(n).text());
												}
										}
										trs.add(tdsj);
									}
				                    
									driver.switchTo().parentFrame();
								}
		                    PushSocket.pushnew(map, UUID, "6000","建设银行储蓄卡获取成功");
		                    Collections.reverse(trs);
		                    Map<String,Object> baseMes=new HashMap<String, Object>();
		                    Map<String,Object> ccb=new HashMap<String, Object>();
		                    ccb.put("dillMes", trs);
		                    ccb.put("IDNumber", IDNumber);
		                    ccb.put("cardNumber", cardNumber);
		                    ccb.put("userName", userName);
		                    ccb.put("baseMes", baseMes);
		                    baseMes.put("accountType", accountType);
		                    baseMes.put("openBranch", openBranch);
		                    baseMes.put("openTime", openTime);
		                	Resttemplate resttemplate = new Resttemplate();
		                    map=resttemplate.SendMessage(ccb,ConstantInterface.port+"/HSDC/savings/authentication");
		                    if(map!=null&&"0000".equals(map.get("errorCode").toString())){
						    	PushState.state(IDNumber, "savings",300);
						    	PushSocket.pushnew(map, UUID, "8000","建设银行储蓄卡认证成功");
				                map.put("errorInfo","查询成功");
				                map.put("errorCode","0000");
				                PushSocket.push(map, UUID, "0000");
				                driver.close();
				                Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
				                
				            }else{
				            	PushState.state(IDNumber, "savings",200);
				            	PushSocket.pushnew(map, UUID, "9000","建设银行储蓄卡认证失败");
				            	logger.warn("建设银行数据推送失败"+IDNumber);
				                PushSocket.push(map, UUID, "0001");
				                driver.close();
				                Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
				            	return map;
				            }
			                 break;  
			                } else  
			                    continue;  
			            }  
			        }  
			    } catch (Exception e) { 
//			    	 PushSocket.push(map, UUID, "0001");
			    	 e.printStackTrace();
			    	 PushState.state(IDNumber, "savings",200);
			    	 map.clear();
					 logger.warn("建设银行详单获取失败"+IDNumber);
					 PushSocket.pushnew(map, UUID, "7000","建设银行储蓄卡获取失败");
					 map.put("errorInfo","获取账单失败");
					 map.put("errorCode","0002");
//					 driver.quit();
					  driver.close();
					  try {
						Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					 return map;  
			    }
			    return map;  
			}
}
