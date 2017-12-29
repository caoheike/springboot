package com.reptile.contorller.chinatelecom;

import com.reptile.service.ChinaTelecom.YunNanTelecomService;
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
 * 云南电信
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Controller
@RequestMapping("YunNanTelecomController")
public class YunNanTelecomController {

    @Autowired
    private YunNanTelecomService service;

    @ApiOperation(value = "", notes = "参数：无")
    @RequestMapping(value = "sendPhoneCode", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> sendPhoneCode(HttpServletRequest request, @RequestParam("phoneNumber") String phoneNumber) {
        return service.sendPhoneCode(request, phoneNumber);
    }

    @ApiOperation(value = "", notes = "参数：无")
    @RequestMapping(value = "getDetailMes", method = RequestMethod.POST)
    @CustomAnnotation
    @ResponseBody
    public Map<String, Object> getDetailMes(HttpServletRequest request, @RequestParam("phoneNumber") String phoneNumber, @RequestParam("serverPwd") String serverPwd,
                                            @RequestParam("phoneCode") String phoneCode, @RequestParam("userName") String userName,
                                            @RequestParam("userCard")String userCard,@RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude,@RequestParam("UUID")String uuid) {
        return service.getDetailMes(request, phoneNumber, serverPwd, phoneCode, userName,userCard,longitude,latitude,uuid);
    }
}
