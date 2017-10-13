package com.reptile.service;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jdk.nashorn.api.scripting.JSObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.util.JSONPObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Tree;
import org.htmlparser.tags.Html;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.google.gson.JsonObject;
import com.reptile.model.MobileBean;
import com.reptile.model.TelecomBean;
import com.reptile.model.UnicomBean;
import com.reptile.springboot.Scheduler;
import com.reptile.util.CrawlerUtil;
import com.reptile.util.GetMonth;
import com.reptile.util.Poi;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.SHCode;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;
import com.reptile.util.htmlUtil;

@SuppressWarnings("deprecation")
@Service("mobileService")
public class MobileService {

	@Autowired
	private application application;
	private PushState PushState;
 	public Map<String,Object> mapWeb=new HashMap<String,Object>();
	private Logger logger = Logger.getLogger(MobileService.class);
	private static CrawlerUtil crawlerUtil = new CrawlerUtil();
	private static htmlUtil htmlUtil = new htmlUtil();
	final List collectedAlerts = new ArrayList();

	MobileBean mobileBean=new MobileBean();
	Resttemplate resttemplate=new Resttemplate();
	
	

	/**
	 * 获取验证码
	 * 初始化
	 * @param mobileBean
	 * @param request
	 * @param response
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void TelecomLogin(MobileBean mobileBean, HttpServletRequest request, HttpServletResponse response)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HttpSession session = request.getSession();	
		WebClient webClient =  new WebClient(BrowserVersion.CHROME);
		UnexpectedPage CodePage = webClient.getPage(mobileBean.GetCodeUrl);
		BufferedImage ioim=ImageIO.read(CodePage.getInputStream());
		session.setAttribute("WebClient", webClient);
		//map.put("WebClients", webClient);
		ImageIO.write(ioim,"png",response.getOutputStream());
		
	}
	
	public Map Login(MobileBean mobileBean, HttpServletRequest request, HttpServletResponse response,String usertype)
			throws IOException, InterruptedException {
		Map<String, Object> map = new HashMap<String, Object>();
		CrawlerUtil craw=new CrawlerUtil();
		HttpSession session = request.getSession();
		WebClient webClient = (WebClient) session.getAttribute("WebClient");
		session.setAttribute("iphone",mobileBean.getUserIphone());
		WebRequest requests=new WebRequest(new URL(mobileBean.loginurl));
	    List<NameValuePair> reqParam = new ArrayList<NameValuePair>();  
	    reqParam.add(new NameValuePair("userName",mobileBean.getUserIphone()));
	    reqParam.add(new NameValuePair("password", mobileBean.getUserPassword()));
	    reqParam.add(new NameValuePair("verifyCode",mobileBean.getUserCode()));
	    reqParam.add(new NameValuePair("OrCookies", "1"));
	    reqParam.add(new NameValuePair("loginType", "1"));
	    reqParam.add(new NameValuePair("fromUrl", "uiue/login_max.jsp"));
	    reqParam.add(new NameValuePair("toUrl", "http://www.sn.10086.cn/my/account/"));
	    requests.setRequestParameters(reqParam);
	    HtmlPage  pages=	webClient.getPage(requests);
	
	    try {
	        if(pages.getElementById("message").asText()==null||"".equals(pages.getElementById("message").asText())){
		    	 map.put("msg","0000");
		     }else{
		    	 map.put("msg",pages.getElementById("message").asText());
		     }
		} catch (Exception e) {

	    	 map.put("msg","0000");
		}
	 
	    session.setAttribute("webClient",webClient);
		return map;

	}
	
	
	private WebRequest WebRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String,Object>> QueryInfo(HttpServletRequest request, HttpServletResponse response, MobileBean mobileBean)
			throws Exception {
		Poi poi=new Poi();
		// boolean flg = true; // 是否需要重新设置
		HttpSession session = request.getSession();
		String iphone=(String) session.getAttribute("iphone");
	//	HtmlPage infopage = (HtmlPage) session.getAttribute("SetcodePage");// 获得新的page对象
		WebClient webClients = (WebClient) request.getSession().getAttribute("webClient");	
		//根据此次认证，可直接查询详单
		List<Map> list = new htmlUtil().liantong();
		XmlPage  page=webClients.getPage(mobileBean.pingzhengurl("201703"));//越过凭证不用发短信
		for (int j = 2; j >=0; j--) {
		page=webClients.getPage(mobileBean.pingzhengurl(list.get(j).get("begin").toString().replace("-","").substring(0,list.get(j).get("begin").toString().replace("-","").length()-2)));//越过凭证不用发短信
		Page unpag=webClients.getPage(mobileBean.downloadUrl);
//		saveFile(unpag, "/Users/hongzheng/"+iphone+"num"+j+".xls");/苹果系统处理
		saveFile(unpag, "D:/"+iphone+"num"+j+".xls");//windows处理
		}
		List<Map<String,Object>> lists=new ArrayList<Map<String,Object>>();
		
		
		for (int i = 0; i < 3; i++) {
			String filePath = "D:\\"+iphone+"num"+i+".xls";
	        File file = new File(filePath);
	       List<Map<String,Object>> listmap= poi.getvalues(file);
	      lists.addAll(listmap);
		}
		   
	       
		 webClients.getPage("https://sn.ac.10086.cn/logout");
		return lists;

	//	 webClients.close();
		
			
			
		

	}
	
	   public static void saveFile(Page page, String file) throws IOException  {
	        InputStream is = page.getWebResponse().getContentAsStream();
	        FileOutputStream output = new FileOutputStream(file);
	        IOUtils.copy(is, output);
	        output.close();
	    }
	   /**
	    * 更新验证码
	    * @param request
	    * @param response
	    * @throws IOException
	    */
	 public void UpdateCodeImg(HttpServletRequest request,HttpServletResponse response) throws IOException{
		    PrintWriter printWriter = response.getWriter();
		 	WebClient webClients=(WebClient) request.getSession().getAttribute("WebClient");
		 	UnexpectedPage page= webClients.getPage(new MobileBean().GetCodeUrl);
		 	BufferedImage io=ImageIO.read(page.getInputStream());
			String  fileName = System.currentTimeMillis() + ".png";
		 	File path = new File(request.getSession().getServletContext().getRealPath("/upload") + "/"); // 此目录保存缩小后的关键图
		 	if (!path.isDirectory()){
				path.mkdirs();
		 	}
			ImageIO.write(io,"png",new File(path,fileName));
			  printWriter.write("upload/" + fileName);
			  request.getSession().setAttribute("webClients",webClients);
	 }
	 public void UpdatePhoneCode(HttpServletRequest request,HttpServletResponse response) throws InterruptedException{
		 HttpSession session=request.getSession();
		 WebClient webClient= (WebClient) session.getAttribute("webClient");
		 HtmlPage TwoPhoneCodePage=(HtmlPage) session.getAttribute("SetcodePage");
		 HtmlPage page= (HtmlPage) TwoPhoneCodePage.executeJavaScript("$('#smsCodeSpan').click();").getNewPage();
		 Thread.sleep(2000);
		 if(page.asText().contains("秒后再次获取")){
			 System.out.println("再次发送成功");
		 }else{
			 System.out.println("网络异常，稍后再试");
		 }
		 
		 
	 }
	 public void UpdatePwdCodeImg(HttpServletRequest request,HttpServletResponse response,MobileBean mobileBean) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		 //设置无界面浏览器
		WebClient webClient= crawlerUtil.setWebClient();
		HtmlPage page= webClient.getPage(mobileBean.UpdatePwdImg);
		HttpSession session=request.getSession();
		session.setAttribute("webClient", webClient);
		HtmlImage htmlImage= (HtmlImage) page.getElementById("pic");
	    ImageReader imageReader=htmlImage.getImageReader();
	    BufferedImage bufferedImage= imageReader.read(0);
	    ImageIO.write(bufferedImage, "png", response.getOutputStream());

	 }
	 
	 public void UpdateCodeImages(HttpServletRequest request,HttpServletResponse response,MobileBean mobileBean) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		 PrintWriter printWriter=response.getWriter();
		 //设置无界面浏览器
		 WebClient webClient= crawlerUtil.setWebClient();
		 HtmlPage page= webClient.getPage(mobileBean.UpdatePwdImg);
		 HttpSession session=request.getSession();
		 session.setAttribute("webClient", webClient);
		 HtmlImage htmlImage= (HtmlImage) page.getElementById("pic");
		 ImageReader imageReader=htmlImage.getImageReader();
		 BufferedImage bufferedImage= imageReader.read(0);		 
			String  fileName = System.currentTimeMillis()+"hs" + ".png";
		 	File path = new File(request.getSession().getServletContext().getRealPath("/upload") + "/"); // 此目录保存缩小后的关键图
		 	if (!path.isDirectory()){
				path.mkdirs();
		 	}
			ImageIO.write(bufferedImage,"png",new File(path,fileName));
			  printWriter.write("upload/" + fileName);
			  request.getSession().setAttribute("webClient",webClient);
		 
	 }
	 public Map<String,Object> UpdatePwdInfo(HttpServletRequest request,HttpServletResponse response,MobileBean mobileBean,String iphone,String Password) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		Map<String,Object>map=new HashMap<String,Object>();
		HttpSession session=request.getSession();
        WebClient webClient=(WebClient) session.getAttribute("webClient");//获取初始化webClient
        WebRequest webRequest=new WebRequest(new URL(mobileBean.UpdatePwdAttestationUrl));
        List<NameValuePair> list=new ArrayList<NameValuePair>();
        list.add(new NameValuePair("service","direct/1/personalinfo.ResetPwdOperation/$Form"));
		list.add(new NameValuePair("sp","S0"));
		list.add(new NameValuePair("Form0","phone_TEMP,PSPT_ID_TEMP"));
		list.add(new NameValuePair("phone",mobileBean.getUserIphone()));//密文
		list.add(new NameValuePair("PSPT_ID",mobileBean.getUserPassword()));
		list.add(new NameValuePair("protocl", "on"));
		list.add(new NameValuePair("phone_TEMP",iphone));//明文
		list.add(new NameValuePair("PSPT_ID_TEMP",Password));
		list.add(new NameValuePair("RSET_YZM",mobileBean.getUserCode()));
		 webRequest.setHttpMethod(HttpMethod.POST);
		 webRequest.setRequestParameters(list);
		 HtmlPage pages= webClient.getPage(webRequest);
		HtmlDivision div= (HtmlDivision) pages.querySelector(".con");	 
		if(div.asText().contains("您的服务密码已重置成功")){
			map.put("msg", "success");
			session.setAttribute("webClient", webClient);
		}else{
			map.put("msg",div.asText());
	
		}
        
	 	return map;
	 }
	 public void ChangePassword(HttpServletRequest request,HttpServletResponse response,MobileBean mobileBean,String Password,String callcode) throws FailingHttpStatusCodeException, IOException, InterruptedException{
     HttpSession session=request.getSession();
     WebClient webClient= (WebClient) session.getAttribute("webClient");
	 WebRequest webRequests=new WebRequest(new URL(mobileBean.UpdatePwd));
	 List<NameValuePair> Lists=new ArrayList<NameValuePair>();
	 Lists.add(new NameValuePair("service","direct/1/personalinfo.ResetPwdOperation/$Form$0"));
	 Lists.add(new NameValuePair("sp","S0"));
	 Lists.add(new NameValuePair("OLD_USER_PASSWD",mobileBean.getCallCode()));//加密后的短信验证码
	 Lists.add(new NameValuePair("X_NEW_PASSWD",mobileBean.getUserPassword()));//加密后的密码
	 Lists.add(new NameValuePair("RE_NEW_PASSWD",mobileBean.getUserPassword()));
	 
	 Lists.add(new NameValuePair("OLD_USER_PASSWD_TEMP",callcode));//铭文 验证码
	 Lists.add(new NameValuePair("X_NEW_PASSWD_TEMP",Password));//铭文密码
	 Lists.add(new NameValuePair("RE_NEW_PASSWD_TEMP",Password));
	 webRequests.setHttpMethod(HttpMethod.POST);
	 webRequests.setRequestParameters(Lists);
	 HtmlPage pagess= webClient.getPage(webRequests);
	 Thread.sleep(3000);
	 System.out.println(pagess.asText());
	
	
	 }
    public Map<String,Object> RsaPassword(String password){
    	
    	Map<String,Object> map=new HashMap<String, Object>();
    	Map<String,Object> data=new HashMap<String, Object>();
		try {

			WebClient webClient =  new WebClient(BrowserVersion.CHROME);
			HtmlPage page=webClient.getPage("http://"+application.getIp()+":"+application.getPort()+"/interface/Rsa.do");
			HtmlInput htmlInput= (HtmlInput) page.getElementById("rsaName");
			String UserPasswords=password;
			htmlInput.setValueAttribute(UserPasswords);
			HtmlPage pages= (HtmlPage) page.executeJavaScript("rsafunction();").getNewPage();
			HtmlDivision div= (HtmlDivision) pages.getElementById("rsa");
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
    public Map<String,Object> UnicomAes(String password){
    	
    	Map<String,Object> map=new HashMap<String, Object>();
    	Map<String,Object> data=new HashMap<String, Object>();
    	try {
    		
    		WebClient webClient =  new WebClient(BrowserVersion.CHROME);
    		HtmlPage page=webClient.getPage("http://"+application.getIp()+":"+application.getPort()+"/interface/UnicomAesPage.do");
    		HtmlInput htmlInput= (HtmlInput) page.getElementById("rsaPwd");
    		String UserPasswords=password;
    		htmlInput.setValueAttribute(UserPasswords);
    		HtmlPage pages= (HtmlPage) page.executeJavaScript("rsafunction();").getNewPage();
    		HtmlDivision div= (HtmlDivision) pages.getElementById("rsas");
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
	 * @param mobileBean
	 * @param request
	 * @param response
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public Map<String,Object>TelecomLogins(HttpServletRequest request, HttpServletResponse response)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		Map<String,Object> map=new HashMap<String,Object>();
		Map<String,Object> maps=new HashMap<String,Object>();
		try {
		HttpSession session = request.getSession();
		WebClient webClient = crawlerUtil.WebClientNices();
		UnexpectedPage CodePage = webClient.getPage(mobileBean.GetCodeUrl);
		BufferedImage ioim=ImageIO.read(CodePage.getInputStream());
		session.setAttribute("WebClient", webClient);
		File path = new File(request.getSession().getServletContext().getRealPath("/uploads") + "/"); // 此目录保存缩小后的关键图
		//如果文件夹不存在则创建    
		if  (!path .exists()  && !path .isDirectory())      
		{       
		    System.out.println("//不存在");  
		    path .mkdir();    
		}
		String  fileName = crawlerUtil.getUUID() +"Interface"+ ".png";
		System.out.println(fileName);
		ImageIO.write(ioim,"png",new File(path,fileName));
	//InetAddress.getLocalHost().getHostAddress()
		maps.put("ip",application.getIp());
		maps.put("FileName",fileName);
		maps.put("FilePath","/uploads");
		maps.put("Port",application.getPort());
		map.put("data",maps);
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
	 * @param mobileBean
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	
	public Map Login(MobileBean mobileBean, HttpServletRequest request, HttpServletResponse response)
			throws IOException, InterruptedException {
		Poi poi=new Poi();
		Map<String,Object> info=new HashMap<String,Object>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		CrawlerUtil craw=new CrawlerUtil();
		HttpSession session = request.getSession();
		WebClient webClient = (WebClient) session.getAttribute("WebClient");
		System.out.println(session.getAttribute("WebClient"));
	
		session.setAttribute("iphone",mobileBean.getUserIphone());
		WebRequest requests=new WebRequest(new URL(mobileBean.loginurl));
	    List<NameValuePair> reqParam = new ArrayList<NameValuePair>();  
	    reqParam.add(new NameValuePair("userName",mobileBean.getUserIphone()));
	    reqParam.add(new NameValuePair("password", mobileBean.getUserPassword()));
	    reqParam.add(new NameValuePair("verifyCode",mobileBean.getUserCode()));
	    reqParam.add(new NameValuePair("OrCookies", "1"));
	    reqParam.add(new NameValuePair("loginType", "1"));
	    reqParam.add(new NameValuePair("fromUrl", "uiue/login_max.jsp"));
	    reqParam.add(new NameValuePair("toUrl", "http://www.sn.10086.cn/my/account/"));
	    requests.setRequestParameters(reqParam);
	    HtmlPage  pages=	webClient.getPage(requests);

	    	 if(pages.getElementById("message")==null||"".equals(pages.getElementById("message").asText())){
			    	
		        	//登录成功 查询数据
	    		 
	    		//根据此次认证，可直接查询详单
	    			List<Map> list = new htmlUtil().liantong();
	    			XmlPage  page=webClient.getPage(mobileBean.pingzhengurl("201703"));//越过凭证不用发短信
	    			for (int j = 2; j >=0; j--) {
	    			page=webClient.getPage(mobileBean.pingzhengurl(list.get(j).get("begin").toString().replace("-","").substring(0,list.get(j).get("begin").toString().replace("-","").length()-2)));//越过凭证不用发短信
	    			Page unpag=webClient.getPage(mobileBean.downloadUrl);
//	    			saveFile(unpag, "/Users/hongzheng/"+iphone+"num"+j+".xls");/苹果系统处理
	    			saveFile(unpag, "D:/"+mobileBean.getUserIphone()+"num"+j+".xls");//windows处理
	    			}
	    			List<Map<String,Object>> lists=new ArrayList<Map<String,Object>>();
	    			
	    			
	    			for (int i = 0; i < 3; i++) {
	    				String filePath = "D:\\"+mobileBean.getUserIphone()+"num"+i+".xls";
	    		        File file = new File(filePath);
	    		       List<Map<String,Object>> listmap= poi.getvalues(file);
	    		      lists.addAll(listmap);
	    			}
	    			   
//	    		       try {
//					
//		    			webClient.getPage("https://sn.ac.10086.cn/logout");
//					} catch (Exception e) {
//					System.out.println("退出异常");
//					}
	    		
		      
			    	map.put("data",lists);
			    	info.put("UserIphone",mobileBean.getUserIphone());
			    	info.put("UserPassword",mobileBean.getUserPassword());
			    	map.put("accountMessage",info);
			    	//推送数据
			    	map=resttemplate.SendMessage(map, 	application.getSendip()+"/HSDC/authcode/callRecord");
			    	
			    	
			    
			  
			     }else{
			    	 map.put("errorInfo",pages.getElementById("message").asText());
			       	 map.put("errorCode","0001");
			     }
	
	       
	
	    	 System.out.println(map.toString());
	    	 session.setAttribute("webClient",webClient);
		return map;

	}

	   /** 接口
	    * 更新验证码
	    * @param request
	    * @param response
	 * @return 
	    * @throws IOException
	    */
	 public Map<String, Object> UpdateCodeImgs(HttpServletRequest request,HttpServletResponse response) throws IOException{
		 	Map<String,Object> map=new HashMap<String,Object>();
			Map<String,Object> data=new HashMap<String,Object>(); 	
		 try { 
	
		 	WebClient webClients=(WebClient) request.getSession().getAttribute("WebClient");
		 	UnexpectedPage page= webClients.getPage(new MobileBean().GetCodeUrl);
		 	BufferedImage io=ImageIO.read(page.getInputStream());
			String  fileName = System.currentTimeMillis() + "UpdateCode"+ ".png";
		 	File path = new File(request.getSession().getServletContext().getRealPath("/upload") + "/"); // 此目录保存缩小后的关键图
		 	if (!path.isDirectory()){
				path.mkdirs();
		 	}
		 	
			  ImageIO.write(io,"png",new File(path,fileName));
	
			  request.getSession().setAttribute("WebClient",webClients);
		
				System.out.println(fileName);
				ImageIO.write(io,"png",new File(path,fileName));
			
				data.put("ip",application.getIp());
				data.put("FileName",fileName);
				data.put("FilePath","/upload");
				data.put("Port",application.getPort());
				map.put("data",data);
				map.put("errorCode", "0000");
				map.put("errorInfo", "查询成功");
			
			} catch (Exception e) {
				map.put("errorCode", "0001");
				map.put("errorInfo", "网络错误");
			}
		return map;
			
			  
	 }
	 /** 接口
	  * 更新验证码
	  * @param request
	  * @param response
	  * @return 
	  * @throws IOException
	  */
	 public Map<String, Object> MobileBelong(HttpServletRequest request,HttpServletResponse response,String phone) throws IOException{
		 	Map<String,Object> map=new HashMap<String,Object>();
			Map<String,Object> data=new HashMap<String,Object>(); 
		 try {
				CrawlerUtil craw=new CrawlerUtil();
				WebClient webClient = craw.setWebClient();
				HtmlPage page= webClient.getPage("http://www.ip138.com:8080/search.asp?mobile="+phone+"&action=mobile");
				System.out.println(		page.querySelectorAll(".tdc2").get(2).asText());
				data.put("MobileBelong",page.querySelectorAll(".tdc2").get(2).asText());
				map.put("data",data);
				map.put("errorCode", "0000");
				map.put("errorInfo", "查询成功");
			} catch (Exception e) {
				map.put("errorCode", "0001");
				map.put("errorInfo", "网络错误");
			}
			
	
			return map;
		 
	 }
	 
	 /** 接口
	    * 更新验证码  联通
	    * @param request
	    * @param response
	    * @return 
	    * @throws IOException
	    */
	 public Map<String, Object> UnicomUpdateCode(HttpServletRequest request,HttpServletResponse response,UnicomBean unicombean) throws IOException{
		 	Map<String,Object> map=new HashMap<String,Object>();
			Map<String,Object> data=new HashMap<String,Object>(); 	
		 try { 
	
		 	WebClient webClients=(WebClient) request.getSession().getAttribute("webClient");
		 	UnexpectedPage page= webClients.getPage("http://uac.10010.com/portal/Service/CreateImage");
		 	BufferedImage io=ImageIO.read(page.getInputStream());
			String  fileName = System.currentTimeMillis() + "UpdateCodeUnicom"+ ".png";
		 	File path = new File(request.getSession().getServletContext().getRealPath("/upload") + "/"); // 此目录保存缩小后的关键图
		 	if (!path.isDirectory()){
				path.mkdirs();
		 	}
		 	
			ImageIO.write(io,"png",new File(path,fileName));
	
			  request.getSession().setAttribute("webClient",webClients);
		
				System.out.println(fileName);
				ImageIO.write(io,"png",new File(path,fileName));
			
				data.put("ip",application.getIp());
				data.put("FileName",fileName);
				data.put("FilePath","/upload");
				data.put("Port",application.getPort());
				map.put("data",data);
				map.put("errorCode", "0000");
				map.put("errorInfo", "查询成功");
			
			} catch (Exception e) {
				map.put("errorCode", "0001");
				map.put("errorInfo", "网络错误");
			}
		return map;
			
			  
	 }
	 
//	 /** 接口
//	  * 获取验证码 联通
//	  * @param request
//	  * @param response
//	  * @return 
//	  * @throws IOException
//	  */
//	 public Map<String, Object> GetCode(HttpServletRequest request,HttpServletResponse response,UnicomBean unicombean) throws IOException{
//		 	Map<String,Object> map=new HashMap<String,Object>();
//			Map<String,Object> data=new HashMap<String,Object>(); 
//		try {
//			HttpSession session=request.getSession();
//			CrawlerUtil crawlerUtil = new CrawlerUtil();
//			WebClient webClient = crawlerUtil.setWebClient();
//			UnexpectedPage  page= webClient.getPage(unicombean.newCodeUrl);
//			session.setAttribute("webClient",webClient);
//		    BufferedImage bi= ImageIO.read(page.getInputStream());
//			String  fileName = System.currentTimeMillis() + "UpdateCode"+ ".png";
//		 	File path = new File(request.getSession().getServletContext().getRealPath("/upload") + "/"); // 此目录保存缩小后的关键图
//		 	if (!path.isDirectory()){
//				path.mkdirs();
//		 	}
//		 	
//			ImageIO.write(bi,"png",new File(path,fileName));
//			
//			
//			
//			data.put("ip",application.getIp());
//			data.put("FileName",fileName);
//			data.put("FilePath","/upload");
//			data.put("Port",application.getPort());
//			map.put("data",data);
//			map.put("errorCode", "0000");
//			map.put("errorInfo", "查询成功");
//		
//		} catch (Exception e) {
//			map.put("errorCode", "0002");
//			map.put("errorInfo", "网络错误");
//		}
//	
//			return map;
//		 
//	 }
//	 
//	 
 
	 /** 接口
	  * 获取登陆验证码 联通
	  * @param request
	  * @param response
	  * @return 
	  * @throws IOException
	 * @throws InterruptedException 
	  */
	 public Map<String, Object> GetCode(HttpServletRequest request,HttpServletResponse response,String Useriphone) {
		 Map<String,Object> map=new HashMap<String,Object>();
	     HttpSession session=request.getSession();
	        WebClient webClient = new WebClientFactory().getWebClient();
			try {
				HtmlPage loginPage=	webClient.getPage("https://uac.10010.com/portal/homeLogin");
			
			Thread.sleep(500);
	        String url="https://uac.10010.com/portal/Service/CheckNeedVerify?callback=jQuery17209863190566662376_"+System.currentTimeMillis()+"&userName="+Useriphone+"&pwdType=01&_="+System.currentTimeMillis();
	        UnexpectedPage page = webClient.getPage(url);
	        Thread.sleep(500);
	       //System.out.println(page.getWebResponse().getContentAsString());
	       String resultInfo=page.getWebResponse().getContentAsString();
	       String tips=	resultInfo.split("\\(")[1].split("\\)")[0];
	       JSONObject jsons=JSONObject.fromObject(tips);	
 		   String tipInfo=jsons.get("resultCode").toString();	
	        if(tipInfo.equals("true")){
	    	   System.out.println("需要图形验证码");
	    	   session.setAttribute("isTrue", "true");
	        }else{
	        	 session.setAttribute("isTrue", "false");
	        	System.out.println("不需要 图形验证码");
	        }      	
	        String url2="https://uac.10010.com/portal/Service/SendCkMSG?callback=jQuery17209863190566662376_"+System.currentTimeMillis()+"&req_time="+System.currentTimeMillis()+"&mobile="+Useriphone+"&_="+System.currentTimeMillis();
	        HtmlPage page1 = webClient.getPage(url2);
	        Thread.sleep(500);
	        String result=page1.asText();
	        if(result.contains("0000")){
				map.put("errorCode", "0000");
				map.put("errorInfo", "验证码发送成功");
				session.setAttribute("webClientone", webClient);
				
			}else{
				JSONObject json=JSONObject.fromObject(result.split("\\(")[1].split("\\)")[0]);
				String resultCode=json.get("resultCode").toString();
				if(resultCode.equals("7098")){
					map.put("errorCode", "0001");
					map.put("errorInfo","随机码发送次数已达上限，请明日再试！");
				}else{
					map.put("errorCode", "0001");
					map.put("errorInfo","距离上次发送不足1分钟");	
				}
						
		    }	
			} catch (Exception e) {
				map.put("errorCode", "0001");
				map.put("errorInfo", "网络异常");
				e.printStackTrace();
			}
	   return map; 
	 }
	 
	/**
	 * 
	 * 登陆
	 *  
	 *  
	 */
 
	 public Map<String,Object> UnicomLogin(HttpServletRequest request,String Useriphone,String password,
		String code) {
        Map<String,Object> map=new HashMap<String,Object>();
    try {
		HttpSession session = request.getSession();
		WebClient webClient=(WebClient) session.getAttribute("webClientone");
		if(webClient==null){
			map.put("errorCode", "0001");
		    map.put("errorInfo", "请先获取短信验证码！");
			return map;
		}	
	  String isTrue=(String) session.getAttribute("isTrue");
	  WebRequest request1=null;
	  if(isTrue.equals("true")){
		//===========需要图形验证码===========================
		//1.读取页面验证码图片到本地
		    String imageUrl="https://uac.10010.com/portal/Service/CreateImage?t="+System.currentTimeMillis();//动态码url
		    UnexpectedPage Imagepage=  webClient.getPage(imageUrl);
		    BufferedImage bufferedImage  = ImageIO.read(Imagepage.getInputStream());
			String findImage="gd"+System.currentTimeMillis()+".png";
			ImageIO.write(bufferedImage , "png", new File("C:\\Shimage",findImage)); 
	        //2.转码
			Map<String,Object> aa= SHCode.getCode("C:\\Shimage\\"+findImage);
	        String catpy=  (String) aa.get("strResult");//转码后的动态码
	        //System.out.println(catpy+"-***-*-");	  
	        WebRequest webRequest3=new WebRequest(new URL("https://uac.10010.com/portal/Service/CtaIdyChk?callback=jQuery17207654655044488388_"+System.currentTimeMillis()+"&verifyCode="+catpy+"&verifyType=1&_="+System.currentTimeMillis()));
		    webRequest3.setHttpMethod(HttpMethod.GET);
		    webRequest3.setAdditionalHeader("Referer", "https://uac.10010.com/portal/homeLogin");
		    HtmlPage page2 = webClient.getPage(webRequest3);
		       String resultInfo=page2.getWebResponse().getContentAsString();
		       String tips=	resultInfo.split("\\(")[1].split("\\)")[0];
		       JSONObject jsons=JSONObject.fromObject(tips);	
	 		   String tipInfo=jsons.get("resultCode").toString();
	 		  if(tipInfo.equals("true")){//图形验证码正确可以发包登陆
		    	   System.out.println("图形验证码正确");
		    	   Set<Cookie> cookies = webClient.getCookieManager().getCookies();
				    String uvc="";
				    for (Cookie c : cookies) {  
		
				   	   if (c.getName().equals("uacverifykey")) {
						   uvc= c.getValue();
						  }
				   	   webClient.getCookieManager().addCookie(c);
				  
				    }  
				    request1=new WebRequest(new URL("https://uac.10010.com/portal/Service/MallLogin?callback=jQuery17207654655044488388_"+System.currentTimeMillis()+"&req_time="+System.currentTimeMillis()+"&redirectURL=http://www.10010.com&userName="+Useriphone+"&password="+password+"&pwdType=01&productType=01&verifyCode="+catpy+"&uvc="+uvc+"&redirectType=01&rememberMe=1&verifyCKCode="+code+"&_="+System.currentTimeMillis()));
					
		        }else{
		        	
		        	System.out.println("图形验证码 错误");
		        	map.put("errorCode", "0001");
					map.put("errorInfo","图形验证码 错误");
					return map;
		        }
	      }else{			
	//===========不需要图形验证码===========================		
		request1=new WebRequest(new URL("https://uac.10010.com/portal/Service/MallLogin?callback=jQuery17209863190566662376_"+System.currentTimeMillis()+"&req_time="+System.currentTimeMillis()+"&redirectURL=http://www.10010.com&userName="+Useriphone+"&password="+password+"&pwdType=01&productType=01&redirectType=01&rememberMe=1&verifyCKCode="+code+"&_="+System.currentTimeMillis()));
		
	     }
	    request1.setHttpMethod(HttpMethod.GET);
		request1.setAdditionalHeader("Referer", "https://uac.10010.com/portal/homeLogin");
		HtmlPage page2 = webClient.getPage(request1);
		Thread.sleep(500);
        String tip=  page2.asText();
        String tips=tip.split("\\(")[1].split("\\)")[0];
        System.out.println(tips);
	     JSONObject json=JSONObject.fromObject(tips);	
 		String tipInfo=json.get("resultCode").toString();	
    	if(tipInfo.equals("0000")){
    		map.put("errorCode", "0000");
			map.put("errorInfo", "登陆成功！");
			session.setAttribute("webClientSucce", webClient);
    	}else{	
    		System.out.println(json.get("msg").toString());
    		map.put("errorCode", "0001");
    		if(json.get("msg").toString().contains("codefail")){
    			map.put("errorInfo", "验证码错误");	
    		}else{
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
 * @param request
 * @param response
 * @param Useriphone
 * @return
 * @throws IOException
 * @throws InterruptedException
 */
public Map<String, Object> GetCodeTwo(HttpServletRequest request) {
	
		 Map<String,Object> map=new HashMap<String,Object>();
		HttpSession session = request.getSession();
		WebClient webClient=(WebClient) session.getAttribute("webClientSucce");
		System.out.println("登陆成功");
		//打开获取详单页面
		try {
			Thread.sleep(1000);
		HtmlPage detailPage=webClient.getPage("http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001");
		
		Thread.sleep(8000);
		//System.out.println(detailPage.asXml());  	
       //============登陆信息校验===========================  
		webClient.addRequestHeader("Accept","application/json, text/javascript, */*; q=0.01");
	    webClient.addRequestHeader("Accept-Encoding","gzip, deflate");
	    webClient.addRequestHeader("Accept-Language","zh-CN,zh;q=0.8");
	    webClient.addRequestHeader("Connection","keep-alive");
	    webClient.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
	    webClient.addRequestHeader("Referer","http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001");
	    webClient.addRequestHeader("X-Requested-With","XMLHttpRequest");
		
		 List<NameValuePair> paramer3=new ArrayList<NameValuePair>();
		 paramer3.add(new NameValuePair("_",System.currentTimeMillis()+""));
		 WebRequest webRequest3=new WebRequest(new URL("http://iservice.10010.com/e3/static/check/checklogin?_="+System.currentTimeMillis()));
		     webRequest3.setHttpMethod(HttpMethod.POST);
		     webRequest3.setRequestParameters(paramer3); 
		    TextPage nextPage= webClient.getPage(webRequest3);
		Thread.sleep(3000);
	//	System.out.println(nextPage.getContent());
       //=====================获取验证码================================================
	    List<NameValuePair> paramer=new ArrayList<NameValuePair>();
	    String time=""+System.currentTimeMillis();
	    paramer.add(new NameValuePair("_",time));
	    paramer.add(new NameValuePair("accessURL","http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001"));
	    paramer.add(new NameValuePair("menuid","000100030001"));  
	    paramer.add(new NameValuePair("menuId","000100030001"));
	    WebRequest webRequest1=new WebRequest(new URL("http://iservice.10010.com/e3/static/query/sendRandomCode?_="+time+"&accessURL=http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001&menuid=000100030001"));
	    webRequest1.setHttpMethod(HttpMethod.POST);
	    webRequest1.setRequestParameters(paramer); 
	   //==============暂时修改===================
	    if(webClient.getPage(webRequest1).isHtmlPage()){
	    	
	    	map.put("errorCode", "0001");
	    	map.put("errorInfo", "验证码发送失败");
	    	return map;
	    }else{
	        TextPage next= webClient.getPage(webRequest1);	
	     //System.out.println(next.getContent());
		    JSONObject json1=JSONObject.fromObject(next.getContent());	
			//String tips1=json1.get("issuccess").toString();
			String tips2=json1.get("sendcode").toString();
			if(tips2.equals("true")){
				System.out.println("验证码发送成功");
				map.put("errorCode", "0000");
				map.put("errorInfo", "验证发送成功");
				session.setAttribute("webClientTwo", webClient);			
			}else{
				
				map.put("errorCode", "0001");
				map.put("errorInfo", "验证码发送失败"); 
				System.out.println("验证码发送失败");
			}
	  //===========================
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
 * 联通获取详单
 * @param request
 * @param Useriphone
 * @param UserPassword
 * @param code
 * @return
 * @throws IOException
 */

public Map<String,Object> getDetial(HttpServletRequest request,String Useriphone,
		String UserPassword,
		String code){
	  Map<String,Object> map=new HashMap<String,Object>();
      
      List listsy=new ArrayList();
      String info = "";
	HttpSession session = request.getSession();
	WebClient webClient=(WebClient) session.getAttribute("webClientTwo");
	if(webClient==null){
		map.put("errorCode", "0001");
	    map.put("errorInfo", "请先获取验证码！");
		return map;
	}

	//System.out.println("验证码发送成功");
//=======================确定验证码===============================================  
  String verCode="http://iservice.10010.com/e3/static/query/verificationSubmit?_="+System.currentTimeMillis()+"&accessURL=http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001&menuid=000100030001";
 
try {
	 WebRequest webRequest2 = new WebRequest(new URL(verCode));
	
  webRequest2.setHttpMethod(HttpMethod.POST);  
  List<NameValuePair> paramer2=new ArrayList<NameValuePair>();
	    paramer2.add(new NameValuePair("_",System.currentTimeMillis()+""));
	    paramer2.add(new NameValuePair("accessURL","http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001"));
	    paramer2.add(new NameValuePair("menuid","000100030001"));  
	    paramer2.add(new NameValuePair("menuId","000100030001"));
	    paramer2.add(new NameValuePair("inputcode",code));
	  
	webRequest2.setRequestParameters(paramer2); 
	TextPage newPage= webClient.getPage(webRequest2);
	//System.out.println(newPage.getContent());
	
	JSONObject json3=JSONObject.fromObject(newPage.getContent());	
	   String resultCode=json3.get("flag").toString();
	     if(resultCode.equals("00")){
	    	 System.out.println("验证码成功，可查询");
	    	 PushState.state(Useriphone,"callLog",100);
//=======================获取详单================================================      		
	        		webClient.addRequestHeader("Accept","application/json, text/javascript, */*; q=0.01");
				    webClient.addRequestHeader("Accept-Encoding","gzip, deflate");
				    webClient.addRequestHeader("Accept-Language","zh-CN,zh;q=0.8");
				    webClient.addRequestHeader("Connection","keep-alive");
				    //webClient.addRequestHeader("Content-Length","56");
				    webClient.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
				    webClient.addRequestHeader("Host","iservice.10010.com");
				    webClient.addRequestHeader("Origin","http://iservice.10010.com");
				    webClient.addRequestHeader("Referer","http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001");
				    webClient.addRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
				    webClient.addRequestHeader("X-Requested-With","XMLHttpRequest");
				    JSONArray array=new JSONArray();
					String begin=GetMonth.nowMonth()+"01";//开始是时间
					String end=GetMonth.today();//结束时间
					int year=new Integer(begin.substring(0, 4));
					int month=new Integer(begin.substring(4,
					 6));//获取月  作为获取每个月的最后一天的参数
					String beforMonth="";//上月
					for (int i = 1; i < 7; i++) {
						Map<String,Object> dataMap=new HashMap<String,Object>();
						System.out.println(begin+"*****"+end);
						WebRequest webRequestss=new WebRequest(new URL("http://iservice.10010.com/e3/static/query/callDetail?_="+System.currentTimeMillis()+"&accessURL=http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001&menuid=000100030001"));
						List<NameValuePair> lists=new ArrayList<NameValuePair>();
						lists.add(new NameValuePair("pageNo","1"));
						lists.add(new NameValuePair("pageSize","2000"));
						lists.add(new NameValuePair("beginDate",begin));
						lists.add(new NameValuePair("endDate",end));
						webRequestss.setHttpMethod(HttpMethod.POST);
						webRequestss.setRequestParameters(lists);
						TextPage chekpages=webClient.getPage(webRequestss);
						
						System.out.println(chekpages.getContent()+"----vvv");
					
						beforMonth=GetMonth.beforMon(year,month,i);//上i月
						begin=beforMonth+"01";
						int y=new Integer(begin.substring(0, 4));
						int m=new Integer(begin.substring(4,6));
						end=GetMonth.lastDate(y, m);//一个月的最后一天
						dataMap.put("item", chekpages.getContent());
						listsy.add(dataMap);
					}	
					map.put("errorCode", "0000");
			    	map.put("errorInfo", "查询成功");
			    	
					map.put("data", listsy);
					map.put("UserIphone", Useriphone);
					map.put("UserPassword", UserPassword);
				    //map=resttemplate.SendMessage(map, "http://192.168.3.35:8080/HSDC/message/linkCallRecord");			
					map=resttemplate.SendMessage(map, application.getSendip()+"/HSDC/message/linkCallRecord");
				    if(map!=null&&"0000".equals(map.get("errorCode").toString())){
				    	    PushState.state(Useriphone, "callLog",300);
			                map.put("errorInfo","推送成功");
			                map.put("errorCode","0000");
		            }else{
			            	//--------------------数据中心推送状态----------------------
			            	PushState.state(Useriphone, "callLog",200);
			            	//---------------------数据中心推送状态----------------------
			                map.put("errorInfo","推送失败");
			                map.put("errorCode","0001");
		            }   				        
	     }else{
	    	PushState.state(Useriphone,"callLog",200);
	    	map.put("errorCode", "0001");
	    	map.put("errorInfo", json3.get("error").toString());
	    	 System.out.println(json3.get("error").toString()); 
	     }
     } catch (Exception e) {
    	    PushState.state(Useriphone,"callLog",200);
	    	map.put("errorCode", "0001");
	    	map.put("errorInfo", "网络异常");
	        e.printStackTrace();
   }  	
    return map;

	}
 
	 /** 接口
	  * 获取验证码 联通
	  * @param request
	  * @param response
	  * @return 
	  * @throws IOException
	 * @throws InterruptedException 
	  */
	 public Map<String, Object> TelecomLogin(HttpServletRequest request,HttpServletResponse response,TelecomBean TelecomBean) throws IOException, InterruptedException{
		 
		 Map<String,Object> map=new HashMap<String, Object>();
		 	Map<String,Object> data=new HashMap<String, Object>();
		 	try {
		 		 WebClient webClient = new WebClient();
				 webClient.getOptions().setUseInsecureSSL(true);
				 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
				 webClient.getOptions().setTimeout(100000);
				 webClient.getOptions().setCssEnabled(false);
				 webClient.getOptions().setJavaScriptEnabled(true);
				 webClient.setJavaScriptTimeout(100000); 
				 webClient.getOptions().setRedirectEnabled(true);
				 webClient.getOptions().setThrowExceptionOnScriptError(false);
				 webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
				 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//		
		 		HtmlPage page= webClient.getPage("http://login.189.cn/login");
				HtmlTextInput txe=(HtmlTextInput) page.getElementById("txtAccount");
				HtmlPasswordInput txepasswprd=(HtmlPasswordInput) page.getElementById("txtPassword");
				txe.setValueAttribute(TelecomBean.getUserPhone());
				txepasswprd.setValueAttribute(TelecomBean.getUserPassword());
				HtmlPage loginpage=(HtmlPage) page.executeJavaScript("$('#loginbtn').click();").getNewPage();
				Thread.sleep(7000);
	
//		    HtmlForm htmlform=  (HtmlForm) loginpage.getElementById("loginForm");
			    HtmlDivision htmlform=  (HtmlDivision) loginpage.getElementById("divErr");

		    if(htmlform==null||htmlform.equals("")){
		    	//开始授权 
		     HtmlPage logi=webClient.getPage("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000202");
		    			 	WebRequest webRequest=new WebRequest(new URL("http://sn.189.cn/service/bill/feeDetailrecordList.action"));
		    			 	 List<NameValuePair> reqParamsinfo = new ArrayList<NameValuePair>();  
		    			 	reqParamsinfo.add(new NameValuePair("currentPage","1"));
		    			 	reqParamsinfo.add(new NameValuePair("pageSize","10"));
		    			 	reqParamsinfo.add(new NameValuePair("effDate","2017-05-01"));
		    			 	reqParamsinfo.add(new NameValuePair("expDate","2017-08-20"));
		    			 	reqParamsinfo.add(new NameValuePair("serviceNbr",TelecomBean.getUserPhone()));
		    			 	reqParamsinfo.add(new NameValuePair("operListID","1"));
		    			 	reqParamsinfo.add(new NameValuePair("isPrepay","0"));
		    			 	reqParamsinfo.add(new NameValuePair("pOffrType","481"));
		    	 		    webRequest.setHttpMethod(HttpMethod.POST);
		    	 		    webRequest.setRequestParameters(reqParamsinfo);
		    	 		    List list=new ArrayList();
		    	 		    
		    	 		    HtmlPage  Infopage=webClient.getPage(webRequest);
		    	 		    System.out.print(Infopage.asXml());
		    	 		   HtmlTable htmlTable=null;
		    	 		    if(!Infopage.asXml().contains("您好，您查询的时间段内没有详单数据。中国电信")){
		    	 		    	htmlTable=(HtmlTable) Infopage.getByXPath("//table").get(0);
		    	 		    	  data.put("info", htmlTable.asXml());
		    	 		    }else{
		    	 		    	  data.put("info","");
		    	 		    }
		    	 		  
		    	 		//s   Document doc = Jsoup.parse(htmlTable.asXml());
//		    	 	        Elements trs = doc.select("table").select("tr");
//		    	 	        for(int i = 0;i<trs.size();i++){
//		    	 	            Elements tds = trs.get(i).select("td");
//		    	 	            for(int j = 0;j<tds.size();j++){
//		    	 	                String text = tds.get(j).text();
//		    	 	                System.out.println(text);
//		    	 	               list.add(text);
//		    	 	         
//		    	 	            }
//		    	 	        }
		    	 	    
		    	 	        // data.put("info", htmlTable.asXml().replace("100%","50%").replace("mt10 transact_tab","testv"));
		    	 		 
		    	 	        map.put("data", data);
//		    			   	map.put("errorCode","0000");
//					    	map.put("errorInfo","成功");
					       	map.put("UserIphone",TelecomBean.getUserPhone());
					    	map.put("UserPassword",TelecomBean.getUserPassword());
					    	map.put("flag","0");
					    	//map=resttemplate.SendMessage(map, "http://192.168.3.35:8080/HSDC/message/telecomCallRecord");
//					    	map=resttemplate.SendMessage(map, 	application.getSendip()+"/HSDC/authcode/callRecordTelecom");
					    	map=resttemplate.SendMessage(map, application.getSendip()+"/HSDC/message/telecomCallRecord"); 		    	
		    }else{
		    	if(htmlform.asText().contains("请输入验证码")){
		    		map.put("errorCode","0001");
			    	map.put("errorInfo","该帐号已被锁定，请您明天再来尝试");
		    	}else{
		    		map.put("errorCode","0001");
			    	map.put("errorInfo","帐号或密码错误");
		    	}
		    
		    	
		    }
			} catch (Exception e) {
				System.out.println(e);
				map.put("errorCode","0001");
		    	map.put("errorInfo","网络异常");
			}
		 	

	  //电信推送
	  
	 
	
			return map;
	 	}
	 
	 /** 接口
	  * 验证码 电信
	  * @param request
	  * @param response
	  * @return 
	  * @throws IOException
	 * @throws InterruptedException 
	  */
	 public Map<String, Object> TelecomQueryInfo(HttpServletRequest request,HttpServletResponse response,TelecomBean TelecomBean) throws IOException, InterruptedException{
		 	Map<String,Object> map=new HashMap<String, Object>();
		 	Map<String,Object> data=new HashMap<String, Object>();
		 	HttpSession session=request.getSession();
		 	WebClient webClient= (WebClient) session.getAttribute("WebClient");
		 	TelecomBean telecomBean=(TelecomBean) session.getAttribute("TelecomBean");
		 	
		 	WebRequest webRequest=new WebRequest(new URL("http://sn.189.cn/service/bill/feeDetailrecordList.action"));
		 	 List<NameValuePair> reqParams = new ArrayList<NameValuePair>();  
 		    reqParams.add(new NameValuePair("currentPage","1"));
 		    reqParams.add(new NameValuePair("pageSize","10"));
 		    reqParams.add(new NameValuePair("effDate","2017-07-01"));
 		    reqParams.add(new NameValuePair("expDate","2017-07-07"));
 		    reqParams.add(new NameValuePair("serviceNbr",telecomBean.getUserPhone()));
 		    reqParams.add(new NameValuePair("operListID","1"));
 		    reqParams.add(new NameValuePair("isPrepay","0"));
 		    reqParams.add(new NameValuePair("pOffrType","481"));
 		    webRequest.setHttpMethod(HttpMethod.POST);
 		    webRequest.setRequestParameters(reqParams);
 		    HtmlPage  Infopage=webClient.getPage(webRequest);
 		    System.out.println(Infopage.asText());
			return data;

	 }
	 
	/**
	 * 
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws InterruptedException
	 */
		public Map<String,Object> encryptrsa(HttpServletRequest request,String qqnumber) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
				Map<String,Object> map=new HashMap<String, Object>();
				WebClient webClients= crawlerUtil.WebClientNice();
				int count=0;
		 	   boolean flg=true;
		        do {
		        	count++;
		        	 Thread.sleep(2000);
		        	  HtmlPage page= webClients.getPage("https://ssl.ptlogin2.qq.com/check?pt_tea=2&uin="+qqnumber+"&appid=522005705&ptlang=2052&regmaster=&pt_uistyle=9&r=0.07655477741844985&pt_jstoken=1515144655");
				        String info=page.asText();
				        String[] infoarry=info.split(",");
				        String xx=infoarry[2].replace("'","");  
				        String code=infoarry[1].replace("'","");
				        String sess=infoarry[3].replace("'","");	
				        String vecode=infoarry[0].replace("'","");
				        System.out.println(vecode+"-----");
				        if(!vecode.contains("1")){
				        	flg=false;
					        map.put("xx", xx);
					        map.put("code", code);
					        map.put("sess", sess);
					      HttpSession session=request.getSession();
					      session.setAttribute("webClients",webClients);
				        }
				        if(vecode.contains("1")&&count==10){
				        	map.put("code", "用户邮箱异常,请明天再来尝试");
				        	flg=false;
				        }
				} while (flg);
//		        if(vecode.contains("1")){
//		        	UnexpectedPage pagecode=webClients.getPage("https://ssl.captcha.qq.com/getimage?uin=_qq&aid=522005705&cap_cd=verify_cap_cd&0.7659631329588592");
//		        	BufferedImage ioim=ImageIO.read(pagecode.getInputStream());
//		        	File path = new File(request.getSession().getServletContext().getRealPath("/upload") + "/"); // 此目录保存缩小后的关键图
//		        	if  (!path .exists()  && !path .isDirectory())      
//		    		{       
//		    		    System.out.println("//不存在");  
//		    		    path .mkdir();    
//		    		}
//		        	
//		        	String  fileName = System.currentTimeMillis() +"mailcode"+ ".png";
//		        	ImageIO.write(ioim,"png",new File(path,fileName));
//		        	//需要验证码
//		            map.put("fileName", fileName);
//		            
//		        }else{
//		            map.put("fileName",'1');
//		        }
		        
		  
	
			return map;
			
		}
		/**
		 * 邮件抓取
		 * @param request
		 * @param qqnumber
		 * @return
		 * @throws FailingHttpStatusCodeException
		 * @throws MalformedURLException
		 * @throws IOException
		 * @throws InterruptedException
		 */
		public Map<String,Object> test(HttpServletRequest request,String qqnumber,String sess,String password,String code,String card,String showpwd) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
			
			HttpSession session=request.getSession();
			List lists=new ArrayList<Object>();			
			Map<String,Object> map=new HashMap<String, Object>();
			WebClient client=(WebClient) session.getAttribute("webClients");	
			System.out.println(sess+"sess");	
			System.out.println(password);
		    HtmlPage pages= client.getPage("https://ssl.ptlogin2.qq.com/login?pt_vcode_v1=0&pt_verifysession_v1="+sess+"&verifycode="+code+"&u="+qqnumber+"&p="+password+"&pt_randsalt=2&ptlang=2052&low_login_enable=1&low_login_hour=720&u1=https%3A%2F%2Fw.mail.qq.com%2Fcgi-bin%2Flogin%3Fvt%3Dpassport%26vm%3Dwsk%26delegate_url%3D%26f%3Dxhtml%26target%3D%26ss%3D1&from_ui=1&fp=loginerroralert&device=2&aid=522005705&daid=4&pt_3rd_aid=0&ptredirect=1&h=1&g=1&pt_uistyle=9&regmaster=&");
		    System.out.println(pages.asXml());
		    if(pages.asText().contains("登录成功")){
		    	 HtmlPage pagev= client.getPage("http://mail.qq.com/cgi-bin/loginpage");
		    	 if(!pagev.asText().contains("使用读屏软件的朋友请进入这个入口无障碍帮助入口")){
		    		 	map.put("errorCode", "0004");
				    	map.put("errorInfo", "请取消独立密码后认证！！！");
		    	 }else{
		    		 String urls=pagev.getUrl().toString().substring(pagev.getUrl().toString().indexOf("?"));
				 	    String mname="信用卡";
				 	    HtmlPage pageinfo= client.getPage("https://w.mail.qq.com/cgi-bin/mail_list?sid="+urls.replace("?sid=", "")+"&t=mail_list&s=search&page=0&pagesize=100&folderid=all&topmails=0&subject=");

				 	    DomNodeList<DomNode> iLis = pageinfo.querySelectorAll(".maillist_listItemRight");
				 	    
			
				 	    for (int i = 0; i < iLis.size(); i++) {
				 			HtmlAnchor div=(HtmlAnchor) iLis.get(i);
				 			if(div.asText().contains("交通银行信用卡电子账单")&&div.asText().contains("2017年06月")){
				 				HtmlPage xykpage= client.getPage("https://w.mail.qq.com/"+div.getHrefAttribute());
				 				System.out.println(xykpage.asText());
				 				lists.add(xykpage.asXml());
				 				
				 			}
				 			if(div.asText().contains("广发卡06月账单")){
				 				HtmlPage xykpage= client.getPage("https://w.mail.qq.com/"+div.getHrefAttribute());
				 				System.out.println(xykpage.asText());
				 				lists.add(xykpage.asXml());
				 				
				 			}
				 			if(div.asText().contains("招商银行信用卡电子账单")){
				 				HtmlPage xykpage= client.getPage("https://w.mail.qq.com/"+div.getHrefAttribute());
				 				System.out.println(xykpage.asText());
				 				if(xykpage.asText().contains("2017/06")){
				 					lists.add(xykpage.asXml());
				 				}
				 		
				 				
				 			}
				 			
				 			if(div.asText().contains("光大银行信用卡电子对账单")){
				 				HtmlPage xykpage= client.getPage("https://w.mail.qq.com/"+div.getHrefAttribute());
				 				System.out.println(xykpage.asText());
				 				if(xykpage.asText().contains("2017/08")){
				 					lists.add(xykpage.asXml());
				 				}
				 		
				 				
				 			}
				 			if(div.asText().contains("邮储银行信用卡电子账单")){
				 				HtmlPage xykpage= client.getPage("https://w.mail.qq.com/"+div.getHrefAttribute());
				 				System.out.println(xykpage.asText());
				 				if(xykpage.asText().contains("2017年08月")){
				 					lists.add(xykpage.asXml());
				 				}
				 		
				 				
				 			}
				 			if(div.asText().contains("中国建设银行信用卡电子账单")){
				 				HtmlPage xykpage= client.getPage("https://w.mail.qq.com/"+div.getHrefAttribute());
				 				System.out.println(xykpage.asText());
				 				if(xykpage.asText().contains("2017-08")){
				 					lists.add(xykpage.asXml());
				 				}
				 		
				 				
				 			}
				 		
				 		
				 			
				 	
				 		}
				 	    if(lists.size()>0){
				 			map.put("data", lists);
				 	    }else{
				 	   	map.put("data", "null");
				 	    }
				
						map.put("qqnumber", qqnumber);
						map.put("password", showpwd);
						map.put("card", card);
						
				 	  map= resttemplate.SendMessage(map,	application.getSendip()+"/HSDC/authcode/mailBill");
				 
				 	 //	map.put("errorCode", "0004");
				    	//map.put("errorInfo", "请取消独立密码后认证！！！");
		    	 }

		 	  
		         
		     
		    }else{
		    	map.put("errorCode", "0001");
		    	map.put("errorInfo", pages.asXml());
		    }
		   
		    return map;
		}
		public Map<String,Object> shixinQuery(HttpServletRequest request,String pCardNum,String pName,String pCode) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

			Map<String,Object> data=new HashMap<String, Object>();
			Map<String,Object> map=new HashMap<String, Object>();
	   		HttpSession session=request.getSession();
	   			WebClient webClient=(WebClient) session.getAttribute("webClient");	
	   			HtmlPage page=(HtmlPage) session.getAttribute("page");
				HtmlForm form= page.getForms().get(0);
				HtmlTextInput htmlTextInput= form.getInputByName("pName");
				HtmlTextInput htmlTextInput1=form.getInputByName("pCardNum");
				HtmlTextInput htmlTextInput2=form.getInputByName("pCode");
				HtmlHiddenInput htmlTextInput4= (HtmlHiddenInput) page.getElementById("captchaId");
				htmlTextInput.setValueAttribute(pName);
				htmlTextInput1.setValueAttribute(pCardNum);
		        HtmlTextInput htmlTextInput3= (HtmlTextInput) page.getElementById("pCode");
		        htmlTextInput3.setValueAttribute(pCode);
				HtmlDivision division=	(HtmlDivision) page.querySelector(".login_button");
				webClient.getOptions().setThrowExceptionOnScriptError(false);
				HtmlPage pageen= division.click();
				if(pageen.asText().contains("验证码")){
					map.put("errorCode", "0001");
					map.put("errorInfo", "验证码错误");
				}else{
					TextPage pagetext;
					if(pageen.asText().contains("共0条")){
						map.put("data","");
					}else{
						pagetext= webClient.getPage("http://shixin.court.gov.cn/disDetailNew?id=120680208&pCode="+pCode+"&captchaId="+htmlTextInput4.getValueAttribute()+"");
						map.put("data", pagetext.getContent());
					}
					
						//推送数据
			
					map.put("pCardNum", pCardNum);
				  map=resttemplate.SendMessage(map, 	application.getSendip()+"/HSDC/grade/humanLaw");
				  
				  
					
				}
		
			
			return map;
			
		}
		public Map<String,Object> shixinQueryCode(HttpServletRequest request) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
			
			HttpSession session = request.getSession();

			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> data = new HashMap<String, Object>();
//			WebClient webClient = new WebClient(BrowserVersion.CHROME);
//			webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
//			webClient.getOptions().setTimeout(90000);
//			webClient.getOptions().setCssEnabled(true);
//			webClient.getOptions().setJavaScriptEnabled(true);
//			webClient.setJavaScriptTimeout(40000);
//			webClient.getOptions().setRedirectEnabled(true);
//			webClient.getOptions().setThrowExceptionOnScriptError(false);
			WebClient webClient=crawlerUtil.WebClientperson();
			HtmlPage page = webClient
					.getPage("http://shixin.court.gov.cn/index_new_form.do");
			HtmlImage image = (HtmlImage) page.getElementById("captchaImg");
			ImageReader ioim = image.getImageReader();//此处可能有异常
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
		public Map<String,Object> AcademicLogin(HttpServletRequest request,String username,String userpwd,String code,String lt,String userCard) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
			//--------------------数据中心推送状态----------------------
        	PushState.state(userCard, "CHSI",100);
        	//---------------------数据中心推送状态----------------------
	   		Map<String,Object> map=new HashMap<String, Object>();
			Map<String,Object> data=new HashMap<String, Object>();
			HttpSession session=request.getSession();
			WebClient webClient= (WebClient) session.getAttribute("xuexinWebClient");
			
			WebRequest webRequest=new  WebRequest(new java.net.URL(crawlerUtil.XuexinPOST));
			List<NameValuePair> list=new ArrayList<NameValuePair>();
			list.add(new NameValuePair("username",username));
			list.add(new NameValuePair("password",userpwd));
			list.add(new NameValuePair("captcha", code));

			list.add(new NameValuePair("lt", lt));
			list.add(new NameValuePair("_eventId","submit"));
			list.add(new NameValuePair("submit","登  录"));
			
			webRequest.setHttpMethod(HttpMethod.POST);
			webRequest.setRequestParameters(list);
			 try {
			HtmlPage pages= webClient.getPage(webRequest);
			
		//	HtmlDivision Logindiv= (HtmlDivision) pages.getElementById("status");
			if(!pages.asText().contains("您输入的用户名或密码有误")&&!pages.asText().contains("图片验证码输入有误")){
			logger.info("学信网登录成功，准备获取数据");
		        HtmlPage pagess= webClient.getPage(crawlerUtil.Xuexininfo);
	 	        HtmlTable table=(HtmlTable) pagess.querySelector(".mb-table");  
	 	         data.put("info", table.asXml());
	 	         map.put("data", data);
	 	         map.put("Usernumber",username); 
	 	         map.put("UserPwd",userpwd);
	 	         map.put("Usercard",userCard); 
	 	     
				
	 	         map=resttemplate.SendMessage(map, 	application.getSendip()+"/HSDC/authcode/hireright");
	 	      //--------------------数据中心推送状态----------------------
	         	PushState.state(userCard, "CHSI",300);
	         	//---------------------数据中心推送状态----------------------
			}else if(pages.asText().contains("您输入的用户名或密码有误")){
		 		map.put("errorCode","0002");
		 		map.put("errorInfo","您输入的用户名或密码有误");
		 		  //--------------------数据中心推送状态----------------------
	         	PushState.state(userCard, "CHSI",200);
	         	//---------------------数据中心推送状态----------------------
	
			}else if(pages.asText().contains("图片验证码输入有误")){
		 		map.put("errorCode","0001");
		 		map.put("errorInfo","图片验证码输入有误");
		 		  //--------------------数据中心推送状态----------------------
	         	PushState.state(userCard, "CHSI",200);
	         	//---------------------数据中心推送状态----------------------
			}
		   	} catch (Exception e) {
		   		System.out.print(e);
		   		if(e.toString().contains("com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException")){
		   		  //--------------------数据中心推送状态----------------------
		         	PushState.state(userCard, "CHSI",200);
		         	//---------------------数据中心推送状态----------------------
		   			map.put("errorCode","0002");
			 		map.put("errorInfo","密码错误");	
		   		}else{
		   		  //--------------------数据中心推送状态----------------------
		         	PushState.state(userCard, "CHSI",200);
		         	//---------------------数据中心推送状态----------------------
		   			map.put("errorCode","0002");
			 		map.put("errorInfo","网络错误");
		   		}
 	    		
			}
//			try {
//				
//	
//	   		WebClient webClient= crawlerUtil.WebClientNice();
//			HtmlPage page= webClient.getPage("https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check");
//			HtmlTextInput htmlTextInput= (HtmlTextInput) page.getElementById("username");
//			HtmlPasswordInput htmlPasswordInput= (HtmlPasswordInput) page.getElementById("password");
//			htmlTextInput.setValueAttribute(Usernumber);
//			htmlPasswordInput.setValueAttribute(UserPwd);
//	        HtmlSubmitInput submit=page.getElementByName("submit"); 
//	        HtmlPage pages=  submit.click();
//	        Thread.sleep(3000);
//	        if(!pages.asText().contains("您输入的用户名或密码有误")){
//	        	if(!pages.asText().contains("为保障您的账号安全，请输入验证码后重新登录")){
//	   	       	 System.out.println(pages.asXml());
//		 	        HtmlPage pagess= webClient.getPage("https://my.chsi.com.cn/archive/gdjy/xj/show.action");
//		 	        Thread.sleep(3000);
//		 	        HtmlTable table=(HtmlTable) pagess.querySelector(".mb-table");    
//		 	         data.put("info", table.asXml());
//		 	         map.put("data", data);
//		 	         map.put("Usernumber",Usernumber); 
//		 	         map.put("UserPwd",UserPwd);
//		 	         map.put("Usercard",Usercard); 
//		 	         map=resttemplate.SendMessage(map, 	application.getSendip()+"/HSDC/authcode/hireright");
//	        	}else{
//	         		map.put("errorCode","0002");
//	    			map.put("errorInfo","出错次数达到上限,请明天再来尝试");
//	        	}
//	        	
//	        	
//
//	        }else{
//	        	if(page.asText().contains("为保障您的账号安全，请输入验证码后重新登录")){
//	        		map.put("errorCode","0002");
//	    			map.put("errorInfo","出错次数达到上限,请明天再来尝试");
//	        	}else{
//	        		map.put("errorCode","0002");
//	    			map.put("errorInfo","信息有误");
//	        	}
//	    
//	        }
//	       
//	      
//			} catch (Exception e) {
//				map.put("errorCode","0003");
//    			map.put("errorInfo","网络错误");
//			}
//	     
//	   		
	   		return map;	
			
		}
		/**
		 * 淘宝信息认证
		 * @param request
		 * @param Usernumber
		 * @param UserPwd
		 * @param Usercard
		 * @return
		 * @throws FailingHttpStatusCodeException
		 * @throws MalformedURLException
		 * @throws IOException
		 * @throws InterruptedException
		 */
		public Map<String,Object> Taobao(HttpServletRequest request,String Usernumber,String UserPwd,String userCard) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
			boolean flg=false;//定义是否重复爬取
			int count=0;//初始化爬取测试
			int maxcount=3;//定义最大爬取次数
	    	Map<String,Object> map=new HashMap<String, Object>();
	    	Map<String,Object> data=new HashMap<String, Object>();
//			WebClient webClient = new WebClient(BrowserVersion.CHROME,Scheduler.port,Scheduler.ip);
	    	//WebClient webClient = new WebClient(BrowserVersion.CHROME, Scheduler.ip, Scheduler.port);
	    	WebClient webClient = new WebClient();
			 webClient.getOptions().setUseInsecureSSL(true);
			 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
			 webClient.getOptions().setTimeout(100000);
			 webClient.getOptions().setCssEnabled(true);
			 webClient.getOptions().setJavaScriptEnabled(true);
			 webClient.setJavaScriptTimeout(100000); 
			 webClient.getOptions().setRedirectEnabled(true);
			 webClient.getOptions().setThrowExceptionOnScriptError(false);
			 webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
			 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			 PushState.state(userCard, "TaoBao",100);//认证中
			 System.out.println("推送认证中=="+userCard);
			 do {
				 count++;
				 WebRequest webRequest=new  WebRequest(new java.net.URL("https://login.taobao.com/member/login.jhtml"));
				 webClient.addRequestHeader("accept-Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
				// webRequest.setAdditionalHeader("accept-Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");		
				 List<NameValuePair> list=new ArrayList<NameValuePair>();
				 list.add(new NameValuePair("TPL_password", UserPwd));
				 list.add(new NameValuePair("TPL_username", Usernumber));
				 list.add(new NameValuePair("newlogin", "1"));
				 list.add(new NameValuePair("callback", "1"));
				 webRequest.setHttpMethod(HttpMethod.POST);
				 webRequest.setRequestParameters(list);
				 HtmlPage pagess=webClient.getPage(webRequest);

					 if(pagess.getTitleText().equals("页面跳转中")){
						 String token=pagess.asXml().substring(pagess.asXml().indexOf("token")).split("&")[0].toString().replaceAll("token=", "");
						 String token2=pagess.asXml().substring(pagess.asXml().indexOf("token",pagess.asXml().indexOf("token")+1)).split("&")[0].toString().replaceAll("token=", "");
						 HtmlPage page= webClient.getPage("https://passport.alibaba.com/mini_apply_st.js?site=0&token="+token+"&callback=callback");
						 HtmlPage page2= webClient.getPage("https://passport.alibaba.com/mini_apply_st.js?site=0&token="+token2+"&callback=callback");
						 System.out.println(page2.asXml());
						 HtmlPage pagev=webClient.getPage("https://login.taobao.com/member/login.jhtml?redirectURL=http%3A%2F%2Fwww.taobao.com%2F");
						 webClient.getPage("https://login.taobao.com/member/vst.htm?st="+""+"&params=style%3Dminisimple%26sub%3Dtrue%26TPL_username%3D"+Usernumber+"%26loginsite%3D0%26from_encoding%3D%26not_duplite_str%3D%26guf%3D%26full_redirect%3D%26isIgnore%3D%26need_sign%3D%26sign%3D%26from%3Ddatacube%26TPL_redirect_url%3Dhttp%25253A%25252F%25252Fmofang.taobao.com%25252Fs%25252Flogin%26css_style%3D%26allp%3D&_ksTS=1404787873165_78&callback=jsonp79");
						HtmlPage enpage=webClient.getPage("http://trade.taobao.com/trade/itemlist/list_bought_items.htm?spm=1.7274553.1997525045.2.C6QtVd");
						 HtmlPage pagea= webClient.getPage("https://member1.taobao.com/member/fresh/deliver_address.htm?addrId=5874841844");
						 HtmlTable table;
						 if(pagea.querySelectorAll(".tbl-main").getLength()>0){
							 	flg=false;
								//-------------------------------
								 //成功进入 进入支付宝页面
								 
							 	  WebRequest requests=new WebRequest(new URL("https://authet15.alipay.com/login/certCheck.htm"));
							 	  List<NameValuePair> lists=new ArrayList<NameValuePair>();
							 	  lists.add(new NameValuePair("goto","https://my.alipay.com/portal/i.htm?src=yy_content_jygl&sign_from=3000&sign_account_no=20881124651440950156&src=yy_content_jygl"));
							 	  lists.add(new NameValuePair("tti","2119"));
							 	  lists.add(new NameValuePair("isIframe","false"));
							 	  lists.add(new NameValuePair("REMOTE_PCID_NAME","_seaside_gogo_pcid"));
							 	  lists.add(new NameValuePair("is_sign","Y"));
							 	  lists.add(new NameValuePair("security_activeX_enabled","false"));
							 	  lists.add(new NameValuePair("securityId","web|cert_check|5c7e8f11-ad18-44f0-8187-db1f35c0b835RZ25"));
							 	  requests.setHttpMethod(HttpMethod.POST);
							 	  requests.setRequestParameters(lists);
							 	  HtmlPage pageinfos= webClient.getPage(requests);
							 	  HtmlPage pageinfoss= webClient.getPage("https://my.alipay.com/portal/i.htm?src=yy_content_jygl&sign_from=3000&sign_account_no=20881124651440950156&src=yy_content_jygl");
							 	  	table=(HtmlTable) pagea.querySelectorAll(".tbl-main").get(0);
							 	  
							 	  	
//									data.put("info", table.asXml());
//									map.put("data", data);
//									map.put("userName", Usernumber);
//									map.put("userPwd", UserPwd);
//									map.put("userCard", userCard);
					
							 	  data.put("info",table.asXml() );
							 	  data.put("page",pageinfoss.asXml() );
							 	  map.put("data", data);
							 	  map.put("userName", Usernumber);
							 	  map.put("userPwd", UserPwd);
							 	  map.put("userCard", userCard);
							 	  map=resttemplate.SendMessage(map, 	application.getSendip()+"/HSDC/authcode/taobaoPush");
							 	  if(map!=null&&"0000".equals(map.get("errorCode").toString())){
								    	PushState.state(userCard, "TaoBao",300);
						                map.put("errorInfo","查询成功");
						                map.put("errorCode","0000");
						        }else{
						            	//--------------------数据中心推送状态----------------------
						            	PushState.state(userCard, "TaoBao",200);
						            	//---------------------数据中心推送状态----------------------
						                map.put("errorInfo","查询失败");
						                map.put("errorCode","0001");
						            }
;							 	  logger.warn("===淘宝"+map.toString());
						 }else{
							 	flg=true;//如果没有继续爬取
							
								
						 }
						 if(flg==true&&count==maxcount){//如果满足条件 不再爬取，提示稍后再来
							 PushState.state(userCard, "TaoBao",200);
							 	map.put("errorCode","0002");
				    			map.put("errorInfo","目前认证的人较多，请稍后再试");
								logger.warn("读取收获地址table为空！请请检查");
						 }
					 }else{
						 PushState.state(userCard, "TaoBao",200);
							HtmlDivision division= (HtmlDivision) pagess.getElementById("J_Message");
							map.put("errorCode","0002");
			    			map.put("errorInfo",division.asText());
			    		
					 }
				
			
				
			} while (flg);
		
	
			
		   		
		   	return map;
			
		}
		
		
		public Map<String,Object> XuexinGetCode(HttpServletRequest request,HttpServletResponse response)
				throws FailingHttpStatusCodeException, MalformedURLException, IOException {
			HttpSession session=request.getSession();
			WebClient webClient =	crawlerUtil.WebClientXuexin();
			Map<String,Object>map=new HashMap<String, Object>();
			Map<String,Object>data=new HashMap<String, Object>();
			HtmlPage pagelt = webClient.getPage(crawlerUtil.XueXinLogin);
			HtmlHiddenInput hiddenInput= pagelt.getElementByName("lt");
			String lt=hiddenInput.getValueAttribute();
			UnexpectedPage pageimg=webClient.getPage(crawlerUtil.XueXinGetCode);
			BufferedImage img=ImageIO.read(pageimg.getInputStream());
			String  fileName = System.currentTimeMillis() + "xuexin.png";
			
		 	File path = new File(request.getSession().getServletContext().getRealPath("/upload") + "/");

		 	// 此目录保存缩小后的关键图
		 	if (!path.isDirectory()){
				path.mkdirs();
		 	}
			ImageIO.write(img,"png",new File(path,fileName));
			data.put("ip",application.getIp());
			data.put("FileName",fileName);
			data.put("FilePath","/upload");
			data.put("Port",application.getPort());
			data.put("lt",lt);
			map.put("data",data);
			map.put("errorCode", "0000");
			map.put("errorInfo", "查询成功");
			session.setAttribute("xuexinWebClient", webClient);
			logger.info(application.getIp()+application.getPort()+"/upload"+fileName);
			logger.info(request.getSession().getServletContext().getRealPath("/upload") + "/");
			return map;
			

		}
		
		
}
