package com.reptile.service.depositCard;

import com.reptile.util.ConstantInterface;
import com.reptile.util.Resttemplate;
import com.reptile.util.winIO.VirtualKeyBoard;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;

import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ZXBankDepositCardService {
    private Logger logger = LoggerFactory.getLogger(ZXBankDepositCardService.class);

    public Map<String, Object> getDetailMes(HttpServletRequest request, String IDNumber, String cardNumber, String userName, String passWord) {
        Map<String, Object> map = new HashMap<>();
        System.setProperty("webdriver.ie.driver", "C:\\Program Files (x86)\\iedriver\\IEDriverServer.exe");
        InternetExplorerDriver driver = new InternetExplorerDriver();
        driver.manage().window().maximize();
        try {
            logger.warn("登录中信银行网上银行");
            driver.get("https://i.bank.ecitic.com/perbank6/signIn.do");
            Thread.sleep(3000);
            //输入账户名密码
            driver.findElementByName("logonNoCert").sendKeys(cardNumber);
            Actions actions = new Actions(driver);
            actions.sendKeys(Keys.TAB).build().perform();
            Thread.sleep(1000);
            for (int i = 0; i < passWord.length(); i++) {
                VirtualKeyBoard.KeyPress(passWord.charAt(i));
                Thread.sleep(200);
            }

            //登录
            driver.findElementById("logonButton").click();
            Thread.sleep(3000);

            //判断是否登录成功
            try {
                WebElement errorReason = driver.findElementByClassName("errorReason");
                logger.warn("登录失败！" + errorReason.getText());
                map.put("errorCode", "0002");
                map.put("errorInfo", errorReason.getText());
                driver.quit();
                return map;
            } catch (NoSuchElementException e) {
                logger.warn("登录成功");
            }catch (UnhandledAlertException e){
                map.put("errorCode", "0002");
                map.put("errorInfo", "账号或密码格式不正确！");
                driver.quit();
                return map;
            }
            logger.warn("获取账单详情...");
            //获取类似于tooken的标识
            String EMP_SID = driver.findElementByName("infobarForm").findElement(By.name("EMP_SID")).getAttribute("value");
            //将mainframe切换到详单页面
            driver.executeScript("document.getElementById(\"mainframe\").src=\"https://i.bank.ecitic.com/perbank6/pb1310_account_detail_query.do?EMP_SID=" + EMP_SID + "\" ");
            Thread.sleep(2000);
            WebElement mainframe = driver.findElementById("mainframe");
            driver.switchTo().frame(mainframe);
            Thread.sleep(1000);

            driver.findElement(By.id("spacilOpenDiv")).click();  //打开自定义查询

            //移除时间输入框的readOnly属性
            driver.executeScript("document.getElementById('beginDate').removeAttribute('readonly');document.getElementById('endDate').removeAttribute('readonly');");
            driver.findElement(By.id("beginDate")).clear();
            driver.findElement(By.id("endDate")).clear();

            //查询开始时间，结束时间（当前时间前两天）设置
            SimpleDateFormat sim = new SimpleDateFormat("yyyyMMdd");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -2);
            String endTime = sim.format(cal.getTime());
            cal.set(Calendar.DAY_OF_MONTH, 1);
            String beginTime = sim.format(cal.getTime());

            //循环获取6个月的账单信息
            List<String> dataList = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                driver.findElement(By.id("beginDate")).clear();
                driver.findElement(By.id("endDate")).clear();
                driver.findElement(By.id("beginDate")).sendKeys(beginTime);
                driver.findElement(By.id("endDate")).sendKeys(endTime);
                driver.findElementById("searchButton").click();
                Thread.sleep(1000);

                String attribute = driver.findElementById("resultTable1").getAttribute("innerHTML");
                dataList.add(attribute);

                cal.add(Calendar.DAY_OF_MONTH, -1); //上月末
                endTime = sim.format(cal.getTime());
                cal.set(Calendar.DAY_OF_MONTH, 1);           //上月初
                beginTime = sim.format(cal.getTime());
            }
            map.put("itemMes", dataList);
            logger.warn("获取账单详情成功");
            logger.warn("获取基本信息");
            //发包获取基本信息
            driver.get("https://i.bank.ecitic.com/perbank6/pb1110_query_detail.do?EMP_SID=" + EMP_SID + "&accountNo=" + cardNumber + "&index=0ff0 ");
            Thread.sleep(1000);
            String baseMes = driver.getPageSource();
            map.put("baseMes", baseMes);
            map = analyData(map);
            map.put("IDNumber", IDNumber);
            map.put("cardNumber", cardNumber);
            map.put("userName", userName);
            map.put("bankName", "中信银行");
            logger.warn("中信银行数据推送...");
            map = new Resttemplate().SendMessage(map, ConstantInterface.port+"/HSDC/savings/authentication");  //推送数据
            logger.warn("中信银行数据推送成功");
            driver.quit();//关闭浏览器
        } catch (Exception e) {
            driver.quit();
            logger.warn("中信银行认证失败", e);
            map=new HashMap<>();
            map.put("errorCode", "0001");
            map.put("errorInfo", "网络请求异常，请稍后再试");
        }
        return map;
    }

    /**
     * 解析从页面获取到的数据并封装
     *
     * @param
     * @return
     */
    private Map<String, Object> analyData(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> map = new HashMap<>();

        List<String> itemMes = (List<String>) paramMap.get("itemMes");  //账单信息
        String baseMes = paramMap.get("baseMes").toString();            //基本信息

        if (itemMes.size() == 0) {
            map.put("errorCode", "1001");
            map.put("errorInfo", "账单信息为空");
            return map;
        }
        List billList;                                                   //解析后账单信息
        Map<String, Object> baseMap;                             //解析后基本信息
        try {
            billList = analyBillMethod(itemMes);                         //解析账单信息
            baseMap = analyBaseMes(baseMes);                     //解析基本信息
        } catch (Exception e) {
            logger.warn("数据解析失败", e);
            throw new Exception("数据解析失败");
        }
        map.put("baseMes", baseMap);
        map.put("billMes", billList);
        return map;
    }


    /**
     * 解析账单信息
     *
     * @param itemMes
     * @return
     */
    private List analyBillMethod(List<String> itemMes) throws Exception {
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> detailMap;  //存放当月的交易详情

        for (int index = 0; index < itemMes.size(); index++) {
            String s = itemMes.get(index).replaceAll("tbody", "table");
            Document parse = Jsoup.parse(s);
            Elements tbody = parse.getElementsByTag("table");
            Elements tr = tbody.get(0).getElementsByTag("tr");
            for (int i = 0; i < tr.size(); i++) {
                Elements td = tr.get(i).getElementsByTag("td");
                detailMap = new HashMap<>();
                detailMap.put("dealTime", td.get(2).text());
                detailMap.put("expendMoney", td.get(3).text());
                detailMap.put("incomeMoney", td.get(4).text());
                detailMap.put("balanceAmount", td.get(5).text());
                detailMap.put("oppositeSideName", td.get(6).text());
                detailMap.put("dealDitch", td.get(7).text());
                detailMap.put("dealReferral", td.get(8).text());
                dataList.add(detailMap);
            }

        }
        return dataList;
    }

    /**
     * 解析基本信息
     *
     * @param baseMes
     * @return
     */
    private Map<String, Object> analyBaseMes(String baseMes) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Document parse = Jsoup.parse(baseMes);
        System.out.println(parse.text());
        Elements tbody = parse.getElementsByTag("tbody");
        Elements td = tbody.get(0).getElementsByTag("td");

        map.put("accountType", td.get(9).text());
        map.put("openBranch", "");
        map.put("openTime", td.get(5).text());
        return map;
    }
}
