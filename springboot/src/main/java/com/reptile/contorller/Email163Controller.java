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


/**
 * 163邮箱
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Controller
@RequestMapping("mail163")
public class Email163Controller {
    @Resource
    private Email163Service service;

    /**
     * 获取邮箱账单信息
     * @param request
     * @param response
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "163邮箱", notes = "参数：账号，密码")
    @ResponseBody
    @RequestMapping(value = "get163Mail", method = RequestMethod.POST)
    public Map<String, Object> get163Mail(HttpServletRequest request,
                                          HttpServletResponse response, @RequestParam String username, @RequestParam String password) throws Exception {

        return service.get163Mail(request, response, username, password);
    }

    /**
     * 认证163电子邮箱，并获取信息
     * @param request
     * @param response
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "RZ163Mail", method = RequestMethod.POST)
    public String rz163Mail(HttpServletRequest request,
                                          HttpServletResponse response, @RequestParam("qqnumber") String username, @RequestParam("password") String password) throws Exception {
        String page="";
        String flag="0000";
        Map<String, Object> mail = service.get163Mail(request, response, username, password);
        if(mail.toString().contains(flag)){
            page="OperatorView/success";
        }else{
            page="OperatorView/error";
        }
        return page;
    }
}
