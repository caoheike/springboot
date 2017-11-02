package com.reptile.service.socialSecurity;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.WebClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NanTongSocialSecurityService {
    private Logger logger = LoggerFactory.getLogger(NanTongSocialSecurityService.class);

    public Map<String, Object> getDetailMes(HttpServletRequest request, String idCard, String socialCard, String passWord, String cityCode) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        List<String> dataList = new ArrayList<>();
        WebClient webClient = new WebClientFactory().getWebClient();
        try {
            logger.warn("进入南通社保查询页面");
            HtmlPage page = webClient.getPage("http://www.jsnt.lss.gov.cn:1002/query/");
            String loginType = page.getElementById("loginType").getAttribute("value");
            String checkcode = page.getElementById("checkcode").getAttribute("value");

            WebRequest post = new WebRequest(new URL("http://www.jsnt.lss.gov.cn:1002/query/loginvalidate.html"));
            post.setHttpMethod(HttpMethod.POST);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new NameValuePair("type", loginType));
            list.add(new NameValuePair("checkcode", checkcode));
            list.add(new NameValuePair("account", socialCard));
            list.add(new NameValuePair("password", passWord));
            post.setRequestParameters(list);

            HtmlPage page1 = webClient.getPage(post);
            Thread.sleep(1000);
            System.out.println(page1.asText());
            String result = page1.asText();

            if (!result.contains("success")) {
                map.put("errorCode", "0002");
                map.put("errorInfo", "密码和用户名不匹配,如忘记密码，请携带身份证到社保局重置！");
                return map;
            }

            logger.warn("南通社保登录成功");
            String substring = result.substring(2, result.length() - 2);
            String[] split = substring.split("\\|");
            String userid = split[1];
            String sessionid = split[2];

            webClient.getPage("http://www.jsnt.lss.gov.cn:1002/query/index.html?userid=" + userid + "&sessionid=" + sessionid);
            logger.warn("获取南通社保基本信息");
            HtmlPage page2 = webClient.getPage("http://www.jsnt.lss.gov.cn:1002/query/person/personYLNZH.html");
            System.out.println(page2.asText());
            dataMap.put("base", page2.asXml());
            logger.warn("获取南通社保缴纳详情");
            post = new WebRequest(new URL("http://www.jsnt.lss.gov.cn:1002/query/person/personJFJL_result.html"));
            list = new ArrayList<>();
            list.add(new NameValuePair("aae002_b", ""));
            list.add(new NameValuePair("aae002_e", ""));
            list.add(new NameValuePair("aae140", "11"));
            list.add(new NameValuePair("pageNo", "1"));
            post.setRequestParameters(list);
            page2 = webClient.getPage(post);
            System.out.println(page2.asText());
            dataList.add(page2.asXml());
            result = page2.asText();
            String[] split1 = result.split("当前1/");
            String countStr = split1[1].substring(0, split1[1].length() - 1);
            int count = Integer.parseInt(countStr);             //详单页数
            Thread.sleep(1000);
            for (int i = 2; i <= count; i++) {
                post = new WebRequest(new URL("http://www.jsnt.lss.gov.cn:1002/query/person/personJFJL_result.html"));
                list = new ArrayList<>();
                list.add(new NameValuePair("aae002_b", ""));
                list.add(new NameValuePair("aae002_e", ""));
                list.add(new NameValuePair("aae140", "11"));
                list.add(new NameValuePair("pageNo", i + ""));
                post.setRequestParameters(list);
                page2 = webClient.getPage(post);
                System.out.println(page2.asText());
                dataList.add(page2.asXml());
                Thread.sleep(1000);
            }
            logger.warn("获取南通社保缴纳详情成功");
            dataMap.put("item", dataList);
            map.put("data", dataMap);
            map.put("userId", idCard);
            map.put("city", cityCode);
        } catch (Exception e) {
            logger.warn("南通社保信息获取失败", e);
            e.printStackTrace();
            map.put("errorCode", "0001");
            map.put("errorInfo", "系统繁忙，请稍后再试");
        } finally {
            if (webClient != null) {
                webClient.close();
            }
        }
        return map;
    }
}
