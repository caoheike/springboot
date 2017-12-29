package com.reptile.model;

/**
 *  
 * @author bigyoung
 * 
 *
 */
public class MobileBean  {
private String UserIphone;
private String UserPassword;
private String UserCode;
private String CallCode;

public String getCallCode() {
	return CallCode;
}
public void setCallCode(String callCode) {
	CallCode = callCode;
}
private static String GetCodeUrl="https://sn.ac.10086.cn/servlet/CreateImage?"+System.currentTimeMillis();
public static String loginurl="https://sn.ac.10086.cn/loginAction";
public static String ifLoginUrl="http://service.sn.10086.cn/app?service=page/MyBillQuery&listener=initPage&type=1&random=0.6167800748196473";
public static String  CodeUrl="https://sn.ac.10086.cn/login";
public static String  SencodeUrl="http://service.sn.10086.cn/app?service=page/feeService.BillQueryNew&listener=initPage&MENU_ID=&loginType=1&isGroup=1";
public static String  CallCodeUrl="http://service.sn.10086.cn/app?service=page/MyMobileSendSms&listener=initPage&type=2";
private static String SenUrl;
public static String iflg="http://service.sn.10086.cn/app?service=page/balanceQueryNew&listener=initPage&random=0.7018434080546756";
//public static String pingzhengUrl="http://service.sn.10086.cn/app?service=ajaxDirect/1/DetailedQuery/DetailedQuery/javascript/refushBusiSearchResult&pagename=DetailedQuery&eventname=queryAll&&MONTH=201705&MONTH_DAY=&LAST_MONTH_DAY=2017-05-31&BILL_TYPE=207&SHOW_TYPE=0&partids=refushBusiSearchResult&ajaxSubmitType=get&ajax_randomcode=0.02440126490078076";
private static String downloadUrl="http://service.sn.10086.cn/app?service=page/DetailedQuery&listener=billExport&BILL_TYPE=201&ShowMonth=X";
//修改密码需要此次认证Url才能进行修改
public static String UpdatePwdAttestationUrl="https://service.sn.10086.cn/app?service=page/personalinfo.ResetPwdOperation&listener=resetOperation";
//认证通过！通过此次请求进行修改
public static String UpdatePwd="https://service.sn.10086.cn/app?service=page/personalinfo.ResetPwdOperation&listener=changeOperation";
//修改密码首页，通过此次请求获得验证码，（通过Url直接获取有验证，So 只能根据元素获取）
private static String UpdatePwdImg="https://service.sn.10086.cn/app?service=page/personalinfo.ResetPwdOperation&listener=initPage&MENU_ID=&loginType=-1&isGroup=1";

public String getUserIphone() {
	return UserIphone;
}
public void setUserIphone(String userIphone) {
	UserIphone = userIphone;
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
public String setUrl(String iphone) {
	int i = (int)(Math.random()*100);
	SenUrl ="http://service.sn.10086.cn/app?service=ajaxDirect/1/MyMobileSendSms/MyMobileSendSms/javascript/null&pagename=MyMobileSendSms&eventname=sendSMS&&serialNumber="+iphone+"&partids=null&ajaxSubmitType=get&ajax_randomcode=0.119035301834136"+i;
return SenUrl;
}
public String setcaught(String CallCode){
	String caught="http://service.sn.10086.cn/app?service=ajaxDirect/1/MyMobileSendSms/MyMobileSendSms/javascript/null&pagename=MyMobileSendSms&eventname=forgotPwd&&SMS_NUMBER="+CallCode+"&partids=null&ajaxSubmitType=get&ajax_randomcode=0.6155049169070319";
return caught;
}
public String pingzhengurl(String date){
	String url="http://service.sn.10086.cn/app?service=ajaxDirect/1/DetailedQuery/DetailedQuery/javascript/refushBusiSearchResult&pagename=DetailedQuery&eventname=queryAll&&MONTH="+date+"&MONTH_DAY=&LAST_MONTH_DAY=2017-05-31&BILL_TYPE=207&SHOW_TYPE=0&partids=refushBusiSearchResult&ajaxSubmitType=get&ajax_randomcode=0.02440126490078076";
return url;
}
public String uvc(String uvc){
String uvcs="https://sn.ac.10086.cn/servlet/CheckCode?code="+uvc+"";
return uvcs;
}
public static String getGetCodeUrl() {
	return GetCodeUrl;
}
public static void setGetCodeUrl(String getCodeUrl) {
	GetCodeUrl = getCodeUrl;
}
public static String getDownloadUrl() {
	return downloadUrl;
}
public static void setDownloadUrl(String downloadUrl) {
	MobileBean.downloadUrl = downloadUrl;
}
public static String getUpdatePwdImg() {
	return UpdatePwdImg;
}
public static void setUpdatePwdImg(String updatePwdImg) {
	UpdatePwdImg = updatePwdImg;
}

}
