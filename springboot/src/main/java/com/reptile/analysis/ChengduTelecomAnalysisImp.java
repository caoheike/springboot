package com.reptile.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reptile.util.HttpURLConection;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;

import net.sf.json.JSONObject;

public class ChengduTelecomAnalysisImp implements ChinaTelecomAnalysisInterface {
    private Logger logger = LoggerFactory.getLogger(ChengduTelecomAnalysisImp.class);

    @Override
    public List<Map<String,String>>analysisXml(List<String> data, String phoneNumber, String... agrs) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String,String>> analysisJson(List<String> data, String phoneNumber, String... agrs) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String,String>> analysisHtml(List<String> data, String phoneNumber, String... agrs) {
        List<Map<String, String>> data1 = new ArrayList<Map<String, String>>();
        try {
            List list = data;
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                List list1 = (List) map.get("retInfo");
                for (int j = 0; j < list1.size(); j++) {
                    Map<String, String> detailed = new HashMap<String, String>();
                    Map map2 = (Map) list1.get(j);

                    //CallWay=呼叫类型
                    String CallWay = (String) map2.get("CALL_TYPE");
                    //CallAddress=通话号码归属地
                    String CallAddress = (String) map2.get("CALLED_CITYCODE");
                    //CallType=通话号码归属地类型
                    String CallType = (String) map2.get("CALCUNIT");
                    //CallMoney=呼叫费用
                    String CallMoney = (String) map2.get("MONEY");
                    //CallTime=呼叫时间
                    String CallTime = (String) map2.get("START_TIME");
                    //CallDuration=呼叫时常
                    String CallDuration = (String) map2.get("TIMELONG");
                    //CallNumber=通话号码
                    String CallNumber = (String) map2.get("OTHERPHONE");
                    detailed.put("CallWay", CallWay);
                    detailed.put("CallAddress", CallAddress);
                    detailed.put("CallType", CallType);
                    detailed.put("CallMoney", CallMoney);
                    detailed.put("CallTime", CallTime);
                    detailed.put("CallDuration", CallDuration);
                    detailed.put("CallNumber", CallNumber);
                    data1.add(detailed);
                }
            }
        } catch (Exception e) {
            logger.warn("---------成都电信解析：" + phoneNumber + "data:" + data1 + "---------------------------");
        }


        return data1;
    }
}
