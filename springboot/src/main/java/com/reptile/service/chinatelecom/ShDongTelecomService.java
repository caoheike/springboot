package com.reptile.service.chinatelecom;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.eclipse.jetty.util.UrlEncoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.gson.JsonObject;
import com.reptile.util.ConstantInterface;
import com.reptile.util.GetMonth;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
/**
 * 山东电信发包
 * @author Administrator
 *
 */
@Service
public class ShDongTelecomService {
	private Logger logger = LoggerFactory.getLogger(ShDongTelecomService.class);
    private static String historyUrl="http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10016&toStUrl=http://sd.189.cn/selfservice/account/returnAuth?columnId=0210";
	private static String sendCodeUrl="http://sd.189.cn/selfservice/service/sendSms";
	private static String checkInfoUrl="http://sd.189.cn/selfservice/service/realnVali";
	private static String totalNumUrl="http://sd.189.cn/selfservice/bill/queryBillDetailNum";
	private static String queryDetailUrl="http://sd.189.cn/selfservice/bill/queryBillDetail";
	/**
	 * 获取图形验证码
	 * @param request
	 * @return
	 */
	public Map<String, Object> getImageCode(HttpServletRequest request) {
		logger.warn("---------------------山东电信获取图片验证码---------------------");
		Map<String, Object> map = new HashMap<String, Object>(16);
		Map<String, String> dataMap = new HashMap<String, String>(16);
		HttpSession session = request.getSession();
		Object attribute = session.getAttribute("GBmobile-webclient");
		if (attribute == null) {
			logger.warn("---------------------山东电信获取图片验证码，未进行前置操作---------------------");
			map.put("errorCode", "0001");
			map.put("errorInfo", "操作异常!");
		} else {
			try {
				WebClient webClient = (WebClient) attribute;
				//打开详单查询页面
				HtmlPage page = this.getPages(webClient, historyUrl, null, null, HttpMethod.GET);
				//Thread.sleep(2000);
				//获取图片验证码
			    dataMap=this.getImageCode(request, "/SDDXimageCode", "SD" + System.currentTimeMillis() + ".png", page, "rand_rn", dataMap);
				map.put("data", dataMap);
				map.put("errorCode", "0000");
				map.put("errorInfo", "验证码获取成功!");
				session.setAttribute("SDDXwebclient", webClient);
				session.setAttribute("SDDXhtmlPage", page);
				logger.warn("---------------------山东电信获取图片验证码成功---------------------");
			} catch (Exception e) {
				logger.warn("---------------------山东获取图片验证码异常------------------------", e);
				map.put("errorCode", "0001");
				map.put("errorInfo", "网络连接异常!");
			}
		}
		return map;
	}
	/**
	 * 获取短信验证码
	 * @param request
	 * @param imageCode 图形验证码
	 * @param phoneNum  手机号码
	 * @return
	 */
	public Map<String, Object> sendPhoneCode(HttpServletRequest request, String imageCode,String phoneNumber) {
		logger.warn("---------------------山东电信:"+phoneNumber+"发送短信验证码---------------------");
		Map<String, Object> map = new HashMap<String, Object>(16);
		HttpSession session = request.getSession();
		Object attribute = session.getAttribute("SDDXwebclient");
		Object htmlpage = session.getAttribute("SDDXhtmlPage");
		if (attribute == null || htmlpage == null) {
			logger.warn("---------------------山东电信："+phoneNumber+"发送短信验证码未进行前置操作---------------------");
			map.put("errorCode", "0001");
			map.put("errorInfo", "操作异常!");
			return map;
		} else {
			WebClient webClient = (WebClient) attribute;
			try {
				//发送短信验证码
				HttpClient httpClient=new HttpClient();
				map=this.sendCode(webClient, httpClient, sendCodeUrl, phoneNumber, imageCode, map);
				logger.warn("---------------------山东电信："+phoneNumber+map.get("errorInfo")+"---------------------");
				if (map.get("errorCode").equals("0000")) {
					//如果验证码发送成功，把webClient和httpClient存到session
					session.setAttribute("SD-sendCode-webClient", webClient);
					session.setAttribute("SD-sendCode-httpClient", httpClient);
				}
			} catch (Exception e) {
				logger.error("---------------------山东电信："+phoneNumber+"网络异常---------------------",e);
				map.put("errorCode", "0001");
				map.put("errorInfo", "网络异常!");
			}
		}
		return map;
	}
	
	/**
	 * 获取详单
	 * @param request
	 * @param phoneNumber 手机号
	 * @param imageCode 图形验证码
	 * @param userName 姓名
	 * @param userCard  身份证
	 * @param phoneCode 短信验证码
	 * @param servePwd  服务密码
	 * @param longitude  经度
	 * @param latitude  纬度
	 * @param uuid
	 * @return
	 */
	public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String imageCode,
			String userName, String userCard, String phoneCode, String servePwd, String longitude, String latitude,
			String uuid) {
		List<String> list=new ArrayList<>();
		Map<String, Object> map = new HashMap<String, Object>(16);
		Map<String, Object> dataMap = new HashMap<String, Object>(16);
		HttpSession session = request.getSession();
		Object attribute = session.getAttribute("SD-sendCode-webClient");
		Object attribute1 = session.getAttribute("SD-sendCode-httpClient");

		if (attribute == null || attribute1 == null) {
			logger.warn( "---------------------山东电信:"+phoneNumber+"获取详单,未进行前置操作---------------------");
			map.put("errorCode", "0001");
			map.put("errorInfo", "操作异常!");
			PushState.state(phoneNumber, "callLog", 200, "登录失败，操作异常!");
			PushSocket.pushnew(map, uuid, "3000", "登录失败，操作异常!");
			return map;
		} else {
				WebClient webClient = (WebClient) attribute;
				HttpClient httpClient=(HttpClient) attribute1;
				logger.warn("---------------------山东电信:"+phoneNumber+"二次身份认证中...---------------------");			
				try {
					//二次校验
					map=this.checkInfo(webClient, httpClient, checkInfoUrl, phoneNumber, imageCode, phoneCode, userCard, userName, map);	
					if (map.get("errorCode").equals("0000")) {
						//校验成功
						logger.warn("---------------------山东电信:"+phoneNumber+"二次身份校验成功！---------------------");
						//获取详单
						logger.warn("---------------------山东电信:"+phoneNumber+"详单获取中...---------------------");
						list=this.getDetails(webClient,httpClient, phoneNumber, list);
						if (list!=null) {
							logger.warn("---------------------山东电信:"+phoneNumber+"详单获取成功,详单："+list+"---------------------");
							//解析详单
							logger.warn("-------------------山东电信:"+phoneNumber+"解析中----------------------");
							dataMap=this.parseDetail(list, phoneNumber, servePwd, longitude, latitude);
							logger.warn("-------------------山东电信解析完成----------------------");
							logger.warn("--------------解析后的数据---------------"+dataMap.toString()+"-------------------");
							//关闭webClient
							webClient.close();
							//推送
							map.clear();
							map=this.pushData(dataMap, uuid, phoneNumber, logger);
						}else {
							//没有详单
							logger.warn( "---------------------山东电信:"+phoneNumber+"没有清单---------------------");
							map.put("errorCode", "0001");
							map.put("errorInfo", "无清单可查");
						}
					}else {
						//校验失败
						logger.warn("---------------------山东电信:"+phoneNumber+"二次身份认证失败，失败原因："+map.get("errorInfo")+"---------------------");
					    map.put(map.get("errorInfo").toString(), map.get("errorInfo").toString()+"请刷新图行验证码，并重新获取短信验证码");
						return map;
					}
				} catch (Exception e) {
					logger.error("---------------------山东电信:"+phoneNumber+"---------------------",e);
					map.put("errorCode", "0001");
					map.put("errorInfo", "网络异常!");
				}
		}
		return map;
	}
	 /***
     * 获得HtmlPage
     * @param webClient
     * @param url
     * @param list  参数
     * @param  header  请求头
     * @param method post或者get
     * @return HtmlPage
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    public HtmlPage getPages(WebClient webClient, String url, List<NameValuePair> list, Map<String, String>  header,HttpMethod method) throws FailingHttpStatusCodeException, IOException, InterruptedException {
         if (header!=null) {
        	 for (Map.Entry<String, String> entry : header.entrySet()) {
        		    System.out.println(entry.getKey() + ":" + entry.getValue());
        		    webClient.addRequestHeader(entry.getKey(), entry.getValue());
        		}
		  }
        WebRequest requests = new WebRequest(new URL(url));
        if (list!=null) {
        	requests.setRequestParameters(list);
		 }
        requests.setHttpMethod(method);
        HtmlPage page = webClient.getPage(requests);
        return page;
    }	
    /**
     * 获取图形验证码，返回存放路径
     * @param request
     * @param filePath 文件夹
     * @param fileName 图片名
     * @param page  含有图形验证码的页面
     * @param imageId  图形验证码id
     * @param dataMap  存放图片路径
     * @return
     * @throws IOException
     */
	public Map<String, String> getImageCode(HttpServletRequest request,String filePath,String fileName,HtmlPage page,String imageId,Map<String, String> dataMap) throws IOException{
		File file = new File(request.getServletContext().getRealPath(filePath));//获取项目路径
		if (!file.exists()) {
			file.mkdirs();
		}
		HtmlImage imageCode = (HtmlImage) page.getElementById(imageId);
		BufferedImage read = imageCode.getImageReader().read(0);
		ImageIO.write(read, "png", new File(file, fileName));
		dataMap.put("CodePath", request.getScheme() + "://" + request.getServerName() + ":"
				+ request.getServerPort() + filePath+"/" + fileName);
		return dataMap;
	}
	  /**
     * 获取cookies
     * @param webClient
     * @return
     */
    public String getCookies(WebClient webClient){
      Set<Cookie>  cookie=	webClient.getCookieManager().getCookies();
      StringBuffer cookies=new StringBuffer();
      for (Cookie c : cookie) {
    	  cookies.append(c.getName()+"="+c.getValue()+";");
	   }
   
	return cookies.toString();
    }
    

    /**
     * post请求
     * @param httpClient
     * @param url
     * @param params
     * @param cookies
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public  String post(HttpClient httpClient,String url, String params,Map<String, String> header) throws HttpException, IOException {
    	  PostMethod post = new PostMethod(url);
	      if (header!=null) {
	        	 for (Map.Entry<String, String> entry : header.entrySet()) {
	        		    //System.out.println(entry.getKey() + ":" + entry.getValue());
	        		    post.setRequestHeader(entry.getKey(), entry.getValue());
	        		}
			  }
	      
	      RequestEntity entity = new StringRequestEntity(params, "text/html", "utf-8");
	      post.setRequestEntity(entity);
	      httpClient.executeMethod(post);
		return post.getResponseBodyAsString();
    }
   /**
    * 发送短信验证码
    * @param webClient 
    * @param httpClient
    * @param url 发送验证码url
    * @param phoneNumber 手机号码
    * @param imageCode 图形验证码
    * @param map 存放发送结果
    * @return
    * @throws HttpException
    * @throws IOException
    */
    public Map<String, Object> sendCode(WebClient webClient,HttpClient httpClient,String url ,String phoneNumber,String imageCode,Map<String, Object> map) throws HttpException, IOException{
    	  String cookies=getCookies(webClient);
    	  String params="{\"smsFlag\":\"real_2busi_validate\",\"orgInfo\":\""+phoneNumber+"\",\"valicode\":\""+imageCode+"\"}";
    	  Map<String,String> header=new HashMap<>();
    	  header.put("Cookie", cookies);
    	  header.put("Content-Type", "application/json");
    	  header.put("Host", "sd.189.cn");
    	  header.put("Origin", "http://sd.189.cn");
    	  header.put("Referer", "http://sd.189.cn/selfservice/bill?tag=monthlyDetail");
    	  String result= post(httpClient, url, params,header);
          	 if (result.equals("0")) {
					  map.put("errorCode", "0000");
		  			  map.put("errorInfo", "验证码发送成功!"); 
				  }else if (result.equals("2")) {
					  map.put("errorCode", "0001");
		  			  map.put("errorInfo", "随机密码在1个小时内只能使用20次，请稍后再试!"); 
				  }else if (result.equals("3")) {
					  map.put("errorCode", "0001");
		  			  map.put("errorInfo", "您输入的验证码错误,请刷新验证码!"); 
				  }else{
	                  map.put("errorCode", "0001");
	          	      map.put("errorInfo", "系统繁忙，请稍后再试!"); 
				  }  
		return map;
    }
    /**
     * 二次校验
     * @param webClient
     * @param httpClient
     * @param url
     * @param phoneNumber
     * @param imageCode
     * @param phonCode
     * @param userCard
     * @param userName
     * @param map
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public Map<String, Object> checkInfo(WebClient webClient,HttpClient httpClient,String url ,String phoneNumber,String imageCode,String phonCode,String userCard,String userName,Map<String, Object> map) throws HttpException, IOException{
    	String cookies=getCookies(webClient);
    	System.err.println("2个cityCode---------------"+cookies);
        String params="{\"username_2busi\":\""+UrlEncoded.encodeString(userName)+"\",\"credentials_type_2busi\":\"1\",\"credentials_no_2busi\":\""+userCard+"\",\"validatecode_2busi\":\""+imageCode+"\",\"randomcode_2busi\":\""+phonCode+"\",\"randomcode_flag\":\"0\",\"rid\":1,\"fid\":\"bill_monthlyDetail\"}";
        Map<String,String> header=new HashMap<>();
  	  header.put("Cookie", cookies);
  	  header.put("Content-Type", "application/json");
  	  header.put("Host", "sd.189.cn");
  	  header.put("Origin", "http://sd.189.cn");
  	  header.put("Referer", "http://sd.189.cn/selfservice/bill?tag=monthlyDetail");
        String result= post(httpClient, url, params,header);
         	 if (result==null) {
         		  map.put("errorCode", "0001");
          	      map.put("errorInfo", "系统繁忙，请稍后再试!"); 
			   }else {
				   JSONObject json=	JSONObject.fromObject(result);
				   //1 2  好像可直接查询
				 if (json.get("retnCode").toString().equals("0")) {
					 map.put("errorCode", "0000");
	          	     map.put("errorInfo", "客户信息校验成功!"); 
				}else if (json.get("retnCode").toString().equals("3")) {
					 map.put("errorCode", "0001");
	          	     map.put("errorInfo", "短信随机码和手机号码不匹配"); 
				}else if (json.get("retnCode").toString().equals("4")) {
					map.put("errorCode", "0001");
	          	     map.put("errorInfo", "短信随机码校验未通过"); 
				}else if (json.get("retnCode").toString().equals("5")) {
					map.put("errorCode", "0001");
	          	    map.put("errorInfo", "您的用户信息校验未通过，请确认后重新输入"); 
				}else if (json.get("retnCode").toString().equals("6")) {
					map.put("errorCode", "0001");
	          	    map.put("errorInfo", " 图片校验码校验未通过"); 
				}else if (json.get("retnCode").toString().equals("-1")) {
					map.put("errorCode", "0001");
	          	    map.put("errorInfo", "请求参数缺失，请确认"); 
				}else if (json.get("retnCode").toString().equals("-9")) {
					map.put("errorCode", "0001");
	          	    map.put("errorInfo", "非常抱歉，系统繁忙，请稍后再试"); 
				}
			   }
		return map;	
    }
 /**
  * 获取总页数
  * @param httpClient
  * @param phoneNumber 手机号
  * @param totalNumUrl  查询总页数url
  * @param params  参数
  * @return
  * @throws HttpException
  * @throws IOException
  */
  public String getTotalNumber(HttpClient httpClient,String phoneNumber,String totalNumUrl,String params,String cookies) throws HttpException, IOException{
	  Map<String ,String> header=new HashMap<>();
	  String result=  post(httpClient, totalNumUrl, params, setHeader(header, cookies));
	  if (result!=null&&!result.equals("")) {
		JSONObject json=JSONObject.fromObject(result);
		if (json.get("resultCode").equals("POR-0000")) {
			if (json.get("records").equals("0")) {
				result="对不起，没有查询到您的详单信息！";
			}else {
				result=json.get("records").toString();
			}
			
		}else {
			result="对不起，没有查询到您的详单信息！";
		}
		
	   }
	return result; 
  }
    /**
     * 获取详单
     * @param webClient
     * @param httpClient
     * @param phoneNumber
     * @param list
     * @return
     * @throws HttpException
     * @throws IOException
     * @throws InterruptedException 
     */
   public List<String> getDetails(WebClient webClient ,HttpClient httpClient,String phoneNumber,List<String> list) throws HttpException, IOException, InterruptedException {
	   //获取当前年月
	   Date date=new Date();
	   String month="";//月份
	   int nowYear=nowYearMonth(date)[0];
	   int nowMonth=nowYearMonth(date)[1];
	   //获取详单
	   for (int i = 0; i < 6; i++) {
		   month=GetMonth.beforMon(nowYear, nowMonth, i);
		   if (i==0) {
			   list= getCurrentDetail(webClient, httpClient, phoneNumber, list,month,"4");
		     }else {
		       list= getCurrentDetail(webClient, httpClient, phoneNumber, list,month,"6");
			} 
		   
	   }
	return list;
	
}
   /**
    * 获得当前年月
    * @param month 当前时间格式 yyyyMM
    * @return
    */
	public static int[] nowYearMonth(Date date){
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyyMM" );
		String result=sdf.format(date);
		int[] day=new int[2];
		day[0]=new Integer(result.substring(0, 4)) ;
		day[1]=new Integer(result.substring(4)) ;
		return day;
		
	}
	
	/**
	 * 获得当前月
	 * @param date 当前时间
	 * @return
	 */

	public static  String nowMonth(Date date){
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyyMM" );
		return sdf.format(date);
	}	
   /**
    * 获取当前月详单
    * @param webClient
    * @param httpClient
    * @param phoneNumber
    * @param list 
    * @param month
    * @param queryType 当前月时为4 历史月为6
    * @return
    * @throws HttpException
    * @throws IOException
 * @throws InterruptedException 
    */
   public List<String> getCurrentDetail(WebClient webClient ,HttpClient httpClient,String phoneNumber,List<String> list,String month,String queryType) throws HttpException, IOException, InterruptedException{
	   //获取cookies
	   String cookies=getCookies(webClient);
	   System.err.println("2个cityCode的："+cookies);
	   //获取当月详单总条数
	   JsonObject json = new JsonObject();
	   json.addProperty("accNbr", phoneNumber);
	   json.addProperty("billingCycle", month);
	   json.addProperty("ticketType", "0");
	   String num=getTotalNumber(httpClient, phoneNumber, totalNumUrl, json.toString(),cookies);
	   int rows=0;
	   try {
		   rows=new Integer(num);
	    } catch (Exception e) {
	    	System.out.println("没有清单");
	    	return list ;
	    }
	   //获取详单
	   list=getCurrentDetail(httpClient, phoneNumber, list, month, cookies, rows,queryType);
	return list; 
   }
   /**
    * 获取当月详单
    * @param httpClient
    * @param phoneNumber 手机号
    * @param list
    * @param month 当前月
    * @param cookies
    * @param rows
    * @param queryType 当前月时为4 历史月为6
    * @return
    * @throws HttpException
    * @throws IOException
 * @throws InterruptedException 
    */
   public List<String> getCurrentDetail(HttpClient httpClient,String phoneNumber,List<String> list,String month,String cookies,int rows,String queryType) throws HttpException, IOException, InterruptedException{
	   int totalPage=(int) Math.ceil(rows/(double)200);
	   Map<String ,String> header=new HashMap<>();
	   for (int i = 1; i < totalPage+1; i++) {
		   String params1="{\"accNbr\":\""+phoneNumber+"\",\"billingCycle\":\""+month+"\",\"pageRecords\":\"200\",\"pageNo\":\""+i+"\",\"qtype\":\"0\",\"totalPage\":\""+totalPage+"\",\"queryType\":\""+queryType+"\"}";
		   String result=post(httpClient, queryDetailUrl, params1,setHeader(header, cookies));
		   if (result!=null&&!result.equals("")) {
				JSONObject json1=JSONObject.fromObject(result);
				if (json1.get("resultCode").equals("POR-0000")) {
					 list.add(result);
				}else {
					System.out.println("没有清单");
			    	return list ;
				}
		   }else{
			   System.out.println("没有清单");
		    	return list ;
		   }
		   Thread.sleep(300);//为了不封ip
	    }
	   
	return list;
	   
   }
  
    /**
     * 设置请求头
     * @param header
     * @param cookies
     * @return
     */
   public Map<String ,String> setHeader(Map<String ,String> header,String cookies){
	      header.put("Accept", "application/json, text/javascript, */*");
		  header.put("Content-Type", "application/json");
		  header.put("Host", "sd.189.cn");
		  header.put("Origin", "http://sd.189.cn");
		  header.put("Referer", "http://sd.189.cn/selfservice/bill/");
		  header.put("Cookie", cookies);
		return header;
   }
   /**
    * 解析详单
    * @param list
    * @param phoneNumber 手机号
    * @param servePwd 服务密码
    * @param longitude 经度
    * @param latitude 纬度
    * @return
    */
   public  static Map<String ,Object> parseDetail(List<String> list,String phoneNumber,String servePwd, String longitude, String latitude){
	   Map<String ,Object> map=new HashMap<>();
	   List<Map<String, String>> data=new ArrayList<>();
	   for (String li:list) {
		JSONObject json=JSONObject.fromObject(li);
		 List<Map<String,String>> detail=(List<Map<String, String>>) json.get("items");
		 for (int i = 0; i < detail.size(); i++) {
			 Map<String, String>  row=new HashMap<>();
			 row.put("CallAddress", detail.get(i).get("position")); //CallAddress  通话号码归属地
			 row.put("CallTime", detail.get(i).get("startTime").substring(5)); // CallTime  呼叫时间
			 row.put("CallDuration", detail.get(i).get("duration")+"秒"); // CallDuration  呼叫时长
			 row.put("CallMoney",  detail.get(i).get("charge"));// CallMoney 呼叫费用
			 row.put("CallNumber", detail.get(i).get("billingNbr")); // CallNumber 通话号码
			 row.put("CallType", detail.get(i).get("eventType")); // CallType	  通话号码归属地类型
			 row.put("CallWay", detail.get(i).get("callType")); //CallWay 呼叫类型
			 data.add(row);
		   }
		 
	    }
	   map.put("phone",phoneNumber);//认证手机号
	   map.put("pwd", servePwd);//手机查询密码
	   map.put("longitude",longitude);//经度
	   map.put("latitude", latitude);//维度
	   map.put("data", data);//数据
	   return map;
   }
   
   
   /**
    * 推送
    * @param map 数据
    * @param uuid
    * @param phoneNumber  手机号码
    * @param logger
    * @return
    */
   	public Map<String, Object> pushData(Map<String, Object> map,String uuid,String phoneNumber, Logger logger){
   		Resttemplate resttemplate = new Resttemplate();
   		Map<String, Object> result=new HashMap<String, Object>();
   		result = resttemplate.SendMessage(map,ConstantInterface.port + "/HSDC/message/operator");
   		String str1 = "0000";
   		String erro = "errorCode";
   		if (result.get(erro).equals(str1)) {
   			logger.warn("------------------------山东电信"+phoneNumber+"，推送成功----------------------");
   			PushSocket.pushnew(result, uuid, "8000", "认证成功");
   			PushState.state(phoneNumber, "callLog", 300);
   		} else {
   			logger.warn("------------------------山东电信"+phoneNumber+"，推送成功----------------------");
   			PushSocket.pushnew(result, uuid, "9000", map.get("errorInfo").toString());
   			PushState.state(phoneNumber, "callLog", 200, result.get("errorInfo").toString());
   		}
   		return result;
   	}
   	
   	
//   	public static void main(String[] args) {
   	//解析测试
//   		List<String> list=new ArrayList<>();
//   		list.add("{\"resultMsg\":\"成功\",\"items\":[{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 09:47:39\",\"intf_startTime\":\"20180113094739\",\"duration\":\"45\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18678166661\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 10:49:53\",\"intf_startTime\":\"20180113104953\",\"duration\":\"121\",\"charge\":\"0.00\",\"callingNbr\":\"18678166661\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 11:48:54\",\"intf_startTime\":\"20180113114854\",\"duration\":\"14\",\"charge\":\"0.00\",\"callingNbr\":\"15169386296\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 12:18:58\",\"intf_startTime\":\"20180113121858\",\"duration\":\"45\",\"charge\":\"0.00\",\"callingNbr\":\"18560278358\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 12:20:58\",\"intf_startTime\":\"20180113122058\",\"duration\":\"71\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"13305335210\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 12:37:11\",\"intf_startTime\":\"20180113123711\",\"duration\":\"26\",\"charge\":\"0.00\",\"callingNbr\":\"18678166661\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 15:31:17\",\"intf_startTime\":\"20180113153117\",\"duration\":\"26\",\"charge\":\"0.00\",\"callingNbr\":\"18678166661\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 15:36:22\",\"intf_startTime\":\"20180113153622\",\"duration\":\"52\",\"charge\":\"0.00\",\"callingNbr\":\"18560278358\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 16:25:43\",\"intf_startTime\":\"20180113162543\",\"duration\":\"10\",\"charge\":\"0.00\",\"callingNbr\":\"18678166661\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"}],\"resultCode\":\"POR-0000\",\"isLastpage\":\"false\"}");
//   		list.add("{\"resultMsg\":\"成功\",\"items\":[{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 12:39:13\",\"intf_startTime\":\"20171214123913\",\"duration\":\"24\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18372222277\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 12:41:13\",\"intf_startTime\":\"20171214124113\",\"duration\":\"13\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"13053324546\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 12:42:51\",\"intf_startTime\":\"20171214124251\",\"duration\":\"157\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18678226663\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 12:45:47\",\"intf_startTime\":\"20171214124547\",\"duration\":\"395\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18372222277\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 13:23:05\",\"intf_startTime\":\"20171214132305\",\"duration\":\"11\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18678166661\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 13:29:52\",\"intf_startTime\":\"20171214132952\",\"duration\":\"20\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18678166661\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 14:24:43\",\"intf_startTime\":\"20171214142443\",\"duration\":\"84\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18372222277\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 14:35:13\",\"intf_startTime\":\"20171214143513\",\"duration\":\"5\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18560278358\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 15:25:04\",\"intf_startTime\":\"20171214152504\",\"duration\":\"33\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18678166661\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"}],\"resultCode\":\"POR-0000\",\"isLastpage\":\"false\"}");
//   		list.add("{\"resultMsg\":\"成功\",\"items\":[{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 12:41:14\",\"intf_startTime\":\"20171224124114\",\"duration\":\"54\",\"charge\":\"0.00\",\"callingNbr\":\"15550347777\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"���内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 12:48:55\",\"intf_startTime\":\"20171224124855\",\"duration\":\"430\",\"charge\":\"0.00\",\"callingNbr\":\"15550347777\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 13:11:23\",\"intf_startTime\":\"20171224131123\",\"duration\":\"117\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"13964410445\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 14:27:51\",\"intf_startTime\":\"20171224142751\",\"duration\":\"349\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15264388761\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 16:15:37\",\"intf_startTime\":\"20171224161537\",\"duration\":\"11\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18953384644\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 16:30:35\",\"intf_startTime\":\"20171224163035\",\"duration\":\"14\",\"charge\":\"0.00\",\"callingNbr\":\"18953384644\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 18:05:52\",\"intf_startTime\":\"20171224180552\",\"duration\":\"135\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18560278358\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 21:26:12\",\"intf_startTime\":\"20171224212612\",\"duration\":\"43\",\"charge\":\"0.00\",\"callingNbr\":\"17705334121\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 23:53:51\",\"intf_startTime\":\"20171224235351\",\"duration\":\"9\",\"charge\":\"0.00\",\"callingNbr\":\"18560278358\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"}],\"resultCode\":\"POR-0000\",\"isLastpage\":\"false\"}");
//   		list.add("{\"resultMsg\":\"成功\",\"items\":[{\"position\":\"淄博市\",\"startTime\":\"2017-11-15 18:46:34\",\"intf_startTime\":\"20171115184634\",\"duration\":\"56\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18553325606\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-11-15 19:01:12\",\"intf_startTime\":\"20171115190112\",\"duration\":\"165\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18553325606\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-11-15 22:17:00\",\"intf_startTime\":\"20171115221700\",\"duration\":\"473\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18653307456\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-11-16 07:34:49\",\"intf_startTime\":\"20171116073449\",\"duration\":\"30\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18953384644\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-11-16 07:53:20\",\"intf_startTime\":\"20171116075320\",\"duration\":\"29\",\"charge\":\"0.00\",\"callingNbr\":\"17705334121\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-11-16 09:47:40\",\"intf_startTime\":\"20171116094740\",\"duration\":\"61\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18606431018\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-11-16 09:53:57\",\"intf_startTime\":\"20171116095357\",\"duration\":\"30\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18678189456\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"}],\"resultCode\":\"POR-0000\",\"isLastpage\":\"false\"}");
//   		
//   		list.add("{\"resultMsg\":\"成功\",\"items\":[{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 09:47:39\",\"intf_startTime\":\"20180113094739\",\"duration\":\"45\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18678166661\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 10:49:53\",\"intf_startTime\":\"20180113104953\",\"duration\":\"121\",\"charge\":\"0.00\",\"callingNbr\":\"18678166661\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 11:48:54\",\"intf_startTime\":\"20180113114854\",\"duration\":\"14\",\"charge\":\"0.00\",\"callingNbr\":\"15169386296\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 12:18:58\",\"intf_startTime\":\"20180113121858\",\"duration\":\"45\",\"charge\":\"0.00\",\"callingNbr\":\"18560278358\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 12:20:58\",\"intf_startTime\":\"20180113122058\",\"duration\":\"71\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"13305335210\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 12:37:11\",\"intf_startTime\":\"20180113123711\",\"duration\":\"26\",\"charge\":\"0.00\",\"callingNbr\":\"18678166661\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 15:31:17\",\"intf_startTime\":\"20180113153117\",\"duration\":\"26\",\"charge\":\"0.00\",\"callingNbr\":\"18678166661\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 15:36:22\",\"intf_startTime\":\"20180113153622\",\"duration\":\"52\",\"charge\":\"0.00\",\"callingNbr\":\"18560278358\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2018-01-13 16:25:43\",\"intf_startTime\":\"20180113162543\",\"duration\":\"10\",\"charge\":\"0.00\",\"callingNbr\":\"18678166661\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"}],\"resultCode\":\"POR-0000\",\"isLastpage\":\"false\"}");
//   		list.add("{\"resultMsg\":\"成功\",\"items\":[{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 12:39:13\",\"intf_startTime\":\"20171214123913\",\"duration\":\"24\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18372222277\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 12:41:13\",\"intf_startTime\":\"20171214124113\",\"duration\":\"13\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"13053324546\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 12:42:51\",\"intf_startTime\":\"20171214124251\",\"duration\":\"157\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18678226663\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 12:45:47\",\"intf_startTime\":\"20171214124547\",\"duration\":\"395\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18372222277\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 13:23:05\",\"intf_startTime\":\"20171214132305\",\"duration\":\"11\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18678166661\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 13:29:52\",\"intf_startTime\":\"20171214132952\",\"duration\":\"20\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18678166661\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 14:24:43\",\"intf_startTime\":\"20171214142443\",\"duration\":\"84\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18372222277\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 14:35:13\",\"intf_startTime\":\"20171214143513\",\"duration\":\"5\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18560278358\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-14 15:25:04\",\"intf_startTime\":\"20171214152504\",\"duration\":\"33\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18678166661\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"}],\"resultCode\":\"POR-0000\",\"isLastpage\":\"false\"}");
//   		list.add("{\"resultMsg\":\"成功\",\"items\":[{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 12:41:14\",\"intf_startTime\":\"20171224124114\",\"duration\":\"54\",\"charge\":\"0.00\",\"callingNbr\":\"15550347777\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"���内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 12:48:55\",\"intf_startTime\":\"20171224124855\",\"duration\":\"430\",\"charge\":\"0.00\",\"callingNbr\":\"15550347777\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 13:11:23\",\"intf_startTime\":\"20171224131123\",\"duration\":\"117\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"13964410445\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 14:27:51\",\"intf_startTime\":\"20171224142751\",\"duration\":\"349\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15264388761\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 16:15:37\",\"intf_startTime\":\"20171224161537\",\"duration\":\"11\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18953384644\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 16:30:35\",\"intf_startTime\":\"20171224163035\",\"duration\":\"14\",\"charge\":\"0.00\",\"callingNbr\":\"18953384644\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 18:05:52\",\"intf_startTime\":\"20171224180552\",\"duration\":\"135\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18560278358\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 21:26:12\",\"intf_startTime\":\"20171224212612\",\"duration\":\"43\",\"charge\":\"0.00\",\"callingNbr\":\"17705334121\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-12-24 23:53:51\",\"intf_startTime\":\"20171224235351\",\"duration\":\"9\",\"charge\":\"0.00\",\"callingNbr\":\"18560278358\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"}],\"resultCode\":\"POR-0000\",\"isLastpage\":\"false\"}");
//   		list.add("{\"resultMsg\":\"成功\",\"items\":[{\"position\":\"淄博市\",\"startTime\":\"2017-11-15 18:46:34\",\"intf_startTime\":\"20171115184634\",\"duration\":\"56\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18553325606\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-11-15 19:01:12\",\"intf_startTime\":\"20171115190112\",\"duration\":\"165\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18553325606\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-11-15 22:17:00\",\"intf_startTime\":\"20171115221700\",\"duration\":\"473\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18653307456\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-11-16 07:34:49\",\"intf_startTime\":\"20171116073449\",\"duration\":\"30\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18953384644\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-11-16 07:53:20\",\"intf_startTime\":\"20171116075320\",\"duration\":\"29\",\"charge\":\"0.00\",\"callingNbr\":\"17705334121\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"15305330036\",\"eventType\":\"国内通话被叫\",\"callType\":\"被叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-11-16 09:47:40\",\"intf_startTime\":\"20171116094740\",\"duration\":\"61\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18606431018\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"},{\"position\":\"淄博市\",\"startTime\":\"2017-11-16 09:53:57\",\"intf_startTime\":\"20171116095357\",\"duration\":\"30\",\"charge\":\"0.00\",\"callingNbr\":\"15305330036\",\"intf_charge\":\"0\",\"billingNbr\":\"15305330036\",\"calledNbr\":\"18678189456\",\"eventType\":\"国内通话主叫\",\"callType\":\"主叫\"}],\"resultCode\":\"POR-0000\",\"isLastpage\":\"false\"}");
//   		
//   		Map<String ,Object> map=parseDetail(list, "15305330036", "511418", "145263", "123456");
//   		System.out.println(JSONObject.fromObject(map).toString());
//   		
//	}
}
