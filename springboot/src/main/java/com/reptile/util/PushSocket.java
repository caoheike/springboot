package com.reptile.util;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

public class PushSocket {
	/**
	 * 0001 失败 0000 成功
	 * @param map
	 * @param UUID
	 * @param errorInfo
	 */
	public static Map<String, Object> push1(Map<String, Object> map,String UUID,String errorInfo){
		Map<String, Object> mapData=new HashMap<String, Object>();
		Session se=	talkFrame.getWsUserMap().get(UUID);
		String seq_id=talkFrame.getWsInfoMap().get(UUID);
		System.out.println(se);
		System.out.println(seq_id);
		try {
//			mapData.put("resultCode", errorInfo);
//			mapData.put("seq_id", seq_id);
//			JSONObject json=JSONObject.fromObject(mapData);
			if(se!=null&&seq_id!=null){
				if(seq_id.equals("hello")){
					se.getBasicRemote().sendText("{\"resultCode\":\""+errorInfo+"\",\"seq_id\":\""+seq_id+"\"}");
				}else{
					se.getBasicRemote().sendText("{\"resultCode\":"+errorInfo+",\"seq_id\":"+seq_id+"}");	
				}
			}
		} catch (Exception e) {
			  map.put("errorCode", "0001");
			  map.put("errorInfo", "网络异常");
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 0001 失败 0000 成功 1000登陆中
	 * @param map
	 * @param UUID
	 * @param resultCode 
	 * @param errorInfor 失败原因
	 * 
	 */
	public static Map<String, Object> pushnew(Map<String, Object> map,String UUID,String resultCode,String errorInfor){
		Map<String, Object> mapData=new HashMap<String, Object>();
		Session se=	talkFrame.getWsUserMap().get(UUID);
		String seq_id=talkFrame.getWsInfoMap().get(UUID);
		System.out.println("se==="+se);
		System.out.println("seq==="+seq_id);
		System.out.println(errorInfor);
		String date=Dates.currentTime();
		try {
			if(se!=null&&seq_id!=null){
				if(seq_id.equals("hello")){
					se.getBasicRemote().sendText("{\"resultCode\":\""+resultCode+"\",\"seq_id\":\""+seq_id+"\",\"errorInfor\":\""+errorInfor+"\",\"date\":\""+date+"\"}");
				}else{
					//se.getBasicRemote().sendText("{\"resultCode\":"+resultCode+",\"seq_id\":"+seq_id+"}");	
					se.getBasicRemote().sendText("{\"resultCode\":"+resultCode+",\"seq_id\":"+seq_id+",\"errorInfor\":\""+errorInfor+"\",\"date\":\""+date+"\"}");
				}
			}
		} catch (Exception e) {
			  map.put("errorCode", "0001");
			  map.put("errorInfo", "网络异常");
			e.printStackTrace();
		}
		return map;
	}
}
