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

public class ChengduTelecomAnalysisImp  implements ChinaTelecomAnalysisInterface{
	private Logger logger = LoggerFactory.getLogger(ChengduTelecomAnalysisImp.class);
						public static void main(String[] args) {
							 String a="{'flag':'1','data':[{'retInfo':[{'CALL_TYPE':'主叫','ACC_NUMBER':'17711307710','OTHERPHONE':'10000','START_TIME':'2018-01-06 19:14:32','TIMELONG':'6','MONEY':'0.00','CT_MONEY':'0.00','OTHER_MONEY':'0.00','YH_MONEY':'0.00','DEGREE':'0.00','CALCUNIT':'本地市话','WANDER_STATE':'0.0','CALLED_CITYCODE':'','BILLING_AREA':'陕西西安'},{'CALL_TYPE':'主叫','ACC_NUMBER':'17711307710','OTHERPHONE':'10000','START_TIME':'2018-01-06 19:15:21','TIMELONG':'221','MONEY':'0.00','CT_MONEY':'0.00','OTHER_MONEY':'0.00','YH_MONEY':'0.00','DEGREE':'0.00','CALCUNIT':'本地市话','WANDER_STATE':'0.0','CALLED_CITYCODE':'','BILLING_AREA':'陕西西安'},{'CALL_TYPE':'被叫','ACC_NUMBER':'17711307710','OTHERPHONE':'15129551566','START_TIME':'2018-01-12 19:17:31','TIMELONG':'14','MONEY':'0.00','CT_MONEY':'0.00','OTHER_MONEY':'0.00','YH_MONEY':'0.00','DEGREE':'0.00','CALCUNIT':'国内通话被叫','WANDER_STATE':'0.0','CALLED_CITYCODE':'陕西渭南','BILLING_AREA':'陕西西安'},{'CALL_TYPE':'主叫','ACC_NUMBER':'17711307710','OTHERPHONE':'15129551566','START_TIME':'2018-01-12 19:19:18','TIMELONG':'31','MONEY':'0.00','CT_MONEY':'0.00','OTHER_MONEY':'0.00','YH_MONEY':'0.00','DEGREE':'0.00','CALCUNIT':'国内通话主叫','WANDER_STATE':'0.0','CALLED_CITYCODE':'陕西渭南','BILLING_AREA':'陕西西安'},{'CALL_TYPE':'被叫','ACC_NUMBER':'17711307710','OTHERPHONE':'15129551566','START_TIME':'2018-01-17 18:30:53','TIMELONG':'35','MONEY':'0.00','CT_MONEY':'0.00','OTHER_MONEY':'0.00','YH_MONEY':'0.00','DEGREE':'0.00','CALCUNIT':'国内通话被叫','WANDER_STATE':'0.0','CALLED_CITYCODE':'陕西渭南','BILLING_AREA':'陕西西安'},{'CALL_TYPE':'主叫','ACC_NUMBER':'17711307710','OTHERPHONE':'18789494202','START_TIME':'2018-01-19 12:04:56','TIMELONG':'17','MONEY':'0.00','CT_MONEY':'0.00','OTHER_MONEY':'0.00','YH_MONEY':'0.00','DEGREE':'0.00','CALCUNIT':'国内通话主叫','WANDER_STATE':'0.0','CALLED_CITYCODE':'陕西西安','BILLING_AREA':'陕西西安'},{'CALL_TYPE':'被叫','ACC_NUMBER':'17711307710','OTHERPHONE':'02963688251','START_TIME':'2018-01-21 10:23:11','TIMELONG':'42','MONEY':'0.00','CT_MONEY':'0.00','OTHER_MONEY':'0.00','YH_MONEY':'0.00','DEGREE':'0.00','CALCUNIT':'国内通话被叫','WANDER_STATE':'0.0','CALLED_CITYCODE':'陕西西安','BILLING_AREA':'陕西西安'},{'CALL_TYPE':'被叫','ACC_NUMBER':'17711307710','OTHERPHONE':'15129551566','START_TIME':'2018-01-21 16:37:37','TIMELONG':'50','MONEY':'0.00','CT_MONEY':'0.00','OTHER_MONEY':'0.00','YH_MONEY':'0.00','DEGREE':'0.00','CALCUNIT':'国内通话被叫','WANDER_STATE':'0.0','CALLED_CITYCODE':'陕西渭南','BILLING_AREA':'陕西西安'},{'CALL_TYPE':'被叫','ACC_NUMBER':'17711307710','OTHERPHONE':'15129551566','START_TIME':'2018-01-21 22:35:59','TIMELONG':'34','MONEY':'0.00','CT_MONEY':'0.00','OTHER_MONEY':'0.00','YH_MONEY':'0.00','DEGREE':'0.00','CALCUNIT':'国内通话被叫','WANDER_STATE':'0.0','CALLED_CITYCODE':'陕西渭南','BILLING_AREA':'陕西西安'}],'SUMMONEY':'0.00','SUMTIMELONG':'450'}],'latitude':'34.249387','UserPassword':'612495','UserIphone':'17711307710','longitude':'108.944332'}";
							 JSONObject test = JSONObject.fromObject(a); 
							 List list=(List) test.get("data");
							 List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
							 for (int i = 0; i < list.size(); i++) {
								 Map map= (Map) list.get(i);
								 List list1=   (List) map.get("retInfo");
								 for (int j = 0; j < list1.size(); j++) {
								Map<String,Object> detailed=new HashMap<String,Object>();
								Map map2=(Map) list1.get(j);
								String	CallWay=(String) map2.get("CALL_TYPE");//CallWay=呼叫类型
								String	CallAddress=	(String) map2.get("CALLED_CITYCODE");//CallAddress=通话号码归属地
								String	CallType=(String) map2.get("CALCUNIT");//CallType=通话号码归属地类型
								String	CallMoney=(String) map2.get("MONEY");//CallMoney=呼叫费用
								String	CallTime=(String) map2.get("START_TIME");//CallTime=呼叫时间
								String	CallDuration=(String) map2.get("TIMELONG");//CallDuration=呼叫时常
								String	CallNumber=(String) map2.get("OTHERPHONE");//CallNumber=通话号码
								detailed.put("CallWay",CallWay );
								detailed.put("CallAddress",CallAddress );
								detailed.put("CallType", CallType);
								detailed.put("CallMoney",CallMoney );
								detailed.put("CallTime",CallTime );
								detailed.put("CallDuration",CallDuration );
								detailed.put("CallNumber", CallNumber);
								data.add(detailed);
								}
							}
							 System.out.println("=data==="+data+"==data=");
							 Map<String, Object> dataMap = new HashMap<String, Object>();
							 
							 dataMap.put("phone", test.get("UserIphone"));
							 String UserIphone=(String) test.get("UserIphone");
				             dataMap.put("pwd", test.get("UserPassword"));
				                //经度
				             dataMap.put("longitude", test.get("longitude"));
				                //纬度
				             dataMap.put("latitude", test.get("latitude"));
				             dataMap.put("data", data);
				             JSONObject json=JSONObject.fromObject(dataMap);
			                 System.out.println(json);
			                 Map<String, String> maps=new HashMap<String, String>();
			                 maps.put("data", json.toString());
			                 String message=HttpURLConection.sendPost(maps, "http://192.168.3.4:8088/HSDC/message/operator");
			                 System.out.println("返回====="+message);
			                 JSONObject b = JSONObject.fromObject(message); 
			                 System.out.println(b);
						}
						@Override
						public Map<String, Object> analysisXml(List<String> data, String phoneNumber, String... agrs) {
							// TODO Auto-generated method stub
							return null;
						}
						@Override
						public Map<String, Object> analysisJson(List<String> data, String phoneNumber, String... agrs) {
							// TODO Auto-generated method stub
							return null;
						}
						@Override
						public Map<String, Object> analysisHtml(List<String> data, String phoneNumber, String... agrs) {
							List<Map<String,Object>> data1=new ArrayList<Map<String,Object>>();
						try {
							 List list=data;
							 for (int i = 0; i < list.size(); i++) {
								 Map map= (Map) list.get(i);
								 List list1=   (List) map.get("retInfo");
								 for (int j = 0; j < list1.size(); j++) {
									 Map<String,Object> detailed=new HashMap<String,Object>();
									 Map map2=(Map) list1.get(j);
									 String	CallWay=(String) map2.get("CALL_TYPE");//CallWay=呼叫类型
									 String	CallAddress=	(String) map2.get("CALLED_CITYCODE");//CallAddress=通话号码归属地
									 String	CallType=(String) map2.get("CALCUNIT");//CallType=通话号码归属地类型
									 String	CallMoney=(String) map2.get("MONEY");//CallMoney=呼叫费用
									 String	CallTime=(String) map2.get("START_TIME");//CallTime=呼叫时间
									 String	CallDuration=(String) map2.get("TIMELONG");//CallDuration=呼叫时常
									 String	CallNumber=(String) map2.get("OTHERPHONE");//CallNumber=通话号码
									 detailed.put("CallWay",CallWay );
									 detailed.put("CallAddress",CallAddress );
									 detailed.put("CallType", CallType);
									 detailed.put("CallMoney",CallMoney );
									 detailed.put("CallTime",CallTime );
									 detailed.put("CallDuration",CallDuration );
									 detailed.put("CallNumber", CallNumber);
									 data1.add(detailed);
								}
							}
						} catch (Exception e) {
							   logger.warn("---------成都电信解析："+phoneNumber+"data:"+data1+"---------------------------");
						}	
							
							 Map<String,Object> data2=new HashMap<String,Object>();
							 data2.put("data", data1);
							return data2;
						}
}
