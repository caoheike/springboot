package com.reptile.model;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * 
 * @author Bigyoung
 *
 */
public class TelecomBean {
private String userPhone;
private String userPassword;
private String servicePass;
private String userCode;
private String userName;
private String usernum;
private String CallCode;
public  final static String LoginUrl="http://login.189.cn/login";
public  final static String SendCodeUrl="http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10027&toStUrl=http://sn.189.cn/service/bill/fee.action?type=ticket&fastcode=10000202&cityCode=sn";
public String getUserPhone() {
	return userPhone;
}
public void setUserPhone(String userPhone) {
	this.userPhone = userPhone;
}
public String getUserPassword() {
	return userPassword;
}
public void setUserPassword(String userPassword) {
	this.userPassword = userPassword;
}
public String getServicePass() {
	return servicePass;
}
public void setServicePass(String servicePass) {
	this.servicePass = servicePass;
}
public String getUserCode() {
	return userCode;
}
public void setUserCode(String userCode) {
	this.userCode = userCode;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public String getUsernum() {
	return usernum;
}
public void setUsernum(String usernum) {
	this.usernum = usernum;
}
public String getCallCode() {
	return CallCode;
}
public void setCallCode(String callCode) {
	CallCode = callCode;
}



}
