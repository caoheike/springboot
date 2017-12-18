package com.reptile.service.socialSecurity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.swt.browser.Browser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.Dates;
import com.reptile.util.ImgUtil;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;

@Service
public class ZhenJiangSocialSecurityService {
	private Logger logger= LoggerFactory.getLogger(ZhenJiangSocialSecurityService.class);
 
	public Map<String, Object> doLogin(HttpServletRequest request, String idCard, String passWord,String cityCode, String idCardNum) {
		Map<String, Object> data = new HashMap<String, Object>();
	    Map<String, Object> dataMap = new HashMap<>();
		//保存cookie
	    WebClient webclient=new WebClientFactory().getWebClient();
		
		String result="";
		try {
			HtmlPage page=webclient.getPage("http://www.hrsszj.gov.cn/PublicServicePlatform/index.action");
			//账号
			HtmlTextInput idcode=(HtmlTextInput) page.getElementById("logName");
			idcode.setValueAttribute(idCard);
		    HtmlPasswordInput password=(HtmlPasswordInput) page.getElementById("password");
		    password.setValueAttribute(passWord);
		    //验证码
		    HtmlImage unitVerifyCode1=(HtmlImage) page.getElementById("logon_user");
			BufferedImage read = unitVerifyCode1.getImageReader().read(0);
			ImageIO.write(read, "png", new File("C://aa.png"));
			Map<String, Object> code = MyCYDMDemo.getCode("C://aa.png");
			String vecCode = code.get("strResult").toString();
			page.getElementById("unitVerifyCode1").setAttribute("value", vecCode);
			// 登录
			HtmlPage newPage = (HtmlPage) page.executeJavaScript("submit()").getNewPage();
			Thread.sleep(6000);
			result = newPage.asText();
			if(result.contains("新用户注册")) {
	        	data.put("errorCode", "0002");
	        	data.put("errorInfo", " 您未登录，请登录后再操作 返回首页");
	        	System.out.println("未登录");
	        }else {
            
			HtmlPage Detailpage=webclient.getPage("http://www.hrsszj.gov.cn/PublicServicePlatform/business/shbx/toPersonPayList.action");
            List<Map<String,Object>> endowmentInsurance = this.getDetail(webclient, "110",Detailpage);//养老保险
			List<Map<String,Object>> unemploymentInsurance = this.getDetail(webclient, "210",Detailpage);//失业保险
			List<Map<String,Object>> medicalInsurance  = this.getDetail(webclient, "310",Detailpage);//医疗保险
			List<Map<String,Object>> accidentInsurance  =  this.getDetail(webclient, "410",Detailpage);//工伤保险
			List<Map<String,Object>> maternityInsurance   = this.getDetail(webclient, "510",Detailpage);//生育保险
        	//保险信息
            dataMap.put("endowmentInsurance", endowmentInsurance);
            dataMap.put("unemploymentInsurance", unemploymentInsurance );
            dataMap.put("medicalInsurance",  medicalInsurance);
            dataMap.put("accidentInsurance", accidentInsurance);
            dataMap.put("maternityInsurance",  maternityInsurance);
			
			
			String idcodenum = (newPage.getElementById("kahao").getTextContent()).substring(6);//身份证号
			dataMap.put("personalInfo",  this.getpersonalInfo(webclient,idcodenum,newPage));//基本信息
		    data.put("city", cityCode);
		    data.put("cityName", "镇江");
		    data.put("userId", idCardNum);
		    data.put("createTime", Dates.currentTime());
		    data.put("data", dataMap);
	        }
		  //data = new Resttemplate().SendMessage(data, "http://192.168.3.16:8089/HSDC/person/socialSecurity");
		  data = new Resttemplate().SendMessage(data, "http://117.34.70.217:8080/HSDC/person/socialSecurity");
		} catch (Exception e) {
			System.out.println("eeee=="+e);
		}
		System.out.println("data----"+data);
		return data;
	}
	/**
	 * 获取详情
	 * @param webClient
	 * @param detailpage 
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String,Object>> getDetail(WebClient webClient,String type, HtmlPage detailpage) throws Exception{
		
		SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
		Calendar cal = Calendar.getInstance();
		
		List<Map<String,Object>> allInfo = new ArrayList<Map<String,Object>>();
		boolean flag = true;//循环查到所有的保险信息
		while(flag){
			String endTime = sim.format(cal.getTime()); //结束时间
			cal.set(Calendar.MONTH,0);
			String beginTime = sim.format(cal.getTime());  //开始时间
			List<List<String>> everyInfo = this.getDetail(beginTime, endTime,webClient,type,detailpage);
			if(everyInfo.size() > 1){
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
     * @param detailpage 
     * @return
     * @throws Exception
     */
    public List<List<String>>  getDetail(String beginTime,String endTime,WebClient webClient,String type, HtmlPage detailpage) throws Exception{
    	 HtmlTextInput aae042=(HtmlTextInput) detailpage.getElementById("aae042");//结束时间
   	     HtmlTextInput aae041=(HtmlTextInput) detailpage.getElementById("aae041");//开始时间
	   	 //HtmlSelect aae078=(HtmlSelect) detailpage.getElementById("aae078");//到账标志
	   	 HtmlSelect aae140=(HtmlSelect) detailpage.getElementById("aae140");//险种类型
	   	 int aae140s=aae140.getChildElementCount();//险种类型数
	   	 aae041.setValueAttribute(beginTime);
	   	 aae042.setValueAttribute(endTime);
	   	 
	   	 HtmlButton submitSearch=(HtmlButton) detailpage.getElementById("submitSearch");
	   	 HtmlPage detail=detailpage;
   	 
	   	 //判断是对应险种时执行点击事件
	   	 for (int i = 0; i < aae140s; i++) {
				if(aae140.getOption(i).getAttribute("value").equals(type)) {
				aae140.getOption(i).setSelected(true); 
				detail=submitSearch.click();
   				Thread.sleep(6000);
			}
		 }
   	 //判断是否加载完  未写
   	 String tableXml = detail.getElementsByTagName("table").get(0).asXml();//第一页
   	 List<List<String>> info = table(tableXml);
   	 if(info.size()>1) {
   	 
        String example_last=detail.getElementById("example_last").getTextContent();
        int last = Integer.parseInt(example_last);   
        //如果总页数大于1 循环点击下一页
        if(last>1) {
       	 HtmlAnchor example_next = detail.getAnchorByText("下一页");
       	 for (int i = 0; i < last-1; i++) {
       		 HtmlPage detail1=example_next.click();
       		 Thread.sleep(5000);
       		 String tableXml1 = detail1.getElementsByTagName("table").get(0).asXml();
           	 List<List<String>> infos = table(tableXml1);//下一页
           	 info.addAll(infos);
			}
        }
    }
    	 return info;
    	 
    }
    /**
     * 整合保险信息
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
    				month_count ++;
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
    				if(!item.get(5).isEmpty()){
    					monthly_personal_income += Double.parseDouble((item.get(5)+"")) ;
    				}
    				if(!item.get(4).isEmpty()){
    					monthly_company_income += Double.parseDouble(item.get(4)+"") ;
    				}
    			
    			map.put("company_name", item.get(0));//公司名称
    			map.put("base_number", item.get(3));//缴费基数
    			map.put("monthly_company_income", monthly_company_income);//单位缴存
    			map.put("monthly_personal_income", monthly_personal_income);//个人缴存
    			if(item.get(6).contains("否")){
    				map.put("type", "欠费");//缴费状态
    			}else if(item.get(6).contains("是")){
    				map.put("type", "缴存");//缴费状态
    			}
    			map.put("company_percentage","");//单位缴存比例
    			map.put("personal_percentage", "");//个人缴存比例
    			map.put("last_pay_date", item.get(7));//缴存日期
    		}
    	}
    	
    	return map;
    }
	/**
     * 获取基本信息
     * @param webClient
	 * @param data 
	 * @param idcodenum 
	 * @param page 
	 * @param page 
     * @return
     * @throws Exception 
     */
    public Map<String,Object> getpersonalInfo(WebClient webClient, String idcodenum, HtmlPage page) throws Exception{
    	//获取基本信息
    	HtmlPage user_management_page=webClient.getPage("http://www.hrsszj.gov.cn/PublicServicePlatform/business/user/searchUser.action");
		Map<String,Object> baseInfo = new HashMap<String, Object>();
		
        baseInfo.put("name", page.getElementById("shebaoName").getTextContent().substring(3));//姓名
		baseInfo.put("identityCards", idcodenum);//公民身份号码
		baseInfo.put("sex", page.getElementById("xingbie").getTextContent());//性别
		baseInfo.put("birthDate", page.getElementById("riqi").getTextContent());//出生日期
		baseInfo.put("nation", page.getElementById("minzu").getTextContent());//民族
		baseInfo.put("country", page.getElementById("guoji").getTextContent());//国家
		baseInfo.put("personalIdentity", "");//个人身份
		baseInfo.put("workDate", "");//参加工作时间
		baseInfo.put("residenceType", "");//户口性质
		baseInfo.put("residenceAddr", "");//户口所在地地址
		baseInfo.put("residencePostcodes","");//户口所在地邮政编码
		baseInfo.put("contactAddress", page.getElementById("dizhi").getTextContent());//居住地(联系)地址
		baseInfo.put("contactPostcodes", "");//居住地（联系）邮政编码
		baseInfo.put("queryMethod", "");//获取对账单方式
		baseInfo.put("email", user_management_page.getElementById("OrderTime").getTextContent());//电子邮件地址
		baseInfo.put("educationalBackground", "");//文化程度
		baseInfo.put("telephone",page.getElementById("dianhua").getTextContent());//参保人电话
		baseInfo.put("phoneNo", user_management_page.getElementById("phone").getTextContent());//参保人手机
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
		String endowmentInsuranceAmount=page.getElementById("zhanghu").getTextContent();
		baseInfo.put("endowmentInsuranceAmount", endowmentInsuranceAmount.subSequence(0, endowmentInsuranceAmount.length()-1));//养老保险缴费余额
        String medicalInsuranceAmount= page.getElementById("ybzh").getTextContent();
		baseInfo.put("medicalInsuranceAmount",medicalInsuranceAmount.substring(0, medicalInsuranceAmount.length()-1));//医疗保险缴费余额
        baseInfo.put("unemploymentInsuranceAmount", "");//失业保险缴费余额
        baseInfo.put("maternityInsuranceAmount", "");//生育保险缴费余额
        baseInfo.put("accidentInsuranceAmount", "");//工伤保险缴费余额
        return baseInfo;
    	
    }

	/**
	 * 解析table
	 * @param xml
	 * @return
	 */
	 private static List<List<String>>  table(String xml){ 
		 
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
			 if(!item.toString().equals("[, , , , , , , ]") && !item.toString().equals("[, , , , , , , , , , , , , , , , , , , , , ]")){
				 list.add(item);
			 }
		 }
		return list;	
    } 
	}
