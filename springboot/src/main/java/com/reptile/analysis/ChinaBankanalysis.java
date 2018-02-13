package com.reptile.analysis;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ChinaBankanalysis {
	
	/**
	 * 交易账单数据解析
	 * @param string 
	 * @return
	 */
	public static JSONObject billanalysis(String pageSource){
		
		 JSONObject AccountSummary=new JSONObject();
		 Document doc = Jsoup.parse(pageSource);
		 
		 //每个月具体的payRecord
		 Elements table = doc.getElementById("creditCardDetails_list").select("table").select("tbody");
		 List<List<String>> billList=table(table);
		 JSONObject datemap=new JSONObject();
		 JSONObject bankListmap=new JSONObject();
		 JSONArray payRecord=new JSONArray();
		
		 String post_amt="";
		 for (int i = 0; i < billList.size(); i++) {
			 List<String> payRecordde=billList.get(i);
			 if(payRecordde.get(4).equals("-")) {
				  post_amt=replaceway(payRecordde.get(3));//交易金额
			 }else {
				  post_amt=replaceway(payRecordde.get(4));//交易金额
			 }
			 String tran_date=replaceway(payRecordde.get(1));//交易时间0104  1218
			 String tran_desc=payRecordde.get(6);//交易描述
			  
			 datemap.put("post_amt", post_amt);
			 datemap.put("tran_desc", tran_desc);
			 datemap.put("tran_date", tran_date);
			 payRecord.add(datemap);
		}
			 
		       bankListmap.put("payRecord", payRecord);
		       //每个月总的数据
		       Elements trs = doc.getElementById("billed_trans_detail").select("table").get(0).select("tbody");
		       List<List<String>> List=table(trs);
		       List<String> Listda=List.get(0);
		       Elements trs1 = doc.getElementById("billed_trans_detail").select("table").get(1).select("tbody");
		       List<List<String>> List1=table(trs1);
		       List<String> Listda1=List1.get(0);
		       String CreditLimit =replaceway(Listda.get(1));//信用额度
		       String RMBCurrentAmountDue = replaceway(Listda1.get(4)).substring(2);//本期应还
		       String RMBMinimumAmountDue = replaceway(Listda1.get(5));//本期最低应还
		       String StatementDate=replaceway(Listda.get(4));//"账单日";
		       
		       String PaymentDueDate=Listda.get(5).replace("/", "");//"到期还款日"
		
				AccountSummary.put("PaymentDueDate", PaymentDueDate);
				AccountSummary.put("RMBCurrentAmountDue", RMBCurrentAmountDue);
				AccountSummary.put("StatementDate",StatementDate);
				AccountSummary.put("RMBMinimumAmountDue",RMBMinimumAmountDue);
				AccountSummary.put("CreditLimit",CreditLimit);
		        bankListmap.put("AccountSummary", AccountSummary);
			 return bankListmap;
			}
	/**
	 * 字符替换
	 * @param string
	 * @return
	 */
    private static String replaceway(String string) {
		 String str=string.replace("/", "").replace(",", "");
		return str;
	}

	/**
		 * 解析table
		 * @param trs
		 * @return
		 */
		 private static List<List<String>>  table(Elements trs){ 
			 
			 Elements trss = trs.select("tr");  
			 List<List<String>> list = new ArrayList<List<String>>();
			 for (int i = 0; i < trss.size(); i++) {
				 Elements tds = trss.get(i).select("td");
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
