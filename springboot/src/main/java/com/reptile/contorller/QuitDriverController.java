package com.reptile.contorller;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.QuitDriverService;

@Controller
@RequestMapping("QuitDriver")
public class QuitDriverController {
	   @Autowired
	   private QuitDriverService service;
	    @RequestMapping(value = "driverClose",method = RequestMethod.POST)
	    @ResponseBody
	    @ApiOperation(value = "关闭driver",notes = "")
	    public Map<String, String> driverClose(HttpServletRequest request,@RequestParam("driverName")String driverName){

	         return service.driverClose(request,driverName);
	    }
	

}
