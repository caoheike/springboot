package com.reptile.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.springboot.Scheduler;
import com.reptile.util.*;

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

/**
 * 移动运营商
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Service
public class PhoneBillsService {
    private long timeStamp = System.currentTimeMillis();
    private String path = "";
    private Logger logger = LoggerFactory.getLogger(PhoneBillsService.class);

    public Map<String, String> getChinaMobileCode(HttpServletRequest request, String userNumber) {
        Map<String, String> map = new HashMap<String, String>(16);
        HttpSession session = request.getSession();

        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
        // 开启cookie管理
        webClient.getCookieManager().setCookiesEnabled(true);
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
            String statusFalse = "false";
            if (statusFalse.equals(page.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "非移动用户请注册互联网用户登录");
                webClient.close();
                return map;
            }
            //发送登录手机验证码
            TextPage page1 = webClient.getPage("https://login.10086.cn/sendRandomCodeAction.action?userName=" + userNumber + "&type=01&channelID=12003");
            String returnBack1 = "0";
            String returnBack2 = "4005";
            String returnBack3 = "1";
            String returnBack4 = "2";
            String returnBack5 = "3";
            if (returnBack1.equals(page1.getContent())) {
                map.put("errorCode", "0000");
                map.put("errorInfo", "已将短信随机码发送至手机，请查收!");
            } else if (returnBack2.equals(page1.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "手机号码有误，请重新输入!");
            } else if (returnBack3.equals(page1.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "对不起，短信随机码暂时不能发送，请一分钟以后再试！");
            } else if (returnBack4.equals(page1.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "短信下发数已达上限！");
            } else if (returnBack5.equals(page1.getContent())) {
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
        Map<String, String> map = new HashMap<String, String>(16);
        HttpSession session = request.getSession();

        Object client = session.getAttribute("YD-webClient");
        if (client == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            return map;
        } else {
            try {
                //执行官网js对短信验证码进行加密
                WebClient webClient = (WebClient) client;
                HtmlPage page = webClient.getPage("https://login.10086.cn/login.html");
                Object javaScriptResult = page.executeJavaScript("encrypt(\""+duanxinCode+"\")").getJavaScriptResult();
                System.out.println(javaScriptResult.toString());

                String loadPath = "https://login.10086.cn/login.htm";
                //登录
                URL url = new URL(loadPath);
                WebRequest webRequest = new WebRequest(url);
                webRequest.setHttpMethod(HttpMethod.GET);
                webRequest.setAdditionalHeader("Referer", "https://login.10086.cn/login.html?channelID=12003&backUrl=http://shop.10086.cn/i/");
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new NameValuePair("accountType", "01"));
                list.add(new NameValuePair("account", userNumber));
                list.add(new NameValuePair("password", javaScriptResult.toString()));
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
                String keyResult = "result";
                String keyCode = "code";
                if (jsonObject.get(keyResult) == null || jsonObject.get(keyCode) == null) {
                    map.put("errorCode", "0003");
                    map.put("errorInfo", "服务器繁忙");
                    return map;
                }

                String result = jsonObject.get("result").toString();
                String code = jsonObject.get("code").toString();
                String backCode = "0";
                if (!backCode.equals(result)) {
                    String backCode2 = "6001";
                    String backCode3 = "6002";
                    if (backCode2.equals(code) || backCode3.equals(code)) {
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
                String retMsg = "retMsg";
                String accessMsg = "可用性成功";
                if (jsonObject1.get(retMsg) == null || !jsonObject1.get(retMsg).toString().contains(accessMsg)) {
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
        Map<String, Object> map = new HashMap<String, Object>(16);
        Map<String, String> mapPath = new HashMap<String, String>(16);

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

        Map<String, String> map = new HashMap<String, String>(16);
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
                String statusSuccess = "success";
                if (!page6.getWebResponse().getContentAsString().contains(statusSuccess)) {
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
                                                String fuwuSec, String imageCode, String longitude, String latitude, String uuid) throws InterruptedException {
        Map<String, Object> map = new HashMap<String, Object>(16);
        List<String> listss = new ArrayList<>();
        System.out.println("---移动---" + userNumber);

        PushSocket.pushnew(map, uuid, "1000", "登录中");
        PushState.state(userNumber, "callLog", 100);
        String signle = "1000";

        Map<String, Object> dataMap = new HashMap<String, Object>(16);
        List dataList = new ArrayList();
        HttpSession session = request.getSession();

        Object client = session.getAttribute("YD-webClient");
        if (client == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            PushSocket.pushnew(map, uuid, "3000", "登录超时");
            PushState.state(userNumber, "callLog", 200, "登录超时。");
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
                String successStatus = "认证成功";
                logger.warn(userNumber + ":本次二次身份认证结果为:" + result);
                if (!page8.getWebResponse().getContentAsString().contains(successStatus)) {
                    String jquerys = "jQuery";
                    if (result.contains(jquerys)) {
                        int s = ("jQuery183045411546722870333_" + timeStamp + "(").length();
                        String json = result.substring(s);
                        result = json.substring(0, json.length() - 1);
                    }
                    JSONObject jsonObject = JSONObject.fromObject(result);

                    String retMsg = jsonObject.get("retMsg").toString();

                    if (retMsg.contains("请先登录")) {
                        logger.warn(System.currentTimeMillis() + ": " + userNumber + ":移动二次身份认证时出现session信息为空，已隐藏！原信息为:" + retMsg);
                        retMsg = "系统繁忙，请稍后重试";
                    }
                    map.put("errorCode", "0002");
                    map.put("errorInfo", retMsg);

                    PushSocket.pushnew(map, uuid, "3000", retMsg);
                    PushState.state(userNumber, "callLog", 200, retMsg);

                    return map;
                }
                PushSocket.pushnew(map, uuid, "2000", "登录成功");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");

                Calendar cal = Calendar.getInstance();
                String sDate = simpleDateFormat.format(cal.getTime());

                PushSocket.pushnew(map, uuid, "5000", "数据获取中");
                signle = "5000";
                int boundCount = 7;
                loop:
                for (int i = 1; i < boundCount; i++) {
                    logger.warn(userNumber+"   "+i + "次开始获取数据   " + sDate);
                    String results = "";
                    Object page9 = webClient.getPage("https://shop.10086.cn/i/v1/fee/detailbillinfojsonp/" + userNumber +
                            "?callback=jQuery183045411546722870333_" + timeStamp + "&curCuror=1&step=200&qryMonth=" + sDate + "&billType=02&_=" + System.currentTimeMillis());
                    if (page9 instanceof HtmlPage) {
                        logger.warn(userNumber+"   "+"第一次请求出错  " + sDate);
                        for (int j = 0; j < 3; j++) {
                            logger.warn(userNumber+"   "+"请出错，现在开始第" + j + "次请求  " + sDate);
                            Object page10 = webClient.getPage("https://shop.10086.cn/i/v1/fee/detailbillinfojsonp/" + userNumber +
                                    "?callback=jQuery183045411546722870333_" + timeStamp + "&curCuror=1&step=200&qryMonth=" + sDate + "&billType=02&_=" + System.currentTimeMillis());
                            if (page10 instanceof UnexpectedPage) {
                                logger.warn(userNumber+"   "+j + ":本次请求成功 " + sDate);
                                UnexpectedPage pages = (UnexpectedPage) page10;
                                results = pages.getWebResponse().getContentAsString();
                                break;
                            } else if (j == 2) {
                                logger.warn(userNumber+"   "+sDate + "：：三次未请求到数据，跳过进行下一月账单读取");
                                cal.add(Calendar.MONTH, -1);
                                sDate = simpleDateFormat.format(cal.getTime());
                                continue loop;
                            }
                            Thread.sleep(2000);
                        }
                    } else {
                        logger.warn(userNumber+"   "+i + "次请求成功!" + sDate);
                        UnexpectedPage pages = (UnexpectedPage) page9;
                        results = pages.getWebResponse().getContentAsString();
                    }

                    int s = ("jQuery183045411546722870333_" + timeStamp + "(").length();
                    String json = results.substring(s);
                    results = json.substring(0, json.length() - 1);

                    //判断数据真伪与重复性
                    if (results.contains("startTime") && results.contains("commPlac")) {
                        JSONObject jsonObject = JSONObject.fromObject(results);
                        String startDate = jsonObject.getString("startDate");
                        //确认获取的6个月账单不会重复
                        if (!listss.contains(startDate)) {
                            dataList.add(results);
                            listss.add(startDate);
                        }else{
                            logger.warn(userNumber+":   获取时间:"+sDate+"   "+i+"次请求数据为重复数据,数据为:---------"+results+"---------");
                        }
                    }else{
                        logger.warn(userNumber+":   获取时间:"+sDate+"   "+i+"次请求数据异常,异常数据为:---------"+results+"---------");
                    }
                    cal.add(Calendar.MONTH, -1);
                    sDate = simpleDateFormat.format(cal.getTime());
                    Thread.sleep(2000);
                }
                logger.warn(userNumber+": 该用户获取的数据总数为："+dataList.size());
                int boundCount3 = 4;
                if (dataList.size() < boundCount3) {
                    PushSocket.pushnew(map, uuid, "7000", "数据获取不完全，请重新认证！(注：请确认手机号使用时长超过6个月)");
                    PushState.state(userNumber, "callLog", 200, "数据获取不完全，请重新认证！(注：请确认手机号使用时长超过6个月)");
                    map.put("errorCode", "0009");
                    map.put("errorInfo", "数据获取不完全，请重新再次认证！(注：请确认手机号使用时长超过6个月)");
                    return map;
                }
                PushSocket.pushnew(map, uuid, "6000", "数据获取成功");
                signle = "4000";
                //通话详单数据
                dataMap.put("data", dataList);
                //手机
                dataMap.put("userPhone", userNumber);
                //服务密码
                dataMap.put("serverCard", fuwuSec);
                //经度
                dataMap.put("longitude", longitude);
                //纬度
                dataMap.put("latitude", latitude);
                map.put("errorCode", "0000");
                map.put("errorInfo", "查询成功");
                map.put("data", dataList.toString());
                Resttemplate resttemplate = new Resttemplate();
                map = resttemplate.SendMessage(dataMap, ConstantInterface.port + "/HSDC/message/mobileCallRecord");
                //推送结果  未写
                String statusResukt = "0000";
                String statusCode = "errorCode";
                if (statusResukt.equals(map.get(statusCode))) {
                    PushSocket.pushnew(map, uuid, "8000", "认证成功");
                    PushState.state(userNumber, "callLog", 300);
                } else {
                    PushSocket.pushnew(map, uuid, "9000", map.get("errorInfo").toString());
                    PushState.state(userNumber, "callLog", 200, map.get("errorInfo").toString());
                }
                webClient.close();
            } catch (Exception e) {
                logger.warn(" -------------------- 获取移动详单异常  mrlu", e);
                map.put("errorCode", "0004");
                map.put("errorInfo", "系统繁忙");
                PushState.state(userNumber, "callLog", 200, "认证失败,系统繁忙！");
                DealExceptionSocketStatus.pushExceptionSocket(signle, map, uuid);
            }
        }
        return map;
    }

    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMM");

        Calendar cal = Calendar.getInstance();
        String sDate = simpleDateFormat.format(cal.getTime());
        for(int i=1;i<7;i++){
            System.out.println(sDate);
            cal.add(Calendar.MONTH,-1);
            sDate = simpleDateFormat.format(cal.getTime());
        }
    }
}
