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

import com.reptile.service.accumulationfund.QinZhouFundService;

@Controller
@RequestMapping("QinZhouFundController")
public class QinZhouFundController {
	@Autowired
	private QinZhouFundService service;
	@ApiOperation(value = "1.钦州公积金获取", notes = "参数：身份证号，密码，城市编号")
    @ResponseBody
    @RequestMapping(value = "QZFlogin", method = RequestMethod.POST)
	public Map<String, Object> login(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("idCardNum")String idCardNum,@RequestParam("userName")String userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("fundCard")String fundCard){
		System.out.println(idCardNum);
		return service.getImageCode(request, idCard, passWord,cityCode,idCardNum);
	}

}
