package com.reptile.contorller.chinatelecom;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.chinatelecom.GZtelecomService;
import com.reptile.util.CustomAnnotation;

/**
 * 贵州电信 发包
 * 
 * @author cui
 *
 */
@Controller
@RequestMapping("guizhouTelecom")
public class GZtelecomController {
	@Autowired
	private GZtelecomService service;
/**
 * 发送验证码
 * @param request
 * @return
 */
	@ApiOperation(value = "0.1获取验证码", notes = "")
	@ResponseBody
	@RequestMapping(value = "guiZhouLogin", method = RequestMethod.POST)
	public Map<String, Object> sendCode(HttpServletRequest request) {
		return service.sendCode(request);
	}
/**
 * 
 * @param request
 * @param phoneNumber 手机号
 * @param servePwd 服务密码
 * @param code  短信验证码
 * @param longitude  经度
 * @param latitude 纬度
 * @param uuid
 * @return
 */ 
	@ApiOperation(value = "0.1获取详单", notes = "手机号，服务密码，短信验证码，经度，纬度")
	@RequestMapping(value = "guiZhouDetial", method = RequestMethod.POST)
	@CustomAnnotation
	@ResponseBody
	public Map<String, Object> getDetail(HttpServletRequest request,
			@RequestParam("phoneNumber") String phoneNumber,
			@RequestParam("servePwd") String servePwd, @RequestParam("code") String code,
			@RequestParam("longitude") String longitude,
			@RequestParam("latitude") String latitude, @RequestParam("UUID") String uuid) {
		return service.getDetail(request, phoneNumber, servePwd, code, longitude, latitude, uuid);

	}
}
