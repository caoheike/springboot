package com.reptile.model;

public class AccumulationFlows {
	private String bizDesc;//业务描述//
	private String operatorDate;//操作时间
	private String amount;//操作金额
	private String type;//操作类型
	private String payMonth;//缴费月份
	private String companyName;
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getBizDesc() {
		return bizDesc;
	}
	public void setBizDesc(String bizDesc) {
		this.bizDesc = bizDesc;
	}
	public String getOperatorDate() {
		return operatorDate;
	}
	public void setOperatorDate(String operatorDate) {
		this.operatorDate = operatorDate;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPayMonth() {
		return payMonth;
	}
	public void setPayMonth(String payMonth) {
		this.payMonth = payMonth;
	}
	@Override
	public String toString() {
		return "AccumulationFlows [bizDesc=" + bizDesc + ", operatorDate="
				+ operatorDate + ", amount=" + amount + ", type=" + type
				+ ", payMonth=" + payMonth + ", companyName=" + companyName
				+ "]";
	}
	
}
