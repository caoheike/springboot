package com.reptile.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class NewAccumulation {
	//private Map<String, String> data=new HashMap<String, String>();//推送字段
	
	private Map<String, String> basicInfos=new HashMap<String, String>();//基本信息
	
	private List<Map<String, String>> flows= new ArrayList<Map<String,String>>();//流水信息
	
	private Map<String, String> loans=new HashMap<String, String>();//贷款信息
//	/**
//	 * userId 身份证
//	 * insertTime 创建时间
//	 * city  公积金所在地
//	 * cityName 城市名称
//	 */
//	private  static final  String[] dataName={"userId","insertTime","city","cityName"};	
//	

	/**
	 * name  用户姓名
	 * idCard 身份证号码
	 * companyFundAccount 单位公积金账号
	 * personFundAccount  个人公积金账号
	 * companyName 公司名称
	 * personFundCard  个人公积金卡号
	 * baseDeposit 缴费基数
	 * companyRatio  公司缴费比例
	 * personRatio 个人缴费比例
	 * personDepositAmount  个人缴费金额
	 * companyDepositAmount 公司缴费金额
	 * lastDepositDate  最后缴费日期
	 * balance  余额
	 * status 状态
	 *
	 */
	private  static final  String[] basicInfosName={"name","idCard","companyFundAccount","personFundAccount","companyName","personFundCard","baseDeposit","companyRatio","personRatio","personDepositAmount","companyDepositAmount","lastDepositDate","balance","status"};
	/**
	 * 
	 * bizDesc  业务描述  汇缴、补缴+ 日期+公积金
	 * operatorDate 操作时间
	 * amount 操作金额
	 * type  操作类型
	 * payMonth 缴费月份
	 * 
	 */
	private  static final  String[] flowsName={"bizDesc","operatorDate","amount","type","payMonth"};
	/**
	 * loanAccNo  贷款账号
	 * loanLimit  贷款期限
	 * openDate 开户日期
	 * loanAmount  贷款总额
	 * lastPaymentDate 最近还款日期
	 * status 还款状态
	 * loanBalance 贷款余额
	 * paymentMethod 还款方式
	 */
	private  static final  String[] loansName={"loanAccNo","loanLimit","openDate","loanAmount","lastPaymentDate","status","loanBalance","paymentMethod"};
	
	
//	public Map<String, String> getData() {
//		
//		
//		return data;
//		//return new JSONObject().fromObject(data);
//	}
// /**
// * 推送数据赋值
// * @param data
// */
//	public void setData(List<String> data) {
//		for (int i = 0; i < this.dataName.length; i++) {
//			
//				this.data.put(this.dataName[i], data.get(i)) ;
//		
//		}
//	}

	public Map<String, String> getBasicInfos() {
		return basicInfos;
		//return new JSONObject().fromObject(basicInfos);
	}
/**
 * 基本信息赋值
 * @param basicInfos
 */
	public void setBasicInfos(List<Object> basicInfos) {
		System.out.println(basicInfos);
		for (int i = 0; i < basicInfosName.length; i++) {
			if(basicInfos.get(i)==null){
				this.basicInfos.put(basicInfosName[i], "");
			}else if(i==6||i==9||i==12){
			    String mony=basicInfos.get(i).toString();
			    if(mony.contains(",")){
			    	mony=mony.replace(",", "");
			    }
				this.basicInfos.put(basicInfosName[i], mony);
				
			}else {
				this.basicInfos.put(basicInfosName[i], basicInfos.get(i).toString());
			}
			
			
		}
		
		
	}

	public List<Map<String, String>> getFlows() {
		
		return flows;
		//return new JSONArray().fromObject(flows);
	}
	public void setFlows(List<String> flowsName,List<Map<String, Object>>	resultsData) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
		
		 // List<Map<String, String>>	flowData=new ArrayList<Map<String,String>>();
		String type,time="";
		Date date=null;
	         for (Map<String, Object> map2 : resultsData) {
	        	Map<String, String> data=new HashMap<String, String>();
	        	for (int i=0;i<this.flowsName.length;i++) {
	        		if(map2.get(flowsName.get(i))==null){
	        			data.put(this.flowsName[i], "");
	        		}else{
	        	      if(i==0){
	        	    	  time=map2.get(flowsName.get(4)).toString();
	        	    	  
	        	    	  type= map2.get(flowsName.get(3)).toString();
	        	    	  if(time.contains("-")&&time.lastIndexOf("-")>4){
	        	    		  date=sdf.parse(time);
	        	    		  time=sdf1.format(date);
	        	    		  if(type.contains("汇缴")){
		        	    		  data.put(this.flowsName[i], "汇缴"+time+"公积金"); 
		        	    	  }else if(type.contains("补缴")){
		        	    		  data.put(this.flowsName[i],"补缴"+time+"公积金"); 
		        	    	  }else{
		        	    		  break;
		        	    	  }
	        	    	  }else{
	        	    		  System.out.println("后期看日期格式");
	        	    	  }
	        	    	 
	        	    	  
	        	      }else{
	        		    if(i==2){
	        		    	String mony=map2.get(flowsName.get(i)).toString();
	        		    	if(mony.contains(",")){
	        			    	mony=mony.replace(",", "");
	        			    }
	        		    	data.put(this.flowsName[i],mony );
	        		     }else{
	        		    	 data.put(this.flowsName[i], map2.get(flowsName.get(i)).toString());
	        		     }
	        			
      		        	
	        	      }
	        		}
	        		
				}
	        	this.flows.add(data);//过滤流水
			  }
	        
		
	}

	public Map<String, String> getLoans() {
		
		return loans;
		//return new JSONObject().fromObject(loans);
	}

	public void setLoans(Map<String, String> loans) {
		
			this.loans = loans;
		
		
	}

}
