package com.reptile.contorller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.CebService;

import io.swagger.annotations.ApiOperation;
/**
 * 
 * @author liubin
 *
 */
@Controller
@RequestMapping("CEBController")
public class CebController {
	
	@Autowired
	private CebService ceb;
		@ApiOperation(value = "1.发送短信验证码", notes = "参数：用户账号(身份证),银行账号")
		@ResponseBody
		@RequestMapping(value="CEBPass",method=RequestMethod.POST)
		public Map<String,Object> cebPass(HttpServletRequest request,@RequestParam("UserCard") String userCard,@RequestParam("UserName") String userName) throws Exception{
			return ceb.ceblogin1(request,userCard,userName);
		}
		@ApiOperation(value = "2.获取信用卡详单", notes = "参数：用户账号(身份证),短信验证码,银行卡号")
		@ResponseBody
		@RequestMapping(value="CEBlogin",method=RequestMethod.POST)
		public Map<String,Object> ceblogin(HttpServletRequest request,@RequestParam("UserCard")String userCard,@RequestParam("Password")String password,@RequestParam("userAccount")String userAccount,@RequestParam("UUID")String uuid,@RequestParam("timeCnt")String timeCnt) throws Exception{
			return ceb.ceblogin2(request,userCard,password,userAccount,uuid,timeCnt);
		}
}
