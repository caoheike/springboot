package com.reptile.service.chinatelecom;

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

/**
 * 西宁电信
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Service
public class XiNingTelecomService {
    private Logger logger= LoggerFactory.getLogger(XiNingTelecomService.class);

    public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String serverPwd,String longitude,String latitude,String uuid){
        Map<String, Object> map = new HashMap<String, Object>(16);
        PushSocket.pushnew(map, uuid, "1000","登录中");
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
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            PushState.state(phoneNumber, "callLog",200,"登录失败,操作异常!");
            PushSocket.pushnew(map, uuid, "3000","登录失败,操作异常!");
            return map;
        } else {
        	PushSocket.pushnew(map, uuid, "2000","登录成功");
            WebClient webClient = (WebClient) attribute;
            try {
                WebRequest requests = new WebRequest(new URL("http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10029&toStUrl=http://qh.189.cn/service/bill/fee.action?type=ticket&fastcode=00920926&cityCode=qh"));
                requests.setHttpMethod(HttpMethod.GET);
                HtmlPage page1 = webClient.getPage(requests);
                PushSocket.pushnew(map, uuid, "5000","数据 获取中");

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
                //当前时间
                String beginTime = simple.format(time);

                calendar.set(Calendar.DAY_OF_MONTH, 1);
                Date endTimes = calendar.getTime();
                //月初时间
                String endTime = simple.format(endTimes);
                int  bountCount=3;
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
                            if (result.contains("无话单记录！")) {
                                break;
                            }
                            if (i > 15) {
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
                    logger.warn(e.getMessage()+"  青海获取过程中ip被封  mrlu",e);
                    Scheduler.sendGet(Scheduler.getIp);
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "数据获取失败，网络异常");
                    PushState.state(phoneNumber, "callLog",200,"数据获取失败，网络异常");
                    PushSocket.pushnew(map, uuid, "7000","数据获取失败，网络异常");
                    return map;
                }
                
                PushSocket.pushnew(map, uuid, "6000","获取数据成功");
                map.put("data", dataList);
                map.put("flag", "2");
                map.put("UserPassword", serverPwd);
                map.put("UserIphone", phoneNumber);
                //经度
                map.put("longitude", longitude);
                //纬度
                map.put("latitude", latitude);
                Resttemplate resttemplate = new Resttemplate();
                map = resttemplate.SendMessage(map, ConstantInterface.port + "/HSDC/message/telecomCallRecord");
                String validateResult="0000";
                String errorCode="errorCode";
                if(validateResult.equals(map.get(errorCode))) {
					PushSocket.pushnew(map, uuid, "8000","认证成功");
					 PushState.state(phoneNumber, "callLog",300);
				}else {
					PushSocket.pushnew(map, uuid, "9000",map.get("errorinfo").toString());
					PushState.state(phoneNumber, "callLog",200,map.get("errorinfo").toString());
				}
            } catch (Exception e) {
                logger.warn(e.getMessage()+"  青海获取详单  mrlu",e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
                PushSocket.pushnew(map, uuid, "9000","网络连接异常!");
                PushState.state(phoneNumber, "callLog",200,"网络连接异常!");
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
