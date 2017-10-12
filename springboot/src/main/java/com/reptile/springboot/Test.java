package com.reptile.springboot;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.WebClientFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws Exception {
        Map map=new HashMap();
        WebClient webClient = new WebClientFactory().getWebClient();
        String url="https://uac.10010.com/portal/Service/CheckNeedVerify?callback=jQuery17209863190566662376_"+System.currentTimeMillis()+"&userName=18682940971&pwdType=01&_="+System.currentTimeMillis();
        UnexpectedPage page = webClient.getPage(url);
        System.out.println(page.getWebResponse().getContentAsString());
        String url2="https://uac.10010.com/portal/Service/SendCkMSG?callback=jQuery17209863190566662376_"+System.currentTimeMillis()+"&req_time="+System.currentTimeMillis()+"&mobile=18682940971&_="+System.currentTimeMillis();
        HtmlPage page1 = webClient.getPage(url2);
        System.out.println(page1.asText());
    }
}
