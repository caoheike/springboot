package com.reptile.service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.springboot.Scheduler;
import com.reptile.util.ConstantInterface;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;
import org.json.XML;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZXBankService {


    private Logger logger = LoggerFactory.getLogger(ZXBankService.class);


    public Map<String, Object> getZXImageCode(HttpServletRequest request) {
        //获取验证码并保存在本地
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> mapInfo = new HashMap<String, Object>();
        HttpSession session = request.getSession();

        HttpClient httpClient = new HttpClient();
        httpClient.getHostConfiguration().setProxy(Scheduler.ip, Scheduler.port);
        GetMethod postVec = new GetMethod("https://creditcard.ecitic.com/citiccard/ucweb/newvalicode.do?time=" + System.currentTimeMillis());

        try {
            httpClient.executeMethod(postVec);
            Header responseHeader = postVec.getResponseHeader("Set-Cookie");
            String cok = responseHeader.getValue();

            InputStream responseBodyAsStream = postVec.getResponseBodyAsStream();

            String path = request.getServletContext().getRealPath("/zxImageCode");
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String fileName = "zx" + System.currentTimeMillis() + ".jpg";
            BufferedImage BufferedImage = ImageIO.read(responseBodyAsStream);
            ImageIO.write(BufferedImage, "JPG", new File(file, fileName));

            session.setAttribute("ZXhttpClient", httpClient);
            session.setAttribute("ZXImageCodeCook", cok);
            map.put("errorInfo", "操作成功");
            map.put("errorCode", "0000");
            mapInfo.put("imageCodePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/zxImageCode/" + fileName);
            map.put("data", mapInfo);
        } catch (Exception e) {

            logger.warn(e.getMessage() + "     mrlu");

            e.printStackTrace();
            Scheduler.sendGet(Scheduler.getIp);
            map.put("errorInfo", "系统繁忙");
            map.put("errorCode", "0001");
        }
        return map;
    }


    public Map<String, String> loadZX(HttpServletRequest request, String userNumber, String passWord, String imageCode) {

        Map<String, String> map = new HashMap<String, String>();
        HttpSession session = request.getSession();

        Object zxhttpClient = session.getAttribute("ZXhttpClient");
        Object zxImageCodeCook = session.getAttribute("ZXImageCodeCook");

        if (zxhttpClient == null || zxImageCodeCook == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            return map;
        } else {
            HttpClient httpClient = (HttpClient) zxhttpClient;
            String cok = zxImageCodeCook.toString();

            try {
                //通过webclient加密密码返回加密后密码passWordSec
                WebClient webClient = new WebClientFactory().getWebClient();
                HtmlPage page = webClient.getPage("https://creditcard.ecitic.com/citiccard/ucweb/entry.do");
                List<String> alert = new ArrayList<String>();
                CollectingAlertHandler collectingAlertHandler = new CollectingAlertHandler(alert);
                webClient.setAlertHandler(collectingAlertHandler);
                ((HtmlInput) page.getElementById("pwd")).setValueAttribute(passWord);
                page.executeJavaScript("var secretcode = $(\"input[name='psd']\").val();\n" + "\t\tsecretcode = hex_md5($.trim(secretcode)); alert(secretcode);");
                String passWordSec = "";
                if (alert.size() > 0 && alert.get(0).length() > 0) {
                    passWordSec = alert.get(0);
                } else {
                    map.put("errorCode", "0002");
                    map.put("errorInfo", "页面加密出现故障");
                    return map;
                }
                System.out.println(passWordSec);
                //通过同一cookie提交页面信息，认证账户信息
                PostMethod post = new PostMethod("https://creditcard.ecitic.com/citiccard/ucweb/login.do?date=" + System.currentTimeMillis());
                post.setRequestHeader("Cookie", cok);
                String str = "{loginType:'01',memCode:'" + passWordSec + "',isBord:'false',phone:'" + userNumber + "',page:'new',source:'PC',valiCode:'" + imageCode + "'}";
                RequestEntity entity = new StringRequestEntity(str, "text/html", "utf-8");
                post.setRequestEntity(entity);
                httpClient.executeMethod(post);
                System.out.println(post.getResponseBodyAsString());
                String result = post.getResponseBodyAsString();
                if (!result.contains("验证成功")) {
                    net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(result);
                    map.put("errorCode", "0002");
                    map.put("errorInfo", jsonObject.get("retMsg").toString());
                    return map;
                }
                Header header = post.getResponseHeader("Set-Cookie");
                String coks = header.getValue();
                //如果认证成功，则跳转到手机短信页面
                GetMethod post1 = new GetMethod("https://creditcard.ecitic.com/citiccard/ucweb/sendSmsInit.do?date=" + System.currentTimeMillis());
                post1.setRequestHeader("Cookie", coks);
                httpClient.executeMethod(post1);
                post1.getParams().setContentCharset("utf-8");
                result = post1.getResponseBodyAsString();
                if (!result.contains("短信验证码")) {
                    map.put("errorCode", "0003");
                    map.put("errorInfo", "操作失败");
                    return map;
                }
                map.put("errorCode", "0000");
                map.put("errorInfo", "操作成功");
                session.setAttribute("zxCookies2", coks);
            } catch (Exception e) {

                logger.warn(e.getMessage() + "     mrlu");

                e.printStackTrace();
                map.put("errorCode", "0005");
                map.put("errorInfo", "系统繁忙");
            }
        }
        return map;
    }


    public Map<String, String> sendPhoneCode(HttpServletRequest request) {

        //发送手机验证码
        Map<String, String> map = new HashMap<String, String>();
        HttpSession session = request.getSession();

        Object zxhttpClient = session.getAttribute("ZXhttpClient");
        Object zxImageCodeCook = session.getAttribute("zxCookies2");

        if (zxhttpClient == null || zxImageCodeCook == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            return map;
        } else {
            HttpClient httpClient = (HttpClient) zxhttpClient;
            String coks = zxImageCodeCook.toString();
            try {
                PostMethod postPhone = new PostMethod("https://creditcard.ecitic.com/citiccard/ucweb/sendSms.do?date=" + System.currentTimeMillis());
                postPhone.setRequestHeader("Cookie", coks);
                httpClient.executeMethod(postPhone);
                System.out.println(postPhone.getResponseBodyAsString());
                String result = postPhone.getResponseBodyAsString();
                if (!result.contains("发送成功")) {
                    net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(result);
                    map.put("errorCode", "0002");
                    map.put("errorInfo", jsonObject.get("rtnMsg").toString());
                    return map;
                }
                map.put("errorCode", "0000");
                map.put("errorInfo", "短信发送成功");
            } catch (Exception e) {

                logger.warn(e.getMessage() + "     mrlu");

                e.printStackTrace();
                map.put("errorCode", "0003");
                map.put("errorInfo", "系统繁忙");
            }
        }
        return map;
    }


    public Map<String, Object> getDetailMes(HttpServletRequest request, String userCard, String phoneCode) {

        Map<String, Object> map = new HashMap<String, Object>();
        HttpSession session = request.getSession();
        Object zxhttpClient = session.getAttribute("ZXhttpClient");

        Object zxImageCodeCook = session.getAttribute("zxCookies2");

        if (zxhttpClient == null || zxImageCodeCook == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            return map;
        } else {
            HttpClient httpClient = (HttpClient) zxhttpClient;
            String coks = zxImageCodeCook.toString();
            try {
                //提交短信验证码
                PostMethod postM = new PostMethod("https://creditcard.ecitic.com/citiccard/ucweb/checkSms.do?date=" + System.currentTimeMillis());
                postM.setRequestHeader("Cookie", coks);
                String str1 = "{smsCode:'" + phoneCode + "'}";
                RequestEntity entity1 = new StringRequestEntity(str1, "text/html", "utf-8");
                postM.setRequestEntity(entity1);
                httpClient.executeMethod(postM);
                postM.getParams().setContentCharset("utf-8");
                System.out.println(postM.getResponseBodyAsString());

                if (!postM.getResponseBodyAsString().contains("校验成功")) {
                    net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(postM.getResponseBodyAsString());
                    map.put("errorCode", "0001");
                    map.put("errorInfo", jsonObject.get("rtnMsg").toString());
                    return map;
                }
                //成功进入信用卡信息页面
                GetMethod getMethod = new GetMethod("https://creditcard.ecitic.com/citiccard/newonline/myaccount.do?func=mainpage");
                getMethod.setRequestHeader("Cookie", coks);
                httpClient.executeMethod(getMethod);
                getMethod.getParams().setContentCharset("utf-8");
                System.out.println(getMethod.getResponseBodyAsString());
                
                if (getMethod.getResponseBodyAsString().contains("您还未绑卡，暂不支持业务办理")) {
                    map.put("errorCode", "0003");
                    map.put("errorInfo", "您还未绑卡，暂不支持业务办理");
                    return map;
                }
                
                //查询信用卡额度及可提现额度
                PostMethod method = new PostMethod("https://creditcard.ecitic.com/citiccard/newonline/settingManage.do?func=getCreditLimit");
                method.setRequestHeader("Cookie", coks);
                httpClient.executeMethod(method);
                System.out.println(method.getResponseBodyAsString());
                String result = method.getResponseBodyAsString();
                JSONObject jsonObject3 = XML.toJSONObject(result);
                JSONObject response2 = (JSONObject) jsonObject3.get("response");
                JSONObject creditLimit = (JSONObject) response2.get("CreditLimit");
                String cashmoney = creditLimit.get("cashmoney").toString();
                String fixedEd = creditLimit.get("fixedEd").toString();


                //查询该账号下对应的银行卡信息有几个
                PostMethod postMethod = new PostMethod("https://creditcard.ecitic.com/citiccard/newonline/common.do?func=querySignCards");
                postMethod.setRequestHeader("Cookie", coks);
                httpClient.executeMethod(postMethod);
                System.out.println(postMethod.getResponseBodyAsString());

                //将得到的xml转换为json数据
                String cardResult = postMethod.getResponseBodyAsString();
                JSONObject jsonObject = XML.toJSONObject(cardResult);
                System.out.println(jsonObject);
                String response1 = jsonObject.get("response").toString();
                net.sf.json.JSONObject jsonObject1 = net.sf.json.JSONObject.fromObject(response1);
                net.sf.json.JSONArray cardlist1 = jsonObject1.getJSONArray("cardlist");

                List dataList = new ArrayList();
                for (int i = 0; i < cardlist1.size(); i++) {
                    net.sf.json.JSONObject jsonObject2 = cardlist1.getJSONObject(i);
                    String card_nbr = jsonObject2.get("card_nbr").toString();
                    String card_des = jsonObject2.get("card_desc").toString();
                    if (!card_des.contains("银联")) {
                        continue;
                    }
                    PostMethod postMethod1 = new PostMethod("https://creditcard.ecitic.com/citiccard/newonline/billQuery.do?func=queryBillInfo");
                    postMethod1.setRequestHeader("Cookie", coks);
                    NameValuePair param1 = new NameValuePair("cardNo", card_nbr);
                    NameValuePair param2 = new NameValuePair("stmt_date", "");
                    NameValuePair param3 = new NameValuePair("crytype", "156");
                    NameValuePair param4 = new NameValuePair("start_pos", "1");
                    NameValuePair param5 = new NameValuePair("count", "12");
                    NameValuePair param6 = new NameValuePair("rowsPage", "10");
                    NameValuePair param7 = new NameValuePair("startpos", "1");
                    postMethod1.setRequestBody(new NameValuePair[]{param1, param2, param3, param4, param5, param6, param7});
                    httpClient.executeMethod(postMethod1);
                    System.out.println(postMethod1.getResponseBodyAsString());

                    JSONObject json = XML.toJSONObject(postMethod1.getResponseBodyAsString());
                    String response = json.get("response").toString();
                    net.sf.json.JSONObject jsonO = net.sf.json.JSONObject.fromObject(response);
                    net.sf.json.JSONArray cardlist = jsonO.getJSONArray("billsMonthList");

                    int size = 0;
                    if (cardlist != null) {
                        if (cardlist.size() > 6) {
                            size = 6;
                        } else {
                            size = cardlist.size();
                        }
                    }

                    for (int j = 0; j < size; j++) {
                        PostMethod postMeth = new PostMethod("https://creditcard.ecitic.com/citiccard/newonline/billQuery.do?func=queryBillInfo");
                        postMeth.setRequestHeader("Cookie", coks);
                        NameValuePair param11 = new NameValuePair("cardNo", card_nbr);
                        NameValuePair param21 = new NameValuePair("stmt_date", cardlist.getJSONObject(j).get("stmt_date").toString());
                        NameValuePair param31 = new NameValuePair("crytype", "156");
                        NameValuePair param41 = new NameValuePair("start_pos", "1");
                        NameValuePair param51 = new NameValuePair("count", "12");
                        NameValuePair param61 = new NameValuePair("rowsPage", "10");
                        NameValuePair param71 = new NameValuePair("startpos", "1");
                        postMeth.setRequestBody(new NameValuePair[]{param11, param21, param31, param41, param51, param61, param71});
                        httpClient.executeMethod(postMeth);
                        dataList.add(postMeth.getResponseBodyAsString());
                        Thread.sleep(500);
                    }
                }

                Map<String, Object> sendMap = new HashMap<String, Object>();
                sendMap.put("idcard", userCard);
                sendMap.put("backtype", "CCB");
                sendMap.put("html", dataList);
                sendMap.put("fixedEd",fixedEd);

                logger.warn(sendMap.toString()+"   mrlu");

                //推送信息
                Map<String, Object> mapTui = new HashMap<String, Object>();
                mapTui.put("data", sendMap);
                Resttemplate rs = new Resttemplate();
                map = rs.SendMessage(mapTui, ConstantInterface.port + "/HSDC/BillFlow/BillFlowByreditCard");
                System.out.println(map);

            } catch (Exception e) {

                logger.warn(e.getMessage() + "     mrlu");

                e.printStackTrace();
                map.put("errorCode", "0002");
                map.put("errorInfo", "查询出错");
            }
        }
        return map;
    }

}
