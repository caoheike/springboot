package com.reptile.model;

import java.util.List;

public class PersonInfo {
	private String userName;
	private String createTime;
	private String personStatus;
	private String payStatus;
	private String amount;
	private String agencyName;
	private List<PersonAccount> accountList;
	
	@Override
    public String toString() {
        return "PersonInfo{" + "userName='" + userName + '\'' + ", createTime='" + createTime + '\''
        		+ ", personStatus='" + personStatus + '\''+ ", payStatus='" + payStatus + '\''
                + ", amount='" + amount + '\''+ ", agencyName='" + agencyName + '\'' + ", accountList='" + accountList + '\'' +'}';
    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getPersonStatus() {
		return personStatus;
	}

	public void setPersonStatus(String personStatus) {
		this.personStatus = personStatus;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public List<PersonAccount> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<PersonAccount> accountList) {
		this.accountList = accountList;
	}
}
