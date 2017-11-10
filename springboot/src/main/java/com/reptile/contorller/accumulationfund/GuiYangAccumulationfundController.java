package com.reptile.contorller.accumulationfund;

import com.reptile.service.accumulationfund.GuiYangAccumulationfundService;

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
@RequestMapping("GuiYangAccumulationfundController")
public class GuiYangAccumulationfundController {
    @Autowired
    private GuiYangAccumulationfundService service;

    @RequestMapping(value = "loadImageCode",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "加载图片验证码",notes = "参数：无")
    public Map<String,Object> loadImageCode(HttpServletRequest request){

        return service.loadImageCode(request);
    }

    @RequestMapping(value = "getDeatilMes",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "贵阳住房公积金",notes = "参数：身份证，密码，图片验证码，城市编号")
    public Map<String,Object> getDeatilMes(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("userName")String userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("fundCard")String fundCard){

        return service.getDeatilMes(request, idCard.trim(), passWord.trim(),catpy.trim(),cityCode.trim());
    }
}
