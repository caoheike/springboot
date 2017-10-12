package com.reptile.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.reptile.springboot.Scheduler;

/**
 * Created by Administrator on 2017/8/18.
 */
public class WebClientFactory {



    public WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45, Scheduler.ip, Scheduler.port);
//    public WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);

    public WebClient getWebClient() {
        webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setTimeout(90000);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.setJavaScriptTimeout(40000);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        return webClient;
    }
}
