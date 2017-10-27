package com.reptile.service.accumulationfund;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.WebClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Service
public class GuiYangService {
    private Logger logger= LoggerFactory.getLogger(GuiYangService.class);

    public Map<String,String> loadMethod(HttpServletRequest request, String userCard, String password) throws IOException {
        logger.warn("登录贵阳住房公积金");
        WebClient webClient = new WebClientFactory().getWebClient();
        HtmlPage page = webClient.getPage("http://zxcx.gygjj.gov.cn/");
        return null;
    }
}
