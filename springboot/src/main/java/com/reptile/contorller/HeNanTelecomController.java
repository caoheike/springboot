package com.reptile.contorller;

import com.reptile.service.HeNanTelecomService;
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

@Controller
@RequestMapping("HeNanTelecomController")
public class HeNanTelecomController {
    @Autowired
    private HeNanTelecomService service;
    @ApiOperation(value = "1.发送短信验证码", notes = "参数：手机号")
    @ResponseBody
    @RequestMapping(value = "sendPhoneCode", method = RequestMethod.POST)
    public  Map<String,Object> sendPhoneCode(HttpServletRequest request, @RequestParam("phoneNumber") String phoneNumber){
        return service.sendPhoneCode(request,phoneNumber);
    }

    @CustomAnnotation
    @ApiOperation(value = "2.获取账单信息", notes = "参数：")
    @ResponseBody
    @RequestMapping(value = "getDetailMes", method = RequestMethod.POST)
    public Map<String, Object> getDetailMes(HttpServletRequest request, @RequestParam("phoneNumber")String phoneNumber,
                                            @RequestParam("serverPwd")String serverPwd,@RequestParam("phoneCode")String phoneCode) {
        return service.getDetailMes(request,phoneNumber,serverPwd,phoneCode);
    }

}
