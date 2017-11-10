package com.reptile.service.depositCard;

import com.reptile.util.Resttemplate;
import com.reptile.util.RobotUntil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class ChinaBankDepositCardService {
    private Logger logger = LoggerFactory.getLogger(ChinaBankDepositCardService.class);

    public Map<String, Object> getDetailMes(HttpServletRequest request, String IDNumber, String cardNumber, String passWord, String userName) {
        Map<String, Object> map = new HashMap<>();
        List<String> dataList = new ArrayList<>();
        String path = request.getServletContext().getRealPath("/vecImageCode");
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        logger.warn("中国银行储蓄卡登录...");
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\iedriver\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        ChromeDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://ebsnew.boc.cn/boc15/login.html");
            List<WebElement> input = driver.findElementsByTagName("input");
            input.get(0).sendKeys(cardNumber);                      //输入卡号
            Thread.sleep(2000);
            Actions actions = new Actions(driver);
            actions.sendKeys(Keys.TAB).build().perform();
            Thread.sleep(3000);

            String msgContent = driver.findElement(By.id("msgContent")).getText();  //判断是否为有效卡号
            if (msgContent.length() != 0) {
                map.put("errorCode", "0001");
                map.put("errorInfo", msgContent);
                driver.close();
                return map;
            }

            input = driver.findElementsByTagName("input");
            input.get(4).sendKeys(passWord);                        //输入密码
            Thread.sleep(1000);

            WebElement imageCode;
            try {
                imageCode = driver.findElement(By.id("captcha_debitCard"));
            } catch (Exception e) {
                map.put("errorCode", "0002");
                map.put("errorInfo", "请输入正确的储蓄卡号");
                driver.close();
                return map;
            }

            String code = new RobotUntil().getImgFileByScreenshot(imageCode, driver, file);  //识别验证码
            input.get(5).sendKeys(code.toLowerCase());                                      //输入验证码

            List<WebElement> elements = driver.findElements(By.className("btn"));
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).getText().contains("查询")) {
                    System.out.println(i);
                    elements.get(i).click();                                                 //提交信息登录
                    break;
                }
            }

            Thread.sleep(15000);

            msgContent = driver.findElement(By.id("msgContent")).getText();                  //判断登录是否成功
            if (msgContent.length() != 0) {
                if (msgContent.contains("验证码输入错误")) {
                    map.put("errorCode", "0003");
                    map.put("errorInfo", "当前系统繁忙，请刷新页面重新认证！");
                } else {
                    map.put("errorCode", "0004");
                    map.put("errorInfo", msgContent);
                }
                driver.close();
                return map;
            }

            logger.warn("中国银行储蓄卡登录成功");

            WebElement cardMain = driver.findElementById("cardMain");
            map.put("baseMes", cardMain.getAttribute("innerHTML"));                             //储蓄卡基本信息

            logger.warn("中国银行储蓄卡基本信息获取成功");

            List<WebElement> element = driver.findElements(By.className("tabs"));                   //切换至交易明细
            for (int i = 0; i < element.size(); i++) {
                if (element.get(i).getText().contains("交易明细")) {
                    element.get(i).click();
                }
            }

            WebElement debitCardTransDetail_table = null;
            Thread.sleep(2000);
            SimpleDateFormat sim = new SimpleDateFormat("yyyy/MM/dd");
            Calendar cal = Calendar.getInstance();
            String endTime = sim.format(cal.getTime());
            cal.set(Calendar.DAY_OF_MONTH, 1);
            String beginTime = sim.format(cal.getTime());
            for (int i = 0; i < 6; i++) {                                                       //循环获取6个月的账单信息
                driver.findElementsByClassName("input").get(0).clear();
                driver.findElementsByClassName("input").get(0).sendKeys(beginTime);      //设置查询开始时间
                driver.findElementsByClassName("input").get(1).clear();
                driver.findElementsByClassName("input").get(1).sendKeys(endTime);        //设置查询结束时间
                driver.findElementsByClassName("ml10").get(1).click();
                List<WebElement> btn = driver.findElements(By.className("btn-r"));
                for (int j = 0; j < btn.size(); j++) {
                    if (btn.get(j).getText().equals("查询")) {
                        btn.get(j).click();
                    }
                }
                Thread.sleep(2000);
                String msgContent1 = driver.findElementById("msgContent").getText();

                //判断当月是否有账单信息
                if (msgContent1.length() != 0) {
                    driver.findElementsByClassName("btn-r").get(4).click();
//                String str=  driver.findElementById("msgContent").getText();
//                System.out.println(str);
                } else {
                    debitCardTransDetail_table = driver.findElementById("debitCardTransDetail_table");
                    dataList.add(debitCardTransDetail_table.getAttribute("innerHTML"));
                }
                cal.add(Calendar.DAY_OF_MONTH, -1); //上月末
                endTime = sim.format(cal.getTime());
                cal.set(Calendar.DAY_OF_MONTH, 1);           //上月初
                beginTime = sim.format(cal.getTime());
            }

            logger.warn("中国银行储蓄卡账单信息获取成功");

            map.put("itemMes", dataList);
            map = analyData(map);                           //解析获得的数据

            logger.warn("中国银行储蓄卡信息解析成功");

            map.put("IDNumber", IDNumber);
            map.put("cardNumber", cardNumber);
            map.put("userName", userName);
            map.put("bankName", "中国银行");
            map = new Resttemplate().SendMessage(map, "http://192.168.3.4:8081/HSDC/savings/authentication");  //推送数据

            logger.warn("中国银行储蓄卡账单信息推送完成");
            driver.close();
        } catch (Exception e) {
            logger.warn("认证中guo储蓄卡出错", e);
            driver.close();
            map.put("errorCode", "0003");
            map.put("errorInfo", "系统异常");
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
            Document parse = Jsoup.parse(itemMes.get(index));
            Elements tbody = parse.getElementsByTag("tbody");
            if(tbody==null||tbody.size()==0){
                break;
            }
            Elements tr = tbody.get(0).getElementsByTag("tr");
            for (int i = 0; i < tr.size(); i++) {
                Element element = tr.get(i);
                Elements td = element.getElementsByTag("td");
                detailMap = new HashMap<>();

                detailMap.put("dealTime", td.get(0).text());
                detailMap.put("dealReferral", td.get(1).text());
                detailMap.put("oppositeSideName", td.get(2).text());
                detailMap.put("oppositeSideNumber", td.get(3).text());
                detailMap.put("currency", td.get(4).text());
                detailMap.put("incomeMoney", td.get(6).text());
                detailMap.put("expendMoney", td.get(7).text());
                detailMap.put("balanceAmount", td.get(8).text());
                detailMap.put("dealDitch", td.get(9).text());
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
        Elements elementsByClass = parse.getElementsByClass("layout-lr");
        Elements li = elementsByClass.get(0).getElementsByTag("li");
        map.put("accountType", li.get(0).getElementsByClass("item-con").text());
        map.put("openBranch", li.get(1).getElementsByClass("item-con").text());
        map.put("openTime", li.get(2).getElementsByClass("item-con").text());
        return map;
    }

}
