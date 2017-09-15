package com.reptile.service;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.reptile.util.ConstantInterface;
import com.reptile.util.Resttemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HeNanTelecomService {


    public Map<String, Object> sendPhoneCode(HttpServletRequest request, String phoneNumber) {

        Map<String, Object> map = new HashMap<String, Object>();
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("GBmobile-webclient");

        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            return map;
        } else {
            try {
                WebClient webClient = (WebClient) attribute;
                WebRequest requests = new WebRequest(new URL("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=20000356"));
                requests.setHttpMethod(HttpMethod.GET);
                HtmlPage page1 = webClient.getPage(requests);
                System.out.println(page1.asXml());

                SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
                Calendar calendar = Calendar.getInstance();
                Date time = calendar.getTime();
                String date = sim.format(time);

                WebRequest req = new WebRequest(new URL("http://ha.189.cn/service/bill/getRand.jsp"));
                req.setHttpMethod(HttpMethod.POST);
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new NameValuePair("PRODTYPE", "713070904705"));
                list.add(new NameValuePair("RAND_TYPE", "002"));
                list.add(new NameValuePair("BureauCode", "0371"));
                list.add(new NameValuePair("ACC_NBR", phoneNumber));
                list.add(new NameValuePair("PROD_TYPE", "713070904705"));
                list.add(new NameValuePair("PROD_PWD", ""));
                list.add(new NameValuePair("REFRESH_FLAG", "1"));
                list.add(new NameValuePair("BEGIN_DATE", ""));
                list.add(new NameValuePair("END_DATE", ""));
                list.add(new NameValuePair("ACCT_DATE", date));
                list.add(new NameValuePair("FIND_TYPE", "2"));
                list.add(new NameValuePair("SERV_NO", ""));
                list.add(new NameValuePair("QRY_FLAG", "1"));
                list.add(new NameValuePair("ValueType", "4"));
                list.add(new NameValuePair("MOBILE_NAME", phoneNumber));
                list.add(new NameValuePair("OPER_TYPE", "CR1"));
                list.add(new NameValuePair("PASSWORD", ""));
                req.setRequestParameters(list);

                XmlPage page = webClient.getPage(req);
                Thread.sleep(1000);
                System.out.println(page.asText());
                String result = page.asText();
                if (result.trim().contains("请等待30分钟后在发送")) {
                    String[] arr = result.trim().split("送");
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "请等待" + arr[1].trim() + "分钟后在发送!");
                } else {
                    map.put("errorCode", "0000");
                    map.put("errorInfo", "短信已成功发送，请注意查收！");
                    session.setAttribute("HNwebClient", webClient);
                }
            } catch (Exception e) {
                e.printStackTrace();
                map.put("errorCode", "0002");
                map.put("errorInfo", "网络异常！");
            }
        }
        return map;
    }

    public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String serverPwd, String phoneCode) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> dataList = new ArrayList<String>();
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("HNwebClient");
        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            return map;
        } else {
            try {
                WebClient webClient = (WebClient) attribute;
                WebRequest req = new WebRequest(new URL("http://ha.189.cn/service/iframe/bill/iframe_inxxall.jsp"));
                req.setHttpMethod(HttpMethod.POST);
                List<NameValuePair> list = new ArrayList<NameValuePair>();

                SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
                Calendar calendar = Calendar.getInstance();
                Date time = calendar.getTime();
                String beforeDate = sim.format(time);

                list.add(new NameValuePair("PRODTYPE", "713070904705"));
                list.add(new NameValuePair("RAND_TYPE", "002"));
                list.add(new NameValuePair("BureauCode", "0371"));
                list.add(new NameValuePair("ACC_NBR", phoneNumber));
                list.add(new NameValuePair("PROD_TYPE", "713070904705"));
                list.add(new NameValuePair("PROD_PWD", ""));
                list.add(new NameValuePair("REFRESH_FLAG", "1"));
                list.add(new NameValuePair("BEGIN_DATE", ""));
                list.add(new NameValuePair("END_DATE", ""));
                list.add(new NameValuePair("ACCT_DATE", beforeDate));
                list.add(new NameValuePair("FIND_TYPE", "2"));
                list.add(new NameValuePair("SERV_NO", ""));
                list.add(new NameValuePair("QRY_FLAG", "1"));
                list.add(new NameValuePair("ValueType", "4"));
                list.add(new NameValuePair("MOBILE_NAME", phoneNumber));
                list.add(new NameValuePair("OPER_TYPE", "CR1"));
                list.add(new NameValuePair("PASSWORD", phoneCode));
                req.setRequestParameters(list);
                HtmlPage page2 = webClient.getPage(req);
                String result = page2.asText();

                if (result.contains("您输入的查询验证码错误或过期")) {
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "您输入的查询验证码错误或过期，请重新核对或再次获取！");
                    return map;
                }
                dataList.add(page2.asXml());

                for (int i = 0; i < 5; i++) {
                    req = new WebRequest(new URL("http://ha.189.cn/service/iframe/bill/iframe_inxxall.jsp"));
                    req.setHttpMethod(HttpMethod.POST);
                    list = new ArrayList<NameValuePair>();

                    calendar.add(Calendar.MONTH, -1);
                    Date date = calendar.getTime();
                    String currentDate = sim.format(date);
                    list.add(new NameValuePair("ACC_NBR", phoneNumber));
                    list.add(new NameValuePair("PROD_TYPE", "713070904705"));
                    list.add(new NameValuePair("BEGIN_DATE", ""));
                    list.add(new NameValuePair("END_DATE", ""));
                    list.add(new NameValuePair("ValueType", "4"));
                    list.add(new NameValuePair("REFRESH_FLAG", "1"));
                    list.add(new NameValuePair("FIND_TYPE", "1"));
                    list.add(new NameValuePair("radioQryType", "on"));
                    list.add(new NameValuePair("QRY_FLAG", "1"));
                    list.add(new NameValuePair("ACCT_DATE", currentDate));
                    list.add(new NameValuePair("ACCT_DATE_1", beforeDate));
                    req.setRequestParameters(list);
                    page2 = webClient.getPage(req);
                    Thread.sleep(1000);
                    dataList.add(page2.asXml());
                    beforeDate = currentDate;
                }
                map.put("UserIphone", phoneNumber);
                map.put("UserPassword", serverPwd);
                map.put("flag", "5");
                map.put("data", dataList);
                Resttemplate resttemplate = new Resttemplate();
                map = resttemplate.SendMessage(map, ConstantInterface.port + "/HSDC/message/telecomCallRecord");

                System.out.println("河南电信拿到的数据条数：" + dataList.size());
            } catch (Exception e) {
                e.printStackTrace();
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
            }
        }
        return map;
    }
}

