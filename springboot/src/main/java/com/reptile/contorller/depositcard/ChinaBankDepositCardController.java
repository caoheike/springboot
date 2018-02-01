package com.reptile.contorller.depositcard;

import com.reptile.service.depositcard.ChinaBankDepositCardService;
import com.reptile.util.CustomAnnotation;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 中国银行储蓄卡
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Controller
@RequestMapping("ChinaBankDepositCardController")
public class ChinaBankDepositCardController {
    @Autowired
    private ChinaBankDepositCardService service;

    @RequestMapping(value = "getDetailMes", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "储蓄卡获取数据", notes = "参数：身份证号，卡号，密码，用户名")
    @CustomAnnotation
    public Map<String, Object> getDetailMes(HttpServletRequest request, @RequestParam("IDNumber") String idNumber, 
    		@RequestParam("cardNumber") String cardNumber,
            @RequestParam("passWord") String passWord,
            @RequestParam("userName") String userName,
            @RequestParam("UUID") String uuid,
            @RequestParam("flag") Boolean flag) {
        Map<String,Object> map=new HashMap<String,Object>(16);
        synchronized (this){
            map= service.getDetailMes(request, idNumber, cardNumber, passWord, userName,uuid,flag);
        }
        return map;
    }
}
