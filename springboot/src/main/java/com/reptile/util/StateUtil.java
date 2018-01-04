package com.reptile.util;

import java.util.HashMap;
import java.util.Map;

public class StateUtil {
	//登录中
	public static final String REPTILE_1000 = "1000";
	//登录成功
	public static final String REPTILE_2000 = "2000";
	//{0}登录失败，{1}
	public static final String REPTILE_3000 = "3000";
	//获取数据中
	public static final String REPTILE_5000 = "5000";
	//获取数据成功
	public static final String REPTILE_6000 = "6000";
	//{0}数据获取失败，{1}
	public static final String REPTILE_7000 = "7000";
	//认证成功
	public static final String REPTILE_8000 = "8000";
	//{0}认证失败，{1}
	public static final String REPTILE_9000 = "9000";
	//认证中
	public static final int REPTILE_100 = 100;
	//认证失败
	public static final int REPTILE_200 = 200;
	//认证成功
	public static final int REPTILE_300 = 300;
	
	
	/**
	 * 状态推送
	 * @param uuid
	 * @param UserCard PushState.state()中入参UserCard
	 * @param approveName PushState.state()中入参approveName
	 * @param errorCode 状态码1000-9000
	 * @param msg1 {1}中要填充的信息,没有填空字符串
	 * @param msg2 {2}中要填充的信息,没有填空字符串
	 */
	public static void  sendState(String uuid,String UserCard,String approveName, String errorCode,String msg1,String msg2){
		String msg = StateUtil.getErrorMsg(errorCode, msg1, msg2);
		Map<String,Object> map = new HashMap<String, Object>();
		
		PushSocket.pushnew(map, uuid, errorCode, msg);
		if(errorCode.equals(REPTILE_1000)){
			PushState.state(UserCard, approveName,REPTILE_100);
		}else if(errorCode.equals(REPTILE_3000) || errorCode.equals(REPTILE_7000) || errorCode.equals(REPTILE_9000)){
			PushState.state(UserCard, approveName,REPTILE_200, msg);
		}else if(errorCode.equals(REPTILE_2000) || errorCode.equals(REPTILE_5000) || errorCode.equals(REPTILE_6000) || errorCode.equals(REPTILE_8000)){
			PushState.state(UserCard, approveName,REPTILE_300);
		}
	}
	
	/**
	 * 状态推送
	 * @param uuid
	 * @param UserCard PushState.state()中入参UserCard
	 * @param approveName PushState.state()中入参approveName
	 * @param errorCode 状态码1000-9000
	 */
	public static void  sendState(String uuid,String UserCard,String approveName, String errorCode){
		String msg = StateUtil.getErrorMsg(errorCode);
		Map<String,Object> map = new HashMap<String, Object>();
		
		PushSocket.pushnew(map, uuid, errorCode, msg);
		if(errorCode.equals(REPTILE_1000)){
			PushState.state(UserCard, approveName,REPTILE_100);
		}else if(errorCode.equals(REPTILE_3000) || errorCode.equals(REPTILE_7000) || errorCode.equals(REPTILE_9000)){
			PushState.state(UserCard, approveName,REPTILE_200, msg);
		}else if(errorCode.equals(REPTILE_2000) || errorCode.equals(REPTILE_5000) || errorCode.equals(REPTILE_6000) || errorCode.equals(REPTILE_8000)){
			PushState.state(UserCard, approveName,REPTILE_300);
		}
	}
	
	/**
	 * 根据错误码获取错误信息
	 * @param errorCode
	 * @return
	 */
	public static String  getErrorMsg(String errorCode){
		
		return ErrorMsgUtil.getProperty(errorCode);
	}
	
	/**
	 * 根据错误码获取错误信息
	 * @param errorCode
	 * @param msg1
	 * @param msg2
	 * @return
	 */
	public static String  getErrorMsg(String errorCode,String msg1,String msg2){
		String str = ErrorMsgUtil.getProperty(errorCode);
		if(str.contains("{1}")){
			str = str.replace("{1}", msg1);
		}
		if(str.contains("{2}")){
			str = str.replace("{2}", msg2);
		}
		
		return str;
	}
	
	
	public static void main(String[] args) {
		sendState("2134", "2312", "wqre", "3000","淘宝","认证失败");
	}
}
