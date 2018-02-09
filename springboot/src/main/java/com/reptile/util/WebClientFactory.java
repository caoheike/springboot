package com.reptile.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.reptile.springboot.Scheduler;

/**
 * webClient对象创建工厂
 *
 * @author mrlu
 * @date 2016/10/31
 */
public class WebClientFactory implements WebClientFactoryInterface {

    @Override
    public WebClient getWebClient() {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
        // 开启cookie管理
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setTimeout(30000);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.setJavaScriptTimeout(30000);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        return webClient;
    }

    /**
     * 获取webClient，不加载js
     *
     * @return
     */
    public WebClient getWebClientJs() {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setTimeout(30000);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.setJavaScriptTimeout(30000);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setJavaScriptEnabled(false);
        return webClient;
    }
}
