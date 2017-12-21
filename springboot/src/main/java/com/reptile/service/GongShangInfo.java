package com.reptile.service;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.WebClientFactory;
import org.w3c.dom.NamedNodeMap;

import java.io.IOException;

public class GongShangInfo {
    public static void main(String[] args) throws IOException {
        WebClient webClient = new WebClientFactory().getWebClient();
        HtmlPage page = webClient.getPage("http://sn.gsxt.gov.cn");

        TextPage page1 = webClient.getPage("http://sn.gsxt.gov.cn/StartCaptchaServlet?&time=" + System.currentTimeMillis());


        System.out.println(page1.getContent());

    }

}
