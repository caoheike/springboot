package com.reptile.contorller.accumulationfund;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.accumulationfund.GuiYangAccumulationfundService;
import com.reptile.service.accumulationfund.LiuZhouAccumulationfundService;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("LiuZhouAccimulationfundController")
public class LiuZhouAccimulationfundController {
	  @Autowired
	    private LiuZhouAccumulationfundService service;
	  	
	  	//柳州住房公积金图片验证码
	    @RequestMapping(value = "loadImageCode",method = RequestMethod.POST)
	    @ResponseBody
	    @ApiOperation(value = "加载图片验证码",notes = "参数：无")
	    public Map<String,Object> loadImageCode(HttpServletRequest request){

	        return service.loginImage(request);
	    }

	    @RequestMapping(value = "getDeatilMes",method = RequestMethod.POST)
	    @ResponseBody
	    @ApiOperation(value = "柳州住房公积金",notes = "参数：身份证,密码，图片验证码,城市编号")
	    public Map<String,Object> getDeatilMes(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("userName")String userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("fundCard")String fundCard,@RequestParam("idCardNum") String idCardNum){

	        return service.getDeatilMes(request, idCard,catpy, fundCard,passWord,cityCode,idCardNum);
	    }
}
