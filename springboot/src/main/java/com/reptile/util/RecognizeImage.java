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
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
    
    
    /**
     * 二值化图片 方便图片更容易辨认
     * @param filePath
     * @return 二值化后图片的路径
     * @throws IOException
     */
    public static String binaryImage(String filePath) throws IOException {
        BufferedInputStream inputStream=new BufferedInputStream(new FileInputStream(new File(filePath)));
        BufferedImage read = ImageIO.read(inputStream);
        int height = read.getHeight();
        int width = read.getWidth();
        BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                int rgb = read.getRGB(i, j);
                String argb = Integer.toHexString(rgb);

                int r = Integer.parseInt(argb.substring(2, 4),16);
                int g = Integer.parseInt(argb.substring(4, 6),16);
                int b = Integer.parseInt(argb.substring(6, 8),16);
                int result=(int)((r+g+b)/3);
                if(result>=170){
                    image.setRGB(i,j, Color.WHITE.getRGB());
                }else{
                    image.setRGB(i,j, Color.black.getRGB());
                }
            }
        }
        String path="f://";
        String fileName="xz"+System.currentTimeMillis()+".jpg";
        ImageIO.write(image,"jpg",new File(path+fileName));
        return path+fileName;
    }

}
