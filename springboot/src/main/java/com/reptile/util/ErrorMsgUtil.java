package com.reptile.util;

import java.util.ResourceBundle;

public class ErrorMsgUtil {
	
	 private static ResourceBundle resource = ResourceBundle.getBundle("error-message");


    /**
     * 获取属性
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {

        return resource.getString(key);

    }
	
}
