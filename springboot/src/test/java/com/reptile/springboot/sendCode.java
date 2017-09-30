package com.reptile.springboot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.htmlparser.tags.Html;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.autoconfigure.jdbc.DataSourceInitializedEvent;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.ConstantInterface;
import com.reptile.util.Resttemplate;

import net.sf.json.JSONObject;



/**
 */
public class sendCode {
public static void main(String[] args) throws FailingHttpStatusCodeException, IOException {
	String ImageURL="https://xyk.cebbank.com/mall/api/captcha";
	WebClient webclient=new WebClient();
	webclient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
	webclient.getOptions().setTimeout(90000);
	webclient.getOptions().setCssEnabled(false);
	webclient.getOptions().setJavaScriptEnabled(true);
	webclient.setJavaScriptTimeout(40000);
	webclient.getOptions().setRedirectEnabled(true);
	webclient.getOptions().setThrowExceptionOnScriptError(true);
	webclient.getOptions().setThrowExceptionOnFailingStatusCode(true);
	webclient.setAjaxController(new NicelyResynchronizingAjaxController());
	webclient.getOptions().setCssEnabled(true);
	//获取验证码
	UnexpectedPage Imagepage=  webclient.getPage(ImageURL);
	BufferedImage bufferedImage  = ImageIO.read(Imagepage.getInputStream());
    Date datas=	new Date();
    String time=new SimpleDateFormat("yyyyMMddhhmmssSSS").format(datas);
	String ImageName="CebloginImger"+time ;
	ImageIO.write(bufferedImage , "png", new File("D://CebloginImger"+ImageName+".png"));
	Scanner scanner=new Scanner(System.in);
	String scannes=scanner.next();
	List<NameValuePair> list = new ArrayList<NameValuePair>();
	//设置参数
	list.add(new NameValuePair("name", "61052319910609847X"));
	list.add(new NameValuePair("code", scannes));
	//发送请求头
	webclient.addRequestHeader("Host", "xyk.cebbank.com");
	webclient.addRequestHeader("cebbank", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
	webclient.addRequestHeader("Accept", "*/*");
	webclient.addRequestHeader("Accept-Language", "gzip, deflate, br");
	webclient.addRequestHeader("Accept-Encoding", "gzip, deflate, br");
	webclient.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	webclient.addRequestHeader("mtoken", "null");
//	webclient.addRequestHeader("X-Requested-With", "XMLHttpRequest");
	webclient.addRequestHeader("Referer", "https://xyk.cebbank.com/mall/login?target=/mycard/bill/info.htm");
//	webclient.addRequestHeader("Content-Length", "33");
//	webclient.addRequestHeader("Cookie", "__v=1.2463984060387318000.1503641057.1503652413.1503658883.5; ALLYESID4=0E62F3648AE10F47; MALL=4007a8c0; __l=78209354; fsid=6bb1ca8dZ13327711Z15e1859d3c8Za141");
	webclient.addRequestHeader("Connection", "keep-alive");
	webclient.addRequestHeader("Origin", "https://xyk.cebbank.com");
	webclient.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
	webclient.addRequestHeader("X-Requested-With", "XMLHttpRequest");//设置格式，否则无法返回JSON




		try {
			 //发包获得验证码 ;https://xyk.cebbank.com/mall/api/usercommon/dynamic
			
			WebRequest request =new WebRequest(new URL("https://xyk.cebbank.com/mall/api/usercommon/dynamic"));
			request.setHttpMethod(HttpMethod.POST);
			request.setRequestParameters(list);
			UnexpectedPage pagev= webclient.getPage(request);
			System.out.println(pagev.getWebResponse().getContentAsString());
			//二次请求模拟登陆
			WebRequest requests =new WebRequest(new URL("https://xyk.cebbank.com/mall/api/user/login?t=1503663657867"));
			List<NameValuePair> lists= new ArrayList<NameValuePair>();
			Scanner scanners=new Scanner(System.in);
			String scanness=scanners.next();
			lists.add(new NameValuePair("userName", "61052319910609847X"));
			lists.add(new NameValuePair("code", scannes));
			lists.add(new NameValuePair("password", scanness));
			lists.add(new NameValuePair("target", ""));
			requests.setHttpMethod(HttpMethod.POST);
			requests.setRequestParameters(lists);
			UnexpectedPage page= webclient.getPage(requests);
			JSONObject jsonObject=JSONObject.fromObject(page.getWebResponse().getContentAsString());
			System.out.println("进入已出账单");
			//三次进入个人
			HtmlPage ccenterPage=webclient.getPage("https://xyk.cebbank.com/mycard/bill/havingprintbill-query.htm");
			HtmlTable  table= (HtmlTable) ccenterPage.getByXPath("//table[@class='tab_one']").get(0);
		     String str = table.asXml().toString();
		     Document doc = Jsoup.parse(str);
		      Elements trs = doc.select("table").select("tr");
		      Map<String, Object> map=new HashMap<String, Object>();
		      Map<String,Object> data=new HashMap<String,Object>();
		      List<String> html =new ArrayList<String>();
		      for(int i = 1;i<trs.size();i++){
		          Elements tds = trs.get(i).select("td");
		          for(int j = 0;j<1;j++){
		        	  String text = tds.get(0).text();
		                text=text.replace("/","");
		      		  	System.out.println(text);
		      		  	System.out.println("------------");
			      		  HtmlPage detailedpage=webclient.getPage("https://xyk.cebbank.com/mycard/bill/billquerydetail.htm?statementDate="+text);
			      		html.add(detailedpage.asXml());
		          }
		      } 
		      Map<String,Object> seo=new HashMap<String, Object>();
		      data.put("idcard","61052319910609847X");
		      data.put("backtype","CEB" );
		      data.put("html", html);
		      seo.put("data", data);
		      Resttemplate resttemplate = new Resttemplate();
		      map=resttemplate.SendMessage(seo, ConstantInterface.port+"/HSDC/BillFlow/BillFlowByreditCard");
		      System.out.println(map);
		      System.out.println("页面已经放置到html中");
		} catch (Exception e) {
			 e.printStackTrace();
			System.out.println("失败！！！，填写信息有误");
		}
}
}
