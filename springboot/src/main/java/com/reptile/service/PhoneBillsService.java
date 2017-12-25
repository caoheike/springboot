package com.reptile.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.springboot.Scheduler;
import com.reptile.util.ConstantInterface;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
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
    private Logger logger = LoggerFactory.getLogger(PhoneBillsService.class);

    public Map<String, String> getChinaMobileCode(HttpServletRequest request, String userNumber) {
        Map<String, String> map = new HashMap<String, String>();
        HttpSession session = request.getSession();

        WebClient webClient = new WebClientFactory().getWebClient();
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

            if ("false".equals(page.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "非移动用户请注册互联网用户登录");
                webClient.close();
                return map;
            }
            //发送登录手机验证码
            TextPage page1 = webClient.getPage("https://login.10086.cn/sendRandomCodeAction.action?userName=" + userNumber + "&type=01&channelID=12003");

            if ("0".equals(page1.getContent())) {
                map.put("errorCode", "0000");
                map.put("errorInfo", "已将短信随机码发送至手机，请查收!");
            } else if ("4005".equals(page1.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "手机号码有误，请重新输入!");
            } else if ("1".equals(page1.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "对不起，短信随机码暂时不能发送，请一分钟以后再试！");
            } else if ("2".equals(page1.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "短信下发数已达上限！");
            } else if ("3".equals(page1.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "对不起，短信发送次数过于频繁！");
            } else {
                map.put("errorCode", "0002");
                map.put("errorInfo", "短信验证码发送错误");
            }
            session.setAttribute("YD-webClient", webClient);
        } catch (HttpHostConnectException e) {
            map.put("errorCode", "0003");
            map.put("errorInfo", "网络繁忙，请刷新后重新再试");
            logger.warn(e.getMessage() + "  获取移动验证码   mrlu", e);
            Scheduler.sendGet(Scheduler.getIp);
            webClient.close();
        } catch (Exception e) {
            Scheduler.sendGet(Scheduler.getIp);
            logger.warn(e.getMessage() + "  获取移动验证码   mrlu", e);
            map.put("errorCode", "0003");
            map.put("errorInfo", "网络繁忙，请刷新后重新再试");
            webClient.close();
        }
        return map;
    }

    public Map<String, String> chinaMobilLoad(HttpServletRequest request, String userNumber, String duanxinCode) {
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
//                进入详单页面
                String path2 = "http://shop.10086.cn/i/apps/serviceapps/billdetail/index.html";
                HtmlPage page4 = webClient.getPage(path2);

                //判断当前登录用户地区是否开放此功能
                UnexpectedPage page5 = webClient.getPage("http://shop.10086.cn/i/v1/res/funcavl?_=" + System.currentTimeMillis());

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

                logger.warn(e.getMessage() + "  移动登录   mrlu", e);
                map.put("errorCode", "0005");
                map.put("errorInfo", "网络繁忙");
            }
        }
        return map;
    }


    public Map<String, Object> getDetialImageCode(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> mapPath = new HashMap<String, String>();

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
                map.put("data", mapPath);
                map.put("errorCode", "0000");
                map.put("errorInfo", "验证码获取成功");
            } catch (Exception e) {
                logger.warn(e.getMessage() + "  获取移动详单图片验证码   mrlu", e);
                map.put("errorCode", "0002");
                map.put("errorInfo", "系统繁忙");
            }
        }
        return map;
    }


    public Map<String, String> getDetialMobilCode(HttpServletRequest request, String userNumber) {

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

                if (!page6.getWebResponse().getContentAsString().contains("success")) {
                    try {
                        String results = page6.getWebResponse().getContentAsString();
                        int s = ("jQuery183045411546722870333_" + timeStamp + "(").length();
                        String json = results.substring(s);
                        results = json.substring(0, json.length() - 1);
                        JSONObject jsonObject = JSONObject.fromObject(results);
                        map.put("errorCode", "0002");
                        map.put("errorInfo", jsonObject.get("retMsg").toString());
                        return map;
                    } catch (Exception e) {
                        map.put("errorCode", "0002");
                        map.put("errorInfo", "短信发送失败");
                        return map;
                    }

                }
                map.put("errorCode", "0000");
                map.put("errorInfo", "短信发送成功");
            } catch (Exception e) {

                logger.warn(e.getMessage() + "  获取移动详单手机验证码   mrlu", e);
                map.put("errorCode", "0003");
                map.put("errorInfo", "系统繁忙");
            }
        }
        return map;
    }


    public Map<String, Object> getDetailAccount(HttpServletRequest request, String userNumber, String phoneCode,
                                                String fuwuSec, String imageCode, String longitude, String latitude, String UUID) {
        Map<String, Object> map = new HashMap<String, Object>();
		System.out.println("---移动---"+userNumber);
        PushSocket.pushnew(map, UUID, "1000","登录中");
        PushState.state(userNumber, "callLog",100);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        Map<String, Object> dataMap = new HashMap<String, Object>();
        List dataList = new ArrayList();
        HttpSession session = request.getSession();

        Object client = session.getAttribute("YD-webClient");
        if (client == null) {
            //PushSocket.push(map, UUID, "0001");
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            PushSocket.pushnew(map, UUID, "3000","登录超时");
            PushState.state(userNumber, "callLog",200);
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
                    PushSocket.pushnew(map, UUID, "3000",jsonObject.get("retMsg").toString());
                    PushState.state(userNumber, "callLog",200);
                    return map;
                }
                PushSocket.pushnew(map, UUID, "2000","登录成功");
                //---------------推-------------------
                //PushSocket.push(map, UUID, "0000");
                Date date = new Date();
                SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyyMM");
                String str = SimpleDateFormat.format(date);
                int sDate = Integer.parseInt(str);
                PushSocket.pushnew(map, UUID, "5000","数据获取中");	
                for (int i = 1; i < 7; i++) {
                    UnexpectedPage page9 = webClient.getPage("https://shop.10086.cn/i/v1/fee/detailbillinfojsonp/" + userNumber +
                            "?callback=jQuery183045411546722870333_" + timeStamp + "&curCuror=1&step=300&qryMonth=" + sDate + "&billType=02&_=" + System.currentTimeMillis());
                    
                    String results = page9.getWebResponse().getContentAsString();
                    if (!results.contains("retCode\":\"000000")) {
                        map.put("errorCode", "0003");
                        map.put("errorInfo", "哪里好像出错了");
                    }
                    int s = ("jQuery183045411546722870333_" + timeStamp + "(").length();
                    String json = results.substring(s);
                    results = json.substring(0, json.length() - 1);
                    if (results.contains("startTime") && results.contains("commPlac")) {
                        dataList.add(results);
                    }

                    sDate--;
                    Thread.sleep(2000);
                }
                	PushSocket.pushnew(map, UUID, "6000","数据获取成功");
                    dataMap.put("data", dataList);//通话详单数据
                    dataMap.put("userPhone", userNumber);//手机
                    dataMap.put("serverCard", fuwuSec);//服务密码
                    dataMap.put("longitude", longitude);//经度
                    dataMap.put("latitude", latitude);//纬度
                    map.put("errorCode", "0000");
                    map.put("errorInfo", "查询成功");
                    map.put("data", dataList.toString());
                    Resttemplate resttemplate = new Resttemplate();
                    map = resttemplate.SendMessage(dataMap, ConstantInterface.port + "/HSDC/message/mobileCallRecord");
                    //推送结果  未写
                    if(map.get("errorCode").equals("0000")) {
                    	PushSocket.pushnew(map, UUID, "8000","认证成功");
                    	 PushState.state(userNumber, "callLog",300);
                    }else {
                    	PushSocket.pushnew(map, UUID, "9000",map.get("errorInfo").toString());
                    	PushState.state(userNumber, "callLog",200);
                    }
                
                webClient.close();
            } catch (Exception e) {
                logger.warn(e.getMessage() + "  获取移动详单  mrlu", e);
                //PushSocket.push(map, UUID, "0001");
                map.put("errorCode", "0004");
                map.put("errorInfo", "系统繁忙");
                PushState.state(userNumber, "callLog",200);
                PushSocket.pushnew(map, UUID, "9000","认证失败,系统繁忙");
            }
        }
        return map;
    }
}
