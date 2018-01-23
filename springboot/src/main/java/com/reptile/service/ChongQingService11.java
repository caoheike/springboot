package com.reptile.service;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.ConstantInterface;
import com.reptile.util.DealExceptionSocketStatus;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: ChongQingTelecomService
 * @Description: TODO
 * @author: 111
 * @date 2018年1月2日
 */

@Service
public class ChongQingService11 {
    private Logger logger = LoggerFactory.getLogger(ChongQingService11.class);

    @Autowired
    private application application;

    /**
     * 获取验证码
     *
     * @param request
     * @param phoneNum
     * @return
     */
    public Map<String, Object> sendCode(HttpServletRequest request, String phoneNum) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        // 从session中获得webClient
        Object attr = request.getSession().getAttribute("GBmobile-webclient");
        // WebClient webClient = (WebClient) attribute;
        if (attr == null) {
            logger.warn("重庆电信--请先登录!");
            map.put("errorCode", "0001");
            map.put("errorInfo", "请先登录!");
            return map;
        } else {
            try {
                WebClient webClient = (WebClient) attr;
                HtmlPage nextPage = webClient
                        .getPage("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=02031273");

                WebRequest get = new WebRequest(new URL(
                        "http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10004&toStUrl=http://cq.189.cn/new-bill/bill_xd?fastcode=02031273&cityCode=cq"));
                get.setHttpMethod(HttpMethod.GET);
                HtmlPage choosepage = webClient.getPage(get);

                WebRequest post = new WebRequest(new
                        URL("http://cq.189.cn/new-bill/bill_DXYZM"));
                post.setHttpMethod(HttpMethod.POST);
                Page page = webClient.getPage(post);
                System.out.println(page.getWebResponse().getContentAsString());
                if (page.getWebResponse() == null) {
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "对不起，系统忙，请稍候再试！");
                    return map;
                } else {
                    JSONObject getJson =
                            JSONObject.fromObject(page.getWebResponse().getContentAsString());
                    if (getJson.getString("errorCode").equals("0")) {
                        request.getSession().setAttribute("chongqingWebclient", webClient);
                        request.getSession().setAttribute("choosepage-chongqing", choosepage);

                        map.put("errorCode", "0000");
                        map.put("errorInfo", "验证码发送成功");
                    } else {
                        map.put("errorCode", "0001");
                        map.put("errorInfo", getJson.getString("errorDescription"));
                        return map;
                    }

                }
            } catch (Exception e) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络异常!");
                logger.error("----------重庆电信-----------", e);
            }
            return map;

        }

    }

    /**
     * 当月第一天
     *
     * @param i
     * @return
     */
    public static String getFirstDay(int i) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String firstday = "";
        Calendar cale = null;
        // 获取前月的第一天i从0开始
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, -i);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        firstday = format1.format(cale.getTime());

        return firstday;
    }

    /**
     * 获得上个月 i从0开始
     *
     * @param i
     * @return
     */
    public static String beforMonth(int i) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -i);
        Date m = c.getTime();
        String mon = format.format(m);
        return mon;
    }

    /**
     * 当月最后一天
     *
     * @param i
     * @return
     */
    public static String getLastDay(int i) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String lastday = "";
        Calendar cale = null;
        // 获取前月的最后一天 i从1开始
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, -i + 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        lastday = format1.format(cale.getTime());
        return lastday;
    }

    /**
     * 重庆电信获取详单
     *
     * @param request
     * @param phoneNumber
     * @param passWord
     * @param code
     * @param longitude
     * @param latitude
     * @param uuid
     * @param userName
     * @return idCard
     */

    public Map<String, Object> getDetail(HttpServletRequest request, String phoneNumber, String passWord, String code,
                                         String longitude, String latitude, String uuid, String userName, String idCard) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        PushState.state(phoneNumber, "callLog", 100);
        PushSocket.pushnew(map, uuid, "1000", "登录中");
        String signle = "1000";
        List<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
        Object attr = request.getSession().getAttribute("chongqingWebclient");
        if (attr == null) {
            logger.warn("请先获取短信验证码!");
            map.put("errorCode", "0001");
            map.put("errorInfo", "请先获取短信验证码!");
            PushState.state(phoneNumber, "callLog", 200, "请先获取短信验证码!");
            PushSocket.pushnew(map, uuid, "3000", "请先获取短信验证码!");
            return map;
        }
        try {
            WebClient webClient = (WebClient) attr;
            Object attr1 = request.getSession().getAttribute("choosepage-chongqing");
            HtmlPage codePage = (HtmlPage) attr1;
            Thread.sleep(1000);
            HtmlElement name = (HtmlElement) codePage.getByXPath("/html/body/div/div/table[2]/tbody/tr[1]/td/span[1]")
                    .get(0);
            Thread.sleep(1000);
            String firstName = name.asText().trim();
            // 截取名
            String tname = userName.substring(firstName.length(), userName.length());
            Thread.sleep(1000);

            String attribute2 = codePage.getElementById("accnbr_div").getAttribute("val");
            String productId = (attribute2.split("#"))[1];

            for (int i = 0; i < 7; i++) {
                WebRequest post = new WebRequest(new URL("http://cq.189.cn/new-bill/bill_XDCXNR"));
                List<NameValuePair> dataList = new ArrayList<>();
                dataList.add(new NameValuePair("accNbr", phoneNumber));
                dataList.add(new NameValuePair("productId", productId));
                dataList.add(new NameValuePair("month", beforMonth(i)));
                dataList.add(new NameValuePair("callType", "01"));
                dataList.add(new NameValuePair("listType", "300001"));
                dataList.add(new NameValuePair("beginTime", getFirstDay(i)));
                dataList.add(new NameValuePair("endTime", getLastDay(i)));
                dataList.add(new NameValuePair("rc", code));
                dataList.add(new NameValuePair("tname", tname));
                dataList.add(new NameValuePair("idcard", idCard));
                dataList.add(new NameValuePair("zq", "2"));
                post.setRequestParameters(dataList);
                post.setCharset("utf-8");
                post.setHttpMethod(HttpMethod.POST);
                Page pages = webClient.getPage(post);
                if (pages.getWebResponse()== null) {
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "对不起，系统忙，请稍候再试！");
                    return map;

                } else {
                    String result = pages.getWebResponse().getContentAsString();
                    System.out.println(result);
                    if (result.contains("xm") && result.contains("sfz")) {
                        map.put("errorCode", "0001");
                        map.put("errorInfo", "身份校验不通过！");
                        return map;
                    } else if (result.contains("xm")) {
                        map.put("errorCode", "0002");
                        map.put("errorInfo", "姓名错误");
                        return map;
                    } else if (result.contains("sfz")) {
                        map.put("errorCode", "0003");
                        map.put("errorInfo", "身份证后六位错误");
                        return map;
                    } else if (result.contains("message")) {
                        JSONObject json = JSONObject.fromObject(result);
                        String tip = (String) json.get("message");
                        if (tip.equals("2333")) {
                            map.put("errorCode", "0004");
                            map.put("errorInfo", "短信验证码错误");
                            PushSocket.pushnew(map, uuid, "3000", "验证码错误");
                            PushState.state(phoneNumber, "callLog", 200, "验证码错误");
                            return map;
                        } else if (tip.equals("0")) {
                            logger.warn("----------------重庆电信，正在获取数据...---------------------");
                            // 校验成功
                            // 获取数据
                            PushSocket.pushnew(map, uuid, "2000", "登录成功");
                            Thread.sleep(2000);
                            PushSocket.pushnew(map, uuid, "5000", "数据获取中");
                            signle = "5000";
                            Map<String, Object> dataMap = new HashMap<String, Object>(16);
                            HttpClient httpClient = new HttpClient();
                            Set<com.gargoylesoftware.htmlunit.util.Cookie> cookie = webClient.getCookieManager()
                                    .getCookies();
                            StringBuffer cookies = new StringBuffer();
                            for (com.gargoylesoftware.htmlunit.util.Cookie c : cookie) {
                                cookies.append(c.toString() + ";");
                            }

                            PostMethod post1 = new PostMethod("http://cq.189.cn/new-bill/bill_XDCX_Page");
                            post1.setRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
                            post1.setRequestHeader("Connection", "keep-alive");
                            post1.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                            post1.setRequestHeader("Origin", "http://nx.189.cn");
                            post1.setRequestHeader("Referer",
                                    "http://cq.189.cn/new-bill/bill_xd?fastcode=02031273&cityCode=cq");
                            post1.setRequestHeader("Cookie", cookies.toString());
                            post1.setParameter("page", "1");
                            post1.setParameter("rows", "2000");
                            httpClient.executeMethod(post1);
                            String html = post1.getResponseBodyAsString();
                            dataMap.put("item", html);
                            arrayList.add(dataMap);
                        } else {
                            logger.warn("----------------重庆电信,数据获取失败---------------------");
                            map.put("errorCode", "0001");
                            map.put("errorInfo", "系统繁忙!");
                            PushSocket.pushnew(map, uuid, "3000", "登录失败,系统繁忙!");
                            PushState.state(phoneNumber, "callLog", 200, "登录失败,系统繁忙!");
                            return map;
                        }
                    }
                }
            }

            PushSocket.pushnew(map, uuid, "6000", "数据获取成功");
            signle = "4000";

            logger.warn("----------------重庆电信,数据获取成功---------------------");
            // ------------推数据------------------------
            map.put("data", arrayList);
            map.put("UserIphone", phoneNumber);
            map.put("UserPassword", passWord);
            // 经度
            map.put("longitude", longitude);
            // 纬度
            map.put("latitude", latitude);
            map.put("flag", "15");
            Resttemplate resttemplate = new Resttemplate();
            map = resttemplate.SendMessage(map, ConstantInterface.port + "/HSDC/message/telecomCallRecord");
            String ss = "0000";
            String errorCode = "errorCode";
            if (map.get(errorCode).equals(ss)) {
                PushSocket.pushnew(map, uuid, "8000", "认证成功");
                PushState.state(phoneNumber, "callLog", 300);
            } else {
                PushSocket.pushnew(map, uuid, "9000", map.get("errorInfo").toString());
                PushState.state(phoneNumber, "callLog", 200, map.get("errorInfo").toString());
            }

        } catch (Exception e) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "网络异常!");
            PushState.state(phoneNumber, "callLog", 200, "网络异常!");
            DealExceptionSocketStatus.pushExceptionSocket(signle, map, uuid);
            logger.error("-------------重庆电信-------------", e);
        }

        return map;
    }

}
