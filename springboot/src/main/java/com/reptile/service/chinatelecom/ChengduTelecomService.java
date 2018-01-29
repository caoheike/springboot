package com.reptile.service.chinatelecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.analysis.ChengduTelecomAnalysisImp;
import com.reptile.analysis.ChinaTelecomAnalysisInterface;
import com.reptile.analysis.ZJTelecomAnalysisImp;
import com.reptile.util.ConstantInterface;
import com.reptile.util.DealExceptionSocketStatus;
import com.reptile.util.HttpURLConection;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 成都电信
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Service
public class ChengduTelecomService {
    private Logger logger = LoggerFactory.getLogger(ChengduTelecomService.class);

    public Map<String, String> sendPhoneCode(HttpServletRequest request) {
        logger.warn("---------------------成都电信发送短信验证码---------------------");
        Map<String, String> map = new HashMap<String, String>(16);
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("GBmobile-webclient");
        if (attribute == null) {
            logger.warn("---------------------成都电信发送短信验证码未进行前置操作---------------------");
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            return map;
        } else {
            try {
                WebClient webClient = (WebClient) attribute;
                WebRequest requests = new WebRequest(new URL("http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10023&toStUrl=http://sc.189.cn/service/v6/xdcx?fastcode=20000326&cityCode=sc"));
                requests.setHttpMethod(HttpMethod.GET);
                HtmlPage page1 = webClient.getPage(requests);
                String flagPhone="手机验证码";
                if (!page1.asText().contains(flagPhone)) {
                    logger.warn("---------------------成都电信未到达发送短信页面---------------------");
                    map.put("errorCode", "0007");
                    map.put("errorInfo", "操作异常！");
                    return map;
                }
                String beginTime = page1.getElementById("ywblbegintime").getAttribute("value");
                String endTime = page1.getElementById("ywblendtime").getAttribute("value");
                String sendPhone = "http://sc.189.cn/service/billDetail/sendSMSAjax.jsp?dateTime1=" + beginTime + "&dateTime2=" + endTime;
                UnexpectedPage page = webClient.getPage(sendPhone);
                String result = page.getWebResponse().getContentAsString();
                String flagSuccess="成功";
                if (!result.contains(flagSuccess)) {
                   
                    JSONObject jsonObject = JSONObject.fromObject(result);
                    map.put("errorCode", "0001");
                    map.put("errorInfo", jsonObject.get("retMsg").toString());
                    logger.warn("---------------------成都电信短信验证码发送失败,失败原因："+jsonObject.get("retMsg").toString()+"---------------------");
                } else {
                    logger.warn("---------------------成都电信短信验证码发送成功---------------------");
                    map.put("errorCode", "0000");
                    map.put("errorInfo", "短信发送成功!");
                    session.setAttribute("SCmobile-webclient2", webClient);
                    session.setAttribute("SCmobile-benginTime", beginTime);
                    session.setAttribute("SCmobile-EndTime", endTime);
                }
            } catch (Exception e) {
                logger.warn("---------------------成都发送手机验证码出错---------------------", e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
            }
        }
        return map;
    }


    public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String phoneCode,
                                            String servePwd, String longitude, String latitude, String uuid) {
        logger.warn(phoneNumber+"：---------------------成都电信获取详单...---------------------");
        Map<String, Object> map = new HashMap<String, Object>(16);
        Map<String, Object> dataMap = new HashMap<String, Object>(16);
        PushState.state(phoneNumber, "callLog",100);
        PushSocket.pushnew(map, uuid, "1000","登录中");
        String signle="1000";
        List list = new ArrayList();
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("SCmobile-webclient2");
        PushState.state(phoneNumber, "callLog",100);
        if (attribute == null) {
            logger.warn(phoneNumber+"：---------------------成都电信获取详单未获取手机验证码---------------------");
            map.put("errorCode", "0001");
            map.put("errorInfo", "请先获取验证码");
            PushState.state(phoneNumber, "callLog",200,"请先获取验证码");
            PushSocket.pushnew(map, uuid, "3000","请先获取验证码");
            return map;
        } else {
            logger.warn(phoneNumber+"：---------------------成都电信获取详单开始---------------------");
            WebClient webClient = (WebClient) attribute;
            try {
                String beginTime = session.getAttribute("SCmobile-benginTime").toString();
                String endTime = session.getAttribute("SCmobile-EndTime").toString();
                byte[] bytes = Base64.encodeBase64(phoneCode.getBytes());
                String secretCode = new String(bytes);
                String getMes = "http://sc.189.cn/service/billDetail/detailQuery.jsp?startTime=" + beginTime + "&endTime=" + endTime + "&qryType=21&randomCode=" + secretCode;
                HtmlPage page = webClient.getPage(getMes);
                String result = page.asText();
                JSONObject jsonObject = JSONObject.fromObject(result);
                String record = null;
                String retCode="retCode";
                String flag0="0";
                if (jsonObject.get(retCode) == null || !flag0.equals(jsonObject.get(retCode).toString())) {
                    String resultInfo="没有查询到相应记录";
                    if (!result.contains(resultInfo)) {
                       
                        map.put("errorCode", "0001");
                        map.put("errorInfo", jsonObject.get("retMsg").toString());
                        PushState.state(phoneNumber, "callLog",200,jsonObject.get("retMsg").toString());  
                        PushSocket.pushnew(map, uuid, "3000",jsonObject.get("retMsg").toString());
                        logger.warn(phoneNumber+"：---------------------成都电信获取详单时，未能请求到正确数据,原因:"+jsonObject.get("retMsg").toString()+"---------------------");
                        return map;
                    }
                } else {
                    record = jsonObject.get("json").toString();
                    list.add(record);
                }
                PushSocket.pushnew(map, uuid, "2000","登录成功");
                Thread.sleep(2000);
                PushSocket.pushnew(map, uuid, "5000","获取数据中");
                signle="5000";
                Calendar calendar = Calendar.getInstance();
                Calendar calendar1 = Calendar.getInstance();
                int boundCount=5;
                for (int i = 0; i < boundCount; i++) {
                    //上月第一天
                    calendar.add(Calendar.MONTH, -1);
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    Date firstTime = calendar.getTime();
                    //上月最后一天
                    calendar1.set(Calendar.DAY_OF_MONTH, 1);
                    calendar1.add(Calendar.DATE, -1);
                    Date lastTime = calendar1.getTime();
                    SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
                    String startTime = simple.format(firstTime);
                    String lastsTime = simple.format(lastTime);

                    getMes = "http://sc.189.cn/service/billDetail/detailQuery.jsp?startTime=" + startTime + "&endTime=" + lastsTime + "&qryType=21&randomCode=" + secretCode;
                    page = webClient.getPage(getMes);
                    result = page.asText();
                    if (!result.contains("没有查询到相应记录")) {
                        jsonObject = JSONObject.fromObject(result);
                        record = jsonObject.get("json").toString();
                        list.add(record);
                    }
                }
               
                logger.warn(phoneNumber+"：---------------------成都电信获取详单结束--------------------本次账单数为："+list.size());
				      
                PushSocket.pushnew(map, uuid, "6000","获取数据成功");
                signle="4000";
                dataMap.put("UserIphone", phoneNumber);
                dataMap.put("UserPassword", servePwd);
                //经度
                dataMap.put("longitude", longitude);
                //纬度
                dataMap.put("latitude", latitude);
//                ChinaTelecomAnalysisInterface chengdu=new ChengduTelecomAnalysisImp();
//    			list=(List) chengdu.analysisHtml(list, phoneNumber);
                dataMap.put("data", list);
                dataMap.put("flag", "1");
                webClient.close();
                Resttemplate resttemplate = new Resttemplate();
                map=resttemplate.SendMessage(dataMap,ConstantInterface.port+"/HSDC/message/operator");
                if(map!=null&&"0000".equals(map.get("errorCode").toString())){
			    	PushState.state(phoneNumber, "callLog",300);
			    	PushSocket.pushnew(map, uuid, "8000","成都电信认证成功");
	                map.put("errorInfo","查询成功");
	                map.put("errorCode","0000");

	            }else{
	            	PushState.state(phoneNumber, "callLog",200,"成都电信认证失败");
	            	PushSocket.pushnew(map, uuid, "9000","成都电信认证失败");
	            	logger.warn("成都电信数据推送失败"+phoneNumber);
	                //PushSocket.push(map, UUID, "0001");
	            	return map;
	            }
//                JSONObject json=JSONObject.fromObject(dataMap);
//                Map<String, String> maps=new HashMap<String, String>();
//                maps.put("data", json.toString());
//                logger.warn(phoneNumber+"：---------------------成都电信获取详单推送数据中--------------------");
//                String message=HttpURLConection.sendPost(maps, "http://192.168.3.4:8088/HSDC/message/operator");
//                logger.warn("返回====="+message);
//                logger.warn(phoneNumber+"：---------------------成都电信获取详单推送数据完成--------------------本次推送返回："+map);
//            	Map<String,Object> results=net.sf.json.JSONObject.fromObject(message);
//       		 if(message.contains("0000")){
//       			 logger.warn("------------------------成都电信"+phoneNumber+"，认证成功----------------------");
//       				PushSocket.pushnew(results, uuid, "8000", "认证成功");
//       				PushState.state(phoneNumber, "callLog", 300);
//       		 }else{
//       			  logger.warn("------------------------成都电信"+phoneNumber+"，认证失败----------------------");
//       				PushSocket.pushnew(results, uuid, "9000", results.get("errorInfo").toString());
//       				PushState.state(phoneNumber, "callLog", 200, results.get("errorInfo").toString());
//       		 }
//                logger.warn(phoneNumber+"：---------------------成都电信获取详单完毕--------------------");
            } catch (Exception e) {
                logger.warn(phoneNumber+":---------------------成都获取详情异常--------------------", e);
                map.put("errorCode", "0002");
                map.put("errorInfo", "网络连接异常!");
                PushState.state(phoneNumber, "callLog",200,"网络连接异常!");
                DealExceptionSocketStatus.pushExceptionSocket(signle,map,uuid);
            }
        }
        return map;
    }

    //通过抓包登录  备用
//    public Map<String, String> loadChengDu2(HttpServletRequest request, String userName, String servePwd) {
//        Map<String, String> map = new HashMap<String, String>();
//        HttpSession session = request.getSession();
//        try {
//            WebClient webClient = new WebClientFactory().getWebClient();
//
//            List<String> list=new ArrayList<String>();
//            CollectingAlertHandler CollectingAlertHandler=new CollectingAlertHandler(list);
//            webClient.setAlertHandler(CollectingAlertHandler);
//            HtmlPage page = webClient.getPage("http://login.189.cn/web/login");
//            page.getElementById("txtAccount").setAttribute("value", userName);
//            page.getElementById("txtPassword").setAttribute("value", servePwd);
//            page.executeJavaScript("$(\"#txtPassword\").val($.trim($(\"#txtPassword\").val()));alert($(\"#txtPassword\").valAesEncryptSet())");
//            String s="";
//            if(list.size()>0){
//                 s= list.get(0);
//                System.out.println(s);
//            }
//            WebRequest request1=new WebRequest(new URL("http://login.189.cn/web/login"));
//            request1.setHttpMethod(HttpMethod.POST);
//            List<NameValuePair> params=new ArrayList<NameValuePair>();
//            params.add(new NameValuePair("Account",userName));
//            params.add(new NameValuePair("UType","201"));
//            params.add(new NameValuePair("ProvinceID","23"));
//            params.add(new NameValuePair("AreaCode",""));
//            params.add(new NameValuePair("CityNo",""));
//            params.add(new NameValuePair("RandomFlag","0"));
//            params.add(new NameValuePair("Password",s));
//            params.add(new NameValuePair("Captcha",""));
//            request1.setRequestParameters(params);
//            HtmlPage loginbtn = webClient.getPage(request1);
//            System.out.println(loginbtn.asXml());
////            HtmlPage loginbtn = page.getElementById("loginbtn").click();
////            Thread.sleep(1000);
//            if (!loginbtn.asText().contains("详单查询")) {
//                String divErr = loginbtn.getElementById("divErr").getTextContent();
//                map.put("errorCode", "0007");
//                map.put("errorInfo", divErr);
//            } else {
//                map.put("errorCode", "0000");
//                map.put("errorInfo", "登陆成功");
//                session.setAttribute("GBmobile-webclient", webClient);
//            }
//
//        } catch (Exception e) {
//            map.put("errorCode", "0001");
//            map.put("errorInfo", "网络连接异常!");
//            e.printStackTrace();
//        }
//        return map;
//    }
}
