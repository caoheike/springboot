package com.reptile.contorller.accumulationfund;


import com.reptile.model.FormBean;
import com.reptile.service.accumulationfund.AccumulationFundService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 成都电信
 *
 * @author HotWong
 * @date 2016/10/31
 */
@Controller
@RequestMapping("accumulationFund")
public class AccumulationFundController {

    @Resource
    private AccumulationFundService accumulationFundService;

    @ResponseBody
    @RequestMapping(value = "/Login.html",method = RequestMethod.POST)
    public Map<String,Object> login( HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("idCardNum")String idCardNum,@RequestParam("userName")String userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("fundCard")String fundCard){
    	FormBean bean=new FormBean();
    	bean.setCityCode(cityCode);
    	bean.setUserId(idCard);
    	bean.setUserName(userName);
    	bean.setUserPass(passWord);
    	bean.setVerifyCode(catpy);
        return accumulationFundService.login(bean,request,idCardNum);
    }

    @ResponseBody
    @RequestMapping(value = "/getVerifyImage.jpg",method = RequestMethod.POST)
    public Map<String,Object> getVerifyImage(HttpServletResponse response, HttpServletRequest request){
        return accumulationFundService.getVerifyImage(response,request);
    }

}
