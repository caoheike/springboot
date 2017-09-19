package com.reptile.util;

import java.util.HashMap;
import java.util.Map;

public class PushState {
	
	public static void state(String UserCard,String approveName,int stat){
		Map<String, Object> map1=new HashMap<String, Object>();
		Map<String, Object> data=new HashMap<String, Object>();
		Map<String,Object> stati=new HashMap<String, Object>();
		String stats=stat+"";
		stati.put("cardNumber",UserCard);
		stati.put("approveName" , approveName);
		stati.put("approveState",stats);
		data.put("data", stati);
		System.out.println("--开始推送-");
		Resttemplate resttemplatestati = new Resttemplate();
		map1=resttemplatestati.SendMessage(data, "http://192.168.3.4:8081/HSDC/authcode/Autherized");
	}
}
