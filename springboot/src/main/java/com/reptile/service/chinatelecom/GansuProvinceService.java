package com.reptile.service.chinatelecom;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.*;

import net.sf.json.JSONObject;

import org.apache.commons.lang.CharSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liubin
 */
@Service
public class GansuProvinceService {
    private Logger logger = LoggerFactory.getLogger(GansuProvinceService.class);
    private static String detailUrl = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000600";
    private static String iframeUrl = "http://gs.189.cn/service/v7/fycx/xd/index.shtml?fastcode=10000600&cityCode=gs";// /dqmh/ssoLink.do?method=linkTo&amp;platNo=10028&amp;toStUrl=http://gs.189.cn/service/v7/fycx/xd/index.shtml?fastcode=10000600&amp;cityCode=gs
    private static String peronInfoUrl = "http://gs.189.cn/web/json/getAncillaryInfo.action";

    /**
     * liubin
     * 发送短信验证码
     *
     * @param request
     * @param userNum
     * @return
     */
//    public Map<String, Object> gansuPhone(HttpServletRequest request, String userNum) {
//        Map<String, Object> map = new HashMap<String, Object>(200);
//        HttpSession session = request.getSession();
//        Object attribute = session.getAttribute("GBmobile-webclient");
//        if (attribute == null) {
//            map.put("errorCode", "0001");
//            map.put("errorInfo", "操作异常!");
//            return map;
//        } else {
//
//            try {
//                WebClient webClient = (WebClient) attribute;
//                WebRequest requests = new WebRequest(new URL("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000600"));
//                requests.setHttpMethod(HttpMethod.GET);
//                HtmlPage page1 = webClient.getPage(requests);
//                String erro = "获取验证码";
//                if (!page1.asText().contains(erro)) {
//                    map.put("errorCode", "0007");
//                    map.put("errorInfo", "操作异常！");
//                    return map;
//                }
//
//                requests.setUrl(new URL("http://gs.189.cn/service/v7/fycx/xd/index.shtml?fastcode=10000600&cityCode=gs"));
//                requests.setHttpMethod(HttpMethod.POST);
//                HtmlPage page2 = webClient.getPage(requests);
//                String value = page2.getElementById("productInfo").getAttribute("value");
//                WebRequest request1 = new WebRequest(new URL("http://gs.189.cn/web/json/sendSMSRandomNumSMZ.action"));
//                //提交方式
//                request1.setHttpMethod(HttpMethod.POST);
//                List<NameValuePair> list = new ArrayList<NameValuePair>();
//                list.add(new NameValuePair("productGroup", value));
//                request1.setRequestParameters(list);
//                UnexpectedPage page = webClient.getPage(request1);
//
//                String one = "1";
//                if (page.getWebResponse().getContentAsString().indexOf(one) != -1) {
//                    session.setAttribute("sessionWebClient-GANSU", webClient);
//                    session.setAttribute("sessionHTMLpaget-GANSU", page2);
//                    map.put("errorCode", "0000");
//                    map.put("errorInfo", "验证码发送成功!");
//                } else {
//                    map.put("errorCode", "0001");
//                    map.put("errorInfo", "电信返回值错误");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                map.put("errorCode", "0002");
//                map.put("errorInfo", "请再次尝试发送验证码");
//            }
//
//        }
//        return map;
//    }


    /**
     * cui
     * 获取详单
     */
    public Map<String, Object> gansuPhone1(HttpServletRequest request, String userCard, String userNum, String userPass, String longitude, String latitude, String uuid) {

        logger.warn(userNum + "   " + userPass + "：甘肃电信用户开始认证通话详单..........");


        Map<String, Object> gansu = new HashMap<String, Object>(200);
        List<Map<String, Object>> datalist = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>(200);
        WebClient webClientTwo = null;

        PushState.state(userNum, "callLog", 100);
        PushSocket.pushnew(map, uuid, "1000", "登录中");
        String signle = "1000";
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("GBmobile-webclient");
//        Object attribute = session.getAttribute("sessionWebClient-GANSU");
//        Object pagess = session.getAttribute("sessionHTMLpaget-GANSU");
        if (attribute == null) {
            PushSocket.pushnew(map, uuid, "3000", "登录失败,操作异常!");
            PushState.state(userNum, "callLog", 200, "登录失败,操作异常!");
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            return map;
        } else {
            try {
                Thread.sleep(2000);
                PushSocket.pushnew(map, uuid, "2000", "登录成功");
                Thread.sleep(2000);
                PushSocket.pushnew(map, uuid, "5000", "数据获取中");
                signle = "5000";
                WebClient webClient = (WebClient) attribute;
                WebRequest requests = new WebRequest(new URL("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000600"));
                requests.setHttpMethod(HttpMethod.GET);
                HtmlPage page1 = webClient.getPage(requests);
                String erro = "获取验证码";
                if (!page1.asText().contains(erro)) {
                    map.put("errorCode", "0007");
                    map.put("errorInfo", "获取数据失败，请联系业务员!");
                    PushSocket.pushnew(map, uuid, "7000", "获取数据失败，请联系业务员!");
                    PushState.state(userNum, "callLog", 200, "获取数据失败，请联系业务员!");
                    return map;
                }
                requests.setUrl(new URL("http://gs.189.cn/service/v7/fycx/xd/index.shtml?fastcode=10000600&cityCode=gs"));
                requests.setHttpMethod(HttpMethod.POST);
                HtmlPage page2 = webClient.getPage(requests);

//                HtmlPage htmlPage = (HtmlPage) pagess;
//                String busitype = htmlPage.getElementById("busitype").getAttribute("value");
//                二次身份认证
//                Map<String, Object> stringObjectMap = checkCurrentInfo(webClient, catpy, busitype, userNum, userName, userCard, catpy, HttpMethod.POST);
//                System.out.println(stringObjectMap.toString());
//
//                if(!stringObjectMap.get("errorCode").equals("0000")){
//                    PushState.state(userNum, "callLog", 200);
//                    PushSocket.pushnew(map, uuid, "1000", "登录失败");
//                    return stringObjectMap;
//                }

//                  初步估计为后台清除某种标志  0
//                String clearSession1 = "http://gs.189.cn/web/json/clearSession.action";
//                WebRequest post123 = new WebRequest(new URL(clearSession1));
//                post123.setHttpMethod(HttpMethod.POST);
//                post123.setAdditionalHeader("Referer","http://gs.189.cn/service/v7/fycx/xd/index.shtml?fastcode=10000600&cityCode=gs");
//                post123.setAdditionalHeader("X-Requested-With","XMLHttpRequest");
//                Page page45 = webClient.getPage(post123);
//                System.out.println(page45.getWebResponse().getContentAsString());
//                                String num = "4:" + userNum;

                String num = page2.getElementById("productInfo").getAttribute("value");
                System.out.println(num + "`````````````````````");
                Thread.sleep(2000);
                //循环一次 只获取一个月详单（甘肃电信要求两次查询详单间隔必须大于5min）
                int num3 = 3;
                for (int i = 0; i < num3; i++) {
                    Map<String, Object> data = new HashMap<String, Object>(200);
                    String months = Dates.beforMonth(i + 1);
                    //未办结算订单查询  返回值未被使用  认证通过情况下为{result: 1}
//                    String urls="http://gs.189.cn/web/json/searchDetailedFeeNew.action";
//                    WebRequest post321=new WebRequest(new URL(urls));
//                    post321.setHttpMethod(HttpMethod.POST);
//
//                    List<NameValuePair> listss=new ArrayList<>();
//                    listss.add(new NameValuePair("productGroup",num));
//                    listss.add(new NameValuePair("orderDetailType","6"));
//                    listss.add(new NameValuePair("queryMonth",months));
//                    listss.add(new NameValuePair("flag","1"));
//                    post321.setRequestParameters(listss);
//                    Page pageF = webClient.getPage(post321);
//                    System.out.println(pageF.getWebResponse().getContentAsString());

                    //判断当前是否通过验证 是否需要弹框（1.不弹框 0.弹框）
//                    String urlss="http://gs.189.cn/web/json/ifPopWinShow.action";
//                    WebRequest popWin=new WebRequest(new URL(urlss));
//                    popWin.setHttpMethod(HttpMethod.POST);
//                    listss.clear();
//                    listss.add(new NameValuePair("productType","4"));
//                    listss.add(new NameValuePair("accessNumber",userNum));
//                    listss.add(new NameValuePair("rand",catpy));
//                    popWin.setRequestParameters(listss);
//                    Page page3 = webClient.getPage(popWin);
//                    System.out.println(page3.getWebResponse().getContentAsString());


//                    String searchFee = "http://gs.189.cn/web/fee/preDetailedFee.action?randT=" + 546213 + "&productGroup=" + num + "&orderDetailType=6&queryMonth=" + months;
//                    Page pagesss = webClient.getPage(new URL(searchFee));
//                    System.out.println(pagesss.getWebResponse().getContentAsString());
//                    Thread.sleep(2000);


//                    String sycnUrl = "http://gs.189.cn/web/json/getAncillaryInfo.action?timestamp="+System.currentTimeMillis();
//                    WebRequest post1 = new WebRequest(new URL(sycnUrl));
//                    List<NameValuePair> dataList = new ArrayList<>();
//                    dataList.add(new NameValuePair("productGroup", num));
//                    post1.setRequestParameters(dataList);
//                    post1.setHttpMethod(HttpMethod.POST);
//                    Page page21 = webClient.getPage(post1);
//                    System.out.println(page21.getWebResponse().getContentAsString());

                    int count = 0;
                    String result = "fail";
                    do {
                        String url = "http://gs.189.cn/web/json/searchDetailedFee.action?timestamp=" + System.currentTimeMillis() + "&productGroup=" + num + "&orderDetailType=6&queryMonth=" + months;
                        WebRequest post = new WebRequest(new URL(url));
                        post.setHttpMethod(HttpMethod.POST);
                        Page pages = webClient.getPage(post);
                        Thread.sleep(2000);
                        String dataResults = pages.getWebResponse().getContentAsString();
                        logger.warn(userNum + ":该甘肃电信用户" + months + "月数据获取数据为：" + dataResults);

                        JSONObject jsonObject = JSONObject.fromObject(dataResults);
                        //result：9998 操作频繁  1数据正常
                        if (jsonObject.getString("result").equals("1")) {
                            data.put("items", dataResults);
                            datalist.add(data);
                            result = "success";
                        } else {
                            result = "fail";
                        }
                        count++;
                        //清除本次查询后的session中某条标识
                        String clearSession = "http://gs.189.cn/web/json/clearSession.action";
                        post = new WebRequest(new URL(clearSession));
                        post.setAdditionalHeader("Referer", "http://gs.189.cn/service/v7/fycx/xd/index.shtml?fastcode=10000600&cityCode=gs");
                        post.setAdditionalHeader("X-Requested-With", "XMLHttpRequest");

                        post.setHttpMethod(HttpMethod.POST);
                        webClient.getPage(post);
                    } while (result.equals("fail") && count < 3);
                    if (i < 2) {
                        int sleepTime = (int) (1000 * 60 * 5 * (Math.random() * 0.2 + 1));
                        Thread.sleep(sleepTime);
                    }
                }

                logger.warn(userNum + ":该用户-------------获取数据总条数:" + datalist.size());
                if (datalist.size() == 0) {
                    map.put("errorCode", "0007");
                    map.put("errorInfo", "本次数据获取失败，请五分钟后重试！");
                    PushSocket.pushnew(map, uuid, "7000", "本次数据获取失败，请五分钟后重试！");
                    PushState.state(userNum, "callLog", 200, "本次数据获取失败，请五分钟后重试！");
                    return map;
                }

                PushSocket.pushnew(map, uuid, "6000", "数据获取成功");
                signle = "4000";

                map.put("data", datalist);
                map.put("UserIphone", userNum);
                map.put("longitude", longitude);
                map.put("latitude", latitude);
                map.put("flag", "6");
                map.put("UserPassword", userPass);
                Resttemplate resttemplate = new Resttemplate();

                map = resttemplate.SendMessage(map, ConstantInterface.port + "/HSDC/message/telecomCallRecord");
                String errorCode = "errorCode";
                String state0 = "0000";
                if (map != null && state0.equals(map.get(errorCode).toString())) {
                    PushState.state(userNum, "callLog", 300);
                    PushSocket.pushnew(map, uuid, "8000", "认证成功");
                    map.put("errorInfo", "查询成功");
                    map.put("errorCode", "0000");
                } else {
                    PushState.state(userNum, "callLog", 200, map.get("errorInfo").toString());
                    PushSocket.pushnew(map, uuid, "9000", map.get("errorInfo").toString());
                    //---------------------数据中心推送状态----------------------
                }
                webClient.close();
            } catch (Exception e) {
                logger.warn(userNum + ": 认证过程中出现异常  ", e);
                map.clear();
                map.put("errorInfo", "服务繁忙，请稍后再试");
                map.put("errorCode", "0002");

                PushState.state(userNum, "callLog", 200, "服务繁忙，请稍后再试");
                DealExceptionSocketStatus.pushExceptionSocket(signle, map, uuid);
            }
        }
        return map;
    }

    /**
     * 二次身份校验
     *
     * @param webClient
     * @param
     * @param validatecode 短信验证码
     * @param busitype     固定3
     * @param mobilenum    电话号码
     * @param newNameO     姓名
     * @param personNoO    身份证
     * @param rand         短信验证码
     * @param method
     * @return
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    public Map<String, Object> checkCurrentInfo(WebClient webClient, String validatecode, String busitype, String mobilenum, String newNameO, String personNoO, String rand, HttpMethod method) throws FailingHttpStatusCodeException, IOException, InterruptedException {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Map<String, Object> map = new HashMap<String, Object>();

        list.add(new NameValuePair("validatecode", validatecode));
        list.add(new NameValuePair("busitype", busitype));
        list.add(new NameValuePair("mobilenum", mobilenum));
        list.add(new NameValuePair("newNameO", newNameO));
        list.add(new NameValuePair("personNoO", personNoO));
        list.add(new NameValuePair("rand", rand));
        UnexpectedPage resultPage = this.getPages(webClient, "http://gs.189.cn/web/commonJson/checkSMZ.action", list, method);
        String result = resultPage.getWebResponse().getContentAsString();

        if (result.contains("returnCode\\\":\\\"0\\\"")) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "验证码输入错误，请重新输入");
        } else if (result.contains("returnCode\\\":\\\"1\\\"")) {
            map.put("errorCode", "0000");
            map.put("errorInfo", "校验成功");
        } else if (result.contains("returnCode\\\":\\\"2\\\"")) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "请登陆后操作");
        } else if (result.contains("returnCode\\\":\\\"3\\\"")) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "输入姓名信息有误");
        } else if (result.contains("returnCode\\\":\\\"4\\\"")) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "证件号有误");
        } else {
            map.put("errorCode", "0001");
            map.put("errorInfo", "系统繁忙，请稍后再试");
        }
        return map;
    }

    /***
     * 获得HtmlPage
     * @param webClient
     * @param url
     * @param list
     * @param method post或者get
     * @return HtmlPage
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    public UnexpectedPage getPages(WebClient webClient, String url, List<NameValuePair> list, HttpMethod method) throws FailingHttpStatusCodeException, IOException, InterruptedException {

        WebRequest requests = new WebRequest(new URL(url));
        requests.setRequestParameters(list);
        requests.setCharset("gbk");
        requests.setHttpMethod(method);
        UnexpectedPage page = webClient.getPage(requests);
        Thread.sleep(2000);
        return page;
    }


    /***
     * 获得  UnexpectedPage
     * @param webClient
     * @param url
     * @param list
     * @param method post或者get
     * @return HtmlPage
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    public UnexpectedPage getDetailPages(WebClient webClient, String url, List<NameValuePair> list, HttpMethod method) throws FailingHttpStatusCodeException, IOException {

        WebRequest requests = new WebRequest(new URL(url));
        requests.setRequestParameters(list);
        requests.setHttpMethod(method);
        return webClient.getPage(requests);
    }

    /***
     * 获得HtmlPage
     * @param webClient
     * @param url
     * @param method get
     * @return HtmlPage
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    public HtmlPage getPages(WebClient webClient, String url, HttpMethod method) throws FailingHttpStatusCodeException, IOException {

        WebRequest requests = new WebRequest(new URL(url));
        requests.setHttpMethod(method);
        return webClient.getPage(requests);
    }

    /***
     * 获得periodList
     * @param webClient
     * @param url
     * @param userNum 手机号码
     * @param method post或者get
     * @return HtmlPage
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    public List<Object> getJsonPages(WebClient webClient, String url, String userNum, HttpMethod method) throws FailingHttpStatusCodeException, IOException {

        WebRequest requests = new WebRequest(new URL(url));

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        String num = "4:" + userNum;
        list.add(new NameValuePair("productGroup", num));
        requests.setRequestParameters(list);
        requests.setHttpMethod(method);
        UnexpectedPage page = webClient.getPage(requests);
        String result = page.getWebResponse().getContentAsString();
        JSONObject json = JSONObject.fromObject(result);
        if (json != null) {
            return (List<Object>) json.get("periodList");
        }
        return null;
    }
}
