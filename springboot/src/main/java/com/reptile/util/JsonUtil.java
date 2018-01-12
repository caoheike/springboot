package com.reptile.util;

import java.util.Iterator;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;

public class JsonUtil {
	
	/**
	 * 返回json字符串中某个key的值
	 * @param str json字符串
	 * @param key 
	 * @return
	 */
	public static String getJsonValue(String str,String key){
			JSONObject jsonObject = JSONObject.fromObject(str);
			Iterator iterator = jsonObject.keys();
			String value = "";
			while(iterator.hasNext()){
		        	String key1 = (String) iterator.next();
		        	String value1 = jsonObject.getString(key1);
		        	if(key1.equals(key)){
		        		value = value1;
		        		break;
		        	}else{
		        		if(isGoodJson(value1)){
		        			value = getJsonValue(value1, key);
		        		}
		        	}
			}
			
			return value;
	}
	
	
	
	/**
	 * 返回json中某个key的值
	 * @param str json字符串
	 * @param key 
	 * @return
	 */
	public static Object getJsonValue1(Object str,String key){
		JSONObject jsonObject = JSONObject.fromObject(str);
		Iterator iterator = jsonObject.keys();
		Object value = new Object();
		while(iterator.hasNext()){
			Object key1 = iterator.next();
			Object value1 = jsonObject.get(key1);
			if(key1.equals(key)){
				value = value1;
				break;
			}else{
				if(isGoodJson(value1)){
					value = getJsonValue1(value1, key);
				}
			}
		}
		
		return value;
	}
	
	
	
	
	/**
	 * 判断Object是否为json格式
	 * @param json
	 * @return
	 */
	public static boolean isGoodJson(Object json) {  
		if(json == null || (json + "").equals("null")){
			return false;
		}
		try {  
			JSONObject.fromObject(json);
			return true;  
		} catch (Exception e) {  
			return false;  
		}  
	} 
	
	
	
	
	/**
	 * 判断字符串是否为json格式
	 * @param json
	 * @return
	 */
    public static boolean isGoodJson(String json) {  
        if (StringUtils.isBlank(json)) {  
            return false;  
        }  
        try {  
        	JSONObject.fromObject(json);
            return true;  
        } catch (Exception e) {  
            return false;  
        }  
    }  
 
    public static void main(String[] args) {
    	WebDriver phantomJsDriver = PhantomJs.phantomJsDriver();
    	phantomJsDriver.get("");
//    	phantomJsDriver.
    
    }
    
}
