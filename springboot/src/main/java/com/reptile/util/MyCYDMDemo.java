package com.reptile.util;

import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.swing.FilePane;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import com.sun.jna.Library;
import com.sun.jna.Native;

import java.util.HashMap;
import java.util.Map;

public class MyCYDMDemo {
    private static Logger logger= LoggerFactory.getLogger(MyCYDMDemo.class);
    // 下载云打码DLL http://yundama.com/apidoc/YDM_SDK.html#DLL
    // yundamaAPI 32位, yundamaAPI-x64 64位
    public static String DLLPATH = ConstantInterface.MyCYDMDemoDLLPATH;

    public interface YDM extends Library {
        YDM INSTANCE = (YDM) Native.loadLibrary(DLLPATH, YDM.class);

        public void YDM_SetBaseAPI(String lpBaseAPI);

        public void YDM_SetAppInfo(int nAppId, String lpAppKey);

        public int YDM_Login(String lpUserName, String lpPassWord);

        public int YDM_DecodeByPath(String lpFilePath, int nCodeType, byte[] pCodeResult);

        public int YDM_UploadByPath(String lpFilePath, int nCodeType);

        public int YDM_EasyDecodeByPath(String lpUserName, String lpPassWord, int nAppId, String lpAppKey, String lpFilePath, int nCodeType, int nTimeOut, byte[] pCodeResult);

        public int YDM_DecodeByBytes(byte[] lpBuffer, int nNumberOfBytesToRead, int nCodeType, byte[] pCodeResult);

        public int YDM_UploadByBytes(byte[] lpBuffer, int nNumberOfBytesToRead, int nCodeType);

        public int YDM_EasyDecodeByBytes(String lpUserName, String lpPassWord, int nAppId, String lpAppKey, byte[] lpBuffer, int nNumberOfBytesToRead, int nCodeType, int nTimeOut, byte[] pCodeResult);

        public int YDM_GetResult(int nCaptchaId, byte[] pCodeResult);

        public int YDM_Report(int nCaptchaId, boolean bCorrect);

        public int YDM_EasyReport(String lpUserName, String lpPassWord, int nAppId, String lpAppKey, int nCaptchaId, boolean bCorrect);

        public int YDM_GetBalance(String lpUserName, String lpPassWord);

        public int YDM_EasyGetBalance(String lpUserName, String lpPassWord, int nAppId, String lpAppKey);

        public int YDM_SetTimeOut(int nTimeOut);

        public int YDM_Reg(String lpUserName, String lpPassWord, String lpEmail, String lpMobile, String lpQQUin);

        public int YDM_EasyReg(int nAppId, String lpAppKey, String lpUserName, String lpPassWord, String lpEmail, String lpMobile, String lpQQUin);

        public int YDM_Pay(String lpUserName, String lpPassWord, String lpCard);

        public int YDM_EasyPay(String lpUserName, String lpPassWord, long nAppId, String lpAppKey, String lpCard);
    }


    public static Map<String, Object> Imagev(String imagepath) throws Exception {

        Map<String, Object> map = new HashMap<>();


        // 注意这里是普通会员账号，不是开发者账号，注册地址 http://www.yundama.com/index/reg/user
        // 开发者可以联系客服领取免费调试题分
        String username = "caoheike";
        String password = "598415805";

        // 测试时可直接使用默认的软件ID密钥，但要享受开发者分成必须使用自己的软件ID和密钥
        // 1. http://www.yundama.com/index/reg/developer 注册开发者账号
        // 2. http://www.yundama.com/developer/myapp 添加新软件
        // 3. 使用添加的软件ID和密钥进行开发，享受丰厚分成
        int appid = 1;
        String appkey = "22cc5376925e9387a23cf797cb9ba745";

        // 图片路径

        //  例：1004表示4位字母数字，不同类型收费不同。请准确填写，否则影响识别率。在此查询所有类型 http://www.yundama.com/price.html
        int codetype = 1004;

        // 只需要在初始的时候登陆一次
        int uid = 0;
        YDM.INSTANCE.YDM_SetAppInfo(appid, appkey);            // 设置软件ID和密钥
        uid = YDM.INSTANCE.YDM_Login(username, password);    // 登陆到云打码
        YDM.INSTANCE.YDM_SetTimeOut(20);
        if (uid > 0) {
            System.out.println("登陆成功,正在提交识别...");

            byte[] byteResult = new byte[30];
            int cid = YDM.INSTANCE.YDM_DecodeByPath(imagepath, codetype, byteResult);
            String strResult = new String(byteResult, "UTF-8").trim();

            // 返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
            map.put("cid", cid);
            map.put("strResult", strResult);
            System.out.println("识别返回代码:" + cid);
            System.out.println("识别返回结果:" + strResult);

        } else {
            System.out.println("登录失败，错误代码为：" + uid);
        }
        return map;
    }

    public static Map<String, Object> getCode(String path) throws Exception {
        // 注意这里是普通会员账号，不是开发者账号，注册地址 http://www.yundama.com/index/reg/user
        // 开发者可以联系客服领取免费调试题分
        Map<String, Object> map = new HashMap<>();

        String username = "caoheike";
        String password = "598415805";

        // 测试时可直接使用默认的软件ID密钥，但要享受开发者分成必须使用自己的软件ID和密钥
        // 1. http://www.yundama.com/index/reg/developer 注册开发者账号
        // 2. http://www.yundama.com/developer/myapp 添加新软件
        // 3. 使用添加的软件ID和密钥进行开发，享受丰厚分成
        int appid = 1;
        String appkey = "22cc5376925e9387a23cf797cb9ba745";

        // 图片路径
        String imagepath = path;

        //  例：1004表示4位字母数字，不同类型收费不同。请准确填写，否则影响识别率。在此查询所有类型 http://www.yundama.com/price.html
        int codetype = 1005;

        // 只需要在初始的时候登陆一次
        int uid = 0;
        YDM.INSTANCE.YDM_SetAppInfo(appid, appkey);            // 设置软件ID和密钥
        uid = YDM.INSTANCE.YDM_Login(username, password);    // 登陆到云打码

        if (uid > 0) {
            System.out.println("登陆成功,正在提交识别...");

            byte[] byteResult = new byte[30];
            int cid = YDM.INSTANCE.YDM_DecodeByPath(imagepath, codetype, byteResult);
            String strResult = new String(byteResult, "UTF-8").trim();

            // 返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
            System.out.println("识别返回代码:" + cid);
            System.out.println("识别返回结果:" + strResult);
            map.put("cid", cid);
            map.put("strResult", strResult);
        } else {
            System.out.println("登录失败，错误代码为：" + uid);
        }
        return map;
    }




//    /**联众打码平台demo ------------------------------------------------------**/
//    /**
//     * 打印四位验证码
//     * @param filePath
//     * @return
//     * @throws Exception
//     */
//    public static Map<String, Object> Imagev(String filePath) throws Exception {
//        Map<String, String> paramMap = getParamMap("4");
//        boolean b = judgeBalance();
//        if(b){
//            Map<String, Object> stringObjectMap = damaDemo(paramMap, filePath);
//            return stringObjectMap;
//        }
//        return null;
//    }
//
//    /**
//     * 打印5位验证码
//     * @param filePath
//     * @return
//     * @throws Exception
//     */
//    public static Map<String, Object> getCode(String filePath) throws Exception {
//        Map<String, String> paramMap = getParamMap("5");
//        boolean b = judgeBalance();
//        if(b){
//            Map<String, Object> stringObjectMap = damaDemo(paramMap, filePath);
//            return stringObjectMap;
//        }
//        return null;
//    }
//
//    /**
//     * 打码给定的验证码图片
//     * @param paramMap
//     * @param filePath
//     * @return
//     */
//    public static Map<String,Object> damaDemo(Map<String, String> paramMap, String filePath ){
//        Map<String ,Object> dataMap=new HashMap<>();
//        String BOUNDARY = "---------------------------68163001211748"; //boundary就是request头和上传文件内容的分隔符
//        String str = "http://v1-http-api.jsdama.com/api.php?mod=php&act=upload";
//        try {
//            URL url = new URL(str);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("content-type", "multipart/form-data; boundary=" + BOUNDARY);
//            connection.setConnectTimeout(30000);
//            connection.setReadTimeout(30000);
//
//            OutputStream out = new DataOutputStream(connection.getOutputStream());
//            // 普通参数
//            if (paramMap != null) {
//                StringBuffer strBuf = new StringBuffer();
//                Iterator<Map.Entry<String, String>> iter = paramMap.entrySet().iterator();
//                while (iter.hasNext()) {
//                    Map.Entry<String, String> entry = iter.next();
//                    String inputName = entry.getKey();
//                    String inputValue = entry.getValue();
//                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
//                    strBuf.append("Content-Disposition: form-data; name=\""
//                            + inputName + "\"\r\n\r\n");
//                    strBuf.append(inputValue);
//                }
//                out.write(strBuf.toString().getBytes());
//            }
//
//            // 图片文件
//            if (filePath != null) {
//                File file = new File(filePath);
//                String filename = file.getName();
//                String contentType = "image/jpeg";//这里看情况设置
//                StringBuffer strBuf = new StringBuffer();
//                strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
//                strBuf.append("Content-Disposition: form-data; name=\""
//                        + "upload" + "\"; filename=\"" + filename + "\"\r\n");
//                strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
//                out.write(strBuf.toString().getBytes());
//                DataInputStream in = new DataInputStream(
//                        new FileInputStream(file));
//                int bytes = 0;
//                byte[] bufferOut = new byte[1024];
//                while ((bytes = in.read(bufferOut)) != -1) {
//                    out.write(bufferOut, 0, bytes);
//                }
//                in.close();
//            }
//            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
//            out.write(endData);
//            out.flush();
//            out.close();
//
//            //读取URLConnection的响应
//            InputStream in = connection.getInputStream();
//            ByteArrayOutputStream bout = new ByteArrayOutputStream();
//            byte[] buf = new byte[1024];
//            while (true) {
//                int rc = in.read(buf);
//                if (rc <= 0) {
//                    break;
//                } else {
//                    bout.write(buf, 0, rc);
//                }
//            }
//            in.close();
//            //结果输出
//            String result=new String(bout.toByteArray());
//            JSONObject jsonObject = JSONObject.fromObject(result);
//            String result1 = jsonObject.getString("result");
//            if(result1!=null&&result1.equals("true")){
//                String string = jsonObject.getJSONObject("data").getString("val");
//                String id = jsonObject.getJSONObject("data").getString("id");
//                dataMap.put("cid",id);
//                dataMap.put("strResult",string);
//                logger.warn("本次打码id:"+id+"  本地打码结果:"+string);
//            }else{
//                dataMap.put("cid","0001");
//                dataMap.put("strResult","error");
//                logger.warn("本次打码id:0001   本地打码结果:error");
//            }
//        } catch (Exception e) {
//            logger.warn("本次打码id:0002   本地打码结果:打码过程中出现异常",e);
//            dataMap.put("cid","0002");
//            dataMap.put("strResult","error");
//        }
//        return dataMap;
//    }
//
//    /**
//     * 参数信息
//     *
//     * @return
//     */
//    private static Map<String, String> getParamMap(String maxle) {
//        Map<String, String> paramMap = new HashMap<String, String>();
//        paramMap.put("user_name", "caoehike");
//        paramMap.put("user_pw", "weizai@123");
//        paramMap.put("yzm_minlen", maxle);
//        paramMap.put("yzm_maxlen", maxle);
//        paramMap.put("yzmtype_mark", "0");
//        paramMap.put("zztool_token", "123");
//        return paramMap;
//    }
//
//    /**
//     * 判断当前账号剩余点数
//     * @return
//     * @throws IOException
//     */
//    private static boolean judgeBalance() throws IOException {
//        HttpClient httpClient=new HttpClient();
//        PostMethod post=new PostMethod("http://v1-http-api.jsdama.com/api.php?mod=php&act=point");
//        NameValuePair[] par=new NameValuePair[2];
//        par[0]=new NameValuePair("user_name","caoehike");
//        par[1]=new NameValuePair("user_pw","weizai@123");
//        post.addParameters(par);
//        httpClient.executeMethod(post);
//        String responseBodyAsString = post.getResponseBodyAsString();
//        System.out.println(responseBodyAsString);
//        JSONObject json=JSONObject.fromObject(responseBodyAsString);
//        String data = json.getString("data");
//        int i = Integer.parseInt(data);
//        logger.warn("-------------当前打码平台剩余点数："+data);
//        if(i<1){
//            logger.warn("----------------请注意,打码平台剩余点数不足-----------------");
//            return false;
//        }
//        return true;
//    }
//
//    public static void main(String[] args) throws Exception {
//        Map<String, Object> imagev = MyCYDMDemo.Imagev("f://1234.png");
//        System.out.println(imagev.toString());
//    }
}


