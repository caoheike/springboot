package com.reptile.service.accumulationfund;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.reptile.util.Dates;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
/**
 * 
 * @ClassName: ZhenJiangAccumulationfundService  
 * @Description: TODO  
 * @author: lusiqin
 * @date 2018年1月2日  
 *
 */
@Service
public class ZhenJiangAccumulationfundService {

	private Logger logger = LoggerFactory.getLogger(ZhenJiangAccumulationfundService.class);

	public Map<String, Object> getDetailMes(HttpServletRequest request, String idCard,String passWord,String cityCode, String idCardNum) {
	        Map<String, Object> map = new HashMap<>(16);
	        Map<String, Object> dataMap = new HashMap<>(16);
	        WebClient webClient = new WebClientFactory().getWebClient();
	        try {
 
	            HtmlPage page = webClient.getPage("http://www.zjgjj.cn/searchPersonLogon.do?");
	            //选择身份证登录
	            HtmlSelect select=(HtmlSelect) page.getElementById("select");
	            select.getOption(1).setSelected(true);
	            //身份证号
	            page.getElementById("spcode").setAttribute("value", idCard); 
	            //密码
	            page.getElementById("sppassword").setAttribute("value", passWord);
	            //验证码
			    HtmlImage image=   (HtmlImage) page.getByXPath("//*[@id=\'login\']/form/table/tbody/tr[6]/td[2]/span/img").get(0);
				BufferedImage read = image.getImageReader().read(0);
				ImageIO.write(read, "png", new File("C://aa.png"));
				Map<String, Object> code = MyCYDMDemo.getCode("C://aa.png");
				String vecCode = code.get("strResult").toString();
				page.getElementById("rand").setAttribute("value", vecCode);
				//登录
				DomNodeList<DomElement> a = page.getElementsByTagName("a");
	            HtmlPage loadPage = a.get(0).click();
	            Thread.sleep(5000);
	            String str="退出系统";
	            System.out.println(loadPage.asXml());
	            if(!(loadPage.asXml().contains(str))) {
	            	dataMap.put("errorCode", "0002");
	            	dataMap.put("errorInfo", " 登录失败");
		        	System.out.println("登录失败");
	            }else {
	            	
	            HtmlPage basicInfos=webClient.getPage("http://www.zjgjj.cn/searchGrye.do?logon="+System.currentTimeMillis());
	            Map<String,Object> parseBaseInfo=parseBaseInfo(basicInfos);
	            
	            HtmlPage flowspage= webClient.getPage("http://www.zjgjj.cn/searchGrmx.do?logon="+System.currentTimeMillis());
	            List<Map<String,Object>> flows=flows(webClient,flowspage);
	         
	            HtmlPage loanspage= webClient.getPage("http://www.zjgjj.cn/searchGrhkmx.do?logon="+System.currentTimeMillis());
	            Map<String, Object> loans=loans(loanspage);
	                
	            //个人信息
	                dataMap.put("basicInfos", parseBaseInfo);
	                //流水
	                dataMap.put("flows", flows);
	                //贷款信息  无贷款信息测试
	                dataMap.put("loans", loans);
	                map.put("data", dataMap);
	                map.put("userId", idCardNum);
	                map.put("city", cityCode);
	                map.put("cityName", "镇江");
	                map.put("insertTime", Dates.currentTime());
	            }
	          map = new Resttemplate().SendMessage(map,"http://117.34.70.217:8080/HSDC/person/accumulationFund"); 
	        } catch (Exception e) {
	            logger.warn("镇江公积金认证失败", e);
	            e.printStackTrace();
	            map.put("errorCode", "0002");
	            map.put("errorInfo", "系统繁忙，请稍后再试");
	        } finally {
	            if (webClient != null) {
	                webClient.close();
	            }
	        }
	        return map;
	    }
	  
	  
	  /**
		  * 去除千分符
		  * @param amount
		  * @return
		  * @throws ParseException
		  */
		 private static String numFormat(String amount) throws ParseException{
			 return amount.replace(",", "");
		 }	 
		 /**
		  * 2007-10-10  去- （20171010）
		  * @param amount
		  * @return
		  * @throws ParseException
		  */
		 private static String numFormatyear(String amount) throws ParseException{
			 return amount.replace("-", "");
		 }

		   
		    private  List<Map<String,Object>> flows(WebClient webClient, HtmlPage flowspage) {
		      	 List<Map<String,Object>> flows = new ArrayList<Map<String,Object>>();
		      	HtmlSelect select=flowspage.getElementByName("select");
		        int num=select.getChildElementCount();
		        for (int n = num-1; n >0; n--) {
		        	select.getOption(n).setSelected(true);
		      	 try {
		      		 HtmlPage nextpage=webClient.getPage("http://www.zjgjj.cn/searchGrmx.do?year="+select.getOption(n).getTextContent()); 
		      		List<List<String>> list=table(nextpage.getElementsByTagName("table").get(0).asXml());
		   			for (int j = list.size()-1; j >1 ; j--) {
		   				List<String> trItem = list.get(j); 
		   				if(trItem.get(3).contains("正常汇缴") || trItem.get(3).contains("其它补缴")|| trItem.get(3).contains("单位补缴")){
		   					Map<String,Object> item = new HashMap<String, Object>(16);
		   					//操作时间（2015-08-19）
		   					item.put("operatorDate",trItem.get(1));
		   					//操作金额
							item.put("amount",numFormat(trItem.get(4)));
		   					String type = "";
		   					if(trItem.get(3).contains("正常汇缴")){
		   						type = "汇缴";
		   					}else{
		   						type = "补缴";
		   					}
		   					//操作类型
		   					item.put("type",type);
		   					//业务描述（汇缴201508公积金）
		   					item.put("bizDesc",type + numFormatyear(trItem.get(2))+"公积金");
		   					//单位名称
		   					item.put("companyName","");
		   					//缴费月份
		   					item.put("payMonth",trItem.get(2));
		   					flows.add(item);
		   				}
		   			}
		   		 } catch (Exception e) {
		   			 // TODO Auto-generated catch block
		   			 e.printStackTrace();
		   		 }}
		   		return flows;
		   	}
		    
			private Map<String, Object> parseBaseInfo(HtmlPage basicInfo) {
		        HtmlTable table = (HtmlTable) basicInfo.getElementsByTagName("table").get(0);
		        Map<String,Object> baseInfo = new HashMap<String, Object>(16);
		        
		        List<List<String>> list =table(table.asXml());
				try {
					//用户姓名
				baseInfo.put("name","");
				//身份证号码
				baseInfo.put("idCard",list.get(0).get(0));
				//单位公积金账号
				baseInfo.put("companyFundAccount","");
				//个人公积金账号
				baseInfo.put("personFundAccount","");
				//公司名称
				baseInfo.put("companyName","");
				//个人公积金卡号
				baseInfo.put("personFundCard","");
				//缴费基数
				baseInfo.put("baseDeposit", numFormat(list.get(0).get(1)));
				//公司缴费比例
				baseInfo.put("companyRatio","");
				//个人缴费比例
				baseInfo.put("personRatio","");
				//个人缴费金额
				baseInfo.put("personDepositAmount","");
				//公司缴费金额
				baseInfo.put("companyDepositAmount","");
				//最后缴费日期
				baseInfo.put("lastDepositDate",list.get(2).get(0));
				//余额
				baseInfo.put("balance",numFormat(list.get(1).get(1)));
				//状态（正常）
				baseInfo.put("status",list.get(2).get(1));
				} catch (ParseException e) {
					e.printStackTrace();
				}
		    	return baseInfo;
		    	}
		    /**
		     * 获取贷款信息
		     * @param loanspage
		     * @return
		     * @throws FailingHttpStatusCodeException
		     * @throws MalformedURLException
		     * @throws IOException
		     */
		    public Map<String,Object> loans(HtmlPage loanspage) throws Exception{
		    	
		    	Map<String,Object> loans = new HashMap<String, Object>(16);
		    	String str="根据您提供的身份证号码未查询到您的贷款信息";
		    	if(!loanspage.asText().contains(str)){
		    		String table = loanspage.getElementsByTagName("table").get(0).asXml();
		    		List<List<String>> list = table(table);
		    		//贷款账号
		    		loans.put("loanAccNo", "");
		    		//贷款期限
		    		loans.put("loanLimit", list.get(2).get(1));
		    		//开户日期
		    		loans.put("openDate",   list.get(1).get(0));
		    		//贷款总额
		    		loans.put("loanAmount",  numFormat(list.get(2).get(0)));
		    		//最近还款日期
		    		loans.put("lastPaymentDate", list.get(4).get(0));
		    		//还款状态
		    		loans.put("status",  "");
		    		//贷款余额
		    		loans.put("loanBalance",  numFormat(list.get(3).get(1)));
		    		//还款方式
		    		loans.put("paymentMethod", list.get(5).get(0));
		    	}
		    	
		    	return loans;
		    	
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
				 for (int i = 0; i < trs.size(); i++) {
					 Elements tds = trs.get(i).select("td");
					 List<String> item = new ArrayList<String>();
					 for (int j = 0; j < tds.size(); j++){
						 String txt = tds.get(j).text().replace(" ", "").replace(" ", "").replace("元", "");
						 item.add(txt);
					 }  
					 list.add(item);
				 }
				return list;	
		    } 
}
 
