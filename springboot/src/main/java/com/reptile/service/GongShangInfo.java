package com.reptile.service;

import com.gargoylesoftware.htmlunit.Page;
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
        DomNodeList<DomElement> input = page.getElementsByTagName("input");
        for(int i=0;i<input.size();i++){
            NamedNodeMap attributes = input.get(i).getAttributes();
            for(int j=0;j<attributes.getLength();j++){
                String textContent = attributes.item(j).getTextContent();
                if(textContent.contains("请输入企业名称、统一社会信用代码或注册号")){
                    input.get(i).setAttribute("value","");
                }
            }
        }
    }

}
