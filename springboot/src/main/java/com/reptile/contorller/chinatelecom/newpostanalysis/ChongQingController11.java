package com.reptile.contorller.chinatelecom.newpostanalysis;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.chinatelecom.newpostanalysis.ChongQingService11;
import com.reptile.util.CustomAnnotation;

/**
 * 
 * @ClassName: ChongQingTelecomController
 * @Description: TODO
 * @author: 111
 * @date 2018年1月2日
 *
 */
@Controller
@RequestMapping("ChongQingController11")
public class ChongQingController11 {
	@Autowired
	private ChongQingService11 chongQingService;

	@ApiOperation(value = "1.发送短信验证码", notes = "参数：手机号")
	@RequestMapping(value = "sendCode", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> sendCode(HttpServletRequest request, @RequestParam("phoneNum") String phoneNum) {

		return chongQingService.sendCode(request, phoneNum);
	}

	@ApiOperation(value = "2.获取详单", notes = "参数：手机号，服务密码，验证码，经度，纬度，姓名，身份证后六位")
	@ResponseBody
	@CustomAnnotation
	@RequestMapping(value = "CQGetDetail1", method = RequestMethod.POST)
	public Map<String, Object> getDetail(HttpServletRequest request, @RequestParam("phoneNumber") String phoneNumber,@RequestParam("passWord") String passWord,
			@RequestParam("code") String code,
			@RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude,
			@RequestParam("UUID") String uuid, @RequestParam("userName") String userName,
			@RequestParam("idCard") String idCard) {
		return chongQingService.getDetail(request, phoneNumber, passWord, code, longitude, latitude, uuid, userName,idCard);

	}
}
