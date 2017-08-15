package com.reptile.contorller;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.Email163Service;


@Controller
@RequestMapping("mail163")
public class Email163Controller {
    @Resource
    private Email163Service service;

    @ApiOperation(value = "163邮箱", notes = "参数：账号，密码")
    // 设置标题描述
    @ResponseBody
    @RequestMapping(value = "get163Mail", method = RequestMethod.POST)
    public Map<String, Object> get163Mail(HttpServletRequest request,
                                          HttpServletResponse response, @RequestParam() String username, @RequestParam String password) throws Exception {

        return service.get163Mail(request, response, username, password);
    }

    @RequestMapping(value = "RZ163Mail", method = RequestMethod.POST)
    public String RZ163Mail(HttpServletRequest request,
                                          HttpServletResponse response, @RequestParam("qqnumber") String username, @RequestParam("password") String password) throws Exception {
        System.out.println(username+"   "+password);
        return null;
    }
}
