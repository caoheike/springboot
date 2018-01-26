package com.reptile.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * 接口调用方法
 * @author Administrator
 *
 */
public class HttpURLConection {
	//post提交
	public static String sendPost(Map<String,String> map, String path){
		String par="";
		String msg = "";
		try {
			if(map!=null){
				Iterator<String> iter = map.keySet().iterator(); 
			    while(iter.hasNext()){ 
			        String key=iter.next(); 
			        String value = map.get(key);
			       par=par+key+"="+value+"&";
			    }
			    par=par.substring(0,par.length()-1);
			}
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 使用 URL 连接进行输出，则将 DoOutput标志设置为 true  
			conn.setDoOutput(true);  
			conn.setRequestMethod("POST");  
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			// 向服务端发送key = value对
			out.write(par);
			out.flush();
			out.close();
			// 如果请求响应码是200，则表示成功  
			if (conn.getResponseCode() == 200) {  
			    // HTTP服务端返回的编码是UTF-8,故必须设置为UTF-8,保持编码统一,否则会出现中文乱码  
			    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));  
			    msg = in.readLine();  
			    in.close();  
			}  
			conn.disconnect();// 断开连接  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return msg;  
    }
	//get提交
	public static String sendGet(String src) {
        try {
            URL url = new URL(src);    // 把字符串转换为URL请求地址
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
            connection.connect();// 连接会话
            // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {// 循环读取流
                sb.append(line);
            }
            br.close();// 关闭流
            connection.disconnect();// 断开连接
            System.out.println(sb.toString());
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("失败!");
        }
        return null;
    }
	
	public static String sendPostOffline(Map<String,String> map, String path){
		String par="";
		String msg = "";
		try {
			if(map!=null){
				Iterator<String> iter = map.keySet().iterator(); 
			    while(iter.hasNext()){ 
			        String key=iter.next(); 
			        String value = map.get(key);
			       par=par+key+"="+value+"&";
			    }
			    par=par.substring(0,par.length()-1);
			}
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 使用 URL 连接进行输出，则将 DoOutput标志设置为 true  
			conn.setRequestProperty("sign", "TRqLO8ARYNdG9x7YGQkzVyBAZD4c37hRiffKjsH4N7hq8IR/+Ao55lag72JNg7SRX8A7HROOxyfTjLFDbAC1xw==");
			conn.setDoOutput(true);  
			conn.setRequestMethod("POST");  
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			// 向服务端发送key = value对
			out.write(par);
			out.flush();
			out.close();
			// 如果请求响应码是200，则表示成功  
			if (conn.getResponseCode() == 200) {  
			    // HTTP服务端返回的编码是UTF-8,故必须设置为UTF-8,保持编码统一,否则会出现中文乱码  
			    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));  
			    msg = in.readLine();  
			    in.close();  
			}  
			conn.disconnect();// 断开连接  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return msg;  
    }
}
