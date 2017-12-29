package com.reptile.contorller.chinatelecom;

import com.reptile.service.ChinaTelecom.JiangSuTelecomService;
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

/**
 * 江苏电信
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Controller
@RequestMapping("JiangSuTelecomController")
public class JiangSuTelecomController {
    @Autowired
    private JiangSuTelecomService service;

    @ApiOperation(value = "1.获取账单信息", notes = "参数：手机号，服务密码")
    @ResponseBody
    @CustomAnnotation
    @RequestMapping(value = "getDetailMes", method = RequestMethod.POST)
    public  Map<String,Object> getDetailMes(HttpServletRequest request, @RequestParam("phoneNumber") String phoneNumber,
                                            @RequestParam("userPassword") String password,@RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude,@RequestParam("UUID")String uuid){
        return service.getDetailMes(request,phoneNumber,password,longitude,latitude,uuid);
    }

}
