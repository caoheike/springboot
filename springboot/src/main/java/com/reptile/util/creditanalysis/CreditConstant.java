package com.reptile.util.creditanalysis;

import java.util.HashMap;
import java.util.Map;

public class CreditConstant {
    public static Map<String,String> successMap=new HashMap<>();

    static{
        successMap.put("ResultInfo","查询成功");
        successMap.put("ResultCode","0000");
        successMap.put("errorInfo","查询成功");
        successMap.put("errorCode","0000");
    }

    public static Map<String,Object> setErrorFailMap(String errorcode,String errorInfo){
        Map<String,Object> failMap=new HashMap<>();
        failMap.put("ResultInfo",errorInfo);
        failMap.put("ResultCode",errorcode);
        failMap.put("errorInfo",errorInfo);
        failMap.put("errorCode",errorcode);
        return failMap;
    }
}
