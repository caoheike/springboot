package com.reptile.util;

import java.util.HashMap;
import java.util.Map;

public class PushState {

    public static void state(String UserCard, String approveName, int stat) {
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> stati = new HashMap<String, Object>();
        String stats = stat + "";
        stati.put("cardNumber", UserCard);
        stati.put("approveName", approveName);
        stati.put("approveState", stats);
        data.put("data", stati);
        System.out.println("-state开始推送-"+data);
        Resttemplate resttemplatestati = new Resttemplate();
        map1 = resttemplatestati.SendMessage(data, ConstantInterface.port + "/HSDC/authcode/Autherized");
    }
    /**
     * 认证项失败原因推送
     * @param cardNumber 身份证
     * @param message消息内容（失败原因）
     * @param approveItem  认证项标识 （淘宝）
     */
    public static void endstate(Map<String,Object> map) {
        Map<String,Object> map1=new HashMap<>();
        Resttemplate resttemplatestati = new Resttemplate();
        map1.put("data", map);
        System.out.println("--认证失败项开始推送-"+map1);
        map1=resttemplatestati.SendMessage(map1,"http://117.34.70.217:8080/HSDC/authcode/messagePush");
        System.out.println("认证失败项推送结果==="+map1);
    }
}
