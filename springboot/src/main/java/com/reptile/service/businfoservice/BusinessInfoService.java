package com.reptile.service.businfoservice;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Service
public class BusinessInfoService {

    public Map<String, String> getBusInfo(HttpServletRequest request,String titile) {
        /**
         * 这里写获取工商信息代码
         */
        return null;
    }
}
