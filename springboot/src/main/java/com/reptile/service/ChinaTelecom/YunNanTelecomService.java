package com.reptile.service.ChinaTelecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.ConstantInterface;
import com.reptile.util.PushSocket;
import com.reptile.util.Resttemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class YunNanTelecomService {
    private Logger logger= LoggerFactory.getLogger(YunNanTelecomService.class);
    public Map<String, Object> sendPhoneCode(HttpServletRequest request, String phoneNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> dataList = new ArrayList<String>();

        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("GBmobile-webclient");
        if (attribute == null) {
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
                if (motoText.getElementById("popup").getFirstChild().getFirstChild().getChildNodes().size() == 3) {
                    popup = motoText.getElementById("popup").getFirstChild().getFirstChild().getChildNodes().get(1).getTextContent();
                } else {
                    popup = motoText.getElementById("popup").getFirstChild().getChildNodes().get(1).getTextContent();
                }


                if (!popup.contains("短信验证码发送成功")) {
                    map.put("errorCode", "0002");
                    map.put("errorInfo", popup);
                } else {
                    map.put("errorCode", "0000");
                    map.put("errorInfo", "短信已成功发送至您的手机，请注意查收！");
                }

                HtmlAnchor popup1 = (HtmlAnchor) motoText.getElementById("popup").getFirstChild().getFirstChild().getChildNodes().get(2).getChildNodes().get(0);
                HtmlPage click1 = popup1.click();
                Thread.sleep(1000);

                session.setAttribute("yunNanWebClient", webClient);
                session.setAttribute("yunNanHtmlPage", click1);
            } catch (Exception e) {
                logger.warn(e.getMessage()+"  云南电信发送手机验证码  mrlu",e);
                map.put("errorCode", "0002");
                map.put("errorInfo", "网络连接异常");
            }
        }
        return map;
    }

    public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String serverPwd, String phoneCode,
                                            String userName, String userCard,String longitude,String latitude,String UUID) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> dataList = new ArrayList<String>();
        HttpSession session = request.getSession();
        Object yunNanWebClient = session.getAttribute("yunNanWebClient");
        Object yunNanHtmlPage = session.getAttribute("yunNanHtmlPage");
        if (yunNanWebClient == null) {
        	PushSocket.push(map, UUID, "0001");
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常");
        } else {
            WebClient webClient = (WebClient) yunNanWebClient;
            HtmlPage htmlPage = (HtmlPage) yunNanHtmlPage;
            try {
                htmlPage.getElementById("NAME").setAttribute("value", userName);
                htmlPage.getElementById("CUSTCARDNO").setAttribute("value", userCard);
                htmlPage.getElementById("PROD_PASS").setAttribute("value", serverPwd);
                htmlPage.getElementById("MOBILE_CODE").setAttribute("value", phoneCode);

                HtmlPage resultPage = (HtmlPage) htmlPage.executeJavaScript("doPwValid()").getNewPage();
                Thread.sleep(4000);

                String areaCode = resultPage.getElementByName("AREA_CODE").getAttribute("value");

                if (resultPage.getElementById("popup") != null) {
                    String popup = "";
                    if (resultPage.getElementById("popup").getFirstChild().getFirstChild().getChildNodes().size() == 3) {
                        popup = resultPage.getElementById("popup").getFirstChild().getFirstChild().getChildNodes().get(1).getTextContent();
                    } else {
                        popup = resultPage.getElementById("popup").getFirstChild().getChildNodes().get(1).getTextContent();
                    }
                    if (!popup.contains("验证成功")) {
                    	  PushSocket.push(map, UUID, "0001");
                        map.put("errorCode", "0003");
                        map.put("errorInfo", popup);
                        return map;
                    }
                }
                PushSocket.push(map, UUID, "0000");
                SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
                Calendar calendar = Calendar.getInstance();
                for (int i = 0; i < 3; i++) {
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
                        for (int j = 2; j < pageCount + 1; j++) {
                            getDetailList(phoneNumber, monthDate, areaCode, webClient, j, dataList);
                        }
                    } catch (Exception e) {
                        logger.warn(e.getMessage()+"  云南循环获取详单出错  mrlu",e);
                    }
                    calendar.add(Calendar.MONTH, -1);
                }
                map.put("data", dataList);
                map.put("flag", "8");
                map.put("UserPassword", serverPwd);
                map.put("UserIphone", phoneNumber);
                map.put("longitude", longitude);//经度
                map.put("latitude", latitude);//纬度
                webClient.close();
                Resttemplate resttemplate = new Resttemplate();
                map = resttemplate.SendMessage(map, ConstantInterface.port+"/HSDC/message/telecomCallRecord");
            } catch (Exception e) {
                logger.warn(e.getMessage()+"  云南详单获取  mrlu",e);
                map.put("errorCode", "0002");
                map.put("errorInfo", "网络连接异常");
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
