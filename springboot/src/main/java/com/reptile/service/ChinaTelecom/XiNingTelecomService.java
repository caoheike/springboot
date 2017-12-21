package com.reptile.service.ChinaTelecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.springboot.Scheduler;
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
public class XiNingTelecomService {
    private Logger logger= LoggerFactory.getLogger(XiNingTelecomService.class);
    //青海省
    public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String serverPwd,String longitude,String latitude,String UUID){
        Map<String, Object> map = new HashMap<String, Object>();
        PushSocket.pushnew(map, UUID, "1000","登录中");
        PushState.state(phoneNumber, "callLog",100);
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        List<String> dataList = new ArrayList<String>();
        HttpSession session = request.getSession();

        Object attribute = session.getAttribute("GBmobile-webclient");

        if (attribute == null) {
        	//PushSocket.push(map, UUID, "0001");
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            PushSocket.pushnew(map, UUID, "3000","登录失败,操作异常!");
            return map;
        } else {
        	PushSocket.pushnew(map, UUID, "2000","登录成功");
            WebClient webClient = (WebClient) attribute;
            try {
                WebRequest requests = new WebRequest(new URL("http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10029&toStUrl=http://qh.189.cn/service/bill/fee.action?type=ticket&fastcode=00920926&cityCode=qh"));
                requests.setHttpMethod(HttpMethod.GET);
                HtmlPage page1 = webClient.getPage(requests);
                PushSocket.pushnew(map, UUID, "5000","数据 获取中");

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

                calendar.set(Calendar.DAY_OF_MONTH, 1);
                Date endTimes = calendar.getTime();
                String endTime = simple.format(endTimes); //月初时间

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
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        endTime = simple.format(calendar.getTime());
                    }
                } catch (Exception e) {
                    logger.warn(e.getMessage()+"  青海获取过程中ip被封  mrlu",e);
                    Scheduler.sendGet(Scheduler.getIp);
                }
                
                PushSocket.pushnew(map, UUID, "6000","获取数据成功");
                map.put("data", dataList);
                map.put("flag", "2");
                map.put("UserPassword", serverPwd);
                map.put("UserIphone", phoneNumber);
                map.put("longitude", longitude);//经度
                map.put("latitude", latitude);//纬度
                Resttemplate resttemplate = new Resttemplate();
                map = resttemplate.SendMessage(map, ConstantInterface.port + "/HSDC/message/telecomCallRecord");
                if(map.get("errorCode").equals("0000")) {
					PushSocket.pushnew(map, UUID, "8000","认证成功");
					 PushState.state(phoneNumber, "callLog",300);
				}else {
					PushSocket.pushnew(map, UUID, "9000",map.get("errorinfo").toString());
					 PushState.state(phoneNumber, "callLog",200);
				}
            } catch (Exception e) {
                logger.warn(e.getMessage()+"  青海获取详单  mrlu",e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
                PushSocket.pushnew(map, UUID, "9000","网络连接异常!");
                PushState.state(phoneNumber, "callLog",200);
            }finally {
                if(webClient!=null){
                    webClient.close();
                }
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
