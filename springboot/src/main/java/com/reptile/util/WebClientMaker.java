package com.reptile.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * webclient制造者
 * 根据不同的省份id创建不同webclient
 *
 * @author mrludw
 */
public class WebClientMaker {
    private static Logger logger = LoggerFactory.getLogger(WebClientMaker.class);

    public static WebClientFactoryInterface createWebClient(String userName, String provinceId) {
        switch (provinceId) {
            case "29":
                logger.warn(userName + "用户属于西宁电信,本次连接采用代理ip访问-----------------");
                return new WebClientFactoryProxy();
            default:
                return new WebClientFactory();
        }

    }

}
