package com.reptile.service.depositcard;

import com.reptile.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import netscape.javascript.JSObject;

import org.apache.commons.collections.map.HashedMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 中国银行储蓄卡
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Service
public class ChinaBankDepositCardService {
    private Logger logger = LoggerFactory.getLogger(ChinaBankDepositCardService.class);

    /**
     * 中国银行储蓄卡账单获取
     * @param request
     * @param idNumber
     * @param cardNumber
     * @param passWord
     * @param userName
     * @return
     */
    @SuppressWarnings("AlibabaUndefineMagicConstant")
    public Map<String, Object> getDetailMes(HttpServletRequest request, String idNumber, String cardNumber, String passWord, String userName, String uuid,boolean flag0) {
        Map<String, Object> map = new HashMap<>(16);
        PushSocket.pushnew(map, uuid, "1000","登录中");
        String flag="1000";
        PushState.stateByFlag(idNumber, "savings",100,flag0);
        List<String> dataList = new ArrayList<>();
        String path = request.getServletContext().getRealPath("/vecImageCode");
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        logger.warn("中国银行储蓄卡登录...");
        System.setProperty(ConstantInterface.chromeDriverKey, ConstantInterface.chromeDriverValue);
        System.out.println("~~");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        ChromeDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        try {
            driver.get("https://ebsnew.boc.cn/boc15/login.html");
            List<WebElement> input = driver.findElementsByTagName("input");
            //输入卡号
            input.get(0).sendKeys(cardNumber);
            Thread.sleep(1000);
            Actions actions = new Actions(driver);
            actions.sendKeys(Keys.TAB).build().perform();
            Thread.sleep(3000);
            //判断是否为有效卡号
            String msgContent = driver.findElement(By.id("msgContent")).getText();
            if (msgContent.length() != 0) {
                map.put("errorCode", "0001");
                map.put("errorInfo", msgContent);
                PushState.stateByFlag(idNumber, "savings",200,msgContent,flag0);
                PushSocket.pushnew(map, uuid, "3000",msgContent);
                driver.quit();
                return map;
            }
            WebElement imageCode;
            try {
                imageCode = driver.findElement(By.id("captcha_debitCard"));
            } catch (Exception e) {
                map.put("errorCode", "0002");
                map.put("errorInfo", "请输入正确的储蓄卡号");
                PushState.stateByFlag(idNumber, "savings",200,"请输入正确的储蓄卡号",flag0);
                PushSocket.pushnew(map, uuid, "3000","请输入正确的储蓄卡号");
                driver.quit();
                return map;
            }
            //输入密码
            input = driver.findElementsByTagName("input");
            input.get(4).sendKeys(passWord);
            Thread.sleep(1000);

            //识别验证码
            String code = RobotUntil.getImgFileByScreenshot(imageCode, driver, file);
            if(code.length()==0){
                code="5210";
            }
            //输入验证码
            input.get(5).sendKeys(code.toLowerCase());
            //提交信息登录
            List<WebElement> elements = driver.findElements(By.className("btn"));
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).getText().contains("查询")) {
                    elements.get(i).click();
                    break;
                }
            }
            Thread.sleep(5000);
            //判断登录是否成功
            msgContent = driver.findElement(By.id("msgContent")).getText();
            if (msgContent.length() != 0) {
                String validateStr="验证码输入错误";
                if (msgContent.contains(validateStr)) {
                    map.put("errorCode", "0003");
                    map.put("errorInfo", "当前系统繁忙，请刷新页面重新认证！");
                    PushSocket.pushnew(map, uuid, "3000","当前系统繁忙，请刷新页面重新认证！");
                    PushState.stateByFlag(idNumber, "savings",200,"当前系统繁忙，请刷新页面重新认证！",flag0);
                } else {
                    map.put("errorCode", "0004");
                    map.put("errorInfo", msgContent);
                    PushSocket.pushnew(map, uuid, "3000",msgContent);
                    PushState.stateByFlag(idNumber, "savings",200,msgContent,flag0);
                }
                driver.quit();
                return map;
            }

            boolean contains = driver.getPageSource().contains("用户名/银行卡号");
            if(contains){
                map.put("errorCode", "0001");
                map.put("errorInfo", "登录失败，系统繁忙");
                PushSocket.pushnew(map, uuid, "3000","登录失败，系统繁忙");
                driver.quit();
                PushState.stateByFlag(idNumber, "savings",200,"登录失败，系统繁忙",flag0);
                return map;
            }

            PushSocket.pushnew(map, uuid, "2000","登录成功");
            Thread.sleep(2000);
            PushSocket.pushnew(map, uuid, "5000","数据获取中");
            flag="5000";
            logger.warn("中国银行储蓄卡登录成功");

            WebElement cardMain = driver.findElementById("cardMain");
            map.put("baseMes", cardMain.getAttribute("innerHTML"));
            logger.warn("中国银行储蓄卡基本信息获取成功");
            //获取详单信息
            /*dataList = getItemMes(dataList, driver);
             map.put("itemMes", dataList);
            //解析获得的数据
            map = analyData(map);*/


            //--------获取详单信息  发包的--------------------------------------------
            //6217853600031823866    521888
            JSONArray  dateArray= getItemMesfb(driver);
            map.put("itemMes", dateArray);
            //解析获得的数据
            map = analyDatafb(map);
            //--------获取详单信息  发包的结束-----------------------------------------

            logger.warn("中国银行储蓄卡信息解析成功");
            System.out.println("最后的数据--------"+map);
            map.put("IDNumber", idNumber);
            map.put("cardNumber", cardNumber);
            map.put("userName", userName);
            map.put("userAccount", cardNumber);
            map.put("bankName", "中国银行");
            //推送数据
            PushSocket.pushnew(map, uuid, "6000","数据获取成功");
            flag="4000";
            map = new Resttemplate().SendMessage(map, ConstantInterface.port+"/HSDC/savings/authentication");
            String resultValidate="0000";
            String resultCode="errorCode";
            if(map!=null&&resultValidate.equals(map.get(resultCode).toString())){
                PushState.stateByFlag(idNumber, "savings",300,flag0);
                PushSocket.pushnew(map, uuid, "8000","认证成功");
            }else {
                PushState.stateByFlag(idNumber, "savings",200,"认证失败",flag0);
                PushSocket.pushnew(map, uuid, "9000","认证失败");
            }
            logger.warn("中国银行储蓄卡账单信息推送完成");
            driver.quit();
        } catch (Exception e) {
            map.clear();
            logger.warn("认证中国储蓄卡出错", e);
            driver.quit();
            map.put("errorCode", "0003");
            map.put("errorInfo", "系统异常");
            PushState.stateByFlag(idNumber, "savings",200, "系统异常",flag0);
            DealExceptionSocketStatus.pushExceptionSocket(flag,map,uuid);
        }
        return map;
    }


    /**
     *
     * 获取账单详情信息
     * @param dataList
     * @param driver
     * @return
     * @throws InterruptedException
     */

    @SuppressWarnings("AlibabaUndefineMagicConstant")
    public  List<String> getItemMes(List<String> dataList, ChromeDriver driver) throws Exception {
        //切换至交易明细
        List<WebElement> element = driver.findElements(By.className("tabs"));
        for (int i = 0; i < element.size(); i++) {
            if (element.get(i).getText().contains("交易明细")) {
                element.get(i).click();
            }
        }
        WebElement debitCardTransDetailTable = null;
        Thread.sleep(2000);
        SimpleDateFormat sim = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        String endTime = sim.format(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String beginTime = sim.format(cal.getTime());
        //循环获取6个月的账单信息
        int boundCount=6;
        for (int i = 0; i < boundCount; i++) {
            //设置查询开始时间
            driver.findElementsByClassName("input").get(0).clear();
            driver.findElementsByClassName("input").get(0).sendKeys(beginTime);
            //设置查询结束时间
            driver.findElementsByClassName("input").get(1).clear();
            driver.findElementsByClassName("input").get(1).sendKeys(endTime);
            driver.findElementsByClassName("ml10").get(1).click();
            List<WebElement> btn = driver.findElements(By.className("btn-r"));
            for (int j = 0; j < btn.size(); j++) {
                if ("查询".equals(btn.get(j).getText())) {
                    btn.get(j).click();
                }
            }
            Thread.sleep(2000);
            String msgContent1 = driver.findElementById("msgContent").getText();
            //判断当月是否有账单信息
            if (msgContent1.length() != 0) {
                driver.findElementsByClassName("btn-r").get(4).click();
            } else {
                debitCardTransDetailTable = driver.findElementById("debitCardTransDetail_table");
                dataList.add(debitCardTransDetailTable.getAttribute("innerHTML"));
            }
            //上月末
            cal.add(Calendar.DAY_OF_MONTH, -1);
            endTime = sim.format(cal.getTime());
            //上月初
            cal.set(Calendar.DAY_OF_MONTH, 1);
            beginTime = sim.format(cal.getTime());
        }
        return dataList;
    }

    /**
     * 解析从页面获取到的数据并封装
     *
     * @param
     * @return
     */
    private Map<String, Object> analyData(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> map = new HashMap<>(16);
        //账单信息
        List<String> itemMes = (List<String>) paramMap.get("itemMes");
        //基本信息
        String baseMes = paramMap.get("baseMes").toString();

        if (itemMes.size() == 0) {
            map.put("errorCode", "1001");
            map.put("errorInfo", "账单信息为空");
            return map;
        }
        List billList;
        Map<String, Object> baseMap;
        try {
            //解析账单信息
            billList = analyBillMethod(itemMes);
            //解析基本信息
            baseMap = analyBaseMes(baseMes);
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
            if (tbody == null || tbody.size() == 0) {
                break;
            }
            Elements tr = tbody.get(0).getElementsByTag("tr");
            for (int i = 0; i < tr.size(); i++) {
                Element element = tr.get(i);
                Elements td = element.getElementsByTag("td");
                detailMap = new HashMap<>(16);

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
        System.out.println("---dataList---"+dataList);
        return dataList;
    }

    /**
     * 解析基本信息
     *
     * @param baseMes
     * @return
     */
    private Map<String, Object> analyBaseMes(String baseMes) throws Exception {
        Map<String, Object> map = new HashMap<>(16);
        Document parse = Jsoup.parse(baseMes);
        Elements elementsByClass = parse.getElementsByClass("layout-lr");
        Elements li = elementsByClass.get(0).getElementsByTag("li");
        map.put("accountType", li.get(0).getElementsByClass("item-con").text());
        map.put("openBranch", li.get(1).getElementsByClass("item-con").text());
        map.put("openTime", li.get(2).getElementsByClass("item-con").text());
        return map;
    }

    /**
     * 发包的  交易明细数据
     * @param
     * @param driver
     * @return
     */
    @SuppressWarnings("AlibabaUndefineMagicConstant")
    private JSONArray getItemMesfb(ChromeDriver driver) {
        //切换至交易明细
        List<WebElement> element = driver.findElements(By.className("tabs"));
        for (int i = 0; i < element.size(); i++) {
            if (element.get(i).getText().contains("交易明细")) {
                element.get(i).click();
            }
        }
        JSONArray Listcontent =new JSONArray();
        try {
            Thread.sleep(2000);
            //第一个发包的参数
            String url="https://ebsnew.boc.cn/BII/PsnGetUserProfile.do";
            //cookie
            Set<Cookie> ingoCookie = driver.manage().getCookies();
            StringBuffer cookies=new StringBuffer();
            for (Cookie co:ingoCookie) {
                cookies.append(co.getName()+"="+co.getValue()+";");
            }
            String cookie = cookies.toString();

          //获取账户序列
            String str=paramsstr(3,"","");
           String respons=SimpleHttpClient.post1(url,str, cookie);
            //获取返回结果中的第二次发包参数需要的值 conversationId
           JSONObject fromObj = JSONObject.fromObject(respons);
           JSONArray fromObjarr = JSONArray.fromObject(fromObj.get("response"));
           JSONObject accountSeqarr = (JSONObject) JSONObject.fromObject(fromObjarr.get(0)).get("result");
            
            //第一次发包
             str=paramsstr(1,"","");
             String response1=SimpleHttpClient.post1(url,str, cookie);
            //获取返回结果中的第二次发包参数需要的值 conversationId
            JSONObject fromObject = JSONObject.fromObject(response1);
            JSONArray fromObject1 = JSONArray.fromObject(fromObject.get("response"));

            //第二个发包的参数
            String accountSeq=accountSeqarr.get("accountSeq").toString();
            String conversationId=JSONObject.fromObject(fromObject1.get(0)).get("result").toString();
            str=paramsstr(2,conversationId,accountSeq);
            //第二次发包
            response1=SimpleHttpClient.post1(url,str, cookie);
            JSONObject fromObjects = JSONObject.fromObject(response1);
            JSONArray fromObject3 = JSONArray.fromObject(fromObjects.get("response"));
            String strr= JSONObject.fromObject(fromObject3.get(0)).get("result").toString();
            JSONObject List = JSONObject.fromObject(strr);
            Listcontent = JSONArray.fromObject(List.get("List"));

            System.out.println("--交易明细数据获取--"+Listcontent.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Listcontent;
    }


    /**
     * 获取交易明细的发包参数
     * @param i  第几次发包
     * @param conversationId 第二次发包的参数值 由第一次发包获取
     * @param conversationId2 
     * @return
     */
    private String paramsstr(int i,String conversationId, String accountSeq) {
        SimpleDateFormat sim = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        String endTime = sim.format(cal.getTime());
        String year=endTime.substring(0, 4);
        int year1=Integer.parseInt(year);
        String nowMonth=endTime.substring(5, 7);
        int nowMonth1=Integer.parseInt(nowMonth);
        String beginTime=GetMonth.beforMonth1(year1, nowMonth1, 6);
        System.out.println(endTime+","+beginTime);
        String str="";
        JSONObject request=new JSONObject();
        request.put("local", "zh_CN");
        request.put("agent", "WEB15");
        request.put("bfw-ctrl", "json");
        request.put("version", "");
        request.put("device", "Google,Chrome,63.0.3239.132");
        request.put("platform", "Microsoft,Windows,7");
        request.put("plugins", "");
        request.put("page", "");
        request.put("ext", "");
        request.put("mac", "");
        request.put("serial", "");
        
        JSONObject request3=new JSONObject();
        request3.put("id", "19");
        request3.put("method", "PsnAccBocnetCreateConversation");
        request3.put("conversationId",null);
        request3.put("params",null);

        JSONArray array=new JSONArray();
        array.add(request3);

        JSONObject request2=new JSONObject();
        request2.put("header", request);
        request2.put("request", array);

        //第二个发包的参数
        JSONObject params=new JSONObject();
        
        params.put("accountSeq", accountSeq);
        params.put("cashRemit", "");
        params.put("currency","001");
        params.put("currentIndex","0");
        params.put("endDate",endTime);
        params.put("pageSize","10");
        params.put("startDate",beginTime);
        params.put("_refresh","true");


        JSONObject request22=new JSONObject();
        request22.put("id", "21");
        request22.put("method", "PsnAccBocnetQryDebitTransDetail");
        request22.put("conversationId",conversationId);
        request22.put("params",params);

        JSONArray array22=new JSONArray();
        array22.add(request22);

        JSONObject request222=new JSONObject();
        request222.put("header", request);
        request222.put("request", array22);
        //获取账户序列的发包参数
        JSONObject request33=new JSONObject();
        request33.put("id", "6");
        request33.put("method", "PsnAccBocnetQryLoginInfo");
        request33.put("conversationId",null);
        request33.put("params",null);
        
        JSONArray array33=new JSONArray();
        array33.add(request33);
        
        JSONObject request333=new JSONObject();
        request333.put("header", request);
        request333.put("request", array33);
        
        
        if(i==1){
            str=request2.toString();
        }else if(i==2){
            str=request222.toString();
        }else {
        	str=request333.toString();
        }
        System.out.println("str=="+i+str);
        return str;
    }

    /**
     * 解析从页面获取到的数据并封装  发包的
     *
     * @param
     * @return
     */
    private Map<String, Object> analyDatafb(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> map = new HashMap<>(16);
        //账单信息
        JSONArray itemMes = (JSONArray) paramMap.get("itemMes");
        //基本信息
        String baseMes = paramMap.get("baseMes").toString();

        if (itemMes.size() == 0) {
            map.put("errorCode", "1001");
            map.put("errorInfo", "账单信息为空");
            return map;
        }
        List billList;
        Map<String, Object> baseMap;
        try {
            //解析账单信息
            billList = analyBillMethodfb(itemMes);
            //解析基本信息
            baseMap = analyBaseMes(baseMes);
        } catch (Exception e) {
            logger.warn("数据解析失败", e);
            throw new Exception("数据解析失败");
        }
        map.put("baseMes", baseMap);
        map.put("billMes", billList);
        return map;
    }

    /**
     * 解析账单信息   发包的
     *
     * @param itemMes
     * @return
     */
    private List analyBillMethodfb(JSONArray itemMes) throws Exception {
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> detailMap;

        JSONArray json = itemMes;
        if(json.size()>0){
            for(int i=0;i<json.size();i++){
                JSONObject job = json.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                double amount1=Double.parseDouble(job.get("amount").toString());
                double balance1=Double.parseDouble(job.get("balance").toString());
                DecimalFormat    df   = new DecimalFormat("######0.00");
                String amount=df.format(amount1).replace("-", "") ;
                String balance=df.format(balance1);

                detailMap = new HashMap<>(16);

                detailMap.put("incomeMoney", "");//收入金额
                detailMap.put("expendMoney", amount);//支出金额   数值
                detailMap.put("dealDitch", job.get("transChnl"));//交易渠道
                detailMap.put("dealTime", job.get("paymentDate"));//交易日期
                detailMap.put("dealReferral", job.get("businessDigest"));//业务摘要  ok
                detailMap.put("oppositeSideName", job.get("payeeAccountName"));//对方账户名
                detailMap.put("oppositeSideNumber", job.get("payeeAccountNumber"));//对方账户
                detailMap.put("currency", job.get("currency"));//币种
                detailMap.put("balanceAmount", balance);//余额  数值
                dataList.add(detailMap);
                System.out.println();
            }
        }
        System.out.println("------dataList--fb--"+dataList) ;  // 得到 每个对象中的属性值
        return dataList;
    }


}



