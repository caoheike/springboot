package com.reptile.service.chinatelecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.Dates;
import com.reptile.util.DealExceptionSocketStatus;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liubin
 */
@Service
public class HuBeiProviceService {
    @Autowired
    private application applications;
    private Logger logger = LoggerFactory.getLogger(HuBeiProviceService.class);

    public Map<String, Object> hubeicode(HttpServletRequest request, String phoneCode, String passPhone) {
        logger.warn(phoneCode + ":---------------------湖北电信发送短信验证码---------------------");
        Map<String, Object> map = new HashMap<String, Object>(200);
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("GBmobile-webclient");
        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            logger.warn(phoneCode + ":---------------------湖北电信发送短信验证码---操作异常：因为未登陆------------------" + phoneCode);
            return map;
        } else {
            try {
                WebClient webClient = (WebClient) attribute;
                WebRequest request1 = new WebRequest(new URL("http://hb.189.cn/pages/selfservice/feesquery/detailListQuery.jsp"));
                request1.setHttpMethod(HttpMethod.GET);
                HtmlPage page1 = webClient.getPage(request1);
                Thread.sleep(2000);
                page1.getElementById("txtAccount").setAttribute("value", phoneCode);
                page1.getElementById("txtPassword").setAttribute("value", passPhone);
                Thread.sleep(2000);

                //---------------------------------------------------------------------
                String realPath = request.getServletContext().getRealPath("/imageFile");
                File file = new File(realPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String fileName = "loadImageCode" + System.currentTimeMillis() + ".png";
                HtmlImage imgCaptcha = (HtmlImage) page1.getElementById("imgCaptcha");
                BufferedImage read = imgCaptcha.getImageReader().read(0);
                ImageIO.write(read, "png", new File(file, fileName));
                Map<String, Object> imagev = MyCYDMDemo.Imagev(realPath + "/" + fileName);
                String code = imagev.get("strResult").toString();
                HtmlInput txtCaptcha = (HtmlInput) page1.getElementById("txtCaptcha");
                txtCaptcha.setValueAttribute(code);
                //----------------------------------------------------------------------

                HtmlPage page2 = page1.getElementById("loginbtn").click();
                System.out.println(page2.asText());
                HtmlInput hiiden = (HtmlInput) page2.getElementById("CITYCODE");
                String citycode = hiiden.getAttribute("value");
                Thread.sleep(4000);
                WebRequest requests2 = new WebRequest(new URL("http://hb.189.cn/feesquery_toListQuery.action"));
                requests2.setHttpMethod(HttpMethod.POST);
                HtmlPage note = webClient.getPage(requests2);
                Thread.sleep(5000);
                WebRequest request2 = new WebRequest(new URL("http://hb.189.cn/feesquery_PhoneIsDX.action"));
                //提交方式
                request2.setHttpMethod(HttpMethod.POST);
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new NameValuePair("productNumber", phoneCode));
                list.add(new NameValuePair("cityCode", citycode));
                list.add(new NameValuePair("sentType", "C"));
                list.add(new NameValuePair("ip", "0"));
                request2.setRequestParameters(list);
                HtmlPage backtrack = webClient.getPage(request2);
                Thread.sleep(4000);
                if (backtrack.asText().indexOf(phoneCode) != -1) {
                    logger.warn("---------------------湖北电信发送短信验证码---“"
                            + backtrack.asText() + "------------短信验证码发送成功------" + phoneCode);
                    System.out.println(backtrack.asText() + "--成功--");
                    session.setAttribute("sessionWebClient-HUBEI", webClient);
                    map.put("errorCode", "0000");
                    map.put("errorInfo", backtrack.asText());
                } else {
                    logger.warn("---------------------湖北电信发送短信验证码---“"
                            + backtrack.asText() + "------------短信验证码发送失败-----" + phoneCode);
                    System.out.println(backtrack.asText() + "-----失败-----");
                    map.put("errorCode", "0001");
                    map.put("errorInfo", backtrack.asText());
                }
            } catch (Exception e) {
                logger.warn(phoneCode + ":---------------------湖北电信发送短信验证码---------------短信验证码发送失败-----", e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "请再尝试发送验证码");
            }
        }
        return map;
    }

    @SuppressWarnings("resource")
    public Map<String, Object> hubeiphone(HttpServletRequest request, String phoneCode, String phoneNume, String phonePass, String longitude, String latitude, String uuid) {
        Map<String, Object> map = new HashMap<String, Object>(200);
        WebClient webClient = null;
        PushState.state(phoneCode, "callLog", 100);
        PushSocket.pushnew(map, uuid, "1000", "登录中");
        String signle = "1000";

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("sessionWebClient-HUBEI");
        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            PushState.state(phoneNume, "callLog", 200, "登录失败,操作异常!");
            PushSocket.pushnew(map, uuid, "3000", "登录失败,操作异常!");
            return map;
        } else {
            try {
                webClient = (WebClient) attribute;
                WebRequest requests = new WebRequest(new URL("http://hb.189.cn/validateWhiteList.action"));
                //提交方式
                requests.setHttpMethod(HttpMethod.POST);
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new NameValuePair("accnbr", phoneNume));
                requests.setRequestParameters(list);
                HtmlPage back1 = webClient.getPage(requests);
                String stat = back1.asText();

                Thread.sleep(3000);
                WebRequest requests2 = new WebRequest(new URL("http://hb.189.cn/feesquery_checkCDMAFindWeb.action"));
                //提交方式
                requests2.setHttpMethod(HttpMethod.POST);
                List<NameValuePair> list2 = new ArrayList<NameValuePair>();
                list2.add(new NameValuePair("random", phoneCode));
                list2.add(new NameValuePair("sentType", "C"));
                requests2.setRequestParameters(list2);
                HtmlPage back2 = webClient.getPage(requests2);
                String stat2 = back2.asText();

                Map<String, Object> hubei = new HashMap<String, Object>(200);
                List<Map<String, Object>> datalist = new ArrayList<Map<String, Object>>();

                PushSocket.pushnew(map, uuid, "2000", "登录成功");

                Thread.sleep(2000);
                PushSocket.pushnew(map, uuid, "5000", "获取数据中");
                signle = "5000";
                int num = 3;
                for (int i = 0; i < num; i++) {
                    Map<String, Object> detailed = new HashMap<String, Object>(200);
                    List<Map<String, Object>> eachMonthList = new ArrayList<Map<String, Object>>();
                    List<NameValuePair> list3 = new ArrayList<NameValuePair>();
                    WebRequest requests3 = new WebRequest(new URL("http://hb.189.cn/feesquery_querylist.action"));
                    //提交方式
                    requests3.setHttpMethod(HttpMethod.POST);
                    String month = Dates.beforMonth(i) + "0000";
                    list3.add(new NameValuePair("startMonth", month));
                    list3.add(new NameValuePair("type", stat2));
                    list3.add(new NameValuePair("random", phoneCode));
                    requests3.setRequestParameters(list3);
                    HtmlPage back3 = webClient.getPage(requests3);
                    String phonedetailed = back3.asText();

                    if (phonedetailed.indexOf(phoneCode) != -1) {
                        map.put("errorCode", "0002");
                        map.put("errorInfo", "获取数据为空!");
                        PushState.state(phoneNume, "callLog", 200, "暂无数据");
                        PushSocket.pushnew(map, uuid, "7000", "暂无数据");
                        return map;

                    }
                    logger.warn("---------------------湖北电信获取账单---“"
                            + phonedetailed + "---------------" + phoneCode);
                    Map<String, Object> pageMap = new HashMap(200);
                    Thread.sleep(3000);
                    logger.warn("---------------------湖北电信获取账单---“"
                            + "------------获取下一页的数值---" + phoneCode);
                    int num1 = 4;
                    for (int j = 1; j < num1; j++) {
                        Map<String, Object> eachpageMap = new HashMap(200);
                        Thread.sleep(2000);
                        List<NameValuePair> list4 = new ArrayList<NameValuePair>();
                        WebRequest requests4 = new WebRequest(new URL("http://hb.189.cn/feesquery_pageQuery.action"));
                        //提交方式
                        requests4.setHttpMethod(HttpMethod.POST);
                        list4.add(new NameValuePair("page", j + ""));
                        list4.add(new NameValuePair("showCount", "100"));
                        requests4.setRequestParameters(list4);
                        HtmlPage back4 = webClient.getPage(requests4);
                        String phonedetailed2 = back4.asText();
                        eachpageMap.put("pageData", phonedetailed2);
                        eachMonthList.add(eachpageMap);
                        logger.warn("--------第" + j + "页------------" + phonedetailed2 + "--------湖北电信数据获取--------------");
                    }
                    detailed.put("item", eachMonthList);
                    datalist.add(detailed);
                }
                PushSocket.pushnew(map, uuid, "6000", "数据获取成功");
                logger.warn("--------" + map + "------------" + phoneNume + "--------湖北电信数据获取------数据获取成功----6000----");
                signle = "4000";
                hubei.put("data", datalist);
                hubei.put("UserIphone", phoneNume);
                hubei.put("longitude", longitude);
                hubei.put("latitude", latitude);
                hubei.put("flag", 12);
                hubei.put("UserPassword", phonePass);
                Resttemplate resttemplate = new Resttemplate();
                map = resttemplate.SendMessage(hubei, applications.getSendip() + "/HSDC/message/telecomCallRecord");
                String errorCode = "errorCode";
                String state0 = "0000";
                if (map != null && state0.equals(map.get(errorCode).toString())) {
                    PushState.state(phoneNume, "callLog", 300);
                    map.put("errorInfo", "查询成功");
                    map.put("errorCode", "0000");
                    logger.warn("--------" + map + "------------" + phoneNume + "--------湖北电信数据获取------认证成功----8000----");
                    PushSocket.pushnew(map, uuid, "8000", "认证成功");
                } else {
                    //--------------------数据中心推送状态----------------------
                    PushState.state(phoneNume, "callLog", 200, map.get("errorInfo").toString());
                    PushSocket.pushnew(map, uuid, "9000", map.get("errorInfo").toString());
                    logger.warn("--------" + map + "------------" + phoneNume + "--------湖北电信数据获取-----" + map.get("errorInfo").toString() + "----8000----");
                    //---------------------数据中心推送状态----------------------
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.warn("------------------" + phoneNume + "--------湖北电信数据获取失败------服务繁忙，请稍后再试-----" + e);
                e.printStackTrace();
                PushState.state(phoneNume, "callLog", 200, "服务繁忙，请稍后再试");
                //---------------------------数据中心推送状态----------------------------------
                map.clear();
                map.put("errorInfo", "服务繁忙，请稍后再试");
                map.put("errorCode", "0002");
//				 PushSocket.pushnew(map, uuid, "9000","服务繁忙，请稍后再试");
                DealExceptionSocketStatus.pushExceptionSocket(signle, map, uuid);
            }
        }
        webClient.close();
        return map;
    }
}
