package com.reptile.contorller;

import com.reptile.service.ZXBankService;
import com.reptile.util.CustomAnnotation;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 信用卡账户信息
 * <p>
 * Created by Administrator on 2017/8/26 0026.
 */
@Controller
@RequestMapping("bankController")
public class ZXBankController {

    @Autowired
    private ZXBankService service;

    @ApiOperation(value = "1.中信银行图片验证码", notes = "参数：无")
    @ResponseBody
    @RequestMapping(value = "getZXImageCode", method = RequestMethod.POST)
    public Map<String,Object> getZXImageCode(HttpServletRequest request){

        return service.getZXImageCode(request);
    }

    @ApiOperation(value = "2.登录中信", notes = "参数：手机号，登录密码，图片验证码")
    @ResponseBody
    @RequestMapping(value = "loadZX", method = RequestMethod.POST)
    public Map<String, String> loadZX(HttpServletRequest request, @RequestParam("userNumber")String userNumber, @RequestParam("passWord") String passWord, @RequestParam("imageCode") String imageCode) throws Exception{

        return service.loadZX(request,userNumber.trim(),passWord.trim(),imageCode.trim());
    }

    @ApiOperation(value = "3.发送手机验证码", notes = "参数：无")
    @ResponseBody
    @RequestMapping(value = "sendPhoneCode", method = RequestMethod.POST)
    public Map<String, String> sendPhoneCode(HttpServletRequest request) throws Exception {
        return service.sendPhoneCode(request);
    }

    @CustomAnnotation
    @ApiOperation(value = "4.获取账单信息", notes = "参数：手机验证码")
    @ResponseBody
    @RequestMapping(value = "getDetailMes", method = RequestMethod.POST)
    public Map<String, Object> getDetailMes(HttpServletRequest request,@RequestParam("userCard") String userCard, @RequestParam("phoneCode") String phoneCode) throws  Exception {
        return service.getDetailMes(request,userCard.trim(),phoneCode.trim());
    }

}
