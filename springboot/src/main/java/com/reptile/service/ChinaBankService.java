package com.reptile.service;

import com.reptile.analysis.ChinaBankanalysis;
import com.reptile.util.*;

import net.sf.json.JSONObject;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 中国银行信用卡
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Service
public class ChinaBankService {
    private Logger logger= LoggerFactory.getLogger(ChinaBankService.class);

    public Map<String, Object> getDetailMes(HttpServletRequest request, String userCard, String cardNumber, String userPwd, String uuid,String timeCnt
    ) throws ParseException {
    	boolean isok = CountTime.getCountTime(timeCnt);
    	System.out.println("isok===="+isok);
        String path = request.getServletContext().getRealPath("/vecImageCode");
        System.setProperty("java.awt.headless", "true");
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        Map<String, Object> map = new HashMap<String, Object>(16);
        System.setProperty(ConstantInterface.chromeDriverKey, ConstantInterface.chromeDriverValue);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        ChromeDriver driver = new ChromeDriver(options);
        driver.get("https://ebsnew.boc.cn/boc15/login.html");

        WebDriverWait wait = new WebDriverWait(driver, 10);
        if(isok==true) {
        	PushState.state(userCard, "bankBillFlow",100);				 
        }
        PushSocket.pushnew(map, uuid, "1000","中国银行登录中");
        String states="1000";
        try {
        	Thread.sleep(3000);
            List<WebElement> input = driver.findElements(By.className("input"));
            for (int i = 0; i < input.size(); i++) {
                if (input.get(i).getAttribute("v") != null && input.get(i).getAttribute("v").contains("用户名")) {
                    input.get(i).sendKeys(cardNumber);
                }
            }
            Actions action = new Actions(driver);
            
            action.sendKeys(Keys.TAB).build().perform();
            Thread.sleep(3000);
            String msgContent = driver.findElement(By.id("msgContent")).getText();
            if (msgContent.length() != 0) {
                map.put("errorCode", "0001");
                map.put("errorInfo", msgContent);
                PushSocket.pushnew(map, uuid, "3000","中国银行信用卡登录失败");
                if(isok==true) {
                    PushState.state(userCard, "bankBillFlow",200,"中国银行信用卡登录失败");
                }else{
                	PushState.statenew(userCard, "bankBillFlow",200,"中国银行信用卡登录失败");
                }
                driver.quit();              
                return map;
            }
            List<WebElement> input1 = driver.findElements(By.tagName("input"));
            for (int i = 0; i < input1.size(); i++) {
                if (input1.get(i).getAttribute("oncopy") != null && input1.get(i).getAttribute("oncopy").contains("return false;")) {
                    if (i == 3) {
                        input1.get(i).sendKeys(userPwd);
                    } else {
                        map.put("errorCode", "0001");
                        map.put("errorInfo", "请使用信用卡号登录");
                        PushSocket.pushnew(map, uuid, "3000","中国银行信用卡登录失败请使用信用卡号登录");
                        if(isok==true) {
                            PushState.state(userCard, "bankBillFlow",200,"中国银行信用卡登录失败请使用信用卡号登录");
                        }else{
                        	PushState.statenew(userCard, "bankBillFlow",200,"中国银行信用卡登录失败请使用信用卡号登录");
                        }
                        driver.quit();                       
                        return map;
                    }
                }
            }
            WebElement imageCode = null;
            try {
                imageCode = driver.findElement(By.id("captcha_creditCard"));
            } catch (Exception e) {
                logger.warn("中信银行信用卡卡号错误",e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "请输入正确的信用卡号");
                PushSocket.pushnew(map, uuid, "3000","中国银行信用卡登录失败请输入正确的信用卡号");
                if(isok==true) {
                    PushState.state(userCard, "bankBillFlow",200,"中国银行信用卡登录失败请输入正确的信用卡号");
                }else{
                	PushState.statenew(userCard, "bankBillFlow",200,"中国银行信用卡登录失败请输入正确的信用卡号");
                }
                driver.quit();              
                return map;
            }
            String code =RobotUntil.getImgFileByScreenshot(imageCode, driver, file);
            if(code.length()==0){
            	code="5210";
            }
            
            List<WebElement> input2 = driver.findElements(By.className("input"));
            for (int i = 0; i < input2.size(); i++) {
                if (input2.get(i).getAttribute("v") != null && input2.get(i).getAttribute("v").contains("验证码") &&
                        input2.get(i).getAttribute("tips") != null && input2.get(i).getAttribute("tips").contains("tipsmax tipsmin")) {
                    input2.get(i).sendKeys(code);
                }
            }


            List<WebElement> elements = driver.findElements(By.className("btn"));
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).getText().contains("查询")) {
                    elements.get(i).click();
                    break;
                }
            }
            Thread.sleep(4000);
            msgContent = driver.findElement(By.id("msgContent")).getText();
            if (msgContent.length() != 0) {
                String yanZhengMa="验证码输入错误";
                if (msgContent.contains(yanZhengMa)) {
                    map.put("errorCode", "0004");
                    map.put("errorInfo", "当前系统繁忙，请刷新页面重新认证！");
                    PushSocket.pushnew(map, uuid, "3000","中国银行信用卡登录失败当前系统繁忙，请刷新页面重新认证！");
                    if(isok==true) {
                        PushState.state(userCard, "bankBillFlow",200,"中国银行信用卡登录失败当前系统繁忙，请刷新页面重新认证！");
                    }else{
                        PushState.statenew(userCard, "bankBillFlow",200,"中国银行信用卡登录失败当前系统繁忙，请刷新页面重新认证！");
                    }

                } else {
                    map.put("errorCode", "0001");
                    map.put("errorInfo", msgContent);
                    PushSocket.pushnew(map, uuid, "3000",msgContent);
                    if(isok==true) {
                        PushState.state(userCard, "bankBillFlow",200,msgContent);
                    }else{
                        PushState.statenew(userCard, "bankBillFlow",200,msgContent);
                    }
                }
                PushSocket.pushnew(map, uuid, "3000","中国银行信用卡登录失败当前系统繁忙，请刷新页面重新认证！");
                if(isok==true) {
                    PushState.state(userCard, "bankBillFlow",200,"中国银行信用卡登录失败当前系统繁忙，请刷新页面重新认证！");
                }else{
                	PushState.statenew(userCard, "bankBillFlow",200,"中国银行信用卡登录失败当前系统繁忙，请刷新页面重新认证！");
                }
                driver.quit();
                return map;
            }

            boolean contains = driver.getPageSource().contains("用户名/银行卡号");
            if(contains){
            	map.put("errorCode", "0001");
                map.put("errorInfo", "登录失败，系统繁忙");
                PushSocket.pushnew(map, uuid, "3000","登录失败，系统繁忙");
                if(isok==true) {
    				PushState.state(userCard, "bankBillFlow",200,"登录失败，系统繁忙");
    			}else{
    				PushState.statenew(userCard, "bankBillFlow",200,"登录失败，系统繁忙");
    			}
                driver.quit();
                return map;
            }
            
            PushSocket.pushnew(map, uuid, "2000","中国银行信用卡登录成功");
            Thread.sleep(2000);
            PushSocket.pushnew(map, uuid, "5000","中国银行信用卡获取中");
            states="5000";
            Thread.sleep(1000);
            List<WebElement> element = driver.findElements(By.className("tabs"));
            for (int i = 0; i < element.size(); i++) {
                if (element.get(i).getText().contains("已出账单")) {
                    element.get(i).click();
                }
            }
            Thread.sleep(2000);
            List<WebElement> listDom = driver.findElements(By.className("sel"));
            String id = "";
            for (int i = 0; i < listDom.size(); i++) {
                if (listDom.get(i).getAttribute("tips") != null && "tipsrequired".equals(listDom.get(i).getAttribute("tips"))) {
                    id = listDom.get(i).getAttribute("id");
                }
            }
            String count = String.valueOf(driver.executeScript("return $(\"#" + id + " ul li\").length;"));
            List<String> listData = new ArrayList<String>();
            JSONObject date=new JSONObject();
            for (int i = 1; i < Integer.valueOf(count) + 1; i++) {
                driver.findElement(By.xpath("//*[@id='" + id + "']/span")).click();
                Thread.sleep(1000);
                driver.findElement(By.xpath("//*[@id='" + id + "']/ul/li[" + i + "]/a")).click();
                Thread.sleep(1000);
                System.out.println("driver.getPageSource()=="+i+","+driver.getPageSource());
                List<WebElement> btn = driver.findElements(By.className("btn"));
                for (int j = 0; j < btn.size(); j++) {
                    if ("查询".equals(btn.get(j).getText())) {
                        btn.get(j).click();
                    }
                }

                Thread.sleep(2000);
                String pageSource = driver.getPageSource();
                listData.add(pageSource);
                //数据解析
                //date=ChinaBankanalysis.billanalysis(pageSource);
            }
            PushSocket.pushnew(map, uuid, "6000","中国银行信用卡获取成功");
            states="4000";
            //--------------数据解析后的-------
            /*map.put("idcard", userCard);
            map.put("backtype", "BOC");
            map.put("userAccount",cardNumber);
            map.put("bankname", "中国银行信用卡");
            map.put("date", date);
            map.put("isok", isok);
            System.out.println("中国银行信用卡map==="+map);*/
            //--------------数据解析后的-------
            Map<String, Object> sendMap = new HashMap<String, Object>(16);
            sendMap.put("idcard", userCard);
            sendMap.put("backtype", "BOC");
            sendMap.put("html", listData);
            sendMap.put("userAccount",cardNumber);
            map.put("data", sendMap);
            map = new Resttemplate().SendMessage(map, ConstantInterface.port + "/HSDC/BillFlow/BillFlowByreditCard");
            driver.quit();
            String ling="0000";
            String codeResult="errorCode";
            if(map!=null&&ling.equals(map.get(codeResult).toString())){
		    	if(isok==true) {
		    		PushState.state(userCard, "bankBillFlow",300);
		    	}
                map.put("errorInfo","查询成功");
                map.put("errorCode","0000");
                PushSocket.pushnew(map, uuid, "8000","中国银行信用卡认证成功");
            }else{
            	//--------------------数据中心推送状态----------------------
            	if(isok==true) {
					PushState.state(userCard, "bankBillFlow",200,"中国银行信用卡认证失败");
				}	else{
					PushState.statenew(userCard, "bankBillFlow",200,"中国银行信用卡认证失败");
				}	            	//---------------------数据中心推送状态----------------------
            	logger.warn("中国银行账单推送失败"+userCard);
            	 PushSocket.pushnew(map, uuid, "9000","中国银行信用卡认证失败");
            }
        } catch (Exception e) {
            logger.warn("中国银行信用卡认证失败",e);
            String signle1="1000";
            String signle2="5000";
            String signle3="4000";
            DealExceptionSocketStatus.pushExceptionSocket(states,map,uuid);
            if(isok==true) {
				if(states.equals(signle1)){
					PushState.state(userCard, "bankBillFlow",200,"登录失败,网络繁忙");
				}
				if(states.equals(signle3)){
					PushState.state(userCard, "bankBillFlow",200,"中国银行信用卡认证失败");
				}
				if(states.equals(signle2)){
					PushState.state(userCard, "bankBillFlow",200,"中国银行信用卡信息获取失败");
				}
			}else{
				if(states.equals(signle1)){
					PushState.statenew(userCard, "bankBillFlow",200,"登录失败,网络繁忙");
				}
				if(states.equals(signle3)){
					PushState.statenew(userCard, "bankBillFlow",200,"中国银行信用卡认证失败");
				}
				if(states.equals(signle2)){
					PushState.statenew(userCard, "bankBillFlow",200,"中国银行信用卡信息获取失败");
				}

			}
            driver.quit();
            map.put("errorCode", "0002");
            map.put("errorInfo", "网络繁忙");       
        }
        return map;
    }
}
