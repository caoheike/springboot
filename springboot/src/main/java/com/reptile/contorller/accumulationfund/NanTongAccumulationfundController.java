package com.reptile.contorller.accumulationfund;

import com.reptile.service.accumulationfund.NanTongAccumulationfundService;

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
@RequestMapping("NanTongAccumulationfundController")
public class NanTongAccumulationfundController {

    @Autowired
    private NanTongAccumulationfundService service;

    @RequestMapping(value = "getDetailMes",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取公积金详情",notes = "参数：身份证，用户名，密码，城市id")
    public Map<String,Object> getDetailMes(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("userName")String userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("fundCard")String fundCard ){

        return service.getDetailMes(request,idCard.trim(),userName.trim(),passWord.trim(),cityCode.trim());
    }
}
