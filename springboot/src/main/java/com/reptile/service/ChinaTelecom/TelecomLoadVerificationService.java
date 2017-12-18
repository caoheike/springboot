
package com.reptile.service.ChinaTelecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.springboot.Scheduler;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.WebClientFactory;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.*;

@Service
public class TelecomLoadVerificationService {
    private Logger logger = LoggerFactory.getLogger(TelecomLoadVerificationService.class);

    //查询号码信息 eg:{"phonesen":"1819455","provinceId":"29","provinceName":"青海","cityNo":"971","cityName":"西宁","areaCode":"971","netId":null,"cardType":null,"remark":null}
    public Map<String, Object> getProvince(String phoneNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<Object, Object> mapdata = new HashMap<Object, Object>();
        WebClient webClient = new WebClientFactory().getWebClient();
        try {
            WebRequest request1 = new WebRequest(new URL("http://login.189.cn/web/login/ajax"));
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new NameValuePair("m", "checkphone"));
            list.add(new NameValuePair("phone", phoneNumber));
            request1.setRequestParameters(list);
            request1.setHttpMethod(HttpMethod.POST);
            UnexpectedPage page = webClient.getPage(request1);
            String result = page.getWebResponse().getContentAsString();
            JSONObject jsonObject = JSONObject.fromObject(result);
            Iterator keys = jsonObject.keys();
            while (keys.hasNext()) {
                String results = keys.next().toString();
                if (jsonObject.get(results).toString() != "null") {
                    mapdata.put(results, jsonObject.get(results));
                }
            }
            map.put("errorCode", "0000");
            map.put("errorInfo", "操作成功");
            map.put("data", mapdata);
        } catch (Exception e) {
            logger.warn(e.getMessage() + "mrlu",e);
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作失败");
        }finally {
            if(webClient!=null){
                webClient.close();
            }
        }
        return map;
    }

    //判断是否需要验证码 eg {"rspType":"0","rspCode":"0000","desc":"图形验证码接口成功","captchaFlag":true}
    public Map<String, Object> judgeVecCode(String account, String provinceID) {
        Map<String, Object> map = new HashMap<String, Object>();
        WebClient webClient = new WebClientFactory().getWebClient();
        try {
            WebRequest request1 = new WebRequest(new URL("http://login.189.cn/web/login/ajax"));
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new NameValuePair("m", "captcha"));
            list.add(new NameValuePair("account", account));
            list.add(new NameValuePair("uType", "201"));
            list.add(new NameValuePair("ProvinceID", provinceID));
            list.add(new NameValuePair("areaCode", ""));
            list.add(new NameValuePair("cityNo", ""));
            request1.setRequestParameters(list);
            request1.setHttpMethod(HttpMethod.POST);
            UnexpectedPage page = webClient.getPage(request1);
            String result = page.getWebResponse().getContentAsString();

//            if (result.contains("true")) {
//                map.put("errorCode", "0001");
//                map.put("errorInfo", "密码错误3次，请休息一会再来！");
//                return map;
//            }
            JSONObject jsonObject = JSONObject.fromObject(result);
            map.put("errorCode", "0000");
            map.put("errorInfo", "操作成功");
            map.put("data", jsonObject);
        } catch (Exception e) {
            logger.warn(e.getMessage() + "mrlu",e);
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作失败");
        }finally {
            if(webClient!=null){
                webClient.close();
            }
        }
        return map;
    }

    public Map<String, String> loadGlobalDX(HttpServletRequest request, String userName, String servePwd) {
        Map<String, String> map = new HashMap<String, String>();
        HttpSession session = request.getSession();
        WebClient webClient = new WebClientFactory().getWebClient();
        try {
            HtmlPage page = webClient.getPage("http://login.189.cn/web/login");
            page.getElementById("txtAccount").focus();
            page.getElementById("txtAccount").setAttribute("value", userName);
            page.getElementById("txtAccount").blur();
            page.getElementById("txtPassword").setAttribute("value", servePwd);

            String realPath = request.getServletContext().getRealPath("/imageFile");
            File file = new File(realPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String fileName = "loadImageCode" + System.currentTimeMillis() + ".png";
            HtmlImage imgCaptcha = (HtmlImage) page.getElementById("imgCaptcha");
            BufferedImage read = imgCaptcha.getImageReader().read(0);
            ImageIO.write(read, "png", new File(file, fileName));
            Map<String, Object> imagev = MyCYDMDemo.Imagev(realPath + "/" + fileName);
            String code = imagev.get("strResult").toString();
            HtmlInput txtCaptcha = (HtmlInput) page.getElementById("txtCaptcha");
            txtCaptcha.setValueAttribute(code.toLowerCase());
            HtmlPage loginbtn = page.getElementById("loginbtn").click();

            Thread.sleep(2000);
            if (!loginbtn.asText().contains("详细查询") && !loginbtn.asText().contains("详单查询") && !loginbtn.asText().contains("账单查询")) {
                String divErr = loginbtn.getElementById("divErr").getTextContent();
                if (divErr.contains("验证码")) {
                    map.put("errorCode", "0008");
                    map.put("errorInfo", "服务器繁忙，请刷新后重试");
                } else {
                    map.put("errorCode", "0007");
                    map.put("errorInfo", "请前往官网修改初始密码！");
                }
                webClient.close();
            } else {
                map.put("errorCode", "0000");
                map.put("errorInfo", "登陆成功");
                session.setAttribute("GBmobile-webclient", webClient);
            }
        } catch (Exception e) {
            webClient.close();
            Scheduler.sendGet(Scheduler.getIp);
            logger.warn(e.getMessage() + "mrlu",e);
            map.put("errorCode", "0001");
            map.put("errorInfo", "网络连接异常!");
        }
        return map;
    }
}

