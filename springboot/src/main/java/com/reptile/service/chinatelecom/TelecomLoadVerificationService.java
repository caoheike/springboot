
package com.reptile.service.chinatelecom;

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
import org.apache.http.conn.HttpHostConnectException;
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

/**
 * 电信统一登录接口
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Service
public class TelecomLoadVerificationService {
    private Logger logger = LoggerFactory.getLogger(TelecomLoadVerificationService.class);

    /**
     * 查询号码信息 eg:{"phonesen":"1819455","provinceId":"29","provinceName":"青海","cityNo":"971","cityName":"西宁","areaCode":"971","netId":null,"cardType":null,"remark":null}
     * @param phoneNumber
     * @return
     */

    public Map<String, Object> getProvince(String phoneNumber) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Map<Object, Object> mapdata = new HashMap<Object, Object>(16);
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
        }catch (HttpHostConnectException e){
            logger.warn(e.getMessage() + "mrlu 网络连接异常",e);
            Scheduler.sendGet(Scheduler.getIp);
            map.put("errorCode", "0001");
            map.put("errorInfo", "网络错误，请重试！");
        }catch (Exception e) {
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

    /**
     * 判断是否需要验证码 eg {"rspType":"0","rspCode":"0000","desc":"图形验证码接口成功","captchaFlag":true}
     * @param account
     * @param provinceID
     * @return
     */
    public Map<String, Object> judgeVecCode(String account, String provinceID) {
        Map<String, Object> map = new HashMap<String, Object>(16);
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

            JSONObject jsonObject = JSONObject.fromObject(result);
            map.put("errorCode", "0000");
            map.put("errorInfo", "操作成功");
            map.put("data", jsonObject);
        }catch (HttpHostConnectException e){
            logger.warn(e.getMessage() + "mrlu 网络连接异常",e);
            Scheduler.sendGet(Scheduler.getIp);
            map.put("errorCode", "0001");
            map.put("errorInfo", "网络错误，请重试！");
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
        Map<String, String> map = new HashMap<String, String>(16);
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
            String signleStr1="详细查询";
            String signleStr2="详单查询";
            String signleStr3="账单查询";
            if (!loginbtn.asText().contains(signleStr1) && !loginbtn.asText().contains(signleStr2) && !loginbtn.asText().contains(signleStr3)) {
                String divErr = loginbtn.getElementById("divErr").getTextContent();
                String  validateCode="验证码";
                if (divErr.contains(validateCode)) {
                    map.put("errorCode", "0008");
                    map.put("errorInfo", "服务器繁忙，请刷新后重试");
                } else {
                    map.put("errorCode", "0007");
                    map.put("errorInfo", divErr);
                }
                webClient.close();
            } else {
                map.put("errorCode", "0000");
                map.put("errorInfo", "登陆成功");
                session.setAttribute("GBmobile-webclient", webClient);
            }
        }catch (HttpHostConnectException e){
            logger.warn(e.getMessage() + "mrlu 网络连接异常",e);
            Scheduler.sendGet(Scheduler.getIp);
            map.put("errorCode", "0001");
            map.put("errorInfo", "网络错误，请重试！");
        } catch (Exception e) {
            Scheduler.sendGet(Scheduler.getIp);
            logger.warn(e.getMessage() + "mrlu",e);
            map.put("errorCode", "0002");
            map.put("errorInfo", "系统繁忙!");
        }finally {
            if(webClient!=null){
                webClient.close();
            }
        }
        return map;
    }
}

