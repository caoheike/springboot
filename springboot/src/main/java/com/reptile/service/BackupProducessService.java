package com.reptile.service;
import java.util.HashMap;
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
	public void identifyProduce(HttpServletRequest request,String phoneNumber,String servePwd,String type,String uuid){
		Map<String, Object> map = new HashMap<String, Object>(16);
		Map<String, Object> data = new HashMap<String, Object>(16);
		try {
			logger.warn("-----------------"+this.getType(type)+":"+phoneNumber+"登录中----------------------");
			PushSocket.pushnew(map, uuid, "1000", "登录中");
	        PushState.state(phoneNumber, "callLog", 100);
			Thread.sleep(2000);
			logger.warn("-----------------"+this.getType(type)+":"+phoneNumber+"登陆成功----------------------");
			PushSocket.pushnew(map, uuid, "2000", "登陆成功");
			Thread.sleep(2000);		
			logger.warn("-----------------"+this.getType(type)+":"+phoneNumber+"数据获取成功----------------------");
			PushSocket.pushnew(map, uuid, "6000", "数据获取成功");
			Thread.sleep(500);
			//推送数据
		   data.put("phone", phoneNumber);
		   data.put("pwd", servePwd);
		   data.put("data", null);
		   Resttemplate resttemplate = new Resttemplate();
		   data = resttemplate.SendMessage(data, ConstantInterface.port + "/HSDC/message/"+this.getAdress(type));
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
    * 获取推送地址
    * @param type 运营商类型
    * @return
    */
   public String getAdress(String type){
		return type.equals("telecom")?"telecomCallRecord":(type.equals("unicom")?"linkCallRecord":"mobileCallRecord");   
	   }
}
