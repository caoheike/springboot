package com.reptile.contorller.accumulationfund;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.accumulationfund.GuiYangAccumulationfundService;
import com.reptile.service.accumulationfund.NingBoAccumulationfundService;
@Controller
@RequestMapping("NingBoAccumulationfundController")
public class NingBoAccumulationfundController {
	@Autowired
    private NingBoAccumulationfundService service;

    @RequestMapping(value = "loadImageCode",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "加载图片验证码",notes = "参数：无")
    public Map<String,Object> loadImageCode(HttpServletRequest request){

        return service.loadImageCode(request);
    }

    @RequestMapping(value = "getDeatilMes",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "贵阳住房公积金",notes = "参数：身份证，密码，图片验证码")
    public Map<String,Object> getDeatilMes(HttpServletRequest request, @RequestParam("idCard") String idCard, @RequestParam("passWord")String passWord,
                                           @RequestParam("imageCode")String imageCode){

        return service.getDeatilMes(request, idCard.trim(), passWord.trim(),imageCode.trim());
    }
}
