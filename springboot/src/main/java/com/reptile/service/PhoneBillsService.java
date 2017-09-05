package com.reptile.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.springboot.Scheduler;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import net.sf.json.JSONObject;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PhoneBillsService {
    private long timeStamp = System.currentTimeMillis();
    private String path = "";
    private Logger logger= LoggerFactory.getLogger(PhoneBillsService.class);
    public Map<String, String> getChinaMobileCode(HttpServletRequest request, String userNumber) {
        Map<String, String> map = new HashMap<String, String>();
        HttpSession session = request.getSession();

        WebClient webClient = new WebClientFactory().getWebClient();
//        WebClient webClient=new WebClient(BrowserVersion.CHROME);
        webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setTimeout(90000);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.setJavaScriptTimeout(40000);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        //验证是否是移动用户
        try {
            TextPage page = webClient.getPage("https://login.10086.cn/chkNumberAction.action?userName=" + userNumber);
            System.out.println(page.getContent());
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            if ("false".equals(page.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "非移动用户请注册互联网用户登录");
                return map;
            }
            //发送登录手机验证码
            TextPage page1 = webClient.getPage("https://login.10086.cn/sendRandomCodeAction.action?userName=" + userNumber + "&type=01&channelID=12003");
            System.out.println(page1.getContent());

            if("0".equals(page1.getContent())){
                map.put("errorCode", "0000");
                map.put("errorInfo", "已将短信随机码发送至手机，请查收!");
            }else if("4005".equals(page1.getContent())){
                map.put("errorCode", "0001");
                map.put("errorInfo", "手机号码有误，请重新输入!");
            }else if ("1".equals(page1.getContent())){
                map.put("errorCode", "0001");
                map.put("errorInfo", "对不起，短信随机码暂时不能发送，请一分钟以后再试！");
            }else if ("2".equals(page1.getContent())){
                map.put("errorCode", "0001");
                map.put("errorInfo", "短信下发数已达上限！");
            }else if ("3".equals(page1.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "对不起，短信发送次数过于频繁！");
            }else {
                map.put("errorCode", "0002");
                map.put("errorInfo", "短信验证码发送错误");
            }
            session.setAttribute("YD-webClient", webClient);
        } catch (HttpHostConnectException e) {
            logger.warn(e.getMessage()+"     mrlu");
            e.printStackTrace();
            Scheduler.sendGet(Scheduler.getIp);
        } catch (Exception e) {
            logger.warn(e.getMessage()+"     mrlu");
            e.printStackTrace();
            map.put("errorCode", "0003");
            map.put("errorInfo", "网络繁忙，请刷新后重新再试");
        }
        return map;
    }

    public Map<String, String> chinaMobilLoad(HttpServletRequest request, String userNumber, String duanxinCode)  {
        Map<String, String> map = new HashMap<String, String>();
        HttpSession session = request.getSession();

        Object client = session.getAttribute("YD-webClient");
        if (client == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            return map;
        } else {
            try {
                WebClient webClient = (WebClient) client;
                String loadPath = "https://login.10086.cn/login.htm";
                //登录
                URL url = new URL(loadPath);
                WebRequest webRequest = new WebRequest(url);
                webRequest.setHttpMethod(HttpMethod.GET);
                webRequest.setAdditionalHeader("Referer", "https://login.10086.cn/login.html?channelID=12003&backUrl=http://shop.10086.cn/i/");
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new NameValuePair("accountType", "01"));
                list.add(new NameValuePair("account", userNumber));
                list.add(new NameValuePair("password", duanxinCode));
                list.add(new NameValuePair("pwdType", "02"));
                list.add(new NameValuePair("smsPwd", ""));
                list.add(new NameValuePair("inputCode", ""));
                list.add(new NameValuePair("backUrl", "http://shop.10086.cn/i/"));
                list.add(new NameValuePair("rememberMe", "0"));
                list.add(new NameValuePair("channelID", "12003"));
                list.add(new NameValuePair("protocol", "https:"));
                String ls = String.valueOf(System.currentTimeMillis());
                list.add(new NameValuePair("timestamp", ls));

                webRequest.setRequestParameters(list);
                UnexpectedPage page2 = webClient.getPage(webRequest);

                System.out.println(page2.getWebResponse().getContentAsString());

                JSONObject jsonObject = JSONObject.fromObject(page2.getWebResponse().getContentAsString());

                if (jsonObject.get("result") == null || jsonObject.get("code") == null) {
                    map.put("errorCode", "0003");
                    map.put("errorInfo", "服务器繁忙");
                    return map;
                }

                String result = jsonObject.get("result").toString();
                String code = jsonObject.get("code").toString();
                if (!"0".equals(result)) {
                    if ("6001".equals(code) || "6002".equals(code)) {
                        map.put("errorCode", "0002");
                        map.put("errorInfo", "短信随机码不正确或已过期，请重新获取");
                        return map;
                    } else {
                        map.put("errorCode", "0003");
                        map.put("errorInfo", "系统繁忙");
                        return map;
                    }
                }

                //跳转到个人中心页面
                String assertAcceptURL = jsonObject.get("assertAcceptURL").toString();
                String artifact = jsonObject.get("artifact").toString();
                String backUrl = "http://shop.10086.cn/i/";

                path = assertAcceptURL + "?backUrl=" + backUrl + "&artifact=" + artifact;
                HtmlPage page3 = webClient.getPage(path);

////                System.out.println(page3.asXml());
//
//                进入详单页面
                String path2 = "http://shop.10086.cn/i/apps/serviceapps/billdetail/index.html";
                HtmlPage page4 = webClient.getPage(path2);

                //判断当前登录用户地区是否开放此功能
                UnexpectedPage page5 = webClient.getPage("http://shop.10086.cn/i/v1/res/funcavl?_=" + System.currentTimeMillis());

                System.out.println(page5.getWebResponse().getContentAsString());
                JSONObject jsonObject1 = JSONObject.fromObject(page5.getWebResponse().getContentAsString());
                if (jsonObject1.get("retMsg") == null || !jsonObject1.get("retMsg").toString().contains("可用性成功")) {
                    map.put("errorCode", "0004");
                    map.put("errorInfo", "抱歉，暂时不提供该地区用户信息");
                    return map;
                }
                session.setAttribute("YD-webClient", webClient);
                map.put("errorCode", "0000");
                map.put("errorInfo", "操作成功");
            } catch (Exception e) {
                logger.warn(e.getMessage()+"     mrlu");
                e.printStackTrace();
                map.put("errorCode", "0005");
                map.put("errorInfo", "网络繁忙");
            }
        }
        return map;
    }

    public Map<String, Object> getDetialImageCode(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String,String> mapPath=new HashMap<String, String>();
        HttpSession session = request.getSession();

        Object client = session.getAttribute("YD-webClient");
        if (client == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            return map;
        } else {
            try {
                WebClient webClient = (WebClient) client;
                UnexpectedPage page7 = webClient.getPage("http://shop.10086.cn/i/authImg?t=" + Math.random());

                String path = request.getServletContext().getRealPath("/vecImageCode");
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String fileName = "yidong" + System.currentTimeMillis() + ".png";
                BufferedImage bi = ImageIO.read(page7.getInputStream());
                ImageIO.write(bi, "png", new File(file, fileName));

                mapPath.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/vecImageCode/" + fileName);
                map.put("data",mapPath);
                map.put("errorCode", "0000");
                map.put("errorInfo", "验证码获取成功");
            } catch (Exception e) {
                logger.warn(e.getMessage()+"     mrlu");
                e.printStackTrace();
                map.put("errorCode", "0002");
                map.put("errorInfo", "系统繁忙");
            }
        }
        return map;
    }

    public Map<String, String> getDetialMobilCode(HttpServletRequest request, String userNumber){
        Map<String, String> map = new HashMap<String, String>();
        HttpSession session = request.getSession();

        Object client = session.getAttribute("YD-webClient");
        if (client == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            return map;
        } else {
            WebClient webClient = (WebClient) client;
            try {
                //获取通话详单时发送手机验证码
                URL url1 = new URL("https://shop.10086.cn/i/v1/fee/detbillrandomcodejsonp/" + userNumber +
                        "?callback=jQuery183045411546722870333_" + timeStamp + "&_=" + System.currentTimeMillis());
                WebRequest webRequest1 = new WebRequest(url1);
                webRequest1.setHttpMethod(HttpMethod.GET);
                webRequest1.setAdditionalHeader("Referer", "http://shop.10086.cn/i/?f=home&welcome=" + System.currentTimeMillis());
                UnexpectedPage page6 = webClient.getPage(webRequest1);
                System.out.println(page6.getWebResponse().getContentAsString());


                if (!page6.getWebResponse().getContentAsString().contains("success")) {
                    try{
                        String results=page6.getWebResponse().getContentAsString();
                        int s = ("jQuery183045411546722870333_" + timeStamp + "(").length();
                        String json = results.substring(s);
                        results = json.substring(0, json.length() - 1);
                        JSONObject jsonObject = JSONObject.fromObject(results);
                        map.put("errorCode", "0002");
                        map.put("errorInfo", jsonObject.get("retMsg").toString());
                        return map;
                    }catch (Exception e){
                        e.printStackTrace();
                        map.put("errorCode", "0002");
                        map.put("errorInfo", "短信发送失败");
                        return map;
                    }
                }
                map.put("errorCode", "0000");
                map.put("errorInfo", "短信发送成功");
            } catch (Exception e) {
                logger.warn(e.getMessage()+"     mrlu");
                e.printStackTrace();
                map.put("errorCode", "0003");
                map.put("errorInfo", "系统繁忙");
            }
        }
        return map;
    }

    public Map<String, Object> getDetailAccount(HttpServletRequest request, String userNumber, String phoneCode, String fuwuSec, String imageCode){
        Map<String, Object> map = new HashMap<String, Object>();

        Map<String, Object> dataMap = new HashMap<String, Object>();
        List dataList = new ArrayList();
        HttpSession session = request.getSession();

        Object client = session.getAttribute("YD-webClient");
        if (client == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            return map;
        } else {
            try {
                WebClient webClient = (WebClient) client;
                HtmlPage page3 = webClient.getPage(path);
                //对服务密码和手机验证码进行加密
                List<String> alert = new ArrayList<String>();
                CollectingAlertHandler collectingAlertHandler = new CollectingAlertHandler(alert);
                webClient.setAlertHandler(collectingAlertHandler);

                page3.executeJavaScript("alert(base64encode(utf16to8('" + fuwuSec + "')))");
                page3.executeJavaScript("alert(base64encode(utf16to8('" + phoneCode + "')))");

                String pwdTempSerCode = "";
                String pwdTempRandCode = "";
                if (alert.size() > 0) {
                    pwdTempSerCode = alert.get(0);
                    pwdTempRandCode = alert.get(1);
                }

                //身份进行二次验证
                URL url2 = new URL("https://shop.10086.cn/i/v1/fee/detailbilltempidentjsonp/" + userNumber +
                        "?callback=jQuery183045411546722870333_" + timeStamp + "&pwdTempSerCode=" + pwdTempSerCode +
                        "&pwdTempRandCode=" + pwdTempRandCode + "&captchaVal=" + imageCode + "&_=" + System.currentTimeMillis());

                WebRequest webRequest2 = new WebRequest(url2);
                webRequest2.setAdditionalHeader("Referer", "http://shop.10086.cn/i/?f=home&welcome=" + System.currentTimeMillis());
                webRequest2.setHttpMethod(HttpMethod.GET);
                UnexpectedPage page8 = webClient.getPage(webRequest2);
                Thread.sleep(1000);
                System.out.println(page8.getWebResponse().getContentAsString());
                String result = page8.getWebResponse().getContentAsString();
                if (!page8.getWebResponse().getContentAsString().contains("认证成功")) {
                    if (result.contains("jQuery")) {
                        int s = ("jQuery183045411546722870333_" + timeStamp + "(").length();
                        String json = result.substring(s);
                        result = json.substring(0, json.length() - 1);
                    }
                    JSONObject jsonObject = JSONObject.fromObject(result);
                    map.put("errorCode", "0002");
                    map.put("errorInfo", jsonObject.get("retMsg").toString());
                    return map;
                }

                Date date = new Date();
                SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyyMM");
                String str = SimpleDateFormat.format(date);
                int sDate = Integer.parseInt(str);
                for (int i = 1; i < 7; i++) {
                    UnexpectedPage page9 = webClient.getPage("https://shop.10086.cn/i/v1/fee/detailbillinfojsonp/" + userNumber +
                            "?callback=jQuery183045411546722870333_" + timeStamp + "&curCuror=1&step=300&qryMonth=" + sDate + "&billType=02&_=" + System.currentTimeMillis());
                    System.out.println(page9.getWebResponse().getContentAsString());
                    String results = page9.getWebResponse().getContentAsString();
                    if (!results.contains("retCode\":\"000000")) {
                        map.put("errorCode", "0003");
                        map.put("errorInfo", "哪里好像出错了");
                    }
                    int s = ("jQuery183045411546722870333_" + timeStamp + "(").length();
                    String json = results.substring(s);
                    results = json.substring(0, json.length() - 1);

                    dataList.add(results);

                    sDate--;
                    Thread.sleep(500);
                }
                dataMap.put("data", dataList);
                dataMap.put("userPhone", userNumber);
                dataMap.put("serverCard", fuwuSec);
                map.put("errorCode", "0000");
                map.put("errorInfo", "查询成功");
                map.put("data", dataList.toString());
                Resttemplate resttemplate = new Resttemplate();

                map = resttemplate.SendMessage(dataMap, "http://192.168.3.35:8080/HSDC/message/mobileCallRecord");
            } catch (Exception e) {
                logger.warn(e.getMessage()+"     mrlu");
                e.printStackTrace();
                map.put("errorCode", "0004");
                map.put("errorInfo", "系统繁忙");
            }
        }
        return map;
    }
}
