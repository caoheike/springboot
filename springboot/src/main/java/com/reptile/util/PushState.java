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
     * 200
     * @param UserCard
     * @param approveName
     * @param stat
     * @param message
     */
    public static void state(String UserCard, String approveName, int stat,String message) {
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> stati = new HashMap<String, Object>();
        String stats = stat + "";
        stati.put("cardNumber", UserCard);
        stati.put("approveName", approveName);
        stati.put("approveState", stats);
        stati.put("message", message);
        data.put("data", stati);
        System.out.println("-200--state开始推送-"+data);
        Resttemplate resttemplatestati = new Resttemplate();
        map1 = resttemplatestati.SendMessage(data, "http://192.168.3.4:8081/HSDC/authcode/Autherized");
    }
  
}
