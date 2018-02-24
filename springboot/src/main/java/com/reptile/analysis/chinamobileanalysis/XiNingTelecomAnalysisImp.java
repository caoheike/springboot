package com.reptile.analysis.chinamobileanalysis;

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

/**
 * 
 * @ClassName: XiNingTelecomAnalysisImp
 * @Description: 西宁电信解析类
 * @author duwei
 * @date 2018年2月5日 下午3:16:47
 *
 */
public class XiNingTelecomAnalysisImp implements ChinaTelecomAnalysisInterface {
	private static Logger logger = LoggerFactory.getLogger(XiNingTelecomAnalysisImp.class);

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
		List<Map<String, String>> data1 = new ArrayList<Map<String, String>>();
		Document parse;
		try {
		for (int i = 0; i < data.size(); i++) {
				parse = Jsoup.parse(new File("D://xining.txt"), "utf-8");
				Elements elementsByTag = parse.getElementsByTag("body");
				Elements tbody = parse.getElementsByTag("tbody");
				if (tbody.isEmpty()) {
					logger.warn("------西宁电信-------:" + phoneNumber + "该用户第" + i + "次详单页面未捕捉到表单数据，data:" + parse.text());
				} else {
					Element table = parse.getElementsByTag("tbody").get(0);
					Elements trs = table.getElementsByTag("tr");
					if (trs.isEmpty()) {
					logger.warn("------西宁电信------:" + phoneNumber + "该用户第" + i + "次详单页面未捕捉到表单数据，data:" + parse.text());
					} else {
						for (int j = 1; j < trs.size(); j++) {
							Elements tds = trs.get(j).getElementsByTag("td");
							Map<String, String> map = new HashMap<String, String>();
							String CallNumber = tds.get(3).text();
							// // "通话开始时间", "CallTime": "10-07 11:29:57",
							String CallTime = tds.get(4).text();
							String substring = CallTime.substring(4);
							String b1 = substring.substring(0, 2);
							String b2 = substring.substring(2, 4);
							String b3 = substring.substring(4, substring.length());
							String dTime = b1 + '-' + b2 + b3;
							// 被叫号码
							map.put("CallNumber", tds.get(3).text());
							// 通话开始时间
							map.put("CallTime", dTime);
							// 时长(秒)
							map.put("CallDuration", tds.get(5).text() + "秒");
							// "通话类型",
							map.put("CallType", tds.get(6).text());
							// "费用(分)",
							map.put("CallMoney", tds.get(7).text());
							// "类型"
							map.put("CallWay", tds.get(8).text());
							// 归属地
							map.put("CallAddress", getMobileHome(CallNumber));
							data1.add(map);
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
	 * 根据被叫号码判断归属地
	 * 
	 * @param CallNumber
	 */
	public static String getMobileHome(String CallNumber) {
		String MobileHome = "";
		try {
			String url = "http://www.ip138.com:8080/search.asp?action=mobile&mobile=%s";
			url = String.format(url, CallNumber);
			Document doc = Jsoup.connect(url).get();
			Elements els = doc.getElementsByClass("tdc2");
			MobileHome = els.get(1).text().replaceAll(" ", "-").substring(3);
//			System.out.println("归属地：" + els.get(1).text().replaceAll(" ", "-").substring(3));
		} catch (IOException e) {

			e.printStackTrace();
		}
		return MobileHome;
	}

	public static void main(String[] args) {
		  ChinaTelecomAnalysisInterface xining = new XiNingTelecomAnalysisImp();
	        List<Map<String, String>> analysisHtml = xining.analysisHtml(null, null);
	        System.out.println(analysisHtml);

	}

}
