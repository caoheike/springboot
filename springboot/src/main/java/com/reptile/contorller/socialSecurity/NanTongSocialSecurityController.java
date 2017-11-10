package com.reptile.contorller.socialSecurity;

import com.reptile.service.socialSecurity.NanTongSocialSecurityService;

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
@RequestMapping("NanTongSocialSecurityController")
public class NanTongSocialSecurityController {

    @Autowired
    private NanTongSocialSecurityService service;

    @RequestMapping(value = "getDetailMes",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取社保详情",notes = "参数：身份证号,社保卡号，密码")
    public Map<String,Object> getDetailMes(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("userName")String  userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("socialCard")String socialCard) {
        return service.getDetailMes(request,idCard.trim(),socialCard.trim(),passWord.trim(), cityCode.trim());
    }

}
