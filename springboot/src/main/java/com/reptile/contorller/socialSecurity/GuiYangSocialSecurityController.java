package com.reptile.contorller.socialSecurity;


import com.reptile.service.socialSecurity.GuiYangSocialSecurityService;

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
@RequestMapping("GuiYangSocialSecurityController")
public class GuiYangSocialSecurityController {

    @Autowired
    private GuiYangSocialSecurityService service;

    @ApiOperation(value = "贵阳社保",notes = "参数，身份证，密码，城市编号")
    @RequestMapping(value = "getDetailMes",method = RequestMethod.POST)
    @ResponseBody
    public synchronized   Map<String, Object> getDetailMes(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("idCardNum")String idCardNum,@RequestParam("userName")String  userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("socialCard")String socialCard) throws Exception {
        return service.getDetailMes(request,idCard.trim(),passWord.trim(),cityCode.trim(),idCardNum.trim());
    }
}
