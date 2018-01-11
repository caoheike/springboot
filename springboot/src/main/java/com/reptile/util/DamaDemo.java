package com.reptile.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Date;

/**
 * 若快打码
 * http://www.ruokuai.com/login
 * ludangwei q123456  用户
 * caoheike q123456  开发者
 * @author  mrlu 2018 1 11
 */
public class DamaDemo {

    public static void main(String[] args) {
        String byUrl = DamaDemo.createByUrl("ludangwei", "q123456", "3040", "60", "95374", "79907081378343459bfa225ecfdf9a9e", "F://1234.png");
        System.out.println(byUrl);
    }

    /**
     * 答题(URL)
     * @param username	用户名
     * @param password	用户密码。(支持32位MD5)
     * @param typeid	题目类型
     * @param timeout	任务超时时间，默认与最小值为60秒。
     * @param softid	软件ID，开发者可自行申请。
     * @param softkey	软件KEY，开发者可自行申请。
     * @param imageurl	远程图片URL
     * @return			平台返回结果XML样式
     * @throws IOException
     */
    public static String createByUrl(String username, String password,
                                     String typeid, String timeout, String softid, String softkey,
                                     String imageurl) {
        String param = String
                .format(
                        "username=%s&password=%s&typeid=%s&timeout=%s&softid=%s&softkey=%s",
                        username, password, typeid, timeout, softid, softkey);
        ByteArrayOutputStream baos = null;
        String result;
        try {
            InputStream input=new FileInputStream(new File(imageurl));
            BufferedImage image = ImageIO.read(input);

            baos = new ByteArrayOutputStream();
            ImageIO.write( image, "jpg", baos);
            baos.flush();
            byte[] data = baos.toByteArray();
            baos.close();

            result = DamaDemo.httpPostImage(
                    "http://api.ruokuai.com/create.json", param, data);
        } catch(Exception e) {
            e.printStackTrace();
            result = "未知问题";
        }
        return result;
    }

    /**
     * 答题
     * @param url 			请求URL，不带参数 如：http://api.ruokuai.com/create.xml
     * @param param			请求参数，如：username=test&password=1
     * @param data			图片二进制流
     * @return				平台返回结果XML样式
     * @throws IOException
     */
    public static String httpPostImage(String url, String param,
                                       byte[] data) throws Exception {
        long time = (new Date()).getTime();
        URL u = null;
        HttpURLConnection con = null;
        String boundary = "----------" + MD5(String.valueOf(time));
        String boundarybytesString = "\r\n--" + boundary + "\r\n";
        OutputStream out = null;

        u = new URL(url);

        con = (HttpURLConnection) u.openConnection();
        con.setRequestMethod("POST");
        //con.setReadTimeout(95000);
        con.setConnectTimeout(95000); //此值与timeout参数相关，如果timeout参数是90秒，这里就是95000，建议多5秒
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setUseCaches(true);
        con.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);

        out = con.getOutputStream();

        for (String paramValue : param.split("[&]")) {
            out.write(boundarybytesString.getBytes("UTF-8"));
            String paramString = "Content-Disposition: form-data; name=\""
                    + paramValue.split("[=]")[0] + "\"\r\n\r\n" + paramValue.split("[=]")[1];
            out.write(paramString.getBytes("UTF-8"));
        }
        out.write(boundarybytesString.getBytes("UTF-8"));

        String paramString = "Content-Disposition: form-data; name=\"image\"; filename=\""
                + "sample.gif" + "\"\r\nContent-Type: image/gif\r\n\r\n";
        out.write(paramString.getBytes("UTF-8"));

        out.write(data);

        String tailer = "\r\n--" + boundary + "--\r\n";
        out.write(tailer.getBytes("UTF-8"));

        out.flush();
        out.close();

        StringBuffer buffer = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(con
                .getInputStream(), "UTF-8"));
        String temp;
        while ((temp = br.readLine()) != null) {
            buffer.append(temp);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    /**
     * 字符串MD5加密
     * @param s 原始字符串
     * @return  加密后字符串
     */
    public final static String MD5(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
