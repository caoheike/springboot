package com.reptile.util;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 朱洪郑
 * 通用请求类
 * 
 *
 */
public class SimpleHttpClient {

    private static CloseableHttpClient httpClient;
    
    private static CookieStore cookieStore;

    static {
        cookieStore  = new BasicCookieStore();

        // 将CookieStore设置到httpClient中
        httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }
    
    public static String getCookie(String name){
        List<Cookie> cookies =  cookieStore.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equalsIgnoreCase(name)){
                return cookie.getValue();
            }
        }
        return null;
        
    }

    /**
     * GET 请求
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String get(String url) throws ClientProtocolException, IOException {
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        return EntityUtils.toString(httpResponse.getEntity());
    }
    
    public static String get(String url,Map<String,String> headers) throws ClientProtocolException, IOException {
    	HttpGet httpGet = new HttpGet(url);
    	for(Map.Entry<String, String> entry : headers.entrySet()){
    		httpGet.addHeader(entry.getKey(), entry.getValue());
        }
    	HttpResponse httpResponse = httpClient.execute(httpGet);
    	return EntityUtils.toString(httpResponse.getEntity());
    }

    /**
     * POST 请求
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static String post(String url, Map<String, Object> params, Map<String, String> headers) throws ParseException, IOException{
        HttpPost httpPost = new HttpPost(url);
        UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(params), "UTF-8");
        httpPost.setEntity(postEntity);
        if(headers != null){
            addHeaders(httpPost, headers);
        }
        HttpResponse httpResponse = httpClient.execute(httpPost);
        return EntityUtils.toString(httpResponse.getEntity());
    }

    /**
     * 参数
     * @param parameterMap
     * @return
     */
    private static List<NameValuePair> getParam(Map<String, Object> parameterMap) {
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        for(Map.Entry<String, Object> entry : parameterMap.entrySet()){
            param.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        return param;
    }
    
    /**
     * 添加头
     * @param httpRequest
     * @param headerMap
     */
    private static void addHeaders(HttpEntityEnclosingRequestBase httpRequest, Map<String, String> headerMap){
        for(Map.Entry<String, String> entry : headerMap.entrySet()){
            httpRequest.addHeader(entry.getKey(), entry.getValue());
        }
    }
}