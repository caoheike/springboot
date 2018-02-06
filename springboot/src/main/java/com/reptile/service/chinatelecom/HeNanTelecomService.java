package com.reptile.service.chinatelecom;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.reptile.springboot.Scheduler;
import com.reptile.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 河南电信
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Service
public class HeNanTelecomService {
    private Logger logger= LoggerFactory.getLogger(HeNanTelecomService.class);

    public Map<String, Object> sendPhoneCode(HttpServletRequest request, String phoneNumber) {

        Map<String, Object> map = new HashMap<String, Object>(16);
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("GBmobile-webclient");

        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            logger.warn("------河南电信异常操作：0001---------------"+phoneNumber);
            return map;
        } else {
            try {
                WebClient webClient = (WebClient) attribute;
                webClient.getOptions().setJavaScriptEnabled(false);
                WebRequest requests = new WebRequest(new URL("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=20000356"));
                requests.setHttpMethod(HttpMethod.GET);
                HtmlPage page1 = webClient.getPage(requests);
                logger.warn(phoneNumber+"------河南电信---------------"+page1.asXml());
                Thread.sleep(2000);

                SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
                Calendar calendar = Calendar.getInstance();
                Date time = calendar.getTime();
                String date = sim.format(time);

//                WebRequest req=new WebRequest(new URL("http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10017&toStUrl=http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-2&fastcode=20000356&cityCode=ha"));
//                req.setHttpMethod(HttpMethod.GET);
//                HtmlPage pages = webClient.getPage(req);

                WebRequest req=new WebRequest(new URL("http://ha.189.cn/service/iframe/bill/iframe_inxx.jsp"));
                req.setHttpMethod(HttpMethod.POST);
                List<NameValuePair> dataList=new ArrayList<>();
                dataList.add(new NameValuePair("ACC_NBR",phoneNumber));
//                dataList.add(new NameValuePair("PROD_TYPE","713055046112"));
                dataList.add(new NameValuePair("ACCTNBR97",""));
                req.setRequestParameters(dataList);
                HtmlPage pages = webClient.getPage(req);
                Thread.sleep(2000);
                System.out.println(pages.asXml());
                if(pages.getElementById("PROD_TYPE")==null){
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "当前使用人数过多，请刷新页面后重新发送验证码");
                    logger.warn("河南电信：当前使用人数过多，请刷新页面后重新发送验证码"+phoneNumber+"===发送短信验证码失败===0001");
                    return map;
                }
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
                list.add(new NameValuePair("RAND_TYPE",  pagess.getElementById("RAND_TYPE").getAttribute("value")));
                list.add(new NameValuePair("BureauCode",  pagess.getElementById("BureauCode").getAttribute("value")));
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
                
                String flagMes="请等待30分钟后在发送";
                if (result.trim().contains(flagMes)) {
                    String[] arr = result.trim().split("送");
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "请等待" + arr[1].trim() + "分钟后在发送!");
                    logger.warn("河南电信"+map+"========="+phoneNumber);
                } else {
                    map.put("errorCode", "0000");
                    map.put("errorInfo", "短信已成功发送，请注意查收！");
                    session.setAttribute("HNwebClient", webClient);
                    session.setAttribute("HeNanHtmlPage", pagess);
                    logger.warn("河南电信短信发送成功"+map+"========="+phoneNumber);
                }
            } catch (Exception e) {
                logger.warn(phoneNumber+":  河南发送手机验证码   mrlu",e);
                map.put("errorCode", "0002");
                map.put("errorInfo", "网络异常！");
            }
        }
        return map;
    }

    public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String serverPwd, String phoneCode,String longitude,String latitude,String uuid) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        logger.warn("河南电信登录中========="+"传过来的参数：1=phoneNumber="+phoneNumber+"2=serverPwd="+serverPwd+"3.=phoneCode="+phoneCode);

        PushState.state(phoneNumber, "callLog",100);
        PushSocket.pushnew(map, uuid, "1000","登录中");
        String flag="1000";
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
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            PushState.state(phoneNumber, "callLog",200,"登录失败,操作异常!");
            PushSocket.pushnew(map, uuid, "3000","登录失败,操作异常!");
            logger.warn("河南电信登录中=session 为空========"+map+"-----------"+phoneNumber);
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
                list.add(new NameValuePair("RAND_TYPE",  pagess.getElementById("RAND_TYPE").getAttribute("value")));
                list.add(new NameValuePair("BureauCode",  pagess.getElementById("BureauCode").getAttribute("value")));
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
                logger.warn(phoneNumber+":河南电信短信二次身份认证结果：---------------"+ page2.asText()+"-----------");
                String flagStr="您输入的查询验证码错误或过期";
                if (result.contains(flagStr)) {
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "您输入的查询验证码错误或过期，请重新核对或再次获取！");
                    PushState.state(phoneNumber, "callLog",200,"您输入的查询验证码错误或过期，请重新核对或再次获取！");
                    PushSocket.pushnew(map, uuid, "3000","您输入的查询验证码错误或过期，请重新核对或再次获取！");
                    logger.warn(phoneNumber+":河南电信登录==验证码输入错误======="+ map+"-----------");
                    return map;
                }
                PushSocket.pushnew(map, uuid, "2000","登录成功");
                Thread.sleep(2000);
                PushSocket.pushnew(map, uuid, "5000","数据获取中");
                flag="5000";
                int boundCount=6;
                for (int i = 0; i < boundCount; i++) {
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
                    logger.warn(phoneNumber+"   "+currentDate+":河南电信本次获得数据：--------------"+ page2.asXml()+"-----------");
                    if(page2.asXml().contains("主叫号码")&&page2.asXml().contains("被叫号码")){
                        dataList.add(page2.asXml());
                    }
                    calendar.add(Calendar.MONTH, -1);
                    beforeDate = currentDate;
                }
                logger.warn(phoneNumber+"   "+"河南电信拿到的数据条数：" + dataList.size());
                //判断获取的账单是否有5个月
                if (dataList.size() < 4) {
                    PushSocket.pushnew(map, uuid, "7000", "数据获取不完全，请重新认证！(注：请确认手机号使用时长超过6个月)");
                    PushState.state(phoneNumber, "callLog", 200, "数据获取不完全，请重新认证！(注：请确认手机号使用时长超过6个月)");
                    map.put("errorCode", "0009");
                    map.put("errorInfo", "数据获取不完全，请重新再次认证！(注：请确认手机号使用时长超过6个月)");
                    return map;
                }

                PushSocket.pushnew(map, uuid, "6000","数据获取成功");
                flag="4000";
                map.put("UserIphone", phoneNumber);
                map.put("UserPassword", serverPwd);
                //经度
                map.put("longitude", longitude);
                //纬度
                map.put("latitude", latitude);
                map.put("flag", "5");
                map.put("data", dataList);
                webClient.close();


                Resttemplate resttemplate = new Resttemplate();
                map = resttemplate.SendMessage(map, ConstantInterface.port + "/HSDC/message/telecomCallRecord");
                String errorCode="errorCode";
                String resultCode="0000";
				if(map.get(errorCode).equals(resultCode)) {
					   logger.warn("河南电信数据==将要推送至数据中心==后的返回值==="+map+"-----------"+phoneCode+"------------");
					PushSocket.pushnew(map, uuid, "8000","认证成功");
					PushState.state(phoneNumber, "callLog",300);
				}else {
					 logger.warn("河南电信数据==将要推送至数据中心==后的返回值==="+map+"-----------"+phoneCode+"------------");
					PushSocket.pushnew(map, uuid, "9000",map.get("errorInfo").toString());
					PushState.state(phoneNumber, "callLog",200,map.get("errorInfo").toString());
				}
				

            } catch (Exception e) {
                logger.warn(phoneNumber+":河南获取详单异常--------------",e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
                PushState.state(phoneNumber, "callLog",200,"网络连接异常!");
                DealExceptionSocketStatus.pushExceptionSocket(flag,map,uuid);
            }
        }
        return map;
    }
}

