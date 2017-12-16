package com.reptile.contorller;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.WebClientFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

@Controller
@RequestMapping("LingYingMessageController")
public class LingYingMessageController {

    @ApiOperation(value = "获取领英信息",notes = "名字，姓")
    @RequestMapping(value = "getDetialMes",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> getDetialMes(@RequestParam("name") String name,@RequestParam("gender")String gender){
        WebClient webClient = new WebClientFactory().getWebClient();
        try {
            webClient.addRequestHeader("accept-language","zh-CN,zh;q=0.8");
            WebRequest get=new WebRequest(new URL("https://www.linkedin.com/in/%E6%B4%AA%E9%83%91-%E6%9C%B1-ab6a55101"));
            get.setHttpMethod(HttpMethod.GET);
            HtmlPage page = webClient.getPage(get);
//            Thread.sleep(3000);
//            page.getElementByName("last").setAttribute("value",gender);
//            page.getElementByName("first").setAttribute("value",name);
//            HtmlPage search = page.getElementByName("search").click();
            Thread.sleep(3000);
            System.out.println(page.asXml());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
            new LingYingMessageController().getDetialMes("洪郑","朱");
    }
}
