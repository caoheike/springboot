package com.reptile.util;
//package com.hommsun.util;
//
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Scanner;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//
//import javax.imageio.ImageIO;
//
//
//
//
//
//
//
//
//
//
//
//
//
//import javax.imageio.ImageReader;
//
//import jj2000.j2k.image.input.ImgReader;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.htmlunit.HtmlUnitDriver;
//import org.openqa.selenium.ie.InternetExplorerDriver;
//
//import scala.reflect.internal.Trees.New;
//
//import com.gargoylesoftware.htmlunit.BrowserVersion;
//import com.gargoylesoftware.htmlunit.CookieManager;
//import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
//import com.gargoylesoftware.htmlunit.HttpMethod;
//import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
//import com.gargoylesoftware.htmlunit.UnexpectedPage;
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.WebRequest;
//import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
//import com.gargoylesoftware.htmlunit.html.HtmlButton;
//import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
//import com.gargoylesoftware.htmlunit.html.HtmlDivision;
//import com.gargoylesoftware.htmlunit.html.HtmlEmailInput;
//import com.gargoylesoftware.htmlunit.html.HtmlEmbed;
//import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
//import com.gargoylesoftware.htmlunit.html.HtmlImage;
//import com.gargoylesoftware.htmlunit.html.HtmlObject;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;
//import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
//import com.gargoylesoftware.htmlunit.html.HtmlSelect;
//import com.gargoylesoftware.htmlunit.html.HtmlSpan;
//import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
//import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
//import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLDivElement;
//import com.gargoylesoftware.htmlunit.util.Cookie;
//import com.gargoylesoftware.htmlunit.util.NameValuePair;
//import com.jacob.activeX.ActiveXComponent;
//import com.jacob.com.Dispatch;
//import com.jacob.com.Variant;
//
//public class posts {
//	
//	private static String code = "https://sn.ac.10086.cn/servlet/CreateImage";
//	private static String loginurl = "https://sn.ac.10086.cn/loginAction";
//	private static String initRSAPubkey = "http://sn.ac.10086.cn/servlet/initRSAPubkey";
//
//	// public static void main(String[] args) throws
//	// FailingHttpStatusCodeException, IOException {
//	// System.out.println("来时");
//	// WebClient webClient = new WebClient();
//	// WebRequest webRequest = new WebRequest(new URL(loginurl));
//	// webRequest.setHttpMethod(HttpMethod.POST);
//	// UnexpectedPage page= webClient.getPage(code);
//	// BufferedImage img=ImageIO.read(page.getInputStream());
//	// ImageIO.write(img,"png", new File("/Users/hongzheng/test.png"));
//	// Scanner scanner =new Scanner(System.in);
//	//
//	//
//	// List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
//	// reqParam.add(new NameValuePair("userName", "18220834780"));
//	// reqParam.add(new NameValuePair("password",
//	// "25a877cb727f6ac5c218261f5db2343779d4516fe479802e1dde87297327995ad65b1abef653e91761f67d7166098b0e8dc41b77582509dbb36d0c9b919b6d06cef0cc8e76e458ff7f718734a3dd851f1248096621a9bcd2cb2d11c01c9eedc2bdca11643cb5356efecb4c3924f4bb53dbd0c0340132a615716662b16d020976"));
//	// reqParam.add(new NameValuePair("verifyCode",scanner.next()));
//	// reqParam.add(new NameValuePair("OrCookies", "1"));
//	// reqParam.add(new NameValuePair("loginType", "1"));
//	// reqParam.add(new NameValuePair("fromUrl", "uiue/login_max.jsp"));
//	// reqParam.add(new NameValuePair("toUrl",
//	// "http://www.sn.10086.cn/my/account/"));
//	// webRequest.setRequestParameters(reqParam);
//	//
//	// HtmlPage pages= webClient.getPage(webRequest);
//	// System.out.println(pages.asText());
//	//
//	// }
//
////	 public static void main(String[] args) throws FailingHttpStatusCodeException, IOException {
////		 
////	 WebClient webClient = new WebClient();
////	 webClient.getOptions().setUseInsecureSSL(true);
////	 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
////	 webClient.getOptions().setTimeout(90000);
////	 webClient.getOptions().setCssEnabled(true);
////	 webClient.getOptions().setJavaScriptEnabled(true);
////	 webClient.setJavaScriptTimeout(40000);
////	 webClient.getOptions().setRedirectEnabled(true);
////	 webClient.getOptions().setThrowExceptionOnScriptError(false);
////	 webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
////	 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
////	 WebRequest webRequest=new WebRequest(new
////	 URL("https://pbsz.ebank.cmbchina.com/CmbBank_GenShell/UI/GenShellPC/Login/GenUniLogin.aspx"));
////	 List<NameValuePair> List=new ArrayList<NameValuePair>();
////	 List.add(new
////	 NameValuePair("ClientNo","4266F7C76935130C21C10F17D74C9E59424556816224276600032831"));
////	 //https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check
////	 List.add(new NameValuePair("CreditCardVersion","2.0"));
////	 List.add(new NameValuePair("BranchNo","0028"));
////	 List.add(new NameValuePair("AccountNo","11"));
////	 List.add(new NameValuePair("Password","undefined"));
////	 List.add(new
////	 NameValuePair("HardStamp","4266F7C76935130C21C10F17D74C9E59424556816224276600032831"));
////	 List.add(new NameValuePair("Licex",
////	 "Ajg1QzI1REJERjU2NzcyMzM4MzMwMTc0Nzk0RTFFNEZEMDE5OTc5NjYwMDM2MTk4OTkwMDAxNzAwMwAAAAAAAABNWSx6LuvvW6CsBXlP8xJ*vVTBq8fSvK9PV4fbZ3Mh*rLchioqmgYeJroAn5WfPG9k0wpi*3-zPp4jdFapx63wyOZCPC---tOfcIDDE2xNNB9Ck6wMNS5l*IqwOQzc6MiwBmv2VySz**CGbvectO-*r9BdDoAF9z2ZjX6R6T9ZL-MaKD1RCgRLtbsneuw1Nr8F7ORkEWrWRFVHmT2Bw0CsEUTQKiGSJ4fYaU2gtirJl0EEkfCbShkoPCYgJ9TmK4MT1NsrEo917nK3Jsb3uHwDcd7Iiemoze0wvVwJmHaueLYKgVZRlhfpcDtYIFjc5QNU*rQj8epLEkASqChNLBSSj97aE5GUOw8S3B6igOMBRotwYTTDkTZ-GJH9Wy2-Q9gLXQbVHGDTnfzTXek4Eiqxwq-47a0gDHILl0x9AA__"));
////	 webRequest.setHttpMethod(HttpMethod.POST);
////	 webRequest.setRequestParameters(List);
////	 //webRequest.setAdditionalHeader("asd", "asd");
////	 webClient.addRequestHeader("Content-Type","application/x-www-form-urlencoded");
////	 webClient.addRequestHeader("Referer","https://pbsz.ebank.cmbchina.com/CmbBank_GenShell/UI/GenShellPC/Login/Login.aspx");
////	 webClient.addRequestHeader("Origin","https://pbsz.ebank.cmbchina.com");
////	 webClient.addRequestHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.1 Safari/603.1.30");
////	 Set<Cookie> cookies = webClient.getCookieManager().getCookies();;
////	
////	 for (Cookie c : cookies) {
////	 webClient.getCookieManager().addCookie(c);
////	
////	 }
////	 HtmlPage page= webClient.getPage(webRequest);
////	 System.out.println(page.asXml());
////	
////		// System.load("C://Windows//System32//CMBEdit.dll");
////	 System.load("C://Windows//SysWOW64//CMBEdit.dll");
////	 System.out.println(System.getProperty("java.library.path"));
////	 System.load("C://Program Files//Java//jdk1.8.0_131//jre//bin//CMBEdit.dll");
////	System.load("C://Windows//System32//CMBEdit.dll");
////	 }
////	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException,IOException, InterruptedException {
////
////		 WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52);
////		 webClient.getOptions().setActiveXNative(true);
////		 webClient.getOptions().setUseInsecureSSL(true);
////		 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
////		 webClient.getOptions().setTimeout(90000);
////		 webClient.getOptions().setCssEnabled(true);
////		 webClient.getOptions().setJavaScriptEnabled(true);
////		 webClient.setJavaScriptTimeout(40000);
////		 webClient.getOptions().setRedirectEnabled(true);
////		 webClient.getOptions().setThrowExceptionOnScriptError(false);
////		 webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
////		 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
////		 HtmlPage page= webClient.getPage("https://pbsz.ebank.cmbchina.com/CmbBank_GenShell/UI/GenShellPC/Login/Login.aspx");
////		 System.out.println(page.asXml());
//////	final ActiveXComponent activeXComponent = new ActiveXComponent("CLSID:0CA54D3F-CEAE-48AF-9A2B-31909CB9515D");
//////		activeXComponent.call(dispatchTarget, dispid, a1, a2, a3, a4, a5, a6, a7, a8)
////		
////	}
//	/**
//	 * 交通银行-
//	 * 
//	 * @throws IOException
//	 * @throws MalformedURLException
//	 * @throws FailingHttpStatusCodeException
//	 * @throws InterruptedException
//	 * 
//	 */
//
//	// public static void main(String[] args) throws
//	// FailingHttpStatusCodeException, MalformedURLException, IOException,
//	// InterruptedException {
//	// System.load("C://Windows//System32//BocomXEdit.dll");
//	//
//	//
//	//
//	// final ActiveXComponent activeXComponent = new
//	// ActiveXComponent("CLSID:65D5224B-B8EE-4F56-99DF-A450C7C5270F");
//	// WebClient webClient = new WebClient();
//	// webClient.getOptions().setUseInsecureSSL(true);
//	// webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
//	// webClient.getOptions().setTimeout(90000);
//	// webClient.getOptions().setCssEnabled(true);
//	// webClient.getOptions().setJavaScriptEnabled(true);
//	// webClient.setJavaScriptTimeout(40000);
//	// webClient.getOptions().setRedirectEnabled(true);
//	// webClient.getOptions().setThrowExceptionOnScriptError(false);
//	// webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//	// webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//	// HtmlPage page=
//	// webClient.getPage("https://pbank.95559.com.cn/personbank/logon.jsp");
//	//
//	// HtmlTextInput htmlTextInput= (HtmlTextInput)
//	// page.getElementById("alias");
//	// HtmlHiddenInput hiddenInput=(HtmlHiddenInput)
//	// page.getElementByName("password");
//	// HtmlTextInput htmlTextInput2=(HtmlTextInput)
//	// page.getElementById("input_captcha");
//	//
//	// HtmlImage htmlImage=page.querySelector(".captchas-img-bg");
//	// BufferedImage imgBufferedImage=htmlImage.getImageReader().read(0);
//	// ImageIO.write(imgBufferedImage,"png", new File("D:/vpn/weizia.png"));
//	// Scanner scanner=new Scanner(System.in);
//	// System.out.println("请输入验证码:");
//	// htmlTextInput2.setValueAttribute(scanner.next());
//	// System.out.println(imgBufferedImage+"vvvv");
//	// hiddenInput.setValueAttribute("asdas");
//	// htmlTextInput.setValueAttribute("asdas");
//	// HtmlAnchor division=(HtmlAnchor) page.getElementById("login");
//	// division.click();
//	// Thread.sleep(3000);
//	//
//	// System.out.println(page.asXml());
//	//`	
//	// }
//	/**
//	 * 
//	 */
//	
//	/**
//	 * 中国银行
//	 * 模拟登录
//	 * @throws IOException 
//	 * @throws MalformedURLException 
//	 * @throws FailingHttpStatusCodeException 
//	 * @throws InterruptedException 
//	 * 	 driver.get("https://ibsbjstar.ccb.com.cn/app/V5/CN/STY1/login.jsp");
//     driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
//     driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
//     driver.manage().window().maximize();
//      
//     driver.switchTo().frame("fclogin");
//     WebElement softKeyPad = driver.findElement(By.id("img_id"));
//     softKeyPad.click();
//     WebElement t=driver.findElement(By.xpath("//input[@value='t']"));
//     WebElement e=driver.findElement(By.xpath("//input[@value='e']"));
//     WebElement s=driver.findElement(By.xpath("//input[@value='s']"));
//     t.click();
//     e.click();
//     s.click();
//     t.click();
//     WebElement confirmButton=driver.findElement(By.name("button12"));
//     confirmButton.click();
//     	// System.out.println(page.asXml());
//	 //System.setProperty(“webdriver.chrome.driver”, bsPath);
//	 System.setProperty("webdriver.ie.driver", "D:/ie/IEDriverServer.exe");
//	 WebDriver driver = new InternetExplorerDriver();
//	 	driver.get("https://ebsnew.boc.cn/boc15/login.html");
////
////	 System.out.println("asdas");
// System.out.println(driver.getPageSource());
//	 */
//// public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
//	 //System.load("C://Windows//System32//KeyboardProtection_x64.dll");
//	// WebClient webClient=new WebClient(BrowserVersion.BEST_SUPPORTED);
//	// final ActiveXComponent activeXComponent = new ActiveXComponent("CLSID:E61E8363-041F-455C-8AD0-8A61F1D8E540");
//
////	 Dispatch disp = activeXComponent.getObject();
//	// System.out.println(disp);
////	 webClient.addRequestHeader("Accept", "*/*");
////	 webClient.addRequestHeader("Accept-Encoding", "gzip, deflate, sdch, br");
////	 webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
////	 webClient.addRequestHeader("Connection", "keep-alive");
////
////	 webClient.addRequestHeader("Host", "ebsnew.boc.cn");
////	 webClient.addRequestHeader("If-Modified-Since", "Sat, 10 Oct 2015 18:48:28 GMT");
////	 webClient.addRequestHeader("Referer", "https://ebsnew.boc.cn/boc15/login.html");
////	 webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
//
//	 
//	// webClient.getOptions().setCssEnabled(true);
//	// webClient.getOptions().setActiveXNative(true);
//	// webClient.getOptions().setUseInsecureSSL(true);
//	// webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
//	// webClient.getOptions().setTimeout(90000);
//	// webClient.getOptions().setCssEnabled(true);
//	// webClient.getOptions().setJavaScriptEnabled(true);
//	// webClient.setJavaScriptTimeout(40000);
////	 webClient.getOptions().setRedirectEnabled(true);
////	 webClient.getOptions().setThrowExceptionOnScriptError(false);	
//	// webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//	// webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//
//	 //HtmlPage page= webClient.getPage("https://ebsnew.boc.cn/boc15/login.html");
//    
////	 Set<Cookie> cookies = webClient.getCookieManager().getCookies();
//	 
////	 for (Cookie c : cookies) {
//	//	 System.out.println(c+"------");
//	// webClient.getCookieManager().addCookie(c);
//	// }
//	 //Thread.sleep(9000);
//	//System.out.println( page.asXml());
////	 HtmlTextInput htmlPage= (HtmlTextInput) page.getElementById("txt_username_79443");
////	 HtmlTextInput htmlPasswordInput=(HtmlTextInput) page.getElementById("input_div_password_79445");
////	 HtmlImage img= (HtmlImage) page.getElementById("captcha");
////	  BufferedImage IOIMG=img.getImageReader().read(0);
////	  ImageIO.write(IOIMG, "PNG", new File("D:/js/code.png"));
////	  System.out.println("请输入验证码:");
////	  Scanner scanner=new Scanner(System.in);
////	 HtmlTextInput htmlTextInput= (HtmlTextInput) page.getElementById("txt_captcha_79449");
////	 htmlTextInput.setValueAttribute(scanner.next());
////	 htmlPage.setValueAttribute("621661280000447287");
////	 htmlPasswordInput.setValueAttribute("199510");
////	// HtmlAnchor anchor=(HtmlAnchor) page.getElementById("btn_login_79676");
////	 HtmlPage weizaipage= (HtmlPage) page.executeJavaScript("document.getElementById('btn_login_79676').click();").getNewPage();
////	 Thread.sleep(3000);
////	System.out.println(weizaipage.asXml());
////	  
////		HtmlSpan span= (HtmlSpan) weizaipage.getElementById("msgContent");
////		System.out.println("--"+span+"0"+span.asText());
//
//	 
//
////}
////	//HtmlUnitDriver
////	public static void main(String[] args) throws InterruptedException {
////		//final ActiveXComponent activeXComponent = new ActiveXComponent("CLSID:E61E8363-041F-455C-8AD0-8A61F1D8E540");
////		 System.setProperty("webdriver.ie.driver", "D:/ie/IEDriverServer.exe");
//	 //WebDriver driver = new InternetExplorerDriver();
////		 driver.get("https://ebsnew.boc.cn/boc15/login.html");	
////		 WebElement txtbox = driver.findElement(By.id("txt_username_79443"));
////		 txtbox.sendKeys("a");
////		 Actions action = new Actions(driver); 
////		 action.contextClick();// 鼠标右键在当前停留的位置做单击操作 
////		 action.sendKeys(Keys.TAB);
////		 System.out.println("结束");
////		    //  WebElement txtboxs = driver.findElement(By.id("input_div_password_79445"));
////		     // System.out.println(txtboxs.getText());
////		 //	System.out.println(driver.getPageSource());
////	}
// 
// 
//// public static void main(String[] args) {
//
//	// int[] ar={50,31,28,1};
//
//	// Arrays.sort(ar);
//	// System.out.println(ar[0]);
//	// System.out.println(ar[1]);
//	// System.out.println(ar[2]);
//	// System.out.println(ar[3]);
//	 
//	
//	// 	int count=0;
////	for (int i = 0; i < ar.length; i++) {
//	//	for (int j = ar.length-1; j>0; j--) {
//	//		if(ar[i]>ar[j]){
//	//			count=ar[i];
//	//		}else{
//			//	count=ar[i];
//			//}
//		//}
//		//System.out.println(count);
////	}
//
////	 System.setProperty("webdriver.ie.driver", "D:/ie/IEDriverServer.exe");
////	 WebDriver driver = new InternetExplorerDriver();
////	 	driver.get("https://ebsnew.boc.cn/boc15/login.html");
////	    WebElement element = driver.findElement(By.id("txt_username_79443"));
////	    WebElement elements = driver.findElement(By.id("input_div_password_79445"));
////	    
////	    element.sendKeys("asdasd");
////	    elements.sendKeys("123");
////	    System.out.println(element);
//
//
// //}
//	/**
//	 * 模拟修改密码
//	 */
////	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
////	//	https://service.sn.10086.cn/app?service=page/personalinfo.ResetPwdOperation&listener=resetOperation
////		 WebClient webClient = new WebClient();
////		 webClient.getOptions().setUseInsecureSSL(true);
////		 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
////		 webClient.getOptions().setTimeout(90000);
////		 webClient.getOptions().setCssEnabled(true);
////		 webClient.getOptions().setJavaScriptEnabled(true);
////		 webClient.setJavaScriptTimeout(40000);
////		 webClient.getOptions().setRedirectEnabled(true);
////		 webClient.getOptions().setThrowExceptionOnScriptError(false);
////		 webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
////		 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
////		 HtmlPage pages= webClient.getPage("https://service.sn.10086.cn/app?service=page/personalinfo.ResetPwdOperation&listener=resetOperation");
////		 HtmlPage page= (HtmlPage) pages.executeJavaScript("$('#protocl').click();").getNewPage();//选中服务协议
////		 
////		 HtmlTextInput htmlTextInput= (HtmlTextInput) page.getElementById("phone_TEMP");//手机号
////		 HtmlTextInput htmlTextInput2=(HtmlTextInput) page.getElementById("PSPT_ID_TEMP");//身份证
////		 HtmlImage image=(HtmlImage) page.getElementById("pic");//验证码图片
////		 HtmlTextInput codeinput=(HtmlTextInput) page.getElementById("RSET_YZM");//验证码
////		 htmlTextInput.setValueAttribute("18220834780");
////		 htmlTextInput2.setValueAttribute("513721199510106811");
////		 
////		 
////		// image.get
////		 BufferedImage image2=image.getImageReader().read(0);
////		 ImageIO.write(image2, "png",new File("D:/weizia.png"));
////		 
////		 Scanner canScanner =new Scanner(System.in);
////		 codeinput.setValueAttribute(canScanner.next());
////		 HtmlPage pageend= (HtmlPage) page.executeJavaScript("resetParam(this);").getNewPage();//点击确定
////		 Thread.sleep(3000);
////		 System.out.println(pageend.asXml());
////		
////	}
//   /**
////    * post登录
//// * @throws IOException 
//// * @throws FailingHttpStatusCodeException 
////    
// * @throws IOException 
// * @throws MalformedURLException 
// * @throws FailingHttpStatusCodeException 
// * @throws InterruptedException */
////	public static void main(String[] args) throws FailingHttpStatusCodeException, IOException, InterruptedException {
////	
////		 	WebClient webClient = new WebClient();
////
////			 webClient.getOptions().setUseInsecureSSL(true);
////			 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
////			 webClient.getOptions().setTimeout(90000);
////			 webClient.getOptions().setCssEnabled(true);
////			 webClient.getOptions().setJavaScriptEnabled(true);
////			 webClient.setJavaScriptTimeout(40000);
////			 webClient.getOptions().setRedirectEnabled(true);
////			 webClient.getOptions().setThrowExceptionOnScriptError(false);
////			 webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
////			 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
////			 HtmlPage  page=webClient.getPage("https://service.sn.10086.cn/app?service=page/personalinfo.ResetPwdOperation&listener=initPage&MENU_ID=&loginType=-1&isGroup=1");
////			 HtmlImage image= (HtmlImage) page.getElementById("pic");
////			 ImageReader ioim=image.getImageReader();
////			 BufferedImage bufferedImage=ioim.read(0);
////			 ImageIO.write( bufferedImage, "png", new File("D://weizai.png"));
////			 System.out.println("请输入验证码：");
////			 Scanner scanner=new Scanner(System.in);
////			 Set<Cookie> cookies = webClient.getCookieManager().getCookies();;
////				
////			 for (Cookie c : cookies) {
////			 webClient.getCookieManager().addCookie(c);
////			
////			 }
////			 WebRequest webRequest=new WebRequest(new URL("https://service.sn.10086.cn/app?service=page/personalinfo.ResetPwdOperation&listener=resetOperation"));
////			 List<NameValuePair> List=new ArrayList<NameValuePair>();
////			 List.add(new NameValuePair("service","direct/1/personalinfo.ResetPwdOperation/$Form"));
////			 List.add(new NameValuePair("sp","S0"));
////			 List.add(new NameValuePair("Form0","phone_TEMP,PSPT_ID_TEMP"));
////			 List.add(new NameValuePair("phone","72e4da914fd89400333e3f7ef105dbeeb658c2bcdc0306e2403c7a43666422576745ad7ef20bfcbeae6bfb3e1f39f6dcd856c6b0e6ae79705c0e7663a61d82ee1f21f335a986ec7555e9c3c7ffccbf905cf9b7511eeda64cd0a540b4209f420904fd522e447bbf2392294d794bf11e4bf9e7fc987c16af5f749bad799d27f420"));
////			 List.add(new NameValuePair("PSPT_ID","96480ce85539fe195bf952a11673f75af9a6c8fb3c5d0c84b0518050cc1eb64ab467fd2e5757e84ade9218d5e1607281c87d0cc2b6fe7eebad8df505117a1e4cf7c22de49c672d738a8e21fb156e8b23c6f612ef214e14b49fb48124cbde0aed00397653802117427e9cfd3c84a45ddbf8c86ed6ebb1288e04525c438203d1df"));
////			 List.add(new NameValuePair("protocl", "on"));
////			 List.add(new NameValuePair("phone_TEMP", "18706807991"));
////			 List.add(new NameValuePair("PSPT_ID_TEMP", "513721199510106811"));
////			 List.add(new NameValuePair("RSET_YZM",scanner.next()));
////			 
////			 
////			 webRequest.setHttpMethod(HttpMethod.POST);
////			 webRequest.setRequestParameters(List);
////			 HtmlPage pages= webClient.getPage(webRequest);
////			HtmlDivision div= pages.querySelector(".con");	 
////			if(div.asText().contains("您的服务密码已重置成功")){
////				 System.out.println("请输入短息密码");
////				 Scanner scanner2=new Scanner(System.in);
////				 System.out.println("请输入加密后的");
////				 Scanner scanner3=new Scanner(System.in);
////				 String rsano=scanner2.next();
////				 String rsayes=scanner3.next();
////	
////				 WebRequest webRequests=new WebRequest(new URL("https://service.sn.10086.cn/app?service=page/personalinfo.ResetPwdOperation&listener=changeOperation"));
////				 List<NameValuePair> Lists=new ArrayList<NameValuePair>();
////				 Lists.add(new NameValuePair("service","direct/1/personalinfo.ResetPwdOperation/$Form$0"));
////				 Lists.add(new NameValuePair("sp","S0"));
////				 Lists.add(new NameValuePair("OLD_USER_PASSWD",rsayes));
////				 Lists.add(new NameValuePair("X_NEW_PASSWD","24c51889beb088777bbd4c4c36c21a9a2967557d09ed7200e5b526e7237ca8916c324b7425e6de939532f3ef207dfcb6ef148e9ca0a21607124b09e4a658b29e50ab1900f932edf72a999af88e1b3f57371212040e95d767dac02c9a65fce8cefb2010044b3c3292f72aaf99455bb0e7d7559f5391be2dd4315db03e1b13b41e"));
////				 Lists.add(new NameValuePair("RE_NEW_PASSWD","24c51889beb088777bbd4c4c36c21a9a2967557d09ed7200e5b526e7237ca8916c324b7425e6de939532f3ef207dfcb6ef148e9ca0a21607124b09e4a658b29e50ab1900f932edf72a999af88e1b3f57371212040e95d767dac02c9a65fce8cefb2010044b3c3292f72aaf99455bb0e7d7559f5391be2dd4315db03e1b13b41e"));
////	
////					
////
////				 Lists.add(new NameValuePair("OLD_USER_PASSWD_TEMP",rsano));
////				 Lists.add(new NameValuePair("X_NEW_PASSWD_TEMP", "950933"));
////				 Lists.add(new NameValuePair("RE_NEW_PASSWD_TEMP", "950933"));
////				 webRequests.setHttpMethod(HttpMethod.POST);
////				 webRequests.setRequestParameters(Lists);
////				 HtmlPage pagess= webClient.getPage(webRequests);
////				 Thread.sleep(3000);
////				 System.out.println(pagess.asText());
////			
////			}else{
////				System.out.println("发送失败，原因"+div.asText());
////			}
//////			 System.out.println(pages.asXml());
//////			 System.out.println(div.asText());
////		
////	}
////		 
////		 public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
////			 	 WebClient webClient = new WebClient();
////				 webClient.getOptions().setUseInsecureSSL(true);
////				 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
////				 webClient.getOptions().setTimeout(90000);
////				 webClient.getOptions().setCssEnabled(true);
////				 webClient.getOptions().setJavaScriptEnabled(true);
////				 webClient.setJavaScriptTimeout(40000);
////				 webClient.getOptions().setRedirectEnabled(true);
////				 webClient.getOptions().setThrowExceptionOnScriptError(false);
////				 webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
////				 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
////				 HtmlPage  page=webClient.getPage("https://xui.ptlogin2.qq.com/cgi-bin/xlogin?proxy_url=http://game.qq.com/comm-htdocs/milo/proxy.html&appid=21000501&target=top&s_url=http%3A%2F%2Flol.qq.com%2Fweb201310%2Fcdkey.shtml&style=20&daid=8");
////				 Set<Cookie> cookies = webClient.getCookieManager().getCookies();
////				 for (Cookie c : cookies) {
////				 webClient.getCookieManager().addCookie(c);
////				 System.out.println(c);
////				 }
//////				 HtmlAnchor htmlAnchor= (HtmlAnchor) page.getElementById("switcher_plogin");
//////				  //HtmlImage img= (HtmlImage) page.getElementById("verifyimg");
//////				 // ImageReader imgReader=img.getImageReader();
//////				 // BufferedImage bufferedImage=imgReader.read(0);
//////				 // ImageIO.write(bufferedImage, "png", new File("d://weizai.png"));
//////				 System.out.println(htmlAnchor);
//////				 htmlAnchor.click();
//////				 Thread.sleep(3000);
//////				HtmlTextInput htmlTextInput= (HtmlTextInput) page.getElementById("u");
//////				HtmlPasswordInput htmlPasswordInput=(HtmlPasswordInput) page.getElementById("p");
//////				System.out.println(htmlPasswordInput+"9527");
//////				System.out.println(htmlTextInput);
//////				htmlTextInput.setValueAttribute("1121212159");
//////				htmlPasswordInput.setValueAttribute("weizai9527");
//////				HtmlSubmitInput htmlSubmitInput=(HtmlSubmitInput) page.getElementById("login_button");
//////				htmlSubmitInput.click();
//////				Thread.sleep(3000);
//////				System.out.println(page.asText());
////           HtmlPage pages=(HtmlPage) page.executeJavaScript("document.getElementById('img_out_1121212159').click();").getNewPage();
////           Thread.sleep(3000);
////           HtmlSelect htmlSelect=(HtmlSelect) pages.getElementById("areaContentId_318014");
////           htmlSelect.getOption(14).setSelected(true); 
////           Thread.sleep(3000);
////           HtmlSelect ss=  (HtmlSelect) pages.getElementById("roleContentId_318014");
////           System.out.println(ss.asText());            
////           HtmlPage pagey= (HtmlPage) pages.executeJavaScript("document.getElementById('link-spanNotBind').click();").getNewPage();
////           //System.out.println(pages.asText());
//////              HtmlSelect htmlSelect= (HtmlSelect) pagey.getElementById("area1ContentId_cf");
//////              System.out.println(htmlSelect);
//////              htmlSelect.getOption(12).setSelected(true); 
//////              HtmlSelect htmlSelect2= (HtmlSelect) pagey.getElementById("areaContentId_cf");
//////              htmlSelect2.getOption(1).setSelected(true); 
//////              Thread.sleep(3000);
////          
////		 }
//	
////  public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
////	  	WebClient webClient = new WebClient(BrowserVersion.CHROME);
////		 webClient.getOptions().setUseInsecureSSL(true);
////		 webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
////		 webClient.getOptions().setTimeout(90000);
////		 webClient.getOptions().setCssEnabled(true);
////		 webClient.getOptions().setJavaScriptEnabled(true);
////		 webClient.setJavaScriptTimeout(40000);
////		 webClient.getOptions().setRedirectEnabled(true);
////		 webClient.getOptions().setThrowExceptionOnScriptError(false);
////		 webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
////		 webClient.setAjaxController(new NicelyResynchronizingAjaxController());
////	  
////		 HtmlPage page= webClient.getPage("http://ui.ptlogin2.qq.com/cgi-bin/login?hide_title_bar=0&low_login=0&qlogin_auto_login=1&no_verifyimg=1&link_target=blank&appid=636014201&target=self&s_url=http%3A//www.qq.com/qq2012/loginSuccess.htm");
////		HtmlAnchor anchor= (HtmlAnchor) page.getElementById("switcher_plogin");
////		anchor.click();
////		Thread.sleep(2000);
////	    HtmlTextInput htmlTextInput=	(HtmlTextInput) page.getElementById("u");
////	    HtmlPasswordInput htmlPasswordInput= (HtmlPasswordInput) page.getElementById("p");
////	    HtmlSubmitInput htmlSubmitInput=(HtmlSubmitInput) page.getElementById("login_button");
////	    htmlTextInput.setValueAttribute("1121212159");
////	    htmlPasswordInput.setValueAttribute("weizai9527");
////	    htmlSubmitInput.click();
////	    Thread.sleep(5000);
////	    
////		
////		 System.out.println(page.asXml());
////}
//}
//
