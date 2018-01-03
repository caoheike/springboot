package com.reptile.contorller.accumulationfund;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reptile.service.accumulationfund.QingDaoAccumulationfundService;

import io.swagger.annotations.ApiOperation;
/**
 * 
 * @ClassName: QingDaoAccumulationfundController  
 * @Description: TODO  
 * @author: lusiqin
 * @date 2018年1月2日  
 *
 */
@Controller
@RequestMapping("QingDaoAccumulationfundController")
public class QingDaoAccumulationfundController {

    @Autowired
    private QingDaoAccumulationfundService service;
	 
    @RequestMapping(value = "getDetailMes",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取公积金详情",notes = "参数：账户，密码，城市id，身份证")
    public Map<String,Object> getDetailMes(HttpServletRequest request,@RequestParam("idCard")String idCard,@RequestParam("passWord")String passWord,@RequestParam("cityCode")String cityCode,@RequestParam("idCardNum")String idCardNum){
        return service.getDetailMes(request,idCard.trim(),passWord.trim(),cityCode.trim(),idCardNum.trim());
    }
}
