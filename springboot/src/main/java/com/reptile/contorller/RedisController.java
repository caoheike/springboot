package com.reptile.contorller;

import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.util.RedisSourceUtil;

@Controller
@RequestMapping("RedisController")
public class RedisController {
	
	@Autowired
	private  RedisSourceUtil redisSourceUtil;
	
	@ApiOperation(value = "往redis中保存数据",notes = "")
	@RequestMapping(value = "saveRedisData",method = RequestMethod.POST)
	@ResponseBody
    public boolean driverClose(HttpServletRequest request,@RequestParam("key")String key,@RequestParam("value")String value){
		 String redisTime = request.getParameter("redisTime");
         return redisSourceUtil.setValue(key, value , redisTime);
    }
	@ResponseBody
	@ApiOperation(value = "从redis中获取数据",notes = "")
	@RequestMapping(value = "getRedisData",method = RequestMethod.POST)
	public String driverClose(HttpServletRequest request,@RequestParam("key")String key){
		return redisSourceUtil.getValue(key);
	}
	
	
}
