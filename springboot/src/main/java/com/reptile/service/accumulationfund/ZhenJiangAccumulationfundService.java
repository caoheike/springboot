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

@Service
public class ZhenJiangAccumulationfundService {

	private Logger logger = LoggerFactory.getLogger(ZhenJiangAccumulationfundService.class);

/*    *//**
	   * 获取图形验证码
 * @param idCardNum 
	   * *//*
	  public Map<String, Object> getImageCode(HttpServletRequest request){
		  Map<String, Object> map = new HashMap<String, Object>();
		  Map<String,String> mapPath=new HashMap<String, String>();
	        HttpSession session = request.getSession();
	        
	        WebClient webClient=new WebClientFactory().getWebClient();
	        //=============图形验证码=====================
	        try {
			HtmlPage page=webClient.getPage(new URL("http://222.172.223.90:8081/kmnbp/"));
			Thread.sleep(500);
			UnexpectedPage page7 = webClient.getPage("http://222.172.223.90:8081/kmnbp/vericode.jsp");

           String path = request.getServletContext().getRealPath("/vecImageCode");
           File file = new File(path);
           if (!file.exists()) {
               file.mkdirs();
           }
           String fileName = "XZCode" + System.currentTimeMillis() + ".png";
           BufferedImage bi = ImageIO.read(page7.getInputStream());
           ImageIO.write(bi, "png", new File(file, fileName));
           
           mapPath.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/vecImageCode/" + fileName);
           System.out.println(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/vecImageCode/"+fileName);
           map.put("data",mapPath);
           map.put("errorCode", "0000");
           map.put("errorInfo", "验证码获取成功");
           session.setAttribute("KM-WebClient", webClient);
          /// session.setAttribute("XZ-page", page);
	        }catch (Exception e) {
	        	logger.warn("镇江住房公积金",e);
       		map.put("errorCode", "0001");
              map.put("errorInfo", "网络连接异常!");
  			e.printStackTrace();
			}
		return map; 
}*/
	  
	  public Map<String, Object> getDetailMes(HttpServletRequest request, String idCard,String passWord,String cityCode, String idCardNum) {
	        Map<String, Object> map = new HashMap<>();
	        Map<String, Object> dataMap = new HashMap<>();
	        WebClient webClient = new WebClientFactory().getWebClient();
	        try {
 
	            HtmlPage page = webClient.getPage("http://www.zjgjj.cn/searchPersonLogon.do?");
	            HtmlSelect select=(HtmlSelect) page.getElementById("select");//选择身份证登录
	            select.getOption(1).setSelected(true);
	            page.getElementById("spcode").setAttribute("value", idCard); //身份证号
	            page.getElementById("sppassword").setAttribute("value", passWord);//密码
	            //验证码
			    HtmlImage image=   (HtmlImage) page.getByXPath("//*[@id=\'login\']/form/table/tbody/tr[6]/td[2]/span/img").get(0);
				BufferedImage read = image.getImageReader().read(0);
				ImageIO.write(read, "png", new File("C://aa.png"));
				Map<String, Object> code = MyCYDMDemo.getCode("C://aa.png");
				String vecCode = code.get("strResult").toString();
				page.getElementById("rand").setAttribute("value", vecCode);
				//登录
				DomNodeList<DomElement> a = page.getElementsByTagName("a");
				//System.out.println("page===="+page.asXml()+a.get(0));
	            HtmlPage loadPage = a.get(0).click();
	            Thread.sleep(5000);
	            System.out.println(loadPage.asXml());
	            if(!(loadPage.asXml().contains("退出系统"))) {
	            	dataMap.put("errorCode", "0002");
	            	dataMap.put("errorInfo", " 登录失败");
		        	System.out.println("登录失败");
	            }else {
	            	
	            HtmlPage basicInfos=webClient.getPage("http://www.zjgjj.cn/searchGrye.do?logon="+System.currentTimeMillis());
	            Map<String,Object> parseBaseInfo=parseBaseInfo(basicInfos);
	            
	            HtmlPage Flowspage= webClient.getPage("http://www.zjgjj.cn/searchGrmx.do?logon="+System.currentTimeMillis());
	            List<Map<String,Object>> Flows=Flows(webClient,Flowspage);
	         
	            HtmlPage Loanspage= webClient.getPage("http://www.zjgjj.cn/searchGrhkmx.do?logon="+System.currentTimeMillis());
	            Map<String, Object> Loans=Loans(Loanspage);
	                
	                dataMap.put("basicInfos", parseBaseInfo);//个人信息
	                dataMap.put("flows", Flows);//流水
	                dataMap.put("loans", Loans);//贷款信息  无贷款信息测试
	                map.put("data", dataMap);
	                map.put("userId", idCardNum);
	                map.put("city", cityCode);
	                map.put("cityName", "镇江");
	                map.put("insertTime", Dates.currentTime());
	            }
	          //数据推送
	         // map = new Resttemplate().SendMessage(map,"http://192.168.3.16:8089/HSDC/person/accumulationFund");//本地
	          map = new Resttemplate().SendMessage(map,"http://117.34.70.217:8080/HSDC/person/accumulationFund");//测试
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

		   
		    private  List<Map<String,Object>> Flows(WebClient webClient, HtmlPage flowspage) {
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
		   					Map<String,Object> item = new HashMap<String, Object>();
		   					item.put("operatorDate",trItem.get(1));//操作时间（2015-08-19）
							item.put("amount",numFormat(trItem.get(4)));//操作金额
		   					String type = "";
		   					if(trItem.get(3).contains("正常汇缴")){
		   						type = "汇缴";
		   					}else{
		   						type = "补缴";
		   					}
		   					item.put("type",type);//操作类型
		   					item.put("bizDesc",type + numFormatyear(trItem.get(2))+"公积金");//业务描述（汇缴201508公积金）
		   					item.put("companyName","");//单位名称
		   					item.put("payMonth",trItem.get(2));//缴费月份
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
		        Map<String,Object> baseInfo = new HashMap<String, Object>();
		        
		        List<List<String>> list =table(table.asXml());
				try {
				baseInfo.put("name","");//用户姓名
				baseInfo.put("idCard",list.get(0).get(0));//身份证号码
				baseInfo.put("companyFundAccount","");//单位公积金账号
				baseInfo.put("personFundAccount","");//个人公积金账号
				baseInfo.put("companyName","");//公司名称
				baseInfo.put("personFundCard","");//个人公积金卡号
				baseInfo.put("baseDeposit", numFormat(list.get(0).get(1)));//缴费基数
				baseInfo.put("companyRatio","");//公司缴费比例
				baseInfo.put("personRatio","");//个人缴费比例
				baseInfo.put("personDepositAmount","");//个人缴费金额
				baseInfo.put("companyDepositAmount","");//公司缴费金额
				baseInfo.put("lastDepositDate",list.get(2).get(0));//最后缴费日期
				baseInfo.put("balance",numFormat(list.get(1).get(1)));//余额
				baseInfo.put("status",list.get(2).get(1));//状态（正常）
				} catch (ParseException e) {
					// TODO Auto-generated catch block
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
		    public Map<String,Object> Loans(HtmlPage loanspage) throws Exception{
		    	
		    	Map<String,Object> loans = new HashMap<String, Object>();
		    	if(!loanspage.asText().contains("根据您提供的身份证号码未查询到您的贷款信息")){
		    		String table = loanspage.getElementsByTagName("table").get(0).asXml();
		    		List<List<String>> list = table(table);
		    		loans.put("loanAccNo", "");//贷款账号
		    		loans.put("loanLimit", list.get(2).get(1));//贷款期限
		    		loans.put("openDate",   list.get(1).get(0));//开户日期
		    		loans.put("loanAmount",  numFormat(list.get(2).get(0)));//贷款总额
		    		loans.put("lastPaymentDate", list.get(4).get(0));//最近还款日期
		    		loans.put("status",  "");//还款状态
		    		loans.put("loanBalance",  numFormat(list.get(3).get(1)));//贷款余额
		    		loans.put("paymentMethod", list.get(5).get(0));//还款方式
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
 
