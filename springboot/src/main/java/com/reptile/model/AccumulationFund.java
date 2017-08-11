package com.reptile.model;

/**
 * Created by HotWong on 2017/7/17 0017.
 */
public class AccumulationFund {
    private String createTime;
    private String desc;
    private String income;
    private String expenditure;

    @Override
    public String toString() {
        return "{AccumulationFund:{" + "createTime:'" + createTime + '\'' + ", desc:'" + desc + '\''
                + ", income:'" + income + '\''+ ", expenditure:'" + expenditure + '\''+"}}";
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(String expenditure) {
        this.expenditure = expenditure;
    }
}
