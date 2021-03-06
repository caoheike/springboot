package com.reptile.contorller;


import com.reptile.util.CustomAnnotation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.model.FormBean;
import com.reptile.service.CreditService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 征信
 *
 * @author HotWong
 * @date 2016/10/31
 */
@Controller
@RequestMapping("credit")
public class CreditController {

    @Resource
    private CreditService creditService;

    @ResponseBody
    @RequestMapping(value = "/preReg.html",method = RequestMethod.POST)
    public Map<String,Object> preReg(FormBean bean,HttpServletRequest request){
        return creditService.preReg(bean,request);
    }

    @ResponseBody
    @RequestMapping(value = "/reg.html",method = RequestMethod.POST)
    public Map<String,Object> reg(FormBean bean,HttpServletRequest request){
        return creditService.reg(bean,request);
    }

    @ResponseBody
    @RequestMapping(value = "/sendRegSms.html",method = RequestMethod.POST)
    public Map<String,Object> sendRegSms(String phone,HttpServletRequest request){
        return creditService.sendRegSms(phone,request);
    }

    @ResponseBody
    @RequestMapping(value = "/Login.html",method = RequestMethod.POST)
    public Map<String,Object> login(FormBean bean,HttpServletRequest request){
        return creditService.login(bean,request);
    }

//    @ResponseBody
//    @RequestMapping(value = "/Login.html",method = RequestMethod.POST)
//    public Map<String,Object> Login(HttpServletRequest request,@RequestParam("userName")String userName,@RequestParam("userPass")String userPass,@RequestParam("verifyCode")String verifyCode){
//        FormBean bean=new FormBean();
//        bean.setUserName(userName);
//        bean.setUserPass(userPass);
//        bean.setVerifyCode(verifyCode);
//        return creditService.login(bean,request);
//    }

    @ResponseBody
    @RequestMapping(value = "/getVerifyImage.jpg",method = RequestMethod.POST)
    public Map<String,Object> getVerifyImage(@RequestParam(value = "type",required = false) String type, HttpServletRequest request){
        return creditService.getVerifyImage(type,request);
    }

    @ResponseBody
    @RequestMapping(value = "/question.html",method = RequestMethod.POST)
    public Map<String,Object> question(@RequestParam(value = "options",required = false) String options,@RequestParam(value = "userId",required = false) String userId, HttpServletRequest request){
        return creditService.question(options,userId,request);
    }

    @ResponseBody
    @RequestMapping(value = "/sendSms.html",method = RequestMethod.POST)
    public Map<String,Object> sendSms(HttpServletRequest request){
        return creditService.sendSms(request);
    }

    @ResponseBody
    @RequestMapping(value = "/subSms.html",method = RequestMethod.POST)
    public Map<String,Object> subSms(String sms,HttpServletRequest request){
        return creditService.subSms(sms,request);
    }

    @ResponseBody
    @CustomAnnotation
    @RequestMapping(value = "/queryCredit.html",method = RequestMethod.POST)
    public Map<String,Object> queryCredit(HttpServletRequest request,@RequestParam("userId")String userId,@RequestParam("verifyCode")String verifyCode,@RequestParam("UUID")String uuid){
        return creditService.queryCredit(request,userId,verifyCode,uuid);
    }

}
