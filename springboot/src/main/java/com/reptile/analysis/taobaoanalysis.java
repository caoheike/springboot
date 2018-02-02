package com.reptile.analysis;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class taobaoanalysis {
	/**
	 * 淘宝的收获地址数据解析
	 * @param string 
	 * @return
	 */
	public static JSONArray address(String straddress){
		
		List<List<String>> list =table(straddress);
		System.out.println("list=="+list);
		
		JSONArray address=new JSONArray();
		for (int i = 1; i < list.size(); i++) {
			
			JSONObject detailedAddress=new JSONObject();
			detailedAddress.put("phone", list.get(i).get(4));
			detailedAddress.put("location", list.get(i).get(1));
			detailedAddress.put("takeMan", list.get(i).get(0));
			detailedAddress.put("detailedAddress", list.get(i).get(2));
			detailedAddress.put("postcode", list.get(i).get(3));
			System.out.println(detailedAddress);
			address.add(detailedAddress);
		}
		return address;
	}
	
	/**
	 * 交易账单数据解析
	 * @param string 
	 * @return
	 */
	public static JSONObject deal(String straddress,String allpage,String allpageinfo){
		//明细信息所在列表
		List<List<String>> list = null;
		//总额度等信息列表
		List<List<String>> listALLmount = list =table(straddress);
		if(allpage.contains("扫描二维码验证身份")) {
			list =table(allpageinfo);
		}else {
			list =table(allpage);
		}
		
		System.out.println("list=="+list);
		//总的数据
		JSONObject ALLmount=new JSONObject();
		ALLmount.put("userCard", "身份证号");
		String  accountAmount = listALLmount.get(0).get(0);
		accountAmount = accountAmount.substring(accountAmount.indexOf("账户余额显示金额")+1,accountAmount.indexOf("充值提现")).substring(7);
		ALLmount.put("accountAmount",accountAmount);// 账户余额 **.**
		
		String accumulatedAmount=listALLmount.get(1).get(0);
		accumulatedAmount=accumulatedAmount.substring(accumulatedAmount.indexOf("累计收益:"),accumulatedAmount.indexOf("[")).substring(5);
		ALLmount.put("accumulatedAmount", accumulatedAmount);//累计收益  91.85
		
		String antAmount=listALLmount.get(0).get(1);
		System.out.println(antAmount);
//		antAmount=antAmount.substring(antAmount.indexOf("总额度"), antAmount.indexOf("查看")).substring(4);
		ALLmount.put("antAmount","**.**" );//花呗可用额度 **.**
		JSONArray deal=new JSONArray();
		for (int i = 2; i < list.size(); i++) {
			//交易详单
			JSONObject detailedAddress=new JSONObject();
			detailedAddress.put("dealDate", list.get(i).get(1));
			String dealDetail = list.get(i).get(2);
			if(dealDetail.contains("转出")) {
				detailedAddress.put("dealAmount", list.get(i).get(4).replace("+", ""));
			}else {
				detailedAddress.put("dealAmount", list.get(i).get(3).replace("+", ""));
			}
			detailedAddress.put("dealDetail", list.get(i).get(2));
			System.out.println(detailedAddress);
			deal.add(detailedAddress);
		}
		ALLmount.put("deal", deal);
		return ALLmount;
	}
	
    /**
		 * 解析table
		 * @param xml
		 * @return
		 */
		 private static List<List<String>>  table(String xml){ 
			 
			 Document doc = Jsoup.parse(xml);
			 Elements trs = doc.select("table").select("tr");  
			 
			 List<List<String>> list = new ArrayList<List<String>>();
			 for (int i = 0; i < trs.size(); i++) {
				 Elements tds = trs.get(i).select("td");
				 List<String> item = new ArrayList<String>();
				 for (int j = 0; j < tds.size(); j++){
					 String txt = tds.get(j).text().replace(" ", "").replace(" ", "").replace("元", "");
					 item.add(txt);
				 }  
				 list.add(item);
			 }
			return list;	
	    } 
 
}
