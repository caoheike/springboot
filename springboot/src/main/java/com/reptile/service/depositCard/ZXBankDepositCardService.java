package com.reptile.service.depositCard;

import com.reptile.util.winIO.VirtualKeyBoard;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;

import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

@Service
public class ZXBankDepositCardService {

    public Map<String, Object> getDetailMes() throws Exception {
        String cardNumber = "6217731702148491";
        System.setProperty("webdriver.ie.driver", "C:\\Program Files (x86)\\iedriver\\IEDriverServer.exe");
        InternetExplorerDriver driver = new InternetExplorerDriver();
        driver.manage().window().maximize();
        driver.get("https://i.bank.ecitic.com/perbank6/signIn.do");
        Thread.sleep(3000);
        System.out.println(driver.getPageSource());
        driver.findElementByName("logonNoCert").sendKeys(cardNumber);
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.TAB).build().perform();
        Thread.sleep(1000);
        String pwd = "peng0502";
        for (int i = 0; i < pwd.length(); i++) {
            VirtualKeyBoard.KeyPress(pwd.charAt(i));
        }
        driver.findElementById("logonButton").click();
        Thread.sleep(3000);
        String s = driver.manage().getCookies().toString();
        String substring = s.substring(1, s.length() - 1);


        //获取类似于tooken的标识
        String EMP_SID = driver.findElementByName("infobarForm").findElement(By.name("EMP_SID")).getAttribute("value");

        //将mainframe切换到详单页面
        driver.executeScript("document.getElementById(\"mainframe\").src=\"https://i.bank.ecitic.com/perbank6/pb1310_account_detail_query.do?EMP_SID="+EMP_SID+"\" ");
        Thread.sleep(3000);
        WebElement mainframe = driver.findElementById("mainframe");
        driver.switchTo().frame(mainframe);
        Thread.sleep(2000);
        System.out.println(driver.getPageSource());

        //发包获取基本信息
        driver.get("https://i.bank.ecitic.com/perbank6/pb1110_query_detail.do?EMP_SID=" + EMP_SID + "&accountNo=" + cardNumber + "&index=0ff0 ");
        Thread.sleep(3000);
        String baseMes = driver.getPageSource();
        return null;
    }

    public static void main(String[] args) throws Exception {
        new ZXBankDepositCardService().getDetailMes();
    }
}
