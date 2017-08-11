package com.reptile.contorller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.model.FormBean;
import com.reptile.service.SocialSecurityService;

import javax.annotation.Resource;

import java.util.Map;

/**
 * Created by HotWong on 2017/4/27 0027.
 */
@Controller
@RequestMapping("socialSecurity")
public class SocialSecurityController {

    @Resource
    private SocialSecurityService socialSecurityService;

    @ResponseBody
    @RequestMapping(value = "/Login.html",method = RequestMethod.POST)
    public Map<String,Object> Login(FormBean bean){
        return socialSecurityService.login(bean);
    }

}
