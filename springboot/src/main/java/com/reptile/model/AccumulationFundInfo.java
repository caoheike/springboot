package com.reptile.model;

/**
 * Created by HotWong on 2017/7/17 0017.
 */
public class AccumulationFundInfo {
    private String monthBase;//月缴基数
    private String unitAccount;//所在单位帐号
    private String unitName;//所在单位名称
    private String openingDate;//开户日期
    private String currentState;//当前状态
    private String lastYearBalance;//上年余额
    private String paidThisYear;//本年缴交
    private String externalTransfer;//外部转入
    private String extractionThisYear;//本年支取
    private String balance;//余额

    @Override
    public String toString() {
        return "info={" + "monthBase='" + monthBase + '\'' + ", unitAccount='" + unitAccount + '\''
                + ", unitName='" + unitName + '\''+ ", openingDate='" + openingDate + '\''
                + ", currentState='" + currentState + '\''+ ", lastYearBalance='" + lastYearBalance + '\''
                + ", paidThisYear='" + paidThisYear + '\''+ ", externalTransfer='" + externalTransfer + '\''
                + ", extractionThisYear='" + extractionThisYear + '\''+ ", balance='" + balance + '\'' +"}";
    }

    public AccumulationFundInfo() {
        this.monthBase = "月缴基数";
        this.unitAccount = "所在单位帐号";
        this.unitName = "所在单位名称";
        this.openingDate = "开户日期";
        this.currentState = "当前状态";
        this.lastYearBalance = "上年余额";
        this.paidThisYear = "本年缴交";
        this.externalTransfer = "外部转入";
        this.extractionThisYear = "本年支取";
        this.balance = "余额";
    }

    public String getMonthBase() {
        return monthBase;
    }

    public void setMonthBase(String monthBase) {
        this.monthBase = monthBase;
    }

    public String getUnitAccount() {
        return unitAccount;
    }

    public void setUnitAccount(String unitAccount) {
        this.unitAccount = unitAccount;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getLastYearBalance() {
        return lastYearBalance;
    }

    public void setLastYearBalance(String lastYearBalance) {
        this.lastYearBalance = lastYearBalance;
    }

    public String getPaidThisYear() {
        return paidThisYear;
    }

    public void setPaidThisYear(String paidThisYear) {
        this.paidThisYear = paidThisYear;
    }

    public String getExternalTransfer() {
        return externalTransfer;
    }

    public void setExternalTransfer(String externalTransfer) {
        this.externalTransfer = externalTransfer;
    }

    public String getExtractionThisYear() {
        return extractionThisYear;
    }

    public void setExtractionThisYear(String extractionThisYear) {
        this.extractionThisYear = extractionThisYear;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
