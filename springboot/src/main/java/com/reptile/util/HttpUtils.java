package com.reptile.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HotWong on 2017/07/24.
 */
public class HttpUtils {

    public static void sendPost(String url,String data) throws Exception {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(new BasicNameValuePair("data", data));
        UrlEncodedFormEntity entity;
        entity = new UrlEncodedFormEntity(paramsList, "UTF-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
        HttpEntity httpEntity = response.getEntity();
        if (httpEntity != null) {
            System.out.println("response:" + EntityUtils.toString(httpEntity, "UTF-8"));
        }
        closeableHttpClient.close();
    }
}
