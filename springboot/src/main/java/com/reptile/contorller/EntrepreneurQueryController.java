package com.reptile.contorller;

import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.reptile.service.EntrepreneurQueryService;

/**
 * 
 * @Title: EntrepreneurQueryController.java
 * @Package com.reptile.contorller
 * @Description: TODO(工商网站信息查询)
 * @author Bigyoung
 * @date 2017年12月16日
 * @version V1.0
 */
@RestController
@RequestMapping("/")
public class EntrepreneurQueryController {

	@Autowired
	private EntrepreneurQueryService entrepreneurqueryservice;

	/**
	 * 通用工商网查询
	 * 
	 * @param request
	 * @param CompanyInfo
	 * @throws Exception
	 */
	@ApiOperation(value = "EntrepreneurQuer", notes = "工商网处理")
	@ResponseBody
	@RequestMapping(value = "EntrepreneurQuer", method = RequestMethod.GET)
	public void entrepreneurQuer(HttpServletRequest request, String companyInfo)
			throws Exception {
		EntrepreneurQueryService.entrepreneurQuer(companyInfo);

	}

	/**
	 * 北京工商网查询
	 * 
	 * @param request
	 * @param CompanyInfo
	 * @throws Exception
	 */
	@ApiOperation(value = "Beijing", notes = "工商网处理")
	@ResponseBody
	@RequestMapping(value = "BeijingEntrepreneurQuer", method = RequestMethod.GET)
	public void beijingEntrepreneurQuer(HttpServletRequest request,
			String companyInfo) throws Exception {
		EntrepreneurQueryService.beijingEntrepreneurQuer(companyInfo);

	}

}
