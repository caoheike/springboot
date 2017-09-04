package com.reptile.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.ConstantInterface;
import com.reptile.util.Resttemplate;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SEOandCHSIService {
    //https://xyk.cebbank.com/mycard/bill/havingprintbill-query.htm
    private final static String CabCardloginPhone = "https://xyk.cebbank.com/mall/api/usercommon/dynamic";//发送短信密码
    private final static String CabCardIndexpage = "https://xyk.cebbank.com/mycard/bill/havingprintbill-query.htm";//光大银行信用卡个人中心
    private final static String CabCardloginImage = "https://xyk.cebbank.com/mall/api/captcha";//光大银行信用卡登录页面图片验证码
    private final static String CabCardloginUrl = "https://xyk.cebbank.com/mall/login";//光大银行信用卡登录页面地址
    private final static String CabCardendnUrl = "https://xyk.cebbank.com/mall/api/user/login?t=1503663657867";//登录提交页面
    private final static String verifyCodeImageUrl = "http://query.xazfgjj.gov.cn/system/resource/creategjjcheckimg.jsp?randomid=" + System.currentTimeMillis();
    //根据企业邮箱查询企业信息
    public Map<String, Object> SeoEmailFind(HttpServletRequest request, String UserEmail, String UserCard) {
        System.out.println("email=============" + UserEmail);
        Map<String, Object> map = new HashMap<String, Object>();
        if (UserEmail == null || UserEmail == "") {
            map.put("ResultInfo", "企业邮箱信息为不正确，请确认后重写填写");
            map.put("ResultCode", "0002");
        } else {
            try {
                String URL = "www" + "." + UserEmail.substring(UserEmail.indexOf("@") + 1);
                System.out.println("URL====" + URL);
                WebClient webclient = new WebClient();
                webclient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
                webclient.getOptions().setTimeout(90000);
                webclient.getOptions().setCssEnabled(false);
                webclient.getOptions().setJavaScriptEnabled(false);
                webclient.setJavaScriptTimeout(40000);
                webclient.getOptions().setRedirectEnabled(true);
                webclient.getOptions().setThrowExceptionOnScriptError(false);
                webclient.getOptions().setThrowExceptionOnFailingStatusCode(false);
                webclient.setAjaxController(new NicelyResynchronizingAjaxController());
                webclient.getOptions().setCssEnabled(false);
                HtmlPage page = webclient.getPage("http://seo.chinaz.com/" + URL);
                HtmlDivision division = (HtmlDivision) page.querySelectorAll(".SeoMaWr01Right").get(4);
                System.out.println(division.asText().trim());
                Map<String, Object> seo = new HashMap<String, Object>();
                seo.put("cardNumber", UserCard);
                seo.put("email", UserEmail);
                seo.put("data", division.asText().trim());
                Resttemplate resttemplate = new Resttemplate();
                map = resttemplate.SendMessage(seo, ConstantInterface.port + "/HSDC/authcode/companyEmail");

            } catch (Exception e) {
                e.printStackTrace();
                map.clear();
                map.put("errorInfo", "企业邮箱信息为不正确，请确认后重写填写!");
                map.put("errorCode", "0002");
            }
        }
        System.out.println(map);
        return map;
    }

    public Map<String, Object> CabCardloginImage(HttpServletRequest request) throws Exception {
        WebClient webclient = new WebClient();
        webclient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
        webclient.getOptions().setTimeout(90000);
        webclient.getOptions().setCssEnabled(false);
        webclient.getOptions().setJavaScriptEnabled(true);
        webclient.setJavaScriptTimeout(40000);
        webclient.getOptions().setRedirectEnabled(true);
        webclient.getOptions().setThrowExceptionOnScriptError(true);
        webclient.getOptions().setThrowExceptionOnFailingStatusCode(true);
        webclient.setAjaxController(new NicelyResynchronizingAjaxController());
        webclient.getOptions().setCssEnabled(true);
        //获取验证码
        HttpSession session = request.getSession();
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            UnexpectedPage Imagepage = webclient.getPage(CabCardloginImage);
            //	UnexpectedPage loginPage=  webclient.getPage(CabCardloginUrl);
            BufferedImage bufferedImage = ImageIO.read(Imagepage.getInputStream());
            Date datas = new Date();
            String time = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(datas);
            String ImageName = "CebloginImger" + time;

            String path = request.getServletContext().getRealPath("/CebloginImger");
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }

            String findImage = "gd" + System.currentTimeMillis() + ".png";
            ImageIO.write(bufferedImage, "png", new File(file, findImage));
            session.setAttribute("sessionWebClient-Cab", webclient);
            //  session.setAttribute("sessionLoginPage-Cab", loginPage);
            data.put("ImagerUrl", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/CebloginImger/" + findImage);
            data.put("ResultInfo", "查询成功");
            data.put("ResultCode", "0000");
            map.put("errorInfo", "查询成功");
            map.put("errorCode", "0000");
        } catch (Exception e) {
            e.printStackTrace();
            map.clear();
            data.put("ResultInfo", "请重新刷新图片！");
            data.put("ResultCode", "0002");
            map.put("errorInfo", "请重新刷新图片");
            map.put("errorCode", "0002");
        }
        map.put("data", data);
        return map;
    }

    public Map<String, Object> CabCardloginPass(HttpServletRequest request, String UserCard, String loginImage) throws Exception {
        HttpSession session = request.getSession();//获得session
        Object sessionWebClient = session.getAttribute("sessionWebClient-Cab");//存在session 中的浏览器
        Map<String, Object> map = new HashMap<String, Object>();
        if (sessionWebClient != null) {
            final WebClient webclient = (WebClient) sessionWebClient;
            Object sessionLoginPage = webclient.getPage(CabCardloginUrl);
            ;//存在session 中的登录页面
            final HtmlPage loginPage = (HtmlPage) sessionLoginPage;
            //设置请求头
            webclient.addRequestHeader("Host", "xyk.cebbank.com");
            webclient.addRequestHeader("cebbank", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
            webclient.addRequestHeader("Accept", "*/*");
            webclient.addRequestHeader("Accept-Language", "gzip, deflate, br");
            webclient.addRequestHeader("Accept-Encoding", "gzip, deflate, br");
            webclient.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            webclient.addRequestHeader("mtoken", "null");
//		webclient.addRequestHeader("X-Requested-With", "XMLHttpRequest");
            webclient.addRequestHeader("Referer", "https://xyk.cebbank.com/mall/login?target=/mycard/bill/info.htm");
//		webclient.addRequestHeader("Content-Length", "33");
//		webclient.addRequestHeader("Cookie", "__v=1.2463984060387318000.1503641057.1503652413.1503658883.5; ALLYESID4=0E62F3648AE10F47; MALL=4007a8c0; __l=78209354; fsid=6bb1ca8dZ13327711Z15e1859d3c8Za141");
            webclient.addRequestHeader("Connection", "keep-alive");
            webclient.addRequestHeader("Origin", "https://xyk.cebbank.com");
            webclient.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
            webclient.addRequestHeader("X-Requested-With", "XMLHttpRequest");//设置格式，否则无法返回JSON
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            //设置参数
            list.add(new NameValuePair("name", UserCard));
            list.add(new NameValuePair("code", loginImage));
            System.out.println(UserCard + loginImage);
            try {
                WebRequest request1 = new WebRequest(new URL("https://xyk.cebbank.com/mall/api/usercommon/dynamic"));
                request1.setHttpMethod(HttpMethod.POST);//提交方式
                request1.setRequestParameters(list);
                UnexpectedPage pagev = webclient.getPage(request1);
                System.out.println(pagev);
                Thread.sleep(1000);
                System.out.println(pagev.getWebResponse().getContentAsString());
                map.put("errorInfo", "动态密码发送成功");
                map.put("errorCode", "0000");
                session.setAttribute("sessionWebClient-Cab", webclient);
                session.setAttribute("sessionLoginPage-Cab", loginPage);
            } catch (Exception e) {
                e.printStackTrace();
                map.clear();
                map.put("errorInfo", "填写信息有误");
                map.put("errorCode", "0002");
            }
        } else {
            map.put("errorInfo", "服务器响应有误，重新尝试");
            map.put("errorCode", "0002");
        }
        return map;
    }

    public Map<String, Object> CabCardloginPage(HttpServletRequest request, String UserCard, String loginImage, String Password) throws Exception {
        HttpSession session = request.getSession();//获得session
        Object sessionWebClient = session.getAttribute("sessionWebClient-Cab");//存在session 中的浏览器
        Object sessionLoginPage = session.getAttribute("sessionLoginPage-Cab");//存在session 中的登录页面
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        if (sessionWebClient != null && sessionLoginPage != null) {
            final HtmlPage loginPage = (HtmlPage) sessionLoginPage;
            final WebClient webclient = (WebClient) sessionWebClient;
            try {
                WebRequest requests = new WebRequest(new URL(CabCardendnUrl));
                System.out.println("--------------进入第三个方法----------------");
                Thread.sleep(2000);
                List<NameValuePair> lists = new ArrayList<NameValuePair>();

                lists.add(new NameValuePair("userName", UserCard));//身份证号
                lists.add(new NameValuePair("code", loginImage));//图形验证码
                lists.add(new NameValuePair("password", Password));//动态密码
                lists.add(new NameValuePair("target", ""));
                requests.setHttpMethod(HttpMethod.POST);
                requests.setRequestParameters(lists);
                UnexpectedPage page = webclient.getPage(requests);
                JSONObject jsonObject = JSONObject.fromObject(page.getWebResponse().getContentAsString());
                System.out.println("进入已出账单");
                HtmlPage ccenterPage = webclient.getPage(CabCardIndexpage);
                HtmlTable table = (HtmlTable) ccenterPage.getByXPath("//table[@class='tab_one']").get(0);
                String str = table.asXml().toString();
                Document doc = Jsoup.parse(str);
                Elements trs = doc.select("table").select("tr");
                List<String> html = new ArrayList<String>();
                for (int i = 1; i < trs.size(); i++) {
                    Elements tds = trs.get(i).select("td");
                    for (int j = 0; j < 1; j++) {
                        String text = tds.get(0).text();
                        text = text.replace("/", "");
                        System.out.println(text);
                        System.out.println("------------");
                        HtmlPage detailedpage = webclient.getPage("https://xyk.cebbank.com/mycard/bill/billquerydetail.htm?statementDate=" + text);//账单详情页面
                        //data.put("html"+i, detailedpage);
                        html.add(detailedpage.asXml());
                    }
                }
                Map<String, Object> seo = new HashMap<String, Object>();
                System.out.println("页面已经放置到html中");
                data.put("html", html);
                seo.put("idcard", UserCard);
                seo.put("data", data);
                seo.put("backtype", "CEB");
                Resttemplate resttemplate = new Resttemplate();
                map = resttemplate.SendMessage(seo, ConstantInterface.port + "HSDC/BillFlow/BillFlowByreditCard");
                map.put("errorInfo", "账单获取完成");
                map.put("errorCode", "0000");
            } catch (Exception e) {
                e.printStackTrace();
                map.clear();
                map.put("errorInfo", "获取账单失败");
                map.put("errorCode", "0002");
            }
        } else {
            map.put("errorInfo", "服务器响应有误，重新尝试");
            map.put("errorCode", "0002");
        }
        return map;
    }

}
