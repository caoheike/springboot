package com.reptile.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
/**
 *  创建浏览器
 * @author bigyoung
 *
 */
public class CrawlerUtil {

	 public static final String port="8079";
	 public static final String ip="113.200.203.114";//194外网
	 public static final String sendip="http://113.200.203.114:8082";//194外网 数据中心
	//--------------------------以上外网需要
	
	//public static final String port="8080";
	//public static final String ip="192.168.3.222";
	//public static final String sendip="http://192.168.3.4:8081";


	public static String XueXinLogin="https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check";
	public static String XueXinGetCode="https://account.chsi.com.cn/passport/captcha.image?id=68.95757530327288";
	public static String XuexinPOST="https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check";

	public static String Xuexininfo="https://my.chsi.com.cn/archive/gdjy/xj/show.action";

	public final WebClient webClient = new WebClient(BrowserVersion.CHROME);
	private static Logger logger=Logger.getLogger(CrawlerUtil.class);

	public WebClient setWebClient(){
		webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
		webClient.getOptions().setTimeout(90000);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.setJavaScriptTimeout(8000);
		webClient.getOptions().setRedirectEnabled(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        //启动ajax代理
        return webClient;
		
		
	}
	public WebClient WebClientNice(){
		webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
		webClient.getOptions().setTimeout(90000);
		webClient.getOptions().setCssEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.setJavaScriptTimeout(40000);
		webClient.getOptions().setRedirectEnabled(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setCssEnabled(false);
   
        //启动ajax代理
   
		return webClient;
		
	}
	public WebClient WebClientNices(){
		webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
		webClient.getOptions().setTimeout(90000);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.setJavaScriptTimeout(40000);
		webClient.getOptions().setRedirectEnabled(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setJavaScriptEnabled(false);
        //启动ajax代理
   
		return webClient;
		
	}
	
	public WebClient WebClientperson(){
		WebClient webClient = new WebClient();
		webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
		webClient.getOptions().setTimeout(90000);
		webClient.setJavaScriptTimeout(5000);
		webClient.getOptions().setCssEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setJavaScriptTimeout(40000);
		webClient.getOptions().setRedirectEnabled(true);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

        //启动ajax代理
   
		return webClient;
		
	}
	public void setdelay(int number) throws InterruptedException{
		Thread.sleep(number);
	}
	/**
	 * 获取电信验证码
	 * @throws IOException 
	 */
	
	public void obtainCode(HttpServletRequest request,HttpServletResponse response) throws InterruptedException, IOException{
		HttpSession reSession=request.getSession();
		 HtmlPage page= (HtmlPage) reSession.getAttribute("LoginPage");//
		 HtmlImage valiCodeImg= (HtmlImage) page.getElementById("imgCaptcha");
		 
			ImageReader imageReader = valiCodeImg.getImageReader();
			BufferedImage bufferedImage = imageReader.read(0);

			BufferedImage inputbig = new BufferedImage(80,40, BufferedImage.TYPE_INT_BGR);
			Graphics2D g = (Graphics2D) inputbig.getGraphics();
			g.drawImage(bufferedImage, 0, 0, 80,40, null); // 画图
			g.dispose();
			inputbig.flush();
		    ImageIO.write(inputbig, "png", response.getOutputStream());
		 
	
	}
	/**
	 * 发送短信
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws FailingHttpStatusCodeException 
	 * @throws InterruptedException 
	 */
	
	 public Map<String,String> SendCode(HttpServletRequest request,com.reptile.model.TelecomBean telecomBean) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException{
		 Map<String,String> map=new HashMap<String,String>(); 
		 HttpSession session= request.getSession();
		 WebClient webClient= (WebClient) session.getAttribute("webClient");
		 HtmlPage page=webClient.getPage(telecomBean.SendCodeUrl);
		 Thread.sleep(8000);
		 if(page.asText().contains("免费获取验证码")){
			 HtmlAnchor anchor=(HtmlAnchor) page.getElementById("verificationcode");//获取发送验证的a标签元素
			 anchor.click();
			 logger.info("验证码发送成功");
			 map.put("msg","发送成功");
			 session.setAttribute("CodePage",page);
			 session.setAttribute("webClient",webClient);
			 
		 }else{
			 map.put("msg","网络繁忙，请稍后再试");
			 logger.info("详情页面不包含发送验证码事件️️");
		 }
		 return map;
	 }
	 
	 /**
	  * 获取联通验证码
	  * @param request
	  * @param response
	  * @throws InterruptedException
	  * @throws IOException
	  */
	public void GetCode(HttpServletRequest request, HttpServletResponse response)
			throws InterruptedException, IOException {
	    final String verifyCodeImageUrl="http://uac.10010.com/portal/Service/CreateImage";//+System.currentTimeMillis();
		HttpSession reSession = request.getSession();
		HtmlPage page = (HtmlPage) reSession.getAttribute("GetCodepage");
		//HtmlPage page=pages.getElementById("loginVerifyImg").click();

	      UnexpectedPage verifyCodeImagePage = webClient.getPage(verifyCodeImageUrl);
          //取图片输入流
          BufferedImage bi= ImageIO.read(verifyCodeImagePage.getInputStream());
          request.setAttribute("Codepages",page);
          //输出图片到响应头
          ImageIO.write(bi, "JPG", response.getOutputStream());
		

	}

	   public static String getUUID(){
	         return UUID.randomUUID().toString().replace("-", "");
	    }
	   
	  /**
	   * 新增学信网工具类
	   * @return
	   */
	
		public WebClient WebClientXuexin(){
			Map<String,Object> map=new HashMap<String, Object>();
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
			return webClient;
			
		}
		
		public  static String Getips() throws FailingHttpStatusCodeException, MalformedURLException, IOException{
			WebClient webClient = new WebClient();
			webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
			webClient.getOptions().setTimeout(90000);
			webClient.setJavaScriptTimeout(5000);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.setJavaScriptTimeout(40000);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			HtmlPage page= webClient.getPage("http://blog.sina.com.cn/s/blog_1688effdf0102zfd1.html");
			HtmlDivision division=(HtmlDivision) page.getElementById("sina_keyword_ad_area2");
			
			return division.asText();
		}
	
}
