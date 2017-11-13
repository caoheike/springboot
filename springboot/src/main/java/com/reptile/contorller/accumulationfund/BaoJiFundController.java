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

import com.reptile.service.accumulationfund.BaoJiFundService;
@Controller
@RequestMapping("BaoJiFundController")
public class BaoJiFundController {
	@Autowired
    private BaoJiFundService service;
	@ApiOperation(value = "1.宝鸡积金", notes = "参数：身份证号，密码")
    @ResponseBody
    @RequestMapping(value = "BaoJilogin", method = RequestMethod.POST)
	public Map<String, Object> login(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("userName")String userName,@RequestParam("passWord")String passWord,@RequestParam("catpy")String catpy,@RequestParam("cityCode")String cityCode,@RequestParam("fundCard")String fundCard){
		return service.login(request, idCard, passWord,cityCode);
				
	}

}
