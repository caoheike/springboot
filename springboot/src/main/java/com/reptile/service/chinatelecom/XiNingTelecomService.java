package com.reptile.service.chinatelecom;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.springboot.Scheduler;
import com.reptile.util.ConstantInterface;
import com.reptile.util.DealExceptionSocketStatus;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;

/**
 * 西宁电信
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Service
public class XiNingTelecomService {
    private Logger logger = LoggerFactory.getLogger(XiNingTelecomService.class);


    /**
     * 西宁电信登录开始
     *
     * @param req
     * @param phone
     * @param servePwd
     * @return
     */
    public Map<String, Object> doLogin(HttpServletRequest req, String phone, String servePwd) {

        Map<String, Object> map = new HashMap<String, Object>(16);
        WebClient webClient = new WebClientFactory().getWebClient();
        logger.warn("----------------西宁电信登录开始。用户名：" + phone + "服务密码：" + servePwd + "----------------");
        try {
            //登录
            HtmlPage loginPage = webClient.getPage("http://login.189.cn/web/login");

            loginPage.getElementById("txtPassword").setAttribute("value", servePwd);

            //监控alert弹窗
            List<String> alertList = new ArrayList<String>();
            CollectingAlertHandler alert = new CollectingAlertHandler(alertList);
            webClient.setAlertHandler(alert);

            loginPage.executeJavaScript("alert($('#txtPassword').valAesEncryptSet())");
            //获取加密后的密码
            String password = "";
            if (alertList.size() > 0) {
                password = alertList.get(0);
            }
            //发送验证码
            WebRequest request = new WebRequest(new URL("http://login.189.cn/web/login/ajax"));
            //请求入参
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new NameValuePair("m", "captcha"));
            list.add(new NameValuePair("account", phone));
            list.add(new NameValuePair("uType", "201"));
            list.add(new NameValuePair("ProvinceID", "29"));
            list.add(new NameValuePair("areaCode", ""));
            list.add(new NameValuePair("cityNo", ""));
            request.setRequestParameters(list);
            //POST请求
            request.setHttpMethod(HttpMethod.POST);
            //发送请求
            Page page1 = webClient.getPage(request);
            //若验证码返回为TRUE
            String result = page1.getWebResponse().getContentAsString();
            String trueStr = "true";
            if (result.contains(trueStr)) {
                //获取验证码
                String catpy = this.getCode(webClient, req);
                HtmlPage login = this.getLogin(webClient, req, phone, password, catpy);

                Thread.sleep(2000);
                String signleStr1 = "详细查询";
                String signleStr2 = "详单查询";
                String signleStr3 = "账单查询";

                if (!login.asText().contains(signleStr1) && !login.asText().contains(signleStr2) && !login.asText().contains(signleStr3)) {
                    String divErr = login.getElementById("divErr").getTextContent();
                    String validateCode = "验证码";
                    if (divErr.contains(validateCode)) {
                        map.put("errorCode", "0008");
                        map.put("errorInfo", "服务器繁忙，请刷新后重试");
                    } else {
                        map.put("errorCode", "0007");
                        map.put("errorInfo", divErr);
                    }
                    logger.warn("----------------西宁电信登录失败。用户名：" + phone + "返回信息为：" + map + "----------------");
                } else {
                    logger.warn("电信登录成功。用户名：" + phone);
                    map.put("errorCode", "0000");
                    map.put("errorInfo", "登陆成功");
                    req.getSession().setAttribute("xiNingWebclient", webClient);
                }
            }

        } catch (HttpHostConnectException e) {
            logger.error(phone + "----------------mrlu 西宁电信网络连接异常----------------", e);
            Scheduler.sendGet(Scheduler.getIp);
            map.put("errorCode", "0001");
            map.put("errorInfo", "网络错误，请重试！");
        } catch (Exception e) {
            logger.error(phone + "----------------mrlu 西宁电信网络连接异常----------------", e);
            Scheduler.sendGet(Scheduler.getIp);
            map.put("errorCode", "0002");
            map.put("errorInfo", "系统繁忙!");
        } finally {
            if (webClient != null) {
                webClient.close();
            }
        }
        return map;
    }


    /**
     * 获取详情
     *
     * @param request
     * @param phoneNumber
     * @param serverPwd
     * @param longitude
     * @param latitude
     * @param uuid
     * @return
     */
    public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String serverPwd, String longitude, String latitude, String uuid) {
        logger.warn(phoneNumber + "：---------------------西宁电信获取详单...---------------------");
        Map<String, Object> map = new HashMap<String, Object>(16);
        PushSocket.pushnew(map, uuid, "1000", "登录中");
        PushState.state(phoneNumber, "callLog", 100);
        String signle = "1000";

        List<String> dataList = new ArrayList<String>();
        HttpSession session = request.getSession();

        Object attribute = session.getAttribute("GBmobile-webclient");
        if (attribute == null) {
            logger.warn(phoneNumber + "：---------------------西宁电信获取详单未调用公共接口---------------------");
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            PushState.state(phoneNumber, "callLog", 200, "登录失败,操作异常!");
            PushSocket.pushnew(map, uuid, "3000", "登录失败,操作异常!");
            return map;
        } else {
            logger.warn(phoneNumber + "：---------------------西宁电信获取详单开始---------------------");
            PushSocket.pushnew(map, uuid, "2000", "登录成功");
            WebClient webClient = (WebClient) attribute;
            try {
                Thread.sleep(1000);
                PushSocket.pushnew(map, uuid, "5000", "数据 获取中");
                signle = "5000";
                WebRequest requests = new WebRequest(new URL("http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10029&toStUrl=http://qh.189.cn/service/bill/fee.action?type=ticket&fastcode=00920926&cityCode=qh"));
                requests.setHttpMethod(HttpMethod.GET);
                webClient.getPage(requests);

                SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                Date time = calendar.getTime();
                //当前时间
                String beginTime = simple.format(time);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                Date endTimes = calendar.getTime();
                //月初时间
                String endTime = simple.format(endTimes);
                int bountCount = 6;
                try {
                    for (int j = 0; j < bountCount; j++) {
                        int i = 1;
                        while (i > 0) {
                            WebRequest webRequest = new WebRequest(new URL("http://qh.189.cn/service/bill/feeDetailrecordList.action"));
                            webRequest.setHttpMethod(HttpMethod.POST);
                            List<NameValuePair> list = new ArrayList<NameValuePair>();
                            list.add(new NameValuePair("currentPage", String.valueOf(i)));
                            list.add(new NameValuePair("pageSize", "100"));
                            list.add(new NameValuePair("effDate", endTime));
                            list.add(new NameValuePair("expDate", beginTime));
                            list.add(new NameValuePair("serviceNbr", phoneNumber));
                            list.add(new NameValuePair("operListID", "12"));
                            list.add(new NameValuePair("pOffrType", "4"));
                            list.add(new NameValuePair("sendSmsFlag", "true"));
                            list.add(new NameValuePair("num", "1"));
                            list.add(new NameValuePair("callTypeVal", "0"));
                            webRequest.setRequestParameters(list);
                            HtmlPage page = webClient.getPage(webRequest);
                            String result = page.asXml();
                            //数据logger
                            logger.warn(phoneNumber + "   " + beginTime + "：---------------------西宁电信获取详------------本次获取数据详情：" + result);
                            if (result.contains("无话单记录！")) {
                                break;
                            }
                            if (i > 8) {
                                break;
                            }
                            Thread.sleep(1000);
                            dataList.add(result);
                            i++;
                        }
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        beginTime = simple.format(calendar.getTime());
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        endTime = simple.format(calendar.getTime());
                    }
                } catch (Exception e) {
                    logger.error(phoneNumber + "   " + beginTime + "：----------------西宁获取过程中出现异常(循环获取数据过程中)---------------", e);
                    Scheduler.sendGet(Scheduler.getIp);
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "数据获取失败，网络异常");
                    PushState.state(phoneNumber, "callLog", 200, "数据获取失败，网络异常");
                    PushSocket.pushnew(map, uuid, "7000", "数据获取失败，网络异常");
                    return map;
                }

//                logger.warn(phoneNumber + "：---------------------西宁电信获取详单结束---------------------本次获取账单数目:" + dataList.size());
                logger.warn(phoneNumber + "：---------------------西宁电信获取详单结束---------------------本次获取账单数目:" + dataList.toString());

                if(dataList.size()<1){
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "数据获取不完全，请重新认证！(注：请确认手机号使用时长超过6个月)");
                    PushState.state(phoneNumber, "callLog", 200, "数据获取不完全，请重新认证！(注：请确认手机号使用时长超过6个月)");
                    PushSocket.pushnew(map, uuid, "7000", "数据获取不完全，请重新认证！(注：请确认手机号使用时长超过6个月)");
                    return map;
                }

                PushSocket.pushnew(map, uuid, "6000", "获取数据成功");
                signle = "4000";
                map.put("data", dataList);
                map.put("flag", "2");
                map.put("UserPassword", serverPwd);
                map.put("UserIphone", phoneNumber);
                //经度
                map.put("longitude", longitude);
                //纬度
                map.put("latitude", latitude);

                logger.warn(phoneNumber + "：---------------------西宁电信获取详单推送数据中--------------------");
                Resttemplate resttemplate = new Resttemplate();
                map = resttemplate.SendMessage(map, ConstantInterface.port + "/HSDC/message/telecomCallRecord");
                logger.warn(phoneNumber + "：---------------------西宁电信获取详单推送数据完成--------------------本次推送返回：" + map);

                String validateResult = "0000";
                String errorCode = "errorCode";
                if (validateResult.equals(map.get(errorCode))) {
                    PushSocket.pushnew(map, uuid, "8000", "认证成功");
                    PushState.state(phoneNumber, "callLog", 300);
                } else {
                    PushSocket.pushnew(map, uuid, "9000", map.get("errorInfo").toString());
                    PushState.state(phoneNumber, "callLog", 200, map.get("errorInfo").toString());
                }
                logger.warn(phoneNumber + "：---------------------西宁电信获取详单完毕--------------------");
            } catch (Exception e) {
                logger.error(phoneNumber + "：---------------------西宁电信获取详单异常--------------------", e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
//                PushSocket.pushnew(map, uuid, "9000","网络连接异常!");
                PushState.state(phoneNumber, "callLog", 200, "网络连接异常!");
                DealExceptionSocketStatus.pushExceptionSocket(signle, map, uuid);
            } finally {
                if (webClient != null) {
                    webClient.close();
                }
            }
        }
        return map;
    }

    /**
     * 获取验证码，并识别
     *
     * @param webClient
     * @return
     * @throws Exception
     */
    public String getCode(WebClient webClient, HttpServletRequest req) throws Exception {
        WebRequest request = new WebRequest(new URL("http://login.189.cn/web/captcha?undefined&source=login&width=100&height=37&0.1488377725428811"));
        request.setHttpMethod(HttpMethod.GET);
        UnexpectedPage imagePage = webClient.getPage(request);

        BufferedImage bufferedImage = ImageIO.read(imagePage.getInputStream());
        String findImage = "xn" + System.currentTimeMillis() + ".png";
        String path = req.getServletContext().getRealPath("/unicomImage");
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        ImageIO.write(bufferedImage, "png", new File(file, findImage));
        // 2.转码
        Map<String, Object> imagev = MyCYDMDemo.Imagev(path + File.separator
                + findImage);
        // 转码后的动态码
        String catpy = (String) imagev.get("strResult");
        return catpy;
    }

    /**
     * 登录
     *
     * @param webClient
     * @param req
     * @param phone
     * @param password
     * @param catpy
     * @return
     * @throws MalformedURLException
     */
    public HtmlPage getLogin(WebClient webClient, HttpServletRequest req, String phone, String password, String catpy) throws Exception {
        WebRequest request = new WebRequest(new URL("http://login.189.cn/web/login"));

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new NameValuePair("Account", phone));
        list.add(new NameValuePair("UType", "201"));
        list.add(new NameValuePair("ProvinceID", "29"));
        list.add(new NameValuePair("AreaCode", ""));
        list.add(new NameValuePair("CityNo", ""));
        list.add(new NameValuePair("RandomFlag", "0"));
        list.add(new NameValuePair("Password", password));
        list.add(new NameValuePair("Captcha", catpy));
        request.setRequestParameters(list);

        request.setHttpMethod(HttpMethod.POST);

        HtmlPage loginbtn = webClient.getPage(request);
        return loginbtn;
    }
}
