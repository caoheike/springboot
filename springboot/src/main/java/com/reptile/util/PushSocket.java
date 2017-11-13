package com.reptile.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

public class PushSocket {
	/**
	 * 0001 失败 0000 成功
	 * @param map
	 * @param UUID
	 * @param errorInfo
	 */
	public static void push(Map<String, Object> map,String UUID,String errorInfo){
		 
		Session se=	talkFrame.getWsUserMap().get(UUID);
		System.out.println(se);
		try {
			se.getBasicRemote().sendText(errorInfo);
		} catch (IOException e) {
			 map.put("errorCode", "0001");
			  map.put("errorInfo", "网络异常");
			e.printStackTrace();
		}
		
	}

}
