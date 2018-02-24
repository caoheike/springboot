package com.reptile.analysis.chinamobileanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 
 * @ClassName: ChongQingTelecomAnalysisImp
 * @Description: TODO(重庆解析数据)
 * @author duwei
 * @date 2018年1月30日 下午2:15:59
 *
 */

public class ChongQingTelecomAnalysisImp implements ChinaTelecomAnalysisInterface {
	private Logger logger = LoggerFactory.getLogger(ChongQingTelecomAnalysisImp.class);

	@Override
	public List<Map<String, String>> analysisXml(List<String> data, String phoneNumber, String... agrs) {

		return null;
	}

	@Override
	public List<Map<String, String>> analysisJson(List<String> data, String phoneNumber, String... agrs) {
		List<Map<String, String>> data1 = new ArrayList<Map<String, String>>();
		try {
			List arrayList = data;
			for (int i = 0; i < arrayList.size(); i++) {
				Map map = (Map) arrayList.get(i);
				List list1 = (List) map.get("rows");
				for (int j = 0; j < list1.size(); j++) {
					Map<String, String> detailed = new HashMap<String, String>();
					Map map2 = (Map) list1.get(j);
					String CallMoney = (String) map2.get("费用（元）");// 费用
					String CallAddress = (String) map2.get("使用地点");// 使用地点
					String CallType = (String) map2.get("通话类型");// 通话类型
					String CallTime = (String) map2.get("起始时间");// 起始时间
					if (CallTime.equals("--")) {
						continue;
					}
					String ctime = CallTime.substring(5);
					String CallDuration = (String) map2.get("通话时长（秒）");// 通话时长
					String CallWay = (String) map2.get("呼叫类型");// 呼叫类型
					String CallNumber = (String) map2.get("对方号码");
					if (CallNumber.equals("合计") || CallType.contains("-") || CallWay.contains("-")
							|| CallAddress.contains("-")) {
						continue;
					}
					detailed.put("CallWay", CallWay);
					detailed.put("CallAddress", CallAddress + "市");
					detailed.put("CallType", CallType);
					detailed.put("CallMoney", CallMoney);
					detailed.put("CallTime", ctime);
					detailed.put("CallDuration", CallDuration + "秒");
					detailed.put("CallNumber", CallNumber);
					data1.add(detailed);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("---------重庆电信解析：" + phoneNumber + "data:" + data1 + "---------------------------");
		}

		return data1;
	}

	@Override
	public List<Map<String, String>> analysisHtml(List<String> data, String phoneNumber, String... agrs) {
		// TODO Auto-generated method stub
		return null;
	}

}
