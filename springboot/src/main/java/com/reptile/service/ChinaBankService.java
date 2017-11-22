package com.reptile.service;

import com.reptile.util.ConstantInterface;
import com.reptile.util.PushSocket;
import com.reptile.util.Resttemplate;
import com.reptile.util.RobotUntil;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChinaBankService {
    private Logger logger= LoggerFactory.getLogger(ChinaBankService.class);

    public Map<String, Object> getDetailMes(HttpServletRequest request, String userCard, String cardNumber, String userPwd, String UUID
    ) {
        String path = request.getServletContext().getRealPath("/vecImageCode");
        System.setProperty("java.awt.headless", "true");
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        System.setProperty("webdriver.chrome.driver", "D:\\ie\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        ChromeDriver driver = new ChromeDriver(options);
        driver.get("https://ebsnew.boc.cn/boc15/login.html");

        WebDriverWait wait = new WebDriverWait(driver, 10);
        try {
            List<WebElement> input = driver.findElements(By.className("input"));
            for (int i = 0; i < input.size(); i++) {
                if (input.get(i).getAttribute("v") != null && input.get(i).getAttribute("v").contains("用户名")) {
                    input.get(i).sendKeys(cardNumber);
                }
            }
            Thread.sleep(1000);
            Actions action = new Actions(driver);
            action.sendKeys(Keys.TAB).build().perform();
            Thread.sleep(3000);
            String msgContent = driver.findElement(By.id("msgContent")).getText();
            if (msgContent.length() != 0) {
                map.put("errorCode", "0001");
                map.put("errorInfo", msgContent);
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
                driver.quit();
                return map;
            }
            String code = new RobotUntil().getImgFileByScreenshot(imageCode, driver, file);

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
            Thread.sleep(5000);
            msgContent = driver.findElement(By.id("msgContent")).getText();
            if (msgContent.length() != 0) {
                if (msgContent.contains("验证码输入错误")) {
                    map.put("errorCode", "0004");
                    map.put("errorInfo", "当前系统繁忙，请刷新页面重新认证！");
                } else {
                    map.put("errorCode", "0001");
                    map.put("errorInfo", msgContent);
                }
                driver.quit();
                return map;
            }
            //--------------推-----------------
            PushSocket.push(map, UUID, "0000");
            //--------------推-----------------
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
                if (listDom.get(i).getAttribute("tips") != null && listDom.get(i).getAttribute("tips").equals("tipsrequired")) {
                    id = listDom.get(i).getAttribute("id");
                }
            }
            String count = String.valueOf(driver.executeScript("return $(\"#" + id + " ul li\").length;"));
            System.out.println("mrludw    " + count);
            List<String> listData = new ArrayList<String>();
            for (int i = 1; i < Integer.valueOf(count) + 1; i++) {
                driver.findElement(By.xpath("//*[@id='" + id + "']/span")).click();
                Thread.sleep(1000);
                driver.findElement(By.xpath("//*[@id='" + id + "']/ul/li[" + i + "]/a")).click();
                Thread.sleep(1000);

                List<WebElement> btn = driver.findElements(By.className("btn"));
                for (int j = 0; j < btn.size(); j++) {
                    if (btn.get(j).getText().equals("查询")) {
                        btn.get(j).click();
                    }
                }

                Thread.sleep(3000);
                String pageSource = driver.getPageSource();
                listData.add(pageSource);
            }
            Map<String, Object> sendMap = new HashMap<String, Object>();
            sendMap.put("idcard", userCard);
            sendMap.put("backtype", "BOC");
            sendMap.put("html", listData);
            map.put("data", sendMap);
            map = new Resttemplate().SendMessage(map, ConstantInterface.port + "/HSDC/BillFlow/BillFlowByreditCard");
            driver.quit();
        } catch (Exception e) {
            logger.warn("中信银行信用卡认证失败",e);
            driver.quit();
            map.put("errorCode", "0002");
            map.put("errorInfo", "网络繁忙");
        }
        return map;
    }
}