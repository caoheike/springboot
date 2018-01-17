package com.reptile.util;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisSourceUtil {
	
	private static Logger logger = LoggerFactory.getLogger(RedisSourceUtil.class);
	
	@Autowired
	private  StringRedisTemplate redisTemplate;
	
	/**
	 * 读取redis中的数据
	 * @param key
	 * @return
	 */
	public   String getValue(String key){
		String value = "";
		try {
			value = redisTemplate.opsForValue().get(key);
			logger.warn("------------获取数据成功。key:"+ key + ",value:"+ value +"-------------");
		} catch (Exception e) {
			logger.error("--------------获取数据失败，key:"+ key + "------------------",e);
		}
		return value;
 	}
	
	
	/**
	 * 保存数据
	 * @param key
	 * @param value
	 */
	public  boolean setValue(String key,String value,String time){
		   
		try {
			if( time != null && !"".equals(time)) {
				/**设置失效的新增**/
				redisTemplate.opsForValue().set(key,value, Integer.parseInt(time), TimeUnit.SECONDS);// 缓存有效期2秒
				logger.warn("--------------设置" + key + "成功!失效时长:" + value +"秒,值为:" + redisTemplate.opsForValue().get(key)+"-----------------");
			}else {
				/**不设置失效的新增**/
				redisTemplate.opsForValue().set(key, value);
				logger.warn(   "--------------设置" + key +"成功!不失效,值为:"+redisTemplate.opsForValue().get(key) +"-----------------");
			}
		} catch (Exception e) {
			logger.error("--------------保存数据失败，key:"+key+"   value:" + value + "------------------",e);
			return false;
		}
		 return true;
	}
	
}
