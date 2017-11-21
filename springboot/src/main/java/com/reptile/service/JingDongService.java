package com.reptile.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reptile.util.Dates;
import com.reptile.util.DriverUtil;
import com.reptile.util.ImgUtil;
import com.reptile.util.JsonUtil;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.SimpleHttpClient;
import com.reptile.util.application;
@Service
public class JingDongService {
	private Logger logger= LoggerFactory.getLogger(JingDongService.class);
	
	@Autowired
	private application application;
	
	/**
	 * 获取京东详细信息
	 * @param request
	 * @param userName
	 * @param passWord
	 * @param idCard
	 * @return
	 */
	public Map<String, Object> doGetDetail(HttpServletRequest request,
			String userName, String passWord,String idCard) {
		Map<String, Object> data = new HashMap<String, Object>();
		
		//添加谷歌驱动
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\iedriver\\chromedriver.exe");
        
        ChromeDriver driver = new ChromeDriver();
        try {
        	logger.warn("---------京东--------详情获取开始---------用户名：" + userName);
			//获取登录页面
			driver.get("https://passport.jd.com/new/login.aspx");
			//若出现账户登录
			if(DriverUtil.waitByClassName("login-form", driver, 15)){
				//使用账户登录
				driver.findElementByLinkText("账户登录").click();
				//用户名
				driver.findElementById("loginname").sendKeys(userName);
				//密码
				driver.findElementById("nloginpwd").sendKeys(passWord);
				//判断2秒内页面上是否有验证码
				if(DriverUtil.VisibilityById("o-authcode", driver, 2)){
					WebElement verifyImg = driver.findElementById("JD_Verification1");
					//验证码图片路径
					String path = request.getServletContext().getRealPath("/vecImageCode");
					String code = ImgUtil.saveImg(verifyImg, driver, path, "jd", "png");
					driver.findElementById("authcode").sendKeys(code);
				}
				//登录
				driver.findElementByLinkText("登    录").click();
				//判断msg-error是否会出现
				if(DriverUtil.VisibilityByClassName("msg-error", driver, 2)){
					
					String text = driver.findElementByClassName("msg-error").getText().replace("\n", "。");
					if(text.contains("验证码")){
						data.put("errorInfo", "系统繁忙，请稍后再试！");
						data.put("errorCode", "0002");
					}else{
						data.put("errorInfo", text);
						data.put("errorCode", "0001");
					}
					logger.warn("---------京东---------登录失败---------用户名：" + userName);
					
				}else if(DriverUtil.waitByLinkText("我的订单", driver, 6)){
					logger.warn("---------京东--------登录成功---------用户名：" + userName);
					
					String cookie = this.GetCookie(driver);
					
					Map<String,Object> map = new HashMap<String, Object>();
					map.put("purchaseRecord", this.getOrderInfo(cookie)); //购买记录
					map.put("shippingAddress", this.getAddressInfo(cookie)); //收获地址
					map.put("smallWhiteGrade", this.getScoreInfo(cookie)); //小白信用
					map.put("cardNumber", idCard); //身份证
					
					Map<String, Object> baiTiaoInfo = this.getBaiTiaoInfo(cookie); 
					map.put("whiteBarDedt", baiTiaoInfo.get("whiteBarDedt")); //白条额度
					map.put("whiteBarAmount", baiTiaoInfo.get("whiteBarAmount"));//白条欠款
					
					Map<String, Object> jinKuInfo = this.getJinKuInfo(cookie); 
					map.put("yesterdayEarnings", jinKuInfo.get("yesterdayEarnings")); //今日收益
					map.put("smallGoldAmount", jinKuInfo.get("smallGoldAmount"));//小金库额度
					
					data.put("data", map);
					logger.warn("---------京东--------详情获取成功---------用户名：" + userName);
					//数据推送
					data = new Resttemplate().SendMessage(data,application.getSendip()+"/HSDC/savings/eastOfBeijing");
					logger.warn("---------京东--------推送状态为---------" + data.toString());
					//状态推送
					if( map !=null && "0000".equals(map.get("ResultCode"))){
						PushState.state(idCard, "eastOfBeijing", 300);
	                    map.put("errorInfo","推送成功");
	                    map.put("errorCode","0000");
	                }else{
                	    PushState.state(idCard, "eastOfBeijing", 200);
	                    map.put("errorInfo","推送失败");
	                    map.put("errorCode","0001");
	                }
					
				}else{
					throw new Exception();
				}
			}else{
				throw new Exception();
			}
		} catch (Exception e) {
			logger.warn("-----------京东获取详情失败！--------------",e);
			data.put("errorInfo", "系统繁忙，请稍后再试！");
            data.put("errorCode", "0002");
		}finally{
			DriverUtil.close(driver);
		}
		return data;
	}

	
	
	
	/**
	 * 获取订单信息
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public List<Map<String,Object>> getOrderInfo(String cookie) throws ParseException, IOException{
		Map<String,String> headers = new HashMap<String, String>();
		headers.put("Cookie", cookie);//cookie加入请求头中
		//近两年的购买记录
		List<Map<String,Object>> info = new ArrayList<Map<String,Object>>();
		//请求参数
		Map<String,String> params = new HashMap<String, String>();
		//请求近一年内的购买记录
		int i = 1;
		boolean flag = true;
		while(flag){
			params.clear();
			params.put("page", i+"");
			params.put("d", "2");
			params.put("s", "4096");
			String result = this.request("https://order.jd.com/center/list.action",headers,params);
			
			List<Map<String,Object>> item = this.parseOrderInfo(result);//解析每一页的数据
			if(item != null && item.size() > 0){
				//循环将数据
				for (Map<String,Object> map : item) {
					info.add(map);
				}
			}else{
				flag = false;
			}
			i++;
		}
		
		//请求去年的购买记录
		flag = true;
		i = 1;
		while(flag){
			params.clear();
			params.put("page", i+"");
			params.put("d", Dates.beforeYear(1));
			params.put("s", "4096");
			String result = this.request("https://order.jd.com/center/list.action",headers,params);
			
			List<Map<String,Object>> item = this.parseOrderInfo(result);//解析每一页的数据
			if(item != null && item.size() > 0){
				//循环将数据
				for (Map<String,Object> map : item) {
					info.add(map);
				}
			}else{
				flag = false;
			}
			i++;
		}
		return info;
	}
	
	
	
	
	/**
	 * 获取白条信息(白条额度、白条欠款)
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public Map<String,Object> getBaiTiaoInfo(String cookie) throws ParseException, IOException{
		Map<String,String> headers = new HashMap<String, String>();
		headers.put("Cookie", cookie);//cookie加入请求头中
		headers.put("Host", "baitiao.jd.com");
		String result = SimpleHttpClient.post("https://baitiao.jd.com/v3/ious/getBtPrivilege", new HashMap<String, Object>(), headers);
		String isSuccess = JsonUtil.getJsonValue1(result, "isSuccess").toString();
		
		Map<String,Object> map = new HashMap<String, Object>();
		if(isSuccess.equals("true")){
			double whiteBarAmount = Double.parseDouble(JsonUtil.getJsonValue1(result, "creditLimit")+"");//白条额度
			double availableLimit = Double.parseDouble(JsonUtil.getJsonValue1(result, "availableLimit")+"");//可用额度
			//白条欠款 = 白条额度 - 可用额度
			double whiteBarDedt = whiteBarAmount - availableLimit;
			map.put("whiteBarAmount", whiteBarAmount);
			map.put("whiteBarDedt", whiteBarDedt);
		} 
		return map;
	}
	
	
	
	
	/**
	 * 获取小白信用分
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public String getScoreInfo(String cookie) throws ParseException, IOException{
		Map<String,String> headers = new HashMap<String, String>();
		headers.put("Cookie", cookie);//cookie加入请求头中
		headers.put("Host", "baitiao.jd.com");
		
		String result = SimpleHttpClient.post("https://baitiao.jd.com/v3/ious/score_getScoreInfo", new HashMap<String, Object>(), headers);
		return result;
	}
	
	
	
	
	/**
	 * 获取收货地址
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public List<String> getAddressInfo(String cookie) throws ParseException, IOException{
		Map<String,String> headers = new HashMap<String, String>();
		headers.put("Cookie", cookie);//cookie加入请求头中
		headers.put("Host", "easybuy.jd.com");
		headers.put("Referer", "https://easybuy.jd.com/address/getEasyBuyList.action");
		
		String result = this.request("https://easybuy.jd.com/address/getEasyBuyList.action",headers,null);
		return this.parseAddressInfo(result);
	}
	
	
	
	
	
	/**
	 * 获取小金库信息（小金库额度）昨日收益
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public Map<String,Object> getJinKuInfo(String cookie) throws ParseException, IOException{
		Map<String,String> headers = new HashMap<String, String>();
		headers.put("Cookie", cookie);//cookie加入请求头中
		headers.put("Host", "jinku.jd.com");
		String result = SimpleHttpClient.post("https://jinku.jd.com/xjk/account", new HashMap<String, Object>(), headers);
		
		Map<String,Object> map = new HashMap<String, Object>();
		if(!result.contains("\"accountResult\":null")){
			//小金库总额
			double total = Double.parseDouble(JsonUtil.getJsonValue1(result, "total")+"");
			map.put("smallGoldAmount",total);
			//今日收益
			double preIncome = Double.parseDouble(JsonUtil.getJsonValue1(result, "preIncome")+"");
			map.put("yesterdayEarnings", preIncome);
		}
		return map;
	}
	
	
	
	
	
	/**
	 * 获取请求
	 * @param url 地址
	 * @param headers 请求头 
	 * @param params 参数
	 * @throws ParseException
	 * @throws IOException
	 */
	public String request(String url,Map<String,String> headers,Map<String,String> params) throws ParseException, IOException{
		
		StringBuffer str = new StringBuffer();
		if( params != null ){
			for(Map.Entry<String, String> entry : params.entrySet()){
				if(!str.toString().isEmpty()){
					str.append("&");
				}
				str.append(entry.getKey());
				str.append("=");
				str.append(entry.getValue());
	        }
		}
		url = url + "?" + str.toString();
		String result = SimpleHttpClient.get(url,headers);
		return result;
	}
	

	
	/**
	 * 解析购买信息
	 * @param xml
	 * @return
	 */
	 private  List<Map<String,Object>>  parseOrderInfo(String xml){ 
		 
		 List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		 if(!xml.contains("没有下过订单")){
			 Document doc = Jsoup.parse(xml);
			 Elements tbodys = doc.select("table").select("tbody");  
			 
			 for (Element item : tbodys) {
				 
				 String text = item.text();
				 if(text.contains("订单金额")){
					 continue;
				 }else{
					 String status = item.getElementsByClass("status").get(0).getElementsByTag("span").get(0).text();
					 if(!status.isEmpty() && status.equals("已完成")){
						 Map<String,Object> map = new HashMap<String, Object>();
						 
						 String recordTime = item.getElementsByClass("dealtime").get(0).text();
						 String recordAmount = item.getElementsByClass("amount").get(0).getElementsByTag("span").get(0).text();
						 String recordRemark = item.getElementsByClass("amount").get(0).getElementsByTag("span").get(1).text();
						 
						 map.put("recordTime", recordTime);
						 map.put("recordAmount", recordAmount.substring(4));
						 map.put("recordRemark", recordRemark);
						 list.add(map);
					 }
				 }
			 }
		 }
		 
		return list;	
    }
	
	 
	 
	 
	 
	 /**
	  * 解析收货地址
	  * @param xml
	  * @return
	  */
	 private   List<String> parseAddressInfo(String xml){ 
		 
		 Document doc = Jsoup.parse(xml);
		 Elements div = doc.getElementsByClass("item-lcol"); 
		 
		 List<String> list = new ArrayList<String>();
		 for (Element itemLcol :  div) {
			 Elements divs = itemLcol.getElementsByClass("item"); 	
			 String address = "";
			 for (Element item : divs) {
				if(item.text().contains("所在地区") || item.text().contains("地址")){
					address += item.getElementsByClass("fl").get(0).text();
				}
			}
			 if(!address.isEmpty()){
				 list.add(address);
			 }
		 }
		 
		 return list;	
	 } 
	 
	 
	/**
	 * 判断页面是否有某个标签
	 * @param driver
	 * @param type id或者class，通过ID或者CLASS来判断
	 * @param eleName
	 * @return
	 */
	/*public static Boolean elementFlag(WebDriver driver,String type,String eleName){
		try {
			if(type.equals("id")){
				driver.findElement(By.id(eleName));
			}else if(type.equals("class")){
				driver.findElement(By.className(eleName));
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}*/
	
	
	/**
	 * 获取cookie
	 * @param driver
	 * @return
	 */
	public  String GetCookie(WebDriver driver)		{
		  //获得cookie用于发包
		Set<Cookie> cookies = driver.manage().getCookies();  
	    StringBuffer tmpcookies = new StringBuffer();

	   	for (Cookie cookie : cookies) {
	   		String name = cookie.getName();
	   		String value = cookie.getValue();
   			tmpcookies.append(name + "="+ value + ";");
		}
	   	String str = tmpcookies.toString();
	   	if(!str.isEmpty()){
	   		str = str.substring(0,str.lastIndexOf(";"));
	   	}
		return str; 	
	}
	
}
