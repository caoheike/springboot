package com.reptile.model;

public class UnicomBean {
	
 private String Useriphone;
 private String UserPassword;
 private String UserCode;
 private String CallCode;
 public static String newCodeUrl="http://uac.10010.com/portal/Service/CreateImage";
 public static String LoginUrl="https://uac.10010.com/portal/homeLogin";
 public static String InfoUrl="http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001";
 public static String centerUrl="http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001";
public String getUseriphone() {
	return Useriphone;
}
public void setUseriphone(String useriphone) {
	Useriphone = useriphone;
}
public String getUserPassword() {
	return UserPassword;
}
public void setUserPassword(String userPassword) {
	UserPassword = userPassword;
}
public String getUserCode() {
	return UserCode;
}
public void setUserCode(String userCode) {
	UserCode = userCode;
}
public String getCallCode() {
	return CallCode;
}
public void setCallCode(String callCode) {
	CallCode = callCode;
}
 
public String Loginurl(UnicomBean unicomBean, String uvc) {

	return "https://uac.10010.com/portal/Service/MallLogin?userName="+unicomBean.getUseriphone()+"&password="+unicomBean.getUserPassword()+"&pwdType=01&productType=01&verifyCode=" + unicomBean.getUserCode() + "&redirectType=03&uvc=" +uvc;
}
}
