package com.reptile.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.reptile.constants.MessageConstamts;
import com.reptile.model.MobileBean;
import com.reptile.model.TelecomBean;
import com.reptile.model.UnicomBean;
import com.reptile.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * 
 * @Title: MobileService.java
 * @Package com.reptile.service
 * @Description: TODO(爬虫集合)
 * @author Bigyoung
 * @date 2017年12月29日
 * @version V1.0
 */
@SuppressWarnings("deprecation")
@Service("mobileService")
public class MobileService {

	@Autowired
	private application application;

	private Logger logger = Logger.getLogger(MobileService.class);
	private static CrawlerUtil crawlerUtil = new CrawlerUtil();
	MobileBean mobileBean = new MobileBean();
	Resttemplate resttemplate = new Resttemplate();

	/**
	 * 获取验证码 初始化
	 *
	 * @param mobileBean
	 * @param request
	 * @param response
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void telecomLogin(MobileBean mobileBean,
			HttpServletRequest messageRequest,
			HttpServletResponse messageRsponse)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {
		HttpSession session = messageRequest.getSession();
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		UnexpectedPage codePage = webClient.getPage(MobileBean.getGetCodeUrl());
		BufferedImage ioim = ImageIO.read(codePage.getInputStream());
		session.setAttribute("WebClient", webClient);
		// map.put("WebClients", webClient);
		ImageIO.write(ioim, "png", messageRsponse.getOutputStream());

	}

	public Map<String, Object> loginIn(MobileBean mobileBean,
			HttpServletRequest request, HttpServletResponse response,
			String usertype) throws IOException, InterruptedException {
		Map<String, Object> map = new HashMap<String, Object>(8);
		CrawlerUtil craw = new CrawlerUtil();
		HttpSession session = request.getSession();
		WebClient webClient = (WebClient) session.getAttribute("WebClient");
		session.setAttribute("iphone", mobileBean.getUserIphone());
		WebRequest requests = new WebRequest(new URL(mobileBean.loginurl));
		List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
		reqParam.add(new NameValuePair("userName", mobileBean.getUserIphone()));
		reqParam.add(new NameValuePair("password", mobileBean.getUserPassword()));
		reqParam.add(new NameValuePair("verifyCode", mobileBean.getUserCode()));
		reqParam.add(new NameValuePair("OrCookies", "1"));
		reqParam.add(new NameValuePair("loginType", "1"));
		reqParam.add(new NameValuePair("fromUrl", "uiue/login_max.jsp"));
		reqParam.add(new NameValuePair("toUrl",
				"http://www.sn.10086.cn/my/account/"));
		requests.setRequestParameters(reqParam);
		HtmlPage pages = webClient.getPage(requests);
		try {
			if (pages.getElementById(MessageConstamts.MESSAGE_03).asText() == null
					|| "".equals(pages.getElementById(
							MessageConstamts.MESSAGE_03).asText())) {
				map.put("msg", "0000");
			} else {
				map.put("msg", pages.getElementById("message").asText());
			}
		} catch (Exception e) {

			map.put("msg", "0000");
		}

		session.setAttribute("webClient", webClient);
		return map;
	}

	private WebRequest webRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> queryInfo(HttpServletRequest request,
			HttpServletResponse response, MobileBean mobileBean)
			throws Exception {
		Poi poi = new Poi();
		// boolean flg = true; // 是否需要重新设置
		HttpSession session = request.getSession();
		String iphone = (String) session.getAttribute("iphone");
		// HtmlPage infopage = (HtmlPage) session.getAttribute("SetcodePage");//
		// 获得新的page对象
		WebClient webClients = (WebClient) request.getSession().getAttribute(
				"webClient");
		for (int j = MessageConstamts.INT_2; j >= MessageConstamts.INT_0; j--) {
			Page unpag = webClients.getPage(MobileBean.getDownloadUrl());
			// 苹果系统处理
			// saveFile(unpag, "/Users/hongzheng/"+iphone+"num"+j+".xls");
			// windows处理
			saveFile(unpag, "D:/" + iphone + "num" + j + ".xls");
		}
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();

		for (int i = MessageConstamts.INT_0; i < MessageConstamts.INT_3; i++) {
			String filePath = "D:\\" + iphone + "num" + i + ".xls";
			File file = new File(filePath);
			List<Map<String, Object>> listmap = Poi.getvalues(file);
			lists.addAll(listmap);
		}

		webClients.getPage("https://sn.ac.10086.cn/logout");
		return lists;

		// webClients.close();

	}

	public static void saveFile(Page page, String file) throws IOException {
		InputStream is = page.getWebResponse().getContentAsStream();
		FileOutputStream output = new FileOutputStream(file);
		IOUtils.copy(is, output);
		output.close();
	}

	/**
	 * 更新验证码
	 *
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void updateCodeImg(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		PrintWriter printWriter = response.getWriter();
		WebClient webClients = (WebClient) request.getSession().getAttribute(
				"WebClient");
		new MobileBean();
		UnexpectedPage page = webClients.getPage(MobileBean.getGetCodeUrl());
		BufferedImage io = ImageIO.read(page.getInputStream());
		String fileName = System.currentTimeMillis() + ".png";
		// 此目录保存缩小后的关键图
		File path = new File(request.getSession().getServletContext()
				.getRealPath("/upload")
				+ "/");
		if (!path.isDirectory()) {
			path.mkdirs();
		}
		ImageIO.write(io, "png", new File(path, fileName));
		printWriter.write("upload/" + fileName);
		request.getSession().setAttribute("webClients", webClients);
	}

	public void updatePhoneCode(HttpServletRequest request,
			HttpServletResponse response) throws InterruptedException {
		HttpSession session = request.getSession();
		HtmlPage twoPhoneCodePage = (HtmlPage) session
				.getAttribute("SetcodePage");
		HtmlPage page = (HtmlPage) twoPhoneCodePage.executeJavaScript(
				"$('#smsCodeSpan').click();").getNewPage();
		Thread.sleep(2000);
		if (page.asText().contains(MessageConstamts.MESSAGE_04)) {
			System.out.println(MessageConstamts.MESSAGE_05);
		} else {
			System.out.println(MessageConstamts.MESSAGE_06);
		}

	}

	public void updatePwdCodeImg(HttpServletRequest request,
			HttpServletResponse response, MobileBean mobileBean)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {
		// 设置无界面浏览器
		WebClient webClient = crawlerUtil.setWebClient();
		HtmlPage page = webClient.getPage(MobileBean.getUpdatePwdImg());
		HttpSession session = request.getSession();
		session.setAttribute("webClient", webClient);
		HtmlImage htmlImage = (HtmlImage) page.getElementById("pic");
		ImageReader imageReader = htmlImage.getImageReader();
		BufferedImage bufferedImage = imageReader.read(0);
		ImageIO.write(bufferedImage, "png", response.getOutputStream());

	}

	public void updateCodeImages(HttpServletRequest request,
			HttpServletResponse response, MobileBean mobileBean)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {
		PrintWriter printWriter = response.getWriter();
		// 设置无界面浏览器
		WebClient webClient = crawlerUtil.setWebClient();
		HtmlPage page = webClient.getPage(MobileBean.getUpdatePwdImg());
		HttpSession session = request.getSession();
		session.setAttribute("webClient", webClient);
		HtmlImage htmlImage = (HtmlImage) page.getElementById("pic");
		ImageReader imageReader = htmlImage.getImageReader();
		BufferedImage bufferedImage = imageReader.read(0);
		String fileName = System.currentTimeMillis() + "hs" + ".png";
		// 此目录保存缩小后的关键图
		File path = new File(request.getSession().getServletContext()
				.getRealPath("/upload")
				+ "/");
		if (!path.isDirectory()) {
			path.mkdirs();
		}
		ImageIO.write(bufferedImage, "png", new File(path, fileName));
		printWriter.write("upload/" + fileName);
		request.getSession().setAttribute("webClient", webClient);
	}

	public Map<String, Object> updatePwdInfo(HttpServletRequest request,
			HttpServletResponse response, MobileBean mobileBean, String iphone,
			String userPassword) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		Map<String, Object> map = new HashMap<String, Object>(8);
		HttpSession session = request.getSession();
		// 获取初始化webClient
		WebClient webClient = (WebClient) session.getAttribute("webClient");
		WebRequest webRequest = new WebRequest(new URL(
				mobileBean.UpdatePwdAttestationUrl));
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new NameValuePair("service",
				"direct/1/personalinfo.ResetPwdOperation/$Form"));
		list.add(new NameValuePair("sp", "S0"));
		list.add(new NameValuePair("Form0", "phone_TEMP,PSPT_ID_TEMP"));
		// 密文
		list.add(new NameValuePair("phone", mobileBean.getUserIphone()));
		list.add(new NameValuePair("PSPT_ID", mobileBean.getUserPassword()));
		list.add(new NameValuePair("protocl", "on"));
		// 明文
		list.add(new NameValuePair("phone_TEMP", iphone));
		list.add(new NameValuePair("PSPT_ID_TEMP", userPassword));
		list.add(new NameValuePair("RSET_YZM", mobileBean.getUserCode()));
		webRequest.setHttpMethod(HttpMethod.POST);
		webRequest.setRequestParameters(list);
		HtmlPage pages = webClient.getPage(webRequest);
		HtmlDivision div = (HtmlDivision) pages.querySelector(".con");
		if (div.asText().contains(MessageConstamts.MESSAGE_07)) {
			map.put("msg", "success");
			session.setAttribute("webClient", webClient);
		} else {
			map.put("msg", div.asText());

		}

		return map;
	}

	public void changePassword(HttpServletRequest request,
			HttpServletResponse response, MobileBean mobileBean,
			String userPassword, String callcode)
			throws FailingHttpStatusCodeException, IOException,
			InterruptedException {
		HttpSession session = request.getSession();
		WebClient webClient = (WebClient) session.getAttribute("webClient");
		WebRequest webRequests = new WebRequest(new URL(mobileBean.UpdatePwd));
		List<NameValuePair> usLists = new ArrayList<NameValuePair>();
		usLists.add(new NameValuePair("service",
				"direct/1/personalinfo.ResetPwdOperation/$Form$0"));
		usLists.add(new NameValuePair("sp", "S0"));
		// 加密后的短信验证码
		usLists.add(new NameValuePair("OLD_USER_PASSWD", mobileBean
				.getCallCode()));
		// 加密后的密码
		usLists.add(new NameValuePair("X_NEW_PASSWD", mobileBean
				.getUserPassword()));
		usLists.add(new NameValuePair("RE_NEW_PASSWD", mobileBean
				.getUserPassword()));
		// 铭文 验证码
		usLists.add(new NameValuePair("OLD_USER_PASSWD_TEMP", callcode));
		// 铭文密码
		usLists.add(new NameValuePair("X_NEW_PASSWD_TEMP", userPassword));
		usLists.add(new NameValuePair("RE_NEW_PASSWD_TEMP", userPassword));
		webRequests.setHttpMethod(HttpMethod.POST);
		webRequests.setRequestParameters(usLists);
		HtmlPage pagess = webClient.getPage(webRequests);
		Thread.sleep(3000);
		System.out.println(pagess.asText());

	}

	public Map<String, Object> rsaPassword(String password) {

		Map<String, Object> map = new HashMap<String, Object>(8);
		Map<String, Object> data = new HashMap<String, Object>(8);
		try {

			WebClient webClient = new WebClient(BrowserVersion.CHROME);
			HtmlPage page = webClient.getPage("http://" + application.getIp()
					+ ":" + application.getPort() + "/interface/Rsa.do");
			HtmlInput htmlInput = (HtmlInput) page.getElementById("rsaName");
			String userPasswords = password;
			htmlInput.setValueAttribute(userPasswords);
			HtmlPage pages = (HtmlPage) page
					.executeJavaScript("rsafunction();").getNewPage();
			HtmlDivision div = (HtmlDivision) pages.getElementById("rsa");
			data.put("RsaPassword", div.asText());
			map.put("errorInfo", "加密成功");
			map.put("errorCode", "0000");
			map.put("data", data);

		} catch (Exception e) {
			map.put("errorInfo", "网络错误");
			map.put("errorCode", "0001");
			map.put("data", "null");
		}

		return map;
	}

	public Map<String, Object> unicomAes(String password) {

		Map<String, Object> map = new HashMap<String, Object>(8);
		Map<String, Object> data = new HashMap<String, Object>(8);
		try {

			WebClient webClient = new WebClient(BrowserVersion.CHROME);
			HtmlPage page = webClient.getPage("http://" + application.getIp()
					+ ":" + application.getPort()
					+ "/interface/UnicomAesPage.do");
			HtmlInput htmlInput = (HtmlInput) page.getElementById("rsaPwd");
			String userPasswords = password;
			htmlInput.setValueAttribute(userPasswords);
			HtmlPage pages = (HtmlPage) page
					.executeJavaScript("rsafunction();").getNewPage();
			HtmlDivision div = (HtmlDivision) pages.getElementById("rsas");
			data.put("RsaPassword", div.asText());
			map.put("errorInfo", "加密成功");
			map.put("errorCode", "0000");
			map.put("data", data);

		} catch (Exception e) {
			System.out.println(e);
			map.put("errorInfo", "网络错误");
			map.put("errorCode", "0001");
			map.put("data", "null");
		}
		return map;
	}

	/**
	 * 接口验证码
	 *
	 * @param
	 * @param request
	 * @param response
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public Map<String, Object> telecomLogins(HttpServletRequest request,
			HttpServletResponse response)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		Map<String, Object> map = new HashMap<String, Object>(8);
		Map<String, Object> maps = new HashMap<String, Object>(8);
		try {
			HttpSession session = request.getSession();
			WebClient webClient = crawlerUtil.WebClientNices();
			UnexpectedPage codePage = webClient.getPage(MobileBean
					.getGetCodeUrl());
			BufferedImage ioim = ImageIO.read(codePage.getInputStream());
			session.setAttribute("WebClient", webClient);
			// 此目录保存缩小后的关键图
			File path = new File(request.getSession().getServletContext()
					.getRealPath("/uploads")
					+ "/");
			// 如果文件夹不存在则创建
			if (!path.exists() && !path.isDirectory()) {
				System.out.println("//不存在");
				path.mkdir();
			}
			String fileName = CrawlerUtil.getUUID() + "Interface" + ".png";
			System.out.println(fileName);
			ImageIO.write(ioim, "png", new File(path, fileName));
			// InetAddress.getLocalHost().getHostAddress()
			maps.put("ip", application.getIp());
			maps.put("FileName", fileName);
			maps.put("FilePath", "/uploads");
			maps.put("Port", application.getPort());
			map.put("data", maps);
			map.put("errorCode", "0000");
			map.put("errorInfo", "查询成功");
			map.put("data", maps);
		} catch (Exception e) {
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络错误");
		}

		return map;

	}

	/**
	 * 接口
	 *
	 * @param mobileBean
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public Map<String, Object> login(MobileBean mobileBean,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, InterruptedException {
		Poi poi = new Poi();
		Map<String, Object> info = new HashMap<String, Object>(8);

		Map<String, Object> map = new HashMap<String, Object>(8);
		Map<String, Object> data = new HashMap<String, Object>(8);
		CrawlerUtil craw = new CrawlerUtil();
		HttpSession session = request.getSession();
		WebClient webClient = (WebClient) session.getAttribute("WebClient");
		System.out.println(session.getAttribute("WebClient"));

		session.setAttribute("iphone", mobileBean.getUserIphone());
		WebRequest requests = new WebRequest(new URL(mobileBean.loginurl));
		List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
		reqParam.add(new NameValuePair("userName", mobileBean.getUserIphone()));
		reqParam.add(new NameValuePair("password", mobileBean.getUserPassword()));
		reqParam.add(new NameValuePair("verifyCode", mobileBean.getUserCode()));
		reqParam.add(new NameValuePair("OrCookies", "1"));
		reqParam.add(new NameValuePair("loginType", "1"));
		reqParam.add(new NameValuePair("fromUrl", "uiue/login_max.jsp"));
		reqParam.add(new NameValuePair("toUrl",
				"http://www.sn.10086.cn/my/account/"));
		requests.setRequestParameters(reqParam);
		HtmlPage pages = webClient.getPage(requests);

		if (pages.getElementById(MessageConstamts.MESSAGE_03) == null
				|| "".equals(pages.getElementById(MessageConstamts.MESSAGE_03)
						.asText())) {

			// 登录成功 查询数据

			// 根据此次认证，可直接查询详单
			List<Map<String, String>> list = new htmlUtil().liantong();
			// 越过凭证不用发短信
			XmlPage page = webClient.getPage(mobileBean.pingzhengurl("201703"));
			for (int j = MessageConstamts.INT_3; j >= MessageConstamts.INT_0; j--) {
				// 越过凭证不用发短信
				page = webClient.getPage(mobileBean.pingzhengurl(list
						.get(j)
						.get("begin")
						.toString()
						.replace("-", "")
						.substring(
								0,
								list.get(j).get("begin").toString()
										.replace("-", "").length() - 2)));
				Page unpag = webClient.getPage(MobileBean.getDownloadUrl());
				// saveFile(unpag,
				// "/Users/hongzheng/"+iphone+"num"+j+".xls");/苹果系统处理
				// windows处理
				saveFile(unpag, "D:/" + mobileBean.getUserIphone() + "num" + j
						+ ".xls");
			}
			List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
			for (int i = MessageConstamts.INT_0; i < MessageConstamts.INT_3; i++) {
				String filePath = "D:\\" + mobileBean.getUserIphone() + "num"
						+ i + ".xls";
				File file = new File(filePath);
				List<Map<String, Object>> listmap = Poi.getvalues(file);
				lists.addAll(listmap);
			}
			map.put("data", lists);
			info.put("UserIphone", mobileBean.getUserIphone());
			info.put("UserPassword", mobileBean.getUserPassword());
			map.put("accountMessage", info);
			// 推送数据
			map = resttemplate.SendMessage(map, application.getSendip()
					+ "/HSDC/authcode/callRecord");

		} else {
			map.put("errorInfo", pages.getElementById("message").asText());
			map.put("errorCode", "0001");
		}

		System.out.println(map.toString());
		session.setAttribute("webClient", webClient);
		return map;

	}

	/**
	 * 接口 更新验证码
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public Map<String, Object> updateCodeImgs(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>(8);
		Map<String, Object> data = new HashMap<String, Object>(8);
		try {

			WebClient webClients = (WebClient) request.getSession()
					.getAttribute("WebClient");
			new MobileBean();
			UnexpectedPage page = webClients
					.getPage(MobileBean.getGetCodeUrl());
			BufferedImage io = ImageIO.read(page.getInputStream());
			String fileName = System.currentTimeMillis() + "UpdateCode"
					+ ".png";
			// 此目录保存缩小后的关键图
			File path = new File(request.getSession().getServletContext()
					.getRealPath("/upload")
					+ "/");
			if (!path.isDirectory()) {
				path.mkdirs();
			}

			ImageIO.write(io, "png", new File(path, fileName));
			request.getSession().setAttribute("WebClient", webClients);

			System.out.println(fileName);
			ImageIO.write(io, "png", new File(path, fileName));
			data.put("ip", application.getIp());
			data.put("FileName", fileName);
			data.put("FilePath", "/upload");
			data.put("Port", application.getPort());
			map.put("data", data);
			map.put("errorCode", "0000");
			map.put("errorInfo", "查询成功");

		} catch (Exception e) {
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络错误");
		}
		return map;

	}

	/**
	 * 接口 更新验证码
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private static String appCode = "410870f466d04b24a0427a076d370278";

	public Map<String, Object> mobileBelong(HttpServletRequest request,
			HttpServletResponse response, String phone) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>(8);
		Map<String, Object> data = new HashMap<String, Object>(8);
		try {
			CrawlerUtil craw = new CrawlerUtil();
			WebClient webClient = craw.setWebClient();
			WebRequest webRequest = new WebRequest(new URL(
					"http://jshmgsdmfb.market.alicloudapi.com/shouji/query"));
			webRequest.setHttpMethod(HttpMethod.GET);
			webRequest.setAdditionalHeader("Authorization", "APPCODE "
					+ appCode);
			List<NameValuePair> list = new ArrayList<>();
			list.add(new NameValuePair("shouji", phone));
			webRequest.setRequestParameters(list);
			UnexpectedPage page1 = webClient.getPage(webRequest);
			String jsonData = page1.getWebResponse().getContentAsString();
			String company = JSONObject.fromObject(jsonData)
					.getJSONObject("result").get("company").toString();
			data.put("MobileBelong", company);
			// HtmlPage page =
			// webClient.getPage("http://jshmgsdmfb.market.alicloudapi.com/shouji/query?shouji="
			// + phone );
			// System.out.println(page.querySelectorAll(".tdc2").get(2).asText());
			// data.put("MobileBelong",
			// page.querySelectorAll(".tdc2").get(2).asText());
			map.put("data", data);
			map.put("errorCode", "0000");
			map.put("errorInfo", "查询成功");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络错误");
		}
		return map;

	}

	/**
	 * 接口 更新验证码 联通
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public Map<String, Object> unicomUpdateCode(HttpServletRequest request,
			HttpServletResponse response, UnicomBean unicombean)
			throws IOException {
		Map<String, Object> map = new HashMap<String, Object>(8);
		Map<String, Object> data = new HashMap<String, Object>(8);
		try {

			WebClient webClients = (WebClient) request.getSession()
					.getAttribute("webClient");
			UnexpectedPage page = webClients
					.getPage("http://uac.10010.com/portal/Service/CreateImage");
			BufferedImage io = ImageIO.read(page.getInputStream());
			String fileName = System.currentTimeMillis() + "UpdateCodeUnicom"
					+ ".png";
			// 此目录保存缩小后的关键图
			File path = new File(request.getSession().getServletContext()
					.getRealPath("/upload")
					+ "/");
			if (!path.isDirectory()) {
				path.mkdirs();
			}
			ImageIO.write(io, "png", new File(path, fileName));

			request.getSession().setAttribute("webClient", webClients);

			System.out.println(fileName);
			ImageIO.write(io, "png", new File(path, fileName));
			data.put("ip", application.getIp());
			data.put("FileName", fileName);
			data.put("FilePath", "/upload");
			data.put("Port", application.getPort());
			map.put("data", data);
			map.put("errorCode", "0000");
			map.put("errorInfo", "查询成功");

		} catch (Exception e) {
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络错误");
		}
		return map;

	}

	/**
	 * 接口 获取登陆验证码 联通
	 *
	 * @param request
	 * @param httpResponse
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Map<String, Object> getCode(HttpServletRequest request,
			HttpServletResponse httpResponse, String useriPhone) {
		System.out.println("访问了。。。。。。。。");
		Map<String, Object> map = new HashMap<String, Object>(8);
		HttpSession session = request.getSession();
		WebClient webClient = new WebClientFactory().getWebClient();
		try {
			HtmlPage loginPage = webClient
					.getPage("https://uac.10010.com/portal/homeLogin");
			Thread.sleep(500);
			String url = "https://uac.10010.com/portal/Service/CheckNeedVerify?callback=jQuery17209863190566662376_"
					+ System.currentTimeMillis()
					+ "&userName="
					+ useriPhone
					+ "&pwdType=01&_=" + System.currentTimeMillis();
			UnexpectedPage page = webClient.getPage(url);
			Thread.sleep(500);
			// System.out.println(page.getWebResponse().getContentAsString());
			String resultInfo = page.getWebResponse().getContentAsString();
			String tips = resultInfo.split("\\(")[1].split("\\)")[0];
			JSONObject jsons = JSONObject.fromObject(tips);
			String tipInfo = jsons.get("resultCode").toString();
			String tip = jsons.get("ckCode").toString();
			if (MessageConstamts.STATUS_TRUE.equals(tipInfo)) {
				// System.out.println("需要图形验证码");
				session.setAttribute("isTrue", "true");
			} else {
				session.setAttribute("isTrue", "false");
				// System.out.println("不需要 图形验证码");
			}
			if (tip.contains(MessageConstamts.STRING_1)) {
				map.put("errorCode", "0000");
				map.put("errorInfo", "此次不需要验证码");
				session.setAttribute("isTrueCk", "false");
			} else {
				System.out.println("需要验证码");
				session.setAttribute("isTrueCk", "true");
				String url2 = "https://uac.10010.com/portal/Service/SendCkMSG?callback=jQuery17209863190566662376_"
						+ System.currentTimeMillis()
						+ "&req_time="
						+ System.currentTimeMillis()
						+ "&mobile="
						+ useriPhone
						+ "&_=" + System.currentTimeMillis();
				HtmlPage page1 = webClient.getPage(url2);
				Thread.sleep(500);
				String result = page1.asText();
				if (result.contains(MessageConstamts.STRING_0000)) {
					map.put("errorCode", MessageConstamts.STRING_0000);
					map.put("errorInfo", MessageConstamts.SEND_CODE_OK);

				} else {
					JSONObject json = JSONObject
							.fromObject(result.split("\\(")[1].split("\\)")[0]);
					String resultCode = json.get("resultCode").toString();
					if (MessageConstamts.STRING_7098.equals(resultCode)) {

						map.put("errorCode", "0001");
						map.put("errorInfo", "随机码发送次数已达上限，请明日再试！");
					} else {
						map.put("errorCode", "0001");
						map.put("errorInfo", "距离上次发送不足1分钟");
					}

				}
			}
			session.setAttribute("webClientone", webClient);
		} catch (Exception e) {
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络异常");
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 联通登陆
	 * 
	 * @param
	 */

	public Map<String, Object> unicomLogin(HttpServletRequest request,
			String userIphone, String password, String code) {
		Map<String, Object> map = new HashMap<String, Object>(8);
		try {
			HttpSession session = request.getSession();
			WebClient webClient = (WebClient) session
					.getAttribute("webClientone");
			if (webClient == null) {
				map.put("errorCode", "0001");
				map.put("errorInfo", "网络异常");
				return map;
			}
			// 图形验证码
			String isTrue = (String) session.getAttribute("isTrue");
			// 短信验证码
			String isTrueCk = (String) session.getAttribute("isTrueCk");
			if (isTrueCk == null) {
				map.put("errorCode", "0001");
				map.put("errorInfo", "请先获取验证码！");
				return map;
			}
			WebRequest request1 = null;
			// ===========需要图形验证码==================================
			if (MessageConstamts.STATUS_TRUE.equals(isTrue)) {
				// 1.读取页面验证码图片到本地
				// 动态码url
				String imageUrl = "https://uac.10010.com/portal/Service/CreateImage?t="
						+ System.currentTimeMillis();
				UnexpectedPage imagePage = webClient.getPage(imageUrl);
				BufferedImage bufferedImage = ImageIO.read(imagePage
						.getInputStream());
				String findImage = "gd" + System.currentTimeMillis() + ".png";
				ImageIO.write(bufferedImage, "png", new File("C:\\Shimage",
						findImage));
				// 2.转码
				Map<String, Object> imagev = MyCYDMDemo.Imagev("C:\\Shimage\\"
						+ findImage);
				// 转码后的动态码
				String catpy = (String) imagev.get("strResult");
				// System.out.println(catpy+"-***-*-");
				WebRequest webRequest3 = new WebRequest(
						new URL(
								"https://uac.10010.com/portal/Service/CtaIdyChk?callback=jQuery17207654655044488388_"
										+ System.currentTimeMillis()
										+ "&verifyCode="
										+ catpy
										+ "&verifyType=1&_="
										+ System.currentTimeMillis()));
				webRequest3.setHttpMethod(HttpMethod.GET);
				webRequest3.setAdditionalHeader("Referer",
						"https://uac.10010.com/portal/homeLogin");
				HtmlPage page2 = webClient.getPage(webRequest3);
				String resultInfo = page2.getWebResponse().getContentAsString();
				String tips = resultInfo.split("\\(")[1].split("\\)")[0];
				JSONObject jsons = JSONObject.fromObject(tips);
				String tipInfo = jsons.get("resultCode").toString();
				if (MessageConstamts.STATUS_TRUE.equals(isTrueCk)) {
					// ===========需要图形验证码，短信验证码===========================
					// 图形验证码正确可以发包登陆
					if (MessageConstamts.STATUS_TRUE.equals(tipInfo)) {
						System.out.println("图形验证码正确");
						Set<Cookie> cookies = webClient.getCookieManager()
								.getCookies();
						String uvc = "";
						for (Cookie c : cookies) {
							if ("uacverifykey".equals(c.getName())) {
								uvc = c.getValue();
							}
							webClient.getCookieManager().addCookie(c);

						}
						request1 = new WebRequest(
								new URL(
										"https://uac.10010.com/portal/Service/MallLogin?callback=jQuery17207654655044488388_"
												+ System.currentTimeMillis()
												+ "&req_time="
												+ System.currentTimeMillis()
												+ "&redirectURL=http://www.10010.com&userName="
												+ userIphone
												+ "&password="
												+ password
												+ "&pwdType=01&productType=01&verifyCode="
												+ catpy
												+ "&uvc="
												+ uvc
												+ "&redirectType=01&rememberMe=1&verifyCKCode="
												+ code
												+ "&_="
												+ System.currentTimeMillis()));
					} else {

						System.out.println("图形验证码 错误");
						map.put("errorCode", "0001");
						map.put("errorInfo", "验证码 错误");
						return map;
					}
				} else {
					// ===========需要图形验证码，不需要短信===========================
					// 图形验证码正确可以发包登陆
					if (MessageConstamts.STATUS_TRUE.equals(tipInfo)) {
						System.out.println("图形验证码正确");
						Set<Cookie> cookies = webClient.getCookieManager()
								.getCookies();
						String uvc = "";
						for (Cookie c : cookies) {
							if ("uacverifykey".equals(c.getName())) {
								uvc = c.getValue();
							}
							webClient.getCookieManager().addCookie(c);

						}
						request1 = new WebRequest(
								new URL(
										"https://uac.10010.com/portal/Service/MallLogin?callback=jQuery17207654655044488388_"
												+ System.currentTimeMillis()
												+ "&req_time="
												+ System.currentTimeMillis()
												+ "&redirectURL=http://www.10010.com&userName="
												+ userIphone
												+ "&password="
												+ password
												+ "&pwdType=01&productType=01&verifyCode="
												+ catpy
												+ "&uvc="
												+ uvc
												+ "&redirectType=01&rememberMe=1&_="
												+ System.currentTimeMillis()));
					} else {

						System.out.println("图形验证码 错误");
						map.put("errorCode", "0001");
						map.put("errorInfo", "验证码 错误");
						return map;
					}

				}
			} else {
				if (MessageConstamts.STATUS_TRUE.equals(isTrueCk)) {
					// ===========不需要图形验证码,需要短信===========================
					request1 = new WebRequest(
							new URL(
									"https://uac.10010.com/portal/Service/MallLogin?callback=jQuery17209863190566662376_"
											+ System.currentTimeMillis()
											+ "&req_time="
											+ System.currentTimeMillis()
											+ "&redirectURL=http://www.10010.com&userName="
											+ userIphone
											+ "&password="
											+ password
											+ "&pwdType=01&productType=01&redirectType=01&rememberMe=1&verifyCKCode="
											+ code
											+ "&_="
											+ System.currentTimeMillis()));
				} else {
					// ===========不需要图形验证码,不需要短信(不会发生)===========================
					request1 = new WebRequest(
							new URL(
									"https://uac.10010.com/portal/Service/MallLogin?callback=jQuery17209863190566662376_"
											+ System.currentTimeMillis()
											+ "&req_time="
											+ System.currentTimeMillis()
											+ "&redirectURL=http://www.10010.com&userName="
											+ userIphone
											+ "&password="
											+ password
											+ "&pwdType=01&productType=01&redirectType=01&rememberMe=1&_="
											+ System.currentTimeMillis()));
				}
			}

			request1.setHttpMethod(HttpMethod.GET);
			request1.setAdditionalHeader("Referer",
					"https://uac.10010.com/portal/homeLogin");
			HtmlPage page2 = webClient.getPage(request1);
			Thread.sleep(500);
			String tip = page2.asText();
			String tips = tip.split("\\(")[1].split("\\)")[0];
			System.out.println(tips);
			JSONObject json = JSONObject.fromObject(tips);
			String tipInfo = json.get("resultCode").toString();
			if (MessageConstamts.STATUS_TRUE.equals(tipInfo)
					|| MessageConstamts.STRING_0301.equals(tipInfo)) {

				map.put("errorCode", "0000");
				map.put("errorInfo", "登陆成功！");
				session.setAttribute("webClientSucce", webClient);
			} else {
				// System.out.println(json.get("msg").toString());
				map.put("errorCode", "0001");
				if (json.get(MessageConstamts.MSG).toString()
						.contains(MessageConstamts.CODEFAIL)) {
					map.put("errorInfo", "验证码错误");
					// }else if(tipInfo.equals("7007")&&tipInfo.contains("")){
					// System.out.println(tipInfo.length());
					// String a=json.get("msg").toString();
					// System.out.println(a.split("\\<a")[0]);
					// map.put("errorInfo", "用户名或密码不正确，还有3次机会");

				} else {
					map.put("errorInfo", json.get("msg").toString());
				}
			}

		} catch (Exception e) {
			map.put("errorInfo", "0001");
			map.put("errorInfo", "网络异常");
			System.out.println(e);

		}
		return map;

	}

	/**
	 * 联通 获取详单的 验证码
	 *
	 * @param
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Map<String, Object> getCodeTwo(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>(8);
		HttpSession session = request.getSession();
		WebClient webClient = (WebClient) session
				.getAttribute("webClientSucce");
		System.out.println("登陆成功");
		if (webClient == null) {
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络异常");
			return map;
		}
		// 打开获取详单页面
		try {
			Thread.sleep(1000);
			HtmlPage detailPage = webClient
					.getPage("http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001");

			Thread.sleep(8000);
			// System.out.println(detailPage.asXml());
			// ============登陆信息校验===========================
			webClient.addRequestHeader("Accept",
					"application/json, text/javascript, */*; q=0.01");
			webClient.addRequestHeader("Accept-Encoding", "gzip, deflate");
			webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
			webClient.addRequestHeader("Connection", "keep-alive");
			webClient.addRequestHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			webClient
					.addRequestHeader(
							"Referer",
							"http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001");
			webClient.addRequestHeader("X-Requested-With", "XMLHttpRequest");

			List<NameValuePair> paramer3 = new ArrayList<NameValuePair>();
			paramer3.add(new NameValuePair("_", System.currentTimeMillis() + ""));
			WebRequest webRequest3 = new WebRequest(new URL(
					"http://iservice.10010.com/e3/static/check/checklogin?_="
							+ System.currentTimeMillis()));
			webRequest3.setHttpMethod(HttpMethod.POST);
			webRequest3.setRequestParameters(paramer3);
			TextPage nextPage = webClient.getPage(webRequest3);
			Thread.sleep(3000);
			// System.out.println(nextPage.getContent());
			// ====================检验是否需要验证码===========

			// =====================获取验证码================================================
			List<NameValuePair> paramer = new ArrayList<NameValuePair>();
			String time = "" + System.currentTimeMillis();
			paramer.add(new NameValuePair("_", time));
			paramer.add(new NameValuePair(
					"accessURL",
					"http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001"));
			paramer.add(new NameValuePair("menuid", "000100030001"));
			paramer.add(new NameValuePair("menuId", "000100030001"));
			WebRequest webRequest1 = new WebRequest(
					new URL(
							"http://iservice.10010.com/e3/static/query/sendRandomCode?_="
									+ time
									+ "&accessURL=http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001&menuid=000100030001"));
			webRequest1.setHttpMethod(HttpMethod.POST);
			webRequest1.setRequestParameters(paramer);
			// ==============暂时修改===================
			if (webClient.getPage(webRequest1).isHtmlPage()) {

				map.put("errorCode", "0001");
				map.put("errorInfo", "验证码发送失败");
				return map;
			} else {
				TextPage next = webClient.getPage(webRequest1);
				// System.out.println(next.getContent());
				JSONObject json1 = JSONObject.fromObject(next.getContent());
				// String tips1=json1.get("issuccess").toString();
				String tips2 = json1.get("sendcode").toString();
				if (MessageConstamts.STATUS_TRUE.equals(tips2)) {
					System.out.println("验证码发送成功");
					map.put("errorCode", "0000");
					map.put("errorInfo", "验证发送成功");
					session.setAttribute("webClientTwo", webClient);
				} else {

					map.put("errorCode", "0001");
					map.put("errorInfo", "验证码发送失败");
					System.out.println("验证码发送失败");
				}
				// ===========================
			}

			Thread.sleep(1000);

		} catch (Exception e) {
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络异常");
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 联通通话详单
	 */

	public Map<String, Object> getDetial(HttpServletRequest request,
			String userIphone, String userPassword, String code,
			String longitude, String latitude, String uuId) {
		Map<String, Object> map = new HashMap<String, Object>(8);
		try {
			PushState.state(userIphone, "callLog", 100);
			PushSocket.pushnew(map, uuId, "1000", "登录中");
			Thread.sleep(2000);

			List<Map<String, Object>> listsy = new ArrayList<Map<String, Object>>();
			String info = "";
			HttpSession session = request.getSession();
			WebClient webClient = (WebClient) session
					.getAttribute("webClientTwo");
			if (webClient == null) {
				// PushSocket.push(map, UUID, "0001");
				map.put("errorCode", "0001");
				map.put("errorInfo", "请先获取验证码！");
				PushSocket.pushnew(map, uuId, "3000", "请先获取验证码！");
				PushState.state(userIphone, "callLog", 200);
				return map;
			}
			// System.out.println("验证码发送成功");
			// =======================确定验证码===============================================
			String verCode = "http://iservice.10010.com/e3/static/query/verificationSubmit?_="
					+ System.currentTimeMillis()
					+ "&accessURL=http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001&menuid=000100030001";
			WebRequest webRequest2 = new WebRequest(new URL(verCode));
			webRequest2.setHttpMethod(HttpMethod.POST);
			List<NameValuePair> paramer2 = new ArrayList<NameValuePair>();
			paramer2.add(new NameValuePair("_", System.currentTimeMillis() + ""));
			paramer2.add(new NameValuePair(
					"accessURL",
					"http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001"));
			paramer2.add(new NameValuePair("menuid", "000100030001"));
			paramer2.add(new NameValuePair("menuId", "000100030001"));
			paramer2.add(new NameValuePair("inputcode", code));
			webRequest2.setRequestParameters(paramer2);

			// if(webClient.getPage(webRequest2).isHtmlPage()){
			//
			// }
			String flag = "";
			try {
				TextPage newPage = webClient.getPage(webRequest2);
				Thread.sleep(1000);
				// System.out.println(newPage.getContent());
				JSONObject json3 = JSONObject.fromObject(newPage.getContent());
				String resultCode = json3.get("flag").toString();
				if (MessageConstamts.STRING_00.equals(resultCode)) {
					// ---------------推-------------------
					PushSocket.pushnew(map, uuId, "2000", "登录成功");
					// ---------------推-------------------
					// =======================获取详单================================================
					PushSocket.pushnew(map, uuId, "5000", "获取数据中");
					flag = "5000";
					webClient.addRequestHeader("Accept",
							"application/json, text/javascript, */*; q=0.01");
					webClient.addRequestHeader("Accept-Encoding",
							"gzip, deflate");
					webClient.addRequestHeader("Accept-Language",
							"zh-CN,zh;q=0.8");
					webClient.addRequestHeader("Connection", "keep-alive");
					// webClient.addRequestHeader("Content-Length","56");
					webClient.addRequestHeader("Content-Type",
							"application/x-www-form-urlencoded;charset=UTF-8");
					webClient.addRequestHeader("Host", "iservice.10010.com");
					webClient.addRequestHeader("Origin",
							"http://iservice.10010.com");
					webClient
							.addRequestHeader(
									"Referer",
									"http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001");
					webClient
							.addRequestHeader(
									"User-Agent",
									"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
					webClient.addRequestHeader("X-Requested-With",
							"XMLHttpRequest");
					JSONArray array = new JSONArray();
					// 开始是时间
					String begin = GetMonth.nowMonth() + "01";
					// 结束时间
					String end = GetMonth.today();
					int year = new Integer(begin.substring(0, 4));
					// 获取月 作为获取每个月的最后一天的参数
					int month = new Integer(begin.substring(4, 6));
					// 上月
					String beforMonth = "";
					for (int i = MessageConstamts.INT_1; i < MessageConstamts.INT_7; i++) {
						Map<String, Object> dataMap = new HashMap<String, Object>(
								8);
						System.out.println(begin + "*****" + end);
						WebRequest webRequestss = new WebRequest(
								new URL(
										"http://iservice.10010.com/e3/static/query/callDetail?_="
												+ System.currentTimeMillis()
												+ "&accessURL=http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001&menuid=000100030001"));
						List<NameValuePair> lists = new ArrayList<NameValuePair>();
						lists.add(new NameValuePair("pageNo", "1"));
						lists.add(new NameValuePair("pageSize", "2000"));
						lists.add(new NameValuePair("beginDate", begin));
						lists.add(new NameValuePair("endDate", end));
						webRequestss.setHttpMethod(HttpMethod.POST);
						webRequestss.setRequestParameters(lists);
						TextPage chekpages = webClient.getPage(webRequestss);
						System.out.println(chekpages.getContent() + "----vvv");
						// 上i月
						beforMonth = GetMonth.beforMon(year, month, i);
						begin = beforMonth + "01";
						int y = new Integer(begin.substring(0, 4));
						int m = new Integer(begin.substring(4, 6));
						// 一个月的最后一天
						end = GetMonth.lastDate(y, m);
						dataMap.put("item", chekpages.getContent());
						listsy.add(dataMap);
					}

					PushSocket.pushnew(map, uuId, "6000", "数据获取成功");
					flag = "6000";

					map.put("errorCode", "0000");
					map.put("errorInfo", "查询成功");
					map.put("data", listsy);
					map.put("UserIphone", userIphone);
					map.put("UserPassword", userPassword);
					map.put("longitude", longitude);
					map.put("latitude", latitude);
					// map=resttemplate.SendMessage(map,
					// "http://192.168.3.35:8080/HSDC/message/linkCallRecord");//魏艳
					// map=resttemplate.SendMessage(map,
					// "http://192.168.3.4:8081/HSDC/message/linkCallRecord");//胡献根
					map = resttemplate.SendMessage(map, application.getSendip()
							+ "/HSDC/message/linkCallRecord");
					System.out.println("推送后==" + map);
					if (MessageConstamts.STRING_0000.equals(map.get(
							MessageConstamts.ERRORCODE).toString())) {

						PushState.state(userIphone, "callLog", 300);
						map.put("errorInfo", "推送成功");
						map.put("errorCode", "0000");
						PushSocket.pushnew(map, uuId, "8000", "认证成功");
					} else {
						// PushSocket.push(map, UUID, "0001");
						// --------------------数据中心推送状态----------------------
						PushState.state(userIphone, "callLog", 200);
						// ---------------------数据中心推送状态---------------------
						map.put("errorInfo", map.get("errorInfo").toString());
						map.put("errorCode", "0001");
						PushSocket.pushnew(map, uuId, "9000",
								map.get("errorInfo").toString());
					}
				} else {
					// ---------------推-------------------
					PushState.state(userIphone, "callLog", 200);
					System.out.println(json3.get("error").toString());
					if (resultCode.equals(MessageConstamts.STRING_01)) {
						map.put("errorCode", "0001");
						map.put("errorInfo", "验证码已过期，请从新获取新的验证码");
						PushSocket.pushnew(map, uuId, "3000",
								"验证码已过期，请从新获取新的验证码");
						// 验证码错误
					} else if (resultCode.equals(MessageConstamts.STRING_02)) {
						map.put("errorCode", "0001");
						map.put("errorInfo", "校验失败");
						PushSocket.pushnew(map, uuId, "3000", "校验失败");
						// sessionFail//session失效
					} else if (resultCode.equals(MessageConstamts.STRING_03)) {
						map.put("errorCode", "0001");
						map.put("errorInfo", "校验失败,请稍后再试！");
						PushSocket.pushnew(map, uuId, "3000", "校验失败,请稍后再试！");
					} else if (resultCode.equals(MessageConstamts.STRING_04)) {
						map.put("errorCode", "0001");
						map.put("errorInfo", "验证码不能为空");
						PushSocket.pushnew(map, uuId, "3000", "验证码不能为空");
					} else {
						map.put("errorCode", "0001");
						map.put("errorInfo", json3.get("error").toString());
						PushSocket.pushnew(map, uuId, "3000",
								"json3.get(\"error\").toString()");
					}
				}
			} catch (ClassCastException e) {
				// e.printStackTrace();
				// --------------------数据中心推送状态----------------------
				PushState.state(userIphone, "callLog", 200);
				// ---------------------数据中心推送状态---------------------
				// ???????
				map.put("errorInfo", "验证失败,稍后再试");
				map.put("errorCode", "0001");
				if (flag.equals(MessageConstamts.STRING_5000)) {
					PushSocket.pushnew(map, uuId, "7000", "数据获失败");
				} else if (flag.equals(MessageConstamts.STRING_6000)) {
					PushSocket.pushnew(map, uuId, "9000", "认证失败");
					PushState.state(userIphone, "callLog", 200);
				}
				return map;
			}

		} catch (Exception e) {
			PushState.state(userIphone, "callLog", 200);
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络异常");
			PushSocket.pushnew(map, uuId, "3000", "登录失败，网络异常");
			PushState.state(userIphone, "callLog", 200);
			e.printStackTrace();
		}
		return map;

	}

	/**
	 * 接口 获取验证码 联通
	 *
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Map<String, Object> telecomLogin(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, TelecomBean telecomBean)
			throws IOException, InterruptedException {

		Map<String, Object> map = new HashMap<String, Object>(8);
		Map<String, Object> data = new HashMap<String, Object>(8);
		try {
			WebClient webClient = new WebClient();
			webClient.getOptions().setUseInsecureSSL(true);
			// 开启cookie管理
			webClient.getCookieManager().setCookiesEnabled(true);
			webClient.getOptions().setTimeout(100000);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.setJavaScriptTimeout(100000);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
			webClient
					.setAjaxController(new NicelyResynchronizingAjaxController());
			//
			HtmlPage page = webClient.getPage("http://login.189.cn/login");
			HtmlTextInput txe = (HtmlTextInput) page
					.getElementById("txtAccount");
			HtmlPasswordInput txepasswprd = (HtmlPasswordInput) page
					.getElementById("txtPassword");
			txe.setValueAttribute(telecomBean.getUserPhone());
			txepasswprd.setValueAttribute(telecomBean.getUserPassword());
			HtmlPage loginpage = (HtmlPage) page.executeJavaScript(
					"$('#loginbtn').click();").getNewPage();
			Thread.sleep(7000);

			// HtmlForm htmlform= (HtmlForm)
			// loginpage.getElementById("loginForm");
			HtmlDivision htmlform = (HtmlDivision) loginpage
					.getElementById("divErr");

			if (htmlform == null || htmlform.equals("")) {
				// 开始授权
				HtmlPage logi = webClient
						.getPage("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000202");
				WebRequest webRequest = new WebRequest(
						new URL(
								"http://sn.189.cn/service/bill/feeDetailrecordList.action"));
				List<NameValuePair> reqParamsinfo = new ArrayList<NameValuePair>();
				reqParamsinfo.add(new NameValuePair("currentPage", "1"));
				reqParamsinfo.add(new NameValuePair("pageSize", "10"));
				reqParamsinfo.add(new NameValuePair("effDate", "2017-05-01"));
				reqParamsinfo.add(new NameValuePair("expDate", "2017-08-20"));
				reqParamsinfo.add(new NameValuePair("serviceNbr", telecomBean
						.getUserPhone()));
				reqParamsinfo.add(new NameValuePair("operListID", "1"));
				reqParamsinfo.add(new NameValuePair("isPrepay", "0"));
				reqParamsinfo.add(new NameValuePair("pOffrType", "481"));
				webRequest.setHttpMethod(HttpMethod.POST);
				webRequest.setRequestParameters(reqParamsinfo);
				List list = new ArrayList();

				HtmlPage infopage = webClient.getPage(webRequest);
				System.out.print(infopage.asXml());
				HtmlTable htmlTable = null;
				if (!infopage.asXml().contains(MessageConstamts.MESSAGE_08)) {
					htmlTable = (HtmlTable) infopage.getByXPath("//table").get(
							0);
					data.put("info", htmlTable.asXml());
				} else {
					data.put("info", "");
				}

				map.put("data", data);
				// map.put("errorCode","0000");
				// map.put("errorInfo","成功");
				map.put("UserIphone", telecomBean.getUserPhone());
				map.put("UserPassword", telecomBean.getUserPassword());
				map.put("flag", "0");

				map = resttemplate.SendMessage(map, application.getSendip()
						+ "/HSDC/authcode/callRecordTelecom");

			} else {
				if (htmlform.asText().contains(MessageConstamts.MESSAGE_09)) {
					map.put("errorCode", "0001");
					map.put("errorInfo", "该帐号已被锁定，请您明天再来尝试");
				} else {
					map.put("errorCode", "0001");
					map.put("errorInfo", "帐号或密码错误");
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			map.put("errorCode", "0001");
			map.put("errorInfo", "网络异常");
		}
		// 电信推送
		return map;
	}

	/**
	 * 接口 验证码 电信
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Map<String, Object> telecomQueryInfo(HttpServletRequest request,
			HttpServletResponse response, TelecomBean telecomBean)
			throws IOException, InterruptedException {
		Map<String, Object> map = new HashMap<String, Object>(8);
		Map<String, Object> data = new HashMap<String, Object>(8);
		HttpSession session = request.getSession();
		WebClient webClient = (WebClient) session.getAttribute("WebClient");
		TelecomBean telecomBean1 = (TelecomBean) session
				.getAttribute("TelecomBean");

		WebRequest webRequest = new WebRequest(new URL(
				"http://sn.189.cn/service/bill/feeDetailrecordList.action"));
		List<NameValuePair> reqParams = new ArrayList<NameValuePair>();
		reqParams.add(new NameValuePair("currentPage", "1"));
		reqParams.add(new NameValuePair("pageSize", "10"));
		reqParams.add(new NameValuePair("effDate", "2017-07-01"));
		reqParams.add(new NameValuePair("expDate", "2017-07-07"));
		reqParams.add(new NameValuePair("serviceNbr", telecomBean1
				.getUserPhone()));
		reqParams.add(new NameValuePair("operListID", "1"));
		reqParams.add(new NameValuePair("isPrepay", "0"));
		reqParams.add(new NameValuePair("pOffrType", "481"));
		webRequest.setHttpMethod(HttpMethod.POST);
		webRequest.setRequestParameters(reqParams);
		HtmlPage infopage = webClient.getPage(webRequest);
		System.out.println(infopage.asText());
		return data;

	}

	/**
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Map<String, Object> encryptrsa(HttpServletRequest request,
			String qqnumber) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException, InterruptedException {
		Map<String, Object> map = new HashMap<String, Object>(8);
		WebClient webClients = crawlerUtil.WebClientNice();
		int count = 0;
		boolean flg = true;
		do {
			count++;
			Thread.sleep(2000);
			HtmlPage page = webClients
					.getPage("https://ssl.ptlogin2.qq.com/check?pt_tea=2&uin="
							+ qqnumber
							+ "&appid=522005705&ptlang=2052&regmaster=&pt_uistyle=9&r=0.07655477741844985&pt_jstoken=1515144655");
			String info = page.asText();
			String[] infoarry = info.split(",");
			String xx = infoarry[2].replace("'", "");
			String code = infoarry[1].replace("'", "");
			String sess = infoarry[3].replace("'", "");
			String vecode = infoarry[0].replace("'", "");
			System.out.println(vecode + "-----");
			if (!vecode.contains(MessageConstamts.STRING_1)) {
				flg = false;
				map.put("xx", xx);
				map.put("code", code);
				map.put("sess", sess);
				HttpSession session = request.getSession();
				session.setAttribute("webClients", webClients);
			}
			if (vecode.contains(MessageConstamts.STRING_1)
					&& count == MessageConstamts.INT_10) {
				map.put("code", "用户邮箱异常,请明天再来尝试");
				flg = false;
			}
		} while (flg);
		return map;

	}

	/**
	 * 邮件抓取
	 *
	 * @param request
	 * @param qqnumber
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Map<String, Object> test(HttpServletRequest request,
			String qqnumber, String sess, String password, String code,
			String card, String showpwd) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException, InterruptedException {

		HttpSession session = request.getSession();
		List<Object> lists = new ArrayList<Object>();
		Map<String, Object> map = new HashMap<String, Object>(8);
		WebClient client = (WebClient) session.getAttribute("webClients");
		System.out.println(sess + "sess");
		System.out.println(password);
		HtmlPage pages = client
				.getPage("https://ssl.ptlogin2.qq.com/login?pt_vcode_v1=0&pt_verifysession_v1="
						+ sess
						+ "&verifycode="
						+ code
						+ "&u="
						+ qqnumber
						+ "&p="
						+ password
						+ "&pt_randsalt=2&ptlang=2052&low_login_enable=1&low_login_hour=720&u1=https%3A%2F%2Fw.mail.qq.com%2Fcgi-bin%2Flogin%3Fvt%3Dpassport%26vm%3Dwsk%26delegate_url%3D%26f%3Dxhtml%26target%3D%26ss%3D1&from_ui=1&fp=loginerroralert&device=2&aid=522005705&daid=4&pt_3rd_aid=0&ptredirect=1&h=1&g=1&pt_uistyle=9&regmaster=&");
		System.out.println(pages.asXml());
		if (pages.asText().contains(MessageConstamts.MESSAGE_10)) {
			HtmlPage pagev = client
					.getPage("http://mail.qq.com/cgi-bin/loginpage");
			// 此处不确定
			if (!pagev.asText().contains(MessageConstamts.MESSAGE_11)) {
				map.put("errorCode", "0004");
				map.put("errorInfo", "请取消独立密码后认证！！！");
			} else {
				String urls = pagev.getUrl().toString()
						.substring(pagev.getUrl().toString().indexOf("?"));
				String mname = "信用卡";
				HtmlPage pageinfo = client
						.getPage("https://w.mail.qq.com/cgi-bin/mail_list?sid="
								+ urls.replace("?sid=", "")
								+ "&t=mail_list&s=search&page=0&pagesize=100&folderid=all&topmails=0&subject=");

				DomNodeList<DomNode> iLis = pageinfo
						.querySelectorAll(".maillist_listItemRight");

				for (int i = 0; i < iLis.size(); i++) {
					HtmlAnchor div = (HtmlAnchor) iLis.get(i);
					if (div.asText().contains("交通银行信用卡电子账单")
							&& div.asText().contains("2017年06月")) {
						HtmlPage xykpage = client
								.getPage("https://w.mail.qq.com/"
										+ div.getHrefAttribute());
						System.out.println(xykpage.asText());
						lists.add(xykpage.asXml());

					}
					if (div.asText().contains("广发卡06月账单")) {
						HtmlPage xykpage = client
								.getPage("https://w.mail.qq.com/"
										+ div.getHrefAttribute());
						System.out.println(xykpage.asText());
						lists.add(xykpage.asXml());

					}
					if (div.asText().contains("招商银行信用卡电子账单")) {
						HtmlPage xykpage = client
								.getPage("https://w.mail.qq.com/"
										+ div.getHrefAttribute());
						System.out.println(xykpage.asText());
						if (xykpage.asText().contains("2017/06")) {
							lists.add(xykpage.asXml());
						}

					}

					if (div.asText().contains("光大银行信用卡电子对账单")) {
						HtmlPage xykpage = client
								.getPage("https://w.mail.qq.com/"
										+ div.getHrefAttribute());
						System.out.println(xykpage.asText());
						if (xykpage.asText().contains("2017/08")) {
							lists.add(xykpage.asXml());
						}

					}
					if (div.asText().contains("邮储银行信用卡电子账单")) {
						HtmlPage xykpage = client
								.getPage("https://w.mail.qq.com/"
										+ div.getHrefAttribute());
						System.out.println(xykpage.asText());
						if (xykpage.asText().contains("2017年08月")) {
							lists.add(xykpage.asXml());
						}
					}
					if (div.asText().contains("中国建设银行信用卡电子账单")) {
						HtmlPage xykpage = client
								.getPage("https://w.mail.qq.com/"
										+ div.getHrefAttribute());
						System.out.println(xykpage.asText());
						if (xykpage.asText().contains("2017-08")) {
							lists.add(xykpage.asXml());
						}
					}
				}
				if (lists.size() > 0) {
					map.put("data", lists);
				} else {
					map.put("data", "null");
				}

				map.put("qqnumber", qqnumber);
				map.put("password", showpwd);
				map.put("card", card);

				map = resttemplate.SendMessage(map, application.getSendip()
						+ "/HSDC/authcode/mailBill");

			}

		} else {
			map.put("errorCode", "0001");
			map.put("errorInfo", pages.asXml());
		}

		return map;
	}

	public Map<String, Object> shixinQuery(HttpServletRequest request,
			String pCardNum, String pName, String pCode)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {

		Map<String, Object> data = new HashMap<String, Object>(8);
		Map<String, Object> map = new HashMap<String, Object>(8);
		HttpSession session = request.getSession();
		WebClient webClient = (WebClient) session.getAttribute("webClient");
		HtmlPage page = (HtmlPage) session.getAttribute("page");
		HtmlForm form = page.getForms().get(0);
		HtmlTextInput htmlTextInput = form.getInputByName("pName");
		HtmlTextInput htmlTextInput1 = form.getInputByName("pCardNum");
		HtmlTextInput htmlTextInput2 = form.getInputByName("pCode");
		HtmlHiddenInput htmlTextInput4 = (HtmlHiddenInput) page
				.getElementById("captchaId");
		htmlTextInput.setValueAttribute(pName);
		htmlTextInput1.setValueAttribute(pCardNum);
		HtmlTextInput htmlTextInput3 = (HtmlTextInput) page
				.getElementById("pCode");
		htmlTextInput3.setValueAttribute(pCode);
		HtmlDivision division = (HtmlDivision) page
				.querySelector(".login_button");
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		HtmlPage pageen = division.click();
		if (pageen.asText().contains(MessageConstamts.SEND_CODE_YES)) {
			map.put("errorCode", "0001");
			map.put("errorInfo", "验证码错误");
		} else {
			TextPage pagetext;
			if (pageen.asText().contains(MessageConstamts.MESSAGE_12)) {
				map.put("data", "");
			} else {
				pagetext = webClient
						.getPage("http://shixin.court.gov.cn/disDetailNew?id=120680208&pCode="
								+ pCode
								+ "&captchaId="
								+ htmlTextInput4.getValueAttribute() + "");
				map.put("data", pagetext.getContent());
			}

			// 推送数据

			map.put("pCardNum", pCardNum);
			map = resttemplate.SendMessage(map, application.getSendip()
					+ "/HSDC/grade/humanLaw");

		}

		return map;

	}

	public Map<String, Object> shixinQueryCode(HttpServletRequest request)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {

		HttpSession session = request.getSession();

		Map<String, Object> map = new HashMap<String, Object>(8);
		Map<String, Object> data = new HashMap<String, Object>(8);
		WebClient webClient = crawlerUtil.WebClientperson();
		HtmlPage page = webClient
				.getPage("http://shixin.court.gov.cn/index_new_form.do");
		HtmlImage image = (HtmlImage) page.getElementById("captchaImg");
		// 此处可能有异常
		ImageReader ioim = image.getImageReader();
		BufferedImage bufferedImage = ioim.read(0);

		File path = new File(request.getSession().getServletContext()
				.getRealPath("/upload")
				+ "/"); // 此目录保存缩小后的关键图
		if (!path.exists() && !path.isDirectory()) {
			System.out.println("//不存在");
			path.mkdir();
		}

		String filename = System.currentTimeMillis() + "renxin.png";
		ImageIO.write(bufferedImage, "png", new File(path, filename));
		data.put("port", application.getPort());
		data.put("host", application.getIp());
		data.put("PathName", "/upload/" + filename);
		map.put("errorCode", "0000");
		map.put("errorInfo", "查询成功");
		map.put("data", data);
		session.setAttribute("webClient", webClient);
		session.setAttribute("page", page);
		return map;

	}

	public Map<String, Object> academicLogin(HttpServletRequest request,
			String username, String userpwd, String code, String lt,
			String userCard, String uuId)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		Map<String, Object> map = new HashMap<String, Object>(8);
		//认证失败原因推送
		Map<String ,Object> endstatemap=new HashMap<String, Object>(16);
		endstatemap.put("cardNumber", userCard);
		endstatemap.put("approveItem", "CHSI");
		// ---------------------数据中心推送状态----------------------
		PushState.state(userCard, "CHSI", 100);
		PushSocket.pushnew(map, uuId, "1000", "登录中");
		HttpSession session = request.getSession();
		WebClient webClient = (WebClient) session
				.getAttribute("xuexinWebClient");

		WebRequest webRequest = new WebRequest(new java.net.URL(
				crawlerUtil.XuexinPOST));
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new NameValuePair("username", username));
		list.add(new NameValuePair("password", userpwd));
		list.add(new NameValuePair("captcha", code));

		list.add(new NameValuePair("lt", lt));
		list.add(new NameValuePair("_eventId", "submit"));
		list.add(new NameValuePair("submit", "登  录"));

		webRequest.setHttpMethod(HttpMethod.POST);
		webRequest.setRequestParameters(list);
		try {
			HtmlPage pages = webClient.getPage(webRequest);

			// HtmlDivision Logindiv= (HtmlDivision)
			// pages.getElementById("status");
			if (!pages.asText().contains(MessageConstamts.MESSAGE_01)
					&& !pages.asText().contains(MessageConstamts.MESSAGE_02)) {
				// PushSocket.push(map, UUID, "0000");
				PushSocket.pushnew(map, uuId, "2000", "登录成功");

				logger.info("学信网登录成功，准备获取数据");
				HtmlPage pagess = webClient
						.getPage(CrawlerUtil.getXuexininfo());
				// HtmlTable table=(HtmlTable)
				DomNodeList<DomElement> img = pagess
						.getElementsByTagName("img");
				List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
				PushSocket.pushnew(map, uuId, "5000", "获取数据中");
				for (DomElement dom : img) {
					String aClass = dom.getAttribute("class");
					if (aClass.equals("xjxx-img")) {
						String src = dom.getAttribute("src");
						UnexpectedPage page = webClient.getPage(src);
						InputStream contentAsStream = page.getWebResponse()
								.getContentAsStream();
						BufferedImage read = ImageIO.read(contentAsStream);
						Map<String, String> dataMap = new HashMap<>(8);
						// 获取图片的二进制
						String jpg = RecognizeImage.getImageBinary(read, "jpg");

						String realPath = request.getSession()
								.getServletContext().getRealPath("/xzimage");
						File file = new File(realPath);
						if (!file.exists()) {
							file.mkdirs();
						}
						String fileName = "xz" + System.currentTimeMillis()
								+ ".jpg";
						ImageIO.write(read, "jpg", new File(file, fileName));
						org.json.JSONObject jsonObject = RecognizeImage
								.recognizeImage(realPath + "/" + fileName);
						System.out.println(jsonObject.toString(2));
						String result = jsonObject.get("words_result")
								.toString();

						JSONArray jsonArray = JSONArray.fromObject(result);

						dataMap.put("schoolLength", "");
						dataMap.put("schoolName", "");
						dataMap.put("studentNumber", "");
						dataMap.put("classGrade", "");
						dataMap.put("QualificationsType", "");
						dataMap.put("graduateTime", "");
						dataMap.put("learningType", "");
						dataMap.put("nationality", "");
						dataMap.put("level", "");
						dataMap.put("joinTime", "");
						dataMap.put("birthdayTime", "");
						dataMap.put("domain", "");
						dataMap.put("branchCourts", "");
						dataMap.put("schoolStatus", "");
						dataMap.put("cardNumber", "");
						dataMap.put("imageUrl", jpg);
						for (int i = 0; i < jsonArray.size(); i++) {
							String words = jsonArray.getJSONObject(i)
									.get("words").toString();
							if (words.contains("名:")) {
								// dataMap.put("",(words.split("名:"))[1]);
							} else if (words.contains("性别:")) {
								// dataMap.put("",(words.split("性别:"))[1]);
							} else if (words.contains("生日期:")) {
								dataMap.put("birthdayTime",
										(words.split("生日期:"))[1]);
							} else if (words.contains("民族:")) {
								dataMap.put("nationality",
										(words.split("民族:"))[1]);
							} else if (words.contains("码:")) {
								dataMap.put("cardNumber",
										(words.split("码:"))[1]);
							} else if (words.contains("称:")) {
								dataMap.put("schoolName",
										(words.split("称:"))[1]);
							} else if (words.contains("次:")) {
								dataMap.put("level", (words.split("次:"))[1]);
							} else if (words.contains("业:")) {
								dataMap.put("domain", (words.split("业:"))[1]);
							} else if (words.contains("制:")) {
								dataMap.put("schoolLength",
										(words.split("制:"))[1]);
							} else if (words.contains("类别:")) {
								dataMap.put("QualificationsType",
										(words.split("类别:"))[1]);
							} else if (words.contains("式:")) {
								dataMap.put("learningType",
										(words.split("式:"))[1]);
							} else if (words.contains("院:")) {
								dataMap.put("branchCourts",
										(words.split("院:"))[1]);
							} else if (words.contains("级:")) {
								dataMap.put("classGrade",
										(words.split("级:"))[1]);
							} else if (words.contains("号:")) {
								dataMap.put("studentNumber",
										(words.split("号:"))[1]);
							} else if (words.contains("学日期:")) {
								dataMap.put("joinTime",
										(words.split("学日期:"))[1]);
							} else if (words.contains("校日期:")) {
								dataMap.put("graduateTime",
										(words.split("校日期:"))[1]);
							} else if (words.contains("态:")) {
								dataMap.put("schoolStatus",
										(words.split("态:"))[1]);
							}
						}
						listData.add(dataMap);
					}
				}
				// 获取数据失败则不推长连接（认证状态）
				PushSocket.pushnew(map, uuId, "6000", "获取数据成功");
				map.put("data", listData);
				map.put("CHSIAcount", username);
				map.put("CHSIPassword", userpwd);
				map.put("Usercard", userCard);
				map = resttemplate.SendMessage(map, ConstantInterface.port
						+ "/HSDC/authcode/hireright");
				System.out.println("学信网数据中心返回结果----" + map);
				// --------------------数据中心推送状态----------------------
				if (map.get(MessageConstamts.MESSAGE_13).equals(
						MessageConstamts.STRING_0000)) {
					PushState.state(userCard, "CHSI", 300);
					PushSocket.pushnew(map, uuId, "8000", "认证成功");
				} else {
					PushState.state(userCard, "CHSI", 200);
					PushSocket.pushnew(map, uuId, "9000", map.get("errorInfo").toString());
					//认证失败原因推送
					endstatemap.put("message", map.get("errorInfo").toString());
					PushState.endstate(endstatemap);
				}

				// ---------------------数据中心推送状态----------------------
			} else if (pages.asText().contains(MessageConstamts.MESSAGE_14)) {
				map.put("errorCode", "0002");
				map.put("errorInfo", "您输入的用户名或密码有误");
				// --------------------数据中心推送状态----------------------
				PushState.state(userCard, "CHSI", 200);
				PushSocket.pushnew(map, uuId, "3000", "您输入的用户名或密码有误");
				//认证失败原因推送
				endstatemap.put("message", "您输入的用户名或密码有误");
				PushState.endstate(endstatemap);
				// ---------------------数据中心推送状态----------------------

			} else if (pages.asText().contains(MessageConstamts.MESSAGE_15)) {
				map.put("errorCode", "0001");
				map.put("errorInfo", "图片验证码输入有误");
				// --------------------数据中心推送状态----------------------
				PushState.state(userCard, "CHSI", 200);
				PushSocket.pushnew(map, uuId, "3000", "图片验证码输入有误");
				//认证失败原因推送
				endstatemap.put("message", "图片验证码输入有误");
				PushState.endstate(endstatemap);
				// ---------------------数据中心推送状态----------------------
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e.toString().contains(MessageConstamts.MESSAGE_16)) {
				// --------------------数据中心推送状态----------------------
				PushState.state(userCard, "CHSI", 200);
				// ---------------------数据中心推送状态----------------------
				map.put("errorCode", "0002");
				map.put("errorInfo", "密码错误");
				PushSocket.pushnew(map, uuId, "3000", "密码错误");
				//认证失败原因推送
				endstatemap.put("message", "密码错误");
				PushState.endstate(endstatemap);
			} else {
				// --------------------数据中心推送状态----------------------
				PushState.state(userCard, "CHSI", 200);
				// ---------------------数据中心推送状态----------------------
				map.put("errorCode", "0002");
				map.put("errorInfo", "网络错误");
				PushSocket.pushnew(map, uuId, "3000", "网络错误");
				//认证失败原因推送
				endstatemap.put("message", "网络错误");
				PushState.endstate(endstatemap);
			}
		}
		return map;
	}

	/**
	 * 淘宝信息认证
	 * 
	 * @param request
	 * @param usernumber
	 * @param userPwd
	 * @param
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Map<String, Object> taoBao(HttpServletRequest request,
			String usernumber, String userPwd, String userCard)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		// 定义是否重复爬取
		boolean flg = false;
		// 初始化爬取测试
		int count = 0;
		// 定义最大爬取次数
		int maxcount = 3;
		Map<String, Object> map = new HashMap<String, Object>(8);
		Map<String, Object> data = new HashMap<String, Object>(8);
		// WebClient webClient = new
		// WebClient(BrowserVersion.CHROME,Scheduler.port,Scheduler.ip);
		// WebClient webClient = new WebClient(BrowserVersion.CHROME,
		// Scheduler.ip, Scheduler.port);
		WebClient webClient = new WebClient();
		webClient.getOptions().setUseInsecureSSL(true);
		// 开启cookie管理
		webClient.getCookieManager().setCookiesEnabled(true);
		webClient.getOptions().setTimeout(100000);
		webClient.getOptions().setCssEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.setJavaScriptTimeout(100000);
		webClient.getOptions().setRedirectEnabled(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		// 认证中
		PushState.state(userCard, "TaoBao", 100);
		System.out.println("推送认证中==" + userCard);
		do {
			count++;
			WebRequest webRequest = new WebRequest(new URL(
					"https://login.taobao.com/member/login.jhtml"));
			webClient.addRequestHeader("accept-Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			// webRequest.setAdditionalHeader("accept-Content-Type",
			// "application/x-www-form-urlencoded; charset=UTF-8");
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new NameValuePair("TPL_password", userPwd));
			list.add(new NameValuePair("TPL_username", usernumber));
			list.add(new NameValuePair("newlogin", "1"));
			list.add(new NameValuePair("callback", "1"));
			webRequest.setHttpMethod(HttpMethod.POST);
			webRequest.setRequestParameters(list);
			HtmlPage pagess = webClient.getPage(webRequest);

			if (pagess.getTitleText().equals(MessageConstamts.MESSAGE_17)) {
				String token = pagess.asXml()
						.substring(pagess.asXml().indexOf("token")).split("&")[0]
						.toString().replaceAll("token=", "");
				String token2 = pagess
						.asXml()
						.substring(
								pagess.asXml().indexOf("token",
										pagess.asXml().indexOf("token") + 1))
						.split("&")[0].toString().replaceAll("token=", "");
				HtmlPage page = webClient
						.getPage("https://passport.alibaba.com/mini_apply_st.js?site=0&token="
								+ token + "&callback=callback");
				HtmlPage page2 = webClient
						.getPage("https://passport.alibaba.com/mini_apply_st.js?site=0&token="
								+ token2 + "&callback=callback");
				System.out.println(page2.asXml());
				HtmlPage pagev = webClient
						.getPage("https://login.taobao.com/member/login.jhtml?redirectURL=http%3A%2F%2Fwww.taobao.com%2F");
				webClient
						.getPage("https://login.taobao.com/member/vst.htm?st="
								+ ""
								+ "&params=style%3Dminisimple%26sub%3Dtrue%26TPL_username%3D"
								+ usernumber
								+ "%26loginsite%3D0%26from_encoding%3D%26not_duplite_str%3D%26guf%3D%26full_redirect%3D%26isIgnore%3D%26need_sign%3D%26sign%3D%26from%3Ddatacube%26TPL_redirect_url%3Dhttp%25253A%25252F%25252Fmofang.taobao.com%25252Fs%25252Flogin%26css_style%3D%26allp%3D&_ksTS=1404787873165_78&callback=jsonp79");
				HtmlPage enpage = webClient
						.getPage("http://trade.taobao.com/trade/itemlist/list_bought_items.htm?spm=1.7274553.1997525045.2.C6QtVd");
				HtmlPage pagea = webClient
						.getPage("https://member1.taobao.com/member/fresh/deliver_address.htm?addrId=5874841844");
				HtmlTable table;
				if (pagea.querySelectorAll(MessageConstamts.MESSAGE_18)
						.getLength() > 0) {
					flg = false;
					// -------------------------------
					// 成功进入 进入支付宝页面

					WebRequest requests = new WebRequest(new URL(
							"https://authet15.alipay.com/login/certCheck.htm"));
					List<NameValuePair> lists = new ArrayList<NameValuePair>();
					lists.add(new NameValuePair(
							"goto",
							"https://my.alipay.com/portal/i.htm?src=yy_content_jygl&sign_from=3000&sign_account_no=20881124651440950156&src=yy_content_jygl"));
					lists.add(new NameValuePair("tti", "2119"));
					lists.add(new NameValuePair("isIframe", "false"));
					lists.add(new NameValuePair("REMOTE_PCID_NAME",
							"_seaside_gogo_pcid"));
					lists.add(new NameValuePair("is_sign", "Y"));
					lists.add(new NameValuePair("security_activeX_enabled",
							"false"));
					lists.add(new NameValuePair("securityId",
							"web|cert_check|5c7e8f11-ad18-44f0-8187-db1f35c0b835RZ25"));
					requests.setHttpMethod(HttpMethod.POST);
					requests.setRequestParameters(lists);
					HtmlPage pageinfos = webClient.getPage(requests);
					HtmlPage pageinfoss = webClient
							.getPage("https://my.alipay.com/portal/i.htm?src=yy_content_jygl&sign_from=3000&sign_account_no=20881124651440950156&src=yy_content_jygl");
					table = (HtmlTable) pagea.querySelectorAll(".tbl-main")
							.get(0);

					data.put("info", table.asXml());
					data.put("page", pageinfoss.asXml());
					map.put("data", data);
					map.put("userName", usernumber);
					map.put("userPwd", userPwd);
					map.put("userCard", userCard);
					map = resttemplate.SendMessage(map, application.getSendip()
							+ "/HSDC/authcode/taobaoPush");
					if (map != null
							&& MessageConstamts.STRING_0000.equals(map.get(
									MessageConstamts.MESSAGE_13).toString())) {
						PushState.state(userCard, "TaoBao", 300);
						map.put("errorInfo", "查询成功");
						map.put("errorCode", "0000");
					} else {
						// --------------------数据中心推送状态----------------------
						PushState.state(userCard, "TaoBao", 200);
						// ---------------------数据中心推送状态----------------------
						map.put("errorInfo", "查询失败");
						map.put("errorCode", "0001");
					}
					;
					logger.warn("===淘宝" + map.toString());
				} else {
					// 如果没有继续爬取
					flg = true;

				}
				// 如果满足条件 不再爬取，提示稍后再来
				if (flg == true && count == maxcount) {
					PushState.state(userCard, "TaoBao", 200);
					map.put("errorCode", "0002");
					map.put("errorInfo", "目前认证的人较多，请稍后再试");
					logger.warn("读取收获地址table为空！请请检查");
				}
			} else {
				PushState.state(userCard, "TaoBao", 200);
				HtmlDivision division = (HtmlDivision) pagess
						.getElementById("J_Message");
				map.put("errorCode", "0002");
				map.put("errorInfo", division.asText());

			}

		} while (flg);

		return map;

	}

	public Map<String, Object> xuexinGetCode(HttpServletRequest request,
			HttpServletResponse response)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {
		HttpSession session = request.getSession();
		WebClient webClient = crawlerUtil.WebClientXuexin();
		Map<String, Object> map = new HashMap<String, Object>(8);
		Map<String, Object> data = new HashMap<String, Object>(8);
		HtmlPage pagelt = webClient.getPage(CrawlerUtil.getXueXinLogin());
		HtmlHiddenInput hiddenInput = pagelt.getElementByName("lt");
		String lt = hiddenInput.getValueAttribute();
		UnexpectedPage pageimg = webClient.getPage(CrawlerUtil
				.getXueXinGetCode());
		BufferedImage img = ImageIO.read(pageimg.getInputStream());
		String fileName = System.currentTimeMillis() + "xuexin.png";

		File path = new File(request.getSession().getServletContext()
				.getRealPath("/upload")
				+ "/");

		// 此目录保存缩小后的关键图
		if (!path.isDirectory()) {
			path.mkdirs();
		}
		ImageIO.write(img, "png", new File(path, fileName));
		data.put("ip", application.getIp());
		data.put("FileName", fileName);
		data.put("FilePath", "/upload");
		data.put("Port", application.getPort());
		data.put("lt", lt);
		map.put("data", data);
		map.put("errorCode", "0000");
		map.put("errorInfo", "查询成功");
		session.setAttribute("xuexinWebClient", webClient);
		logger.info(application.getIp() + application.getPort() + "/upload"
				+ fileName);
		logger.info(request.getSession().getServletContext()
				.getRealPath("/upload")
				+ "/");
		return map;
	}

}
