
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

