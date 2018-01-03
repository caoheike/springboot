package com.reptile.service.socialsecurity;

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
/**
 * 
 * @ClassName: ZhenJiangSocialSecurityService  
 * @Description: TODO  
 * @author: lusiqin
 * @date 2018年1月2日  
 *
 */
@Service
public class ZhenJiangSocialSecurityService {
	private Logger logger= LoggerFactory.getLogger(ZhenJiangSocialSecurityService.class);
 
	public Map<String, Object> doLogin(HttpServletRequest request, String idCard, String passWord,String cityCode, String idCardNum) {
		Map<String, Object> data = new HashMap<String, Object>(16);
	    Map<String, Object> dataMap = new HashMap<>(16);
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
			String str="新用户注册";
			if(result.contains(str)) {
	        	data.put("errorCode", "0002");
	        	data.put("errorInfo", " 您未登录，请登录后再操作 返回首页");
	        	System.out.println("未登录");
	        }else {
            
			HtmlPage detailpage=webclient.getPage("http://www.hrsszj.gov.cn/PublicServicePlatform/business/shbx/toPersonPayList.action");
			//养老保险
            List<Map<String,Object>> endowmentInsurance = this.getDetail(webclient, "110",detailpage);
            //失业保险
			List<Map<String,Object>> unemploymentInsurance = this.getDetail(webclient, "210",detailpage);
			//医疗保险
			List<Map<String,Object>> medicalInsurance  = this.getDetail(webclient, "310",detailpage);
			//工伤保险
			List<Map<String,Object>> accidentInsurance  =  this.getDetail(webclient, "410",detailpage);
			//生育保险
			List<Map<String,Object>> maternityInsurance   = this.getDetail(webclient, "510",detailpage);
        	//保险信息
            dataMap.put("endowmentInsurance", endowmentInsurance);
            dataMap.put("unemploymentInsurance", unemploymentInsurance );
            dataMap.put("medicalInsurance",  medicalInsurance);
            dataMap.put("accidentInsurance", accidentInsurance);
            dataMap.put("maternityInsurance",  maternityInsurance);
			
			
            //身份证号
			String idcodenum = (newPage.getElementById("kahao").getTextContent()).substring(6);
			//基本信息
			dataMap.put("personalInfo",  this.getpersonalInfo(webclient,idcodenum,newPage));
		    data.put("city", cityCode);
		    data.put("cityName", "镇江");
		    data.put("userId", idCardNum);
		    data.put("createTime", Dates.currentTime());
		    data.put("data", dataMap);
	        }
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
		//循环查到所有的保险信息
		boolean flag = true;
		while(flag){
			//结束时间
			String endTime = sim.format(cal.getTime()); 
			cal.set(Calendar.MONTH,0);
			//开始时间
			String beginTime = sim.format(cal.getTime()); 
			List<List<String>> everyInfo = this.getDetail(beginTime, endTime,webClient,type,detailpage);
			if(everyInfo.size() > 1){
				allInfo.add(this.distinct(everyInfo));
			}else{
				//如果当年信息为空，则结束循环
				flag = false; 
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
    	//结束时间
    	 HtmlTextInput aae042=(HtmlTextInput) detailpage.getElementById("aae042");
    	 //开始时间
   	     HtmlTextInput aae041=(HtmlTextInput) detailpage.getElementById("aae041");
   	     //险种类型
	   	 HtmlSelect aae140=(HtmlSelect) detailpage.getElementById("aae140");
	   	 //险种类型数
	   	 int aae140s=aae140.getChildElementCount();
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
	   	 //第一页
   	 String tableXml = detail.getElementsByTagName("table").get(0).asXml();
   	 List<List<String>> info = table(tableXml);
   	 if(info.size()>1) {
   	 
        String examplelast=detail.getElementById("example_last").getTextContent();
        int last = Integer.parseInt(examplelast);   
        //如果总页数大于1 循环点击下一页
        if(last>1) {
       	 HtmlAnchor examplenext = detail.getAnchorByText("下一页");
       	 for (int i = 0; i < last-1; i++) {
       		 HtmlPage detail1=examplenext.click();
       		 Thread.sleep(5000);
       		 String tableXml1 = detail1.getElementsByTagName("table").get(0).asXml();
       		 //下一页
           	 List<List<String>> infos = table(tableXml1);
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
    	//计算已缴费的月份数
    	int monthcount = 0;
    	List<String> temp = new ArrayList<String>();  
    	//获取到所有的月份并去重
    	for(List<String> item : info){
    		String month = item.get(1);
    		if(!month.isEmpty() && month.length() >= 6 && !temp.contains(item.get(1))){
    			temp.add(item.get(1));
    				monthcount ++;
    		}
    	}
	    //取最新的一个月
    	String one = temp.get(temp.size()-1);
    	Map<String,Object> map = new HashMap<String, Object>(16);
    	//年份
    	map.put("year", one.substring(0,4)); 
    	//月数 
    	map.put("month_count", monthcount);
    	double monthlycompanyincome = 0;
    	double monthlypersonalincome = 0;
    	for(List<String> item : info){
    		if(item.get(1).equals(one)){
    				if(!item.get(5).isEmpty()){
    					monthlypersonalincome += Double.parseDouble((item.get(5)+"")) ;
    				}
    				if(!item.get(4).isEmpty()){
    					monthlycompanyincome += Double.parseDouble(item.get(4)+"") ;
    				}
    			
    				//公司名称
    			map.put("company_name", item.get(0));
    			//缴费基数
    			map.put("base_number", item.get(3));
    			//单位缴存
    			map.put("monthly_company_income", monthlycompanyincome);
    			//个人缴存
    			map.put("monthly_personal_income", monthlypersonalincome);
    			if(item.get(6).contains("否")){
    				//缴费状态
    				map.put("type", "欠费");
    			}else if(item.get(6).contains("是")){
    				//缴费状态
    				map.put("type", "缴存");
    			}
    			//单位缴存比例
    			map.put("company_percentage","");
    			//个人缴存比例
    			map.put("personal_percentage", "");
    			//缴存日期
    			map.put("last_pay_date", item.get(7));
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
    	HtmlPage usermanagementpage=webClient.getPage("http://www.hrsszj.gov.cn/PublicServicePlatform/business/user/searchUser.action");
		Map<String,Object> baseInfo = new HashMap<String, Object>(16);
		
		//姓名
        baseInfo.put("name", page.getElementById("shebaoName").getTextContent().substring(3));
        //公民身份号码
		baseInfo.put("identityCards", idcodenum);
		//性别
		baseInfo.put("sex", page.getElementById("xingbie").getTextContent());
		//出生日期
		baseInfo.put("birthDate", page.getElementById("riqi").getTextContent());
		//民族
		baseInfo.put("nation", page.getElementById("minzu").getTextContent());
		//国家
		baseInfo.put("country", page.getElementById("guoji").getTextContent());
		//个人身份
		baseInfo.put("personalIdentity", "");
		//参加工作时间
		baseInfo.put("workDate", "");
		//户口性质
		baseInfo.put("residenceType", "");
		//户口所在地地址
		baseInfo.put("residenceAddr", "");
		//户口所在地邮政编码
		baseInfo.put("residencePostcodes","");
		//居住地(联系)地址
		baseInfo.put("contactAddress", page.getElementById("dizhi").getTextContent());
		//居住地（联系）邮政编码
		baseInfo.put("contactPostcodes", "");
		//获取对账单方式
		baseInfo.put("queryMethod", "");
		//电子邮件地址
		baseInfo.put("email", usermanagementpage.getElementById("OrderTime").getTextContent());
		//文化程度
		baseInfo.put("educationalBackground", "");
		//参保人电话
		baseInfo.put("telephone",page.getElementById("dianhua").getTextContent());
		//参保人手机
		baseInfo.put("phoneNo", usermanagementpage.getElementById("phone").getTextContent());
		//申报月均工资收入（元）
		baseInfo.put("income", "");
		//证件类型
		baseInfo.put("documentType", "");
		//证件号码
		baseInfo.put("documentNumber", "");
		//委托代发银行名称
		baseInfo.put("bankName", "");
		//委托代发银行账号
		baseInfo.put("bankNumber", "");
		//缴费人员类别
		baseInfo.put("paymentPersonnelCategory", "");
		//医疗参保人员类别
		baseInfo.put("insuredPersonCategory", "");
		//离退休类别
		baseInfo.put("retireType", "");
		//离退休日期
		baseInfo.put("retireDate", "");
		
		//定点医疗机构 1
		baseInfo.put("sentinelMedicalInstitutions1", "");
		//定点医疗机构 2
		baseInfo.put("sentinelMedicalInstitutions2", "");
		//定点医疗机构 3
		baseInfo.put("sentinelMedicalInstitutions3", "");
		//定点医疗机构 4
		baseInfo.put("sentinelMedicalInstitutions4", "");
		//定点医疗机构 5
		baseInfo.put("sentinelMedicalInstitutions5", "");
		//是否患有特殊病
		baseInfo.put("specialDisease", "");
		String endowmentInsuranceAmount=page.getElementById("zhanghu").getTextContent();
		//养老保险缴费余额
		baseInfo.put("endowmentInsuranceAmount", endowmentInsuranceAmount.subSequence(0, endowmentInsuranceAmount.length()-1));
        String medicalInsuranceAmount= page.getElementById("ybzh").getTextContent();
        //医疗保险缴费余额
		baseInfo.put("medicalInsuranceAmount",medicalInsuranceAmount.substring(0, medicalInsuranceAmount.length()-1));
		//失业保险缴费余额
        baseInfo.put("unemploymentInsuranceAmount", "");
        //生育保险缴费余额
        baseInfo.put("maternityInsuranceAmount", "");
        //工伤保险缴费余额
        baseInfo.put("accidentInsuranceAmount", "");
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
			 String str="[, , , , , , , ]";
			 String str1="[, , , , , , , , , , , , , , , , , , , , , ]";
			 if(!item.toString().equals(str) && !item.toString().equals(str1)){
				 list.add(item);
			 }
		 }
		return list;	
    } 
	}
