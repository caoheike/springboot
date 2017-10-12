package com.reptile.util;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author bigyoung
 * @version 1.0
 * 
 *
 */
public class Resttemplate {
	/**
	 * 
	 * @param map 需要推送的数据
	 * @param Url 推送的地址
	 * @return 返回推送状态
	 */
  public Map<String,Object> SendMessage(Map<String,Object> map,String Url){
	  Map<String,Object> message=new HashMap<String, Object>();
	  try {
		  StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));  
		  RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build();  
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.MULTIPART_FORM_DATA);
          MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
          headers.setContentType(type);
          headers.add("Accept", MediaType.APPLICATION_JSON.toString());
          System.out.println(JSONObject.fromObject(map).toString()+"sssvvvv");
          HttpEntity<String> formEntity = new HttpEntity<String>(JSONObject.fromObject(map).toString(), headers);
          String result = restTemplate.postForObject(Url, formEntity,String.class);
          JSONObject jsonObject=JSONObject.fromObject(result);
          if(jsonObject.get("errorCode").equals("0000")){
        		message.put("errorCode","0000");
    			message.put("errorInfo","查询成功");
          }else{
        		message.put("errorCode",jsonObject.get("errorCode"));//异常处理
    			message.put("errorInfo",jsonObject.get("errorInfo"));
          }
           
		} catch (Exception e) {
			message.put("errorCode","0003");//异常处理
			message.put("errorInfo","推送失败");
		}
	  	return message;
	  
  }

  public Map<String,Object> SendMessage(JSONObject jsonObject,String Url,boolean flg){
	  Map<String,Object> message=new HashMap<String, Object>();
	  try {
		  StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));  
		  RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build();  
		  HttpHeaders headers = new HttpHeaders();
		  MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
		  headers.setContentType(type);
		  headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		  System.out.println(jsonObject);
		  HttpEntity<String> formEntity = new HttpEntity<String>(jsonObject.toString(), headers);
		  String result = restTemplate.postForObject(Url, formEntity,String.class);
		  JSONObject jsonObjects=JSONObject.fromObject(result);
		  if(jsonObjects.equals("0000")){
			  message.put("errorCode","0000");
			  message.put("errorInfo","查询成功");
		  }else{
			  message.put("errorCode",jsonObjects.get("errorCode"));//异常处理
			  message.put("errorInfo",jsonObjects.get("errorInfo"));
		  }
		  
	  } catch (Exception e) {
		  System.out.println(e);
		  message.put("errorCode","0003");//异常处理
		  message.put("errorCode","推送失败");
	  }
	  return message;
	  
  }
  
  //ludangwei 
  public Map<String,Object> SendMessageCredit(JSONObject jsonObject,String Url){
	  Map<String,Object> message=new HashMap<String, Object>();
	  try {
		  StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));  
		  RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build();  
		  HttpHeaders headers = new HttpHeaders();
		  MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
		  headers.setContentType(type);
		  headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		  System.out.println(jsonObject);
		  HttpEntity<String> formEntity = new HttpEntity<String>(jsonObject.toString(), headers);
		  String result = restTemplate.postForObject(Url, formEntity,String.class);
		  JSONObject jsonObjects=JSONObject.fromObject(result);
		  if("0".equals(jsonObjects.get("errorCode").toString())){
			  message.put("ResultCode","0000");
			  message.put("ResultInfo","查询成功");
		  }else{
			  message.put("ResultCode",jsonObjects.get("errorCode"));//异常处理
			  message.put("ResultInfo",jsonObjects.get("errorInfo"));
		  }
		  
	  } catch (Exception e) {
		  System.out.println(e);
		  message.put("ResultCode","0003");//异常处理
		  message.put("ResultInfo","推送失败");
	  }
	  return message;
	  
  }
	//ludangwei
	public Map<String,Object> SendSDYDMessage(Map<String,Object> map,String Url){
		Map<String,Object> message=new HashMap<String, Object>();
		try {
			StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));
			RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
			headers.setContentType(type);
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			System.out.println(map.toString()+"sssvvvv");
			HttpEntity<String> formEntity = new HttpEntity<String>(map.toString(), headers);
			String result = restTemplate.postForObject(Url, formEntity,String.class);
			JSONObject jsonObject=JSONObject.fromObject(result);
			if(jsonObject.get("errorCode").equals("0000")){
				message.put("errorCode","0000");
				message.put("errorInfo","查询成功");
			}else{
				message.put("errorCode",jsonObject.get("errorCode"));//异常处理
				message.put("errorInfo",jsonObject.get("errorInfo"));
			}

		} catch (Exception e) {
			message.put("errorCode","0003");//异常处理
			message.put("errorInfo","操作失败");
		}
		return message;

	}
  		 
  
  
  public Map<String,Object> SendMessageCredit(JSONArray jsonarray,String Url){
	  Map<String,Object> message=new HashMap<String, Object>();
	  try {
		  StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));  
		  RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build();  
		  HttpHeaders headers = new HttpHeaders();
		  MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
		  headers.setContentType(type);
		  headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		  System.out.println(jsonarray);
		  HttpEntity<String> formEntity = new HttpEntity<String>(jsonarray.toString(), headers);
		  String result = restTemplate.postForObject(Url, formEntity,String.class);
		  JSONObject jsonObjects=JSONObject.fromObject(result);
		  if("0".equals(jsonObjects.get("errorCode").toString())){
			  message.put("ResultCode","0000");
			  message.put("ResultInfo","查询成功");
		  }else{
			  message.put("ResultCode",jsonObjects.get("errorCode"));//异常处理
			  message.put("ResultInfo",jsonObjects.get("errorInfo"));
		  }
		  
	  } catch (Exception e) {
		  System.out.println(e);
		  message.put("ResultCode","0003");//异常处理
		  message.put("ResultInfo","推送失败");
	  }
	  return message;
	  
  }
  public Map<String,Object> SendMessage(Map<String,Object> map,String Url,String card){
	  Map<String,Object> message=new HashMap<String, Object>();
	  try {
		  StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));  
		  RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build();  
		  HttpHeaders headers = new HttpHeaders();
		  headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		  MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
		  headers.setContentType(type);
		  headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		  System.out.println(JSONObject.fromObject(map).toString()+"sssvvvv");
		  HttpEntity<String> formEntity = new HttpEntity<String>(JSONObject.fromObject(map).toString(), headers);
		  String result = restTemplate.postForObject(Url, formEntity,String.class);
		  JSONObject jsonObject=JSONObject.fromObject(result);
		  if(jsonObject.get("errorCode").equals("0000")){
			  message.put("errorCode","0000");
			  message.put("errorInfo","查询成功");
	            PushState.state(card, "CHSI",300);
		  }else if(jsonObject.get("errorCode").equals("1111")){
			  message.put("errorCode","0001");
			  message.put("errorInfo","认证失败，暂无学历信息");
	           PushState.state(card, "CHSI",200);
		  } else{
			  message.put("errorCode",jsonObject.get("errorCode"));//异常处理
			  message.put("errorInfo",jsonObject.get("errorInfo"));
	           PushState.state(card, "CHSI",200);
		  }
		  
	  } catch (Exception e) {
		  message.put("errorCode","0003");//异常处理
		  message.put("errorInfo","推送失败");
          PushState.state(card, "CHSI",200);
	  }
	  return message;
	  
  }

}
