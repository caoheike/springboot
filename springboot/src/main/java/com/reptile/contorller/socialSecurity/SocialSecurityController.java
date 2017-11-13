package com.reptile.contorller.socialSecurity;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.model.FormBean;
import com.reptile.service.socialSecurity.SocialSecurityService;

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
    public Map<String,Object> Login(@RequestParam("idCard")String idCard,@RequestParam("userName")String userName,@RequestParam("passWord")String passWord,@RequestParam("cityCode")String cityCode){
    	FormBean bean=new FormBean();
    	bean.setCityCode(cityCode);
    	bean.setUserId(idCard);
    	bean.setUserName(userName);
    	bean.setUserPass(passWord);

        return socialSecurityService.login(bean);
    }

}
