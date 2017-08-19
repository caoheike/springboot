package com.reptile.springboot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    private static Logger logger = LoggerFactory.getLogger(Scheduler.class);
    public static String getIp = "http://http-api.taiyangruanjian.com/getip?num=1&type=2&pro=&city=0&yys=0&port=1&pack=1356&ts=1&ys=0&cs=0&lb=1&sb=0&pb=4&mr=1";
    private static int i = 0;
    public static String ip = "";
    public static int port = 0;
    public static String expire_time = "";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static boolean flag = true;

    static {
        Scheduler.sendGet(getIp);
        System.out.println(ip + ":" + port + "     time " + dateFormat.format(System.currentTimeMillis()) + " expire_time" + expire_time);
        logger.info(ip + ":" + port + "     time " + dateFormat.format(System.currentTimeMillis()) + " expire_time" + expire_time);
    }

    @Scheduled(fixedRate = 5000)
    public static void judeValid() throws Exception {
        if (flag) {
            Date date = dateFormat.parse(expire_time);
            if (date.getTime() - 60000 < System.currentTimeMillis()) {
                Scheduler.sendGet(getIp);
                System.out.println(ip + ":" + port + "     time " + dateFormat.format(System.currentTimeMillis()) + " expire_time" + expire_time);
                logger.info(ip + ":" + port + "     time " + dateFormat.format(System.currentTimeMillis()) + " expire_time" + expire_time);
            }
        }
    }


    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            JSONObject fromObject = JSONObject.fromObject(result);
            JSONArray fromObject2 = JSONArray.fromObject(fromObject.get("data"));
            ip = fromObject2.getJSONObject(0).getString("ip");
            port = Integer.parseInt(fromObject2.getJSONObject(0).getString("port"));
            expire_time = fromObject2.getJSONObject(0).getString("expire_time");
            logger.info(ip + ":" + port + "     time " + dateFormat.format(System.currentTimeMillis()) + " expire_time" + expire_time);
        } catch (Exception e) {
            flag=false;
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
