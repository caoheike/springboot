package com.reptile.util;

import java.util.Map;

/**
 * 当代码运行过程中出现异常，推送正确的socket状态
 * @author  mrlu 2017 1 12
 */
public class DealExceptionSocketStatus {
    /**
     * 爬取过程出现异常，try块中根据signle推送不同的认证状态
     * @param signle
     * @param map
     * @param uuid
     */
    public static  void pushExceptionSocket(String signle,Map<String,Object> map,String uuid){
        if("1000".equals(signle)){
            //登录失败推送
            PushSocket.pushnew(map, uuid, "3000","系统繁忙,请重试");
        }else if("5000".equals(signle)){
            //获取数据失败推送
            PushSocket.pushnew(map, uuid, "7000","系统繁忙,请重试");
        }else if("4000".equals(signle)){
            //认证失败推送
            PushSocket.pushnew(map, uuid, "9000","系统繁忙,请重新认证");
        }
    }
}
