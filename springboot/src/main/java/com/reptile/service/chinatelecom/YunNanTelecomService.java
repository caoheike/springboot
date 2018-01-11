package com.reptile.service.chinatelecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.ConstantInterface;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 云南电信
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Service
public class YunNanTelecomService {
    private Logger logger = LoggerFactory.getLogger(YunNanTelecomService.class);

    public Map<String, Object> sendPhoneCode(HttpServletRequest request, String phoneNumber) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        List<String> dataList = new ArrayList<String>();
        logger.warn(phoneNumber + "：---------------------云南电信发送短信验证码---------------------");
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("GBmobile-webclient");
        if (attribute == null) {
            logger.warn(phoneNumber + "：---------------------云南电信获取账单...未访问公共登录接口---------------------");
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常");
        } else {
            WebClient webClient = (WebClient) attribute;
            try {
                webClient.getPage("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=01941229");
                Thread.sleep(1000);
                HtmlPage page1 = webClient.getPage("http://yn.189.cn/service/jt/bill/qry_mainjt.jsp?SERV_NO=SHQD1&fastcode=01941229&cityCode=yn");
                Thread.sleep(2000);

                HtmlDivision contentInfo = (HtmlDivision) page1.getElementById("contentInfo");
                HtmlPage motoText = null;
                if (contentInfo != null) {
                    DomNodeList<HtmlElement> input = contentInfo.getElementsByTagName("input");
                    for (int i = 0; i < input.size(); i++) {
                        String onclick = input.get(i).getAttribute("onclick");
                        if (onclick.contains(phoneNumber)) {
                            HtmlPage page = input.get(i).click();
                            Thread.sleep(2000);
                            motoText = (HtmlPage) page.executeJavaScript("postValidCode()").getNewPage();
                            Thread.sleep(2000);
                        }
                    }
                } else {
                    motoText = (HtmlPage) page1.executeJavaScript("postValidCode()").getNewPage();
                    Thread.sleep(2000);
                }

                String popup = "";
                String popupSignle = "popup";
                int validateInt = 3;
                if (motoText.getElementById(popupSignle).getFirstChild().getFirstChild().getChildNodes().size() == validateInt) {
                    popup = motoText.getElementById(popupSignle).getFirstChild().getFirstChild().getChildNodes().get(1).getTextContent();
                } else {
                    popup = motoText.getElementById(popupSignle).getFirstChild().getChildNodes().get(1).getTextContent();
                }

                String validateResult = "短信验证码发送成功";
                if (!popup.contains(validateResult)) {
                    logger.warn(phoneNumber + "：---------------------云南电信短信验证码发送失败---------------------  失败原因：" + popup);
                    map.put("errorCode", "0002");
                    map.put("errorInfo", popup);
                } else {
                    logger.warn(phoneNumber + "：---------------------云南电信短信验证码发送成功---------------------");
                    map.put("errorCode", "0000");
                    map.put("errorInfo", "短信已成功发送至您的手机，请注意查收！");
                }

                HtmlAnchor popup1 = (HtmlAnchor) motoText.getElementById("popup").getFirstChild().getFirstChild().getChildNodes().get(2).getChildNodes().get(0);
                HtmlPage click1 = popup1.click();
                Thread.sleep(1000);

                session.setAttribute("yunNanWebClient", webClient);
                session.setAttribute("yunNanHtmlPage", click1);
            } catch (Exception e) {
                logger.warn(phoneNumber + "-------------------云南电信发送手机验证码异常---------------", e);
                map.put("errorCode", "0002");
                map.put("errorInfo", "网络连接异常");
            }
        }
        return map;
    }

    public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String serverPwd, String phoneCode,
                                            String userName, String userCard, String longitude, String latitude, String uuid) {
        logger.warn(phoneNumber + "：---------------------云南电信获取详单...---------------------");
        Map<String, Object> map = new HashMap<String, Object>(16);
        PushSocket.pushnew(map, uuid, "1000", "登录中");
        PushState.state(phoneNumber, "callLog", 100);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        List<String> dataList = new ArrayList<String>();
        HttpSession session = request.getSession();
        Object yunNanWebClient = session.getAttribute("yunNanWebClient");
        Object yunNanHtmlPage = session.getAttribute("yunNanHtmlPage");
        if (yunNanWebClient == null) {
            logger.warn(phoneNumber + "：---------------------云南电信未获取手机验证码.---------------------");
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常");
            PushState.state(phoneNumber, "callLog", 200, "登录失败,操作异常");
            PushSocket.pushnew(map, uuid, "3000", "登录失败,操作异常");
        } else {
            WebClient webClient = (WebClient) yunNanWebClient;
            HtmlPage htmlPage = (HtmlPage) yunNanHtmlPage;
            try {
                logger.warn(phoneNumber + "：---------------------云南电信二次身份认证中.---------------------");
                htmlPage.getElementById("NAME").setAttribute("value", userName);
                htmlPage.getElementById("CUSTCARDNO").setAttribute("value", userCard);
                htmlPage.getElementById("PROD_PASS").setAttribute("value", serverPwd);
                htmlPage.getElementById("MOBILE_CODE").setAttribute("value", phoneCode);

                HtmlPage resultPage = (HtmlPage) htmlPage.executeJavaScript("doPwValid()").getNewPage();
                Thread.sleep(4000);

                String areaCode = resultPage.getElementByName("AREA_CODE").getAttribute("value");
                String popupSignle = "popup";
                if (resultPage.getElementById(popupSignle) != null) {
                    String popup = "";
                    int count = 3;
                    if (resultPage.getElementById(popupSignle).getFirstChild().getFirstChild().getChildNodes().size() == count) {
                        popup = resultPage.getElementById(popupSignle).getFirstChild().getFirstChild().getChildNodes().get(1).getTextContent();
                    } else {
                        popup = resultPage.getElementById(popupSignle).getFirstChild().getChildNodes().get(1).getTextContent();
                    }
                    String validateYan = "验证成功";
                    if (!popup.contains(validateYan)) {
                        logger.warn(phoneNumber + "：---------------------云南电信二次身份认证失败---------------------  失败原因:"+popup);
                        map.put("errorCode", "0003");
                        map.put("errorInfo", popup);
                        PushState.state(phoneNumber, "callLog", 200, popup);
                        PushSocket.pushnew(map, uuid, "3000", popup);
                        return map;
                    }
                }

                PushSocket.pushnew(map, uuid, "2000", "登录成功");
                Thread.sleep(2000);
                PushSocket.pushnew(map, uuid, "5000", "获取数据中");
                SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
                Calendar calendar = Calendar.getInstance();
                logger.warn(phoneNumber + "：---------------------云南电信获取详单开始---------------------");
                int boundCount = 3;
                for (int i = 0; i < boundCount; i++) {
                    String monthDate = sim.format(calendar.getTime());
                    WebRequest webRequest = new WebRequest(new URL("http://yn.189.cn/service/jt/bill/actionjt/ifr_bill_detailslist_em_new.jsp"));
                    webRequest.setHttpMethod(HttpMethod.POST);
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new NameValuePair("pageno", "1"));
                    list.add(new NameValuePair("CYCLE_END_DATE", ""));
                    list.add(new NameValuePair("NUM", phoneNumber));
                    list.add(new NameValuePair("QUERY_TYPE", "10"));
                    list.add(new NameValuePair("BILLING_CYCLE", monthDate));
                    list.add(new NameValuePair("CYCLE_BEGIN_DATE", ""));
                    list.add(new NameValuePair("AREA_CODE", areaCode));
                    webRequest.setRequestParameters(list);
                    webRequest.setAdditionalHeader("Referer", "http://yn.189.cn/service/jt/bill/actionjt/ifr_bill_detailslist_em_new.jsp");
                    HtmlPage page2 = webClient.getPage(webRequest);
                    Thread.sleep(2000);

                    dataList.add(page2.asXml());
                    try {
                        String pageContent = page2.getElementById("page_bar").getTextContent();
                        String[] split = pageContent.split("1/");
                        String[] split1 = split[1].split("\\(每页");
                        int pageCount = Integer.parseInt(split1[0]);
                        int countSignle = 2;
                        for (int j = countSignle; j < pageCount + 1; j++) {
                            getDetailList(phoneNumber, monthDate, areaCode, webClient, j, dataList);
                        }
                    } catch (Exception e) {
                        logger.warn(phoneNumber +"：---------------------云南循环获取详单出错---------------------", e);
                    }
                    calendar.add(Calendar.MONTH, -1);
                }
                logger.warn(phoneNumber + "：---------------------云南电信获取详单完成--------------------本次获取账单数目：" + dataList.size());
                PushSocket.pushnew(map, uuid, "6000", "获取数据成功");
                map.put("data", dataList);
                map.put("flag", "8");
                map.put("UserPassword", serverPwd);
                map.put("UserIphone", phoneNumber);
                //经度
                map.put("longitude", longitude);
                //纬度
                map.put("latitude", latitude);
                webClient.close();
                Resttemplate resttemplate = new Resttemplate();

                logger.warn(phoneNumber + "：---------------------云南电信获取详单推送数据中--------------------");
                map = resttemplate.SendMessage(map, ConstantInterface.port + "/HSDC/message/telecomCallRecord");
                logger.warn(phoneNumber + "：---------------------云南电信获取详单推送数据完成--------------------本次推送返回：" + map);

                String errorCode = "errorCode";
                String numberResult = "0000";
                if (map.get(errorCode).equals(numberResult)) {
                    PushSocket.pushnew(map, uuid, "8000", "认证成功");
                    PushState.state(phoneNumber, "callLog", 300);
                } else {
                    PushSocket.pushnew(map, uuid, "9000", map.get("errorInfo").toString());
                    PushState.state(phoneNumber, "callLog", 200, map.get("errorInfo").toString());
                }
                logger.warn(phoneNumber + "：---------------------云南电信获取详单完毕--------------------");
            } catch (Exception e) {
                logger.warn(phoneNumber +":---------------------云南详单获取异常---------------------", e);
                map.put("errorCode", "0002");
                map.put("errorInfo", "网络连接异常");
                PushSocket.pushnew(map, uuid, "9000", "网络连接异常");
                PushState.state(phoneNumber, "callLog", 200, "网络连接异常");
            }
        }

        return map;
    }


    private void getDetailList(String phoneNumber, String monthDate, String areaCode, WebClient webClient, int j, List<String> dataList) throws Exception {
        WebRequest webRequest = new WebRequest(new URL("http://yn.189.cn/service/jt/bill/actionjt/ifr_bill_detailslist_em_new.jsp"));
        webRequest.setHttpMethod(HttpMethod.POST);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new NameValuePair("pageno", "" + j));
        list.add(new NameValuePair("CYCLE_END_DATE", ""));
        list.add(new NameValuePair("NUM", phoneNumber));
        list.add(new NameValuePair("QUERY_TYPE", "10"));
        list.add(new NameValuePair("BILLING_CYCLE", monthDate));
        list.add(new NameValuePair("CYCLE_BEGIN_DATE", ""));
        list.add(new NameValuePair("AREA_CODE", areaCode));
        webRequest.setRequestParameters(list);
        webRequest.setAdditionalHeader("Referer", "http://yn.189.cn/service/jt/bill/actionjt/ifr_bill_detailslist_em_new.jsp");
        HtmlPage page2 = webClient.getPage(webRequest);
        Thread.sleep(Math.round(3000));

        dataList.add(page2.asXml());
    }

}
