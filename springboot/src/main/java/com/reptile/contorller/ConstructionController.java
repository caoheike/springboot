package com.reptile.contorller;

import java.util.Map;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.reptile.service.ConstructionService;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author liubin
 *
 */
@Controller
@RequestMapping("ConstructionController")
public class ConstructionController {
	
	@Autowired
	private ConstructionService con;
	
	@ApiOperation(value = "建设银行信用卡", notes = "参数：用户账号(身份证),银行账号,查询密码")
	@ResponseBody
	@RequestMapping(value="Construction",method=RequestMethod.POST)
	public Map<String,Object> construction(HttpServletRequest request,@RequestParam("UserCard") String userCard,@RequestParam("UserCode") String userCode,
											@RequestParam("CodePass") String codePass,@RequestParam("UUID")String uuid,@RequestParam("timeCnt")String timeCnt) throws Exception{
		return con.check(request, userCard, userCode, codePass,uuid,timeCnt);
	}
}
