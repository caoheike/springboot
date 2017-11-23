package com.reptile.contorller.accumulationfund;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.accumulationfund.NanNingAccumulationfundService;


@Controller
@RequestMapping("NanNingAccumulationfundController")
public class NanNingAccumulationfundController {
	@Autowired
    private NanNingAccumulationfundService service;

    
    @RequestMapping(value = "getDeatilMes",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "南宁住房公积金",notes = "参数：身份证，密码")
    public Map<String,Object> getDeatilMes(HttpServletRequest request, @RequestParam("idCard") String idCard, @RequestParam("passWord")String passWord){

        return service.getDeatilMes(request, idCard.trim(), passWord.trim());
    }
}
