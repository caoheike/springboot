package com.reptile.springboot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

public class liubinxuexin2 {
	public static void main(String[] args) throws Exception {
		new liubinxuexin2().SetEmail();
		
	}
	@SuppressWarnings("unused")
	//获取页面的信息传到前台 
	public static Map<Object, String> xuexin2() throws Exception{//获得选择找回密码方式的页面信息
		Map<String, Object> map= liubinxuexin1.xuexin1();
		HtmlPage xuexin2= (HtmlPage) map.get("xuexin1");
		WebClient webclient=(WebClient) map.get("webclient");
		System.out.println("--------------------------------------------------------------------"+webclient);
		Map<Object, String> psdRtv_cMap=new HashMap<Object, String>();
		//psdRtv_c
		HtmlElement regline=   (HtmlElement) xuexin2.getByXPath("//div[@class='regline']").get(0);
		String  regline_txt= regline.asText().trim();
		psdRtv_cMap.put("regline_txt", regline_txt);
		List<?> psdRtv_c=   (List<?>) xuexin2.getByXPath("//div[@class='psdRtv_c']");
		HtmlInput  ctoken=xuexin2.getElementByName("ctoken");//每次的都是会变的
		System.out.println(ctoken.getDefaultValue()+"++++++++++++++++++++++++++++");
		for (int i = 0; i < psdRtv_c.size(); i++) {
			System.out.println(i+"--"+psdRtv_c.get(i));
			HtmlElement psdRtv_ctxt=   (HtmlElement) xuexin2.getByXPath("//div[@class='psdRtv_c']").get(i);
			System.out.println(psdRtv_ctxt.asText().trim());
			String psdRtv_ctxts=psdRtv_ctxt.asText().trim();
			psdRtv_cMap.put("psdRtv_ctxts"+i, psdRtv_ctxts);
		}
		return psdRtv_cMap;
	}
	public Map<String, Object> Letter() throws Exception{//进行选择找回密码方式
		Map<String, Object> map= liubinxuexin1.xuexin1();
		HtmlPage xuexin2= (HtmlPage) map.get("xuexin1");
		WebClient webclient=(WebClient) map.get("webclient");
		HtmlInput  ctoken=xuexin2.getElementByName("ctoken");//每次的都是会变的
	    HtmlAnchor byemail_link=	(HtmlAnchor) xuexin2.getElementById("byemail_link");//根据邮箱进行找回密码
//	    HtmlPage   emailpage=byemail_link.click();
//	    System.out.println("----++-----"+emailpage.asXml());
	   
	    HtmlAnchor  bymphone_link= (HtmlAnchor) xuexin2.getElementById("bymphone_link");//根据手机号进行找回密码
	    HtmlPage phonepage=bymphone_link.click();
	    System.out.println("----++-----"+ phonepage.asXml());
	
//	    HtmlAnchor  byanswer_link=(HtmlAnchor) xuexin2.getElementById("byanswer_link");//根据安全问题进行找回密码
//	    HtmlPage answerpage=byanswer_link.click();
//	    System.out.println("----++-----"+answerpage.asXml());
//	    map.put("answerpage", answerpage);
	    map.put("phonepage",phonepage);
	//    map.put("emailpage", emailpage);
		return map;
		
	}
	
	public static Map<String,Object> Email() throws Exception{//获取邮箱找回密码页面信息
	  Map <String ,Object> map= new liubinxuexin2().Letter();
	    HtmlPage emailpage=	(HtmlPage) map.get("emailpage");
	    WebClient webclient=(WebClient) map.get("webclient");
	    HtmlSelect selectUsername= (HtmlSelect) emailpage.getElementById("username");
	    System.out.println(selectUsername.getOptionSize());
	    Map <String,Object> selectMap=new HashMap<String, Object>();
	    for (int i = 0; i < selectUsername.getOptionSize(); i++) {
			  selectMap.put("selectUsername"+i, selectUsername.getOption(i).asText());
			  System.out.println(selectUsername.getOption(i).asText());
		}
		return selectMap;
		
	}
	public static Map<String,Object> SetEmail() throws Exception{//通过邮箱进行密码找回
	
		String realName="刘彬";//真实姓名
		String papersNum="610424199309067615";//证件号
		int selectint=1;
		Map <String ,Object> map= new liubinxuexin2().Letter();
		HtmlPage emailpage=	(HtmlPage) map.get("emailpage");
	    WebClient webclient=(WebClient) map.get("webclient");
		System.out.println("-------------------------------------------------------------------");
	    HtmlInput realNameInput=(HtmlInput) emailpage.getElementById("xm");
	    HtmlInput paperNumInput=(HtmlInput) emailpage.getElementById("sfzh");
	    HtmlInput  ctoken=emailpage.getElementByName("ctoken");//每次的都是会变的
	    realNameInput.setValueAttribute(realName);
	    paperNumInput.setValueAttribute(papersNum);
	    HtmlSelect selectUserName= (HtmlSelect) emailpage.getElementById("username");
	    selectUserName.setSelectedIndex(selectint);
	    System.out.println(paperNumInput.asXml());
	    System.out.println(selectUserName.getDefaultValue());
	    System.out.println(realNameInput.asXml());
	    HtmlButtonInput newButton= (HtmlButtonInput) emailpage.getElementById("newbutton");//获得确定按钮

	    HtmlPage sendEmail=newButton.click();
	    Thread.sleep(2000);
	//    HtmlForm  user_retrivePsd_form=  (HtmlForm) emailpage.getElementById("user_retrivePsd_form");
	  
	  //  System.out.println(user_retrivePsd_form.asXml());
	    //HtmlPage sendEmail= user_retrivePsd_form.click();
	    // HtmlPage sendEmail= (HtmlPage) emailpage.executeJavaScript("$('#user_retrivePsd_form').submit();").getNewPage();
	   System.out.println(sendEmail.asXml());
		return null;
		
	}
	public static Map<String,Object> phoneImage() throws Exception{//获取手机找回密码页面的验证码
		Map <String ,Object> map= new liubinxuexin2().Letter();
		String ImageUrl="https://account.chsi.com.cn/account/captchimagecreateaction.action?time=1503488613887";
		WebClient webclient=(WebClient) map.get("webclient");
		HtmlPage phonePage=(HtmlPage) map.get("phonepage");
		UnexpectedPage Imagepage=  webclient.getPage(ImageUrl);
		BufferedImage bufferedImage  = ImageIO.read(Imagepage.getInputStream());
	    Date data=	new Date();
	    String time=new SimpleDateFormat("yyyyMMddhhmmssSSS").format(data);
		String ImageName="CHSIPHONE"+time ;
		ImageIO.write(bufferedImage , "png", new File("D://CHSIFINDPASSWORD//PHONE"+ImageName+".png"));
		Scanner scanner=new Scanner(System.in);
		Map<String, Object> Image=new HashMap<String, Object>();
		Image.put("scanner", scanner);
		return Image;
	}
	public static Map<String,Object>phonePage() throws Exception{//手机找回页面的信息
		Map<String,Object>Image=new liubinxuexin2().phoneImage();
		String Imagetxt=(String) Image.get("scanner");
		String UserName="";
		String UserCard="";
		String UserPhone="";
		Map<String,Object>map=new liubinxuexin2().Letter();
		WebClient webclient=(WebClient) map.get("webclient");
		HtmlPage  phonePage=(HtmlPage) map.get("phonepage");
	    HtmlInput	capth= (HtmlInput) phonePage.getElementById("capth");//验证码信息
	    HtmlInput  mphone=(HtmlInput) phonePage.getElementById("mphone");//手机号码
	    HtmlInput  username=(HtmlInput) phonePage.getElementById("xm");//姓名
	    HtmlInput   usercard=(HtmlInput) phonePage.getElementById("sfzh");//证件号
	    capth.setValueAttribute(Imagetxt);
	    mphone.setValueAttribute(UserPhone);
	    username.setValueAttribute(UserName);
	    usercard.setValueAttribute(UserCard);
	    HtmlInput newbutton=(HtmlInput) phonePage.getElementById("newbutton");
	    newbutton.click();
	    
		
				
		return null;
		
	}
	public static Map<String,Object> answer(){
		
		
		return null;
		
	}
}
