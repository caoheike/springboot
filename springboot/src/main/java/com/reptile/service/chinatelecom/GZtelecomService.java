package com.reptile.service.chinatelecom;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.ConstantInterface;
import com.reptile.util.GetMonth;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
@Service
public class GZtelecomService {

	 private Logger logger= LoggerFactory.getLogger(GZtelecomService.class);
	 private static String checkUrl="http://www.189.cn//dqmh/ssoLink.do?method=linkTo&platNo=10024&toStUrl=http://service.gz.189.cn/web/query.php?action=call&fastcode=00320353&cityCode=gz";
	 private static String sendCodeUrl="http://service.gz.189.cn/web/query.php?action=postsms";
	 private static String checkCodeUrl="http://service.gz.189.cn/web/query.php?";
	/**
	 * 获取短信验证码
	 * @param request
	 * @return
	 */
	public  Map<String,Object> sendCode(HttpServletRequest request){
		 Map<String, Object> map = new HashMap<String, Object>(16); 
		 //从session中获得webClient
	     Object attribute = request.getSession().getAttribute("GBmobile-webclient");
	     WebClient webClient=(WebClient)attribute; 
	     if(webClient==null){
	    	 logger.warn("-----------贵州电信请先登录!--------------------");
	    	 map.put("errorCode", "0001");
			 map.put("errorInfo", "请先登录!");
			 return map;
	     }
	     HtmlPage page=null;
	    try {
	    	//打开查询详单页面
			page=webClient.getPage(checkUrl);
			Thread.sleep(1000);
			//发送短信验证码
			String isSendCode=this.sendCode(webClient, sendCodeUrl, null, HttpMethod.POST);
			if (isSendCode.equals("1")) {
				//验证码发送成功
				 logger.warn("-----------贵州电信，验证码发送成功--------------------");
				 map.put("errorCode", "0000");
				 map.put("errorInfo", "验证码发送成功");
				 request.getSession().setAttribute("GTelecomCode", webClient);
			}else if (isSendCode.contains("-")) {
				//发送次数过多，请稍后重试
				logger.warn("-----------贵州电信，验证码发送次数过多，请稍后重试--------------------");
				map.put("errorCode", "0001");
				map.put("errorInfo", "发送次数过多，请稍后重试");
			}
		} catch (Exception e) {
			logger.error("-----------贵州电信--------------------",e);
			 map.put("errorCode", "0001");
			 map.put("errorInfo", "网络异常");
			 return map;
		}
		return map;	
	}
	
	/**
	 * 
	 * @param request
	 * @param phoneNumber 手机号
	 * @param servePwd 服务密码
	 * @param code 短信验证码
	 * @param longitude 经度
	 * @param latitude 纬度
	 * @param uuid
	 * @return
	 */
	public Map<String,Object> getDetail(HttpServletRequest request,String phoneNumber,String servePwd,String code,String longitude,String latitude,String uuid) {
		    Map<String, Object> map = new HashMap<String, Object>(16);
		    Map<String, Object>  data=new HashMap<String, Object>();
		    PushState.state(phoneNumber, "callLog",100);
		    PushSocket.pushnew(map, uuid, "1000","登录中");
		    List<String> allDatas=new ArrayList<>();
		    //从session中获得webClient;
		    WebClient webClient=(WebClient)request.getSession().getAttribute("GTelecomCode");
	        if(webClient==null){
	        	 logger.warn("---------------------------贵州电信:"+phoneNumber+",请先获取验证码-------------------------");
		    	 map.put("errorCode", "0001");
			     map.put("errorInfo", "请先获取验证码");	
			     PushState.state(phoneNumber, "callLog",200,"请先获取验证码");
			     PushSocket.pushnew(map, uuid, "3000","请先获取验证码");
				 return map;
		    }
		    try {
		    	//验证码校验
				HtmlPage codeIsTrue=webClient.getPage(checkCodeUrl+"_="+System.currentTimeMillis()+"&action=getAllCall&QueryMonthly="+GetMonth.nowMonth()+"&QueryType=1&checkcode="+code);
				logger.warn("------------------------------贵州电信:"+phoneNumber+",验证码校验结果："+codeIsTrue.asText());
				if (codeIsTrue.asText().equals("-2")) {
			    	//验证码错误
			    	logger.warn("---------------------------贵州电信:"+phoneNumber+",验证码错误-------------------------");
			    	 map.put("errorCode", "0001");
				     map.put("errorInfo", "验证码错误");	
	 				 PushState.state(phoneNumber, "callLog",200,"验证码错误");
	 				 PushSocket.pushnew(map, uuid, "3000","验证码错误");
				     return map;
				}else if (codeIsTrue.asText().equals("-1")) {
					//请先获取验证码(官网js返回--)
					logger.warn("---------------------------贵州电信:"+phoneNumber+",请先获取验证码-------------------------");
			    	 map.put("errorCode", "0001");
				     map.put("errorInfo", "请先获取验证码");
				     PushState.state(phoneNumber, "callLog",200,"验证码错误");
	 				 PushSocket.pushnew(map, uuid, "3000","验证码错误");
				     return map;
				}else {
					//验证码正确
					if (codeIsTrue.asText().contains("CDMA_CALL_CDR")) {
						 PushSocket.pushnew(map, uuid, "5000","获取数据中");
						//所有详单
						allDatas=this.getAllDetail(webClient, checkCodeUrl, code, 6,phoneNumber, logger);
						System.out.println(allDatas.toString());
						//解析数据
						data=this.parseDetails(allDatas, data, phoneNumber, servePwd, longitude, latitude,logger);
						logger.warn("-------------------贵州电信解析完成----------------------");
						logger.warn("--------------解析后的数据---------------"+data.toString()+"-------------------");
						webClient.close();
						//推送数据
						Resttemplate resttemplate = new Resttemplate();
						data = resttemplate.SendMessage(data, ConstantInterface.port+"/HSDC/message/operator");
				 
				           String sss="0000";
				           String errorCode="errorCode";
				         
				           if(data.get(errorCode).equals(sss)){
				        	   logger.warn("------------贵州电信查询成功-----------------");
				        	   map.put("errorCode", "0000");
				        	   map.put("errorInfo", "查询成功");
				           }else {
				        	   logger.warn("------------贵州电信查询失败-----------------");
				        	   map.put("errorCode", "0001");
				        	   map.put("errorInfo", data.get("errorInfo"));
						   }
					}else {
						//未存在记录
						 logger.warn("---------------------------贵州电信:"+phoneNumber+",详单为空-------------------------");
				    	 map.put("errorCode", "0001");
					     map.put("errorInfo", "未存在通话记录");
					     PushState.state(phoneNumber, "callLog",200,"未存在通话记录");
			        	 PushSocket.pushnew(map, uuid, "9000","未存在通话记录");
					     return map;
					}
					
				}
		    }  catch (IOException e) {
		    	 logger.error("---------------------------贵州电信:"+phoneNumber+",请先获取验证码-------------------------",e);
		    	 map.put("errorCode", "0001");
			     map.put("errorInfo", "网络异常");
			     PushState.state(phoneNumber, "callLog",200,"网络连接异常");
    	         PushSocket.pushnew(map, uuid, "3000","网络连接异常");
				 return map;
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
        requests.setRequestParameters(list);
        requests.setHttpMethod(method);
        HtmlPage page = webClient.getPage(requests);
        Thread.sleep(2000);
        return page;
    }
    /**
     * 
     * @param webClient
     * @param url  发送验证码url
     * @param header 请求头
     * @param method  方法类型
     * @return  
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     * @throws InterruptedException
     */
   public String sendCode(WebClient webClient, String url,Map<String, String>  header,HttpMethod method) throws FailingHttpStatusCodeException, IOException, InterruptedException{
	     List<NameValuePair>  list=new ArrayList<NameValuePair>();
	    list.add(new NameValuePair("action", "postsms"));
	    HtmlPage isSendCode=  this.getPages(webClient,url, list,header, method);
	return isSendCode.asText();
	
   }
   /**
    * 查询所有详单
    * @param webClient
    * @param url  
    * @param code 验证码
    * @param num  月数
    * @param data 所有数据
    * @return
    * @throws IOException 
    * @throws MalformedURLException 
    * @throws FailingHttpStatusCodeException 
    */
   public List<String> getAllDetail(WebClient webClient,String url,String code,int num,String phoneNumber,Logger logger) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
	   List<String> allDatas=new ArrayList<>();
	   int[] yearAndMonth=GetMonth.nowYearMonth();
	   int year=yearAndMonth[0];
	   int month=yearAndMonth[1];
	   String detailMonth="";
	   for (int i = 0; i < num; i++) {
		  // Map<String, String> data=new HashMap<String, String>();
		   detailMonth=GetMonth.beforMon(year, month, i);
		   HtmlPage codeIsTrue=webClient.getPage(checkCodeUrl+"_="+System.currentTimeMillis()+"&action=getAllCall&QueryMonthly="+detailMonth+"&QueryType=1&checkcode="+code);
	       if (codeIsTrue.asText().contains("CDMA_CALL_CDR")) {
	    	   logger.warn("------------贵州电信:"+phoneNumber+"-----"+detailMonth+"月："+codeIsTrue.asText());
	    	   //data.put("item", codeIsTrue.asText());
	    	   allDatas.add(codeIsTrue.asText());
		   }
	      
	   }	
	return allDatas;   
   }

   /**
    * 解析通话详单
    * @param allDatas  获取的数据
    * @param data  存放解析后的数据
    * @param phone  手机号
    * @param pwd  服务密码
    * @param longitude  经度
    * @param latitude   纬度
    * @return
    */
   public Map<String, Object> parseDetails(List<String> allDatas,Map<String, Object>  data,String phone,String pwd,String longitude,String latitude,Logger logger) {
	   List<Map<String, String>> list=new ArrayList<Map<String,String>>();
	   logger.warn("-------------------贵州电信解析中....");
	   data.put("phone",phone);//认证手机号
	   data.put("pwd", pwd);//手机查询密码
	   data.put("longitude",longitude);//经度
	   data.put("latitude", latitude);//维度
	   for (int i = 0; i < allDatas.size(); i++) {
		   JSONObject json=new JSONObject().fromObject(allDatas.get(i));
		   List<Map<String, Object>> detail=  (List<Map<String, Object>>) json.get("CDMA_CALL_CDR");
		   for (int j = 0; j < detail.size(); j++) {
			   Map<String, Object> row= detail.get(i);
			   Map<String,String>  rowData=new HashMap<String, String>();
			   rowData.put("CallMoney", row.get("FEE2").toString());//呼叫费用    
			   if (row.get("CALLED_AREA").getClass().isInstance(String.class)) {
				  String address=row.get("CALLED_AREA").toString();
				   try {
					   rowData.put("CallAddress", getAddress(address));//通话号码归属地
				   } catch (IOException e) {
					
					   rowData.put("CallAddress", "");//通话号码归属地
				  }
			    }else {
			    	 rowData.put("CallAddress", "");//通话号码归属地
				}
			   rowData.put("CallType", row.get("NAME").toString());//通话号码归属地类型
			   rowData.put("CallTime", row.get("START_DATE").toString().substring(5));//呼叫时间
			   rowData.put("CallDuration", row.get("DURATION")+"秒");//呼叫时常
			   rowData.put("CallWay", row.get("CALLING_TYPE_NAME").toString());//呼叫类型
			   rowData.put("CallNumber", row.get("ORG_CALLED_NBR").toString());//通话号码
			   list.add(rowData);
		   }
	     }
	   data.put("data", list);
	   return data;
}
   /**
    * 根据区号获取地区
    * @param code
    * @return
    * @throws FailingHttpStatusCodeException
    * @throws MalformedURLException
    * @throws IOException
    */
   public  String getAddress(String code) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
	 WebClient client=new WebClientFactory().getWebClient();
	HtmlPage page= client.getPage("http://113.200.105.34:8065/code?code="+code);
	code= page.asText();
	client.close();
	return code;
 }
}
