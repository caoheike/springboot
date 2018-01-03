package com.reptile.service.socialsecurity;


import com.reptile.util.ConstantInterface;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.RobotUntil;
import com.reptile.util.winIO.VirtualKeyBoard;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class GuiYangSocialSecurityService {
    private Logger log = LoggerFactory.getLogger(GuiYangSocialSecurityService.class);

    public Map<String, Object> getDetailMes(HttpServletRequest request, String userCard, String password, String cityCode,String idCardNum) {

        String realPath = request.getServletContext().getRealPath("/imageCode");
        File file = new File(realPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        InternetExplorerDriver driver = null;
        try {
            log.warn("登录贵阳社保");
            System.setProperty(ConstantInterface.ieDriverKey, ConstantInterface.ieDriverValue);
            driver = new InternetExplorerDriver();
            driver.manage().window().maximize();
            driver.get("http://118.112.188.109/nethall/login.jsp");
            Thread.sleep(1000);
            driver.findElementById("login_person").click();
            Thread.sleep(1000);
            driver.findElementById("c_username").sendKeys(userCard);
            Thread.sleep(1000);
            Actions actions = new Actions(driver);
            for (int i = 0; i < 3; i++) {
                actions.sendKeys(Keys.TAB).build().perform();
                Thread.sleep(500);
            }
            for (int i = 0; i < password.length(); i++) {
                VirtualKeyBoard.KeyPress(password.charAt(i));
                Thread.sleep(100);
            }
            WebElement codeimgC = driver.findElementById("codeimgC");
            String imgFileByScreenshot = RobotUntil.getImgFileByScreenshot5(codeimgC, driver, file);
            driver.findElementById("checkCodeC").sendKeys(imgFileByScreenshot.toLowerCase());
            Thread.sleep(1000);
            driver.findElementById("loginBtnC").click();
            Thread.sleep(6000);
            List<WebElement> panel = driver.findElements(By.className("panel"));

            if (panel.size() > 0) {
                String info = panel.get(1).getText();
                if (info.contains("验证码输入错误")) {
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "系统繁忙，请刷新后重试！");
                } else {
                    map.put("errorCode", "0001");
                    map.put("errorInfo", info);
                }
                driver.close();
                return map;
            }
            PushState.state(idCardNum, "socialSecurity", 100);
            log.warn("获取贵阳社保基本信息");
            WebElement if_00 = driver.findElementById("if_00");
            driver.switchTo().frame(if_00);
            Thread.sleep(1000);
            dataMap.put("base", driver.getPageSource());
            log.warn("获取贵阳社保详情");
            driver.switchTo().defaultContent();
            List<WebElement> elements = driver.findElements(By.className("bl-menu"));
            for (int i = 0; i < elements.size(); i++) {
                String text = elements.get(i).getText();
                if (text.contains("养老保险")) {
                    elements.get(i).click();
                    Thread.sleep(3000);
                    break;
                }
            }
            List<WebElement> iframe = driver.findElements(By.tagName("iframe"));
            WebElement webElement = iframe.get(1);
            driver.switchTo().frame(webElement);
            Thread.sleep(1000);
            List<WebElement> button = driver.findElements(By.tagName("button"));
            for (int i = 0; i < button.size(); i++) {
                String text = button.get(i).getText();
                if (text.contains("查询全部")) {
                    System.out.println(button.get(i).getAttribute("id"));
                    button.get(i).click();
                    Thread.sleep(1000);
                    break;
                }
            }
            System.out.println(driver.getPageSource());
            dataMap.put("item", driver.getPageSource());
            map.put("data", dataMap);
            map.put("city", cityCode);
            map.put("userId", idCardNum);
            log.warn("贵阳社保获取成功");
            map = new Resttemplate().SendMessage(map, ConstantInterface.port+"/HSDC/person/socialSecurity");
            
            if(map!=null&&"0000".equals(map.get("errorCode").toString())){
            	PushState.state(idCardNum, "socialSecurity", 300);
            	map.put("errorInfo","推送成功");
            	map.put("errorCode","0000");
            }else{
            	PushState.state(idCardNum, "socialSecurity", 200);
            	map.put("errorInfo","推送失败");
            	map.put("errorCode","0001");
            }
            
        } catch (Exception e) {
            log.warn("贵阳社保获取失败", e);
            e.printStackTrace();
            map.put("errorCode", "0001");
            map.put("errorInfo", "当前网络繁忙，请刷新后重试");
        } finally {
            if (driver != null) {
                driver.close();
            }
        }
        return map;
    }
}
