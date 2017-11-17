package com.reptile.contorller;

import com.reptile.service.TaobaoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("TaobaoController")
public class TaobaoController {
    @Autowired
    TaobaoService service;

    @ApiOperation(value = "登录淘宝地址",notes = "参数：账号，密码")
    @RequestMapping("loadTaoBao")
    @ResponseBody
    public Map<String,String> loadTaoBao(HttpServletRequest request, @RequestParam("userAccount") String userAccount,@RequestParam("passWord") String passWord){
        return service.loadTaoBao(request,userAccount,passWord);
    }
}
