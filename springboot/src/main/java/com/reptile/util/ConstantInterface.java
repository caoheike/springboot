
package com.reptile.util;

/**
 * Created by Administrator on 2017/8/15.
 */
public interface ConstantInterface {

    String ieDriverKey = "webdriver.ie.driver";
    String ieDriverValue = "C:\\Program Files\\iedriver\\IEDriverServer.exe";
    String chromeDriverKey = "webdriver.chrome.driver";
    String chromeDriverValue = "C:\\Program Files\\iedriver\\chromedriver.exe";

    //测试环境
    String MyCYDMDemoDLLPATH = "C://yundamaAPI-x64.dll";
    String port = "http://192.168.3.4:8081";

    //正式环境
    //    String MyCYDMDemoDLLPATH = "C://yundamaAPI.dll";
    //	  String port="http://10.1.1.12:8080";
}

