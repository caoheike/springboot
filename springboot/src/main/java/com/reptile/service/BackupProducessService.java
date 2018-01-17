package com.reptile.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.reptile.util.ConstantInterface;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
/**
 * 伪流程
 * @author cui
 *
 */
@Service
public class BackupProducessService {
	private Logger logger= LoggerFactory.getLogger(BackupProducessService.class);
	/**
	 * 认证流程
	 * @param request
	 * @param phoneNumber 手机号
	 * @param servePwd  服务密码
	 * @param type  运营商类型
	 * @param uuid  
	 */
	public void identifyProduce(HttpServletRequest request,String phoneNumber,String servePwd,String type,String longitude,String latitude,String uuid){
		Map<String, Object> map = new HashMap<String, Object>(16);
		Map<String, Object> data = new HashMap<String, Object>(16);
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		try {
			Thread.sleep(2000);
			logger.warn("-----------------"+this.getType(type)+":"+phoneNumber+"登录中----------------------");
			PushSocket.pushnew(map, uuid, "1000", "登录中");
	        PushState.state(phoneNumber, "callLog", 100);
			Thread.sleep(2000);
			logger.warn("-----------------"+this.getType(type)+":"+phoneNumber+"登陆成功----------------------");
			PushSocket.pushnew(map, uuid, "2000", "登陆成功");
			Thread.sleep(2000);		
			logger.warn("-----------------"+this.getType(type)+":"+phoneNumber+"数据获取成功----------------------");
			PushSocket.pushnew(map, uuid, "6000", "数据获取成功");
			Thread.sleep(2000);
			//推送数据
			data =this.pushDate(phoneNumber, servePwd, type, longitude, latitude, data, list);
		    //根据data判断认证状态
		   if(data.get("errorCode").equals("0000")){
				logger.warn("-----------------"+this.getType(type)+":"+phoneNumber+"认证成功----------------------");
				PushSocket.pushnew(map, uuid, "8000", "认证成功");
				PushState.state(phoneNumber, "callLog", 300);
		   }else {
				logger.warn("-----------------"+this.getType(type)+":"+phoneNumber+"认证失败----------------------");
				PushSocket.pushnew(map, uuid, "7000", "认证失败");
				PushState.state(phoneNumber, "callLog", 200); 
		   }
		} catch (Exception e) {
			logger.error("-----------------"+this.getType(type)+":"+phoneNumber+"认证失败----------------------",e);
			PushSocket.pushnew(map, uuid, "7000", "认证失败");
			PushState.state(phoneNumber, "callLog", 200);
		}
	}
	/**
	 * 获取运营商类型
	 * @param type 运营商类型
	 * @return
	 */
   public String getType(String type){
	return type.equals("telecom")?"电信":(type.equals("unicom")?"联通":"移动");   
   }
   
   /**
    * 给不同运营商推送数据
    * @param phoneNumber 手机号
    * @param servePwd  服务密码
    * @param type   运营商类型
    * @param longitude  经度
    * @param latitude  纬度
    * @return
    */
   public Map<String, Object> pushDate(String phoneNumber,String servePwd,String type,String longitude,String latitude,Map<String, Object> data,List<Map<String,String>> list){
	    Resttemplate resttemplate = new Resttemplate();
	    data.put("longitude", longitude);
		data.put("latitude", latitude);
		data.put("data", list);
	   if(type.equals("telecom")||type.equals("unicom")){
		   data.put("UserIphone", phoneNumber);
		   data.put("UserPassword", servePwd);
		   if(type.equals("telecom")){//电信
			   data.put("flag", "100");
			   data = resttemplate.SendMessage(data, ConstantInterface.port + "/HSDC/message/telecomCallRecord"); 
		   }else{//联通
			   data = resttemplate.SendMessage(data, ConstantInterface.port + "/HSDC/message/linkCallRecord");
		   }   
	   }else{//移动phonBill
		   data.put("userPhone", phoneNumber);
		   data.put("serverCard", servePwd);
           data = resttemplate.SendMessage(data, ConstantInterface.port + "/HSDC/message/mobileCallRecord");
	   }
	return data;  
   }
}
