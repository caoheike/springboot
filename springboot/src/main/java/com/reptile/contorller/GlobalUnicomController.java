package com.reptile.contorller;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.reptile.model.UnicomBean;
import com.reptile.service.MobileService;
import com.reptile.util.CustomAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Map;


//全国联通
@Controller
@RequestMapping("GlobalUnicomController")
public class GlobalUnicomController {
    @Autowired
    private com.reptile.util.application application;
    @Resource
    private MobileService mobileService;

    /**
     * 中国联通 获取验证码
     *
     * @param request
     * @param response
     * @param unicombean
     * @return
     * @throws FailingHttpStatusCodeException
     * @throws MalformedURLException
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "UnicomGetCode",method = RequestMethod.POST)
    public Map<String, Object> UnicomGetCode(HttpServletRequest request,
                                             HttpServletResponse response, UnicomBean unicombean)
            throws FailingHttpStatusCodeException, MalformedURLException,
            IOException {
        return mobileService.GetCode(request, response, unicombean);
    }

    /**
     * 联通登录接口
     *
     * @param request
     * @param response
     * @param
     * @return
     * @throws FailingHttpStatusCodeException
     * @throws MalformedURLException
     * @throws IOException
     */
    @CustomAnnotation
    @ResponseBody
    @RequestMapping(value = "UnicomLogin",method = RequestMethod.POST)
    public Map<String, Object> UnicomLogin(HttpServletRequest request,
                                           @RequestParam("Useriphone") String Useriphone,
                                           @RequestParam("UserPassword") String UserPassword,
                                           @RequestParam("UserCode") String UserCode)throws Exception {
        UnicomBean unicombean = new UnicomBean();
        unicombean.setUseriphone(Useriphone);
        unicombean.setUserPassword(UserPassword);
        unicombean.setUserCode(UserCode);
        System.out.println("已经被访问了");
        return mobileService.UnicomLogin(request,unicombean);
    }

}
