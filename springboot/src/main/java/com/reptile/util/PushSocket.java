package com.reptile.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PushSocket {
	private static Logger logger= LoggerFactory.getLogger(PushSocket.class);
	/**
	 * 0001 失败 0000 成功
	 * @param map
	 * @param UUID
	 * @param errorInfo
	 */
	public static Map<String, Object> push1(Map<String, Object> map,String UUID,String errorInfo){
		Map<String, Object> mapData=new HashMap<String, Object>();
		Session se=	LongLink.getWsUserMap().get(UUID);
		String seq_id=LongLink.getWsInfoMap().get(UUID);
		System.out.println(se);
		System.out.println(seq_id);
		try {
			if(se!=null&&seq_id!=null){
				if(seq_id.equals("hello")){
					se.getBasicRemote().sendText("{\"resultCode\":\""+errorInfo+"\",\"seq_id\":\""+seq_id+"\"}");
				}else{
					se.getBasicRemote().sendText("{\"resultCode\":"+errorInfo+",\"seq_id\":"+seq_id+"}");	
				}
			}
		} catch (Exception e) {
			logger.warn("-----------------推送状态时，长链接出现问题-------------------",e);
			map.put("errorCode", "1100");
			map.put("errorInfo", "推送状态时，连接已关闭");
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
		Session se=	LongLink.getWsUserMap().get(UUID);
		String seq_id=LongLink.getWsInfoMap().get(UUID);
		System.out.println("se==="+se);
		System.out.println("seq==="+seq_id);
		System.out.println(errorInfor+resultCode);
		String date=currentTime();
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
			logger.warn("------------------------推送状态时，长链接出现问题----------------------",e);
			map.put("errorCode", "1100");
			map.put("errorInfo", "推送状态时，连接已关闭");
		}
		return map;
	}
	/**
	 * 获取当前时间（年月日时分秒）
	 * @return
	 */
	public static String currentTime(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
	    String mon = format.format(new Date());
		return mon;
	}
}
