package com.reptile.service;

import com.reptile.util.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author liubin
 *
 */
@Service
public class ConstructionService {
	@Autowired
	  private application applications;
	  private Logger logger= LoggerFactory.getLogger(ConstructionService.class);
	  public Map<String,Object> check(HttpServletRequest request,String userCard,String userCode,String codePass,String uuid,String timeCnt) throws ParseException{
		  boolean isok = CountTime.getCountTime(timeCnt);
		  //创建map
		  Map<String,Object>map=new HashMap<String,Object>(200);
		  PushSocket.pushnew(map, uuid, "1000","建设银行信用卡登陆中");
		  if(isok==true) {
				PushState.state(userCard, "bankBillFlow",100);
		  }
		  System.out.println(Thread.currentThread().getName());  
		  //创建谷歌浏览器
			System.setProperty(ConstantInterface.chromeDriverKey, ConstantInterface.chromeDriverValue);
			WebDriver driver = new ChromeDriver();
			
		 //driver到建设银行的登录网址
			driver.get("http://creditcard.ccb.com/tran/WCCMainPlatV5?CCB_IBSVersion=V5&SERVLET_NAME=WCCMainPlatV5&TXCODE=NE3050");
			String flag="";
			try {
				//进入frame进行操作
				driver.switchTo().frame("itemiframe");
				//driver隐式等待
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				//通过元素进行定位
				WebElement accnoTemp= driver.findElement(By.id("ACC_NO_temp"));
				accnoTemp.click();
				//输入userCode 银行卡号
				accnoTemp.sendKeys(userCode);
				//输入建设银行信用卡查询密码
				driver.findElement(By.id("LOGPASS")).sendKeys(codePass);
				//点击登录按钮
				driver.findElement(By.className("btn_blue")).click();
				//获取整个页面
				String detailedpage1= driver.getPageSource();
				String erro="账号或卡号与账户类型不匹配";
				//进行登录效验
				if(detailedpage1.contains(erro)){
					if(isok==true) {
						PushState.state(userCard, "bankBillFlow",200,"建设银行信用卡账号或卡号与账户类型不匹配");
					}else{
						PushState.statenew(userCard, "bankBillFlow",200,"建设银行信用卡账号或卡号与账户类型不匹配");
					}
					driver.close();
					map.clear();
					map.put("errorInfo","账号或卡号与账户类型不匹配");
					PushSocket.pushnew(map, uuid, "3000","建设银行信用卡账号或卡号与账户类型不匹配");
					map.put("errorCode","0002");
					return map;
				}
				String erro1="您输入的密码不正确";
				if(detailedpage1.contains(erro1)){
					if(isok==true) {
						PushState.state(userCard, "bankBillFlow",200,"建设银行信用卡您输入的密码不正确");
					}else{
						PushState.statenew(userCard, "bankBillFlow",200,"建设银行信用卡您输入的密码不正确");
					}
					driver.close();
					map.clear();
					logger.warn("您输入的密码不正确"+userCode);
					map.put("errorInfo","您输入的密码不正确");
					PushSocket.pushnew(map, uuid, "3000","建设银行信用卡您输入的密码不正确");
					map.put("errorCode","0002");
					return map;
				}
				
				//账单日，可用额度，信用额度，取款额度信息
				System.out.println(driver.findElement(By.className("crd_box")).getText()) ;
				driver.switchTo().frame("result1");
				//获取table ,再获取其中a标签
				WebElement table= driver.findElement(By.className("pbd_table_form"));
				WebElement a= table.findElement(By.tagName("a"));				
				PushSocket.pushnew(map, uuid, "2000","建设银行信用卡登陆成功");
				flag="2000";
				List<String> html =new ArrayList<String>();
				int num=7;
				//开始获取页面元素
				for (int i = 1; i < num; i++) {
					Map<String,Object>map1=new HashMap<String,Object>(200);
					driver.findElement(By.className("select_value")).click();
					driver.findElement(By.xpath("//*[@id='jqueryFrom']/div/table/tbody/tr/td[3]/div/div/ul/li["+i+"]")).click();
					a.click();
					String detailedpage2=driver.getPageSource();
					driver.switchTo().frame("result2");
					String text= driver.getPageSource();
					String detailedpage3= driver.getPageSource();
					html.add(detailedpage1+detailedpage2+detailedpage3);
					driver.switchTo().parentFrame();
					System.out.println("-----------"+i+"--------------");
				}
				PushSocket.pushnew(map, uuid, "5000","建设银行账单获取中");
				flag="5000";
				Map<String,Object> con=new HashMap<String, Object>(200);
				Map<String,Object> data=new HashMap<String,Object>(200);
			  	System.out.println("页面已经放置到html中");
			  	data.put("html", html);
			  	data.put("backtype","CCNB");
			  	data.put("idcard",userCard);
				data.put("userAccount",userCode);
			  	con.put("data", data);
				PushSocket.pushnew(map, uuid, "6000","建设银行账单获取成功");
				logger.warn(map+"========2======="+userCard);
				flag="6000";
				Resttemplate resttemplate = new Resttemplate();
				map=resttemplate.SendMessage(con,applications.getSendip()+"/HSDC/BillFlow/BillFlowByreditCard");
				logger.warn(map+"=======1========"+userCard);
				driver.close();
				String errorCode = "errorCode";
				String state0 = "0000";
				
				if (map != null && state0.equals(map.get(errorCode).toString())){
//						if(isok==true) {
//					    	PushState.state(userCard, "bankBillFlow",300);
//						}
						PushState.stateByFlag(userCard, "savings",300,isok);
		                map.put("errorInfo","推送成功");
		                map.put("errorCode","0000");
		            	PushSocket.pushnew(map, uuid, "8000","建设银行账单认证成功");
		            }else{
		            	//--------------------数据中心推送状态----------------------
		            	logger.warn("建设推送失败"+userCode+map);
		            	if(isok==true) {
		            		PushState.state(userCard, "bankBillFlow",200,"建设银行账单认证失败");
		            	}else{
		            		PushState.statenew(userCard, "bankBillFlow",200,"建设银行账单认证失败");
		            	}
		            	//---------------------数据中心推送状态----------------------
		            	PushSocket.pushnew(map, uuid, "9000","建设银行账单认证失败");

		            } 
			} catch (Exception e) { 
					e.printStackTrace();
					String state = "2000";
					String state1 = "5000";
					String state2 = "6000";
					logger.warn("建设银行账单获取失败"+userCard+e);
					
					if(isok==true) {
						PushState.state(userCard, "bankBillFlow",200,"认证失败");
						if(flag.equals(state)){
							PushSocket.pushnew(map, uuid, "7000","建设银行账单获取失败");
						}else if(flag.equals(state1)){
							PushSocket.pushnew(map, uuid, "7000","建设银行账单获取失败");
						}else if(flag.equals(state2)){
							PushSocket.pushnew(map, uuid, "9000","认证失败");
						}else{
							PushSocket.pushnew(map, uuid, "3000","登录失败");
						}
					}else{
						if(flag.equals(state)){
							PushSocket.pushnew(map, uuid, "7000","建设银行账单获取失败");
							PushState.statenew(userCard, "bankBillFlow",200,"建设银行账单获取失败");
						}else if(flag.equals(state1)){
							PushSocket.pushnew(map, uuid, "7000","建设银行账单获取失败");
							PushState.statenew(userCard, "bankBillFlow",200,"建设银行账单获取失败");
						}else if(flag.equals(state2)){
							PushSocket.pushnew(map, uuid, "9000","认证失败");
							PushState.statenew(userCard, "bankBillFlow",200,"认证失败");
						}else{
							PushSocket.pushnew(map, uuid, "3000","登录失败");
							PushState.statenew(userCard, "bankBillFlow",200,"登录失败");
						}
					}
					driver.close();
					e.printStackTrace();
					map.clear();
					map.put("errorInfo","获取账单失败");
					map.put("errorCode","0002");
					}
			
		return map;
		  
	  }
}
