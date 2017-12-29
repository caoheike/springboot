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

import com.reptile.util.ConstantInterface;
import com.reptile.util.Dates;
import com.reptile.util.DriverUtil;
import com.reptile.util.ImgUtil;
import com.reptile.util.JsonUtil;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.SimpleHttpClient;
import com.reptile.util.application;

/**
 * 
 * @ClassName: JingDongService  
 * @Description: TODO (京东)
 * @author: xuesongcui
 * @date 2017年12月29日  
 *
 */
@Service
public class JingDongService {
	private Logger logger= LoggerFactory.getLogger(JingDongService.class);
	
	@Autowired
	private application application;
	
	
	private static String loginForm = "login-form";
	private static int waitFifteen = 15;
	private static int two = 2;
	private static int six = 6;
	private static String success = "0000";
	private static String resultCode = "ResultCode";
	private static String viCodeStr = "验证码";
	private static String authcode = "o-authcode";
	private static String msgError = "msg-error";
	private static String ownerOrder = "我的订单";
	private static String trueStr = "true";
	private static String totalCountStr = "totalCount";
	private static String totalPageStr = "totalPage";
	private static String pageCountStr = "pageCount";
	private static String noOrderStr = "没有下过订单";
	private static int pageSize = 10;
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
		Map<String, Object> data = new HashMap<String, Object>(16);
		
		//添加谷歌驱动
        System.setProperty(ConstantInterface.chromeDriverKey,ConstantInterface.chromeDriverValue);
        
        ChromeDriver driver = new ChromeDriver();
        try {
        	logger.warn("---------京东--------详情获取开始---------用户名：" + userName);
			//获取登录页面
			driver.get("https://passport.jd.com/new/login.aspx");
			//若出现账户登录
			if(DriverUtil.waitByClassName(loginForm, driver, waitFifteen)){
				//使用账户登录
				driver.findElementByLinkText("账户登录").click();
				//用户名
				driver.findElementById("loginname").sendKeys(userName);
				//密码
				driver.findElementById("nloginpwd").sendKeys(passWord);
				//判断2秒内页面上是否有验证码
				if(DriverUtil.visibilityById(authcode, driver, two)){
					WebElement verifyImg = driver.findElementById("JD_Verification1");
					//验证码图片路径
					String path = request.getServletContext().getRealPath("/vecImageCode");
					String code = ImgUtil.saveImg(verifyImg, driver, path, "jd", "png");
					driver.findElementById("authcode").sendKeys(code);
				}
				//登录
				driver.findElementByLinkText("登    录").click();
				//判断msg-error是否会出现
				if(DriverUtil.visibilityByClassName(msgError, driver, two)){
					
					String text = driver.findElementByClassName("msg-error").getText().replace("\n", "。");
					if(text.contains(viCodeStr)){
						data = doGetDetail(request, userName, passWord, idCard);
						return data;
					}else{
						data.put("errorInfo", text);
						data.put("errorCode", "0001");
					}
					logger.warn("---------京东---------登录失败---------用户名：" + userName);
					
				}else if(DriverUtil.waitByLinkText(ownerOrder, driver, six)){
					logger.warn("---------京东--------登录成功---------用户名：" + userName);
					
					String cookie = this.getCookie(driver);
					
					Map<String,Object> map = new HashMap<String, Object>(16);
					//购买记录
					map.put("purchaseRecord", this.getOrderInfo(cookie)); 
					//收获地址
					map.put("shippingAddress", this.getAddressInfo(cookie)); 
					//资产总览
					map.put("creditData", this.getBasicInfo(cookie)); 
					//白条信息
					map.put("baiTiaoInfo", this.getBaiTiaoInfo(cookie)); 
					//小金库
					map.put("jinKuInfo", this.getJinKuInfo(cookie)); 
					//金条明细
					map.put("jtDetailList", this.getJtDetailList(cookie)); 
					
					
				/*	map.put("purchaseRecord", this.getOrderInfo(cookie)); //购买记录
					map.put("shippingAddress", this.getAddressInfo(cookie)); //收获地址
					map.put("smallWhiteGrade", this.getScoreInfo(cookie)); //小白信用
					map.put("cardNumber", idCard); //身份证
					
					Map<String, Object> baiTiaoInfo = this.getBaiTiaoInfo(cookie); 
					map.put("whiteBarDedt", baiTiaoInfo.get("whiteBarDedt")); //白条额度
					map.put("whiteBarAmount", baiTiaoInfo.get("whiteBarAmount"));//白条欠款
					
					Map<String, Object> jinKuInfo = this.getJinKuInfo(cookie); 
					map.put("yesterdayEarnings", jinKuInfo.get("yesterdayEarnings")); //今日收益
					map.put("smallGoldAmount", jinKuInfo.get("smallGoldAmount"));//小金库额度
				*/					
					data.put("data", map);
					logger.warn("---------京东--------详情获取成功---------用户名：" + userName);
					//数据推送
					data = new Resttemplate().SendMessage(data,application.getSendip()+"/HSDC/savings/eastOfBeijing");
					logger.warn("---------京东--------推送状态为---------" + data.toString());
					//状态推送
					if( data != null && success.equals(data.get(resultCode))){
						PushState.state(idCard, "eastOfBeijing", 300);
						data.put("errorInfo","推送成功");
						data.put("errorCode","0000");
	                }else{
                	    PushState.state(idCard, "eastOfBeijing", 200);
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
		Map<String,String> headers = new HashMap<String, String>(16);
		//cookie加入请求头中
		headers.put("Cookie", cookie);
		//近两年的购买记录
		List<Map<String,Object>> info = new ArrayList<Map<String,Object>>();
		//请求参数
		Map<String,String> params = new HashMap<String, String>(16);
		//请求近一年内的购买记录
		int i = 1;
		boolean flag = true;
		while(flag){
			params.clear();
			params.put("page", i+"");
			params.put("d", "2");
			params.put("s", "4096");
			String result = this.request("https://order.jd.com/center/list.action",headers,params);
			//解析每一页的数据
			List<Map<String,Object>> item = this.parseOrderInfo(result);
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
			//解析每一页的数据
			List<Map<String,Object>> item = this.parseOrderInfo(result);
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
	 * 获取京东白条信息
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public Map<String,Object> getBaiTiaoInfo(String cookie) throws ParseException, IOException{
		Map<String,Object> datas = new HashMap<String,Object>(16);
		//已出账单
		datas.put("billList", this.getBaiTiaoBillList(cookie));
		//未出账单
		datas.put("notOutAccount", this.getBaiTiaoNotOutAccount(cookie));
		//还款流水
		datas.put("billRepayment", this.getBaiTiaoBillRepayment(cookie));
		//退款记录
		datas.put("refundList", this.getBaiTiaoRefundList(cookie));
		//消费明细
		datas.put("billConsumeList", this.getBaiTiaoBillConsumeList(cookie));
		//白条信用分
		datas.put("scoreInfo", this.getBaiTiaoScoreInfo(cookie));
		return datas;
	}
	
		
	 /**
	 * 获取白条已出账单
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public List<String> getBaiTiaoBillList(String cookie) throws ParseException, IOException{
		//请求地址
		String url = "https://baitiao.jd.com/v3/ious/getBillList";
		
		Map<String,String> headers = new HashMap<String, String>(16);
		//cookie加入请求头中
		headers.put("Cookie", cookie);
		headers.put("Host", "baitiao.jd.com");
		
		//请求入参
		Map<String,Object> params = new HashMap<String, Object>(16);
		params.put("pageNum", "1");
		params.put("pageSize", pageSize+"");
		
		String result = SimpleHttpClient.post(url,params , headers);
		String isSuccess = JsonUtil.getJsonValue1(result, "isSuccess").toString();
		
		List<String> list = new ArrayList<String>();
		
		if(trueStr.equals(isSuccess)){
			
			list.add(result);
			
			if(JsonUtil.getJsonValue1(result,totalCountStr) != null){
				//获取账单总条数
				int totalCount = (int) JsonUtil.getJsonValue1(result, totalCountStr);
				//若账单总条数大于10，需要拿到其余几页的信息
				if(totalCount > pageSize){
					
					int totalPage = totalCount/pageSize + 1;
					
					for (int i = two; i <= totalPage; i++) {
						
						params.clear();
						params.put("pageNum", i);
						params.put("pageSize", pageSize + "");
						
						list.add(SimpleHttpClient.post(url,params , headers));
					}
				}
			}
		} 
		return list;
	}
	
	/**
	 * 获取白条未出账单
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public String getBaiTiaoNotOutAccount(String cookie) throws ParseException, IOException{
		//请求地址
		String url = "https://baitiao.jd.com/v3/ious/queryNotOutAccount";
		
		Map<String,String> headers = new HashMap<String, String>(16);
		//cookie加入请求头中
		headers.put("Cookie", cookie);
		headers.put("Host", "baitiao.jd.com");
		
		//请求入参
		Map<String,Object> params = new HashMap<String, Object>(16);
		
		String result = SimpleHttpClient.post(url,params , headers);
		return result;
	}
	
	/**
	 * 获取白条未出账单明细
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public String getBaiTiaoOrderDetail(String cookie) throws ParseException, IOException{
		//请求地址
		String url = "https://baitiao.jd.com/v3/ious/getBillOrderDetail";
		
		Map<String,String> headers = new HashMap<String, String>(16);
		//cookie加入请求头中
		headers.put("Cookie", cookie);
		headers.put("Host", "baitiao.jd.com");
		
		//请求入参
		Map<String,Object> params = new HashMap<String, Object>(16);
		params.put("billId", "");
		params.put("billType", "0");
		params.put("isNotAcount", "1");
		
		String result = SimpleHttpClient.post(url,params , headers);
		return result;
	}
	
	
	
	
	/**
	 * 获取白条还款流水
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public List<String> getBaiTiaoBillRepayment(String cookie) throws ParseException, IOException{
		//请求地址
		String url = "https://baitiao.jd.com/v3/ious/billRepayment";
		
		Map<String,String> headers = new HashMap<String, String>(16);
		//cookie加入请求头中
		headers.put("Cookie", cookie);
		headers.put("Host", "baitiao.jd.com");
		
		//请求入参
		Map<String,Object> params = new HashMap<String, Object>(16);
		params.put("pageNum", "1");
		params.put("pageSize", pageSize);
		
		String result = SimpleHttpClient.post(url,params , headers);
		String isSuccess = JsonUtil.getJsonValue1(result, "isSuccess").toString();
		
		List<String> list = new ArrayList<String>();
		
		if(trueStr.equals(isSuccess)){
			
			list.add(result);
			
			if(JsonUtil.getJsonValue1(result, totalPageStr) != null){
				//获取账单总条数
				int totalPage = (int) JsonUtil.getJsonValue1(result, totalPageStr);
				//若账单总条数大于10，需要拿到其余几页的信息
				if(totalPage > 1){
					
					for (int i = two; i <= totalPage; i++) {
						
						params.clear();
						params.put("pageNum", i);
						params.put("pageSize", pageSize+"");
						
						list.add(SimpleHttpClient.post(url , params , headers));
					}
				}
			}
		} 
		return list;
	}
	
	
	/**
	 * 获取白条退款记录
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public List<String> getBaiTiaoRefundList(String cookie) throws ParseException, IOException{
		//请求地址
		String url = "https://baitiao.jd.com/v3/ious/queryRefundList";
		
		Map<String,String> headers = new HashMap<String, String>(16);
		//cookie加入请求头中
		headers.put("Cookie", cookie);
		headers.put("Host", "baitiao.jd.com");
		
		//请求入参
		Map<String,Object> params = new HashMap<String, Object>(16);
		params.put("pageNum", "1");
		params.put("pageSize", pageSize+"");
		
		String result = SimpleHttpClient.post(url,params , headers);
		String isSuccess = JsonUtil.getJsonValue1(result, "isSuccess").toString();
		
		List<String> list = new ArrayList<String>();
		if(trueStr.equals(isSuccess)){
			
			list.add(result);
			
			if(JsonUtil.getJsonValue1(result, totalPageStr) != null){
				//获取账单总条数
				int totalPage = (int) JsonUtil.getJsonValue1(result, totalPageStr);
				//若账单总条数大于10，需要拿到其余几页的信息
				if(totalPage > 1){
					
					for (int i = two; i <= totalPage; i++) {
						
						params.clear();
						params.put("pageNum", i);
						params.put("pageSize", pageSize+"");
						
						list.add(SimpleHttpClient.post(url , params , headers));
					}
				}
			}
		}
		
		return list;
	}
	
	
	
	/**
	 * 获取白条消费明细
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public List<String> getBaiTiaoBillConsumeList(String cookie) throws ParseException, IOException{
		//请求地址
		String url = "https://baitiao.jd.com/v3/ious/billConsumeList";
		
		Map<String,String> headers = new HashMap<String, String>(16);
		//cookie加入请求头中
		headers.put("Cookie", cookie);
		headers.put("Host", "baitiao.jd.com");
		
		//请求入参
		Map<String,Object> params = new HashMap<String, Object>(16);
		params.put("pageNum", "1");
		params.put("pageSize", ""+pageSize);
		
		String result = SimpleHttpClient.post(url,params , headers);
		String isSuccess = JsonUtil.getJsonValue1(result, "isSuccess").toString();
		
		List<String> list = new ArrayList<String>();
		
		if(trueStr.equals(isSuccess)){
			
			list.add(result);
			
			if(JsonUtil.getJsonValue1(result, pageCountStr) != null){
				//获取账单总条数
				int pageCount = (int) JsonUtil.getJsonValue1(result, pageCountStr);
				//若账单总条数大于10，需要拿到其余几页的信息
				if(pageCount > 1){
					
					for (int i = two; i <= pageCount; i++) {
						
						params.clear();
						params.put("pageNum", i);
						params.put("pageSize", ""+pageSize);
						
						list.add(SimpleHttpClient.post(url , params , headers));
					}
				}
			}
		}
		
		return list;
	}
	
	
	
	
	
	/**
	 * 获取小白信用分
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public String getBaiTiaoScoreInfo(String cookie) throws ParseException, IOException{
		Map<String,String> headers = new HashMap<String, String>(16);
		//cookie加入请求头中
		headers.put("Cookie", cookie);
		headers.put("Host", "baitiao.jd.com");
		
		String result = SimpleHttpClient.post("https://baitiao.jd.com/v3/ious/score_getScoreInfo", new HashMap<String, Object>(16), headers);
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
		Map<String,String> headers = new HashMap<String, String>(16);
		//cookie加入请求头中
		headers.put("Cookie", cookie);
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
	public String getJinKuInfo(String cookie) throws ParseException, IOException{
		Map<String,String> headers = new HashMap<String, String>(16);
		//cookie加入请求头中
		headers.put("Cookie", cookie);
		headers.put("Host", "jinku.jd.com");
		String result = SimpleHttpClient.post("https://jinku.jd.com/xjk/account", new HashMap<String, Object>(16), headers);
		
	/*	Map<String,Object> map = new HashMap<String, Object>(16);
		if(!result.contains("\"accountResult\":null")){
			//小金库总额
			double total = Double.parseDouble(JsonUtil.getJsonValue1(result, "total")+"");
			map.put("smallGoldAmount",total);
			//今日收益
			double preIncome = Double.parseDouble(JsonUtil.getJsonValue1(result, "preIncome")+"");
			map.put("yesterdayEarnings", preIncome);
		}*/
		return result;
	}
	/**
	 * 获取金条明细
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public List<String> getJtDetailList(String cookie) throws ParseException, IOException{
		//请求地址
		String url = "https://baitiao.jd.com/v3/ious/getJtDetailList";
		
		Map<String,String> headers = new HashMap<String, String>(16);
		//cookie加入请求头中
		headers.put("Cookie", cookie);
		headers.put("Host", "baitiao.jd.com");
		
		//请求入参
		Map<String,Object> params = new HashMap<String, Object>(16);
		params.put("pageNum", "1");
		params.put("pageSize", "10");
		params.put("funCode", "ALL");
		
		String result = SimpleHttpClient.post(url,params , headers);
		String isSuccess = JsonUtil.getJsonValue1(result, "isSuccess").toString();
		
		List<String> list = new ArrayList<String>();
		
		if(trueStr.equals(isSuccess)){
			
			list.add(result);
			
			if(JsonUtil.getJsonValue1(result, pageCountStr) != null){
				//获取账单总条数
				int pageCount = (int) JsonUtil.getJsonValue1(result, pageCountStr);
				//若账单总条数大于10，需要拿到其余几页的信息
				if(pageCount > 1){
					
					for (int i = two; i <= pageCount; i++) {
						
						params.clear();
						params.put("pageNum", i);
						params.put("pageSize", "10");
						params.put("funCode", "ALL");
						
						list.add(SimpleHttpClient.post(url , params , headers));
					}
				}
			}
		}
		return list;
	}
	
	
	/**
	 * 获取白条基本信息和金条基本信息（totalDebt：总负债，creditLimit ：白条总额度，availableLimit：京东白条可用额度；
	 *	creditWaitPay：白条待还款，creditWaitPayPercent ：占负债比重，creditWaitPaySeven ：近七日还款）
	 * @param cookie
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public String getBasicInfo(String cookie) throws ParseException, IOException{
		Map<String,String> headers = new HashMap<String, String>(16);
		//cookie加入请求头中
		headers.put("Cookie", cookie);
		headers.put("Host", "trade.jr.jd.com");
		headers.put("Referer", "https://trade.jr.jd.com/centre/browse.action");
		String str = this.request("https://trade.jr.jd.com/async/creditData.action", headers, null);
		return str;
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
		 if(!xml.contains(noOrderStr)){
			 Document doc = Jsoup.parse(xml);
			 Elements tbodys = doc.select("table").select("tbody");  
			 
			 for (Element item : tbodys) {
				 
				 String text = item.text();
				 if(text.contains("订单金额")){
					 continue;
				 }else{
					 String status = item.getElementsByClass("status").get(0).getElementsByTag("span").get(0).text();
					 if(!status.isEmpty() && "已完成".equals(status)){
						 Map<String,Object> map = new HashMap<String, Object>(16);
						 
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
	public  String getCookie(WebDriver driver)		{
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
