package com.reptile.service.businfoservice;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.CustomException;
import com.reptile.util.JavaExcuteJs;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.util.PropertyExpander;

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
	public static String gtDaMaUrl = "http://jiyanapi.c2567.com/shibie";

	/**
	 * 本方法进行webclient的常用设置 是否使用代理取决于传入对象
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
	 * @param goalUrl
	 *            目标地区工商网地址
	 * @param formDataMap
	 *            如果提交信息时需要页面的某些数据{"form":"0"（页面第1个表单）,"idname":"0"(第一个表单中的第1个input),"name":"1"}
	 *            ||{"noForm":"0"（没有表单）,"idname":"0"(页面第1个input),"name":"1"}
	 *            不需要则提交空集合或者null
	 * @param getGtUrl
	 *            获取目标网址gt信息
	 * @param validateUrl
	 *            验证打码极验后返回的数据地址
	 * @param subInfoUrl
	 *            提交信息
	 * @param paramMap
	 *            需要查询的数据{"key":"needToSelect"} 最后一步需要提交的参数
	 * @param needValidateResult
	 *            第5步是否需要第三步的验证结果，需要的话list中放入key值，不需要传空集合或者null
	 *            {验证结果中返回的key:最后一步需要提交数据的key}
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static Map<String, Object> getCompanyInfo(WebClient webClient, String goalUrl,
			Map<String, String> formDataMap, Map<String, String> needValidateResult, String getGtUrl,
			String validateUrl, String subInfoUrl, Map<String, String> paramMap, String encodeType) throws Exception {
		Map<String, Object> resultMap = new HashMap<>(16);

		// 获取gt参数
		JSONObject gtJson = new JSONObject();
		int boundCount=3;
		try {
			for (int count = 0; count < boundCount; count++) {
				logger.warn("gt信息获取中...");
				gtJson = getCodePram(webClient, goalUrl, formDataMap, getGtUrl, paramMap);
				logger.warn("gt极验打码中...");
				gtJson = daMaGT(webClient, gtJson);
				String flagOk="ok";
				if (gtJson.getString("status").equals(flagOk)) {
					logger.warn("gt验证打码结果中...");
					String validateResult = validateGT(webClient, gtJson, validateUrl);

					if (validateResult.contains("true") || validateResult.contains("ok")
							|| validateResult.contains("success")) {
						if (needValidateResult != null && needValidateResult.size() > 0) {
							JSONObject jsonObject = JSONObject.fromObject(validateResult);
							Set<String> keySet = needValidateResult.keySet();
							Iterator<String> iterator = keySet.iterator();
							while (iterator.hasNext()) {
								String titile = iterator.next().toString();
								String key = needValidateResult.get(titile).toString();
								String value = jsonObject.getString(titile);
								// 处理特殊情况，返回值里面含有请求连接 eg:山西 验证结果含有第五步需要的连接地址
								// {"success":true,"msg":"操作成功","obj":"searchList.jspx?top=top&checkNo=8295c961a88171fbeb7fa690a4ec8acd","attributes":null}
								if (value.contains(gtJson.getString("validate"))) {
									value = gtJson.getString("validate");
								}
								paramMap.put(key, value);
							}
						}
						break;
					}
				}
			}
			logger.warn("获取匹配企业中...");
			resultMap = getAllCompany(webClient, gtJson, subInfoUrl, paramMap, encodeType);
			return resultMap;
		} catch (CustomException e) {
			logger.warn(e.getExceptionInfo(), e);
			resultMap.put("errorCode", "0001");
			resultMap.put("errorInfo", "网络异常");
			return resultMap;
		} catch (Exception e) {
			logger.warn("循环打码出错", e);
			resultMap.put("errorCode", "0002");
			resultMap.put("errorInfo", "网络异常");
			return resultMap;
		}
	}

	/**
	 * 获取官网gt信息
	 *
	 * @param webClient
	 * @param getGtUrl
	 *            获取gt信息地址
	 * @return
	 * @throws IOException
	 */
	public static JSONObject getCodePram(WebClient webClient, String goalUrl, Map<String, String> formDataMap,
			String getGtUrl, Map<String, String> paramMap) {

		Map<String, String> map = new HashMap<>(16);
		JSONObject jsonObject = new JSONObject();
		// 如果最终需要提交表单中的某些数据 循环迭代formDataMap从页面拿去数据，以key value的形式放入paramMap中，最终拼接在请求地址中
		try {
			HtmlPage page = webClient.getPage(new URL(goalUrl));
			if (formDataMap != null && formDataMap.size() > 1) {
				DomNodeList inputList = null;
				String formFlag="form";
				String noFormFlag="noForm";
				if (formDataMap.get(formFlag) != null) {
					int formIndex = Integer.parseInt(formDataMap.get("form").toString());
					HtmlForm htmlForm = page.getForms().get(formIndex);
					formDataMap.remove("form");
					inputList = htmlForm.getElementsByTagName("input");
				} else if (formDataMap.get(noFormFlag) != null) {
					int noForm = Integer.parseInt(formDataMap.get("noForm").toString());
					formDataMap.remove("noForm");
					inputList = page.getElementsByTagName("input");
				} else {
					throw new CustomException("参数格式错误", new Exception());
				}
				Set<Map.Entry<String, String>> entries = formDataMap.entrySet();
				for (Map.Entry<String, String> input : entries) {
					int index = Integer.parseInt(input.getValue());
					String value = ((HtmlElement) (inputList.get(index))).getAttribute("value");
					String key = input.getKey();
					map.put(key, value);
				}
			}
			paramMap.putAll(map);

			Object gtPage = webClient.getPage(getGtUrl);
			if (gtPage instanceof TextPage) {
				TextPage tpage = (TextPage) gtPage;
				jsonObject = JSONObject.fromObject(tpage.getContent());
			} else {
				UnexpectedPage unPage = (UnexpectedPage) gtPage;
				jsonObject = JSONObject.fromObject(unPage.getWebResponse().getContentAsString());
			}

			System.out.println("获取到gt信息：" + jsonObject.toString());
			return jsonObject;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("获取gt信息出错", e);
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
			// 判断极验为第几代
			String flagInt="1";
			String model = gtJson.getString("success").equals(flagInt) ? "0" : "1";
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
		} catch (Exception e) {
			throw new CustomException("极验打码失败", e);
		}
	}

	/**
	 * 校验打码是否可以通过目标网址验证
	 *
	 * @param webClient
	 * @param gtJson
	 *            打码平台返回的数据
	 * @param validateUrl
	 *            目标网址极验结构提交地址
	 * @return
	 */
	public static String validateGT(WebClient webClient, JSONObject gtJson, String validateUrl) {

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
			return result;
		} catch (Exception e) {
			throw new CustomException("校验打码结果失败", e);
		}
	}

	/**
	 * 提交查询信息获取匹配的所有公司
	 *
	 * @param webClient
	 * @param gtJson
	 *            极验打码结果
	 * @param subInfoUrl
	 *            提交地址
	 * @param paramMap
	 *            查询所需要的参数
	 * @param encodeType
	 *            编码方式
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static Map<String, Object> getAllCompany(WebClient webClient, JSONObject gtJson, String subInfoUrl,
			Map<String, String> paramMap, String encodeType) throws IOException, InterruptedException {
		Map<String, Object> map = new HashMap<>(16);
		StringBuffer param = new StringBuffer();
		try {
			Iterator<String> iterator = paramMap.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = paramMap.get(key);
				param.append("&" + key + "=" + URLEncoder.encode(value, encodeType));
			}
			String paramStr = param.toString();

			String str = subInfoUrl + "geetest_challenge=" + gtJson.getString("challenge") + "&geetest_validate="
					+ gtJson.getString("validate") + "&geetest_seccode=" + gtJson.getString("validate") + "|jordan"
					+ paramStr;
			System.out.println(str);
			WebRequest post = new WebRequest(new URL(str));
			post.setHttpMethod(HttpMethod.POST);
			Object page3 = webClient.getPage(post);
			if (page3 instanceof HtmlPage) {
				HtmlPage page = (HtmlPage) page3;
				System.out.println(page.asText());
			} else {
				TextPage page=(TextPage) page3;
				System.out.println(page.getContent());
			}
			Thread.sleep(3000);

			map.put("errorCode", "1000");
			map.put("errorInfo", "成功获取匹配的所有企业");
			map.put("GSHtmlPage", page3);
			map.put("GSWebClient", webClient);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("获取匹配信息失败", e);
		}
	}

	/**
	 * 以吉林为例 最终提交方式为get
	 * 提交验证参数的key和其他不一样（challenge!=geetest_challenge），url不需要urlencode
	 * 
	 * @param webClient
	 * @param gtJson
	 * @param subInfoUrl
	 * @param paramMap
	 * @param encodeType
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static Map<String, Object> getAllCompanyJL(WebClient webClient, JSONObject gtJson, String subInfoUrl,
			Map<String, String> paramMap, String encodeType) throws IOException, InterruptedException {
		Map<String, Object> map = new HashMap<>(16);
		StringBuffer param = new StringBuffer();
		try {
			Iterator<String> iterator = paramMap.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = paramMap.get(key);
				param.append("&" + key + "=" + value);
			}
			String paramStr = param.toString();

			String str = subInfoUrl + "challenge=" + gtJson.getString("challenge") + "&validate="
					+ gtJson.getString("validate") + "&seccode=" + gtJson.getString("validate") + "|jordan" + paramStr;
			System.out.println(str);
			WebRequest post = new WebRequest(new URL(str));
			post.setHttpMethod(HttpMethod.GET);
			HtmlPage page3 = webClient.getPage(post);
			Thread.sleep(3000);
			System.out.println(page3.asText());
			map.put("errorCode", "1000");
			map.put("errorInfo", "成功获取匹配的所有企业");
			map.put("GSHtmlPage", page3);
			map.put("GSWebClient", webClient);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("获取匹配信息失败", e);
		}
	}

	public static void main(String[] args) throws Exception {
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
		webClient = createWebClient(webClient);

		/**
		 * 陕西工商信息
		 */
		// String goalUrl = "http://sn.gsxt.gov.cn";
		// String getGtUrl = "http://sn.gsxt.gov.cn/StartCaptchaServlet?time=" +
		// System.currentTimeMillis();
		// String validateUrl = "http://sn.gsxt.gov.cn/VerifyLoginServlet?time=" +
		// System.currentTimeMillis();
		// String subInfoUrl =
		// "http://sn.gsxt.gov.cn/ztxy.do?method=sslist&djjg=&random=" +
		// System.currentTimeMillis()+"&";
		// Map<String, String> paramMap = new HashMap<>();
		// paramMap.put("maent.entname", "百度");
		// Map<String, String> dataMap = new HashMap<>();//表单中存在数据 页面第一个表单 第一个input
		// dataMap.put("form", "0");
		// dataMap.put("type", "0");
		// Map<String, String> needValidateResult = new HashMap<>();
		// String encodeType = "gb2312";

		/**
		 *
		 * 北京工商信息
		 */
		// String goalUrl="http://bj.gsxt.gov.cn";
		// String
		// getGtUrl="http://bj.gsxt.gov.cn/pc-geetest/register?t="+System.currentTimeMillis();
		// String validateUrl="http://bj.gsxt.gov.cn/pc-geetest/validate";
		// String
		// subInfoUrl="http://bj.gsxt.gov.cn/es/esAction!entlist.dhtml?urlflag=0&";
		// Map<String,String> paramMap=new HashMap<>();
		// paramMap.put("keyword","百度");
		// Map<String,String> dataMap=new HashMap<>();
		// dataMap.put("form","0");
		// dataMap.put("urlflag","2");
		// dataMap.put("nowNum","0");
		// dataMap.put("clear","3");
		// Map<String, String> needValidateResult = new HashMap<>();
		//
		// String encodeType="utf-8";

		/**
		 * 四川工商信息
		 */
		// String goalUrl="http://sc.gsxt.gov.cn/notice";
		// String getGtUrl="http://sc.gsxt.gov.cn/notice/pc-geetest/register?t=" +
		// System.currentTimeMillis();
		// String validateUrl="http://sc.gsxt.gov.cn/notice/pc-geetest/validate";
		// String subInfoUrl="http://sc.gsxt.gov.cn/notice/search/ent_info_list?";
		// Map<String,String> paramMap=new HashMap<>();
		// paramMap.put("condition.keyword","百度");
		// paramMap.put("condition.searchType","1");
		// String encodeType="utf-8";
		// Map<String,String> dataMap=new HashMap<>();
		// dataMap.put("form","0");
		// dataMap.put("captcha","1");
		// dataMap.put("session.token","5");
		// Map<String, String> needValidateResult = new HashMap<>();

		/**
		 * 福建工商信息
		 *
		 */
		// String goalUrl = "http://fj.gsxt.gov.cn";
		// String getGtUrl = "http://fj.gsxt.gov.cn/notice/pc-geetest/register?t=" +
		// System.currentTimeMillis();
		// String validateUrl = "http://fj.gsxt.gov.cn/notice/pc-geetest/validate";
		// String subInfoUrl = "http://fj.gsxt.gov.cn/notice/search/ent_info_list?";
		// Map<String, String> paramMap = new HashMap<>();
		// paramMap.put("condition.keyword", "百度");
		// paramMap.put("condition.searchType", "1");
		// paramMap.put("captcha", "");
		// //表单中存在数据 页面第一个表单 第一个input
		// Map<String, String> dataMap = new HashMap<>();
		// dataMap.put("form", "0");
		// dataMap.put("session.token", "5");
		// Map<String, String> needValidateResult = new HashMap<>();
		// String encodeType = "utf-8";

		/**
		 *
		 * 河北工商信息
		 */
		// String goalUrl = "http://he.gsxt.gov.cn";
		// String getGtUrl = "http://he.gsxt.gov.cn/notice/pc-geetest/register?t=" +
		// System.currentTimeMillis();
		// String validateUrl = "http://he.gsxt.gov.cn/notice/pc-geetest/validate";
		// String subInfoUrl = "http://he.gsxt.gov.cn/notice/search/ent_info_list?";
		// Map<String, String> paramMap = new HashMap<>();
		// paramMap.put("condition.keyword", "百度");
		// paramMap.put("condition.searchType", "1");
		// paramMap.put("captcha", "");
		// //表单中存在数据 页面第一个表单 第一个input
		// Map<String, String> dataMap = new HashMap<>();
		// dataMap.put("form", "0");
		// dataMap.put("session.token", "5");
		// String encodeType = "utf-8";
		// Map<String, String> needValidateResult = new HashMap<>();

		/**
		 * 山西
		 */
		// String goalUrl = "http://sx.gsxt.gov.cn";
		// String getGtUrl = "http://sx.gsxt.gov.cn/registerValidate.jspx?t=" +
		// System.currentTimeMillis();
		// String validateUrl = "http://sx.gsxt.gov.cn/validateSecond.jspx";
		// String subInfoUrl = "http://sx.gsxt.gov.cn/searchList.jspx?";
		// Map<String, String> paramMap = new HashMap<>();
		// paramMap.put("searchType", "1");
		// paramMap.put("entName", "百度");
		// paramMap.put("top", "top");
		//
		// //表单中存在数据 页面第一个表单 第一个input
		// Map<String, String> dataMap = new HashMap<>();
		// String encodeType = "utf-8";
		// //验证结果中含有最后一步需要提交的数据 没有则提交null;
		// Map<String, String> needValidateResult = new HashMap<>();
		// //{验证结果中返回的key:最后一步需要提交数据的key}
		// needValidateResult.put("obj", "checkNo");

		/**
		 * 辽宁
		 */
		// String goalUrl = "http://ln.gsxt.gov.cn";
		// String getGtUrl = "http://ln.gsxt.gov.cn/saicpub/pc-geetest/register?t=" +
		// System.currentTimeMillis();
		// String validateUrl = "http://ln.gsxt.gov.cn/saicpub/pc-geetest/validate";
		// String subInfoUrl =
		// "http://ln.gsxt.gov.cn/saicpub/entPublicitySC/entPublicityDC/lngsSearchFpc.action?";
		// Map<String, String> paramMap = new HashMap<>();
		// paramMap.put("authCode", "finish");
		// paramMap.put("method", "searchValidate");
		// paramMap.put("searchType", "qyxyxx");
		// paramMap.put("solrCondition", "中国出口商品基地建设辽宁公司");
		//
		//
		// //表单中存在数据 页面第一个表单 第一个input
		// Map<String, String> dataMap = new HashMap<>();
		// String encodeType = "utf-8";
		// //验证结果中含有最后一步需要提交的数据 没有则提交null;
		// Map<String, String> needValidateResult = new HashMap<>();
		//
		// Map<String, Object> companyInfo = getCompanyInfo(webClient, goalUrl, dataMap,
		// needValidateResult, getGtUrl, validateUrl, subInfoUrl, paramMap, encodeType);
		// WebClient webClient1 = (WebClient) companyInfo.get("GSWebClient");
		// WebRequest post=new WebRequest(new
		// URL("http://ln.gsxt.gov.cn/saicpub/entPublicitySC/entPublicityDC/lngsSearchFpc!searchSolr.action?solrCondition="+URLEncoder.encode("中国出口商品基地建设辽宁公司","utf-8")));
		// post.setHttpMethod(HttpMethod.POST);
		// List<NameValuePair> list = new ArrayList<>();
		// list.add(new NameValuePair("authCode", "finish"));
		// list.add(new NameValuePair("currentPage", "1"));
		// list.add(new NameValuePair("pageSize", "10"));
		// post.setRequestParameters(list);
		// UnexpectedPage page = webClient1.getPage(post);
		// System.out.println(page.getWebResponse().getContentAsString("utf-8"));

		/**
		 * 吉林
		 */
		// String goalUrl = "http://jl.gsxt.gov.cn";
		// String getGtUrl = "http://jl.gsxt.gov.cn/api/Common/GetCaptcha?t=" +
		// System.currentTimeMillis();
		// String subInfoUrl = "http://jl.gsxt.gov.cn/SearchResult.html?";
		// Map<String, String> paramMap = new HashMap<>();
		// paramMap.put("keyword",new
		// String(Base64.getEncoder().encode("110".getBytes())));
		// paramMap.put("searchType", "searchNormal");
		// //表单中存在数据 页面第一个表单 第一个input
		// Map<String, String> dataMap = new HashMap<>();
		// String encodeType = "utf-8";
		//
		// JSONObject codePram = getCodePram(webClient, goalUrl, dataMap, getGtUrl,
		// paramMap);
		// JSONObject jsonObject = daMaGT(webClient, codePram);
		// getAllCompanyJL(webClient, jsonObject, subInfoUrl, paramMap, encodeType);

		/**
		 * 黑龙江
		 */
		// String goalUrl = "http://hl.gsxt.gov.cn";
		// String getGtUrl = "http://hl.gsxt.gov.cn/registerValidate.jspx?t=" +
		// System.currentTimeMillis();
		// String validateUrl = "http://hl.gsxt.gov.cn/validateSecond.jspx";
		// String subInfoUrl = "http://hl.gsxt.gov.cn/searchList.jspx?";
		// Map<String, String> paramMap = new HashMap<>();
		// paramMap.put("top", "top");
		// paramMap.put("entName", "百度");
		// paramMap.put("searchType", "1");
		//
		// //表单中存在数据 页面第一个表单 第一个input
		// Map<String, String> dataMap = new HashMap<>();
		// String encodeType = "utf-8";
		// //验证结果中含有最后一步需要提交的数据 没有则提交null;
		// Map<String, String> needValidateResult = new HashMap<>();
		// //{验证结果中返回的key:最后一步需要提交数据的key}
		// needValidateResult.put("obj", "checkNo");

		/**
		 * 上海
		 */
		// String goalUrl = "http://sh.gsxt.gov.cn";
		// String getGtUrl = "http://sh.gsxt.gov.cn/notice/pc-geetest/register?t=" +
		// System.currentTimeMillis();
		// String validateUrl = "http://sh.gsxt.gov.cn/notice/pc-geetest/validate";
		// String subInfoUrl = "http://sh.gsxt.gov.cn/notice/search/ent_info_list?";
		// Map<String, String> paramMap = new HashMap<>();
		// paramMap.put("condition.searchType","1");
		// paramMap.put("captcha","");
		// paramMap.put("condition.keyword","百度");
		// //表单中存在数据 页面第一个表单 第一个input
		// Map<String, String> dataMap = new HashMap<>();
		// dataMap.put("form","0");
		// dataMap.put("session.token","5");
		//
		// String encodeType = "utf-8";
		// //验证结果中含有最后一步需要提交的数据 没有则提交null;
		// Map<String, String> needValidateResult = new HashMap<>();

		/**
		 * 安徽
		 */
		// String goalUrl = "http://ah.gsxt.gov.cn";
		// String getGtUrl = "http://ah.gsxt.gov.cn/registerValidate.jspx?t=" +
		// System.currentTimeMillis();
		// String validateUrl = "http://ah.gsxt.gov.cn/validateSecond.jspx";
		// String subInfoUrl = "http://ah.gsxt.gov.cn/searchList.jspx?";
		// Map<String, String> paramMap = new HashMap<>();
		// paramMap.put("top","top");
		// paramMap.put("searchType","1");
		// paramMap.put("entName",URLEncoder.encode("百度","utf-8"));
		// //表单中存在数据 页面第一个表单 第一个input
		// Map<String, String> dataMap = new HashMap<>();
		//
		// String encodeType = "utf-8";
		// //验证结果中含有最后一步需要提交的数据 没有则提交null;
		// Map<String, String> needValidateResult = new HashMap<>();
		// needValidateResult.put("obj","checkNo");

		/**
		 * 江西
		 * 加载的js需要引入相对路径
		 */
//		String goalUrl = "http://jx.gsxt.gov.cn";
//		String getGtUrl = "http://jx.gsxt.gov.cn/start/querygeetest.do?v=" + System.currentTimeMillis();
//		String subInfoUrl = "http://jx.gsxt.gov.cn/vfygeettest/querygeetest?";
//		Map<String, String> paramMap = new HashMap<>();
//		paramMap.put("searchtype", "qyxy");
//		String excuteJs = new JavaExcuteJs().excuteJs("/js/base64.js","encode","百度");
//		paramMap.put("searchkey", excuteJs);
//		paramMap.put("entname", "百度");
//		// 表单中存在数据 页面第一个表单 第一个input
//		Map<String, String> dataMap = new HashMap<>();
//		String encodeType = "utf-8";
//
//		JSONObject codePram = getCodePram(webClient, goalUrl, dataMap, getGtUrl, paramMap);
//		JSONObject daMaGT = daMaGT(webClient, codePram);
//		Map<String, Object> allCompanyJL = getAllCompany(webClient, daMaGT, subInfoUrl, paramMap, encodeType);
//		
//		Object object = allCompanyJL.get("GSHtmlPage");
//		TextPage page=(TextPage) object;
//		String asText = page.getContent();
//		String string2 = JSONObject.fromObject(asText).getString("url");
//		HtmlPage page1 = webClient.getPage("http://jx.gsxt.gov.cn/"+string2);
//		System.out.println(page1.asText());
		
		
		/**
		 *
		 */
		 String goalUrl = "http://ah.gsxt.gov.cn";
		 String getGtUrl = "http://ah.gsxt.gov.cn/registerValidate.jspx?t=" +System.currentTimeMillis();
		 String validateUrl = "http://ah.gsxt.gov.cn/validateSecond.jspx";
		 String subInfoUrl = "http://ah.gsxt.gov.cn/searchList.jspx?";
		 Map<String, String> paramMap = new HashMap<>(16);
		 paramMap.put("top","top");
		 paramMap.put("searchType","1");
		 paramMap.put("entName",URLEncoder.encode("百度","utf-8"));
		 //表单中存在数据 页面第一个表单 第一个input
		 Map<String, String> dataMap = new HashMap<>(16);
		
		 String encodeType = "utf-8";
		 //验证结果中含有最后一步需要提交的数据 没有则提交null;
		 Map<String, String> needValidateResult = new HashMap<>(16);
		 needValidateResult.put("obj","checkNo");
		
		 Map<String, Object> companyInfo = getCompanyInfo(webClient, goalUrl, dataMap,needValidateResult, getGtUrl, validateUrl, subInfoUrl, paramMap, encodeType);
	}
}
