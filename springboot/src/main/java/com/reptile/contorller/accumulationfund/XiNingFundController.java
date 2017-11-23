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

import com.reptile.service.accumulationfund.XiNingFundService;



@Controller
@RequestMapping("XiNingFundController")
public class XiNingFundController {
	@Autowired
    private XiNingFundService service;
	@ApiOperation(value = "1.西宁公积金获取图形验证码", notes = "参数：")
    @ResponseBody
    @RequestMapping(value = "XNFImageCode", method = RequestMethod.POST)
	 public Map<String, Object> getImageCode(HttpServletRequest request){
		return service.getImageCode(request);
	}
	   @ApiOperation(value = "2.西宁公积金获得清单", notes = "参数：身份证，密码,图形验证码")
	    @ResponseBody
	    @RequestMapping(value = "XNlogin", method = RequestMethod.POST)
	public Map<String, Object> login(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("idCardNum")String idCardNum,@RequestParam("userName")String userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("fundCard")String fundCard){
		System.out.println("已经访问了");
		return service.login( request,idCard, passWord,catpy,cityCode,idCardNum);
	}
}
