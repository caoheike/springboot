package com.reptile.contorller;

import com.reptile.service.ChinaBankService;
import com.reptile.util.CustomAnnotation;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 中国银行信用卡
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Controller
@RequestMapping("ChinaBankController")
public class ChinaBankController {
    @Autowired
    private ChinaBankService service;

    @ApiOperation(value = "获取ZGYH信用卡信息",notes = "参数：身份证，信用卡号，查询密码")
    @RequestMapping(value = "getDetailMes",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getDetailMes(HttpServletRequest request, @RequestParam("userCard") String userCard,
                                            @RequestParam("cardNumber") String cardNumber,@RequestParam("userPwd") String userPwd,@RequestParam("UUID")String uuid,@RequestParam("timeCnt")String timeCnt) throws ParseException {

        Map<String,Object> map=new HashMap<String,Object>(16);
        synchronized (this){
            map= service.getDetailMes(request, userCard, cardNumber, userPwd,uuid,timeCnt);
        }
        return map;
    }
}
