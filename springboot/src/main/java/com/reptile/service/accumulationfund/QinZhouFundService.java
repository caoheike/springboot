package com.reptile.service.accumulationfund;

import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.reptile.model.NewAccumulation;
import com.reptile.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
/**
 *  * @author: 111
 */
@Service
public class QinZhouFundService {
	 private Logger logger= LoggerFactory.getLogger(QinZhouFundService.class);
	  
	  public Map<String, Object> getImageCode(HttpServletRequest request,String idCard,String passWord,String cityCode,String idCardNum){

		  Map<String,Object> dataMap=new HashMap<String, Object>(16);

			Map<String, Object> map = new HashMap<String, Object>(16);
			System.setProperty(ConstantInterface.chromeDriverKey,ConstantInterface.chromeDriverValue);

			ChromeOptions options = new ChromeOptions();
	        options.addArguments("start-maximized");
			WebDriver driver = new ChromeDriver(options);
			driver.get("http://wangting.qzsgjj.com/wt-web/grlogin");	
			driver.navigate().refresh();
			try {
				
         //===========图形验证==========================
			String path=request.getServletContext().getRealPath("/vecImageCode");
	        File file=new File(path);
	        if(!file.exists()){
	            file.mkdirs();
	        }
			  WebElement captchaImg = driver.findElement(By.id("captcha_img"));
		      File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		      //全屏截图
		      BufferedImage  fullImg = ImageIO.read(screenshot);
		      //坐标
		      Point point = captchaImg.getLocation();
		      //宽
		      int eleWidth = captchaImg.getSize().getWidth();
		      //高
		      int eleHeight = captchaImg.getSize().getHeight();
		      BufferedImage eleScreenshot= fullImg.getSubimage(point.getX(), point.getY(),
		    		  //图形验证码截图
		          eleWidth, eleHeight);
		      
		      String filename="qz"+System.currentTimeMillis()+".png";
		      ImageIO.write(eleScreenshot, "png", new File(file ,filename));
		      Thread.sleep(2000);
		    
		      //图片验证，打码平台
		     Map<String,Object> map1=MyCYDMDemo.Imagev(file + "/" +filename);
		     System.out.println(map1);
		     String catph= (String) map1.get("strResult");
		         WebElement userName=	 driver.findElement(By.id("username"));
		         Thread.sleep(100);
		         userName.sendKeys(idCard);
	        	WebElement password= driver.findElement(By.id("in_password"));
	        	 Thread.sleep(100);
	        	 password.sendKeys(passWord);
	        	WebElement captcha= driver.findElement(By.id("captcha"));
	        	 Thread.sleep(100);	
	        	captcha.sendKeys(catph);
	        	WebElement button=driver.findElement(By.id("gr_login"));
	        	button.click();
		        Thread.sleep(1000);
	        	 driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		         String huany="欢迎您";
	        	 if(driver.getPageSource().contains(huany)){
		    	  PushState.state(idCardNum, "accumulationFund",100);
		    	  //System.out.println("成功");
		    	 Thread.sleep(1000);
		    	  //new WebDriverWait(driver, 15).until(ExpectedConditions.presenceOfElementLocated(By.className("dk_more")));
		    	  logger.warn("钦州住房公积金登陆成功"); 
		    	  try{
		    	 // String grxx=driver.getPageSource().split("grzh=")[1].split(";")[0].split("'")[1];
		    	 
		    	  driver.findElement(By.className("dk_more")).click();
		    	  Thread.sleep(1000);
		    	  String mid=driver.getPageSource().split("jczqqccx")[1];
		    	  Thread.sleep(1000);
                  String paramers=mid.split("params=")[1].split("\\>")[0];
                  Thread.sleep(200);
                  String jsonString=paramers.substring(1, paramers.length()-1).replace("&quot;", "\"");
		    	  System.out.println(jsonString);
		    	  JSONObject json=new JSONObject().fromObject(jsonString);
		    	  String grxx=json.getString("grxx");
		    	  String ffbm=json.getString("ffbm");
		    	  String ywfl=json.getString("ywfl");
		    	  String ywlb=json.getString("ywlb");
		    	  String blqd=json.getString("blqd");
		    	  Set<Cookie> cookie=driver.manage().getCookies();
			 		 StringBuffer cookies=new StringBuffer();
			 		 for (Cookie c : cookie) {   
			 			 cookies.append(c.toString()+";");
			 		 } 

			 	 HttpClient client = new HttpClient(); 
		     	 PostMethod post=new PostMethod("http://wangting.qzsgjj.com/wt-web/jcr/jcrkhxxcx_mh.service");
		 	  	 post.addRequestHeader("Accept","*/*");	
		 	   	 post.addRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");		
		 	   	 post.addRequestHeader("Cookie",cookies.toString()); 	
		 	  	 
		 	     post.addRequestHeader("Origin","http://wangting.qzsgjj.com");		
		 	   	 post.addRequestHeader("Referer","http://wangting.qzsgjj.com/wt-web/home?logintype=1");
		 	     NameValuePair[] parameters={new NameValuePair("ffbm",ffbm),new NameValuePair("ywfl",ywfl),new NameValuePair("ywlb",ywlb),new NameValuePair("cxlx","01")};
		 	     post.addParameters(parameters);
		 	     client.executeMethod(post);
		 	     //基本信息
		 	     System.out.println(post.getResponseBodyAsString());
		 	    JSONObject basicInfos=new JSONObject().fromObject(post.getResponseBodyAsString());
		        Map<String, Object>	baseData= (Map<String, Object>) basicInfos.get("data");
		        
		        //用来存放基本信息
		        List<Object>  baseList=new ArrayList<Object>();
		        //用户姓名
		        baseList.add(baseData.get("xingming"));
		        //身份证号码
		        baseList.add(baseData.get("zjhm"));
		        //单位公积金账号
		        baseList.add(baseData.get("dwzh"));
		        //个人公积金账号
		        baseList.add(baseData.get("grzh"));
		        //公司名称
		        baseList.add(baseData.get("dwmc"));
		        //个人公积金卡号
		        baseList.add(baseData.get("grckzhhm"));
		        //缴费基数
		        baseList.add(baseData.get("grjcjs"));
		        //公司缴费比例
		        baseList.add(baseData.get("dwjcl"));
		        //个人缴费比例
		        baseList.add(baseData.get("grjcl"));
		        
		        
		        //个人缴费金额
		        baseList.add(baseData.get("gryjce"));
		        //公司缴费金额
		        baseList.add(baseData.get("dwyjce"));
		        //最后缴费日期
		        baseList.add(baseData.get("rzrq"));
		        //余额
		        baseList.add(baseData.get("grzhye"));
		        //状态
		        baseList.add(baseData.get("grzhzt"));
		      
		 		  String today=GetMonth.today1();
		 		  int year= Integer.parseInt(today.substring(0,4)); 
		 		  int month=Integer.parseInt(today.substring(5,7));
		 		    
		 		 
			 		GetMethod get = new GetMethod("http://wangting.qzsgjj.com/wt-web/jcr/jcrxxcxzhmxcx.service?ffbm="+ffbm+"&ywfl="+ywfl+"&ywlb="+ywlb+"&blqd="+blqd+"&ksrq="+(year-6)+"-01-01&jsrq="+today+"&grxx="+grxx+"&pageNum=1&page=1&startPage=0&pageSize=100&size=100&_="+System.currentTimeMillis());  
			 		get.addRequestHeader("Accept","*/*");
			 		get.addRequestHeader("Cookie",cookies.toString()); 
			 		get.addRequestHeader("Referer","http://wangting.qzsgjj.com/wt-web/home?logintype=1");
			        client.executeMethod(get);  
		 		
			        //流水详细信息
		 	    	System.out.println(get.getResponseBodyAsString());
		 	    	
		 	    	JSONObject flows=new JSONObject().fromObject(get.getResponseBodyAsString());
		 	    	//获取的流水详细信息
			         List<Map<String, Object>>	resultsData= (List<Map<String, Object>>) flows.get("results");
			         List<String> flowsName=new ArrayList<String>();
			         //业务描述
			         flowsName.add("czbz");
			         //操作时间
			         flowsName.add("jzrq");
			         //操作金额
			         flowsName.add("fse");
			         //操作类型
			         flowsName.add("gjhtqywlx");
			         //缴费月份
			         flowsName.add("jzrq");  
	 	    	     NewAccumulation accumulation=new NewAccumulation();
	 	    	     //基本信息
				      accumulation.setBasicInfos(baseList);
				    //  accumulation.setData(dataInfo);//推送信息
				      accumulation.setFlows(flowsName, resultsData);
				      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				      dataMap.put("basicInfos", accumulation.getBasicInfos());
				      dataMap.put("flows", accumulation.getFlows());
				     
				      dataMap.put("loans",accumulation.getLoans());
				      map.put("userId", idCardNum);
				      System.out.println(idCardNum);
				      map.put("insertTime", sdf.format(new Date()));
				      map.put("city", cityCode);
				      map.put("cityName", "钦州市");
				      map.put("data", dataMap);
				      System.out.println(new JSONArray().fromObject(map));
				      map=new Resttemplate().SendMessage(map,ConstantInterface.port+"/HSDC/person/accumulationFund");
				      //关闭网页
				      driver.findElements(By.className("hover_img")).get(2).click();
			     		try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			     		driver.findElement(By.name("logout_btn")).click();
				      String kkk="0000";
				      String errorCode="errorCode";
			     		if(map!=null&&kkk.equals(map.get(errorCode).toString())){
					    	PushState.state(idCardNum, "accumulationFund",300);
					    	map.put("errorInfo","查询成功");
					    	map.put("errorCode","0000");
				          
				        }else{
				        	//--------------------数据中心推送状态----------------------
				        	PushState.state(idCardNum, "accumulationFund",200);
				        	//---------------------数据中心推送状态----------------------
				        	
				            map.put("errorInfo","查询失败");
				            map.put("errorCode","0001");
				        	
				        } 
				   
		     	}catch(Exception e){
		     		PushState.state(idCardNum, "accumulationFund",200);
		     		logger.warn("钦州住房公积金",e);
		     		driver.findElements(By.className("hover_img")).get(2).click();
		     		Thread.sleep(300);
		     		driver.findElement(By.name("logout_btn")).click();
	         		map.put("errorCode", "0001");
	                map.put("errorInfo", "网络连接异常!");
					
					driver.close();
		   	   } 
		    
		      }else{
		    	  WebElement usernametip=	 driver.findElement(By.id("username_tip"));
		    	  String bu0="不";
		    	  String bu1="错";
		    	  String bu2="无";
		    	  boolean bool=usernametip.getText()!=null&&(usernametip.getText().contains(bu0)||usernametip.getText().contains(bu1)||usernametip.getText().contains(bu2));
		    	  if(bool){
		    			//PushState.state(idCardNum, "accumulationFund",200);
		    		  logger.warn("钦州住房公积金"+usernametip.getText());
		         		map.put("errorCode", "0001");
		                map.put("errorInfo", usernametip.getText()) ;
		                driver.close();
		                return map;
		    	  }else{
		    			//PushState.state(idCardNum, "accumulationFund",200);
		    		  WebElement pwdtip=	 driver.findElement(By.id("pwd_tip"));
		    		  boolean boole=pwdtip.getText()!=null&&(pwdtip.getText().contains(bu0)||pwdtip.getText().contains(bu1));
			    		 if (boole) {
			    			 logger.warn("钦州住房公积金"+pwdtip.getText());
				         		map.put("errorCode", "0001");
				                map.put("errorInfo", pwdtip.getText()) ;
				                driver.close();
				                return map;
						}else{
							 WebElement yzmtip=	 driver.findElement(By.id("yzm_tip"));
			    			 boolean boolea=yzmtip.getText()!=null&&(yzmtip.getText().contains(bu0)||yzmtip.getText().contains(bu1));
							 if(boolea){
			    				    logger.warn("钦州住房公积金 系统繁忙请稍后再试");
			    				  
					         		map.put("errorCode", "0001");
					                map.put("errorInfo", "系统繁忙请稍后再试") ;
					                driver.close();
					                return map;
			    			 }
			 			}
		    		   
			    			//PushState.state(idCardNum, "accumulationFund",200);
			    		
							    logger.warn("钦州住房公积金 该账号已在别处登陆");
			  				  
				         		map.put("errorCode", "0001");
				                map.put("errorInfo", "该账号已在别处登陆") ;
				                driver.close();
				                return map;
		    	  }
		    	  
		      }
	      Thread.sleep(2000);
		      
			} catch (Exception e) {
				//PushState.state(idCardNum, "accumulationFund",200);
				logger.warn("钦州住房公积金",e);
         		map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");	
				driver.close();
			}	
			
     		
	return map;	 
		  
	  }	  
		  
	  
 
	   /**
		 * 保存验证码图片,返回浏览器可访问到图片的地址
		 * @param htmlImg 图片流
		 * @param prefix  图片名称前缀
		 * @param verifyImagesPath 图片在项目中的相对路径 如：/verifyImages
		 * @param suffix 图片名称后缀 如png
		 * @param request 
		 * @return
		 * @throws IOException
		 */
		public static String saveImg(UnexpectedPage htmlImg,String prefix, String verifyImagesPath,String suffix,HttpServletRequest request) throws IOException{
		    String verifyImages =  request.getServletContext().getRealPath(verifyImagesPath);
			
            String path = request.getServletContext().getRealPath("/vecImageCode");
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String fileName = "Code" + System.currentTimeMillis() + ".png";
            BufferedImage bi = ImageIO.read(htmlImg.getInputStream());
            ImageIO.write(bi, "png", new File(file, fileName));
            
			return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/vecImageCode/" + fileName;
		}

}
