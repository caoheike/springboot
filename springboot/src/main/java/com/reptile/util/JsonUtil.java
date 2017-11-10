package com.reptile.util;

import java.util.Iterator;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

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
    
}
