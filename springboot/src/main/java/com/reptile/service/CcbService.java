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

/**
 * 
 * @author liubin
 *
 */

@Service
public class CcbService {
	@Autowired
	  private application applications;
	  private Logger logger= LoggerFactory.getLogger(CcbService.class);
	  /**
	   * 建设银行登录页面
	   */
	  private final static String CEB_LOGIN="https://ibsbjstar.ccb.com.cn/CCBIS/B2CMainPlat_06?SERVLET_NAME=B2CMainPlat_06&CCB_IBSVersion=V6&PT_STYLE=1&CUSTYPE=0&TXCODE=CLOGIN&DESKTOP=0&EXIT_PAGE=login.jsp&WANGZHANGLOGIN=&FORMEPAY=2";
	  public Map<String,Object> ccbInformation(HttpServletRequest request,String iDNumber,String cardNumber,String cardPass,String uuid,boolean pushFlag){
			//创建Map进行与app的数据传输
		  	Map<String, Object> map=new HashMap<String, Object>(70);
			//创建driver(IE)
//			System.setProperty(ConstantInterface.ieDriverKey, ConstantInterface.ieDriverValue);
//			WebDriver driver = new InternetExplorerDriver();
		  	String flag = "";
			//创建谷歌浏览器
			System.setProperty(ConstantInterface.chromeDriverKey, ConstantInterface.chromeDriverValue);
			WebDriver driver = new ChromeDriver();
			//输入需要登录的地址
			driver.get(CEB_LOGIN);
			//设置浏览器属性,隐式等待
			driver.navigate().refresh();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			//向app推送登录状态
			PushSocket.pushnew(map, uuid, "1000","建设银行储蓄卡登录中");
			PushState.stateByFlag(iDNumber, "savings",100,pushFlag);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
		try {
			
	
			//输入建设银行储蓄卡卡号
			driver.findElement(By.id("USERID")).sendKeys(cardNumber);
			//按下tab
			Actions action = new Actions(driver);
	        action.sendKeys(Keys.TAB).build().perform();
	        //输入建设银行储蓄卡网银查询密码
	        driver.findElement(By.id("LOGPASS")).sendKeys(cardPass);
	        //点击登录
	        driver.findElement(By.id("loginButton")).click();
	        //进行判断是否登录成功
	        String dpage1=driver.getPageSource();
	        String state="";
	        state="您输入的登录密码";
	        if(dpage1.indexOf(state)!=-1){
	        	logger.warn("登录密码错误"+iDNumber+driver.getPageSource());
	        	map.put("errorInfo","您输入的登录密码错误");
	            map.put("errorCode","0003");
	        	try {
	        		//关闭浏览器
					driver.quit();
				} catch (Exception e2) {
				}
	        	PushSocket.pushnew(map, uuid, "3000","建设银行储蓄卡输入的登录密码错误");
	        	PushState.stateByFlag(iDNumber, "savings",200,"建设银行储蓄卡输入的登录密码错误",pushFlag);
	            return map;  
	        }
	        state="您输入的信息有误";
	        if(dpage1.indexOf(state)!=-1){
	        	logger.warn("登录信息填写有误"+iDNumber+driver.getPageSource());
	        	try {
					driver.quit();
				} catch (Exception e2) {
					// TODO: handle exception
				}
	        	map.put("errorInfo","您输入的信息有误");
	            map.put("errorCode","0003");
	            PushSocket.pushnew(map, uuid, "3000","建设银行储蓄卡输入的信息有误");
	            PushState.stateByFlag(iDNumber, "savings",200,"建设银行储蓄卡输入的信息有误",pushFlag);
	        	return map;  
	        }
	        
	       //到详情页面获取数据
	      driver.get("https://ibsbjstar.ccb.com.cn/CCBIS/B2CMainPlat_06?SERVLET_NAME=B2CMainPlat_06&CCB_IBSVersion=V6&PT_STYLE=1#");
	      //获取到户名
	      WebElement name= driver.findElement(ByClassName.className("msg_welcome"));
		  String userName = name.getText().substring(0, name.getText().lastIndexOf("，"));
		  System.out.println(userName);
		  //点击账户查询
		  driver.findElement(By.id("per1")).click();
		  try {
			  Thread.sleep(2000);
		  } catch (InterruptedException e) {
			  e.printStackTrace();
		  }
		  //开户状态
		  String accountType = null;
		  //开户网点
		  String openBranch = null;
		  //开户时间
		  String openTime = null;
		  PushSocket.pushnew(map, uuid, "2000","建设银行储蓄卡登录成功");
		  flag = "2000";
		  try {
			  	//第一级的frame
			  	 driver.switchTo().frame("txmainfrm");
			  	//第2级的frame
				 driver.switchTo().frame("result");
				//第3级的frame,可点击页面元素
				 driver.switchTo().frame("result");
				//将页面进行jsoup转换
				 Document page= Jsoup.parse(driver.getPageSource());
				//开户状态
				 accountType	=page.getElementsByClass("mt_10").text();
				 PushSocket.pushnew(map, uuid, "5000","建设银行储蓄卡获取中");
				 flag = "5000";
				 page.getElementsByClass("pl_10 pt_10").text();
				 String open= page.getElementsByClass("mb_20").text();
				//开户网点
				 openBranch=open.substring(open.indexOf("签约分行")+5, open.lastIndexOf("签约状态"));
				//开户时间
				 openTime =open.substring(open.indexOf("可用余额 操作")+9, open.lastIndexOf("活期储蓄"));
				//点击查看明细
				 driver.findElement(ByXPath.xpath("/html/body/div/div[2]/table/tbody/tr/td[6]/a")).click();
				 Thread.sleep(5000);
				} catch (Exception e) {
					PushState.stateByFlag(iDNumber, "savings",200,"建设银行储蓄卡获取失败",pushFlag);
	            	logger.warn("已登录在获取基本信息时报错！建设银行页面数据加载缓慢"+iDNumber);
	            	 PushSocket.pushnew(map, uuid, "7000","建设银行储蓄卡获取失败");
	            		try {
	    					driver.quit();
	    				} catch (Exception e2) {
	    				}
		        	map.put("errorInfo","网络异常！数据加载过慢");
		            map.put("errorCode","0004");
		        	return map;  
					
				}
				//------------------------进行建设银行资金流水的循环查询，需要进行点击下一页，进行查询---------------------------------------
			    try {  
			        String currentHandle = driver.getWindowHandle();  
			        Set<String> handles = driver.getWindowHandles();  
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
			                	//获得一个日历的实例
			     			    Calendar c = Calendar.getInstance();
			     			    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			     			    //初始日期
			     			    date = sdf.parse(time);
			     			 //设置日历时间
			     			    c.setTime(date);
			     			 //在日历的月份上减少10个月
			     			    c.add(Calendar.MONTH,-10);
			     			    //-------------------------------------------------------------------
			     			    JavascriptExecutor jss = (JavascriptExecutor) driver;
			     			    String jsv =" $('#START_DATE').val('"+sdf.format(c.getTime())+"');";
			     			    jss.executeScript(jsv, "");
			     			    Thread.sleep(2000);
			     			    driver.findElement(By.id("qd1")).click();
			     			 //第一页
			                    driver.switchTo().frame("result1");
			                    Document page1= Jsoup.parse(driver.getPageSource()); 
			                    Element table = page1.getElementById("result");
			                    Elements tr =table.getElementsByTag("tr");
			                    List<Map<String,Object>> trs = new ArrayList<Map<String,Object>>();
			                    for (int i = 1; i < tr.size(); i++) {
			                	 	Elements td =  tr.get(i).select("td");
									Map<String,Object> tds=new HashMap<String,Object>(70);
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
			                    int a=2;
			                    int b=9;
		                    for (int j = a; j < b; j++) {
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
										Map<String,Object> tdsj=new HashMap<String,Object>(200);
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
		                    //数据获取完成，进行推送
		                    PushSocket.pushnew(map, uuid, "6000","建设银行储蓄卡获取成功");
		                    flag = "6000";
		                    Collections.reverse(trs);
		                    Map<String,Object> baseMes=new HashMap<String, Object>(200);
		                    Map<String,Object> ccb=new HashMap<String, Object>(200);
		                    ccb.put("dillMes", trs);
		                    ccb.put("IDNumber", iDNumber);
		                    ccb.put("cardNumber", cardNumber);
		                    ccb.put("userName", userName);
		                    ccb.put("baseMes", baseMes);
		                    baseMes.put("accountType", accountType);
		                    baseMes.put("openBranch", openBranch);
		                    baseMes.put("openTime", openTime);
		                	Resttemplate resttemplate = new Resttemplate();
		                    map=resttemplate.SendMessage(ccb,ConstantInterface.port+"/HSDC/savings/authentication");
		                   //判断推送是否成功
		                    if(map!=null&&"0000".equals(map.get("errorCode").toString())){
		                    	PushState.stateByFlag(iDNumber, "savings",300,pushFlag);
						    	PushSocket.pushnew(map, uuid, "8000","建设银行储蓄卡认证成功");
				                map.put("errorInfo","查询成功");
				                map.put("errorCode","0000");
				            	try {
									driver.quit();
								} catch (Exception e2) {
								}
				                
				            }else{
				            	PushState.stateByFlag(iDNumber, "savings",200,"建设银行储蓄卡认证失败",pushFlag);
				            	PushSocket.pushnew(map, uuid, "9000","建设银行储蓄卡认证失败");
				            	logger.warn("建设银行数据推送失败"+iDNumber);
				                //PushSocket.push(map, UUID, "0001");
				            	try {
									driver.quit();
								} catch (Exception e2) {
								}
				            	return map;
				            }
			                 break;  
			                } else {
			                	continue;  
			                }
			            }  
			        }  
			    } catch (Exception e) { 
			    	 e.printStackTrace();
			    	 PushState.stateByFlag(iDNumber, "savings",200,"建设银行储蓄卡获取失败",pushFlag);
			    	 map.clear();
					 logger.warn("建设银行详单获取失败"+iDNumber);
					 PushSocket.pushnew(map, uuid, "7000","建设银行储蓄卡获取失败");
					 map.put("errorInfo","获取账单失败");
					 map.put("errorCode","0002");
						try {
							driver.quit();
						} catch (Exception e2) {
							
						}
					 return map;  
			    }	
			    } catch (Exception e) {
			    	String state = "2000";
					String state1 = "5000";
					String state2 = "6000";
					if (state.equals(flag)) {
						PushSocket.pushnew(map, uuid, "7000", "建设银行账单获取失败");
						PushState.stateByFlag(iDNumber, "bankBillFlow", 200, "建设银行账单获取失败",pushFlag);
					} else if (state1.equals(flag)) {
						PushSocket.pushnew(map, uuid, "7000", "建设银行账单获取失败");
						PushState.stateByFlag(iDNumber, "bankBillFlow", 200, "建设银行账单获取失败",pushFlag);
					} else if (state2.equals(flag)) {
						PushSocket.pushnew(map, uuid, "9000", "认证失败");
						PushState.stateByFlag(iDNumber, "bankBillFlow", 200,"认证失败",pushFlag);
					} else {
						PushSocket.pushnew(map, uuid, "3000", "登录失败，密码错误");
						PushState.stateByFlag(iDNumber, "bankBillFlow", 200, "登录失败，密码错误",pushFlag);
					}
			    	 e.printStackTrace();
			    	 PushState.stateByFlag(iDNumber, "savings",200,"建设银行储蓄卡获取失败",pushFlag);
			    	 map.clear();
					 logger.warn("建设银行详单获取失败"+iDNumber);
					 map.put("errorInfo","获取账单失败");
					 map.put("errorCode","0002");
					try {
						driver.quit();
					} catch (Exception e2) {
						
					}
				}	
			    return map;  
			}
}
