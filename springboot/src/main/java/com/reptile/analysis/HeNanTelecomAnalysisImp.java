package com.reptile.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeNanTelecomAnalysisImp implements ChinaTelecomAnalysisInterface {
	private static Logger logger = LoggerFactory.getLogger(HeNanTelecomAnalysisImp.class);

	@Override
	public List<Map<String, String>> analysisXml(List<String> data, String phoneNumber, String... agrs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, String>> analysisJson(List<String> data, String phoneNumber, String... agrs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, String>> analysisHtml(List<String> data, String phoneNumber, String... agrs) {
		// TODO Auto-generated method stub
		List<Map<String, String>> data1 = new ArrayList<Map<String, String>>();
			try {
				for (int i = 0; i < data.size(); i++) {
				String detail=data.get(i).toString();	
				Document parse = Jsoup.parse(new File("D://12.txt"), "utf-8");
				Elements body = parse.getElementsByTag("body");
				if (body.isEmpty()) {
					logger.warn("------河南电信-------:" + phoneNumber + "该用户第" + i + "次详单页面未捕捉到表单数据，data:" + parse.text());
				} else {
					Element element = body.get(0);
					Element tbody = element.getElementsByTag("tbody").get(1);
					// System.out.println(tbody);
					Elements trs = tbody.getElementsByTag("tr");
					if (trs.isEmpty()) {
					logger.warn("------河南电信-------:" + phoneNumber + "该用户第" + i + "次详单页面未捕捉到表单数据，data:" + parse.text());
					} else {
						for (int j = 0; j < trs.size(); j++) {
							Map<String, String> map = new HashMap<String, String>();
							Elements tds = trs.get(j).getElementsByTag("td");
							if (tds.isEmpty()) {
								logger.warn("------河南电信-------:" + phoneNumber + "该用户第" + j + "次详单页面未捕捉到表单数据，data:"+ parse.text());
							} else {
								String CallNumber = tds.get(1).text();
								// "通话开始时间", "CallTime": "10-07 11:29:57",
								String CallTime = tds.get(2).text();
								String substring = CallTime.substring(4);
								String b1 = substring.substring(0, 2);
								String b2 = substring.substring(2, 4);
								String b3 = substring.substring(4, substring.length());
								String dTime = b1 + '-' + b2 + b3;
								// 通话时长
								String CallDuration = tds.get(4).text();
								// 被叫号码
								map.put("CallNumber", tds.get(1).text());
								// 通话开始时间
								map.put("CallTime", dTime);
								// 时长(秒)
								map.put("CallDuration", getSeconds(CallDuration) + "秒");
								// "通话类型",
								map.put("CallType", "");
								// "费用(分)",
								map.put("CallMoney", tds.get(6).text());
								// "类型"
								map.put("CallWay", tds.get(5).text());
								// 归属地
								map.put("CallAddress", "");
								data1.add(map);
							}
						}
						

					}
				}
			}
			} catch (IOException e) {

				e.printStackTrace();
			}
		
		return data1;
	}

	/**
	 * 
	* @Title: getSeconds 
	* @Description: TODO(将00:00:00转化为秒) 
	* @param @param CallDuration
	* @param @return    设定文件 
	* @throws
	 */
	public static String getSeconds(String CallDuration) {
		String s = CallDuration;
		int index1 = s.indexOf(":");
		int index2 = s.indexOf(":", index1 + 1);
		int hh = Integer.parseInt(s.substring(0, index1));
		int mi = Integer.parseInt(s.substring(index1 + 1, index2));
		int ss = Integer.parseInt(s.substring(index2 + 1));
		String duration = hh * 60 * 60 + mi * 60 + ss + "";
		return duration;

	}

	public static void main(String[] args) {
		
		ChinaTelecomAnalysisInterface   henan=new  HeNanTelecomAnalysisImp();
		List<Map<String,String>> analysisHtml = henan.analysisHtml(null, null);
		System.out.println(analysisHtml);
	}

}
