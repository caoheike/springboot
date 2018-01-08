package com.reptile.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.constants.MessageConstamts;
import com.reptile.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Service
public class GlobalUnicomService {
    @Autowired
    private application application;

    /**
     * 接口 获取登陆验证码 联通
     *
     * @param request
     * @param httpResponse
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public Map<String, Object> getCode(HttpServletRequest request,
                                       HttpServletResponse httpResponse, String useriPhone) {
        System.out.println("访问了。。。。。。。。");
        Map<String, Object> map = new HashMap<String, Object>(8);
        HttpSession session = request.getSession();
        WebClient webClient = new WebClientFactory().getWebClient();
        try {
            HtmlPage loginPage = webClient
                    .getPage("https://uac.10010.com/portal/homeLogin");
            Thread.sleep(500);
            String url = "https://uac.10010.com/portal/Service/CheckNeedVerify?callback=jQuery17209863190566662376_"
                    + System.currentTimeMillis()
                    + "&userName="
                    + useriPhone
                    + "&pwdType=01&_=" + System.currentTimeMillis();
            UnexpectedPage page = webClient.getPage(url);
            Thread.sleep(500);
            // System.out.println(page.getWebResponse().getContentAsString());
            String resultInfo = page.getWebResponse().getContentAsString();
            String tips = resultInfo.split("\\(")[1].split("\\)")[0];
            JSONObject jsons = JSONObject.fromObject(tips);
            String tipInfo = jsons.get("resultCode").toString();
            String tip = jsons.get("ckCode").toString();
            if (MessageConstamts.STATUS_TRUE.equals(tipInfo)) {
                // System.out.println("需要图形验证码");
                session.setAttribute("isTrue", "true");
            } else {
                session.setAttribute("isTrue", "false");
                // System.out.println("不需要 图形验证码");
            }
            if (tip.contains(MessageConstamts.STRING_1)) {
                map.put("errorCode", "0000");
                map.put("errorInfo", "此次不需要验证码");
                session.setAttribute("isTrueCk", "false");
            } else {
                System.out.println("需要验证码");
                session.setAttribute("isTrueCk", "true");
                String url2 = "https://uac.10010.com/portal/Service/SendCkMSG?callback=jQuery17209863190566662376_"
                        + System.currentTimeMillis()
                        + "&req_time="
                        + System.currentTimeMillis()
                        + "&mobile="
                        + useriPhone
                        + "&_=" + System.currentTimeMillis();
                HtmlPage page1 = webClient.getPage(url2);
                Thread.sleep(500);
                String result = page1.asText();
                if (result.contains(MessageConstamts.STRING_0000)) {
                    map.put("errorCode", MessageConstamts.STRING_0000);
                    map.put("errorInfo", MessageConstamts.SEND_CODE_OK);

                } else {
                    JSONObject json = JSONObject
                            .fromObject(result.split("\\(")[1].split("\\)")[0]);
                    String resultCode = json.get("resultCode").toString();
                    if (MessageConstamts.STRING_7098.equals(resultCode)) {

                        map.put("errorCode", "0001");
                        map.put("errorInfo", "随机码发送次数已达上限，请明日再试！");
                    } else {
                        map.put("errorCode", "0001");
                        map.put("errorInfo", "距离上次发送不足1分钟");
                    }

                }
            }
            session.setAttribute("webClientone", webClient);
        } catch (Exception e) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "网络异常");
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 联通登陆
     *
     * @param
     */

    public Map<String, Object> unicomLogin(HttpServletRequest request,
                                           String userIphone, String password, String code) {
        Map<String, Object> map = new HashMap<String, Object>(8);
        try {
            HttpSession session = request.getSession();
            WebClient webClient = (WebClient) session
                    .getAttribute("webClientone");
            if (webClient == null) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络异常");
                return map;
            }
            // 图形验证码
            String isTrue = (String) session.getAttribute("isTrue");
            // 短信验证码
            String isTrueCk = (String) session.getAttribute("isTrueCk");
            if (isTrueCk == null) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "请先获取验证码！");
                return map;
            }
            WebRequest request1 = null;
            // ===========需要图形验证码==================================
            if (MessageConstamts.STATUS_TRUE.equals(isTrue)) {
                // 1.读取页面验证码图片到本地
                // 动态码url
                String imageUrl = "https://uac.10010.com/portal/Service/CreateImage?t="
                        + System.currentTimeMillis();
                UnexpectedPage imagePage = webClient.getPage(imageUrl);
                BufferedImage bufferedImage = ImageIO.read(imagePage
                        .getInputStream());
                String findImage = "gd" + System.currentTimeMillis() + ".png";
                String path=request.getServletContext().getRealPath("/unicomImage");
                File file=new File(path);
                if(!file.exists()){
                    file.mkdirs();
                }
                ImageIO.write(bufferedImage, "png", new File(file,
                        findImage));
                // 2.转码
                Map<String, Object> imagev = MyCYDMDemo.Imagev(path+File.separator
                        + findImage);
                // 转码后的动态码
                String catpy = (String) imagev.get("strResult");
                // System.out.println(catpy+"-***-*-");
                WebRequest webRequest3 = new WebRequest(
                        new URL(
                                "https://uac.10010.com/portal/Service/CtaIdyChk?callback=jQuery17207654655044488388_"
                                        + System.currentTimeMillis()
                                        + "&verifyCode="
                                        + catpy
                                        + "&verifyType=1&_="
                                        + System.currentTimeMillis()));
                webRequest3.setHttpMethod(HttpMethod.GET);
                webRequest3.setAdditionalHeader("Referer",
                        "https://uac.10010.com/portal/homeLogin");
                HtmlPage page2 = webClient.getPage(webRequest3);
                String resultInfo = page2.getWebResponse().getContentAsString();
                String tips = resultInfo.split("\\(")[1].split("\\)")[0];
                JSONObject jsons = JSONObject.fromObject(tips);
                String tipInfo = jsons.get("resultCode").toString();
                if (MessageConstamts.STATUS_TRUE.equals(isTrueCk)) {
                    // ===========需要图形验证码，短信验证码===========================
                    // 图形验证码正确可以发包登陆
                    if (MessageConstamts.STATUS_TRUE.equals(tipInfo)) {
                        System.out.println("图形验证码正确");
                        Set<Cookie> cookies = webClient.getCookieManager()
                                .getCookies();
                        String uvc = "";
                        for (Cookie c : cookies) {
                            if ("uacverifykey".equals(c.getName())) {
                                uvc = c.getValue();
                            }
                            webClient.getCookieManager().addCookie(c);

                        }
                        request1 = new WebRequest(
                                new URL(
                                        "https://uac.10010.com/portal/Service/MallLogin?callback=jQuery17207654655044488388_"
                                                + System.currentTimeMillis()
                                                + "&req_time="
                                                + System.currentTimeMillis()
                                                + "&redirectURL=http://www.10010.com&userName="
                                                + userIphone
                                                + "&password="
                                                + password
                                                + "&pwdType=01&productType=01&verifyCode="
                                                + catpy
                                                + "&uvc="
                                                + uvc
                                                + "&redirectType=01&rememberMe=1&verifyCKCode="
                                                + code
                                                + "&_="
                                                + System.currentTimeMillis()));
                    } else {

                        System.out.println("图形验证码 错误");
                        map.put("errorCode", "0001");
                        map.put("errorInfo", "验证码 错误");
                        return map;
                    }
                } else {
                    // ===========需要图形验证码，不需要短信===========================
                    // 图形验证码正确可以发包登陆
                    if (MessageConstamts.STATUS_TRUE.equals(tipInfo)) {
                        System.out.println("图形验证码正确");
                        Set<Cookie> cookies = webClient.getCookieManager()
                                .getCookies();
                        String uvc = "";
                        for (Cookie c : cookies) {
                            if ("uacverifykey".equals(c.getName())) {
                                uvc = c.getValue();
                            }
                            webClient.getCookieManager().addCookie(c);

                        }
                        request1 = new WebRequest(
                                new URL(
                                        "https://uac.10010.com/portal/Service/MallLogin?callback=jQuery17207654655044488388_"
                                                + System.currentTimeMillis()
                                                + "&req_time="
                                                + System.currentTimeMillis()
                                                + "&redirectURL=http://www.10010.com&userName="
                                                + userIphone
                                                + "&password="
                                                + password
                                                + "&pwdType=01&productType=01&verifyCode="
                                                + catpy
                                                + "&uvc="
                                                + uvc
                                                + "&redirectType=01&rememberMe=1&_="
                                                + System.currentTimeMillis()));
                    } else {

                        System.out.println("图形验证码 错误");
                        map.put("errorCode", "0001");
                        map.put("errorInfo", "验证码 错误");
                        return map;
                    }

                }
            } else {
                if (MessageConstamts.STATUS_TRUE.equals(isTrueCk)) {
                    // ===========不需要图形验证码,需要短信===========================
                    request1 = new WebRequest(
                            new URL(
                                    "https://uac.10010.com/portal/Service/MallLogin?callback=jQuery17209863190566662376_"
                                            + System.currentTimeMillis()
                                            + "&req_time="
                                            + System.currentTimeMillis()
                                            + "&redirectURL=http://www.10010.com&userName="
                                            + userIphone
                                            + "&password="
                                            + password
                                            + "&pwdType=01&productType=01&redirectType=01&rememberMe=1&verifyCKCode="
                                            + code
                                            + "&_="
                                            + System.currentTimeMillis()));
                } else {
                    // ===========不需要图形验证码,不需要短信(不会发生)===========================
                    request1 = new WebRequest(
                            new URL(
                                    "https://uac.10010.com/portal/Service/MallLogin?callback=jQuery17209863190566662376_"
                                            + System.currentTimeMillis()
                                            + "&req_time="
                                            + System.currentTimeMillis()
                                            + "&redirectURL=http://www.10010.com&userName="
                                            + userIphone
                                            + "&password="
                                            + password
                                            + "&pwdType=01&productType=01&redirectType=01&rememberMe=1&_="
                                            + System.currentTimeMillis()));
                }
            }

            request1.setHttpMethod(HttpMethod.GET);
            request1.setAdditionalHeader("Referer",
                    "https://uac.10010.com/portal/homeLogin");
            HtmlPage page2 = webClient.getPage(request1);
            Thread.sleep(500);
            String tip = page2.asText();
            String tips = tip.split("\\(")[1].split("\\)")[0];
            System.out.println(tips);
            JSONObject json = JSONObject.fromObject(tips);
            String tipInfo = json.get("resultCode").toString();
            if (MessageConstamts.STRING_0000.equals(tipInfo)
                    || MessageConstamts.STRING_0301.equals(tipInfo)) {

                map.put("errorCode", "0000");
                map.put("errorInfo", "登陆成功！");
                session.setAttribute("webClientSucce", webClient);
            } else {
                // System.out.println(json.get("msg").toString());
                map.put("errorCode", "0001");
                if (json.get(MessageConstamts.MSG).toString()
                        .contains(MessageConstamts.CODEFAIL)) {
                    map.put("errorInfo", "验证码错误");
                    // }else if(tipInfo.equals("7007")&&tipInfo.contains("")){
                    // System.out.println(tipInfo.length());
                    // String a=json.get("msg").toString();
                    // System.out.println(a.split("\\<a")[0]);
                    // map.put("errorInfo", "用户名或密码不正确，还有3次机会");

                } else {
                    map.put("errorInfo", json.get("msg").toString());
                }
            }

        } catch (Exception e) {
            map.put("errorInfo", "0001");
            map.put("errorInfo", "网络异常");
            System.out.println(e);

        }
        return map;

    }

    /**
     * 联通 获取详单的 验证码
     *
     * @param
     * @param
     * @param
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public Map<String, Object> getCodeTwo(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>(8);
        HttpSession session = request.getSession();
        WebClient webClient = (WebClient) session
                .getAttribute("webClientSucce");
        System.out.println("登陆成功");
        if (webClient == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "网络异常");
            return map;
        }
        // 打开获取详单页面
        try {
            Thread.sleep(1000);
            HtmlPage detailPage = webClient
                    .getPage("http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001");

            Thread.sleep(8000);
            // System.out.println(detailPage.asXml());
            // ============登陆信息校验===========================
            webClient.addRequestHeader("Accept",
                    "application/json, text/javascript, */*; q=0.01");
            webClient.addRequestHeader("Accept-Encoding", "gzip, deflate");
            webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
            webClient.addRequestHeader("Connection", "keep-alive");
            webClient.addRequestHeader("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            webClient
                    .addRequestHeader(
                            "Referer",
                            "http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001");
            webClient.addRequestHeader("X-Requested-With", "XMLHttpRequest");

            List<NameValuePair> paramer3 = new ArrayList<NameValuePair>();
            paramer3.add(new NameValuePair("_", System.currentTimeMillis() + ""));
            WebRequest webRequest3 = new WebRequest(new URL(
                    "http://iservice.10010.com/e3/static/check/checklogin?_="
                            + System.currentTimeMillis()));
            webRequest3.setHttpMethod(HttpMethod.POST);
            webRequest3.setRequestParameters(paramer3);
            TextPage nextPage = webClient.getPage(webRequest3);
            Thread.sleep(3000);
            // System.out.println(nextPage.getContent());
            // ====================检验是否需要验证码===========

            // =====================获取验证码================================================
            List<NameValuePair> paramer = new ArrayList<NameValuePair>();
            String time = "" + System.currentTimeMillis();
            paramer.add(new NameValuePair("_", time));
            paramer.add(new NameValuePair(
                    "accessURL",
                    "http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001"));
            paramer.add(new NameValuePair("menuid", "000100030001"));
            paramer.add(new NameValuePair("menuId", "000100030001"));
            WebRequest webRequest1 = new WebRequest(
                    new URL(
                            "http://iservice.10010.com/e3/static/query/sendRandomCode?_="
                                    + time
                                    + "&accessURL=http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001&menuid=000100030001"));
            webRequest1.setHttpMethod(HttpMethod.POST);
            webRequest1.setRequestParameters(paramer);
            // ==============暂时修改===================
            if (webClient.getPage(webRequest1).isHtmlPage()) {

                map.put("errorCode", "0001");
                map.put("errorInfo", "验证码发送失败");
                return map;
            } else {
                TextPage next = webClient.getPage(webRequest1);
                // System.out.println(next.getContent());
                JSONObject json1 = JSONObject.fromObject(next.getContent());
                // String tips1=json1.get("issuccess").toString();
                String tips2 = json1.get("sendcode").toString();
                if (MessageConstamts.STATUS_TRUE.equals(tips2)) {
                    System.out.println("验证码发送成功");
                    map.put("errorCode", "0000");
                    map.put("errorInfo", "验证发送成功");
                    session.setAttribute("webClientTwo", webClient);
                } else {

                    map.put("errorCode", "0001");
                    map.put("errorInfo", "验证码发送失败");
                    System.out.println("验证码发送失败");
                }
                // ===========================
            }

            Thread.sleep(1000);

        } catch (Exception e) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "网络异常");
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 联通通话详单
     */

    public Map<String, Object> getDetial(HttpServletRequest request,
                                         String userIphone, String userPassword, String code,
                                         String longitude, String latitude, String uuId) {
        Map<String, Object> map = new HashMap<String, Object>(8);
        try {
            PushState.state(userIphone, "callLog", 100);
            PushSocket.pushnew(map, uuId, "1000", "登录中");
            Thread.sleep(2000);

            List<Map<String, Object>> listsy = new ArrayList<Map<String, Object>>();
            String info = "";
            HttpSession session = request.getSession();
            WebClient webClient = (WebClient) session
                    .getAttribute("webClientTwo");
            if (webClient == null) {
                // PushSocket.push(map, UUID, "0001");
                map.put("errorCode", "0001");
                map.put("errorInfo", "请先获取验证码！");
                PushSocket.pushnew(map, uuId, "3000", "请先获取验证码！");
                PushState.state(userIphone, "callLog", 200, "请先获取验证码！");
                return map;
            }
            // System.out.println("验证码发送成功");
            // =======================确定验证码===============================================
            String verCode = "http://iservice.10010.com/e3/static/query/verificationSubmit?_="
                    + System.currentTimeMillis()
                    + "&accessURL=http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001&menuid=000100030001";
            WebRequest webRequest2 = new WebRequest(new URL(verCode));
            webRequest2.setHttpMethod(HttpMethod.POST);
            List<NameValuePair> paramer2 = new ArrayList<NameValuePair>();
            paramer2.add(new NameValuePair("_", System.currentTimeMillis() + ""));
            paramer2.add(new NameValuePair(
                    "accessURL",
                    "http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001"));
            paramer2.add(new NameValuePair("menuid", "000100030001"));
            paramer2.add(new NameValuePair("menuId", "000100030001"));
            paramer2.add(new NameValuePair("inputcode", code));
            webRequest2.setRequestParameters(paramer2);

            // if(webClient.getPage(webRequest2).isHtmlPage()){
            //
            // }
            String flag = "";
            try {
                TextPage newPage = webClient.getPage(webRequest2);
                Thread.sleep(1000);
                // System.out.println(newPage.getContent());
                JSONObject json3 = JSONObject.fromObject(newPage.getContent());
                String resultCode = json3.get("flag").toString();
                if (MessageConstamts.STRING_00.equals(resultCode)) {
                    // ---------------推-------------------
                    PushSocket.pushnew(map, uuId, "2000", "登录成功");
                    // ---------------推-------------------
                    // =======================获取详单================================================
                    PushSocket.pushnew(map, uuId, "5000", "获取数据中");
                    flag = "5000";
                    webClient.addRequestHeader("Accept",
                            "application/json, text/javascript, */*; q=0.01");
                    webClient.addRequestHeader("Accept-Encoding",
                            "gzip, deflate");
                    webClient.addRequestHeader("Accept-Language",
                            "zh-CN,zh;q=0.8");
                    webClient.addRequestHeader("Connection", "keep-alive");
                    // webClient.addRequestHeader("Content-Length","56");
                    webClient.addRequestHeader("Content-Type",
                            "application/x-www-form-urlencoded;charset=UTF-8");
                    webClient.addRequestHeader("Host", "iservice.10010.com");
                    webClient.addRequestHeader("Origin",
                            "http://iservice.10010.com");
                    webClient
                            .addRequestHeader(
                                    "Referer",
                                    "http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001");
                    webClient
                            .addRequestHeader(
                                    "User-Agent",
                                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
                    webClient.addRequestHeader("X-Requested-With",
                            "XMLHttpRequest");
                    JSONArray array = new JSONArray();
                    // 开始是时间
                    String begin = GetMonth.nowMonth() + "01";
                    // 结束时间
                    String end = GetMonth.today();
                    int year = new Integer(begin.substring(0, 4));
                    // 获取月 作为获取每个月的最后一天的参数
                    int month = new Integer(begin.substring(4, 6));
                    // 上月
                    String beforMonth = "";
                    for (int i = MessageConstamts.INT_1; i < MessageConstamts.INT_7; i++) {
                        Map<String, Object> dataMap = new HashMap<String, Object>(
                                8);
                        System.out.println(begin + "*****" + end);
                        WebRequest webRequestss = new WebRequest(
                                new URL(
                                        "http://iservice.10010.com/e3/static/query/callDetail?_="
                                                + System.currentTimeMillis()
                                                + "&accessURL=http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001&menuid=000100030001"));
                        List<NameValuePair> lists = new ArrayList<NameValuePair>();
                        lists.add(new NameValuePair("pageNo", "1"));
                        lists.add(new NameValuePair("pageSize", "2000"));
                        lists.add(new NameValuePair("beginDate", begin));
                        lists.add(new NameValuePair("endDate", end));
                        webRequestss.setHttpMethod(HttpMethod.POST);
                        webRequestss.setRequestParameters(lists);
                        TextPage chekpages = webClient.getPage(webRequestss);
                        System.out.println(chekpages.getContent() + "----vvv");
                        // 上i月
                        beforMonth = GetMonth.beforMon(year, month, i);
                        begin = beforMonth + "01";
                        int y = new Integer(begin.substring(0, 4));
                        int m = new Integer(begin.substring(4, 6));
                        // 一个月的最后一天
                        end = GetMonth.lastDate(y, m);
                        dataMap.put("item", chekpages.getContent());
                        listsy.add(dataMap);
                    }

                    PushSocket.pushnew(map, uuId, "6000", "数据获取成功");
                    flag = "6000";

                    map.put("errorCode", "0000");
                    map.put("errorInfo", "查询成功");
                    map.put("data", listsy);
                    map.put("UserIphone", userIphone);
                    map.put("UserPassword", userPassword);
                    map.put("longitude", longitude);
                    map.put("latitude", latitude);
                    // map=resttemplate.SendMessage(map,
                    // "http://192.168.3.35:8080/HSDC/message/linkCallRecord");//魏艳
                    // map=resttemplate.SendMessage(map,
                    // "http://192.168.3.4:8081/HSDC/message/linkCallRecord");//胡献根
                    Resttemplate resttemplate=new Resttemplate();
                    map = resttemplate.SendMessage(map, application.getSendip()
                            + "/HSDC/message/linkCallRecord");
                    System.out.println("推送后==" + map);
                    if (MessageConstamts.STRING_0000.equals(map.get(
                            MessageConstamts.ERRORCODE).toString())) {

                        PushState.state(userIphone, "callLog", 300);
                        map.put("errorInfo", "推送成功");
                        map.put("errorCode", "0000");
                        PushSocket.pushnew(map, uuId, "8000", "认证成功");
                    } else {
                        // PushSocket.push(map, UUID, "0001");
                        // --------------------数据中心推送状态----------------------
                        PushState.state(userIphone, "callLog", 200, map.get("errorInfo").toString());
                        // ---------------------数据中心推送状态---------------------
                        map.put("errorInfo", map.get("errorInfo").toString());
                        map.put("errorCode", "0001");
                        PushSocket.pushnew(map, uuId, "9000", map.get("errorInfo").toString());
                    }
                } else {
                    // ---------------推-------------------
                    System.out.println(json3.get("error").toString());
                    if (resultCode.equals(MessageConstamts.STRING_01)) {
                        map.put("errorCode", "0001");
                        map.put("errorInfo", "验证码已过期，请从新获取新的验证码");
                        PushState.state(userIphone, "callLog", 200, "验证码已过期，请从新获取新的验证码");
                        PushSocket.pushnew(map, uuId, "3000","验证码已过期，请从新获取新的验证码");
                        // 验证码错误
                    } else if (resultCode.equals(MessageConstamts.STRING_02)) {
                        map.put("errorCode", "0001");
                        map.put("errorInfo", "校验失败");
                        PushSocket.pushnew(map, uuId, "3000", "校验失败");
                        PushState.state(userIphone, "callLog", 200, "校验失败");
                        // sessionFail//session失效
                    } else if (resultCode.equals(MessageConstamts.STRING_03)) {
                        map.put("errorCode", "0001");
                        map.put("errorInfo", "校验失败,请稍后再试！");
                        PushSocket.pushnew(map, uuId, "3000", "校验失败,请稍后再试！");
                        PushState.state(userIphone, "callLog", 200, "校验失败,请稍后再试！");
                    } else if (resultCode.equals(MessageConstamts.STRING_04)) {
                        map.put("errorCode", "0001");
                        map.put("errorInfo", "验证码不能为空");
                        PushSocket.pushnew(map, uuId, "3000", "验证码不能为空");
                        PushState.state(userIphone, "callLog", 200, "验证码不能为空");
                    } else {
                        map.put("errorCode", "0001");
                        map.put("errorInfo", json3.get("error").toString());
                        PushSocket.pushnew(map, uuId, "3000",json3.get("error").toString());
                        PushState.state(userIphone, "callLog", 200, json3.get("error").toString());
                    }
                }
            } catch (ClassCastException e) {
                map.put("errorInfo", "验证失败,稍后再试");
                map.put("errorCode", "0001");
                if (flag.equals(MessageConstamts.STRING_5000)) {
                    PushSocket.pushnew(map, uuId, "7000", "数据获失败");
                    PushState.state(userIphone, "callLog", 200, "验证失败,稍后再试");
                } else if (flag.equals(MessageConstamts.STRING_6000)) {
                    PushSocket.pushnew(map, uuId, "9000", "认证失败,网络异常");
                    PushState.state(userIphone, "callLog", 200, "认证失败,网络异常");
                }
                return map;
            }
        } catch (Exception e) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "网络异常");
            PushSocket.pushnew(map, uuId, "3000", "登录失败，网络异常");
            PushState.state(userIphone, "callLog", 200, "登录失败，网络异常");
            e.printStackTrace();
        }
        return map;

    }

}
