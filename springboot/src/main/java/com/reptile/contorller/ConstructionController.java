package com.reptile.contorller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.CEBService;
import com.reptile.service.ConstructionService;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("ConstructionController")
public class ConstructionController {
	
	@Autowired
	private ConstructionService con;
	
	@ApiOperation(value = "建设银行信用卡", notes = "参数：用户账号(身份证),银行账号,查询密码")
	@ResponseBody
	@RequestMapping(value="Construction",method=RequestMethod.POST)
	public Map<String,Object> Construction(HttpServletRequest request,@RequestParam("UserCard") String UserCard,@RequestParam("UserCode") String UserCode,@RequestParam("CodePass") String CodePass,@RequestParam("UUID")String UUID) throws Exception{
		return con.check(request, UserCard, UserCode, CodePass,UUID);
	}
}
