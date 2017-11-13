package com.reptile.contorller;

import com.reptile.service.PhoneBillsService;
import com.reptile.util.CustomAnnotation;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 手机通话详情
 */
@Controller
@RequestMapping("chinaMobile")
public class PhoneBillsController {
    @Autowired
    private PhoneBillsService phoneService;

    @ApiOperation(value = "第一步，获取移动手机验证码", notes = "参数：手机号码")
    @ResponseBody
    @RequestMapping(value = "getChinaMobileCode",method = RequestMethod.POST)
    public Map<String,String> getChinaMobileCode(HttpServletRequest request, @RequestParam("userNumber")String userNumber) throws Exception {
        return phoneService.getChinaMobileCode(request,userNumber.trim());
    }

    @ApiOperation(value = "第二步，输入手机验证码+手机号", notes = "参数：手机号码，手机验证码")
    @ResponseBody
    @RequestMapping(value = "chinaMobilLoad",method = RequestMethod.POST)
    public Map<String,String> chinaMobilLoad(HttpServletRequest request, @RequestParam("userNumber") String userNumber, @RequestParam("phoneCode") String phoneCode) throws Exception {
        return phoneService.chinaMobilLoad(request,userNumber.trim(),phoneCode.trim());
    }

    @ApiOperation(value = "第三步，获取详情页面的验证码", notes = "参数：无")
    @ResponseBody
    @RequestMapping(value = "getDetialImageCode",method = RequestMethod.POST)

    public Map<String,Object> getDetialImageCode(HttpServletRequest request) throws Exception {

        return phoneService.getDetialImageCode(request);
    }

    @ApiOperation(value = "第四步，输入手机获得手机验证码", notes = "参数：手机号码")
    @ResponseBody
    @RequestMapping(value = "getDetialMobilCode",method = RequestMethod.POST)
    public Map<String,String> getDetialMobilCode(HttpServletRequest request, @RequestParam("userNumber")String userNumber) throws Exception {
        return phoneService.getDetialMobilCode(request,userNumber.trim());
    }

    @ApiOperation(value = "第5步，获取账单信息", notes = "参数：手机号码")
    @ResponseBody
    @CustomAnnotation
    @RequestMapping(value = "getDetailAccount",method = RequestMethod.POST)
    public Map<String,Object> getDetailAccount(HttpServletRequest request, @RequestParam("userNumber")String userNumber, @RequestParam("phoneCode")String phoneCode,
                                               @RequestParam("fuwuSec")String fuwuSec, @RequestParam("imageCode")String imageCode ,
                                               @RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude,@RequestParam("UUID")String UUID ) throws Exception {
        return phoneService.getDetailAccount(request,userNumber.trim(),phoneCode.trim(),fuwuSec.trim(),imageCode.trim(),longitude.trim(),latitude.trim(),UUID.trim());
    }
}
