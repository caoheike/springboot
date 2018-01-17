
package com.reptile.util;

/**
 * 配置一些常量
 * @author mrlu
 * @date 2016/10/31
 */
public interface ConstantInterface {
    /**
     * 公用配置
     */
    String ieDriverKey = "webdriver.ie.driver";
    String chromeDriverKey = "webdriver.chrome.driver";
    //人法网开关
    String lawStatus= "lawStatus";
    //民生信用卡密码框坐标X
    String cmbcCreditPassWordX= "cmbcCreditPassWordX";
    //民生信用卡密码框坐标Y
    String cmbcCreditPassWordY= "cmbcCreditPassWordX";
    //民生储蓄卡密码框坐标X
    String cmbcDepositPassWordX= "cmbcDepositPassWordX";
    //民生储蓄卡密码框坐标Y
    String cmbcDepositPassWordY= "cmbcDepositPassWordY";
    //招商储蓄卡用户框坐标X
    String cmbDepositUsernameX= "cmbDepositUsernameX";
    //招商储蓄卡用户框坐标Y
    String cmbDepositUsernameY= "cmbDepositUsernameY";
    //招商储蓄卡密码框坐标X
    String cmbDepositPassWordX= "cmbDepositPassWordX";
    //招商储蓄卡密码框坐标Y
    String cmbDepositPassWordY= "cmbDepositPassWordY";
    //招商信用卡用户框坐标X
    String cmbCreditUsernameX= "cmbCreditUsernameX";
    //招商信用卡用户框坐标Y
    String cmbCreditUsernameY= "cmbCreditUsernameY";
    //招商信用卡密码框坐标X
    String cmbCreditPassWordX= "cmbCreditPassWordX";
    //招商信用卡密码框坐标Y
    String cmbCreditPassWordY= "cmbCreditPassWordY";
   
    /**
     * 测试环境
     */
    String MyCYDMDemoDLLPATH = "C://yundamaAPI.dll";
    String port = "http://117.34.70.217:8080";
    String ieDriverValue = "C:\\Program Files\\iedriver\\IEDriverServer.exe";
    String chromeDriverValue = "C:\\Program Files\\iedriver\\chromedriver.exe";

    /**
     * 正式环境
     */
//        String MyCYDMDemoDLLPATH = "C://yundamaAPI.dll";
//    	  String port="http://10.1.1.12:8080";s
//        String ieDriverValue = "D:\\ie\\IEDriverServer.exe";
//        String chromeDriverValue = "D:\\ie\\chromedriver.exe";
}

