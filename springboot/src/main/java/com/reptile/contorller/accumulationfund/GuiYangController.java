package com.reptile.contorller.accumulationfund;

import com.reptile.service.accumulationfund.GuiYangService;
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
@RequestMapping("GuiYangController")
public class GuiYangController {
    @Autowired
    private GuiYangService service;
    @RequestMapping(value = "loadMethod",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "贵阳住房公积金",notes = "参数：身份证，密码")
    public Map<String,String> loadMethod(HttpServletRequest request, @RequestParam("userCard") String userCard, @RequestParam("password")String password){

        return null;
    }
}
