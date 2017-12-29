package com.reptile.util;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * 识别图片
 *
 * @author mrlu
 * @date 2016/10/31
 */
public class RecognizeImage {
    private static String appId = "10532232";
    private static String appKey = "ckA04dT4pQA9Y3yuugnxpdEi";
    private static String appTokken = "76lDHL1dH8GEBZvSm0ElqQNFcn4AYD2P";
    private static Logger logger = LoggerFactory.getLogger(RecognizeImage.class);

    /**
     * 对图片进行识别  返回json数据
     *
     * @param filePath
     * @return
     */
    public static JSONObject recognizeImage(String filePath) {
        AipOcr aipOcr = new AipOcr(appId, appKey, appTokken);
        // 可选：设置网络连接参数
        aipOcr.setConnectionTimeoutInMillis(2000);
        aipOcr.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//		aipOcr.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//		aipOcr.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
        String path = filePath;
        JSONObject res = aipOcr.basicGeneral(path, new HashMap<String, String>(16));
        return res;
    }

    /**
     * 将图片转换成二进制
     *
     * @return
     */
    public static String getImageBinary(BufferedImage image,String imageType) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //经测试转换的图片是格式这里就什么格式，否则会失真
        ImageIO.write(image, imageType, baos);
        byte[] bytes = baos.toByteArray();
        return new BASE64Encoder().encode(bytes).trim();

    }

}
