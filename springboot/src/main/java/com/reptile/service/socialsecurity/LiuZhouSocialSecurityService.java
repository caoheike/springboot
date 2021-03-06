package com.reptile.service.socialsecurity;

import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 
 * @author liubin
 *
 */
@Service
public class LiuZhouSocialSecurityService {
	 private Logger logger = LoggerFactory.getLogger(LiuZhouSocialSecurityService.class);
	 @Autowired
	 private application applications;
	 public Map<String,Object> loginImage(HttpServletRequest request){
		 WebClient webclient=new WebClientFactory().getWebClient();
			
		 Map<String,Object>map=new HashMap<String,Object>(200);
		 HttpSession session = request.getSession();
		 Map<String,Object> data=new HashMap<String,Object>(200);
		 	UnexpectedPage imagepage;
			try {
				imagepage = webclient.getPage("http://siquery.lzsrsj.com/siqg/VerifyCode.aspx?");
				BufferedImage bufferedImage  = ImageIO.read(imagepage.getInputStream());
				String path=request.getServletContext().getRealPath("/liuzhouImger");
				File file=new File(path);
				if(!file.exists()){
					file.mkdirs();
				}
				
				String findImage="gd"+System.currentTimeMillis()+".png";
				ImageIO.write(bufferedImage , "png", new File(file,findImage));
				 session.setAttribute("sessionWebClient-Cab", webclient);
		       //  session.setAttribute("sessionLoginPage-Cab", loginPage);
				 System.out.println(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/liuzhouImger/"+findImage);
				data.put("imagePath",request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/liuzhouImger/"+findImage);
				session.setAttribute("sessionwebclient-lzsb", webclient);
	            map.put("errorCode", "0000");
	            map.put("errorInfo", "加载验证码成功");
	            map.put("data", data);
	            
			}  catch (Exception e) {
				e.printStackTrace();
				logger.warn("柳州社保 ", e);
		        map.put("errorCode", "0001");
		        map.put("errorInfo", "当前网络繁忙，请刷新后重试");
		        webclient.close();
			}
		 	
			
		return map;
	 }
	 public Map<String,Object> getDeatilMes(HttpServletRequest request,String idCard,String catpy,String userName,String passWord,String cityCode,String idCardNum ){
		 Map<String, Object> map = new HashMap<>(200);
	        Map<String, Object> dataMap = new HashMap<>(200);
	        HttpSession session = request.getSession();
	        Object webclients = session.getAttribute("sessionwebclient-lzsb");
	        WebClient webclient=(WebClient) webclients;
	        if (webclients != null ) {
	        	try {
	    			HtmlPage loginPage=  webclient.getPage("http://siquery.lzsrsj.com/siqg/grdl.aspx");
	    			HtmlTextInput sfzh= (HtmlTextInput) loginPage.getElementById("txtSfzh");
	    			sfzh.setValueAttribute(idCard);
	    		   HtmlTextInput xm=	(HtmlTextInput) loginPage.getElementById("txtXm");
	    		   xm.setValueAttribute(userName);
	    		   
	    		   HtmlPasswordInput txtMm= (HtmlPasswordInput) loginPage.getElementById("txtMm");
	    		   txtMm.setValueAttribute(passWord);
	    		   //
	    			HtmlTextInput txtYzm2=(HtmlTextInput) loginPage.getElementById("txtYzm2");
	    			txtYzm2.setValueAttribute(catpy);
	    			Thread.sleep(2000);
	    			HtmlPage nextPage= loginPage.getElementByName("btnLogin").click();
	    			Thread.sleep(4000);
	    			
	    			try{
	    				HtmlDivision jiben=(HtmlDivision) nextPage.getByXPath("//*[@id='content']/div[2]/div[2]/div[1]").get(0);
		    			Map<String,Object> data=new HashMap<String,Object>(200);
		    			data.put("base",jiben.asXml() );
		    			HtmlPage minxiPage= webclient.getPage("http://siquery.lzsrsj.com/SIQG/grjfmx.aspx");
		    			List<String> htmls =new ArrayList<String>();
		    			Date date=new Date();
		    			String year= new SimpleDateFormat("yyyy").format(date);
		    			int years=Integer.parseInt(year);
		    			int nums=7;
		    			for (int i = 0; i < nums; i++) {
		    				System.out.println(years);
//		    				List<String> html =new ArrayList<String>();
		    				
		    				HtmlTable xiandan= (HtmlTable) minxiPage.getElementById("ContentPlaceHolder1_grvGrjfmx");
		    				String aa=xiandan.asXml();
//		    				html.add(xiandan.asXml());
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
		    				PushState.state(idCardNum, "socialSecurity", 100);
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
//		    					html.add(xiandan.asXml());
		    					aa=aa+xiandan.asXml();
		    					minxiPage=a.click();
		    					Thread.sleep(2000);
		    				}
		    				System.out.println(years);
		    				years--;
		    				HtmlInput  fristyear= (HtmlInput) minxiPage.getElementById("ContentPlaceHolder1_txtKssj");
		    				fristyear.setValueAttribute(years+"-01");
		    			    HtmlInput endyear=	(HtmlInput) minxiPage.getElementById("ContentPlaceHolder1_txtZzsj");
		    			    endyear.setValueAttribute(years+"-12");
		    			    minxiPage=minxiPage.getElementById("ContentPlaceHolder1_btnQuery").click();
		    			    htmls.add( aa);
		    			    Thread.sleep(2000);
		    			    if(minxiPage.asText().contains("对不起，当前年月范围没有查询到您的缴费明细！")){
		    			    	break;
		    			    }
		    			}
		    		
		    			Map<String,Object> lz=new HashMap<String, Object>(200);
		    			data.put("item", htmls);
		    			lz.put("data", data);
		    			lz.put("city", cityCode);
		    			lz.put("userId", idCardNum);
		    			Resttemplate resttemplate = new Resttemplate();
		    			map=resttemplate.SendMessage(lz,applications.getSendip()+"/HSDC/person/socialSecurity");
		    			String errorCode = "errorCode";
						String state0 = "0000";
		    			if(map!=null&&state0.equals(map.get(errorCode).toString())){
		                	PushState.state(idCardNum, "socialSecurity", 300);
		                	map.put("errorInfo","推送成功");
		                	map.put("errorCode","0000");
		                }else{
		                	PushState.state(idCardNum, "socialSecurity", 200);
		                	webclient.close();
		                }
		    			System.out.println(map);
	    			}catch(Exception e){
	    				e.printStackTrace();
		    			map.put("errorCode", "0001");
		 	            map.put("errorInfo", "登录失败,请正确输入");
		 	            webclient.close();
	    			}
	    		} catch (Exception e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    			map.put("errorCode", "0001");
	 	            map.put("errorInfo", "获取失败");
	 	           webclient.close();
	    		}
	        }else {
	            logger.warn("柳州社保登录过程中出错session异常 ");
	            map.put("errorCode", "0001");
	            map.put("errorInfo", "非法操作！");
	            webclient.close();
	        }
		 return map;
		 
		 
	 }
}
