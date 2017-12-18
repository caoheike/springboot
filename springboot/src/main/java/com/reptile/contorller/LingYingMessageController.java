package com.reptile.contorller;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.WebClientFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
            HtmlPage page1 = webClient.getPage("https://www.linkedin.com/?trk=brandpage_baidu_pc-mainlink");
            System.out.println(page1.asText());
//            webClient.addRequestHeader(":authority","www.linkedin.com");
//            webClient.addRequestHeader(":method","GET");
//            webClient.addRequestHeader(":path","/pub/dir/%E6%B4%AA%E9%83%91/%E6%9C%B1?trk=uno-reg-guest-home-name-search");
//            webClient.addRequestHeader(":scheme","https");
//            webClient.addRequestHeader("referer","https://www.linkedin.com/");
//            webClient.addRequestHeader("upgrade-insecure-requests","1");
//            webClient.addRequestHeader("user-agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
            WebRequest get=new WebRequest(new URL("https://www.linkedin.com/pub/dir/%E6%B4%AA%E9%83%91/%E6%9C%B1?trk=uno-reg-guest-home-name-search"));
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
