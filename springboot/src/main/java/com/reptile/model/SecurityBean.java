package com.reptile.model;

public class SecurityBean {
	private String year;//年份
	private String month_count;//月数
	private String company_name;//公司名称
	private String base_number;//缴费基数
	private String monthly_company_income;//单位缴存
	private String monthly_personal_income;//个⼈人缴存
	private String type;//缴费状态
	private String company_percentage;//单位缴存比例
	private String personal_percentage;//个人缴存比例
	private String last_pay_date;//缴存日期
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth_count() {
		return month_count;
	}
	public void setMonth_count(String month_count) {
		this.month_count = month_count;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getBase_number() {
		return base_number;
	}
	public void setBase_number(String base_number) {
		this.base_number = base_number;
	}
	public String getMonthly_company_income() {
		return monthly_company_income;
	}
	public void setMonthly_company_income(String monthly_company_income) {
		this.monthly_company_income = monthly_company_income;
	}
	public String getMonthly_personal_income() {
		return monthly_personal_income;
	}
	public void setMonthly_personal_income(String monthly_personal_income) {
		this.monthly_personal_income = monthly_personal_income;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCompany_percentage() {
		return company_percentage;
	}
	public void setCompany_percentage(String company_percentage) {
		this.company_percentage = company_percentage;
	}
	public String getPersonal_percentage() {
		return personal_percentage;
	}
	public void setPersonal_percentage(String personal_percentage) {
		this.personal_percentage = personal_percentage;
	}
	public String getLast_pay_date() {
		return last_pay_date;
	}
	public void setLast_pay_date(String last_pay_date) {
		this.last_pay_date = last_pay_date;
	}
	@Override
	public String toString() {
		return "SecurityBean [year=" + year + ", month_count=" + month_count
				+ ", company_name=" + company_name + ", base_number="
				+ base_number + ", monthly_company_income="
				+ monthly_company_income + ", monthly_personal_income="
				+ monthly_personal_income + ", type=" + type
				+ ", company_percentage=" + company_percentage
				+ ", personal_percentage=" + personal_percentage
				+ ", last_pay_date=" + last_pay_date + "]";
	}

}
