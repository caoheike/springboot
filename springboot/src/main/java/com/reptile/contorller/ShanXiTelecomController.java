package com.reptile.contorller;

import com.reptile.service.ShanXiTelecomService;
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
@RequestMapping("ShanXiTelecomController")
public class ShanXiTelecomController {
    @Autowired
    private ShanXiTelecomService service;

    @CustomAnnotation
    @ResponseBody
    @RequestMapping(value = "TelecomLogin", method = RequestMethod.POST)
    @ApiOperation(value = "陕西电信登录", notes = "电信登录")
    public Map<String, Object> TelecomLogin(HttpServletRequest req, @RequestParam("userPhone") String userPhone,
                                            @RequestParam("userPassword") String userPassword) throws Exception {
        return service.TelecomLogin(req, userPhone,userPassword);
    }
}
