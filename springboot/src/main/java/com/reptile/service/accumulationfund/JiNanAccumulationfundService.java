package com.reptile.service.accumulationfund;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.reptile.model.AccumulationFlows;
import com.reptile.util.DriverUtil;
import com.reptile.util.GetMonth;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.application;
/**
 * 
 * @ClassName: JiNanAccumulationfundService  
 * @Description: TODO  
 * @author: fangshuang
 * @date 2017年12月29日  
 *
 */
@Service
public class JiNanAccumulationfundService {
	@Autowired 
	private application applicat;
	private Logger logger = LoggerFactory.getLogger(GuiYangAccumulationfundService.class);
	Date date=new Date();
	List<String> alert=new ArrayList<>();
	DecimalFormat df= new DecimalFormat("#.00");
    CollectingAlertHandler alertHandler=new CollectingAlertHandler(alert);
	@SuppressWarnings("unused")
	public Map<String, Object> getDeatilMes(HttpServletRequest request, String userCard, String password,String idCardNum) {
        Map<String, Object> map = new HashMap<>(10);
        Map<String, Object> dataMap = new HashMap<>(10);
        Map<String, Object> loansdata = new HashMap<>(10);
    	Map<String,Object> baseInfo = new HashMap<String, Object>(10);
    	List<Object> infoList=new ArrayList<Object>();
    	List<Object> loansList=new ArrayList<Object>();
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
        try {
        	PushState.state(idCardNum, "socialSecurity", 100);
        	//登录页面
			driver.get("http://123.233.117.50:801/jnwt/indexPerson.jsp");	
			Thread.sleep(2000);
	        
			WebElement username = driver.findElements(By.tagName("input")).get(0);
			username.sendKeys(userCard);
			WebElement passWord = driver.findElements(By.tagName("input")).get(1);
			passWord.sendKeys(password);			
			
			
			
			WebElement captchaImg  = driver.findElements(By.tagName("img")).get(0);
			File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			BufferedImage  fullImg = ImageIO.read(screenshot);
			//坐标
			Point point = captchaImg.getLocation();
			//宽
			int eleWidth = captchaImg.getSize().getWidth();
			if(eleWidth==0){
				logger.warn("网络繁忙，请刷新页面后重试！");
            	map.put("errorCode", "0001");
            	map.put("errorInfo", "网络繁忙，请刷新页面后重试！");
            	return map;
			}
			//高
			int eleHeight = captchaImg.getSize().getHeight();
			BufferedImage eleScreenshot= fullImg.getSubimage(point.getX(), point.getY(),eleWidth, eleHeight);            
            ImageIO.write(eleScreenshot, "png", new File("C://aa.png"));
            Map<String, Object> codes = MyCYDMDemo.getCode("C://aa.png");
            String vecCode = codes.get("strResult").toString();
            WebElement imgCodeInput = driver.findElements(By.tagName("input")).get(2);
            imgCodeInput.sendKeys(vecCode);
			WebElement button = driver.findElements(By.tagName("button")).get(0);
			button.click();
			try{
				//监控弹框
    			Alert alt = driver.switchTo().alert();
        		String errorInfo = alt.getText();
        		final String chaoShi = "超时了";
        		if (errorInfo!=null&&errorInfo.contains(chaoShi)){
                	logger.warn("连接超时，请重新登陆！");
                	map.put("errorCode", "0001");
                	map.put("errorInfo", "连接超时，请重新登陆！");
                	return map;
                }
        		alt.accept();
    		}catch(Exception e){
    			Thread.sleep(1000);
    		}
			
            /*
             * 基本信息
             */
            dataMap = getBaseInfo(driver);
            /*
             * 明细信息
             */
    		driver.get("http://123.233.117.50:801/jnwt/init.summer?_PROCID=60020010");	
    		Thread.sleep(2000);
    		boolean isFind = DriverUtil.waitById("BegDate",driver,5);
            if(isFind==true){
            	String today = GetMonth.today1();
            	int year = Integer.valueOf(today.substring(0,4))-3;
            	String startDate = String.valueOf(year)+"-01-01";
            	//起始时间
            	WebElement begDate = driver.findElement(By.id("BegDate"));
            	begDate.clear();
            	begDate.sendKeys(startDate);          	
            	//终止时间
    			WebElement endDate = driver.findElement(By.id("EndDate"));
    			endDate.clear();
    			endDate.sendKeys(today);
    			WebElement queryButton = driver.findElement(By.id("b_query"));
    			queryButton.click();
    			Thread.sleep(2000);
    			String page = driver.findElement(By.id("list_datalist_buttons")).getText();
    			System.out.println(page);
    			int num = page.indexOf("1 / ");
    			int num1 = page.indexOf(" 页 共 ");
    			String yeshu = page.substring(num+4, num1);
    			boolean isTable = DriverUtil.waitById("datalist",driver,5);
                if(isTable==true){
                	AccumulationFlows flows = new AccumulationFlows();                	               	
                	for(int j=1;j<=Integer.parseInt(yeshu);j++){
                		WebElement infoTable = driver.findElement(By.id("datalist"));
                		List<WebElement>  tdList = infoTable.findElements(By.tagName("td"));
                		final int d = 10;
	                	for(int i=1;i<tdList.size();i=i+d){
	                		System.out.println();
	                		String type1 = tdList.get(i+2).getText();
	        				if(type1.indexOf("汇缴")==-1&&type1.indexOf("补缴")==-1){
	        					continue;
	        				}
	        				if(type1.contains("补缴")){
	        					type1="补缴";
	        				}
	        				if(type1.contains("汇缴")){
	        					type1="汇缴";
	        				}
	        				String time = tdList.get(i+1).getText().substring(0, 7).replace("-", "");
	        				//业务描述
	        				String bizDesc = type1+time+"公积金";
	        				//操作金额
	        				flows.setAmount(tdList.get(i+4).getText().replace(",", ""));
	        				flows.setBizDesc(bizDesc);
	        				//操作时间
	        				flows.setOperatorDate(tdList.get(i+1).getText());
	        				//缴费月份
	        				flows.setPayMonth(time);
	        				flows.setType(type1);
	        				//公司名
	        				flows.setCompanyName("");
	        				JSONObject jsonObject = JSONObject.fromObject(flows);
	    	    			String jsonBean = jsonObject.toString();
	    	    			System.out.println(jsonBean);
	    	    			infoList.add(jsonBean);
	        			}
	                	driver.findElements(By.tagName("button")).get(3).click();
                	}
        			dataMap.put("flows", infoList); 
                	
        			
                }else{
                	logger.warn("明细信息获取失败");
                	map.put("errorCode", "0001");
                	map.put("errorInfo", "明细信息获取失败，请重试！");
                }
            }else{
            	logger.warn("明细信息获取失败");
            	map.put("errorCode", "0001");
            	map.put("errorInfo", "明细信息获取失败，请重试！");
            }
            /*
			 * 贷款信息查询
			 */
			driver.get("http://123.233.117.50:801/jnwt/init.summer?_PROCID=60050005");	
    		Thread.sleep(2000);
    		WebElement loanaccnum = driver.findElement(By.id("loanaccnum"));
    		loanaccnum.sendKeys(userCard);
    		WebElement query = driver.findElement(By.id("b_query"));
    		query.click();
    		try{
    			//监控弹框
    			Alert alt = driver.switchTo().alert();
        		String errorInfo1 = alt.getText();
        		final String heTong = "贷款合同信息不存在";
        		if (errorInfo1!=null&&errorInfo1.contains(heTong)){
                	dataMap.put("loans", loansList); 
                }
        		alt.accept();
    		}catch(Exception e){
    			loansdata.put("loanAccNo", "");
            	loansdata.put("loanLimit", "");
            	loansdata.put("openDate", "");
            	loansdata.put("loanAmount", "");
            	loansdata.put("lastPaymentDate", "");
            	loansdata.put("status", "");
            	loansdata.put("loanBalance", "");
            	loansdata.put("paymentMethod", "");
            	loansList.add(loansdata);
            	dataMap.put("loans", loansList);  
    		}
            	
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
        map.put("cityName", "济南");
        map.put("city", "013");
        map.put("userId", idCardNum);
        map.put("createTime", today);
        Resttemplate resttemplate=new Resttemplate();
        map = resttemplate.SendMessage(map, applicat.getSendip()+"/HSDC/person/accumulationFund");
        
        /*if(map!=null&&"0000".equals(map.get("errorCode").toString())){
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
	public Map<String, Object> getBaseInfo(WebDriver driver) throws InterruptedException{
		Map<String, Object> dataMap = new HashMap<>(10);
		Map<String, Object> data = new HashMap<>(10);		
		driver.get("http://123.233.117.50:801/jnwt/init.summer?_PROCID=60020009");	
		Thread.sleep(2000);
		//错误提示
		boolean isFind = DriverUtil.waitById("ct_form",driver,5);
        if(isFind==true){
        	data.put("companyName", driver.findElement(By.id("UnitAccName")).getAttribute("value"));
        	//个人缴费金额
        	data.put("personDepositAmount", driver.findElement(By.id("MonPaySum")).getAttribute("value"));
        	//缴费基数
        	data.put("baseDeposit", driver.findElement(By.id("BaseNumber")).getAttribute("value"));
        	//个人公积金卡号
        	data.put("personFundCard", driver.findElement(By.id("AccNum")).getAttribute("value"));
        	//公司缴费比例
        	data.put("companyRatio", "");
        	//个人缴费比例
        	data.put("personRatio", "");
        	//公司公积金账号
        	data.put("companyFundAccount", driver.findElement(By.id("UnitAccNum")).getAttribute("value"));
        	//公司缴费金额
        	data.put("companyDepositAmount", "");
        	//最后缴费日期
        	data.put("lastDepositDate", driver.findElement(By.id("LastPayDate")).getAttribute("value"));
        	//余额
        	data.put("balance", driver.findElement(By.id("Balance")).getAttribute("value"));
        	String state = driver.findElement(By.id("PerAccState")).getAttribute("value");
        	final String a = "1";
        	final String b = "2";
        	final String c = "3";
        	final String d = "4";
        	final String e = "8";
        	final String f = "9";
        	if(a.equals(state)){
        		state="正常";
        	}else if(b.equals(state)){
        		state="封存";
        	}else if(c.equals(state)){
        		state="托管";
        	}else if(d.equals(state)){
        		state="待查";
        	}else if(e.equals(state)){
        		state="转移锁定";
        	}else if(f.equals(state)){
        		state="销户";
        	}       	
        	//状态        
        	data.put("status", state);	
        	data.put("userCard", driver.findElement(By.id("AccNum")).getAttribute("value"));
        	data.put("personFundAccount", "");
        	data.put("name", driver.findElement(By.id("AccName")).getAttribute("value"));
        	dataMap.put("basicInfos", data);
        }else{
        	logger.warn("基本信息获取失败");
        	dataMap.put("errorCode", "0001");
        	dataMap.put("errorInfo", "基本信息获取失败，请重试！");
        	return dataMap;
        }
		
		return dataMap;
	}
	
}
     
