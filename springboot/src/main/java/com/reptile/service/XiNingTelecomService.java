package com.reptile.service;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.springboot.Scheduler;
import com.reptile.util.ConstantInterface;
import com.reptile.util.Resttemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class XiNingTelecomService {
    //青海省
    public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String serverPwd) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> dataList = new ArrayList<String>();
        HttpSession session = request.getSession();

        Object attribute = session.getAttribute("GBmobile-webclient");

        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            return map;
        } else {
            try {
                WebClient webClient = (WebClient) attribute;
                WebRequest requests = new WebRequest(new URL("http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10029&toStUrl=http://qh.189.cn/service/bill/fee.action?type=ticket&fastcode=00920926&cityCode=qh"));
                requests.setHttpMethod(HttpMethod.GET);
                HtmlPage page1 = webClient.getPage(requests);
                System.out.println(page1.asXml());


//                WebRequest webRequest = new WebRequest(new URL("http://qh.189.cn/service/bill/feeDetailrecordList.action"));
//                        webRequest.setHttpMethod(HttpMethod.POST);
//                        List<NameValuePair> list = new ArrayList<NameValuePair>();
//                        list.add(new NameValuePair("currentPage", "1"));
//                        list.add(new NameValuePair("pageSize", "5"));
//                        list.add(new NameValuePair("effDate", "2017-09-01"));
//                        list.add(new NameValuePair("expDate", "2017-09-06"));
//                        list.add(new NameValuePair("serviceNbr", "18194551655"));
//                        list.add(new NameValuePair("operListID", "12"));
//                        list.add(new NameValuePair("pOffrType", "4"));
//                        list.add(new NameValuePair("sendsmsflag", "true"));
//                        list.add(new NameValuePair("num", "1"));
//                        list.add(new NameValuePair("callTypeVal", "0"));
//                        webRequest.setRequestParameters(list);
//                        HtmlPage page = webClient.getPage(webRequest);
//                        String result = page.asXml();
//                System.out.println(result);


                SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                Date time = calendar.getTime();
                String beginTime = simple.format(time); //当前时间
                System.out.println(beginTime);

                calendar.set(Calendar.DAY_OF_MONTH, 1);
                Date endTimes = calendar.getTime();
                String endTime = simple.format(endTimes); //月初时间
                System.out.println(endTime);

                try {
                    for (int j = 0; j < 3; j++) {
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
                            if (result.contains("无话单记录！")) {
                                break;
                            }
                            if (i > 15) {
                                break;
                            }
                            Thread.sleep(1000);
                            dataList.add(result);
                            i++;
//                        System.out.println(page.asXml());
                        }
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        beginTime = simple.format(calendar.getTime());
                        System.out.println(simple.format(calendar.getTime()));

                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        endTime = simple.format(calendar.getTime());
                        System.out.println(simple.format(calendar.getTime()));
                    }
                } catch (Exception e) {
                    Scheduler.sendGet(Scheduler.getIp);
                    e.printStackTrace();
                }
                map.put("data", dataList);
                map.put("flag", "2");
                map.put("UserPassword", serverPwd);
                map.put("UserIphone", phoneNumber);
                Resttemplate resttemplate = new Resttemplate();
                map = resttemplate.SendMessage(map, ConstantInterface.port + "/HSDC/message/telecomCallRecord");
            } catch (Exception e) {
                e.printStackTrace();
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
            }
        }
        return map;
    }

    public static void main(String[] args) {
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();
        String beginTime = simple.format(time);
        System.out.println(beginTime);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date endTimes = calendar.getTime();
        String endTime = simple.format(endTimes);
        System.out.println(endTime);

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        System.out.println(simple.format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        System.out.println(simple.format(calendar.getTime()));
    }
}
