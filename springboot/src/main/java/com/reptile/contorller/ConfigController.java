package com.reptile.contorller;

import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.reptile.service.Email163Service;
import com.reptile.util.application;


@RestController
public class ConfigController {

	@Autowired
	private application application;
	@RequestMapping("ipconfig")
	public  Map<String,String> ipconfig(){
		Map<String,String> map=new HashMap<String, String>();
		map.put("ip",application.getIp()+"用于返回验证码");
		map.put("prot",application.getPort()+"用于返回验证码");
		map.put("sendip",application.getSendip()+"目前推送地址");

		
		return map;
		
	}

}
