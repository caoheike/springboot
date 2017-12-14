package com.reptile.service.ChinaTelecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.ConstantInterface;
import com.reptile.util.PushSocket;
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


@Service
public class ChengduTelecomService {
    private Logger logger = LoggerFactory.getLogger(ChengduTelecomService.class);

//    public Map<String, Object> getImageCode(HttpServletRequest request) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        Map<String, String> imageMap = new HashMap<String, String>();
//        HttpSession session = request.getSession();
//
//        WebClient webClient = new WebClientFactory().getWebClient();
//        try {
//            File file = new File(request.getServletContext().getRealPath("/imageCode"));
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            String fileName = "sc" + System.currentTimeMillis() + ".png";
//            HtmlPage page = webClient.getPage("http://login.189.cn/web/login");
//            UnexpectedPage page1 = webClient.getPage("http://login.189.cn/web/captcha?undefined&source=login&width=100&height=37&" + Math.random());
//            InputStream inputStream = page1.getInputStream();
//            BufferedImage read = ImageIO.read(inputStream);
//            ImageIO.write(read, "png", new File(file, fileName));
//
//            map.put("errorCode", "0000");
//            map.put("errorInfo", "操作成功");
//            imageMap.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/imageCode/" + fileName);
//            map.put("data", imageMap);
//            session.setAttribute("vecHtmlPage-sc", page);
//            session.setAttribute("vecWebClinet-sc", webClient);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            map.put("errorCode", "0001");
//            map.put("errorInfo", "网络连接异常!");
//        }
//        return map;
//    }


    public Map<String, String> sendPhoneCode(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        HttpSession session = request.getSession();

        Object attribute = session.getAttribute("GBmobile-webclient");

        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            return map;
        } else {
            try {
                WebClient webClient = (WebClient) attribute;
                WebRequest requests = new WebRequest(new URL("http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10023&toStUrl=http://sc.189.cn/service/v6/xdcx?fastcode=20000326&cityCode=sc"));
                requests.setHttpMethod(HttpMethod.GET);
                HtmlPage page1 = webClient.getPage(requests);
                if (!page1.asText().contains("手机验证码")) {
                    map.put("errorCode", "0007");
                    map.put("errorInfo", "操作异常！");
                    return map;
                }
                String beginTime = page1.getElementById("ywblbegintime").getAttribute("value");
                String endTime = page1.getElementById("ywblendtime").getAttribute("value");
                String sendPhone = "http://sc.189.cn/service/billDetail/sendSMSAjax.jsp?dateTime1=" + beginTime + "&dateTime2=" + endTime;
                UnexpectedPage page = webClient.getPage(sendPhone);
                String result = page.getWebResponse().getContentAsString();
                if (!result.contains("成功")) {
                    JSONObject jsonObject = JSONObject.fromObject(result);
                    map.put("errorCode", "0001");
                    map.put("errorInfo", jsonObject.get("retMsg").toString());
                } else {
                    map.put("errorCode", "0000");
                    map.put("errorInfo", "短信发送成功!");
                    session.setAttribute("SCmobile-webclient2", webClient);
                    session.setAttribute("SCmobile-benginTime", beginTime);
                    session.setAttribute("SCmobile-EndTime", endTime);
                }

            } catch (Exception e) {
                logger.warn("成都发送手机验证码mrlu", e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
            }
        }
        return map;
    }


    public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String phoneCode,
                                            String servePwd, String longitude, String latitude, String UUID) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        PushSocket.pushnew(map, UUID, "1000","登录中");
        List list = new ArrayList();
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("SCmobile-webclient2");

        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "手机验证码错误,请重新获取!");
            PushSocket.pushnew(map, UUID, "3000","登录失败");
            return map;
        } else {
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
                PushSocket.pushnew(map, UUID, "2000","登录成功");
                Thread.sleep(2000);
                PushSocket.pushnew(map, UUID, "5000","获取数据中");
                String record = null;
                if (jsonObject.get("retCode") == null || !"0".equals(jsonObject.get("retCode").toString())) {
                    if (!result.contains("没有查询到相应记录")) {
                        map.put("errorCode", "0001");
                        map.put("errorInfo", jsonObject.get("retMsg").toString());
                        return map;
                    }
                } else {
                    //PushSocket.push(map, UUID, "0000");
                    record = jsonObject.get("json").toString();
                    list.add(record);
                }


                Calendar calendar = Calendar.getInstance();
                Calendar calendar1 = Calendar.getInstance();

                for (int i = 0; i < 5; i++) {
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
                if(list.size()>0) {
                	PushSocket.pushnew(map, UUID, "6000","获取数据成功");
                }else {
                	PushSocket.pushnew(map, UUID, "7000","获取数据失败");
                }
                dataMap.put("UserIphone", phoneNumber);
                dataMap.put("UserPassword", servePwd);
                dataMap.put("longitude", longitude);//经度
                dataMap.put("latitude", latitude);//纬度
                dataMap.put("flag", "1");
                dataMap.put("data", list);
                webClient.close();
                Resttemplate resttemplate = new Resttemplate();
                map = resttemplate.SendMessage(dataMap, ConstantInterface.port + "/HSDC/message/telecomCallRecord");
               if(map.get("errorCode").equals("0000")) {
            	   PushSocket.pushnew(map, UUID, "8000","认证成功");   
               }else{
            	   PushSocket.pushnew(map, UUID, "9000","认证失败");  
               }
            } catch (Exception e) {
                logger.warn("成都获取详情mrlu", e);
                map.put("errorCode", "0002");
                map.put("errorInfo", "网络连接异常!");
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
