package com.reptile.service;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.springboot.Scheduler;
import com.reptile.util.JavaExcuteJs;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.WebClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全国电信统一登录（发包模式）
 *
 * @author mrlu 2018 1 19
 */
@Service
public class TelecomTestService {
    private static Logger logger = LoggerFactory.getLogger(TelecomTestService.class);

    /**
     * 全国电信登录
     * @param request
     * @param userName    用户手机号
     * @param serverPwd   服务密码
     * @param provinceId  所属省份id
     * @return
     * @throws Exception
     */
    public  Map<String, String> loginTelecom(HttpServletRequest request, String userName, String serverPwd, String provinceId){
        Map<String, String> map = new HashMap<String, String>(16);
        //创建webclient对象
        WebClient webClient = new WebClientFactory().getWebClient();
        try {
            //请求验证码连接
            BufferedImage read = getBufferImageCode(webClient);
            //打码平台对验证码进行打码
            String strResult = reconginzeImg(request, read);
            //判断是否登录成功
            HtmlPage loginbtn = loadIng(webClient, userName, provinceId, serverPwd, strResult);
            //判断是否登录成功
            map = isLogin(request, webClient, loginbtn);
        } catch (Exception e) {
            //更换代理ip
            Scheduler.sendGet(Scheduler.getIp);

            logger.warn("-----------手机号：" + userName + " 在登录全国电信统一接口时出现异--------------", e);

            map.put("errorCode", "0002");
            map.put("errorInfo", "系统繁忙!");
        } finally {
            closeWebClient(webClient);
        }
        return map;
    }

    /**
     * 传入bufferImage用打码平台进行打码  返回打码后的数值
     *
     * @param request
     * @param read
     * @return
     * @throws Exception
     */
    private static String reconginzeImg(HttpServletRequest request, BufferedImage read) throws Exception {
        String realPath = request.getServletContext().getRealPath("/imageFile");
        File file = new File(realPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = "loadImageCode" + System.currentTimeMillis() + ".png";
        ImageIO.write(read, "png", new File(file, fileName));
        Map<String, Object> imagev = MyCYDMDemo.Imagev(realPath + "/" + fileName);
        String strResult = imagev.get("strResult").toString();
        return strResult;
    }

    /**
     * 登录电信全国统一接口
     *
     * @param webClient
     * @param userName
     * @param provinceId
     * @param serverPwd
     * @param strResult
     * @return
     * @throws Exception
     */
    private static HtmlPage loadIng(WebClient webClient, String userName, String provinceId, String serverPwd, String strResult) throws Exception {
        //执行js对密码进行加密
        String passWord = JavaExcuteJs.excuteJs("static/js/telecomlogin.js", "valAesEncryptSet", serverPwd);

        WebRequest webPost = new WebRequest(new URL("http://login.189.cn/web/login"));

        List<NameValuePair> list = new ArrayList<>();
        list.add(new NameValuePair("Account", userName));
        list.add(new NameValuePair("UType", "201"));
        list.add(new NameValuePair("ProvinceID", provinceId));
        list.add(new NameValuePair("AreaCode", ""));
        list.add(new NameValuePair("CityNo", ""));
        list.add(new NameValuePair("RandomFlag", "0"));
        list.add(new NameValuePair("Password", passWord));
        list.add(new NameValuePair("Captcha", strResult));
        webPost.setRequestParameters(list);
        webPost.setHttpMethod(HttpMethod.POST);

        HtmlPage loginbtn = webClient.getPage(webPost);
        return loginbtn;
    }

    /**
     * 获取本次请求的验证码bufferimage
     *
     * @param webClient
     * @return
     * @throws IOException
     */
    private static BufferedImage getBufferImageCode(WebClient webClient) throws IOException {
        UnexpectedPage page = webClient.getPage("http://login.189.cn/web/captcha?undefined&source=login&width=100&height=37&" + Math.random());
        InputStream contentAsStream = page.getWebResponse().getContentAsStream();
        BufferedImage read = ImageIO.read(contentAsStream);
        return read;
    }

    /**
     * 判断本次电信登录是否成功
     *
     * @param request
     * @param webClient
     * @param loginbtn
     * @return
     */
    private static Map<String, String> isLogin(HttpServletRequest request, WebClient webClient, HtmlPage loginbtn) {
        Map<String, String> map = new HashMap<String, String>(16);
        String signleStr1 = "详细查询";
        String signleStr2 = "详单查询";
        String signleStr3 = "账单查询";
        if (!loginbtn.asText().contains(signleStr1) && !loginbtn.asText().contains(signleStr2) && !loginbtn.asText().contains(signleStr3)) {
            String divErr = loginbtn.getElementById("divErr").getTextContent();
            String validateCode = "验证码";
            if (divErr.contains(validateCode)) {
                map.put("errorCode", "0008");
                map.put("errorInfo", "服务器繁忙，请刷新后重试");
            } else {
                map.put("errorCode", "0007");
                map.put("errorInfo", divErr);
            }
            webClient.close();
        } else {
            HttpSession session = request.getSession();
            map.put("errorCode", "0000");
            map.put("errorInfo", "登陆成功");
            session.setAttribute("GBmobile-webclient", webClient);
        }
        return map;
    }

    /**
     * 关闭webclient
     * @param webClient
     */
    private static void closeWebClient(WebClient webClient) {
        if (webClient != null) {
            webClient.close();
        }
    }
}
