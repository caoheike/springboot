package com.reptile.service.socialSecurity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reptile.model.SecurityBean;
import com.reptile.service.accumulationfund.GuiYangAccumulationfundService;
import com.reptile.util.DriverUtil;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;

@Service
public class NanNingSocialSecurityService {
	@Autowired 
	private application applicat;
	private Logger logger = LoggerFactory.getLogger(GuiYangAccumulationfundService.class);
	Date date=new Date();
	DecimalFormat df= new DecimalFormat("#.00");
	public Map<String, Object> getDeatilMes(HttpServletRequest request, String userCard, String password, String socialCard,String idCardNum,String UUID) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
    	Map<String,Object> baseInfo = new HashMap<String, Object>();
    	List<SecurityBean> yanglaoList=new ArrayList<SecurityBean>();
    	List<SecurityBean> yiliaoList=new ArrayList<SecurityBean>();
    	List<SecurityBean> shiyeList=new ArrayList<SecurityBean>();
    	List<SecurityBean> gongshList=new ArrayList<SecurityBean>();
    	List<SecurityBean> shengyuList=new ArrayList<SecurityBean>();
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
        try {
        	//登录页面
			driver.get("http://222.216.5.212:8060/Cas/login?service=http://222.216.5.212:8081/siweb/userlogin.do?method=begin_dl");	

	        
			WebElement username = driver.findElement(By.id("username"));
			username.sendKeys(userCard);
			WebElement passWord = driver.findElement(By.id("password"));
			passWord.sendKeys(password);
			WebElement button = driver.findElement(By.id("loginButton"));
			button.click();
            Thread.sleep(1000);
            boolean isFind = DriverUtil.waitById("status11",driver,5);//错误提示
            if(isFind==true){
            	String errorInfo = driver.findElement(By.id("status11")).getText();
            	logger.warn(errorInfo);
                map.put("errorCode", "0001");
                map.put("errorInfo", errorInfo);
                return map;
            }
            //输入社保账号页面
            driver.get("http://222.216.5.212:8081/siweb/userlogin.do?method=begin_dl");
            Thread.sleep(500);
            if(!driver.getPageSource().contains("请输入个人编号或社保卡")){
            	logger.warn("当前网络繁忙，请刷新后重试");
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
                return map;
            }
            WebElement nextlogon = driver.findElement(By.id("text1"));
            nextlogon.sendKeys(socialCard);
            WebElement lastlogon = driver.findElement(By.id("button1"));
            lastlogon.click();
            if(driver.getPageSource().contains("您录入的个人编号、社保卡号、医保卡号有误，不能登录")){
            	logger.warn("社保账号错误，请重试！");
                map.put("errorCode", "0001");
                map.put("errorInfo", "社保账号错误，请重试！");
                return map;
            }
            
            Thread.sleep(1000);
           //再次确认社保账号页面，有的账号没有这个页面
            if(DriverUtil.waitById("dl_title",driver,5)){//
            	WebElement grbh1 = driver.findElement(By.id("grbh1"));
            	String grbh3 = grbh1.getAttribute("value");
            	WebElement grbh2 = driver.findElement(By.id("grbh2"));
            	String grbh4 = grbh2.getAttribute("value");
            	if(socialCard.equals(grbh3)){
            		WebElement radio1 = driver.findElement(By.id("radio1"));
            		radio1.click();
            		Thread.sleep(3000);
            	}else if(socialCard.equals(grbh4)){
            		WebElement radio2 = driver.findElement(By.id("radio2"));
            		radio2.click();
            		Thread.sleep(3000);
            	}else{
            		logger.warn("社保账号错误，请重试！");
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "社保账号错误，请重试！");
                    return map;
            	}
            }
            PushSocket.push(dataMap, UUID, "0000");
        	PushState.state(idCardNum, "socialSecurity", 100);
            /*
        	 *获取医辽余额页面                               
        	*/
            driver.get("http://222.216.5.212:8081/siweb/web_person_ylzhgquery.do?method=begin");
        
        	//获取从哪一年开始买社保
            List<WebElement> tdDomNodeList = driver.findElement(By.id("unieap_grid_View_0")).findElements(By.tagName("td"));  
            int nodes = tdDomNodeList.size()-1;
            String medicalInsuranceAmount = driver.findElement(By.id("unieap_grid_View_0")).findElements(By.tagName("td")).get(nodes).getText();
            //缴费明细页面
            driver.get("http://222.216.5.212:8081/siweb/emp_payinof_query.do?method=begin");
 
            if(!driver.getPageSource().contains("个人缴费明细信息")){
            	logger.warn("南宁社保基本信息获取失败");
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
                return map;//
            }
            String yeshutest = driver.findElement(By.id("unieap_grid_view_toolbar_0")).findElements(By.tagName("table")).get(4).getText();  
            
            int num = yeshutest.indexOf("条记录");
            int num2 = yeshutest.indexOf("条记录",num+1);
            int tiaoshu = Integer.parseInt(yeshutest.substring(num+5, num2));           
            int yeshu = tiaoshu%10>0?tiaoshu/10+1:tiaoshu/10;//确定页数
            SecurityBean yanglao = null;
            SecurityBean yiliao = null;
            SecurityBean shiye = null;
            SecurityBean gongshang = null;
            SecurityBean shengyu = null;
            
            

            for(int i=1;i<=yeshu;i++){
            	WebElement tdtable = driver.findElement(By.id("unieap_grid_View_0"));  
            	List<WebElement> tdlist = tdtable.findElements(By.tagName("td"));
            	int month_count=1;
    			for(int td=4;td<=tdlist.size();td=td+7){    				
    				String year = tdtable.findElements(By.tagName("td")).get(td-1).getText().substring(0, 4);
    				String type = tdtable.findElements(By.tagName("td")).get(td).getText();

    				
        			if(type.equals("基本养老保险")){     
            			yanglao = new SecurityBean();
        				yanglao.setCompany_name(tdtable.findElements(By.tagName("td")).get(td-2).getText());
        				yanglao.setYear(year);
        				String monthly_personal_income = tdtable.findElements(By.tagName("td")).get(td+2).getText();
        				yanglao.setMonthly_personal_income(monthly_personal_income);
        				yanglao.setLast_pay_date(tdtable.findElements(By.tagName("td")).get(td-1).getText());
        				yanglaoList.add(yanglao);
        			}
        			if(type.contains("基本医疗保险")){     
            			yiliao = new SecurityBean();
            			yiliao.setCompany_name(tdtable.findElements(By.tagName("td")).get(td-2).getText());
            			yiliao.setYear(year);
        				String monthly_personal_income = tdtable.findElements(By.tagName("td")).get(td+2).getText();
        				yiliao.setMonthly_personal_income(monthly_personal_income);
        				yiliao.setLast_pay_date(tdtable.findElements(By.tagName("td")).get(td-1).getText());
        				yiliaoList.add(yiliao);
        			}
        			if(type.equals("失业保险")){     
            			shiye = new SecurityBean();
            			shiye.setCompany_name(tdtable.findElements(By.tagName("td")).get(td-2).getText());
            			shiye.setYear(year);
        				String monthly_personal_income = tdtable.findElements(By.tagName("td")).get(td+2).getText();
        				shiye.setMonthly_personal_income(monthly_personal_income);
        				shiye.setLast_pay_date(tdtable.findElements(By.tagName("td")).get(td-1).getText());
        				shiyeList.add(shiye);
        			}
        			if(type.equals("工伤保险")){     
            			gongshang = new SecurityBean();
            			gongshang.setCompany_name(tdtable.findElements(By.tagName("td")).get(td-2).getText());
            			gongshang.setYear(year);
        				String monthly_personal_income = tdtable.findElements(By.tagName("td")).get(td+2).getText();
        				gongshang.setMonthly_personal_income(monthly_personal_income);
        				gongshang.setLast_pay_date(tdtable.findElements(By.tagName("td")).get(td-1).getText());
        				gongshList.add(gongshang);
        			}
        			if(type.equals("生育保险")){     
            			shengyu = new SecurityBean();
            			shengyu.setCompany_name(tdtable.findElements(By.tagName("td")).get(td-2).getText());
            			shengyu.setYear(year);
        				String monthly_personal_income = tdtable.findElements(By.tagName("td")).get(td+2).getText();
        				shengyu.setMonthly_personal_income(monthly_personal_income);
        				shengyu.setLast_pay_date(tdtable.findElements(By.tagName("td")).get(td-1).getText());
        				shengyuList.add(shengyu);
        			}
        		}
    			WebElement span = (WebElement) driver.findElements(By.tagName("span")).get(6);
    			span.click();
        		Thread.sleep(2000);

    			
            }
            /*
             * 进行排序
             */
            yanglaoList = sort(yanglaoList,yanglao);
            
            yiliaoList = sort(yiliaoList,yiliao);
            shiyeList = sort(shiyeList,shiye);	
            gongshList = sort(gongshList,gongshang);
            shengyuList = sort(shengyuList,shengyu);
            /*
             * 余额
             */
             
            double endowmentInsuranceAmount = getSum(yanglaoList);//养老保险缴费余额
            double unemploymentInsuranceAmount = getSum(shiyeList);//失业保险缴费余额
            double maternityInsuranceAmount = getSum(shengyuList);//生育保险缴费余额
            double accidentInsuranceAmount = getSum(gongshList);//工伤保险缴费余额

            
        	/*
        	 * 解析			
        	 */
            yanglaoList = 	getDeatilInfo(yanglaoList,1);
            yiliaoList = 	getDeatilInfo(yiliaoList,1);
            shiyeList = 	getDeatilInfo(shiyeList,1);
            gongshList = 	getDeatilInfo(gongshList,2);
            shengyuList = 	getDeatilInfo(shengyuList,2);
            
            
            
            /*
             * 基本信息
             */
            baseInfo = getBaseInfo(endowmentInsuranceAmount,unemploymentInsuranceAmount, maternityInsuranceAmount, accidentInsuranceAmount,Double.valueOf(medicalInsuranceAmount));
            dataMap.put("personalInfo", baseInfo);
    		dataMap.put("endowmentInsurance", yanglaoList);
    		dataMap.put("medicalInsurance", yiliaoList);
    		dataMap.put("unemploymentInsurance", shiyeList);
        	dataMap.put("accidentInsurance", gongshList);
        	dataMap.put("maternityInsurance", shengyuList);
            
            
            
		}catch (Exception e) {
            logger.warn("南宁社保获取失败",e);
            e.printStackTrace();
            map.put("errorCode", "0001");
            map.put("errorInfo", "当前网络繁忙，请刷新后重试");
        }finally {
        	driver.quit(); 
        }
        SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy年MM月dd日  hh:mm:ss" );
		String today = sdf.format(date);
        map.put("data", dataMap);
        map.put("cityName", "南宁");
        map.put("city", "013");
        map.put("userId", idCardNum);
        map.put("createTime", today);
       /* Resttemplate resttemplate=new Resttemplate();
        map = resttemplate.SendMessage(map, applicat.getSendip()+"/HSDC/person/socialSecurity");
        
        if(map!=null&&"0000".equals(map.get("errorCode").toString())){
          	PushState.state(idCardNum, "socialSecurity", 300);
          	map.put("errorInfo","推送成功");
          	map.put("errorCode","0000");
          }else{
          	PushState.state(idCardNum, "socialSecurity", 200);
          	map.put("errorInfo","推送失败");
          	map.put("errorCode","0001");
          }*/
        
        return map;
	}
	
	
	/*
	 * 对集合进行排序
	 */
	@SuppressWarnings("unchecked")
	public List<SecurityBean> sort(List<SecurityBean> list,SecurityBean securityBean){
		Collections.sort(list, new Comparator<SecurityBean>(){
		      @Override
		      public int compare(SecurityBean item, SecurityBean item1) {
		    	  String year = item.getLast_pay_date();
		    	  String year1 = item1.getLast_pay_date();
		    	  Double nowyear = Double.valueOf(year);
		    	  Double nowyear1 = Double.valueOf(year1);
		    	  int num=0;
			      if(nowyear>nowyear1){			    	  
			    	  num=-1;
			      }
			      if(nowyear<nowyear1){			    	  
			    	  num=1;
			      }
			      if(nowyear==nowyear1){			    	  
			    	  num=0;
			      }
			      return num;
		      }
		});
		return list;
		      
	}
	
	
	/*
	 * 解析数据
	 */
	 public List<SecurityBean>  getDeatilInfo(List<SecurityBean> list,int num){			 
		 List<SecurityBean> newlist = new ArrayList<SecurityBean>();
		 int monthCount = 0;
		 for(int i=1;i<=list.size();i++){
			 if(num==1){
				 if(i<list.size()){					 
					 if(!list.get(i).getYear().equals(list.get(i-1).getYear())||i==list.size()-1){
						 SecurityBean newSecurityBean = new SecurityBean();
						 String personal_income1;
						 String personal_income2;	
						 monthCount = (monthCount+1)/2;
						 if(monthCount>12) {
							 monthCount = 12;
						 }
						 if(i==list.size()-1){
							 personal_income1 = list.get(i).getMonthly_personal_income();		 					 
							 personal_income2 = list.get(i-1).getMonthly_personal_income();			
						 }else {
							 personal_income1 = list.get(i-1).getMonthly_personal_income();						 					 
							 personal_income2 = list.get(i-2).getMonthly_personal_income();	
						 }					 					 
						 Double personal_income = Double.valueOf(personal_income1)+Double.valueOf(personal_income2);
						 String base_number = df.format(personal_income/0.379);
						 newSecurityBean.setYear(list.get(i-1).getYear());
						 newSecurityBean.setMonth_count(String.valueOf(monthCount));
						 newSecurityBean.setMonthly_personal_income(df.format(personal_income));
						 newSecurityBean.setBase_number(base_number);
						 newSecurityBean.setType("缴存");
						 newSecurityBean.setLast_pay_date(list.get(i-1).getLast_pay_date());	
						 newSecurityBean.setCompany_name(list.get(i-1).getCompany_name());
						 newlist.add(newSecurityBean);
						 monthCount = 0;
					 }else if(list.get(i).getYear().equals(list.get(i-1).getYear())){
						 monthCount = monthCount+1;	
					 }
				 } 
			 }else{
				 if(i<list.size()){
					 	
					 if(!list.get(i).getYear().equals(list.get(i-1).getYear())||i==list.size()-1){
						 monthCount = monthCount+1;	
						 if(monthCount>12) {
							 monthCount = 12;
						 }
						 SecurityBean newSecurityBean = new SecurityBean();
						 String personal_income1 = list.get(i-1).getMonthly_personal_income();
						 Double personal_income = Double.valueOf(personal_income1);
						 String base_number = df.format(personal_income/0.379);
						 newSecurityBean.setYear(list.get(i-1).getYear());
						 newSecurityBean.setMonth_count(String.valueOf(monthCount));
						 newSecurityBean.setMonthly_personal_income(df.format(personal_income));
						 newSecurityBean.setBase_number(base_number);
						 newSecurityBean.setType("缴存");
						 newSecurityBean.setLast_pay_date(list.get(i-1).getLast_pay_date());	
						 newSecurityBean.setCompany_name(list.get(i-1).getCompany_name());
						 newlist.add(newSecurityBean);
						 monthCount = 0;
					 }else if(list.get(i).getYear().equals(list.get(i-1).getYear())){
						 monthCount = monthCount+1;	
					 }
				 }
			 }			 	
		 }		
		 return newlist;
	 }
	 public Map<String,Object> getBaseInfo(Double endowmentInsurance,
			 							   Double unemploymentInsurance,Double maternityInsurance,Double accidentInsurance,
			 							   Double medicalInsurance) throws Exception{
	    	Map<String,Object> baseInfo = new HashMap<String, Object>();

			baseInfo.put("name", "");//姓名
			baseInfo.put("identityCards", "");//公民身份号码
			baseInfo.put("sex", "");//性别
			baseInfo.put("birthDate", "");//出生日期
			baseInfo.put("nation", "");//民族
			baseInfo.put("country", "");//国家
			baseInfo.put("personalIdentity", "");//个人身份
			baseInfo.put("workDate", "");//参加工作时间
			baseInfo.put("residenceType","");//户口性质
			baseInfo.put("residenceAddr", "");//户口所在地地址
			baseInfo.put("residencePostcodes", "");//户口所在地邮政编码
			baseInfo.put("contactAddress", "");//居住地(联系)地址
			baseInfo.put("contactPostcodes", "");//居住地（联系）邮政编码
			baseInfo.put("queryMethod", "");//获取对账单方式
			baseInfo.put("email", "");//电子邮件地址
			baseInfo.put("educationalBackground", "");//文化程度
			baseInfo.put("telephone", "");//参保人电话
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
			baseInfo.put("sentinelMedicalInstitutions2","");//定点医疗机构 2
			baseInfo.put("sentinelMedicalInstitutions3", "");//定点医疗机构 3
			baseInfo.put("sentinelMedicalInstitutions4", "");//定点医疗机构 4
			baseInfo.put("sentinelMedicalInstitutions5", "");//定点医疗机构 5
			baseInfo.put("specialDisease", "");//是否患有特殊病
			
			baseInfo.put("unemploymentInsuranceAmount", unemploymentInsurance);//失业保险缴费余额
			baseInfo.put("endowmentInsuranceAmount", endowmentInsurance);//养老保险缴费余额
			baseInfo.put("maternityInsuranceAmount", maternityInsurance);//生育保险缴费余额
			baseInfo.put("accidentInsuranceAmount", accidentInsurance);//工伤保险缴费余额
			//医保余额
			
			baseInfo.put("medicalInsuranceAmount", medicalInsurance);//医疗保险缴费余额
			//总额
			double totalAmount = unemploymentInsurance + endowmentInsurance + maternityInsurance + accidentInsurance + medicalInsurance;
			baseInfo.put("totalAmount", df.format(totalAmount));
			
			return baseInfo;
		
		}
	public Double getSum(List<SecurityBean> list){
		Double sum = 0.00;
		for(int i=0;i<list.size();i++){
			String monthly_personal_income = list.get(i).getMonthly_personal_income();
			sum = sum+Double.valueOf(monthly_personal_income);
		}
		return sum;
	}
}
