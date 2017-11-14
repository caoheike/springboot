package com.reptile.util;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import net.sf.json.JSONObject;

import org.java_websocket.WebSocket;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/19.
 */
@Component
@ServerEndpoint("/hello")
public class talkFrame {
	private static  Map<String,Session> wsUserMap = new HashMap<String,Session>();
	private static  Map<String,String> wsInfoMap = new HashMap<String,String>();
	
    public static Map<String, String> getWsInfoMap() {
		return wsInfoMap;
	}
	public static void setWsInfoMap(Map<String, String> wsInfoMap) {
		talkFrame.wsInfoMap = wsInfoMap;
	}
	public static Map<String, Session> getWsUserMap() {
		return wsUserMap;
	}
	public static void setWsUserMap(Map<String, Session> wsUserMap) {
		talkFrame.wsUserMap = wsUserMap;
	}
	@OnOpen
    public void onopen(Session session){
        System.out.println("连接成功");
//        try {
//            session.getBasicRemote().sendText("1234...");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    @OnClose
    public void onclose(Session session){
        System.out.println("close....");
    }
    @OnMessage
    public void onsend(Session session,String msg){
    	if(msg!=null&&!msg.equals("")){
    		try {
				session.getBasicRemote().sendText(msg);//链接成功
			} catch (IOException e) {
				
				e.printStackTrace();
			}
    		JSONObject json=JSONObject.fromObject(msg);
    		msg=json.get("req").toString();
    		
    		wsUserMap.put(msg, session);
    		wsInfoMap.put(msg, json.get("seq_id").toString());
    	}else{
    		//System.out.println("qddd");
    		onclose(session);
    	}
     
    }
    
    
}
