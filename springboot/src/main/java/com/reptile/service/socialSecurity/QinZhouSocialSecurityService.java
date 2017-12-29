package com.reptile.service.socialSecurity;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 
 * @ClassName: QinZhouSocialSecurityService  
 * @Description: TODO (钦州社保)
 * @author: xuesongcui
 * @date 2017年12月29日  
 *
 */
@Service
public class QinZhouSocialSecurityService {
	private Logger logger= LoggerFactory.getLogger(QinZhouSocialSecurityService.class);
	
	private static String errorCode = "errorCode";
	private static String success = "0000";
	private static String loginTitle = "广西壮族自治区社会保险事业局网上查询系统";
	private static String viCodeStr = "错误的验证码！";
	private static String errorStr = "用户名不存在或密码错误！";
	private static String gsCode = "gxqz_query.vpq_jfmx_gs";
	private static String syCode = "gxqz_query.vpq_jfmx_sy";
	private static String accountCode = "gxqz_query.vpq_empgg_yilaccount";
	private static String nullCode = "null";
	
	@Autowired
	private application application;
	    
	
	/**
	 * 获取验证码图片
	 * @param request
	 * @return
	 */
	public Map<String, Object> doGetVerifyImg(HttpServletRequest request) {
		Map<String, Object> data = new HashMap<String, Object>(16);
		Map<String, Object> map = new HashMap<String, Object>(16);
		
		WebClient webClient = new WebClientFactory().getWebClient();
		try {
			HtmlPage loginPage = webClient.getPage("http://gx.si.gov.cn:8001/siweb/login.do?method=person#");
			Thread.sleep(500);
			//读取页面验证码图片到本地
			HtmlImage jcaptcha = (HtmlImage) loginPage.getElementById("jcaptcha");
			
			map.put("imagePath", ImgUtil.saveImg(jcaptcha, "qz", "/verifyImages", "png", request));
			data.put("data", map);
			request.getSession().setAttribute("qinZhouWebClient",webClient); 
			request.getSession().setAttribute("qinZhouLoginPage",loginPage); 
			data.put("errorInfo", "获取验证码成功");
            data.put("errorCode", "0000");
            
		} catch (Exception e) {
			logger.error("获取验证码失败！",e);
			data.put("errorInfo", "系统繁忙，请稍后再试！");
            data.put("errorCode", "0002");
		}finally {
            if (webClient != null) {
                webClient.close();
            }
        }
		return data;
	}
	
	/**
	 * 获取详情
	 * @param request
	 * @param idCard
	 * @param passWord
	 * @param catpy
	 * @param cityCode
	 * @return
	 */
	public Map<String, Object> doGetDetail(HttpServletRequest request,
			String idCard, String passWord, String catpy, String cityCode,String idCardNum) {
		Map<String, Object> data = new HashMap<String, Object>(16);
		//从session中获得webClient
		WebClient webClient = (WebClient)request.getSession().getAttribute("qinZhouWebClient");
		//从session中获得loginPage
		HtmlPage loginPage = (HtmlPage)request.getSession().getAttribute("qinZhouLoginPage");
		if(webClient == null || loginPage == null){
			data.put("errorInfo", "系统繁忙，请稍后再试！");
			data.put("errorCode", "0002");
			return data;
		}
		try {
			PushState.state(idCardNum, "socialSecurity", 100);
			//封装请求参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new NameValuePair("j_username", idCard.trim()));
			list.add(new NameValuePair("j_password",passWord.trim()));
			list.add(new NameValuePair("jcaptcha_response", catpy.trim()));
			
			String response  = this.webRequest("http://gx.si.gov.cn:8001/siweb/j_unieap_security_check.do?logtype=1", list, HttpMethod.POST, webClient);
			if(response.contains(loginTitle)){
				//养老保险
				List<Map<String,Object>> endowmentInsurance = this.getDetail(webClient,"gxqz_query.vpq_jfmx_yl","GRJFMX");
				//失业保险
				List<Map<String,Object>> unemploymentInsurance = this.getDetail(webClient,"gxqz_query.vpq_jfmx_shiy","GRJFMX");
				//医疗保险
				List<Map<String,Object>> medicalInsurance  = this.getDetail(webClient,"gxqz_query.vpq_jfmx_yil","GRJFMX");
				//工伤保险
				List<Map<String,Object>> accidentInsurance  = this.getDetail(webClient,"gxqz_query.vpq_jfmx_gs","VPQ");
				//生育保险
				List<Map<String,Object>> maternityInsurance   = this.getDetail(webClient,"gxqz_query.vpq_jfmx_sy","VPQ");
				//基本信息
				Map<String,Object> base = this.getBaseInfo(list, webClient, endowmentInsurance, unemploymentInsurance, medicalInsurance, accidentInsurance, maternityInsurance);
				Map<String,Object> info = new HashMap<String, Object>(16);
				info.put("personalInfo", base);
				//保险信息
				info.put("endowmentInsurance", endowmentInsurance);
				info.put("unemploymentInsurance", unemploymentInsurance);
				info.put("medicalInsurance", medicalInsurance);
				info.put("accidentInsurance", accidentInsurance);
				info.put("maternityInsurance", maternityInsurance);
				//data
				data.put("city", cityCode);
				data.put("cityName", "钦州");
				data.put("userId", idCardNum);
				data.put("createTime", Dates.currentTime());
				data.put("data", info);
				data = new Resttemplate().SendMessage(data,application.getSendip()+"/HSDC/person/socialSecurity");
				 if(data != null && success .equals(data.get(errorCode).toString())){
			          	PushState.state(idCardNum, "socialSecurity", 300);
			          	data.put("errorInfo","推送成功");
			          	data.put("errorCode","0000");
			          }else{
			          	PushState.state(idCardNum, "socialSecurity", 200);
			          	data.put("errorInfo","推送失败");
			          	data.put("errorCode","0001");
			          }
			}else{
				if(response.contains(viCodeStr)){
					data.put("errorInfo", "错误的验证码！");
		            data.put("errorCode", "0001");
				}else if(response.contains(errorStr)){
					data.put("errorInfo", "用户名不存在或密码错误！");
		            data.put("errorCode", "0001");
				}else{
					data.put("errorInfo", "系统繁忙，请稍后再试！");
		            data.put("errorCode", "0002");
				}
			}
		} catch (Exception e) {
			logger.warn("钦州社保登录失败",e);
			data.put("errorInfo", "系统繁忙，请稍后再试！");
            data.put("errorCode", "0002");
		}finally {
            if (webClient != null) {
                webClient.close();
            }
        }
		return data;
	}
	
	/**
	 * 获取社保基本信息并解析,并对各保险进行余额计算
	 * @param list
	 * @param webClient
	 * @param endowmentInsurance
	 * @param unemploymentInsurance
	 * @param medicalInsurance
	 * @param accidentInsurance
	 * @param maternityInsurance
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws IOException
	 */
	public Map<String,Object> getBaseInfo(List<NameValuePair> list,WebClient webClient,List<Map<String,Object>> endowmentInsurance,
			List<Map<String,Object>> unemploymentInsurance,List<Map<String,Object>> medicalInsurance,List<Map<String,Object>> accidentInsurance,
			List<Map<String,Object>> maternityInsurance) throws Exception{
		
		String response = this.getEveryYearInfo("gxqz_query.vpq_gg_info", webClient,0);;
		JSONObject baseJson = (JSONObject) JSONArray.fromObject(response).get(0);
		
		Map<String,Object> baseInfo = new HashMap<String, Object>(16);
		//姓名
		baseInfo.put("name", baseJson.get("VPQ_GG_INFO_AAC003"));
		//公民身份号码
		baseInfo.put("identityCards", baseJson.get("VPQ_GG_INFO_AAE135"));
		//性别
		baseInfo.put("sex", baseJson.get("VPQ_GG_INFO_AAC004"));
		//出生日期
		baseInfo.put("birthDate", baseJson.get("VPQ_GG_INFO_AAC006"));
		//民族
		baseInfo.put("nation", baseJson.get("VPQ_GG_INFO_AAC005"));
		//国家
		baseInfo.put("country", baseJson.get(""));
		//个人身份
		baseInfo.put("personalIdentity", baseJson.get("VPQ_GG_INFO_AAC012"));
		//参加工作时间
		baseInfo.put("workDate", baseJson.get("VPQ_GG_INFO_AAC007"));
		//户口性质
		baseInfo.put("residenceType", baseJson.get("VPQ_GG_INFO_AAC009"));
		//户口所在地地址
		baseInfo.put("residenceAddr", baseJson.get("VPQ_GG_INFO_AAC010"));
		//户口所在地邮政编码
		baseInfo.put("residencePostcodes", baseJson.get(""));
		//居住地(联系)地址
		baseInfo.put("contactAddress", baseJson.get("VPQ_GG_INFO_BAB304"));
		//居住地（联系）邮政编码
		baseInfo.put("contactPostcodes", baseJson.get(""));
		//获取对账单方式
		baseInfo.put("queryMethod", baseJson.get(""));
		//电子邮件地址
		baseInfo.put("email", baseJson.get("VPQ_GG_INFO_AAE159"));
		//文化程度
		baseInfo.put("educationalBackground", baseJson.get("VPQ_GG_INFO_AAC011"));
		//参保人电话
		baseInfo.put("telephone", baseJson.get("VPQ_GG_INFO_BAE017"));
		//参保人手机
		baseInfo.put("phoneNo", baseJson.get("VPQ_GG_INFO_BAE017"));
		//申报月均工资收入（元）
		baseInfo.put("income", baseJson.get(""));
		//证件类型
		baseInfo.put("documentType", baseJson.get("VPQ_GG_INFO_AAC058"));
		//证件号码
		baseInfo.put("documentNumber", baseJson.get("VPQ_GG_INFO_AAE135"));
		//委托代发银行名称
		baseInfo.put("bankName", baseJson.get(""));
		//委托代发银行账号
		baseInfo.put("bankNumber", baseJson.get(""));
		//缴费人员类别
		baseInfo.put("paymentPersonnelCategory", baseJson.get(""));
		//医疗参保人员类别
		baseInfo.put("insuredPersonCategory", baseJson.get(""));
		//离退休类别
		baseInfo.put("retireType", baseJson.get(""));
		//离退休日期
		baseInfo.put("retireDate", baseJson.get("VPQ_GG_INFO_AIC162"));
		//定点医疗机构 1
		baseInfo.put("sentinelMedicalInstitutions1", baseJson.get(""));
		//定点医疗机构 2
		baseInfo.put("sentinelMedicalInstitutions2", baseJson.get(""));
		//定点医疗机构 3
		baseInfo.put("sentinelMedicalInstitutions3", baseJson.get(""));
		//定点医疗机构 4
		baseInfo.put("sentinelMedicalInstitutions4", baseJson.get(""));
		//定点医疗机构 5
		baseInfo.put("sentinelMedicalInstitutions5", baseJson.get(""));
		//是否患有特殊病
		baseInfo.put("specialDisease", baseJson.get("VPQ_GG_INFO_AAC033"));
		
		
		double unemploymentInsuranceAmount = this.getCal(unemploymentInsurance);
		//失业保险缴费余额
		baseInfo.put("unemploymentInsuranceAmount", unemploymentInsuranceAmount);
		double endowmentInsuranceAmount = this.getCal(endowmentInsurance);
		//养老保险缴费余额
		baseInfo.put("endowmentInsuranceAmount", endowmentInsuranceAmount);
		double maternityInsuranceAmount = this.getCal(maternityInsurance);
		//生育保险缴费余额
		baseInfo.put("maternityInsuranceAmount", maternityInsuranceAmount);
		double accidentInsuranceAmount = this.getCal(accidentInsurance);
		//工伤保险缴费余额
		baseInfo.put("accidentInsuranceAmount", accidentInsuranceAmount);
		//医保余额
		String info = this.getEveryYearInfo("gxqz_query.vpq_empgg_yilaccount", webClient,(int)baseJson.get("VPQ_GG_INFO_AAC001"));
		Object obj = ((JSONObject)JSONArray.fromObject(info).get(0)).get("VPQ_EMPGG_YILACCOUNT_AKC089");
		double medicalInsuranceAmount = 0;
		if(obj != null){
			if(! nullCode.equals((obj + ""))){
				medicalInsuranceAmount  = Double.parseDouble(obj+"");
			}
		}
		//医疗保险缴费余额
		baseInfo.put("medicalInsuranceAmount", medicalInsuranceAmount);
		//总额
		double totalAmount = unemploymentInsuranceAmount + endowmentInsuranceAmount + maternityInsuranceAmount + accidentInsuranceAmount + medicalInsuranceAmount;
		baseInfo.put("totalAmount", totalAmount);
		
		return baseInfo;
	
	}
	
	
	/**
	 * 获取详情
	 * @param webClient
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String,Object>> getDetail(WebClient webClient,String type,String prefix) throws Exception{
		
		SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
		Calendar cal = Calendar.getInstance();
		
		Map<String, Object> map = new HashMap<String, Object>(16);
		//循环查到所有的保险信息
		boolean flag = true;
		while(flag){
			//结束时间
			String endTime = sim.format(cal.getTime()); 
			cal.set(Calendar.MONTH,0);
			 //开始时间
			String beginTime = sim.format(cal.getTime()); 
			String primary = this.getEveryYearInfo(beginTime, endTime,type,webClient);
			if(!primary.isEmpty() && !primary.equals("[]")){
				map.put(cal.get(Calendar.YEAR)+"", primary);
			}else{
				//如果当年信息为空，则结束循环
				flag = false; 
			}
			cal.add(Calendar.MONTH,-1);
		}
		List<Map<String,Object>> detailInfo = this.parseInsuranceInfo(map,prefix);
		return detailInfo;
	}
	
	
	
	/**
	 *以年为单位获取社保信息
	 * @param beginTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public String getEveryYearInfo(String beginTime, String endTime,String type,WebClient webClient) throws Exception{
		
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod("http://gx.si.gov.cn:8001/siweb/rpc.do?method=doQuery");
		String requestPayLoad = "";
		//请求参数拼接
		if(type.equals(gsCode) || type.equals(syCode)){
			requestPayLoad = "{header:{\"code\":0,\"message\":{\"title\":\"\",\"detail\":\"\"}},body:{dataStores:{\"\":{rowSet:{\"primary\":[],\"filter\":[],\"delete\":[]},name:\"\",pageNumber:1,pageSize:20,recordCount:0,rowSetName:\""+type+"\",condition:\"[VPQ_年月]>='"+beginTime+"' and [VPQ_年月]<='"+endTime+"'\"}},parameters:{\"synCount\":\"true\"}}}";
		}else{
			requestPayLoad = "{header:{\"code\":0,\"message\":{\"title\":\"\",\"detail\":\"\"}},body:{dataStores:{\"\":{rowSet:{\"primary\":[],\"filter\":[],\"delete\":[]},name:\"\",pageNumber:1,pageSize:20,recordCount:0,rowSetName:\""+type+"\",condition:\"GRJFMX_年月>='"+beginTime+"' and GRJFMX_年月<='"+endTime+"'\"}},parameters:{\"synCount\":\"true\"}}}";
		}
        RequestEntity entity = new StringRequestEntity(requestPayLoad, "text/html", "utf-8");
        post.setRequestEntity(entity);
        //获取Cookie
        Set<Cookie> cookies = webClient.getCookieManager().getCookies();
        String cok = cookies.toString();
        String substring = cok.substring(1, cok.indexOf(";"));
        post.setRequestHeader("Cookie", substring);
        
        client.executeMethod(post);
        //获取结果json串中的可以为primary的值
        String primary = JsonUtil.getJsonValue(post.getResponseBodyAsString(), "primary");
		return primary;
	}
	/**
	 *获取基本信息和医疗保险余额
	 * @param type
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getEveryYearInfo(String type,WebClient webClient,int id) throws Exception{
		
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod("http://gx.si.gov.cn:8001/siweb/rpc.do?method=doQuery");
		//请求参数拼接
		String requestPayLoad = "";
		if(type.equals(accountCode)){
			requestPayLoad = "{header:{\"code\":0,\"message\":{\"title\":\"\",\"detail\":\"\"}},body:{dataStores:{\"\":{rowSet:{\"primary\":[],\"filter\":[],\"delete\":[]},name:\"\",pageNumber:1,pageSize:20,recordCount:0,rowSetName:\""+type+"\",condition:\"[VPQ_EMPGG_YILACCOUNT_AAC001] ='"+id+"'\"}},parameters:{\"synCount\":\"true\"}}}";
		}else{
			requestPayLoad = "{header:{\"code\":0,\"message\":{\"title\":\"\",\"detail\":\"\"}},body:{dataStores:{\"\":{rowSet:{\"primary\":[],\"filter\":[],\"delete\":[]},name:\"\",pageNumber:1,pageSize:20,recordCount:0,rowSetName:\""+type+"\"}},parameters:{\"synCount\":\"true\"}}}";
		}
		RequestEntity entity = new StringRequestEntity(requestPayLoad, "text/html", "utf-8");
		post.setRequestEntity(entity);
		//获取Cookie
		Set<Cookie> cookies = webClient.getCookieManager().getCookies();
		String cok = cookies.toString();
		String substring = cok.substring(1, cok.indexOf(";"));
		post.setRequestHeader("Cookie", substring);
		
		client.executeMethod(post);
		//获取结果json串中的可以为primary的值
		String primary = JsonUtil.getJsonValue(post.getResponseBodyAsString(), "primary");
		return primary;
	}
	
	/**
	 * 解析社保信息
	 * @param map
	 * @return
	 */
	public List<Map<String,Object>> parseInsuranceInfo(Map<String, Object> map,String prefix){
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		Set<String> keys = map.keySet();
		for (String key : keys) {
			String str  = (String) map.get(key);
			if(!str.isEmpty()){
				JSONArray array = JSONArray.fromObject(str);
				int count = 0;
				Map<String, Object> item = new HashMap<String, Object>(16);
				for (int j = 0; j < array.size(); j++) {
					JSONObject json = (JSONObject)array.get(j);
					//缴费类型为正常缴费
					if(json.get(prefix+"_缴费类型") != null && json.get(prefix+"_缴费类型").equals("10")){
						//缴费标志为已实缴，count++
						if(json.get(prefix+"_缴费标志") != null && json.get(prefix+"_缴费标志").equals("1")){
							count++;
						}
						//年份
						item.put("year", key); 
						//月数 
						item.put("month_count", count);
						//公司名称
						item.put("company_name", json.get(prefix+"_单位名称"));
						//缴费基数
						item.put("base_number", json.get(prefix+"_缴费基数"));
						//单位缴存
						item.put("monthly_company_income", json.get(prefix+"_单位应缴金额"));
						//个人缴存
						item.put("monthly_personal_income", json.get(prefix+"_个人应缴金额"));
						
						if(json.get(prefix+"_缴费标志") != null && json.get(prefix+"_缴费标志").equals("0")){
							//缴费状态
							item.put("type", "欠费");
						}else if(json.get(prefix+"_缴费标志") != null && json.get(prefix+"_缴费标志").equals("1")){
							//缴费状态
							item.put("type", "缴存");
						}
						//单位缴存比例
						item.put("company_percentage", json.get(prefix+"_单位缴费比例"));
						//个人缴存比例
						item.put("personal_percentage", json.get(prefix+"_个人缴费比例"));
						//缴存日期
						item.put("last_pay_date", "");
					}
				}
				list.add(item);
			}
		}
		return list;
	}
	
	/**
	 * 根据保险详情计算保险余额
	 * @param insurance
	 * @return
	 */
	public double getCal(List<Map<String,Object>> insurance){
		double count = 0;
		for (int i = 0; i < insurance.size(); i++) {
			Map<String,Object> map = insurance.get(i);
			if(map.get("month_count") != null){
				int monthCount = (int)map.get("month_count");
				double monthlyCompanyIncome = 0;
				double monthlyPersonalIncome = 0;
				if(map.get("monthly_company_income") != null){
					if(!(map.get("monthly_company_income")+"").equals("null")){
						monthlyCompanyIncome = Double.parseDouble(map.get("monthly_company_income")+"");
					}
				}
				if(map.get("monthly_personal_income") != null){
					if(!(map.get("monthly_personal_income")+"").equals("null")){
						monthlyCompanyIncome = Double.parseDouble(map.get("monthly_personal_income")+"");
					}
				}
				count += monthCount * (monthlyCompanyIncome + monthlyPersonalIncome);
			}
		}
		return count;
	}
	
	
	/**
	 * 根据参数获取请求结果
	 * @param url 请求地址
	 * @param list 请求参数
	 * @param httpMethod 请求方式 get或post
	 * @param webClient 请求的webClient
	 * @return 请求结果
	 * @throws FailingHttpStatusCodeException
	 * @throws IOException
	 */
	public String webRequest(String url,List<NameValuePair> list,HttpMethod httpMethod,WebClient webClient) throws FailingHttpStatusCodeException, IOException{
		
		WebRequest webRequest = new WebRequest(new URL(url));
		
		webRequest.setRequestParameters(list);
		webRequest.setHttpMethod(httpMethod);
		
		HtmlPage page =	webClient.getPage(webRequest);
		String response = page.getWebResponse().getContentAsString();
		
		return response;
	}
	
	

	
}
