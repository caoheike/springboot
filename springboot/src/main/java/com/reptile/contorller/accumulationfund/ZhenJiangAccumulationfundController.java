package com.reptile.contorller.accumulationfund;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.accumulationfund.ZhenJiangAccumulationfundService;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("ZhenJiangAccumulationfundController")
public class ZhenJiangAccumulationfundController {

    @Autowired
    private ZhenJiangAccumulationfundService service;
    /*@ApiOperation(value = "镇江公积金获取图形验证码", notes = "参数：无")
    @ResponseBody
    @RequestMapping(value = "ZhenJiangImageCode", method = RequestMethod.POST)
	 public Map<String, Object> getImageCode(HttpServletRequest request){
		return service.getImageCode(request);
	}*/
    @RequestMapping(value = "getDetailMes",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取公积金详情",notes = "参数：身份证，密码，城市id,身份证号")
    public Map<String,Object> getDetailMes(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("passWord")String passWord,@RequestParam("cityCode")String cityCode,@RequestParam("idCardNum")String idCardNum){

        return service.getDetailMes(request,idCard.trim(),passWord.trim(),cityCode.trim(),idCardNum.trim());
    }
}
