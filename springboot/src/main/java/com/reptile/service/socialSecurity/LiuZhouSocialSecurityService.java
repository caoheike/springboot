package com.reptile.service.socialSecurity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.reptile.util.ConstantInterface;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;

@Service
public class LiuZhouSocialSecurityService {
	 private Logger logger = LoggerFactory.getLogger(LiuZhouSocialSecurityService.class);
	 @Autowired
	 private application applications;
	 private PushState PushState;
	 public Map<String,Object> loginImage(HttpServletRequest request){
		 WebClient webclient=new WebClientFactory().getWebClient();
			
		 Map<String,Object>map=new HashMap<String,Object>();
		 HttpSession session = request.getSession();
		 Map<String,Object> data=new HashMap<String,Object>();
		 	UnexpectedPage Imagepage;
			try {
				Imagepage = webclient.getPage("http://siquery.lzsrsj.com/siqg/VerifyCode.aspx?");
				BufferedImage bufferedImage  = ImageIO.read(Imagepage.getInputStream());
			 	String path=request.getSession().getServletContext().getRealPath("/upload") + "/";
			 	System.out.println(path);
				File file=new File(path);
				String findImage="lzsb"+System.currentTimeMillis()+".png";
				ImageIO.write(bufferedImage , "png", new File(file,findImage));
				System.out.println(file);
				session.setAttribute("sessionwebclient-lzsb", webclient);
				data.put("imagePath", "http://192.168.3.38:8080/upload/"+findImage);
	            map.put("errorCode", "0000");
	            map.put("errorInfo", "加载验证码成功");
	            map.put("data", data);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				 logger.warn("柳州社保 ", e);
		            map.put("errorCode", "0001");
		            map.put("errorInfo", "当前网络繁忙，请刷新后重试");
			}
		 	
			
		return map;
	 }
	 public Map<String,Object> getDeatilMes(HttpServletRequest request,String idCard,String catpy,String userName,String passWord,String cityCode,String idCardNum ){
		 Map<String, Object> map = new HashMap<>();
	        Map<String, Object> dataMap = new HashMap<>();
	        HttpSession session = request.getSession();
	        Object webclients = session.getAttribute("sessionwebclient-lzsb");
	        if (webclients != null ) {
	        	WebClient webclient=(WebClient) webclients;
	        	try {
	    			HtmlPage loginPage=  webclient.getPage("http://siquery.lzsrsj.com/siqg/grdl.aspx");
	    			HtmlTextInput sfzh= (HtmlTextInput) loginPage.getElementById("txtSfzh");
	    			sfzh.setValueAttribute(idCard);
	    		   HtmlTextInput xm=	(HtmlTextInput) loginPage.getElementById("txtXm");
	    		   xm.setValueAttribute(userName);
	    		   
	    		   HtmlPasswordInput Mm= (HtmlPasswordInput) loginPage.getElementById("txtMm");
	    		   Mm.setValueAttribute(passWord);
	    		   //
	    			HtmlTextInput Yzm=(HtmlTextInput) loginPage.getElementById("txtYzm2");
	    			Yzm.setValueAttribute(catpy);
	    			Thread.sleep(2000);
	    			HtmlPage nextPage= loginPage.getElementByName("btnLogin").click();
	    			Thread.sleep(3000);
	    			
	    			HtmlDivision jiben=(HtmlDivision) nextPage.getByXPath("//*[@id='content']/div[2]/div[2]/div[1]").get(0);
	    			Map<String,Object> data=new HashMap<String,Object>();
	    			data.put("base",jiben.asXml() );
	    			HtmlPage minxiPage= webclient.getPage("http://siquery.lzsrsj.com/SIQG/grjfmx.aspx");
	    			List<String> htmls =new ArrayList<String>();
	    			Date date=new Date();
	    			String year= new SimpleDateFormat("yyyy").format(date);
	    			int years=Integer.parseInt(year);
	    			for (int i = 0; i < 7; i++) {
	    				System.out.println(years);
//	    				List<String> html =new ArrayList<String>();
	    				
	    				HtmlTable xiandan= (HtmlTable) minxiPage.getElementById("ContentPlaceHolder1_grvGrjfmx");
	    				String aa=xiandan.asXml();
//	    				html.add(xiandan.asXml());
	    				Thread.sleep(100);
	    				
	    				HtmlDivision mun= 	(HtmlDivision) minxiPage.getElementById("ContentPlaceHolder1_anpPaging");
	    				System.out.println(minxiPage.asText());
	    				String yemian= minxiPage.asXml();
	    				boolean judge= yemian.contains("ContentPlaceHolder1_anpPaging");
	    				System.out.println(judge);
	    				Thread.sleep(100);
	    				if(!judge){
	    					  htmls.add(aa);
	    					break;
	    				} 
	    				String muns= mun.asText();
	    				String subStr = muns.substring(muns.indexOf("共")+1, muns.indexOf("页"));
	    				int num=Integer.parseInt(subStr);
	    				 minxiPage.getAnchorByText("下一页");
	    				HtmlAnchor a=  minxiPage.getAnchorByText("下一页");
	    				minxiPage= a.click();
	    				for (int j = 1; j < num; j++) {
	    					Thread.sleep(100);
	    					a= minxiPage.getAnchorByText("下一页");
	    					System.out.println("----------"+j+"----------");
	    					System.out.println(a.asText());
	    					xiandan= (HtmlTable) minxiPage.getElementById("ContentPlaceHolder1_grvGrjfmx");
//	    					html.add(xiandan.asXml());
	    					aa=aa+xiandan.asXml();
	    					minxiPage=a.click();
	    					Thread.sleep(2000);
	    				}
	    				System.out.println(years);
	    				years--;
	    				HtmlInput  fristyear= (HtmlInput) minxiPage.getElementById("ContentPlaceHolder1_txtKssj");
	    				fristyear.setValueAttribute(years+"-01");
	    			    HtmlInput Endyear=	(HtmlInput) minxiPage.getElementById("ContentPlaceHolder1_txtZzsj");
	    			    Endyear.setValueAttribute(years+"-12");
	    			    minxiPage=minxiPage.getElementById("ContentPlaceHolder1_btnQuery").click();
	    			    htmls.add( aa);
	    			    Thread.sleep(2000);
	    			    if(minxiPage.asText().contains("对不起，当前年月范围没有查询到您的缴费明细！"))break;
	    			}
	    		
	    			Map<String,Object> lz=new HashMap<String, Object>();
	    			data.put("item", htmls);
	    			lz.put("data", data);
	    			lz.put("city", cityCode);
	    			lz.put("userId", idCardNum);
	    			Resttemplate resttemplate = new Resttemplate();
	    			//map=resttemplate.SendMessage(lz,"http://192.168.3.16:8089/HSDC/person/socialSecurity");
	    			map=resttemplate.SendMessage(lz,ConstantInterface.port+"/HSDC/person/socialSecurity");
	    			
	    			System.out.println(map);
	    		} catch (Exception e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    			map.put("errorCode", "0001");
	 	            map.put("errorInfo", "获取失败");
	    		}
	        }else {
	            logger.warn("柳州社保登录过程中出错session异常 ");
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "非法操作！");
	        }
		 return map;
		 
		 
	 }
}
