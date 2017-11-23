
package com.reptile.util;

/**
 * Created by Administrator on 2017/8/15.
 */
public interface ConstantInterface {

    //公用配置
    String ieDriverKey = "webdriver.ie.driver";
    String chromeDriverKey = "webdriver.chrome.driver";

    //测试环境
    String MyCYDMDemoDLLPATH = "C://yundamaAPI-x64.dll";
    String port = "http://192.168.3.4:8081";
    String ieDriverValue = "C:\\Program Files\\iedriver\\IEDriverServer.exe";
    String chromeDriverValue = "C:\\Program Files\\iedriver\\chromedriver.exe";

    //正式环境
    //    String MyCYDMDemoDLLPATH = "C://yundamaAPI.dll";
    //	  String port="http://10.1.1.12:8080";
    //    String ieDriverValue = "D:\\ie\\IEDriverServer.exe";
    //    String chromeDriverValue = "D:\\ie\\chromedriver.exe";
}

