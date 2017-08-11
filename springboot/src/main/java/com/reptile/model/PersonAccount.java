package com.reptile.model;

public class PersonAccount {
	private String year;
	private String payMonthNumber;
	private String payBaseNumber;
	private String personPayAmount;
	private String companyPayAmount;
	private String lastYearPayMonthNumber;
	
	@Override
    public String toString() {
        return "PersonAccount{" + "year='" + year + '\'' + ", payMonthNumber='" + payMonthNumber + '\''
                + ", payBaseNumber='" + payBaseNumber + '\''+ ", personPayAmount='" + personPayAmount + '\''
                + ", companyPayAmount='" + companyPayAmount + '\''+ ", lastYearPayMonthNumber='" + lastYearPayMonthNumber + '\''+'}';
    }

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPayMonthNumber() {
		return payMonthNumber;
	}

	public void setPayMonthNumber(String payMonthNumber) {
		this.payMonthNumber = payMonthNumber;
	}

	public String getPayBaseNumber() {
		return payBaseNumber;
	}

	public void setPayBaseNumber(String payBaseNumber) {
		this.payBaseNumber = payBaseNumber;
	}

	public String getPersonPayAmount() {
		return personPayAmount;
	}

	public void setPersonPayAmount(String personPayAmount) {
		this.personPayAmount = personPayAmount;
	}

	public String getCompanyPayAmount() {
		return companyPayAmount;
	}

	public void setCompanyPayAmount(String companyPayAmount) {
		this.companyPayAmount = companyPayAmount;
	}

	public String getLastYearPayMonthNumber() {
		return lastYearPayMonthNumber;
	}

	public void setLastYearPayMonthNumber(String lastYearPayMonthNumber) {
		this.lastYearPayMonthNumber = lastYearPayMonthNumber;
	}
	
}
