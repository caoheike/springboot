package com.reptile.service;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.ConstantInterface;
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
public class GuangXiTelecomService {
    private Logger logger= LoggerFactory.getLogger(GuangXiTelecomService.class);

    public Map<String, Object> sendPhoneCode(HttpServletRequest request, String phoneNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("GBmobile-webclient");
        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常");
        } else {
            WebClient webClient = (WebClient) attribute;
            try {
                HtmlPage page = webClient.getPage("http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10021&toStUrl=http://gx.189.cn/public/login_jt_sso.jsp%3FVISIT_URL=/chaxun/iframe/user_center.jsp?SERV_NO=FCX-4");
                Thread.sleep(4000);
                HtmlPage click = page.getElementById("PASSWORD").getNextElementSibling().click();
                Thread.sleep(4000);
//                System.out.println(click.asXml());
                String popup = click.getElementById("popup").getFirstChild().getChildNodes().get(1).getTextContent();

                if (popup.contains("获取成功")) {
                    map.put("errorCode", "0000");
                    map.put("errorInfo", "短信验证码已发送到您的手机，请注意查收！");
                } else {
                    map.put("errorCode", "0002");
                    map.put("errorInfo", popup);
                }
                HtmlAnchor popup1 = (HtmlAnchor) click.getElementById("popup").getFirstChild().getChildNodes().get(2).getChildNodes().get(0);
                HtmlPage click1 = popup1.click();
                Thread.sleep(1000);

//                System.out.println(click1.asXml());
                session.setAttribute("GXDXwebClient", webClient);
                session.setAttribute("GXDXHtmlPage", click1);
            } catch (Exception e) {
                logger.warn(e.getMessage()+" 广西发送手机验证码   mrlu",e);
                e.printStackTrace();
                map.put("errorCode", "0005");
                map.put("errorInfo", "网络异常");
            }
        }
        return map;
    }


    public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String serverPwd, String phoneCode, String userName,
                                            String userCard,String longitude,String latitude) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> dataList=new ArrayList<String>();
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("GXDXwebClient");
        Object pages = session.getAttribute("GXDXHtmlPage");

        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常");
            return map;
        } else {
            WebClient webClient = (WebClient) attribute;
            HtmlPage fpage = (HtmlPage) pages;
            try {
                fpage.getElementById("PASSWORD").setAttribute("value", phoneCode);
                fpage.getElementById("CUST_NAME").setAttribute("value", userName);
                fpage.getElementById("CARD_NO").setAttribute("value", userCard);
                HtmlPage xmlPage = fpage.getElementById("buttonQry3").click();
                Thread.sleep(2000);
//                System.out.println(xmlPage.asXml());

                if(xmlPage.getElementById("popup")!=null){
                    String popup = xmlPage.getElementById("popup").getFirstChild().getChildNodes().get(1).getTextContent();

                    HtmlAnchor popup1 = (HtmlAnchor) xmlPage.getElementById("popup").getFirstChild().getChildNodes().get(2).getChildNodes().get(0);
                    HtmlPage click1 = popup1.click();

                    Thread.sleep(1000);
                    map.put("errorCode", "0003");
                    map.put("errorInfo", popup);
                    return map;
                }
                SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
                Calendar calendar=Calendar.getInstance();
                String lastTime = sim.format(calendar.getTime());
                String startTime=lastTime;

                for (int i = 0; i < 6; i++) {
                    WebRequest request1 = new WebRequest(new URL("http://gx.189.cn/chaxun/iframe/inxxall_new.jsp"));
                    request1.setHttpMethod(HttpMethod.POST);
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new NameValuePair("ACC_NBR", phoneNumber));
                    list.add(new NameValuePair("PROD_TYPE", "2020966"));
                    list.add(new NameValuePair("BEGIN_DATE", ""));
                    list.add(new NameValuePair("END_DATE", ""));
                    list.add(new NameValuePair("REFRESH_FLAG", "1"));
                    list.add(new NameValuePair("QRY_FLAG", "1"));
                    list.add(new NameValuePair("FIND_TYPE", "1031"));
                    list.add(new NameValuePair("radioQryType", "on"));
                    list.add(new NameValuePair("ACCT_DATE", startTime));
                    list.add(new NameValuePair("ACCT_DATE_1", lastTime));
                    request1.setRequestParameters(list);

                    HtmlPage page1 = webClient.getPage(request1);
                    Thread.sleep(1000);
                    lastTime = sim.format(calendar.getTime());
                    calendar.add(Calendar.MONTH,-1);
                    startTime=sim.format(calendar.getTime());
//                    System.out.println(page1.asXml());
                    dataList.add(page1.asXml());
                }
                map.put("data",dataList);
                map.put("flag","7");
                map.put("UserPassword",serverPwd);
                map.put("UserIphone",phoneNumber);
                map.put("longitude", longitude);//经度
                map.put("latitude", latitude);//纬度
                Resttemplate resttemplate=new Resttemplate();
                map = resttemplate.SendMessage(map, ConstantInterface.port+"/HSDC/message/telecomCallRecord");
            } catch (Exception e) {
                logger.warn(e.getMessage()+" 广西获取详单信息   mrlu",e);
                e.printStackTrace();
                map.put("errorCode", "0005");
                map.put("errorInfo", "网络异常");
            }
        }
        return map;
    }
}
