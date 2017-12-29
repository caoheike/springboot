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
/**
 * 
 * @ClassName: NanNingSocialSecurityService  
 * @Description: TODO  
 * @author: fangshuang
 * @date 2017年12月29日  
 *
 */
@Service
public class NanNingSocialSecurityService {
	@SuppressWarnings("unused")
	@Autowired 
	private application applicat;
	private Logger logger = LoggerFactory.getLogger(GuiYangAccumulationfundService.class);
	Date date=new Date();
	DecimalFormat df= new DecimalFormat("#.00");
	public Map<String, Object> getDeatilMes(HttpServletRequest request, String userCard, String password, String socialCard,String idCardNum,String UUID) {
        Map<String, Object> map = new HashMap<>(10);
        Map<String, Object> dataMap = new HashMap<>(10);
    	Map<String,Object> baseInfo = new HashMap<String, Object>(10);
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
            //错误提示
            boolean isFind = DriverUtil.waitById("status11",driver,5);
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
            final String shuRu = "请输入个人编号或社保卡";
            if(!driver.getPageSource().contains(shuRu)){
            	logger.warn("当前网络繁忙，请刷新后重试");
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
                return map;
            }
            WebElement nextlogon = driver.findElement(By.id("text1"));
            nextlogon.sendKeys(socialCard);
            WebElement lastlogon = driver.findElement(By.id("button1"));
            lastlogon.click();
            final String cantdengLu = "您录入的个人编号、社保卡号、医保卡号有误，不能登录";
            if(driver.getPageSource().contains(cantdengLu)){
            	logger.warn("社保账号错误，请重试！");
                map.put("errorCode", "0001");
                map.put("errorInfo", "社保账号错误，请重试！");
                return map;
            }
            
            Thread.sleep(1000);
           //再次确认社保账号页面，有的账号没有这个页面
            final String dltitle = "dl_title";
            final int countS = 5;
            if(DriverUtil.waitById(dltitle,driver,countS)){
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
            //PushSocket.push(dataMap, UUID, "0000");
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
            final String mingxiInfo = "个人缴费明细信息";
            if(!driver.getPageSource().contains(mingxiInfo)){
            	logger.warn("南宁社保基本信息获取失败");
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
                return map;
            }
            String yeshutest = driver.findElement(By.id("unieap_grid_view_toolbar_0")).findElements(By.tagName("table")).get(4).getText();  
            
            int num = yeshutest.indexOf("条记录");
            int num2 = yeshutest.indexOf("条记录",num+1);
            int tiaoshu = Integer.parseInt(yeshutest.substring(num+5, num2));  
            //确定页数
            int yeshu = tiaoshu%10>0?tiaoshu/10+1:tiaoshu/10;
            SecurityBean yanglao = null;
            SecurityBean yiliao = null;
            SecurityBean shiye = null;
            SecurityBean gongshang = null;
            SecurityBean shengyu = null;
            
            

            for(int i=1;i<=yeshu;i++){
            	WebElement tdtable = driver.findElement(By.id("unieap_grid_View_0"));  
            	List<WebElement> tdlist = tdtable.findElements(By.tagName("td"));
            	//int month_count=1;
            	final int count = 7;
    			for(int td=4;td<=tdlist.size();td=td+count){    				
    				String year = tdtable.findElements(By.tagName("td")).get(td-1).getText().substring(0, 4);
    				String type = tdtable.findElements(By.tagName("td")).get(td).getText();

    				
        			if("基本养老保险".equals(type)){     
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
        			if("失业保险".equals(type)){     
            			shiye = new SecurityBean();
            			shiye.setCompany_name(tdtable.findElements(By.tagName("td")).get(td-2).getText());
            			shiye.setYear(year);
        				String monthly_personal_income = tdtable.findElements(By.tagName("td")).get(td+2).getText();
        				shiye.setMonthly_personal_income(monthly_personal_income);
        				shiye.setLast_pay_date(tdtable.findElements(By.tagName("td")).get(td-1).getText());
        				shiyeList.add(shiye);
        			}
        			if("工伤保险".equals(type)){     
            			gongshang = new SecurityBean();
            			gongshang.setCompany_name(tdtable.findElements(By.tagName("td")).get(td-2).getText());
            			gongshang.setYear(year);
        				String monthly_personal_income = tdtable.findElements(By.tagName("td")).get(td+2).getText();
        				gongshang.setMonthly_personal_income(monthly_personal_income);
        				gongshang.setLast_pay_date(tdtable.findElements(By.tagName("td")).get(td-1).getText());
        				gongshList.add(gongshang);
        			}
        			if("生育保险".equals(type)){     
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
            //养老保险缴费余额
            double endowmentInsuranceAmount = getSum(yanglaoList);
            //失业保险缴费余额
            double unemploymentInsuranceAmount = getSum(shiyeList);
            //生育保险缴费余额
            double maternityInsuranceAmount = getSum(shengyuList);
            //工伤保险缴费余额
            double accidentInsuranceAmount = getSum(gongshList);

            
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
	
	/**
	 * 对集合进行排序
	 * @param list
	 * @param securityBean
	 * @return
	 */	
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
			      if(nowyear.equals(nowyear1)){			    	  
			    	  num=0;
			      }
			      return num;
		      }
		});
		return list;
		      
	}
	
	/**
	 * 解析数据
	 * @param list
	 * @param num
	 * @return
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
	    	Map<String,Object> baseInfo = new HashMap<String, Object>(10);

			baseInfo.put("name", "");
			baseInfo.put("identityCards", "");
			baseInfo.put("sex", "");
			baseInfo.put("birthDate", "");
			baseInfo.put("nation", "");
			baseInfo.put("country", "");
			baseInfo.put("personalIdentity", "");
			baseInfo.put("workDate", "");
			baseInfo.put("residenceType","");
			baseInfo.put("residenceAddr", "");
			baseInfo.put("residencePostcodes", "");
			baseInfo.put("contactAddress", "");
			baseInfo.put("contactPostcodes", "");
			baseInfo.put("queryMethod", "");
			baseInfo.put("email", "");
			baseInfo.put("educationalBackground", "");
			baseInfo.put("telephone", "");
			baseInfo.put("phoneNo", "");
			baseInfo.put("income", "");
			baseInfo.put("documentType", "");
			baseInfo.put("documentNumber", "");
			baseInfo.put("bankName", "");
			baseInfo.put("bankNumber", "");
			baseInfo.put("paymentPersonnelCategory", "");
			baseInfo.put("insuredPersonCategory", "");
			baseInfo.put("retireType", "");
			baseInfo.put("retireDate", "");
			baseInfo.put("sentinelMedicalInstitutions1", "");
			baseInfo.put("sentinelMedicalInstitutions2","");
			baseInfo.put("sentinelMedicalInstitutions3", "");
			baseInfo.put("sentinelMedicalInstitutions4", "");
			baseInfo.put("sentinelMedicalInstitutions5", "");
			baseInfo.put("specialDisease", "");
			//失业保险缴费余额
			baseInfo.put("unemploymentInsuranceAmount", unemploymentInsurance);
			//养老保险缴费余额
			baseInfo.put("endowmentInsuranceAmount", endowmentInsurance);
			//生育保险缴费余额
			baseInfo.put("maternityInsuranceAmount", maternityInsurance);
			//工伤保险缴费余额
			baseInfo.put("accidentInsuranceAmount", accidentInsurance);
			//医保余额
			//医疗保险缴费余额
			baseInfo.put("medicalInsuranceAmount", medicalInsurance);
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
