package com.reptile.service.ChinaTelecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
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

@Service
public class HeNanTelecomService {
    private Logger logger= LoggerFactory.getLogger(HeNanTelecomService.class);

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

                SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
                Calendar calendar = Calendar.getInstance();
                Date time = calendar.getTime();
                String date = sim.format(time);

                WebRequest req = new WebRequest(new URL("http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10017&toStUrl=http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-2&fastcode=20000356&cityCode=ha"));
                req.setHttpMethod(HttpMethod.GET);
                HtmlPage pages=webClient.getPage(req);
                Thread.sleep(1000);
                req=new WebRequest(new URL("http://ha.189.cn/service/iframe/bill/iframe_inxxall.jsp"));
                req.setHttpMethod(HttpMethod.POST);
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new NameValuePair("ACC_NBR",phoneNumber));
                list.add(new NameValuePair("PROD_TYPE",pages.getElementById("PROD_TYPE").getAttribute("value")));
                list.add(new NameValuePair("BEGIN_DATE",""));
                list.add(new NameValuePair("END_DATE",""));
                list.add(new NameValuePair("SERV_NO",""));
                list.add(new NameValuePair("ValueType","1"));
                list.add(new NameValuePair("REFRESH_FLAG",pages.getElementById("REFRESH_FLAG").getAttribute("value")));
                list.add(new NameValuePair("FIND_TYPE",pages.getElementById("FIND_TYPE").getAttribute("value")));
                list.add(new NameValuePair("radioQryType","on"));
                list.add(new NameValuePair("QRY_FLAG",pages.getElementById("QRY_FLAG").getAttribute("value")));
                list.add(new NameValuePair("ACCT_DATE",date));
                list.add(new NameValuePair("ACCT_DATE_1",date));
                req.setRequestParameters(list);
                HtmlPage pagess= webClient.getPage(req);
                Thread.sleep(1000);

                req = new WebRequest(new URL("http://ha.189.cn/service/bill/getRand.jsp"));
                req.setHttpMethod(HttpMethod.POST);
                list = new ArrayList<NameValuePair>();
                list.add(new NameValuePair("PRODTYPE", pagess.getElementById("PRODTYPE").getAttribute("value")));
                list.add(new NameValuePair("RAND_TYPE",  pagess.getElementById("RAND_TYPE").getAttribute("value")));//
                list.add(new NameValuePair("BureauCode",  pagess.getElementById("BureauCode").getAttribute("value")));//
                list.add(new NameValuePair("ACC_NBR",  phoneNumber));
                list.add(new NameValuePair("PROD_TYPE",  pagess.getElementById("PROD_TYPE").getAttribute("value")));
                list.add(new NameValuePair("PROD_PWD",  ""));
                list.add(new NameValuePair("REFRESH_FLAG",  pagess.getElementById("REFRESH_FLAG").getAttribute("value")));
                list.add(new NameValuePair("BEGIN_DATE",  ""));
                list.add(new NameValuePair("END_DATE",  ""));
                list.add(new NameValuePair("ACCT_DATE",  pagess.getElementById("ACCT_DATE").getAttribute("value")));
                list.add(new NameValuePair("FIND_TYPE",  pagess.getElementById("FIND_TYPE").getAttribute("value")));
                list.add(new NameValuePair("SERV_NO",  pagess.getElementById("SERV_NO").getAttribute("value")));
                list.add(new NameValuePair("QRY_FLAG",  pagess.getElementById("QRY_FLAG").getAttribute("value")));
                list.add(new NameValuePair("ValueType",  "4"));
                list.add(new NameValuePair("MOBILE_NAME",  phoneNumber));
                list.add(new NameValuePair("OPER_TYPE",  "CR1"));
                list.add(new NameValuePair("PASSWORD",  ""));
                req.setRequestParameters(list);

                XmlPage page = webClient.getPage(req);
                Thread.sleep(1000);
                String result = page.asText();
                if (result.trim().contains("请等待30分钟后在发送")) {
                    String[] arr = result.trim().split("送");
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "请等待" + arr[1].trim() + "分钟后在发送!");
                } else {
                    map.put("errorCode", "0000");
                    map.put("errorInfo", "短信已成功发送，请注意查收！");
                    session.setAttribute("HNwebClient", webClient);
                    session.setAttribute("HeNanHtmlPage", pagess);
                }
            } catch (Exception e) {
                logger.warn(e.getMessage()+"  广西发送手机验证码   mrlu",e);
                map.put("errorCode", "0002");
                map.put("errorInfo", "网络异常！");
            }
        }
        return map;
    }

    public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String serverPwd, String phoneCode,String longitude,String latitude,String UUID) {
        Map<String, Object> map = new HashMap<String, Object>();
        PushState.state(phoneNumber, "callLog",100);
        PushSocket.pushnew(map, UUID, "1000","登录中");
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        List<String> dataList = new ArrayList<String>();
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("HNwebClient");
        Object pag = session.getAttribute("HeNanHtmlPage");
        if (attribute == null) {
        	//PushSocket.push(map, UUID, "0001");
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            PushSocket.pushnew(map, UUID, "3000","登录失败,操作异常!");
            return map;
        } else {
            try {
                WebClient webClient = (WebClient) attribute;
                HtmlPage pagess = (HtmlPage) pag;
                WebRequest req = new WebRequest(new URL("http://ha.189.cn/service/iframe/bill/iframe_inxxall.jsp"));
                req.setHttpMethod(HttpMethod.POST);
                List<NameValuePair> list = new ArrayList<NameValuePair>();

                SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
                Calendar calendar = Calendar.getInstance();
                Date time = calendar.getTime();
                String beforeDate = sim.format(time);

                list.add(new NameValuePair("PRODTYPE", pagess.getElementById("PRODTYPE").getAttribute("value")));
                list.add(new NameValuePair("RAND_TYPE",  pagess.getElementById("RAND_TYPE").getAttribute("value")));//
                list.add(new NameValuePair("BureauCode",  pagess.getElementById("BureauCode").getAttribute("value")));//
                list.add(new NameValuePair("ACC_NBR",  phoneNumber));
                list.add(new NameValuePair("PROD_TYPE",  pagess.getElementById("PROD_TYPE").getAttribute("value")));
                list.add(new NameValuePair("PROD_PWD",  pagess.getElementById("PROD_PWD").getAttribute("value")));
                list.add(new NameValuePair("REFRESH_FLAG",  pagess.getElementById("REFRESH_FLAG").getAttribute("value")));
                list.add(new NameValuePair("BEGIN_DATE",  ""));
                list.add(new NameValuePair("END_DATE",  ""));
                list.add(new NameValuePair("ACCT_DATE",  beforeDate));
                list.add(new NameValuePair("FIND_TYPE",  pagess.getElementById("FIND_TYPE").getAttribute("value")));
                list.add(new NameValuePair("SERV_NO",  pagess.getElementById("SERV_NO").getAttribute("value")));
                list.add(new NameValuePair("QRY_FLAG",  pagess.getElementById("QRY_FLAG").getAttribute("value")));
                list.add(new NameValuePair("ValueType",  "4"));
                list.add(new NameValuePair("MOBILE_NAME", phoneNumber ));
                list.add(new NameValuePair("OPER_TYPE",  "CR1"));
                list.add(new NameValuePair("PASSWORD",  phoneCode));
                req.setRequestParameters(list);
                HtmlPage page2 = webClient.getPage(req);
                Thread.sleep(1000);
                String result = page2.asText();

                if (result.contains("您输入的查询验证码错误或过期")) {
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "您输入的查询验证码错误或过期，请重新核对或再次获取！");
                    PushSocket.pushnew(map, UUID, "3000","您输入的查询验证码错误或过期，请重新核对或再次获取！");
                    return map;
                }
                PushSocket.pushnew(map, UUID, "2000","登录成功");
                Thread.sleep(2000);
                PushSocket.pushnew(map, UUID, "5000","数据获取中");
                //PushSocket.push(map, UUID, "0000");
                for (int i = 0; i < 6; i++) {
                    req = new WebRequest(new URL("http://ha.189.cn/service/iframe/bill/iframe_inxxall.jsp"));
                    req.setHttpMethod(HttpMethod.POST);
                    list = new ArrayList<NameValuePair>();

                    Date date = calendar.getTime();
                    String currentDate = sim.format(date);
                    list.add(new NameValuePair("ACC_NBR", phoneNumber));
                    list.add(new NameValuePair("PROD_TYPE", page2.getElementById("PROD_TYPE").getAttribute("value")));
                    list.add(new NameValuePair("BEGIN_DATE", ""));
                    list.add(new NameValuePair("END_DATE", ""));
                    list.add(new NameValuePair("ValueType", "4"));
                    list.add(new NameValuePair("REFRESH_FLAG", page2.getElementById("REFRESH_FLAG").getAttribute("value")));
                    list.add(new NameValuePair("FIND_TYPE", "1"));
                    list.add(new NameValuePair("radioQryType", "on"));
                    list.add(new NameValuePair("QRY_FLAG", page2.getElementById("QRY_FLAG").getAttribute("value")));
                    list.add(new NameValuePair("ACCT_DATE", currentDate));
                    list.add(new NameValuePair("ACCT_DATE_1", beforeDate));
                    req.setRequestParameters(list);
                    page2 = webClient.getPage(req);
                    Thread.sleep(1000);
                    dataList.add(page2.asXml());
                    calendar.add(Calendar.MONTH, -1);
                    beforeDate = currentDate;
                }
                if(dataList.size()>0) {
                	PushSocket.pushnew(map, UUID, "6000","数据获取成功");
                }else {
                	PushSocket.pushnew(map, UUID, "7000","数据获取失败");
                }
                map.put("UserIphone", phoneNumber);
                map.put("UserPassword", serverPwd);
                map.put("longitude", longitude);//经度
                map.put("latitude", latitude);//纬度
                map.put("flag", "5");
                map.put("data", dataList);
                webClient.close();
                Resttemplate resttemplate = new Resttemplate();
                map = resttemplate.SendMessage(map, ConstantInterface.port + "/HSDC/message/telecomCallRecord");
				if(map.get("errorCode").equals("0000")) {
					PushSocket.pushnew(map, UUID, "8000","认证成功");
					PushState.state(phoneNumber, "callLog",300);
				}else {
					PushSocket.pushnew(map, UUID, "9000",map.get("errorInfo").toString());
					PushState.state(phoneNumber, "callLog",200);
				}
				
                System.out.println("河南电信拿到的数据条数：" + dataList.size());
            } catch (Exception e) {
                logger.warn(e.getMessage()+"  广西获取详单   mrlu",e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
                PushSocket.pushnew(map, UUID, "9000","网络连接异常!");
            }
        }
        return map;
    }
}

