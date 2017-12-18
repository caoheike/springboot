package com.reptile.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.FormEncodingType;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.constants.MessageConstamts;
import com.reptile.constants.StatucConstants;

/**
 * 
 * @Title: EntrepreneurQueryService.java
 * @Package com.reptile.service
 * @Description: TODO(工商网处理类)
 * @author Administrator
 * @date 2017年12月16日
 * @version V1.0
 */
@Service
public class EntrepreneurQueryService {
	private static Logger logger = Logger
			.getLogger(EntrepreneurQueryService.class);
	private static final String CURRENCY_URL = "http://www.gsxt.gov.cn/SearchItemCaptcha?v=1513412155000";

	public static void entrepreneurQuer(String companyInfo)
			throws FailingHttpStatusCodeException, IOException {
		/** 设置WebClient **/
		try {
			logger.info("....开始爬取" + companyInfo + "工商网信息....");
			WebClient webClient = setClient();
			WebRequest requests = new WebRequest(new URL(CURRENCY_URL));
			TextPage pages = webClient.getPage(requests);
			new net.sf.json.JSONObject();
			net.sf.json.JSONObject jsonObject = JSONObject.fromObject(pages
					.getContent());
			logger.info("....开始发包获得极验数据....");
			TextPage pages1 = ocr(webClient, jsonObject, companyInfo);
			new net.sf.json.JSONObject();
			net.sf.json.JSONObject jsonObject1 = JSONObject.fromObject(pages1
					.getContent());
			logger.info("....开始发包获得工商网最终数据...");
			HtmlPage pageinfo = getInfo(jsonObject1, webClient, companyInfo);
			if (pageinfo.asText().contains(MessageConstamts.STATUS_01)) {
				logger.warn("....ip问题导致查询失败，服务器返回504...");
			} else if (pageinfo.asText().contains(MessageConstamts.STATUS_02)) {
				logger.warn("....工商网内部出错....");
			} else if (pageinfo.asText().contains(MessageConstamts.STATUS_03)) {
				logger.warn("....由于您操作过于频繁，请稍后返回首页重新操作....");
			} else {
				logger.info("...工商网获得" + pageinfo.asXml() + "信息如下...");
			}

		} catch (java.lang.ClassCastException e) {
			logger.info("...请求出错,需要递归...");
			entrepreneurQuer(companyInfo);
		} catch (net.sf.json.JSONException e) {
			logger.info("...格式出错,需要递归...");
			entrepreneurQuer(companyInfo);
		}

	}

	public static void beijingEntrepreneurQuer(String companyInfo)
			throws FailingHttpStatusCodeException, IOException,
			InterruptedException {
		logger.info("....开始爬取北京工商网信息,被查组织" + companyInfo + "...");
		WebClient client = EntrepreneurQueryService.setClient();
		// 发包获取极验信息
		TextPage page = getOcr(client, companyInfo);
		logger.info("....开始获取极验信息...");
		// 验证极验
		JSONObject jsonObject = authenticationData(page, client);
		logger.info("....开始极验...");
		String info = getInfo(client, jsonObject, companyInfo);
		logger.info("....爬取结束..." + info);

	}

	private static String getInfo(WebClient client, JSONObject jsonObject5,
			String companyInfo) throws FailingHttpStatusCodeException,
			IOException {

		@SuppressWarnings("deprecation")
		WebRequest requests6 = new WebRequest(
				new URL(
						"http://bj.gsxt.gov.cn/es/esAction!entlist.dhtml?nowNum=&keyword="
								+ URLEncoder.encode(companyInfo, "UTF-8")			
								+ "&urlflag=0&clear=%E8%AF%B7%E8%BE%93%E5%85%A5%E4%BC%81%E4%B8%9A%E5%90%8D%E7%A7%B0%E3%80%81%E7%BB%9F%E4%B8%80%E7%A4%BE%E4%BC%9A%E4%BF%A1%E7%94%A8%E4%BB%A3%E7%A0%81%E6%88%96%E6%B3%A8%E5%86%8C%E5%8F%B7"));
		requests6.setHttpMethod(HttpMethod.POST);
		HtmlPage page6 = client.getPage(requests6);
		return page6.asText();

	}

	private static JSONObject authenticationData(TextPage page3,
			WebClient client) throws FailingHttpStatusCodeException,
			IOException {

		JSONObject jsonObject5 = JSONObject.fromObject(page3.getContent());

		WebRequest requests5 = new WebRequest(new URL(
				"http://bj.gsxt.gov.cn/pc-geetest/validate"));

		List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
		reqParam.add(new NameValuePair("geetest_challenge", jsonObject5.get(
				"challenge").toString()));
		reqParam.add(new NameValuePair("geetest_validate", jsonObject5.get(
				"validate").toString()));
		reqParam.add(new NameValuePair("geetest_seccode", jsonObject5.get(
				"validate").toString()
				+ "7Cjordan"));

		requests5.setRequestParameters(reqParam);
		requests5.setHttpMethod(HttpMethod.POST);
		TextPage page5 = client.getPage(requests5);
		return jsonObject5;

	}

	public static WebClient setClient() {
		WebClient client = new WebClient(BrowserVersion.INTERNET_EXPLORER);
		client.setJavaScriptTimeout(5000);
		client.getCookieManager().setCookiesEnabled(true);
		client.getOptions().setTimeout(90000);
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		client.setJavaScriptTimeout(8000);
		client.getOptions().setRedirectEnabled(true);
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		client.setAjaxController(new NicelyResynchronizingAjaxController());
		return client;

	}

	/**
	 * 处理极验证数据
	 * 
	 * @param webClient
	 * @param jsonObject
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws IOException
	 */
	public static TextPage ocr(WebClient webClient, JSONObject jsonObject,
			String companyInfo) throws FailingHttpStatusCodeException,
			IOException {
		WebRequest requests = new WebRequest(
				new URL(
						"http://jiyanapi.c2567.com/shibie?gt="
								+ jsonObject.get("gt")
								+ "&challenge="
								+ jsonObject.get("challenge")
								+ "&referer=http://www.gsxt.gov.cn&user=caoheike&pass=598415805&return=json&format=utf8"));
		TextPage page = webClient.getPage(requests);
		/* 如果处理级验失败后，继续递归执行，直到成功为止 */
		if (page.toString().contains(StatucConstants.no.toString())) {
			logger.info("。。。。极验处理失败，开始递归。。。");
			entrepreneurQuer(companyInfo);
		}
		return page;

	}

	/**
	 * 获得返回的数据
	 * 
	 * @param jsonObject
	 * @param client
	 * @throws FailingHttpStatusCodeException
	 * @throws IOException
	 */
	public static HtmlPage getInfo(JSONObject jsonObject, WebClient client,
			String companyInfo) throws FailingHttpStatusCodeException,
			IOException {
		WebRequest requests = new WebRequest(new URL(
				"http://www.gsxt.gov.cn/corp-query-search-1.html"));
		List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
		reqParam.add(new NameValuePair("tab", "ent_tab"));
		reqParam.add(new NameValuePair("token", "120808701"));
		reqParam.add(new NameValuePair("geetest_challenge", jsonObject
				.getString("challenge")));
		reqParam.add(new NameValuePair("geetest_validate", jsonObject
				.getString("validate")));
		reqParam.add(new NameValuePair("geetest_seccode", jsonObject
				.getString("validate") + "|jordan"));
		reqParam.add(new NameValuePair("searchword", companyInfo));
		requests.setRequestParameters(reqParam);
		requests.setHttpMethod(HttpMethod.POST);
		HtmlPage page = client.getPage(requests);
		return page;

	}

	public static TextPage getOcr(WebClient client, String companyInfo)
			throws FailingHttpStatusCodeException, IOException,
			InterruptedException {
		WebRequest requests = new WebRequest(new URL(
				"http://bj.gsxt.gov.cn/sydq/loginSydqAction!sydq.dhtml"));
		HtmlPage page = client.getPage(requests);
		WebRequest requests1 = new WebRequest(new URL(
				"http://bj.gsxt.gov.cn/pc-geetest/register"));
		TextPage page1 = client.getPage(requests1);
		System.out.println(page1.getContent());
		WebRequest requests3;
		JSONObject jsonObject = JSONObject.fromObject(page1.getContent());
		if (jsonObject.get(StatucConstants.success).equals(1)) {
			requests3 = new WebRequest(
					new URL(
							"http://jiyanapi.c2567.com/shibie?user=caoheike&pass=598415805&return=json&ip=&gt="
									+ jsonObject.get("gt")
									+ "&challenge="
									+ jsonObject.get("challenge")
									+ "&format=utf8"));
		} else {
			requests3 = new WebRequest(
					new URL(
							"http://jiyanapi.c2567.com/shibie?user=caoheike&pass=598415805&return=json&ip=&gt="
									+ jsonObject.get("gt")
									+ "&challenge="
									+ jsonObject.get("challenge")
									+ "&format=utf8&model=1"));
		}

		TextPage page3 = client.getPage(requests3);
		if (page3.getContent().contains(MessageConstamts.STATUS_03)) {
			logger.info("....极验失败，问题:被判定为机器人,需要递归重新验证....");
			beijingEntrepreneurQuer(companyInfo);
		}
		return page3;

	}
	

}