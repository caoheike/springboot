package com.reptile.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.WebClientFactory;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class GongShangInfo {
    private int count = 0;
    private Logger logger = LoggerFactory.getLogger(GongShangInfo.class);

    public void getSiChuanGSmes() throws IOException, InterruptedException {
        WebClient webClient = new WebClientFactory().getWebClient();
        HtmlPage page = webClient.getPage("http://sc.gsxt.gov.cn/notice");
        Thread.sleep(2000);
        String token = page.getElementByName("session.token").getAttribute("value");
        TextPage page1 = webClient.getPage("http://sc.gsxt.gov.cn/notice/pc-geetest/register?t=" + System.currentTimeMillis());
        String content = page1.getContent();
        JSONObject jsonObject = JSONObject.fromObject(content);
        String gt = jsonObject.getString("gt");
        String challenge = jsonObject.getString("challenge");

        WebRequest get = new WebRequest(new URL("http://jiyanapi.c2567.com/shibie"));
        get.setHttpMethod(HttpMethod.GET);
        List<NameValuePair> list = new ArrayList<>();
        list.add(new NameValuePair("user", "caoheike"));
        list.add(new NameValuePair("pass", "598415805"));
        list.add(new NameValuePair("return", "json"));
        list.add(new NameValuePair("ip", ""));
        list.add(new NameValuePair("model", "3"));
        list.add(new NameValuePair("gt", gt));
        list.add(new NameValuePair("challenge", challenge));
        get.setRequestParameters(list);
        TextPage page2 = webClient.getPage(get);

        String content1=page2.getWebResponse().getContentAsString("gbk");
        if(content1.contains("重试")){
            count=count+1;
            System.out.println("~~~~~~~~~~~~~~~~~极验验证失败~~~~~~~~~~~~~~~~~~~~count="+count);
            webClient.close();
            getSiChuanGSmes();
        }


        JSONObject jsonObject1 = JSONObject.fromObject(content);
        WebRequest post = new WebRequest(new URL("http://sc.gsxt.gov.cn/notice/search/ent_info_list"));
        post.setHttpMethod(HttpMethod.POST);
        list.clear();
        list.add(new NameValuePair("condition.searchType", "1"));
        list.add(new NameValuePair("captcha", ""));
        list.add(new NameValuePair("geetest_challenge", jsonObject1.getString("challenge")));
        list.add(new NameValuePair("geetest_validate", jsonObject1.getString("validate")));
        list.add(new NameValuePair("geetest_seccode", jsonObject1.getString("validate") + "|jordan"));
        list.add(new NameValuePair("session.token", token));
        list.add(new NameValuePair("condition.keyword", URLEncoder.encode("百度")));
        post.setRequestParameters(list);

        HtmlPage page3 = webClient.getPage(post);
        System.out.println(page2.getContent());

    }


    public static void main(String[] args) throws IOException, InterruptedException {
        new GongShangInfo().getSiChuanGSmes();
    }

}
