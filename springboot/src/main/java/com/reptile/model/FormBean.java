package com.reptile.model;

/**
 * Created by HotWong on 2017/4/27 0027.
 */
public class FormBean {
    private Long userId;
    private String userName;
    private String userPass;
    private String email;
    private String phone;
    private String verifyCode;
    private String createTime;
    private String desc;

    @Override
    public String toString() {
        return "FormBean{" + "userId='" + userId + '\'' + ", userName='" + userName + '\''
                + ", email='" + email + '\''+ ", phone='" + phone + '\''
                + ", createTime='" + createTime + '\''+ ", desc='" + desc + '\''
                + ", userPass='" + userPass + '\''+ ", verifyCode='" + verifyCode + '\'' +"}";
    }
    public boolean verifyParams(FormBean bean){
        if(bean.getUserId()!=null && (bean.getUserName()!=null && !bean.getUserName().equals("")) && (bean.getUserPass()!=null && !bean.getUserPass().equals("")) && (bean.getVerifyCode()!=null && !bean.getVerifyCode().equals(""))){
            return true;
        }
        return false;
    }
    public boolean verifyCredit(FormBean bean){
        if((bean.getUserName()!=null && !bean.getUserName().equals("")) && (bean.getUserPass()!=null && !bean.getUserPass().equals("")) && (bean.getVerifyCode()!=null && !bean.getVerifyCode().equals(""))){
            return true;
        }
        return false;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
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
}
