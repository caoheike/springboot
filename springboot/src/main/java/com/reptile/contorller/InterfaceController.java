package com.reptile.contorller;

import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

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
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.model.MobileBean;
import com.reptile.model.TelecomBean;
import com.reptile.model.UnicomBean;
import com.reptile.service.MobileService;;


@Controller
@RequestMapping("interface")
public class InterfaceController  {
	@Resource 
	private MobileService mobileService;
	

	/**
	 * 获取验证码
	 * @param request
	 * @param response
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
    @ApiOperation(value="获取移动验证码", notes="")//设置标题描述
	@ResponseBody
	@RequestMapping(value="GetCode", method=RequestMethod.POST)
	public Map<String, Object> getBank(HttpServletRequest request, HttpServletResponse response) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
	return	mobileService.TelecomLogins(request, response);

	
}
	/**
	 * 中国移动登录
	 * @param mobileBean
	 * @param request
	 * @param responsl
	 * @param userType
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws InterruptedException
	 */
    @ApiOperation(value="移动登录查询信息", notes="这个暂时无法测试！！！需提交自段为：UserIphone，UserPassword，UserCode")//设置标题描述
	@ResponseBody
	@RequestMapping(value="MobileLogin",method=RequestMethod.POST)
    //@ApiImplicitParam(name = "mobileBean", value = "移动实体", required = true, dataType = "MobileBean")添加实体
	public Map MobileLogin(MobileBean mobileBean, HttpServletRequest request, HttpServletResponse response) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException{
		System.out.println(mobileBean.getUserIphone()+"--");
		return mobileService.Login(mobileBean, request, response);
	}
	
	/**
	 * Rsa页面
	 * @param modole
	 * @return
	 */
    
	@RequestMapping("Rsa")
	public String Rsa(ModelAndView modole) {
		modole.setViewName("OperatorView/Rsa");
		return "OperatorView/Rsa";
	}
	/**
	 * Rsa加密
	 * @param password
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */

    @ApiOperation(value="移动密码加密", notes="")//设置标题描述
	@ResponseBody
	@RequestMapping(value="RsaPassword",method=RequestMethod.POST)
	public Map<String,Object> RsaPassword(@RequestParam("password") String password) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		return mobileService.RsaPassword(password);
	}
	/**
	 * 移动刷新验证码
	 * @param password
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */

	   	@ApiOperation(value="移动刷新验证码", notes="")//设置标题描述
		@ResponseBody
		@RequestMapping(value="UpdateCodeImg",method=RequestMethod.POST)
	public Map<String,Object> UpdateCodeImg(HttpServletRequest request,HttpServletResponse response) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		return mobileService.UpdateCodeImgs(request,response);
	}
	/**
	 * 移动查询归属地
	 * @param request
	 * @param response
	 * @param phone
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */

	
	   	@ApiOperation(value="手机卡归属地", notes="")//设置标题描述
		@ResponseBody
		@RequestMapping(value="MobileBelong",method=RequestMethod.POST)
	public Map<String,Object> MobileBelong(HttpServletRequest request,HttpServletResponse response,@RequestParam("phone") String phone) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		return mobileService.MobileBelong(request,response,phone);
	}
	/**
	 * 中国联通 
	 * 获取验证码
	 * @param request
	 * @param response
	 * @param unicombean
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("UnicomGetCode")
	public Map<String,Object> UnicomGetCode(HttpServletRequest request,HttpServletResponse response,UnicomBean unicombean) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		return mobileService.GetCode(request, response, unicombean);
	}
	/**
	 * 联通登录接口
	 * @param request
	 * @param response
	 * @param unicombean
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("UnicomLogin")
	public Map<String,Object> UnicomLogin(HttpServletRequest request,HttpServletResponse response,UnicomBean unicombean) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		System.out.println("已经被访问了");
		return mobileService.UnicomLogin(request, response, unicombean);
	}
/**
 * 联通切换验证码
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
	public Map<String, Object> UnicomUpdateCode(HttpServletRequest request,HttpServletResponse response,UnicomBean unicombean) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		System.out.println("已经被访问了");
		return mobileService.UnicomUpdateCode(request, response, unicombean);
	}
	
	
//	/**
//	 * 中国电信
//	 * Login操作
//	 */
//	@ResponseBody
//	@RequestMapping("UnicomLogin")
//	public String UnicomLogins(HttpServletRequest request,HttpServletResponse response,TelecomBean Telecom) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
//		System.out.println("已经被访问了");
//		return mobileService.TelecomLogin(request, response, Telecom);
//	}
	
	/**
	 * 中国电信加密
	 */
	

	@RequestMapping("UnicomAesPage")
	public String UnicomAesPage(HttpServletRequest request,HttpServletResponse response,TelecomBean Telecom) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		return "OperatorView/UnicomAes";
	}
	 
	
	
	/**
	 * Rsa加密
	 * @param password
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	
	@ResponseBody
	@RequestMapping(value="UnicomAes", method = RequestMethod.POST)
	@ApiOperation(value = "电信密码加密", notes = "电信密码加密")
	public Map<String,Object> UnicomAes(@RequestParam("AesPwd") String AesPwd) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
	
		return mobileService.UnicomAes(AesPwd);
	}
	@ResponseBody
	@RequestMapping(value="TelecomLogin", method = RequestMethod.POST)
	@ApiOperation(value = "电信登录", notes = "电信登录")
	public Map<String,Object> TelecomLogin(HttpServletRequest req,HttpServletResponse response,TelecomBean bean) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		return mobileService.TelecomLogin(req,response,bean);
	}
	@ResponseBody
	@RequestMapping("TelecomQueryInfo")
	public void TelecomQueryInfo(HttpServletRequest req,HttpServletResponse response,TelecomBean bean,@RequestParam("papertype") String papertype) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		 mobileService.TelecomQueryInfo(req,response,bean);
	}
	/**
	 * 跳转邮箱加密页面
	 * @return 
	 */

	@RequestMapping("encrypt")
	public String encrypt(HttpServletRequest request,@RequestParam("IdCard") String IdCard) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HttpSession session=request.getSession();
		session.setAttribute("IdCard", IdCard);
		return "OperatorView/encrypt";
	}
	/**
	 * 获取信息准备加密
	 */
	@ResponseBody
	@RequestMapping("encryptrsa")
	public Map<String,Object> encryptrsa(HttpServletRequest request,@RequestParam("qqnumber") String qqnumber) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
    return mobileService.encryptrsa(request,qqnumber);
	}

	@RequestMapping("test")
	public String test(HttpServletRequest request,@RequestParam( "qqnumber") String qqnumber,@RequestParam("sess") String sess,@RequestParam("password")String password,@RequestParam("code")String code,@RequestParam("card") String card,@RequestParam("showpwd") String showpwd ) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HttpSession session=request.getSession();
		Map<String,Object> map=new HashMap<String, Object>();
		String page="";
		
		map= mobileService.test(request, qqnumber, sess, password, code,session.getAttribute("IdCard").toString(),showpwd);
		if(map.toString().contains("0000")){
			page="OperatorView/success";
		}else{
			page="OperatorView/error";
		}
		return page;
		
	}
		

	
	/**
	 * 人法网
	 */
   	@ApiOperation(value="人法网登陆", notes="人法网登陆")//设置标题描述
	@ResponseBody
	@RequestMapping(value="shixinQuery",method=RequestMethod.POST)
	public Map<String,Object> shixinQuery(HttpServletRequest request,@RequestParam("pName") String pName,@RequestParam("pCardNum") String pCardNum,@RequestParam("pCode") String pCode) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

		return mobileService.shixinQuery(request, pCardNum, pName, pCode);

}

	@ApiOperation(value = "人法网验证码", notes = "无需参数")
	// 设置标题描述
	@ResponseBody
	@RequestMapping(value = "shixinQueryCode", method = RequestMethod.POST)
	public Map<String, Object> shixinQueryCode(HttpServletRequest request)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
	
		return mobileService.shixinQueryCode(request);
	}
   
   	@ApiOperation(value="学信网查询", notes="无需参数")//设置标题描述
	@ResponseBody
	@RequestMapping(value="AcademicLogin",method=RequestMethod.POST)
	public Map<String,Object> AcademicLogin(HttpServletRequest request,@RequestParam("Usernumber") String Usernumber,@RequestParam("UserPwd") String UserPwd,@RequestParam("Usercard") String Usercard) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
   		return mobileService.AcademicLogin(request, Usernumber, UserPwd,Usercard);
   		

   	}
   	@RequestMapping(value="payLogin",method=RequestMethod.GET)
   	public String payLogin(HttpServletRequest request) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
   		return "OperatorView/payLogin";

   		
   	}
//   	@ResponseBody
//   	@RequestMapping(value="pay",method=RequestMethod.GET)
//   	public Map<String,Object> pay(HttpServletRequest request,@RequestParam("userName") String userName,@RequestParam("userPassword") String userPassword) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
//
//    	Map<String,Object> map=new HashMap<String, Object>();
//     WebClient webClient = new WebClient();
//	 webClient.getOptions().setUseInsecureSSL(true);
//	 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
//	 webClient.getOptions().setTimeout(100000);
//	 webClient.getOptions().setCssEnabled(true);
//	 webClient.getOptions().setJavaScriptEnabled(true);
//	 webClient.setJavaScriptTimeout(100000); 
//	 webClient.getOptions().setRedirectEnabled(true);
//	 webClient.getOptions().setThrowExceptionOnScriptError(false);
//	 webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
//	 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//	 WebRequest webRequest=new  WebRequest(new java.net.URL("https://login.taobao.com/member/login.jhtml"));
//	 webClient.addRequestHeader("accept-Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//	 webRequest.setAdditionalHeader("accept-Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");		
//	 List<NameValuePair> list=new ArrayList<NameValuePair>();
//	 list.add(new NameValuePair("TPL_password", userPassword));
//	 list.add(new NameValuePair("TPL_username", userName));
//	 list.add(new NameValuePair("newlogin", "1"));
//	 list.add(new NameValuePair("callback", "1"));
//	 webRequest.setHttpMethod(HttpMethod.POST);
//	 webRequest.setRequestParameters(list);
//	 HtmlPage page= webClient.getPage(webRequest);
//	 String token=page.asXml().substring(page.asXml().indexOf("token")).split("&")[0].toString().replaceAll("token=", "");
//		     HtmlPage pages= webClient.getPage("https://passport.alipay.com/mini_apply_st.js?site=0&token="+token+"&callback=vstCallback65");
//		      String[] sts=pages.asText().split(";");
//		      String  sta=sts[2].replace("vstCallback65(", "").replace(")", "");
//		 	 JSONObject jsonObjects=JSONObject.fromObject(sta);
//		 	  String st=JSONObject.fromObject(jsonObjects.getString("data")).getString("st");
//		 	  HtmlPage pageinfo= webClient.getPage("https://login.taobao.com/member/vst.htm?st="+st+"&params=style%3Dminisimple%26sub%3Dtrue%26TPL_username%3D"+userName+"%26loginsite%3D0%26from_encoding%3D%26not_duplite_str%3D%26guf%3D%26full_redirect%3D%26isIgnore%3D%26need_sign%3D%26sign%3D%26from%3Ddatacube%26TPL_redirect_url%3Dhttp%25253A%25252F%25252Fmofang.taobao.com%25252Fs%25252Flogin%26css_style%3D%26allp%3D&_ksTS=1404787873165_78&callback=jsonp79");
//		 	  HtmlPage page2= webClient.getPage("https://member1.taobao.com/member/fresh/deliver_address.htm?spm=a1z08.2.a210b.8.6ec3169evDTXbB");
//		 	  HtmlTable table=(HtmlTable) page2.querySelector(".tbl-main");
//		 	  String dizhi=table.asText();
//		 	  Set<Cookie> cookies = webClient.getCookieManager().getCookies();
//		 	 UnexpectedPage pagepay= webClient.getPage("https://i.taobao.com/my_taobao_api/alipay_blance.json?_ksTS=1501638125567_716");//打开支付宝
//		 	 String payinfo =pagepay.getWebResponse().getContentAsString(); //balance 支付宝余额      0.8余额宝    107 累计收益
//		 	 JSONObject jsonObject2=JSONObject.fromObject(payinfo); 
//		 	  System.out.println("开始进入支付宝");
//		 	  WebRequest requests=new WebRequest(new URL("https://authet15.alipay.com/login/certCheck.htm"));
//		 	  List<NameValuePair> lists=new ArrayList<NameValuePair>();
//		 	  lists.add(new NameValuePair("goto","https://my.alipay.com/portal/i.htm?src=yy_content_jygl&sign_from=3000&sign_account_no=20881124651440950156&src=yy_content_jygl"));
//		 	  lists.add(new NameValuePair("tti","2119"));
//		 	  lists.add(new NameValuePair("isIframe","false"));
//		 	  lists.add(new NameValuePair("REMOTE_PCID_NAME","_seaside_gogo_pcid"));
//		 	  lists.add(new NameValuePair("is_sign","Y"));
//		 	  lists.add(new NameValuePair("security_activeX_enabled","false"));
//		 	  lists.add(new NameValuePair("securityId","web|cert_check|5c7e8f11-ad18-44f0-8187-db1f35c0b835RZ25"));
//		 	  requests.setHttpMethod(HttpMethod.POST);
//		 	  requests.setRequestParameters(lists);
//		 	  HtmlPage pageinfos= webClient.getPage(requests);
//		 	  HtmlPage pageinfoss= webClient.getPage("https://my.alipay.com/portal/i.htm?src=yy_content_jygl&sign_from=3000&sign_account_no=20881124651440950156&src=yy_content_jygl");
//		 	  HtmlDivision division=(HtmlDivision) pageinfoss.querySelector(".i-content");//花呗额度
//		 	 map.put("dizhi",dizhi);
//		     map.put("yue", JSONObject.fromObject(jsonObject2.get("data")).getString("balance"));
//		     map.put("shouyi", JSONObject.fromObject(jsonObject2.get("data")).getString("totalProfit"));
//		  	 map.put("yeb", JSONObject.fromObject(jsonObject2.get("data")).getString("totalQuotient"));
//		 	 map.put("zfb", division.asXml());
//		 	 map.put("error", "成功");
//	
//		
//
//	
// 		
//   	
//   		
//   	return map;
//   		
//   	}

	@ResponseBody
	@RequestMapping(value="pay",method=RequestMethod.POST)
   	public Map<String,Object> pay(HttpServletRequest request,@RequestParam("userName") String userName,@RequestParam("userPassword") String userPassword,@RequestParam("userCard") String userCard) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

		
   		return mobileService.Taobao(request, userName, userPassword,userCard);

   	}
   	


	


   	
   	
}
