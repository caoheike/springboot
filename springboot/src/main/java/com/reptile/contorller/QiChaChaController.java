package com.reptile.contorller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.reptile.service.QiChaChaService;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author 刘彬
 *
 */
@RestController
@RequestMapping("QiChaChaController")
public class QiChaChaController {
	@Autowired
	private QiChaChaService qiChaCha;
	
	@ApiOperation(value = "企查查工商查询", notes = "参数：公司名称")
	@ResponseBody
	@RequestMapping(value="qiChaCha",method=RequestMethod.POST)
	public Map<String ,Object> qiChaCha(HttpServletRequest request,@RequestParam ("findName") String findName){
		
		
		return qiChaCha.qichacha(request, findName);
		
	}
}
