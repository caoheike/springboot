package com.reptile.service.socialSecurity;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.Dates;
import com.reptile.util.WebClientFactory;

@Service
public class NanTongSocialSecurityService {
    private Logger logger = LoggerFactory.getLogger(NanTongSocialSecurityService.class);

    public Map<String, Object> getDetailMes(HttpServletRequest request, String idCard, String socialCard, String passWord, String cityCode) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        WebClient webClient = new WebClientFactory().getWebClient();
        try {
            logger.warn("进入南通社保查询页面");
            HtmlPage page = webClient.getPage("http://www.jsnt.lss.gov.cn:1002/query/");
            String loginType = page.getElementById("loginType").getAttribute("value");
            String checkcode = page.getElementById("checkcode").getAttribute("value");

            WebRequest post = new WebRequest(new URL("http://www.jsnt.lss.gov.cn:1002/query/loginvalidate.html"));
            post.setHttpMethod(HttpMethod.POST);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new NameValuePair("type", loginType));
            list.add(new NameValuePair("checkcode", checkcode));
            list.add(new NameValuePair("account", socialCard));
            list.add(new NameValuePair("password", passWord));
            post.setRequestParameters(list);

            HtmlPage page1 = webClient.getPage(post);
            Thread.sleep(1000);
            System.out.println(page1.asText());
            String result = page1.asText();

            if (!result.contains("success")) {
                map.put("errorCode", "0002");
                map.put("errorInfo", "密码和用户名不匹配,如忘记密码，请携带身份证到社保局重置！");
                return map;
            }

            logger.warn("南通社保登录成功");
            String substring = result.substring(2, result.length() - 2);
            String[] split = substring.split("\\|");
            String userid = split[1];
            String sessionid = split[2];

            webClient.getPage("http://www.jsnt.lss.gov.cn:1002/query/index.html?userid=" + userid + "&sessionid=" + sessionid);
           /* logger.warn("获取南通社保基本信息");
            HtmlPage page2 = webClient.getPage("http://www.jsnt.lss.gov.cn:1002/query/person/personYLNZH.html");
            System.out.println(page2.asXml());
            dataMap.put("base", page2.asXml());
            logger.warn("获取南通社保缴纳详情");
            post = new WebRequest(new URL("http://www.jsnt.lss.gov.cn:1002/query/person/personJFJL_result.html"));
            list = new ArrayList<>();
            list.add(new NameValuePair("aae002_b", ""));
            list.add(new NameValuePair("aae002_e", ""));
            list.add(new NameValuePair("aae140", "11"));
            list.add(new NameValuePair("pageNo", "1"));
            post.setRequestParameters(list);
            page2 = webClient.getPage(post);
            System.out.println(page2.asText());
            dataList.add(page2.asXml());
            result = page2.asText();
            String[] split1 = result.split("当前1/");
            String countStr = split1[1].substring(0, split1[1].length() - 1);
            int count = Integer.parseInt(countStr);             //详单页数
            Thread.sleep(1000);
            for (int i = 2; i <= count; i++) {
                post = new WebRequest(new URL("http://www.jsnt.lss.gov.cn:1002/query/person/personJFJL_result.html"));
                list = new ArrayList<>();
                list.add(new NameValuePair("aae002_b", ""));
                list.add(new NameValuePair("aae002_e", ""));
                list.add(new NameValuePair("aae140", "11"));
                list.add(new NameValuePair("pageNo", i + ""));
                post.setRequestParameters(list);
                page2 = webClient.getPage(post);
                System.err.println(page2.asXml());
                dataList.add(page2.asXml());
                Thread.sleep(1000);
            }*/
            logger.warn("获取南通社保缴纳详情成功");
            List<Map<String,Object>> endowmentInsurance = this.getDetail(webClient, "11");//养老保险
			List<Map<String,Object>> unemploymentInsurance = this.getDetail(webClient, "21");//失业保险
			List<Map<String,Object>> medicalInsurance  = this.getDetail(webClient, "31");//医疗保险
			List<Map<String,Object>> accidentInsurance  =  this.getDetail(webClient, "41");//工伤保险
			List<Map<String,Object>> maternityInsurance   = this.getDetail(webClient, "51");//生育保险
        	//保险信息
            dataMap.put("endowmentInsurance", endowmentInsurance);
            dataMap.put("unemploymentInsurance", unemploymentInsurance );
            dataMap.put("medicalInsurance",  medicalInsurance);
            dataMap.put("accidentInsurance", accidentInsurance);
            dataMap.put("maternityInsurance",  maternityInsurance);
            dataMap.put("personalInfo",  this.getBaseInfo(webClient, unemploymentInsurance, accidentInsurance, maternityInsurance));
			//data
			map.put("city", cityCode);
			map.put("cityName", "南通");
			map.put("userId", idCard);
			map.put("createTime", Dates.currentTime());
			map.put("data", dataMap);
        } catch (Exception e) {
            logger.warn("南通社保信息获取失败", e);
            e.printStackTrace();
            map.put("errorCode", "0001");
            map.put("errorInfo", "系统繁忙，请稍后再试");
        } finally {
            if (webClient != null) {
                webClient.close();
            }
        }
        return map;
    }
    
    /**
     * 获取基本信息
     * @param webClient
     * @return
     * @throws Exception 
     */
    public Map<String,Object> getBaseInfo(WebClient webClient,List<Map<String,Object>> unemploymentInsurance,List<Map<String,Object>> accidentInsurance,List<Map<String,Object>> maternityInsurance) throws Exception{
    	//获取养老年账户信息
    	WebRequest request = new WebRequest(new URL("http://www.jsnt.lss.gov.cn:1002/query/person/personYLNZH.html"));
        HtmlPage page = webClient.getPage(request);
        List<List<String>> yangLaoBaseInfo = table1(page.getElementsByTagName("table").get(4).asXml());
        
        Map<String,Object> baseInfo = new HashMap<String, Object>();
		
		baseInfo.put("name", yangLaoBaseInfo.get(0).get(0));//姓名
		baseInfo.put("identityCards", yangLaoBaseInfo.get(0).get(15));//公民身份号码
		baseInfo.put("sex", yangLaoBaseInfo.get(0).get(1));//性别
		baseInfo.put("birthDate", "");//出生日期
		baseInfo.put("nation", "");//民族
		baseInfo.put("country", "");//国家
		baseInfo.put("personalIdentity", "");//个人身份
		baseInfo.put("workDate", "");//参加工作时间
		baseInfo.put("residenceType", "");//户口性质
		baseInfo.put("residenceAddr", yangLaoBaseInfo.get(0).get(16));//户口所在地地址
		baseInfo.put("residencePostcodes","");//户口所在地邮政编码
		baseInfo.put("contactAddress", yangLaoBaseInfo.get(0).get(17));//居住地(联系)地址
		baseInfo.put("contactPostcodes", yangLaoBaseInfo.get(0).get(18));//居住地（联系）邮政编码
		baseInfo.put("queryMethod", "");//获取对账单方式
		baseInfo.put("email", "");//电子邮件地址
		baseInfo.put("educationalBackground", "");//文化程度
		baseInfo.put("telephone","");//参保人电话
		baseInfo.put("phoneNo", "");//参保人手机
		baseInfo.put("income", "");//申报月均工资收入（元）
		baseInfo.put("documentType", "");//证件类型
		baseInfo.put("documentNumber", "");//证件号码
		baseInfo.put("bankName", "");//委托代发银行名称
		baseInfo.put("bankNumber", "");//委托代发银行账号
		baseInfo.put("paymentPersonnelCategory", "");//缴费人员类别
		baseInfo.put("insuredPersonCategory", "");//医疗参保人员类别
		baseInfo.put("retireType", "");//离退休类别
		baseInfo.put("retireDate", "");//离退休日期
		baseInfo.put("sentinelMedicalInstitutions1", "");//定点医疗机构 1
		baseInfo.put("sentinelMedicalInstitutions2", "");//定点医疗机构 2
		baseInfo.put("sentinelMedicalInstitutions3", "");//定点医疗机构 3
		baseInfo.put("sentinelMedicalInstitutions4", "");//定点医疗机构 4
		baseInfo.put("sentinelMedicalInstitutions5", "");//定点医疗机构 5
		baseInfo.put("specialDisease", "");//是否患有特殊病
		baseInfo.put("endowmentInsuranceAmount", yangLaoBaseInfo.get(0).get(11));//养老保险缴费余额
		
		//医保余额
		request = new WebRequest(new URL("http://www.jsnt.lss.gov.cn:1002/query/person/personYILZH.html"));
        page = webClient.getPage(request);
        System.err.println(page.asXml());
        yangLaoBaseInfo = table1(page.getElementsByTagName("table").get(8).asXml());
        baseInfo.put("medicalInsuranceAmount", yangLaoBaseInfo.get(0).get(7));//医疗保险缴费余额
        
		double unemploymentInsuranceAmount = this.getCal(unemploymentInsurance);
		baseInfo.put("unemploymentInsuranceAmount", unemploymentInsuranceAmount);//失业保险缴费余额
		double maternityInsuranceAmount = this.getCal(maternityInsurance);
		baseInfo.put("maternityInsuranceAmount", maternityInsuranceAmount);//生育保险缴费余额
		double accidentInsuranceAmount = this.getCal(accidentInsurance);
		baseInfo.put("accidentInsuranceAmount", accidentInsuranceAmount);//工伤保险缴费余额
        
    	return baseInfo;
    	
    }

	/**
	 * 获取详情
	 * @param webClient
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String,Object>> getDetail(WebClient webClient,String type) throws Exception{
		
		SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
		Calendar cal = Calendar.getInstance();
		
		List<Map<String,Object>> allInfo = new ArrayList<Map<String,Object>>();
		boolean flag = true;//循环查到所有的保险信息
		while(flag){
			String endTime = sim.format(cal.getTime()); //结束时间
			cal.set(Calendar.MONTH,0);
			String beginTime = sim.format(cal.getTime());  //开始时间
			List<List<String>> everyInfo = this.getDetail(beginTime, endTime,webClient,type);
			if(everyInfo.size() > 0){
				allInfo.add(this.distinct(everyInfo));
			}else{
				flag = false; //如果当年信息为空，则结束循环
			}
			cal.add(Calendar.MONTH,-1);
		}
		return allInfo;
	}
    
    /**
     * 循环获取每年的信息
     * @param beginTime
     * @param endTime
     * @param webClient
     * @param type
     * @return
     * @throws Exception
     */
    public List<List<String>>  getDetail(String beginTime,String endTime,WebClient webClient,String type) throws Exception{
    	 //查询参数
    	 List<NameValuePair> params = new ArrayList<NameValuePair>();
    	 params.add(new NameValuePair("aae002_b", beginTime));
    	 params.add(new NameValuePair("aae002_e", endTime));
    	 params.add(new NameValuePair("aae140", type));
    	 params.add(new NameValuePair("pageNo", "1"));
    	 
    	 WebRequest request = new WebRequest(new URL("http://www.jsnt.lss.gov.cn:1002/query/person/personJFJL_result.html"));
    	 request.setRequestParameters(params);
         HtmlPage page = webClient.getPage(request);
         String tableXml = page.getElementsByTagName("table").get(8).asXml();
    	 List<List<String>> info = table1(tableXml);
         System.out.println(page.asXml());
         String result = page.asText();
         //获取页数
         String[] split1 = result.split("当前1/");
         String countStr = split1[1].substring(0, split1[1].length() - 1);
         int count = Integer.parseInt(countStr);             
         for (int i = 2; i <= count; i++) {
        	 request = new WebRequest(new URL("http://www.jsnt.lss.gov.cn:1002/query/person/personJFJL_result.html"));
        	 params.clear();
        	 params.add(new NameValuePair("aae002_b", beginTime));
        	 params.add(new NameValuePair("aae002_e", endTime));
        	 params.add(new NameValuePair("aae140", type));
        	 params.add(new NameValuePair("pageNo", i + ""));
        	 request.setRequestParameters(params);
        	 page = webClient.getPage(request);
        	 tableXml = page.getElementsByTagName("table").get(8).asXml();
        	 System.out.println(tableXml);
        	 List<List<String>> table = table1(tableXml);
        	 for(List<String> item : table){
        		 info.add(item);
        	 }
         }
    	 
    	 return info;
    	 
    }	
    
    /**
     * 给月份去重，并整合计算每年的保险信息
     * @param info
     * @return
     */
    public Map<String,Object> distinct(List<List<String>> info){
    	int month_count = 0;//计算已缴费的月份数
    	List<String> temp = new ArrayList<String>();  
    	//获取到所有的月份并去重
    	for(List<String> item : info){
    		String month = item.get(1);
    		if(!month.isEmpty() && month.length() >= 6 && !temp.contains(item.get(1))){
    			temp.add(item.get(1));
    			if(item.get(7).contains("已实缴")){
    				month_count ++;
    			}
    		}
    	}
	    //取最新的一个月
    	String one = temp.get(temp.size()-1);
    	Map<String,Object> map = new HashMap<String, Object>();
    	map.put("year", one.substring(0,4)); //年份
    	map.put("month_count", month_count);//月数 
    	double monthly_company_income = 0;
    	double monthly_personal_income = 0;
    	for(List<String> item : info){
    		if(item.get(1).equals(one)){
    			if(item.get(4).contains("个人")){
    				if(!item.get(6).isEmpty()){
    					monthly_personal_income += Double.parseDouble((item.get(6)+"")) ;
    				}
    			}else if(item.get(4).contains("参保组织")){
    				if(!item.get(6).isEmpty()){
    					monthly_company_income += Double.parseDouble(item.get(6)+"") ;
    				}
    			}
    			
    			map.put("company_name", item.get(2));//公司名称
    			map.put("base_number", item.get(5));//缴费基数
    			map.put("monthly_company_income", monthly_company_income);//单位缴存
    			map.put("monthly_personal_income", monthly_personal_income);//个人缴存
    			if(item.get(7).contains("欠费")){
    				map.put("type", "欠费");//缴费状态
    			}else if(item.get(7).contains("已实缴")){
    				map.put("type", "缴存");//缴费状态
    			}
    			map.put("company_percentage","");//单位缴存比例
    			map.put("personal_percentage", "");//个人缴存比例
    			map.put("last_pay_date", "");//缴存日期
    		}
    	}
    	return map;
    }
    
    /**
	 * 解析table
	 * @param xml
	 * @return
	 */
	 private static List<List<String>>  table1(String xml){ 
		 
		 Document doc = Jsoup.parse(xml);
		 Elements trs = doc.select("table").select("tr");  
		 
		 List<List<String>> list = new ArrayList<List<String>>();
		 for (int i = 1; i < trs.size(); i++) {
			 Elements tds = trs.get(i).select("td");
			 List<String> item = new ArrayList<String>();
			 for (int j=0; j<tds.size(); j++){  
				 String txt = tds.get(j).text().replace(" ", "").replace(" ","");  
				 item.add(txt);
			 }  
			 System.out.println(item.toString());
			 if(!item.toString().equals("[, , , , , , , ]") && !item.toString().equals("[, , , , , , , , , , , , , , , , , , , , , ]")){
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
		private double getCal(List<Map<String,Object>> insurance){
			double count = 0;
			for (int i = 0; i < insurance.size(); i++) {
				Map<String,Object> map = insurance.get(i);
				if(map.get("month_count") != null){
					int month_count = (int)map.get("month_count");
					double monthly_company_income = 0;
					double monthly_personal_income = 0;
					if(map.get("monthly_company_income") != null){
						if(!(map.get("monthly_company_income")+"").equals("null")){
							monthly_company_income = Double.parseDouble(map.get("monthly_company_income")+"");
						}
					}
					if(map.get("monthly_personal_income") != null){
						if(!(map.get("monthly_personal_income")+"").equals("null")){
							monthly_company_income = Double.parseDouble(map.get("monthly_personal_income")+"");
						}
					}
					count += month_count * (monthly_company_income + monthly_personal_income);
				}
			}
			return count;
		}
    
}
