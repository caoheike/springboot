package com.reptile.contorller;

import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.BackupProducessService;


@Controller
@RequestMapping("backupProducessController")
public class BackupProducessController {
	 @Autowired
	 private BackupProducessService service;
	 @ApiOperation(value = "", notes = "参数：手机号码,服务密码，运营商类型")
	 @ResponseBody
	 @RequestMapping(value = "identifyProduce",method = RequestMethod.POST)
	 public void identify(HttpServletRequest request,@RequestParam("phoneNumber")String phoneNumber,@RequestParam("servePwd")String servePwd,@RequestParam("type")String type,@RequestParam("longitude")String longitude,@RequestParam("latitude")String  latitude,@RequestParam("UUID")String uuid){
		 service.identifyProduce(request, phoneNumber, servePwd, type,longitude,latitude, uuid);
	 }

}
