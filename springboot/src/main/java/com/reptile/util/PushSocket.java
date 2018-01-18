package com.reptile.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PushSocket {
    private static Logger logger = LoggerFactory.getLogger(PushSocket.class);


    /**
     * 0001 失败 0000 成功 1000登陆中
     *
     * @param map
     * @param UUID
     * @param resultCode
     * @param errorInfor 失败原因
     */
    public static Map<String, Object> pushnew(Map<String, Object> map, String UUID, String resultCode, String errorInfor) {

        logger.warn(UUID + "：本次认证流程，现处于阶段为：" + errorInfor);
        Map<String, Object> mapData = new HashMap<String, Object>();
        Session se = LongLink.getWsUserMap().get(UUID);
        String seq_id = LongLink.getWsInfoMap().get(UUID);
        System.out.println("se===" + se);
        System.out.println("seq===" + seq_id);
        System.out.println(errorInfor + resultCode);
        String date = currentTime();

        String actionType = "oldLink";
        if (UUID.contains("+")) {
            logger.warn("--------------------新版本长连接。本次连接目标为："+UUID+"--------actionType:"+actionType);
            actionType = (UUID.split("\\+"))[1];
        }

        try {
            if (se != null && seq_id != null) {
                se.getBasicRemote().sendText("{\"resultCode\":\"" + resultCode + "\",\"seq_id\":\"" + seq_id + "\",\"errorInfor\":\"" + errorInfor + "\",\"date\":\"" + date + "\",\"actionType\":\"" + actionType + "\"}");
            }
        } catch (Exception e) {
            logger.warn("------------------------推送状态时，长链接出现问题----------------------", e);
            map.put("errorCode", "1100");
            map.put("errorInfo", "推送状态时，连接已关闭");
        }
        return map;
    }

    /**
     * 获取当前时间（年月日时分秒）
     *
     * @return
     */
    public static String currentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
        String mon = format.format(new Date());
        return mon;
    }
}
