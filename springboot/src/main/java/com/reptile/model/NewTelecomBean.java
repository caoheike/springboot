package com.reptile.model;

public class NewTelecomBean {
	
	private String CallNumber;//通话号码
	private String CallType;//通话号码归属地类型
	private String CallAddress;//通话号码归属地
	private String CallWay;//呼叫类型
	private Double CallMoney;//呼叫费用
	private String CallTime;//呼叫时间
	private String CallDuration;//呼叫时常
	
	
	
	public String getCallNumber() {
		return CallNumber;
	}
	public void setCallNumber(String callNumber) {
		CallNumber = callNumber;
	}
	public String getCallType() {
		return CallType;
	}
	public void setCallType(String callType) {
		CallType = callType;
	}
	public String getCallAddress() {
		return CallAddress;
	}
	public void setCallAddress(String callAddress) {
		CallAddress = callAddress;
	}
	public String getCallWay() {
		return CallWay;
	}
	public void setCallWay(String callWay) {
		CallWay = callWay;
	}
	public Double getCallMoney() {
		return CallMoney;
	}
	public void setCallMoney(Double callMoney) {
		CallMoney = callMoney;
	}
	public String getCallTime() {
		return CallTime;
	}
	public void setCallTime(String callTime) {
		CallTime = callTime;
	}
	public String getCallDuration() {
		return CallDuration;
	}
	public void setCallDuration(String callDuration) {
		CallDuration = callDuration;
	}
	@Override
	public String toString() {
		return "HuNanTelecomBean [CallNumber=" + CallNumber + ", CallType="
				+ CallType + ", CallAddress=" + CallAddress + ", CallWay="
				+ CallWay + ", CallMoney=" + CallMoney + ", CallTime="
				+ CallTime + ", CallDuration=" + CallDuration + "]";
	}
	
	

}
