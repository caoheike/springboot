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

import com.reptile.service.accumulationfund.TaiZhouAccumulationfundService;
/**
 * 
 * @ClassName: TaiZhouAccumulationfundController  
 * @Description: TODO  
 * @author: fangshuang
 * @date 2017年12月29日  
 *
 */
@Controller
@RequestMapping("TaiZhouAccumulationfundController")
public class TaiZhouAccumulationfundController {
	@Autowired
    private TaiZhouAccumulationfundService service;

    @RequestMapping(value = "loadImageCode",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "加载图片验证码",notes = "参数：无")
    public Map<String,Object> loadImageCode(HttpServletRequest request){

        return service.loadImageCode(request);
    }

    @RequestMapping(value = "getDeatilMes",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "台州住房公积金",notes = "参数：身份证，密码，图片验证码")
    public Map<String,Object> getDeatilMes(HttpServletRequest request, @RequestParam("idCard")String idCard,
    		@RequestParam("idCardNum")String idCardNum,@RequestParam("userName")String userName,
    		@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,
    		@RequestParam("cityCode")String cityCode,@RequestParam("fundCard")String fundCard){

        return service.getDeatilMes(request, idCard.trim(), passWord.trim(),catpy.trim(),idCardNum.trim());
    }
}
