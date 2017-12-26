package com.reptile.contorller.businfocontroller;

import com.reptile.service.businfoservice.BusinessInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 工商信息获取模板
 *
 * @author mrlu
 * @date 2017/12/25
 */
@Controller
@RequestMapping("BusinessInfoController")
public class BusinessInfoController {
    @Autowired
    private BusinessInfoService service;

    @ApiOperation(value = "1.获取XX工商信息", notes = "参数：无")
    @ResponseBody
    @RequestMapping(value = "sendPhoneCode", method = RequestMethod.POST)
    public Map<String, String> getBusInfo(HttpServletRequest request,String titile) {
        return service.getBusInfo(request,titile);
    }
}
