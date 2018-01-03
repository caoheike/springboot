package com.reptile.contorller;

import io.swagger.annotations.ApiOperation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.portlet.ModelAndView;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlScript;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.reptile.constants.MessageConstamts;
import com.reptile.model.MobileBean;
import com.reptile.model.TelecomBean;
import com.reptile.model.UnicomBean;
import com.reptile.service.MobileService;
import com.reptile.util.CrawlerUtil;
import com.reptile.util.Dates;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;
import com.sun.jna.Library;
import com.sun.jna.Native;
/**
 * 
* @Title: InterfaceController.java  
* @Package com.reptile.contorller  
* @Description: TODO(爬虫集合)  
* @author Administrator  
* @date 2017年12月29日  
* @version V1.0
 */
@Controller
@RequestMapping("interface")
public class InterfaceController {
	private Logger logger = Logger.getLogger(InterfaceController.class);
	@Autowired
	private application application;
	@Resource
	private MobileService mobileService;
	Resttemplate resttemplate=new Resttemplate();
	private static CrawlerUtil crawlerUtil = new CrawlerUtil();
	

	/**
	 * 获取验证码
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */

	@ApiOperation(value = "获取移动验证码", notes = "")
	@ResponseBody
	@RequestMapping(value = "GetCode", method = RequestMethod.POST)
	public Map<String, Object> getBank(HttpServletRequest request,
			HttpServletResponse response)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {
		return mobileService.telecomLogins(request, response);

	}

	/**
	 * 中国移动登录
	 * 
	 * @param mobileBean
	 * @param request
	 * @param
	 * @param
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@ApiOperation(value = "移动登录查询信息", notes = "这个暂时无法测试！！！需提交自段为：UserIphone，UserPassword，UserCode")
	// 设置标题描述
	@ResponseBody
	@RequestMapping(value = "MobileLogin", method = RequestMethod.POST)

	public Map mobileLogin(MobileBean mobileBean, HttpServletRequest request,
			HttpServletResponse response)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		System.out.println(mobileBean.getUserIphone() + "--");
		return mobileService.login(mobileBean, request, response);
	}

	/**
	 * Rsa页面
	 * 
	 * @param modole
	 * @return
	 */

	@RequestMapping("Rsa")
	public String rsaPass(ModelAndView modole) {
		modole.setViewName("OperatorView/Rsa");
		return "OperatorView/Rsa";
	}

	/**
	 * Rsa加密
	 * 
	 * @param password
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */

	@ApiOperation(value = "移动密码加密", notes = "")

	@ResponseBody
	@RequestMapping(value = "RsaPassword", method = RequestMethod.POST)
	public Map<String, Object> rsaPassword(
			@RequestParam("password") String password)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {
		return mobileService.rsaPassword(password);
	}

	/**
	 * 移动刷新验证码
	 * 
	 * @param password
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */

	@ApiOperation(value = "移动刷新验证码", notes = "")
	@ResponseBody
	@RequestMapping(value = "UpdateCodeImg", method = RequestMethod.POST)
	public Map<String, Object> updateCodeImg(HttpServletRequest request,
			HttpServletResponse response)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		return mobileService.updateCodeImgs(request, response);
	}

	/**
	 * 移动查询归属地
	 * 
	 * @param request
	 * @param response
	 * @param phone
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */

	@ApiOperation(value = "手机卡归属地", notes = "")
	@ResponseBody
	@RequestMapping(value = "MobileBelong", method = RequestMethod.POST)
	public Map<String, Object> mobileBelong(HttpServletRequest request,
			HttpServletResponse response, @RequestParam("phone") String phone)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		return mobileService.mobileBelong(request, response, phone);
	}

//	/**
//	 * 中国联通 获取验证码
//	 * 
//	 * @param request
//	 * @param response
//	 * @param unicombean
//	 * @return
//	 * @throws FailingHttpStatusCodeException
//	 * @throws MalformedURLException
//	 * @throws IOException
//	 */
//	@ResponseBody
//	@RequestMapping("UnicomGetCode")
//	public Map<String, Object> UnicomGetCode(HttpServletRequest request,
//			HttpServletResponse response, UnicomBean unicombean)
//			throws FailingHttpStatusCodeException, MalformedURLException,
//			IOException {
//
//		return mobileService.GetCode(request, response, unicombean);
//	}

	/**
	 * 联通登录接口
	 * 
	 * @param request
	 * @param response
	 * @param unicombean
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
//	@ResponseBody
//	@RequestMapping("UnicomLogin")
//	 public Map<String,Object> UnicomLogin(HttpServletRequest
//	 request,HttpServletResponse response,UnicomBean unicombean) throws
//	 FailingHttpStatusCodeException, MalformedURLException, IOException {
////	public Map<String, Object> UnicomLogin(HttpServletRequest request,HttpServletResponse response,
//////			@RequestParam("Useriphone") String Useriphone,
//////			@RequestParam("UserPassword") String UserPassword,
//////			@RequestParam("UserCode") String UserCode)
////			throws FailingHttpStatusCodeException, MalformedURLException,
////			IOException {
////
////		UnicomBean u = new UnicomBean();
////		u.setUseriphone(Useriphone);
////		u.setUserPassword(UserPassword);
////		u.setUserCode(UserCode);
//		System.out.println("已经被访问了");
//		return mobileService.UnicomLogin(request, unicombean);
//	}

	/**
	 * 联通切换验证码
	 * 
	 * @param request
	 * @param response
	 * @param unicombean
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("UnicomUpdateCode")
	public Map<String, Object> unicomUpdateCode(HttpServletRequest request,
			HttpServletResponse response, UnicomBean unicombean)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {
		System.out.println("已经被访问了");
		return mobileService.unicomUpdateCode(request, response, unicombean);
	}

	// /**
	// * 中国电信
	// * Login操作
	// */
	// @ResponseBody
	// @RequestMapping("UnicomLogin")
	// public String UnicomLogins(HttpServletRequest request,HttpServletResponse
	// response,TelecomBean Telecom) throws FailingHttpStatusCodeException,
	// MalformedURLException, IOException {
	// System.out.println("已经被访问了");
	// return mobileService.TelecomLogin(request, response, Telecom);
	// }

	/**
	 * 中国电信加密
	 */

	@RequestMapping("UnicomAesPage")
	public String unicomAesPage(HttpServletRequest request,
			HttpServletResponse response, TelecomBean teleCom)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		return "OperatorView/UnicomAes";
	}

	/**
	 * Rsa加密
	 * 
	 * @param password
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */

	@ResponseBody
	@RequestMapping(value = "UnicomAes", method = RequestMethod.POST)
	@ApiOperation(value = "电信密码加密", notes = "电信密码加密")
	public Map<String, Object> unicomAes(@RequestParam("AesPwd") String aesPwd)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		return mobileService.unicomAes(aesPwd);
	}

	@ResponseBody
	@RequestMapping(value = "TelecomLogin", method = RequestMethod.POST)
	@ApiOperation(value = "电信登录", notes = "电信登录")
	public Map<String, Object> telecomLogin(HttpServletRequest req,
			HttpServletResponse response,
			@RequestParam("userPhone") String userPhone,
			@RequestParam("userPassword") String userPassword)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		TelecomBean bean = new TelecomBean();
		bean.setUserPhone(userPhone);
		bean.setUserPassword(userPassword);

		return mobileService.telecomLogin(req, response, bean);
	}

	@ResponseBody
	@RequestMapping("TelecomQueryInfo")
	public void telecomQueryInfo(HttpServletRequest req,
			HttpServletResponse response, TelecomBean bean,
			@RequestParam("papertype") String papertype)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		mobileService.telecomQueryInfo(req, response, bean);
	}

	/**
	 * 跳转邮箱加密页面
	 * 
	 * @return
	 */

	@RequestMapping("encrypt")
	public String encrypt(HttpServletRequest request,
			@RequestParam("IdCard") String idCard)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		HttpSession session = request.getSession();
		session.setAttribute("IdCard", idCard);
		return "OperatorView/encrypt";
	}

	/**
	 * 获取信息准备加密
	 */
	@ResponseBody
	@RequestMapping("encryptrsa")
	public Map<String, Object> encryptrsa(HttpServletRequest request,
			@RequestParam("qqnumber") String qqnumber)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		return mobileService.encryptrsa(request, qqnumber);
	}

	@RequestMapping("test")
	public String test(HttpServletRequest request,
			@RequestParam("qqnumber") String qqnumber,
			@RequestParam("sess") String sess,
			@RequestParam("password") String password,
			@RequestParam("code") String code,
			@RequestParam("showpwd") String showpwd)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		HttpSession session = request.getSession();
		Map<String, Object> map = new HashMap<String, Object>(8);
		String page = "";

		map = mobileService.test(request, qqnumber, sess, password, code,
				session.getAttribute("IdCard").toString(), showpwd);
		if (map.toString().contains(MessageConstamts.STRING_0000)) {
			page = "OperatorView/indexsuccess";
		} else if (map.toString().contains(MessageConstamts.STRING_0004)) {
			page = "OperatorView/index";
		} else {
			page = "OperatorView/indexone";
		}
		return page;

	}

	/**
	 * 人法网
	 */
	@ApiOperation(value = "人法网登陆", notes = "人法网登陆")
	@ResponseBody
	@RequestMapping(value = "shixinQuery", method = RequestMethod.POST)
	public Map<String, Object> shixinQuery(HttpServletRequest request,
			@RequestParam("pName") String pName,
			@RequestParam("pCardNum") String pCardNum,
			@RequestParam("pCode") String pCode)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {

		return mobileService.shixinQuery(request, pCardNum, pName, pCode);

	}

	@ApiOperation(value = "人法网验证码", notes = "无需参数")
	@ResponseBody
	@RequestMapping(value = "shixinQueryCode", method = RequestMethod.POST)
	public Map<String, Object> shixinQueryCode(HttpServletRequest request)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {

		return mobileService.shixinQueryCode(request);
	}

	@ApiOperation(value = "学信网查询", notes = "无需参数")
	@ResponseBody
	@RequestMapping(value = "AcademicLogin", method = RequestMethod.POST)
	public Map<String, Object> academicLogin(HttpServletRequest request,
			@RequestParam("Usernumber") String userNumber,
			@RequestParam("UserPwd") String userPwd,
			@RequestParam("Usercard") String userCard,
			@RequestParam("lt") String lt, @RequestParam("code") String code,@RequestParam("UUID")String uuId)
			throws Exception {
		return mobileService.academicLogin(request, userNumber, userPwd, code,
				lt, userCard,uuId);

	}

	@RequestMapping(value = "payLogin", method = RequestMethod.GET)
	public String payLogin(HttpServletRequest request)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		return "OperatorView/payLogin";

	}

	// @ResponseBody
	// @RequestMapping(value="pay",method=RequestMethod.GET)
	// public Map<String,Object> pay(HttpServletRequest
	// request,@RequestParam("userName") String
	// userName,@RequestParam("userPassword") String userPassword) throws
	// FailingHttpStatusCodeException, MalformedURLException, IOException,
	// InterruptedException {
	//
	// Map<String,Object> map=new HashMap<String, Object>();
	// WebClient webClient = new WebClient();
	// webClient.getOptions().setUseInsecureSSL(true);
	// webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
	// webClient.getOptions().setTimeout(100000);
	// webClient.getOptions().setCssEnabled(true);
	// webClient.getOptions().setJavaScriptEnabled(true);
	// webClient.setJavaScriptTimeout(100000);
	// webClient.getOptions().setRedirectEnabled(true);
	// webClient.getOptions().setThrowExceptionOnScriptError(false);
	// webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
	// webClient.setAjaxController(new NicelyResynchronizingAjaxController());
	// WebRequest webRequest=new WebRequest(new
	// java.net.URL("https://login.taobao.com/member/login.jhtml"));
	// webClient.addRequestHeader("accept-Content-Type",
	// "application/x-www-form-urlencoded; charset=UTF-8");
	// webRequest.setAdditionalHeader("accept-Content-Type",
	// "application/x-www-form-urlencoded; charset=UTF-8");
	// List<NameValuePair> list=new ArrayList<NameValuePair>();
	// list.add(new NameValuePair("TPL_password", userPassword));
	// list.add(new NameValuePair("TPL_username", userName));
	// list.add(new NameValuePair("newlogin", "1"));
	// list.add(new NameValuePair("callback", "1"));
	// webRequest.setHttpMethod(HttpMethod.POST);
	// webRequest.setRequestParameters(list);
	// HtmlPage page= webClient.getPage(webRequest);
	// String
	// token=page.asXml().substring(page.asXml().indexOf("token")).split("&")[0].toString().replaceAll("token=",
	// "");
	// HtmlPage pages=
	// webClient.getPage("https://passport.alipay.com/mini_apply_st.js?site=0&token="+token+"&callback=vstCallback65");
	// String[] sts=pages.asText().split(";");
	// String sta=sts[2].replace("vstCallback65(", "").replace(")", "");
	// JSONObject jsonObjects=JSONObject.fromObject(sta);
	// String
	// st=JSONObject.fromObject(jsonObjects.getString("data")).getString("st");
	// HtmlPage pageinfo=
	// webClient.getPage("https://login.taobao.com/member/vst.htm?st="+st+"&params=style%3Dminisimple%26sub%3Dtrue%26TPL_username%3D"+userName+"%26loginsite%3D0%26from_encoding%3D%26not_duplite_str%3D%26guf%3D%26full_redirect%3D%26isIgnore%3D%26need_sign%3D%26sign%3D%26from%3Ddatacube%26TPL_redirect_url%3Dhttp%25253A%25252F%25252Fmofang.taobao.com%25252Fs%25252Flogin%26css_style%3D%26allp%3D&_ksTS=1404787873165_78&callback=jsonp79");
	// HtmlPage page2=
	// webClient.getPage("https://member1.taobao.com/member/fresh/deliver_address.htm?spm=a1z08.2.a210b.8.6ec3169evDTXbB");
	// HtmlTable table=(HtmlTable) page2.querySelector(".tbl-main");
	// String dizhi=table.asText();
	// Set<Cookie> cookies = webClient.getCookieManager().getCookies();
	// UnexpectedPage pagepay=
	// webClient.getPage("https://i.taobao.com/my_taobao_api/alipay_blance.json?_ksTS=1501638125567_716");//打开支付宝
	// String payinfo =pagepay.getWebResponse().getContentAsString(); //balance
	// 支付宝余额 0.8余额宝 107 累计收益
	// JSONObject jsonObject2=JSONObject.fromObject(payinfo);
	// System.out.println("开始进入支付宝");
	// WebRequest requests=new WebRequest(new
	// URL("https://authet15.alipay.com/login/certCheck.htm"));
	// List<NameValuePair> lists=new ArrayList<NameValuePair>();
	// lists.add(new
	// NameValuePair("goto","https://my.alipay.com/portal/i.htm?src=yy_content_jygl&sign_from=3000&sign_account_no=20881124651440950156&src=yy_content_jygl"));
	// lists.add(new NameValuePair("tti","2119"));
	// lists.add(new NameValuePair("isIframe","false"));
	// lists.add(new NameValuePair("REMOTE_PCID_NAME","_seaside_gogo_pcid"));
	// lists.add(new NameValuePair("is_sign","Y"));
	// lists.add(new NameValuePair("security_activeX_enabled","false"));
	// lists.add(new
	// NameValuePair("securityId","web|cert_check|5c7e8f11-ad18-44f0-8187-db1f35c0b835RZ25"));
	// requests.setHttpMethod(HttpMethod.POST);
	// requests.setRequestParameters(lists);
	// HtmlPage pageinfos= webClient.getPage(requests);
	// HtmlPage pageinfoss=
	// webClient.getPage("https://my.alipay.com/portal/i.htm?src=yy_content_jygl&sign_from=3000&sign_account_no=20881124651440950156&src=yy_content_jygl");
	// HtmlDivision division=(HtmlDivision)
	// pageinfoss.querySelector(".i-content");//花呗额度
	// map.put("dizhi",dizhi);
	// map.put("yue",
	// JSONObject.fromObject(jsonObject2.get("data")).getString("balance"));
	// map.put("shouyi",
	// JSONObject.fromObject(jsonObject2.get("data")).getString("totalProfit"));
	// map.put("yeb",
	// JSONObject.fromObject(jsonObject2.get("data")).getString("totalQuotient"));
	// map.put("zfb", division.asXml());
	// map.put("error", "成功");
	//
	//
	//
	//
	//
	//
	//
	// return map;
	//
	// }

	@ResponseBody
	@RequestMapping(value = "pay", method = RequestMethod.POST)
	public Map<String, Object> pay(HttpServletRequest request,
			@RequestParam("userName") String userName,
			@RequestParam("userPassword") String userPassword,
			@RequestParam("userCard") String userCard)
			throws Exception {

		return mobileService.taoBao(request, userName, userPassword, userCard);

	}

	@ApiOperation(value = "学信网获得验证码", notes = "无需参数")
	@ResponseBody
	@RequestMapping(value = "XuexinGetCode", method = RequestMethod.POST)
	public Map<String, Object> xuexinGetCode(HttpServletRequest request,
			HttpServletResponse response)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		return mobileService.xuexinGetCode(request, response);

	}

	@RequestMapping(value = "agreement.html", method = RequestMethod.GET)
	public String agreement(HttpServletRequest request,
			HttpServletResponse response)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		return "OperatorView/agreement";

	}

	@RequestMapping(value = "index.html", method = RequestMethod.GET)
	public String index(HttpServletRequest request, HttpServletResponse response)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		return "OperatorView/indexsuccess";

	}

	/**
	 * 登陆
	 * @param request
	 * @param response
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws NotFoundException
	 */
	@ResponseBody
	@RequestMapping(value = "tab.html", method = RequestMethod.POST)
	public Map<String,Object> test(HttpServletRequest request, HttpServletResponse response)throws FailingHttpStatusCodeException, MalformedURLException,IOException, InterruptedException, NotFoundException {
		new CrawlerUtil();
		//生成UUid 用于区分浏览器
		String sessid=CrawlerUtil.getUUID(); 
		WebClient webClient = new WebClientFactory().getWebClientJs();
		// 此目录保存缩小后的关键图
			File path = new File(request.getSession().getServletContext().getRealPath("/upload")+"/"); 
		  TextPage page= webClient.getPage("https://qrlogin.taobao.com/qrcodelogin/generateQRCode4Login.do");
		  JSONObject jsonObject=JSONObject.fromObject(page.getContent());
		  UnexpectedPage paerwm= webClient.getPage("https:"+jsonObject.get("url"));
		  System.out.println(jsonObject.get("url"));
		  BufferedImage img=ImageIO.read(paerwm.getInputStream());
			String filename = System.currentTimeMillis() + "renxin.png";
			if (!path.exists() && !path.isDirectory()) {
				System.out.println("//不存在");
				path.mkdir();
			}
		  ImageIO.write(img,"png", new File(path,filename));
		  request.getSession().setAttribute("webClient", webClient);
		  

	        MultiFormatReader formatReader=new MultiFormatReader();

	        File file=new File(path,filename);
	        BufferedImage image=ImageIO.read(file);

	        BinaryBitmap binaryBitmap=new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));

	        //定义二维码的参数:
	        HashMap hints=new HashMap(8);
	      //定义字符集
	        hints.put(EncodeHintType.CHARACTER_SET,"utf-8");
	      //开始解析
	        Result result=formatReader.decode(binaryBitmap,hints);

	        System.out.println("解析结果:"+result.toString());
	        System.out.println("二维码的格式类型是:"+result.getBarcodeFormat());
	        System.out.println("二维码的文本内容是:"+result.getText());

			Map<String, Object> map = new HashMap<String, Object>(8);
			Map<String, Object> data = new HashMap<String, Object>(8);
			map.put("errorCode", "0000");
			map.put("errorInfo", "查询成功");
			data.put("url", result.getText());
			
			request.getSession().setAttribute(sessid, webClient);
			data.put("sessid", sessid);
			data.put("Token", jsonObject.get("lgToken"));
			map.put("data", data);
			return map;
		


	}
	/**
	 * 获得详情
	 * @param request
	 * @param response
	 * @param sessid
	 * @param Token
	 * @param idCard
	 * @param uuId
	 * @return
	 * @throws Exception 
	 */
		@ResponseBody
	  @RequestMapping(value = "tabLogin.html", method = RequestMethod.POST)
	  public Map<String,Object> tabLogin(HttpServletRequest request, HttpServletResponse response,@RequestParam("sessid") String sessid,@RequestParam("Token") String toKen ,@RequestParam("idCard") String idCard,@RequestParam("UUID")String uuId)throws Exception {
	    System.out.println("---------------"+"");
	    Map<String, Object> map = new HashMap<String, Object>(8);
	    Map<String, Object> data = new HashMap<String, Object>(8);
	    HttpSession session=request.getSession();
	    WebClient webClient = (WebClient) session.getAttribute(sessid);
	    TextPage pages= webClient.getPage("https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do?lgToken="+toKen+"&defaulturl=https%3A%2F%2Fwww.taobao.com%2F");
	    PushState.state(idCard, "TaoBao",100);
	    PushSocket.pushnew(map, uuId, "1000","登录中");
	    Thread.sleep(2000);
	    System.out.println(pages.getContent());
	    JSONObject jsonObject2=JSONObject.fromObject(pages.getContent());
	    if(jsonObject2.get(MessageConstamts.STRING_CODE).equals(MessageConstamts.STRING_10086)){
	    	HtmlPage pageinfo= webClient.getPage(jsonObject2.getString("url"));
		    System.out.println(pageinfo.asXml());
		    PushSocket.pushnew(map, uuId, "2000","登录成功");
		    PushState.state(idCard, "TaoBao",100);
		    PushSocket.pushnew(map, uuId, "5000","获取数据中");
		    map.put("errorCode", "0000");
		    map.put("errorInfo", "成功");
		    HtmlPage pagev= webClient.getPage("https://member1.taobao.com/member/fresh/deliver_address.htm");
		    HtmlTable  table= pagev.querySelector(".tbl-main");
		    System.out.println(table.asXml()+"收货地址");
		    
		    HtmlPage html = webClient.getPage("https://member1.taobao.com/member/fresh/account_management.htm?spm=a1z08.1.a210b.11.6a3294bcvaQod7");
		    String href = ((HtmlAnchor)html.getByXPath("//*[@id='main-content']/div/div[2]/div/div[2]/p/a[1]").get(0)).getAttribute("href");
		    String signAccountno = this.getSignNo(href);
		    webClient.getPage("https://login.taobao.com/member/login.jhtml?tpl_redirect_url=https%3A%2F%2Fauthgtj.alipay.com%3A443%2Flogin%2FtrustLoginResultDispatch.htm%3FredirectType%3D%26sign_from%3D3000%26goto%3Dhttps%253A%252F%252Fmy.alipay.com%252Fportal%252Fi.htm%253Fsrc%253Dyy_taobao_gl_01%2526sign_from%253D3000%2526sign_account_no%253D"+signAccountno+"%2526guide_q_control%253Dtop%2526guide_q_control%253Dtop%2526src%253Dyy_taobao_gl_01&from_alipay=1");
		    webClient.getPage("https://authgtj.alipay.com/login/trustLoginResultDispatch.htm?redirectType=&sign_from=3000&goto=https%3A%2F%2Fmy.alipay.com%2Fportal%2Fi.htm%3Fsrc%3Dyy_taobao_gl_01%26sign_from%3D3000%26sign_account_no%3D"+signAccountno+"%26guide_q_control%3Dtop%26guide_q_control%3Dtop%26src%3Dyy_taobao_gl_01&sign_from=3000&sign_account_no="+signAccountno+"&sign_date=1513665702&sign_signature=e0jc_hahtfu_cpipr9_xpj_ht_ig_zbt_v5%2Bz2_x_r9lp%2Bo_k_x%2By_jpkc_i_i_e2xylg%3D%3D&nick=xuesongcui");
	    
		    //现在开始爬取 支付宝信息
	       WebRequest requests=new WebRequest(new URL("https://authet15.alipay.com/login/certCheck.htm"));
	       List<NameValuePair> lists=new ArrayList<NameValuePair>();
	       
	       lists.add(new NameValuePair("goto","https://my.alipay.com/portal/i.htm?src=yy_taobao_gl_01&sign_from=3000&sign_account_no=20888028082824820156&guide_q_control=top&guide_q_control=top&src=yy_taobao_gl_01"));
	       lists.add(new NameValuePair("redirectType",""));
	       lists.add(new NameValuePair("loginScene",""));
	       lists.add(new NameValuePair("tti","1302"));
	       lists.add(new NameValuePair("isIframe","false"));
	       lists.add(new NameValuePair("_seaside_gogo_",""));
	       lists.add(new NameValuePair("_seaside_gogo_p","false"));
	       lists.add(new NameValuePair("_seaside_gogo_pcid",""));
	       lists.add(new NameValuePair("is_sign","Y"));
	       lists.add(new NameValuePair("real_sn",""));
	       lists.add(new NameValuePair("signedData",""));
	       lists.add(new NameValuePair("certCmdOutput",""));
	       lists.add(new NameValuePair("certCmdInput",""));
	       lists.add(new NameValuePair("certfg",""));
	       lists.add(new NameValuePair("goto",""));
	       lists.add(new NameValuePair("security_chrome_extension_aliedit_installed",""));
	       lists.add(new NameValuePair("security_chrome_extension_alicert_installed",""));
	       lists.add(new NameValuePair("security_activeX_enabled","false"));
	       lists.add(new NameValuePair("REMOTE_PCID_NAME","_seaside_gogo_pcid"));
	       lists.add(new NameValuePair("security_activeX_enabled","false"));
	       lists.add(new NameValuePair("securityId","web|cert_check|5c7e8f11-ad18-44f0-8187-db1f35c0b835RZ25"));
	       requests.setHttpMethod(HttpMethod.POST);
	       requests.setRequestParameters(lists);
	       HtmlPage pageinfos= webClient.getPage(requests);
	       
	       WebRequest detailInfo = new WebRequest(new URL("https://my.alipay.com/portal/i.htm?src=yy_content_jygl&sign_from=3000&sign_account_no=20881124651440950156&src=yy_content_jygl"));
	       Map<String,String> headers = new HashMap<String, String>(8);
	       headers.put("cookie", this.getCookie(webClient));
	 
	       detailInfo.setHttpMethod(HttpMethod.GET);
	       detailInfo.setAdditionalHeaders(headers);
	       HtmlPage pageinfosss= webClient.getPage(detailInfo);
	       Thread.sleep(2000);
	       
	       
	       HtmlPage pageinfoss= webClient.getPage("https://my.alipay.com/tile/service/portal:recent.tile?t=1513677423273&_input_charset=utf-8&ctoken=nZN5I4t_L29w1rOM&_output_charset=utf-8");
	       PushSocket.pushnew(map, uuId, "6000","获取数据成功");
	       
	       //获取收货地址
	       data.put("addresses",this.getAddress(webClient));
	       data.put("info",table.asXml() );
	       data.put("page",pageinfosss.asXml()+pageinfoss.asXml() );
	       map.put("data", data);
	       map.put("userName", "123");
	       map.put("userPwd", "123");
	       map.put("userCard", idCard);
	       
	       map=resttemplate.SendMessage(map, application.getSendip()+"/HSDC/authcode/taobaoPush");

			if(map!=null&&MessageConstamts.STRING_0000.equals(map.get(MessageConstamts.ERRORCODE).toString())) {
				PushState.state(idCard, "TaoBao", 300);
				map.put("errorInfo", "查询成功");
				map.put("errorCode", "0000");
				PushSocket.pushnew(map, uuId, "8000","淘宝查询成功");
			}else{
				//--------------------数据中心推送状态----------------------
				PushState.state(idCard, "TaoBao",200,map.get("errorInfo").toString());
				//---------------------数据中心推送状态----------------------
				map.put("errorInfo","查询失败");
				map.put("errorCode","0001");
				PushSocket.pushnew(map, uuId, "9000",map.get("errorInfo").toString());
			}
	    }else if(jsonObject2.get(MessageConstamts.STRING_CODE).equals(MessageConstamts.STRING_1004)){
	      PushState.state(idCard, "TaoBao",200,"二维码过期");
	      System.out.println("二维码过期");
	      map.put("errorCode", "0001");
	      map.put("errorInfo", "二维码过期");
	      PushSocket.pushnew(map, uuId, "3000","二维码过期");
	    }else if(jsonObject2.get(MessageConstamts.STRING_CODE).equals(MessageConstamts.STRING_1001)) {
	      if(map.size()==0){
	        map.put("errorCode", "0001");
	        map.put("errorInfo", "请勿乱操作");
		      PushState.state(idCard, "TaoBao",200,"登录失败，操作异常");
		      PushSocket.pushnew(map, uuId, "3000","登录失败，操作异常");
	      }else{
	        map.put("errorCode", "0000");
	        map.put("errorInfo", "成功");
	      }
	    }else if(jsonObject2.get(MessageConstamts.STRING_CODE).equals(MessageConstamts.STRING_10000)){
	    	
	      System.out.println("等待授权");
	      map.put("errorCode", "0001");
	      map.put("errorInfo", "等待授权");
	      PushState.state(idCard, "TaoBao",200,"登录失败，等待授权");
	      PushSocket.pushnew(map, uuId, "3000","登录失败，等待授权");
	      
	    }else{
	    	PushState.state(idCard, "TaoBao",200,"非法操作！请重试");
	      map.put("errorCode", "0001");
	      map.put("errorInfo", "非法操作！请重试");
	      PushSocket.pushnew(map, uuId, "3000","非法操作！请重试");
	    }
	    return map;

	    
	  }
		
		/**
		 * 获取sign_account_no
		 * @param str
		 * @return
		 */
		public String getSignNo(String str){
			String text = "";
			str = str.substring(str.indexOf("?")+1);
			String [] array = str.split("&");
			for (String item : array) {
				if(item.contains("sign_account_no")){
					text = item.split("=")[1];
				}
			}
			return text;
		}
		
		/**
		 * 获取cookie
		 * @param driver
		 * @return
		 */
		public  String getCookie(WebClient webClient)		{
			  //获得cookie用于发包
			Set<Cookie> cookies = webClient.getCookieManager().getCookies();
		    StringBuffer tmpcookies = new StringBuffer();

		   	for (Cookie cookie : cookies) {
		   		String name = cookie.getName();
		   		String value = cookie.getValue();
	   			tmpcookies.append(name + "="+ value + ";");
			}
		   	String str = tmpcookies.toString();
		   	if(!str.isEmpty()){
		   		str = str.substring(0,str.lastIndexOf(";"));
		   	}
			return str; 	
		}
		
		
		/**
		 * 获取收货地址
		 * @param webClient
		 * @return
		 * @throws Exception
		 */
		public List<String> getAddress(WebClient webClient) throws Exception{
			
			  WebRequest requests=new WebRequest(new URL("https://buyertrade.taobao.com/trade/itemlist/asyncBought.htm?action=itemlist/BoughtQueryAction&event_submit_do_query=1&_input_charse"));
		       List<NameValuePair> lists=new ArrayList<NameValuePair>();
		       lists.add(new NameValuePair("pageNum","1"));
		       lists.add(new NameValuePair("pageSize","15"));
		       lists.add(new NameValuePair("dateBegin",Dates.getBeforeTime()+""));
		       lists.add(new NameValuePair("dateEnd",Dates.getCurrentTime()+""));
		       lists.add(new NameValuePair("auctionStatus","SUCCESS"));
		       lists.add(new NameValuePair("prePageNo","1"));
		       requests.setHttpMethod(HttpMethod.POST);
		       requests.setRequestParameters(lists);
		       Map<String,String> headers = new HashMap<String, String>(8);
		       headers.put("origin", "https://buyertrade.taobao.com");
		       headers.put("referer", "https://buyertrade.taobao.com/trade/itemlist/list_bought_items.htm?");
		       requests.setAdditionalHeaders(headers);
		       HtmlPage info= webClient.getPage(requests);
		       String str = info.getWebResponse().getContentAsString();
		       JSONArray array = JSONObject.fromObject(str).getJSONArray("mainOrders");
		       List<String> urls = new ArrayList<String>();
		       
		       for (int i = 0; i < array.size(); i++) {
					try {
						JSONObject obj = array.getJSONObject(i);
						JSONArray operations = obj.getJSONObject("statusInfo").getJSONArray("operations");
						for (int j = 0; j < operations.size(); j++) {
							JSONObject json = operations.getJSONObject(j);
							if(json.getString("id").equals("viewDetail")){
								urls.add(json.getString("url"));
							}
						}
						
					} catch (Exception e) {
						continue;
					}
		       }
		     //分页 
		     String  totalPage = JSONObject.fromObject(str).getJSONObject("page").getString("totalPage");
		     if(!totalPage.isEmpty()){
		    	 int total = new Integer(totalPage);
		    	 for (int i = 2; i <= total; i++) {
					List<String> list = this.getDetail(i, webClient);
					for (String item:list) {
						urls.add(item);
					}
				}
		     }
		 	 List<String> addresses  = new ArrayList<String>();
		 	 for (String url : urls) {
		 		HtmlPage html = webClient.getPage("http:"+url);
		 		logger.warn("----------------url---------"+html.asXml());
		 		String address = this.getAddress(html);
		 		if(!address.isEmpty()){
		 			addresses.add(address);
		 		}
			}
		 	logger.warn("----------------addresses---------"+JSONArray.fromObject(addresses));
		 	 return addresses;
		}
		
		/**
		 * 分页，循环获取每一页的订单信息
		 * @param count
		 * @param webClient
		 * @return
		 * @throws FailingHttpStatusCodeException
		 * @throws IOException
		 */
		public List<String>  getDetail(int count , WebClient webClient) throws FailingHttpStatusCodeException, IOException{
			 WebRequest requests=new WebRequest(new URL("https://buyertrade.taobao.com/trade/itemlist/asyncBought.htm?action=itemlist/BoughtQueryAction&event_submit_do_query=1&_input_charse"));
		       List<NameValuePair> lists=new ArrayList<NameValuePair>();
		       lists.add(new NameValuePair("pageNum",count+""));
		       lists.add(new NameValuePair("pageSize","15"));
		       lists.add(new NameValuePair("dateBegin",Dates.getBeforeTime()+""));
		       lists.add(new NameValuePair("dateEnd",Dates.getCurrentTime()+""));
		       lists.add(new NameValuePair("auctionStatus","SUCCESS"));
		       lists.add(new NameValuePair("prePageNo",(count-1)+""));
		       requests.setHttpMethod(HttpMethod.POST);
		       requests.setRequestParameters(lists);
		       Map<String,String> headers = new HashMap<String, String>(8);
		       headers.put("origin", "https://buyertrade.taobao.com");
		       headers.put("referer", "https://buyertrade.taobao.com/trade/itemlist/list_bought_items.htm?");
		       requests.setAdditionalHeaders(headers);
		       HtmlPage info= webClient.getPage(requests);
		       String str = info.getWebResponse().getContentAsString();
		       JSONArray array = JSONObject.fromObject(str).getJSONArray("mainOrders");
		       List<String> urls = new ArrayList<String>();
		       for (int i = 0; i < array.size(); i++) {
					try {
						JSONObject obj = array.getJSONObject(i);
						JSONArray operations = obj.getJSONObject("statusInfo").getJSONArray("operations");
						for (int j = 0; j < operations.size(); j++) {
							JSONObject json = operations.getJSONObject(j);
							if(json.getString("id").equals("viewDetail")){
								urls.add(json.getString("url"));
							}
						}
						
					} catch (Exception e) {
						continue;
					}
		       }
		       
		       return urls;
		}
		
		/**
		 * 从每个购买记录详情中获取收货地址
		 * @param html
		 * @return
		 */
		public String getAddress(HtmlPage html){
			String text = "";
			try {
				List<DomElement> list = html.getElementsByTagName("script");
				String detailData = "";
				for (int i = 0; i < list.size(); i++) {
					String s = ((HtmlScript)list.get(i)).asXml();
					if(s.contains("detailData")){
						detailData = s.substring(s.indexOf("var detailData = ")+"var detailData = ".length(),s.indexOf("//]]>"));
						logger.warn(((HtmlScript)list.get(i)).asXml());
					}
				}
				JSONArray array = JSONObject.fromObject(detailData).getJSONObject("basic").getJSONArray("lists");
				for (int i = 0; i < array.size(); i++) {
					JSONObject json = array.getJSONObject(i);
					if("收货地址".equals(json.getString("key"))){
						text = json.getJSONArray("content").getJSONObject(0).getString("text");
						return text;
					};
				}
			} catch (Exception e) {
				return text;
			}
			return text;
			
		}
	
//	/**
//	 * OA学信网
//	 */
//	@ResponseBody
//	@RequestMapping(value = "xuexin.html", method = RequestMethod.GET)
//	public Map<String,Object> tabLogin1(HttpServletRequest request, HttpServletResponse response,@RequestParam("UserName") String UserName,@RequestParam("UserPwd") String UserPwd)throws FailingHttpStatusCodeException, MalformedURLException,IOException, InterruptedException, NotFoundException {
//		System.out.println(application.getPort());
//		WebClient webClient =	crawlerUtil.WebClientXuexin();
//		Map<String,Object>map=new HashMap<String, Object>();
//		Map<String,Object>data=new HashMap<String, Object>();
//		HtmlPage pagelt = webClient.getPage(CrawlerUtil.getXueXinLogin());
//		HtmlHiddenInput hiddenInput= pagelt.getElementByName("lt");
//		String lt=hiddenInput.getValueAttribute();
//		UnexpectedPage pageimg=webClient.getPage(CrawlerUtil.getXueXinGetCode());
//		BufferedImage img=ImageIO.read(pageimg.getInputStream());
//		String  fileName = System.currentTimeMillis() + "xuexin.png";
//
//		
//	 	File path = new File(request.getSession().getServletContext().getRealPath("/upload") + "/");
//
//	 	// 此目录保存缩小后的关键图
//	 	if (!path.isDirectory()){
//			path.mkdirs();
//	 	}
//		ImageIO.write(img,"png",new File(path,fileName));
//		
//	  //开始大吗
//		
//		// 注意这里是普通会员账号，不是开发者账号，注册地址 http://www.yundama.com/index/reg/user
//		// 开发者可以联系客服领取免费调试题分
//		String username = "caoheike";
//		String password	= "598415805";
//
//		// 测试时可直接使用默认的软件ID密钥，但要享受开发者分成必须使用自己的软件ID和密钥
//		// 1. http://www.yundama.com/index/reg/developer 注册开发者账号
//		// 2. http://www.yundama.com/developer/myapp 添加新软件
//		// 3. 使用添加的软件ID和密钥进行开发，享受丰厚分成
//		int 	appid	= 1;									
//		String 	appkey	= "22cc5376925e9387a23cf797cb9ba745";
//		
//		// 图片路径
//		String	imagepath	=request.getSession().getServletContext().getRealPath("/upload") + "/"+fileName;
//		System.out.println("地址"+imagepath);
//		//  例：1004表示4位字母数字，不同类型收费不同。请准确填写，否则影响识别率。在此查询所有类型 http://www.yundama.com/price.html
//		int codetype = 1004;
//		
//		// 只需要在初始的时候登陆一次
//		int uid = 0;
//		YDM.INSTANCE.YDM_SetAppInfo(appid, appkey);			// 设置软件ID和密钥
//		uid = YDM.INSTANCE.YDM_Login(username, password);	// 登陆到云打码
//
//		if(uid > 0){
//			System.out.println("登陆成功,正在提交识别...");
//			
//			byte[] byteResult = new byte[30];
//			int cid = YDM.INSTANCE.YDM_DecodeByPath(imagepath, codetype, byteResult);
//			String strResult = new String(byteResult, "UTF-8").trim();
//			
//			// 返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
//			System.out.println("识别返回代码:" + cid);
//			System.out.println("识别返回结果:" + strResult); 
//			
//			//如果返回结果继续执行
//			WebRequest webRequest=new  WebRequest(new java.net.URL(crawlerUtil.XuexinPOST));
//			List<NameValuePair> list=new ArrayList<NameValuePair>();
//			list.add(new NameValuePair("username",UserName));
//			list.add(new NameValuePair("password",UserPwd));
//			list.add(new NameValuePair("captcha", strResult));
//
//			list.add(new NameValuePair("lt", lt));
//			list.add(new NameValuePair("_eventId","submit"));
//			list.add(new NameValuePair("submit","登  录"));
//			
//			webRequest.setHttpMethod(HttpMethod.POST);
//			webRequest.setRequestParameters(list);
//			 try {
//			HtmlPage pages= webClient.getPage(webRequest);
//			
//		//	HtmlDivision Logindiv= (HtmlDivision) pages.getElementById("status");
//			if(!pages.asText().contains("您输入的用户名或密码有误")&&!pages.asText().contains("图片验证码输入有误")){
//		
//		        HtmlPage pagess= webClient.getPage(CrawlerUtil.getXuexininfo());
//	 	        HtmlTable table=(HtmlTable) pagess.querySelector(".mb-table");  
//	 	         data.put("info", table.asXml());
//	 	         map.put("data", data);
//	 	         map.put("Usernumber",UserName); 
//	 	         map.put("UserPwd",UserPwd);
//	 	 
//	 	  
//	 	 
//			}else if(pages.asText().contains("您输入的用户名或密码有误")){
//		 		map.put("errorCode","0002");
//		 		map.put("errorInfo","您输入的用户名或密码有误");
//	
//			}else if(pages.asText().contains("图片验证码输入有误")){
//		 		map.put("errorCode","0001");
//		 		map.put("errorInfo","图片验证码输入有误");
//	
//			}
//		   	} catch (Exception e) {
//		   		System.out.print(e);
//		   		if(e.toString().contains("com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException")){
//		   			map.put("errorCode","0002");
//			 		map.put("errorInfo","密码错误");	
//		   		}else{
//		   			map.put("errorCode","0002");
//			 		map.put("errorInfo","网络错误");
//		   		}
// 	    		
//			}
//
//		}else{
//			System.out.println("登录失败，错误代码为：" + uid);
//		}  
//		
//		return map;
//
//		
//	}
	
}
