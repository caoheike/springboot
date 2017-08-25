package com.reptile.springboot;

import java.io.IOException;
import java.net.MalformedURLException;





import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class mail {

	private static final Logger LOGh = LoggerFactory.getLogger(mail.class);  
	
	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
	   boolean flg=true;
	do {
			WebClient client=new WebClient();
		    HtmlPage page= client.getPage("https://ssl.ptlogin2.qq.com/check?pt_tea=2&uin="+1121212159+"&appid=522005705&ptlang=2052&regmaster=&pt_uistyle=9&r="+System.currentTimeMillis()+"pt_jstoken=1515144655");
		    
		    String info=page.asText();
	        String[] infoarry=info.split(",");
	        String xx=infoarry[2].replace("'","");  
	        String code=infoarry[1].replace("'","");
	        String sess=infoarry[3].replace("'","");	
	        String vecode=infoarry[0].replace("'","");
	        if(!vecode.contains("1")){
	        	flg=false;
	        }
	        System.out.println(vecode);
	} while (flg);
	
//        if(vecode.contains("1")){
//        	final List collectedAlerts = new ArrayList();
//        	//cap_cd
//        	UnexpectedPage unexpectedPage= client.getPage("https://ssl.captcha.qq.com/getimage?uin=%s&aid=%s&cap_cd="+xx);
//        	 BufferedImage img=ImageIO.read(unexpectedPage.getInputStream());
//        	 ImageIO.write(img,"png", new File("E:/img/weizai.png"));
//        	System.out.println(xx);
//        	System.out.println("需要验证码");
//        	HtmlPage pages= client.getPage("https://ui.ptlogin2.qq.com/cgi-bin/login?style=9&appid=522005705&daid=4&s_url=https%3A%2F%2Fw.mail.qq.com%2Fcgi-bin%2Flogin%3Fvt%3Dpassport%26vm%3Dwsk%26delegate_url%3D%26f%3Dxhtml%26target%3D&hln_css=http%3A%2F%2Fmail.qq.com%2Fzh_CN%2Fhtmledition%2Fimages%2Flogo%2Fqqmail%2Fqqmail_logo_default_200h.png&low_login=1&hln_autologin=%E8%AE%B0%E4%BD%8F%E7%99%BB%E5%BD%95%E7%8A%B6%E6%80%81&pt_no_onekey=1");
//        	pages.executeJavaScript(" alert('encryptionPassword')");
//        	System.out.println(collectedAlerts.size());
//        }else{
//        	System.out.println("不需要");
//        }
        
	LOGh.info("asdas");
	}
}
