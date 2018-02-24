package com.reptile.analysis.chinamobileanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JiangSuTelecomAnalysisImp implements ChinaTelecomAnalysisInterface {
	private static Logger logger = LoggerFactory.getLogger(JiangSuTelecomAnalysisImp.class);

	@Override
	public List<Map<String, String>> analysisXml(List<String> data, String phoneNumber, String... agrs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, String>> analysisJson(List<String> data, String phoneNumber, String... agrs) {
		List<Map<String, String>> data1 = new ArrayList<Map<String, String>>();
		String str="[{'resultFlag':'getVoiceInterfaceMsg','durationSecond':'157','iresult':'0','items':[{'startTime':'20180207125141','startTimeNew':'12:51:41','ticketChargeCh':'0.00','accNbr':'13913892901','duration':'3','durationCh':'00:02:37','areaCode':'021','ticketTypeNew':'被叫','ticketTypeId':'2','ticketType':'被叫-国内通话','ticketNumber':'1','durationType':'国内通话','productName':'CDMA','startDateNew':'2018-02-07','productId':'100000379'},{'startTime':'20180207125141','startTimeNew':'12:51:41','ticketChargeCh':'0.00','accNbr':'13913892901','duration':'3','durationCh':'00:02:37','areaCode':'021','ticketTypeNew':'被叫','ticketTypeId':'2','ticketType':'被叫-国内通话','ticketNumber':'1','durationType':'国内通话','productName':'CDMA','startDateNew':'2018-02-07','productId':'100000379'}],'durationSecondCnt':'41437','changeCntch':'0.00','durationCnt':'833','duartionCntCh':'11:30:37','smsg':'成功'},{'resultFlag':'getVoiceInterfaceMsg','durationSecond':'157','iresult':'0','items':[{'startTime':'20180207125141','startTimeNew':'12:51:41','ticketChargeCh':'0.00','accNbr':'13913892901','duration':'3','durationCh':'00:02:37','areaCode':'021','ticketTypeNew':'被叫','ticketTypeId':'2','ticketType':'被叫-国内通话','ticketNumber':'1','durationType':'国内通话','productName':'CDMA','startDateNew':'2018-02-07','productId':'100000379'},{'startTime':'20180207125141','startTimeNew':'12:51:41','ticketChargeCh':'0.00','accNbr':'13913892901','duration':'3','durationCh':'00:02:37','areaCode':'021','ticketTypeNew':'被叫','ticketTypeId':'2','ticketType':'被叫-国内通话','ticketNumber':'1','durationType':'国内通话','productName':'CDMA','startDateNew':'2018-02-07','productId':'100000379'}],'durationSecondCnt':'41437','changeCntch':'0.00','durationCnt':'833','duartionCntCh':'11:30:37','smsg':'成功'},{'resultFlag':'getVoiceInterfaceMsg','durationSecond':'157','iresult':'0','items':[{'startTime':'20180207125141','startTimeNew':'12:51:41','ticketChargeCh':'0.00','accNbr':'13913892901','duration':'3','durationCh':'00:02:37','areaCode':'021','ticketTypeNew':'被叫','ticketTypeId':'2','ticketType':'被叫-国内通话','ticketNumber':'1','durationType':'国内通话','productName':'CDMA','startDateNew':'2018-02-07','productId':'100000379'},{'startTime':'20180207125141','startTimeNew':'12:51:41','ticketChargeCh':'0.00','accNbr':'13913892901','duration':'3','durationCh':'00:02:37','areaCode':'021','ticketTypeNew':'被叫','ticketTypeId':'2','ticketType':'被叫-国内通话','ticketNumber':'1','durationType':'国内通话','productName':'CDMA','startDateNew':'2018-02-07','productId':'100000379'}],'durationSecondCnt':'41437','changeCntch':'0.00','durationCnt':'833','duartionCntCh':'11:30:37','smsg':'成功'},{'resultFlag':'getVoiceInterfaceMsg','durationSecond':'157','iresult':'0','items':[{'startTime':'20180207125141','startTimeNew':'12:51:41','ticketChargeCh':'0.00','accNbr':'13913892901','duration':'3','durationCh':'00:02:37','areaCode':'021','ticketTypeNew':'被叫','ticketTypeId':'2','ticketType':'被叫-国内通话','ticketNumber':'1','durationType':'国内通话','productName':'CDMA','startDateNew':'2018-02-07','productId':'100000379'},{'startTime':'20180207125141','startTimeNew':'12:51:41','ticketChargeCh':'0.00','accNbr':'13913892901','duration':'3','durationCh':'00:02:37','areaCode':'021','ticketTypeNew':'被叫','ticketTypeId':'2','ticketType':'被叫-国内通话','ticketNumber':'1','durationType':'国内通话','productName':'CDMA','startDateNew':'2018-02-07','productId':'100000379'}],'durationSecondCnt':'41437','changeCntch':'0.00','durationCnt':'833','duartionCntCh':'11:30:37','smsg':'成功'}]";
		JSONArray jsonArray2 = JSONArray.fromObject(str);
		for (Object obj : jsonArray2) {
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray jsonArray = jsonObject.getJSONArray("items");
			if (jsonArray == null && jsonArray.size() < 0) {
				logger.warn("------江苏电信-------:" + phoneNumber + "该用户第未捕捉到表单数据，data:" + jsonArray.size());
			} else {
				for (int j = 0; j < jsonArray.size(); j++) {
					Map<String, String> detail = new HashMap<String, String>();
					JSONObject obj1 = jsonArray.getJSONObject(j);
					// 通话时间
					String durationCh = obj1.getString("durationCh");
					// 被叫号码的区号
					// String s6 = obj1.getString("areaCode");
					// 费用
					detail.put("CallMoney", obj1.getString("ticketChargeCh"));
					// 地址
					detail.put("CallAddress", "");
					// 被叫-国内通话
					detail.put("CallType", obj1.getString("ticketType"));
					// 开始时间
					detail.put("CallTime", obj1.getString("startTimeNew"));
					// 通话时长
					detail.put("CallDuration", getSeconds(durationCh));
					// 呼叫类型
					detail.put("CallWay", obj1.getString("ticketTypeNew"));
					// 被叫号码
					detail.put("CallNumber", obj1.getString("accNbr"));
					data1.add(detail);
				}

			}

		}

		return data1;
	}

	@Override
	public List<Map<String, String>> analysisHtml(List<String> data, String phoneNumber, String... agrs) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	* @Title: getSeconds 
	* @Description: TODO(将00:00:00转化为秒) 
	* @param @param durationCh
	* @param @return    设定文件 
	* @throws
	 */
	public static String getSeconds(String durationCh) {
		String s = durationCh;
		int index1 = s.indexOf(":");
		int index2 = s.indexOf(":", index1 + 1);
		int hh = Integer.parseInt(s.substring(0, index1));
		int mi = Integer.parseInt(s.substring(index1 + 1, index2));
		int ss = Integer.parseInt(s.substring(index2 + 1));
		String duration = hh * 60 * 60 + mi * 60 + ss + "";
		return duration + "秒";

	}

	public static void main(String[] args) {
		ChinaTelecomAnalysisInterface jiangsu = new JiangSuTelecomAnalysisImp();
		List<Map<String, String>> analysisJson = jiangsu.analysisJson(null, null);
		System.out.println(analysisJson);
	}
}
