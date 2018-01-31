package com.reptile.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 浙江电信（发包版）解析
 * @author 崔
 *
 */
public class ZJTelecomAnalysisImp implements ChinaTelecomAnalysisInterface{
	private Logger logger = LoggerFactory.getLogger(ZJTelecomAnalysisImp.class);

	@Override
	public  List<Map<String,String>> analysisXml(List<String> data,
			String phoneNumber, String... agrs) {
				return null;
		
	}

	@Override
	public  List<Map<String,String>> analysisJson(List<String> data,
			String phoneNumber, String... agrs) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 浙江电信解析
	 */
	@Override
	public  List<Map<String,String>> analysisHtml(List<String> data,
			String phoneNumber, String... agrs) {

		   Map<String, Object> dataMap=new HashMap<String, Object>();
		   List<Map<String, String>> list=new ArrayList<Map<String,String>>();
		   String[] sfm=new String[3];
		   int[] sfmNew=new int[3];
		   String CallDuration="";
		   String CallMoney="";
		   for (int i = 0; i < data.size(); i++) {
			   String detail=data.get(i).toString();
			   Document doc=Jsoup.parse(detail);
			   Element detailDiv= doc.getElementById("Pzone_details_content_2");
			   Element table=detailDiv.getElementsByTag("table").get(0);
			   if (table==null) {
				   logger.warn("---------浙江电信解析："+phoneNumber+"第"+i+"次详单页面未捕捉到数据,data:"+doc+"---------------------------");
			   }else {
				   Elements trs=table.getElementsByTag("tr");
				   if (trs==null) {
					   logger.warn("---------浙江电信解析："+phoneNumber+"第"+i+"次详单页面未捕捉到数据,data:"+doc+"---------------------------");
				   }else {
					   for (int j = 2; j < trs.size(); j++) {
						   Elements tds=trs.get(j).getElementsByTag("td");
						   if (tds==null) {
							   logger.warn("---------浙江电信解析："+phoneNumber+"第"+i+"次第"+j+"行详单页面未捕捉到数据,data:"+doc+"---------------------------");
						    }else {
						    	   Map<String, String> map=new HashMap<String, String>();
								   map.put("CallNumber", tds.get(1).text());//对方号码   CallNumber
								   map.put("CallWay", tds.get(2).text());//呼叫类型     CallWay
								   map.put("CallTime", tds.get(3).text().substring(5));//通话日期起始时间   CallTime
								   CallDuration=getCallDuration(tds.get(4).text(), sfm, sfmNew);
								   map.put("CallDuration", CallDuration);//通话时长     CallDuration    
								   map.put("CallAddress",  tds.get(5).text());//通话地  CallAddress
								   map.put("CallType", tds.get(6).text());//通话类型 CallType
								   if (tds.get(9).text()!=null&&!tds.get(9).text().equals("")) {
									   CallMoney=new Double(tds.get(9).text())+"";
								   }else {
									   CallMoney="";
								   }
								   map.put("CallMoney",CallMoney);//费用小计  CallMoney
								   list.add(map);
							}
						   
					   }
				}
			  }
		   }
		   //把解析好的数据存入dataMap
//		   dataMap=setValue(dataMap, phoneNumber, agrs[0], agrs[1], agrs[2], list);
		   logger.warn("-----------------------浙江电信"+phoneNumber+"，数据解析完成,data:"+list.toString()+"-----------------");
		return list;
	}
	/**
	 * 解析好的数据存放在data
	 * @param dataMap
	 * @param phone 手机号
	 * @param pwd 服务密码
	 * @param longitude 经度
	 * @param latitude 纬度
	 * @param list 数据
	 * @return
	 */
	public Map<String, Object> setValue(Map<String, Object> dataMap,String phone,String pwd,String longitude,String latitude,List<Map<String, String>> list) { 
	   dataMap.put("phone", phone);//手机号
	   dataMap.put("pwd", pwd);
	   dataMap.put("longitude", longitude);
	   dataMap.put("latitude", latitude);
	   dataMap.put("data", list);
	return dataMap;
	}
	   /**
	    * 获取通话时长 如：3秒
	    * @param callDuration  callDuration 00:00:00格式
	    * @param sfm  获取到的时分秒
	    * @param sfmNew  把时分秒转化成int后的结果
	    * @return
	    */
	   public String getCallDuration(String callDuration, String[] sfm,int[] sfmNew){
		   if (callDuration!=null&&!callDuration.equals("")) {
			   sfm=callDuration.split(":");
			   for (int i = 0; i < sfm.length; i++) {
				   sfmNew[i]=new Integer(sfm[i]);
			     }
		   }
		return (sfmNew[0]*3600+sfmNew[1]*60+sfmNew[2])+"秒"; 
	   }

}
