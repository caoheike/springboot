package com.reptile.contorller.depositCard;

import com.reptile.service.depositCard.ChinaBankDepositCardService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 中国银行储蓄卡
 */

@Controller
@RequestMapping("ChinaBankDepositCardController")
public class ChinaBankDepositCardController {
    @Autowired
    private ChinaBankDepositCardService service;

    @RequestMapping(value = "getDetailMes",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "储蓄卡获取数据",notes = "参数：")
    public Map<String, Object> getDetailMes(HttpServletRequest request) {
        return service.getDetailMes(request);
    }
}
