package com.reptile.contorller.chinatelecom;

import com.reptile.service.chinatelecom.TelecomLoadVerificationService;
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
 * 电信登录通用类
 *  主要进行登录前  手机号归属地，手机号是否可登录状态的查询
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Controller
@RequestMapping("TelecomLoadVerification")
public class TelecomLoadVerificationController {

    @Autowired
    private TelecomLoadVerificationService service;

    @ApiOperation(value = "0.1获取手机号码信息", notes = "参数：手机号")
    @ResponseBody
    @RequestMapping(value = "getProvince", method = RequestMethod.POST)
    public  Map<String, Object> getProvince(@RequestParam("phoneNumber")String phoneNumber) {
        return service.getProvince(phoneNumber);
    }

    @ApiOperation(value = "0.2判断是否需要图片验证码", notes = "参数：手机号,省份ID")
    @ResponseBody
    @RequestMapping(value = "judgeVecCode", method = RequestMethod.POST)
    public Map<String, Object> judgeVecCode(@RequestParam("account")String account, @RequestParam("provinceID")String provinceID) {
        return service.judgeVecCode(account, provinceID);
    }

    @ApiOperation(value = "1.登录全国电信网上营业厅", notes = "参数：手机号，服务密码")
    @ResponseBody
    @RequestMapping(value = "loadGlobalDX", method = RequestMethod.POST)
    public Map<String, String> loadGlobalDX(HttpServletRequest request, @RequestParam("userName") String userName,
                                           @RequestParam("servePwd") String servePwd) {
        return service.loadGlobalDX(request, userName, servePwd);
    }
}
