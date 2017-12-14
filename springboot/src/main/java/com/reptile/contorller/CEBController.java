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
import com.reptile.service.SEOandCHSIService;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("CEBController")
public class CEBController {
	
	@Autowired
	private CEBService ceb;
		@ApiOperation(value = "1.发送短信验证码", notes = "参数：用户账号(身份证),银行账号")
		@ResponseBody
		@RequestMapping(value="CEBPass",method=RequestMethod.POST)
		public Map<String,Object> CEBPass(HttpServletRequest request,@RequestParam("UserCard") String UserCard,@RequestParam("UserName") String UserName) throws Exception{
			return ceb.CEBlogin1(request,UserCard,UserName);
		}
		@ApiOperation(value = "2.获取信用卡详单", notes = "参数：用户账号(身份证),短信验证码")
		@ResponseBody
		@RequestMapping(value="CEBlogin",method=RequestMethod.POST)
		public Map<String,Object> CEBlogin(HttpServletRequest request,@RequestParam("UserCard")String UserCard,@RequestParam("Password")String Password,@RequestParam("UUID")String UUID,@RequestParam("timeCnt")String timeCnt) throws Exception{
			return ceb.CEBlogin2(request,UserCard,Password,UUID,timeCnt);
		}
}
