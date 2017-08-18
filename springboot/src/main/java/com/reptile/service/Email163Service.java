package com.reptile.service;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.reptile.springboot.Scheduler;
import com.reptile.util.ConstantInterface;
import com.reptile.util.WebClientFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.reptile.util.CrawlerUtil;
import com.reptile.util.Resttemplate;


@Service
public class Email163Service {

	public Map<String, Object> get163Mail(HttpServletRequest request,
			HttpServletResponse response, @RequestParam String username,
			@RequestParam String password) throws Exception {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> infoMap = new HashMap<String, Object>();
		HttpSession session=request.getSession();

		if (username == null || username.trim().length()==0) {
			dataMap.put("errorinfo", "请输入账号！");
			dataMap.put("errorCode", "0001");
			return dataMap;
		}
		if (password == null ||password.trim().length()==0) {
			dataMap.put("errorinfo", "请输入密码!");
			dataMap.put("errorCode", "0001");
			return dataMap;
		}

		if(!username.contains("@163.com")){
			dataMap.put("errorinfo", "163邮箱地址不正确!");
			dataMap.put("errorCode", "0001");
			return dataMap;
		}
		username=(username.split("@163.com"))[0];

		WebClient webClient = new WebClientFactory().getWebClient();

		// 模拟打开163邮箱登陆页面
		String url = "https://mail.163.com/entry/cgi/ntesdoor?funcid=loginone&language=-1&passtype=1&iframe=1&product=mail163&from=web&df=email163&race=26_27_-\n"
				+ "2_bj&module=&uid="
				+ username
				+ "&style=-1&net=c&skinid=null";

		XmlPage page1 = null;
		try {
			URL url1 = new URL(url);

			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new NameValuePair("username", username));
			list.add(new NameValuePair("url2",
					"http://email.163.com/errorpage/error163.htm"));
			list.add(new NameValuePair("savalogin", "0"));
			list.add(new NameValuePair("password", password));
			WebRequest webRequest = new WebRequest(url1);
			webRequest.setRequestParameters(list);
			webRequest.setHttpMethod(HttpMethod.POST);
			HtmlPage click = webClient.getPage(webRequest);
			Thread.sleep(1000);

			if (click.asText().contains("帐号或密码错误")) {
				dataMap.put("errorCode", "0001");
				dataMap.put("errorInfo", "账号或者密码错误！");
				return dataMap;
			}
			if (click.asText().contains("此帐号已被锁定")) {
				dataMap.put("errorCode", "0001");
				dataMap.put("errorInfo", "此帐号已被锁定！");
				return dataMap;
			}

			// 获取sid
			String sid = "";
			Set<Cookie> cookies = webClient.getCookieManager().getCookies();
			Iterator<Cookie> iterator = cookies.iterator();
			while (iterator.hasNext()) {
				Cookie cookie = iterator.next();
				String name = cookie.getName();
				if ("COREMAIL.SID".equals(name.toUpperCase())) {
					sid = cookie.getValue();
				}
			}
			// 获得所有的邮件信息

			page1 = webClient.getPage("http://mail.163.com/js6/s?sid=" + sid
					+ "&func=mbox:listMessages");
		} catch (Exception e) {
			dataMap.put("errorinfo", "系统异常！");
			dataMap.put("errorCode", "0001");
			return dataMap;
		}

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		// 解析页面并找到收件箱所有信息
		String xmlPage = page1.asXml();
		org.dom4j.Document document = DocumentHelper.parseText(xmlPage);

		Element rootElement = document.getRootElement(); // result层
		List elements = rootElement.elements();
		for (int i = 0; i < elements.size(); i++) {
			Element element = (Element) elements.get(i);
			if (element.attribute("name") != null
					&& "var".equals(element.attribute("name").getValue())) { // array层
				List elements1 = element.elements();

				for (int j = 0; j < elements1.size(); j++) { // object层
					Element element2 = (Element) elements1.get(j);
					List elements2 = element2.elements();

					for (int k = 0; k < elements2.size(); k++) { // 信息层
						Element element3 = (Element) elements2.get(k);
						if (element3.attribute("name") != null
								&& "fid".equals(element3.attribute("name")
										.getValue())
								&& "1".equals(element3.getTextTrim())) {
							Map<String, Object> data = new HashMap<String, Object>();

							for (int q = 0; q < elements2.size(); q++) {
								Element elementGoal = (Element) elements2
										.get(q);
								if (elementGoal.attribute("name") != null) {
									String key = elementGoal.attribute("name")
											.getValue();
									String datas = elementGoal.getTextTrim();
									data.put(key, datas);
								}
							}
							dataList.add(data);
						}
					}
				}
			}
		}

		// 设置要爬取信件的发件人集合
		List<String> sendMember = new ArrayList<String>();
		sendMember.add("PCCC@bocomcc.com");	//交通
		sendMember.add("ccsvc@message.cmbchina.com");  //招商
		sendMember.add("creditcard@cgbchina.com.cn");	//广发
		sendMember.add("cebbank@cardcenter.cebbank.com"); //广大
		sendMember.add("creditcardcenter@cardmail.psbc.com");  //邮储
		sendMember.add("349834823@qq.com");  //邮储
		// 将查询到的所有账单信息封装到list中
		List<Object> list = new ArrayList<Object>();

		for (int i = 0; i < sendMember.size(); i++) {
			// 筛选出账单邮件信息
			List<Map<String, Object>> goalData = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> map : dataList) {
				if (map.get("from") != null
						&& map.get("from").toString()
								.contains(sendMember.get(i))
						&& map.get("subject") != null
						&& map.get("subject").toString().contains("账单")) {
					goalData.add(map);
				}
			}

			// 当前没有对应信息 则进入下一个循环
			if (goalData.size() == 0) {
				continue;
			}

			Long maxDate = 0L;
			int index = 0;
			// 找到最新的一封邮件
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			for (int k = 0; k < goalData.size(); k++) {
				String sentDate = goalData.get(k).get("sentDate").toString();
				Date parse = format.parse(sentDate);
				long time = parse.getTime();
				if (time > maxDate) {
					maxDate = time;
					index = k;
				}
			}

			// 获取邮件的内容
			Map<String, Object> map = goalData.get(index);
			try {
				String mid = map.get("id").toString();
				String urls = "http://mail.163.com/js6/read/readhtml.jsp?mid="
						+ mid;
				HtmlPage page2 = webClient.getPage(urls);
				list.add(page2.asXml());
			} catch (Exception e) {
				dataMap.put("errorinfo", "系统维护中！");
				dataMap.put("errorCode", "0001");
				return dataMap;
			}
		}

//		if (list.size() == 0) {
//			dataMap.put("errorCode", "0000");
//			dataMap.put("errorInfo", "暂无账单信息");
//			return dataMap;
//		}

		// 将信息封装到infoMap中进行推送
		infoMap.put("qqnumber", username+"@163.com" );
		infoMap.put("password", password);
		infoMap.put("card", session.getAttribute("IdCard").toString());
		infoMap.put("data", list);
		Resttemplate resttemplate = new Resttemplate();
		dataMap = resttemplate.SendMessage(infoMap, ConstantInterface.port+"/HSDC/authcode/mailBill");
		webClient.close();
		return dataMap;
	}
}
