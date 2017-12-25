package com.reptile.service.businfoservice;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.CustomException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;


/**
 * 工商网获取信息模板
 *
 * @author mrlu
 * @date 2016/12/23
 */
public class CompanyInfoModel {
    private static Logger logger = LoggerFactory.getLogger(CompanyInfoModel.class);
    public static  String gtDaMaUrl = "http://jiyanapi.c2567.com/shibie";

    /**
     * 本方法进行webclient的常用设置
     * 是否使用代理取决于传入对象
     *
     * @return
     */
    public static WebClient createWebClient(WebClient webClient) {
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getOptions().setTimeout(90000);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.setJavaScriptTimeout(10000);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        return webClient;
    }

    /**
     * 获取工商网信息
     *
     * @param webClient
     * @param goalUrl     目标地区工商网地址
     * @param formDataMap 如果提交信息时需要页面的某些数据{"form":"0"（页面第1个表单）,"idname":"0"(页面第1个input),"name":"1"}
     * @param getGtUrl    获取目标网址gt信息
     * @param validateUrl 验证打码极验后返回的数据地址
     * @param subInfoUrl  提交信息
     * @param paramMap    需要查询的数据{"key":"needToSelect"}
     * @throws IOException
     * @throws InterruptedException
     */
    public static Map<String, Object> getCompanyInfo(WebClient webClient, String goalUrl, Map<String, String> formDataMap, String getGtUrl, String validateUrl, String subInfoUrl, Map<String, String> paramMap, String encodeType) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        //获取gt参数
        JSONObject gtJson = new JSONObject();
        try {
            for (int count = 0; count < 3; count++) {
                logger.warn("gt信息获取中...");
                gtJson = getCodePram(webClient, goalUrl, formDataMap,getGtUrl,paramMap);
                logger.warn("gt极验打码中...");
                gtJson = daMaGT(webClient, gtJson);
                if (gtJson.getString("status").equals("ok")) {
                    logger.warn("gt验证打码结果中...");
                    boolean flag = validateGT(webClient, gtJson, validateUrl);
                    if(flag){
                        break;
                    }
                }
            }
            logger.warn("获取匹配企业中...");
            resultMap = getAllCompany(webClient, gtJson, subInfoUrl, paramMap, encodeType);
            return resultMap;
        } catch (CustomException e) {
            logger.warn(e.getExceptionInfo(),e);
            resultMap.put("errorCode", "0001");
            resultMap.put("errorInfo", "网络异常");
            return resultMap;
        }catch (Exception e){
            logger.warn("循环打码出错",e);
            resultMap.put("errorCode", "0002");
            resultMap.put("errorInfo", "网络异常");
            return resultMap;
        }
    }

    /**
     * 获取官网gt信息
     *
     * @param webClient
     * @param getGtUrl  获取gt信息地址
     * @return
     * @throws IOException
     */
    public static JSONObject getCodePram(WebClient webClient, String goalUrl, Map<String, String> formDataMap, String getGtUrl,Map<String, String> paramMap){

        Map<String, String> map = new HashMap<>();
        JSONObject jsonObject=new JSONObject();
        //如果最终需要提交表单中的某些数据 循环迭代formDataMap从页面拿去数据，以key value的形式放入paramMap中，最终拼接在请求地址中
        try {
            HtmlPage page = webClient.getPage(new URL(goalUrl));
            if (formDataMap != null && formDataMap.size() > 1) {
                int formIndex = Integer.parseInt(formDataMap.get("form").toString());
                HtmlForm htmlForm = page.getForms().get(formIndex);
                formDataMap.remove("form");
                DomNodeList<HtmlElement> inputList = htmlForm.getElementsByTagName("input");
                Set<Map.Entry<String, String>> entries = formDataMap.entrySet();
                for (Map.Entry<String, String> input : entries) {
                    int index = Integer.parseInt(input.getValue());
                    String value = inputList.get(index).getAttribute("value");
                    String key = input.getKey();
                    map.put(key, value);
                }
            }
            paramMap.putAll(map);

            TextPage gtPage = webClient.getPage(getGtUrl);
            jsonObject = JSONObject.fromObject(gtPage.getContent());
            System.out.println("获取到gt信息："+jsonObject.toString());
            return jsonObject;
        }catch (Exception e){
            e.printStackTrace();
            throw  new CustomException("获取gt信息出错",e);
        }
    }

    /**
     * 极验打码
     *
     * @param webClient
     * @param gtJson
     * @return
     * @throws IOException
     */
    public static JSONObject daMaGT(WebClient webClient, JSONObject gtJson) throws MalformedURLException {
        try {
            WebRequest get = new WebRequest(new URL(gtDaMaUrl));
            get.setHttpMethod(HttpMethod.GET);
            //判断极验为第几代
            String model = gtJson.getString("success").equals("1") ? "0" : "1";
            List<NameValuePair> list = new ArrayList<>();
            list.add(new NameValuePair("user", "caoheike"));
            list.add(new NameValuePair("pass", "598415805"));
            list.add(new NameValuePair("return", "json"));
            list.add(new NameValuePair("referer", "http://www.gsxt.gov.cn"));
            list.add(new NameValuePair("model", model));
            list.add(new NameValuePair("ip", ""));
            list.add(new NameValuePair("gt", gtJson.getString("gt")));
            list.add(new NameValuePair("challenge", gtJson.getString("challenge")));
            get.setRequestParameters(list);
            TextPage page2 = webClient.getPage(get);
            gtJson = JSONObject.fromObject(page2.getContent());
            System.out.println("极验打码结果:" + gtJson.toString());
            return gtJson;
        }catch (Exception e){
            throw new CustomException("极验打码失败",e);
        }
    }

    /**
     * 校验打码是否可以通过目标网址验证
     * @param webClient
     * @param gtJson      打码平台返回的数据
     * @param validateUrl 目标网址极验结构提交地址
     * @return
     */
    public static boolean validateGT(WebClient webClient, JSONObject gtJson, String validateUrl) {

        try {
            WebRequest request = new WebRequest(new URL(validateUrl));
            request.setHttpMethod(HttpMethod.POST);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new NameValuePair("geetest_challenge", gtJson.getString("challenge")));
            list.add(new NameValuePair("geetest_validate", gtJson.getString("validate")));
            list.add(new NameValuePair("geetest_seccode", gtJson.getString("validate") + "|Cjordan"));
            request.setRequestParameters(list);
            TextPage page4 = webClient.getPage(request);
            String result = page4.getContent();
            System.out.println("极验校准结果:" + result);
            return result.contains("success") ? true : false;
        }catch (Exception e){
            throw new CustomException("校验打码结果失败",e);
        }
    }

    /**
     * 提交查询信息获取匹配的所有公司
     *
     * @param webClient
     * @param gtJson     极验打码结果
     * @param subInfoUrl 提交地址
     * @param paramMap   查询所需要的参数
     * @param encodeType 编码方式
     * @throws IOException
     * @throws InterruptedException
     */
    public static Map<String,Object> getAllCompany(WebClient webClient, JSONObject gtJson, String subInfoUrl, Map<String, String> paramMap, String encodeType) throws IOException, InterruptedException {
        Map<String,Object> map=new HashMap<>();
        StringBuffer param = new StringBuffer();
        try {
            Iterator<String> iterator = paramMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = paramMap.get(key);
                param.append("&" + key + "=" + URLEncoder.encode(value, encodeType));
            }
            String paramStr = param.toString();

            String str = subInfoUrl + "geetest_challenge=" + gtJson.getString("challenge") +
                    "&geetest_validate=" + gtJson.getString("validate") + "&geetest_seccode=" + gtJson.getString("validate") + "|jordan" + paramStr;
            System.out.println(str);
            WebRequest post = new WebRequest(new URL(str));
            post.setHttpMethod(HttpMethod.POST);
            HtmlPage page3 = webClient.getPage(post);
            Thread.sleep(3000);
            System.out.println(page3.asText());
            map.put("errorCode","1000");
            map.put("errorInfo","成功获取匹配的所有企业");
            map.put("GSHtmlPage",page3);
            return map;
        }catch (Exception e){
            throw new CustomException("获取匹配信息失败",e);
        }
    }

    public static void main(String[] args) throws Exception {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
        webClient = createWebClient(webClient);

        /**
         * 陕西工商信息
         */
//        String goalUrl = "http://sn.gsxt.gov.cn";
//        String getGtUrl = "http://sn.gsxt.gov.cn/StartCaptchaServlet?time=" + System.currentTimeMillis();
//        String validateUrl = "http://sn.gsxt.gov.cn/VerifyLoginServlet?time=" + System.currentTimeMillis();
//        String subInfoUrl = "http://sn.gsxt.gov.cn/ztxy.do?method=sslist&djjg=&random=" + System.currentTimeMillis()+"&";
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("maent.entname", "百度");
//        Map<String, String> dataMap = new HashMap<>();//表单中存在数据 页面第一个表单 第一个input
//        dataMap.put("form", "0");
//        dataMap.put("type", "0");
//        String encodeType = "gb2312";

        /**
         *
         * 北京工商信息
         */
//        String goalUrl="http://bj.gsxt.gov.cn";
//        String getGtUrl="http://bj.gsxt.gov.cn/pc-geetest/register?t="+System.currentTimeMillis();
//        String validateUrl="http://bj.gsxt.gov.cn/pc-geetest/validate";
//        String subInfoUrl="http://bj.gsxt.gov.cn/es/esAction!entlist.dhtml?urlflag=0&";
//        Map<String,String> paramMap=new HashMap<>();
//        paramMap.put("keyword","百度");
//        String encodeType="utf-8";
//
//        Map<String,String> dataMap=new HashMap<>();
//        dataMap.put("form","0");
//        dataMap.put("urlflag","2");
//        dataMap.put("nowNum","0");
//        dataMap.put("clear","3");

        /**
         * 四川工商信息
         */
//        String goalUrl="http://sc.gsxt.gov.cn/notice";
//        String getGtUrl="http://sc.gsxt.gov.cn/notice/pc-geetest/register?t=" + System.currentTimeMillis();
//        String validateUrl="http://sc.gsxt.gov.cn/notice/pc-geetest/validate";
//        String subInfoUrl="http://sc.gsxt.gov.cn/notice/search/ent_info_list?";
//        Map<String,String> paramMap=new HashMap<>();
//        paramMap.put("condition.keyword","百度");
//        paramMap.put("condition.searchType","1");
//        String encodeType="utf-8";
//        Map<String,String> dataMap=new HashMap<>();
//        dataMap.put("form","0");
//        dataMap.put("captcha","1");
//        dataMap.put("session.token","5");

        /**
         * 福建工商信息
         *
         */
//        String goalUrl = "http://fj.gsxt.gov.cn";
//        String getGtUrl = "http://fj.gsxt.gov.cn/notice/pc-geetest/register?t=" + System.currentTimeMillis();
//        String validateUrl = "http://fj.gsxt.gov.cn/notice/pc-geetest/validate";
//        String subInfoUrl = "http://fj.gsxt.gov.cn/notice/search/ent_info_list?";
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("condition.keyword", "百度");
//        paramMap.put("condition.searchType", "1");
//        paramMap.put("captcha", "");
//        //表单中存在数据 页面第一个表单 第一个input
//        Map<String, String> dataMap = new HashMap<>();
//        dataMap.put("form", "0");
//        dataMap.put("session.token", "5");
//        String encodeType = "utf-8";

        /**
         * 山东
         */
        String goalUrl = "http://sd.gsxt.gov.cn";
        String getGtUrl = "http://sd.gsxt.gov.cn/pub/geetest/register/" + System.currentTimeMillis()+"?_="+System.currentTimeMillis();
        Map<String, String> dataMap = new HashMap<>();
        Map<String, String> paramMap = new HashMap<>();
        JSONObject codePram = getCodePram(webClient, goalUrl, dataMap, getGtUrl, paramMap);
        System.out.println(codePram);

//        getCompanyInfo(webClient, goalUrl, dataMap, getGtUrl, validateUrl, subInfoUrl, paramMap, encodeType);
    }
}
