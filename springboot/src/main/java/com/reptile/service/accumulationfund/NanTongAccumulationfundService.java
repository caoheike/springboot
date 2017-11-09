package com.reptile.service.accumulationfund;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.WebClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sun.net.www.http.HttpClient;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class NanTongAccumulationfundService {

    private Logger logger = LoggerFactory.getLogger(NanTongAccumulationfundService.class);

    public Map<String, Object> getDetailMes(HttpServletRequest request, String idCard, String userName, String passWord, String cityCode) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        WebClient webClient = new WebClientFactory().getWebClient();
        try {
            logger.warn("登录南通住房公积金网");
//            String str = "http://58.221.92.98:8080/searchPersonLogon.do?spidno=" + idCard + "&spname=" + URLEncoder.encode(userName) + "&sppassword=" + passWord;

            List<String> alert = new ArrayList<>();
            CollectingAlertHandler alertHandler = new CollectingAlertHandler(alert);
            webClient.setAlertHandler(alertHandler);
            HtmlPage page = webClient.getPage("http://www.ntgjj.com/gjjcx_gr.aspx?UrlOneClass=78");
            page.getElementById("txtCode1").setAttribute("value", idCard);
            page.getElementById("txtUserName1").setAttribute("value", userName);
            page.getElementById("txtPwd1").setAttribute("value", passWord);
            page = page.getElementById("btnsub1").click();
            Thread.sleep(2000);

            if (alert.size() > 0) {
                map.put("errorCode", "0005");
                map.put("errorInfo", alert.get(0));
                return map;
            }

            String pageContext = page.asText();
            HtmlPage loadPage = null;
            int count = 0;
            if (pageContext.contains("人员代码") && pageContext.contains("操作")) {
                DomNodeList<DomElement> a = page.getElementsByTagName("a");
                count = a.size();
                loadPage = a.get(a.size() - 1).click();
                Thread.sleep(2000);
            } else {
                loadPage = page;
            }
            logger.warn("判断该账户缴存单位个数：" + count);
            pageContext = loadPage.asText();
            if (pageContext.contains("个人明细查询") && pageContext.contains("个人公积金基本信息")) {
                logger.warn("登录成功，获取到基本信息");
                HtmlPage page2 = webClient.getPage("http://58.221.92.98:8080/searchGrye.do");
                dataMap.put("base", page2.asXml());
                Calendar instance = Calendar.getInstance();
                SimpleDateFormat sim = new SimpleDateFormat("yyyy");
                String format = sim.format(instance.getTime());
                int years = Integer.parseInt(format);
                List<String> itemList = new ArrayList<>();
                logger.warn("获取详细缴纳信息");
                for (int i = 0; i < 3; i++) {
                    HtmlPage page1 = webClient.getPage("http://58.221.92.98:8080/searchGrmx.do?year=" + years);
                    Thread.sleep(1000);
                    itemList.add(page1.asXml());
                    System.out.println(page1.asText());
                    years--;
                }
                dataMap.put("item", itemList);
                map.put("data", dataMap);
                map.put("userId", idCard);
                map.put("city", cityCode);
            } else {
                map.put("errorCode", "0001");
                map.put("errorInfo", "认证过程中出现未知错误");
            }
        } catch (Exception e) {
            logger.warn("南通公积金认证失败", e);
            e.printStackTrace();
            map.put("errorCode", "0002");
            map.put("errorInfo", "系统繁忙，请稍后再试");
        } finally {
            if (webClient != null) {
                webClient.close();
            }
        }
        return map;
    }
}
