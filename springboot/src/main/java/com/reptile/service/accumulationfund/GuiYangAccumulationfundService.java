package com.reptile.service.accumulationfund;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GuiYangAccumulationfundService {
    private Logger logger = LoggerFactory.getLogger(GuiYangAccumulationfundService.class);

    public Map<String, Object> loadImageCode(HttpServletRequest request) {
        logger.warn("获取贵阳公积金图片验证码");
        Map<String, Object> map = new HashMap<>();
        Map<String, String> datamap = new HashMap<>();
        String path = request.getServletContext().getRealPath("ImageCode");
        HttpSession session = request.getSession();

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        WebClient webClient = new WebClientFactory().getWebClient();

        HtmlPage page = null;
        try {
            page = webClient.getPage("http://zxcx.gygjj.gov.cn/");
            HtmlImage rand = (HtmlImage) page.getElementById("rand");
            BufferedImage read = rand.getImageReader().read(0);
            String fileName = "guiyang" + System.currentTimeMillis() + ".png";
            ImageIO.write(read, "png", new File(file, fileName));
            datamap.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/ImageCode/" + fileName);
            map.put("errorCode", "0000");
            map.put("errorInfo", "加载验证码成功");
            map.put("data", datamap);
            session.setAttribute("htmlWebClient-guiyang", webClient);
            session.setAttribute("htmlPage-guiyang", page);
        } catch (IOException e) {
            logger.warn("贵阳住房公积金 ", e);
            map.put("errorCode", "0001");
            map.put("errorInfo", "当前网络繁忙，请刷新后重试");
            e.printStackTrace();

        }
        return map;
    }

    public Map<String, Object> getDeatilMes(HttpServletRequest request, String userCard, String password, String imageCode,String cityCode) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        HttpSession session = request.getSession();
        Object htmlWebClient = session.getAttribute("htmlWebClient-guiyang");
        Object htmlPage = session.getAttribute("htmlPage-guiyang");

        if (htmlWebClient != null && htmlPage != null) {
            HtmlPage page = (HtmlPage) htmlPage;
            WebClient webClient = (WebClient) htmlWebClient;
            List<String> alert=new ArrayList<>();
            CollectingAlertHandler alertHandler=new CollectingAlertHandler(alert);
            webClient.setAlertHandler(alertHandler);
            try {
                //以发包形式登录，但是判断是否登录成功比较麻烦，所以采用模拟点击登录
//                String url = "http://zxcx.gygjj.gov.cn/checklogin.do?method=login";
//                WebRequest webRequest = new WebRequest(new URL(url));
//                webRequest.setHttpMethod(HttpMethod.POST);
//                List<NameValuePair> list = new ArrayList<NameValuePair>();
//                list.add(new NameValuePair("aaxmlrequest", "true"));
//                list.add(new NameValuePair("logintype", "person"));
//                list.add(new NameValuePair("spcode", ""));
//                list.add(new NameValuePair("fromtype", "null"));
//                list.add(new NameValuePair("IsCheckVerifyCode", "on"));
//                list.add(new NameValuePair("IdCard", userCard));
//                list.add(new NameValuePair("PassWord", password));
//                list.add(new NameValuePair("Ed_Confirmation", imageCode));
//                webRequest.setRequestParameters(list);
//                XmlPage logon = webClient.getPage(webRequest);//登录贵阳住房公积金网站
//                Thread.sleep(5000);
                page.getElementById("IdCard").setAttribute("value",userCard);
                page.getElementById("PassWord").setAttribute("value",password);
                page.getElementById("Ed_Confirmation").setAttribute("value",imageCode);
                HtmlPage logon = page.getElementById("logon").click();
                Thread.sleep(5000);
                System.out.println(alert.size());
                logger.warn("登录贵阳住房公积金:"+alert.size());
                if(alert.size()>0){
                    map.put("errorCode", "0005");
                    map.put("errorInfo", alert.get(0));
                    return map;
                }
                logger.warn("贵阳住房公积金获取基本缴纳信息");
                WebRequest getMethod=new WebRequest(new URL("http://zxcx.gygjj.gov.cn/PersonBaseInfo.do?method=view"));
                getMethod.setHttpMethod(HttpMethod.GET);
                getMethod.setAdditionalHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
                HtmlPage page1 = webClient.getPage(getMethod);
                Thread.sleep(3000);
                System.out.println(page1.asXml());
                if(page1.asXml().contains("此网页使用了框架，但您的浏览器不支持框架")){
                    map.put("errorCode", "0002");
                    map.put("errorInfo", "认证失败，请重新认证");
                    return map;
                }
                dataMap.put("base", page1.asXml()); //存入基本信息
                logger.warn("贵阳住房公积金缴纳信息详情获取");
                List<String> detailMes = new ArrayList<>();
                WebRequest post = new WebRequest(new URL("http://zxcx.gygjj.gov.cn/PersonAccountsList.do?method=list"));
                post.setHttpMethod(HttpMethod.POST);
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String endTime = format.format(calendar.getTime());
                List<NameValuePair>  list = new ArrayList<NameValuePair>();
                list.add(new NameValuePair("aaxmlrequest", "true"));
                list.add(new NameValuePair("startTime_a", "2001-01-01"));
                list.add(new NameValuePair("startTime_b", endTime));
                post.setRequestParameters(list);

                XmlPage page2 = webClient.getPage(post);
                Thread.sleep(1000);
                detailMes.add(page2.asText());
                System.out.println(page2.asText());

                org.jsoup.nodes.Document xmlDocument = Jsoup.parse(page2.asText());
                String sort = xmlDocument.getElementById("sort").val();
                String dir = xmlDocument.getElementById("dir").val();
                String stateDir = xmlDocument.getElementById("stateDir").val();
                String curye = xmlDocument.getElementById("curye").val();

                String[] counts = page2.asText().split("条记录&nbsp;&nbsp;共<b>");
                int count = Integer.parseInt(counts[1].substring(0, 1));
                for (int i = 2; i <= count; i++) {
                    post = new WebRequest(new URL("http://zxcx.gygjj.gov.cn/PersonAccountsList.do?method=list"));
                    post.setHttpMethod(HttpMethod.POST);

                    list = new ArrayList<NameValuePair>();
                    list.add(new NameValuePair("aaxmlrequest", "true"));
                    list.add(new NameValuePair("currentPage", i + ""));
                    list.add(new NameValuePair("sort", sort));
                    list.add(new NameValuePair("dir", dir));
                    list.add(new NameValuePair("stateDir", stateDir));
                    list.add(new NameValuePair("startTime_a", "2001-01-01"));
                    list.add(new NameValuePair("startTime_b", endTime));
                    list.add(new NameValuePair("curye", curye));
                    post.setRequestParameters(list);
                    page2 = webClient.getPage(post);
                    Thread.sleep(1000);
                    System.out.println(page2.asText());
                    detailMes.add(page2.asText());
                    xmlDocument = Jsoup.parse(page2.asText());
                    sort = xmlDocument.getElementById("sort").val();
                    dir = xmlDocument.getElementById("dir").val();
                    stateDir = xmlDocument.getElementById("stateDir").val();
                    curye = xmlDocument.getElementById("curye").val();
                }
                logger.warn("贵阳住房公积金缴纳信息详情获取完成");
                dataMap.put("item", detailMes);
                map.put("data", dataMap);
                map.put("userId", userCard);
                map.put("city", cityCode);
                map = new Resttemplate().SendMessage(map, "http://192.168.3.16:8089/HSDC/person/accumulationFund");
            } catch (Exception e) {
                logger.warn("贵阳住房公积金获取失败",e);
                e.printStackTrace();
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
            }
        } else {
            logger.warn("贵阳住房公积金登录过程中出错 ");
            map.put("errorCode", "0001");
            map.put("errorInfo", "非法操作！");
        }
        return map;
    }
}
