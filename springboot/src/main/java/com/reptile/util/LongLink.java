package com.reptile.util;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2017/5/19.
 */
@Component
@ServerEndpoint("/hello")
public class LongLink {
    private Logger logger = LoggerFactory.getLogger(LongLink.class);
    private static Map<String, Session> wsUserMap = new HashMap<String, Session>();
    private static Map<String, String> wsInfoMap = new HashMap<String, String>();

    public static Map<String, String> getWsInfoMap() {
        return wsInfoMap;
    }

    public static void setWsInfoMap(Map<String, String> wsInfoMap) {
        LongLink.wsInfoMap = wsInfoMap;
    }

    public static Map<String, Session> getWsUserMap() {
        return wsUserMap;
    }

    public static void setWsUserMap(Map<String, Session> wsUserMap) {
        LongLink.wsUserMap = wsUserMap;
    }

    @OnOpen
    public void onopen(Session session) {

        System.out.println("连接成功");
    }

    @OnClose
    public void onclose(Session session) {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("本次连接已断开....");
    }

    @OnMessage
    public void onsend(Session session, String msg) {
        System.out.println("msg====" + msg);
        JSONObject json = new JSONObject();
        if (msg != null && !msg.equals("")) {
            try {
                System.out.println("msg====" + msg);
                session.getBasicRemote().sendText(msg.substring(0, msg.length() - 1).toString() + ",\"resultCode\":\"1111\"}");//链接成功
                json = JSONObject.fromObject(msg);
            } catch (Exception e) {
                logger.warn("长连接发送数据不合法:" + msg);
                e.printStackTrace();
            }

            Object flag = json.get("flag");
            msg = json.get("req").toString();
            //处理现已安装app的手机功能不能正常使用；
            if (flag == null) {
                logger.warn("这是旧版本推送，不含flag标志，未清除已关闭的session");
                wsUserMap.put(msg, session);
                wsInfoMap.put(msg, json.get("seq_id").toString());
            } else {
                String isopen = flag.toString();
                logger.warn("这是新版本推送，含flag标志,会清除已关闭的session，flag="+isopen);
                //是否对此连接执行关闭操作
                if (isopen.equals("open")) {
                    logger.warn("连接成功并持状态中:" + msg);
                    wsUserMap.put(msg, session);
                    wsInfoMap.put(msg, json.get("seq_id").toString());
                } else if (isopen.equals("close")) {
                    logger.warn("已移除此次连接:" + msg);
                    wsUserMap.remove(msg);
                    wsInfoMap.remove(msg);
                }
            }
        } else {
            logger.warn("长连接发送数据不全:" + msg);
            onclose(session);
        }
    }


}
