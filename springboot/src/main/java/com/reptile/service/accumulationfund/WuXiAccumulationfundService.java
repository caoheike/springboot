package com.reptile.service.accumulationfund;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
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
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.reptile.util.Dates;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
/**
 * 
 * @ClassName: WuXiAccumulationfundService  
 * @Description: TODO  
 * @author: lusiqin
 * @date 2018年1月2日  
 *
 */
@Service
public class WuXiAccumulationfundService {

    private static Object numFormat;
	private Logger logger = LoggerFactory.getLogger(WuXiAccumulationfundService.class);

    public Map<String, Object> getDetailMes(HttpServletRequest request, String idCard, String userName, String passWord, String cityCode) {
        Map<String, Object> map = new HashMap<>(16);
        Map<String, Object> dataMap = new HashMap<>(16);
        WebClient webClient = new WebClientFactory().getWebClient();
        try {

            List<String> alert = new ArrayList<>();
            CollectingAlertHandler alertHandler = new CollectingAlertHandler(alert);
            webClient.setAlertHandler(alertHandler);
            HtmlPage page = webClient.getPage("http://58.215.195.18:10010/login_person.jsp?");
            //选择身份证登录
            HtmlSelect select=(HtmlSelect) page.getElementById("logontype");
            select.getOption(0).setSelected(true);
            //身份证号
            page.getElementById("loginname").setAttribute("value", "321281198912312738"); 
            //密码
            page.getElementById("password").setAttribute("value", "891231");
            //验证码
    	    HtmlImage image=   (HtmlImage) page.getElementById("kaptcha-img1");
    		BufferedImage read = image.getImageReader().read(0);
    		ImageIO.write(read, "png", new File("C://aa.png"));
    		Map<String, Object> code = MyCYDMDemo.getCode("C://aa.png");
    		String vecCode = code.get("strResult").toString();
    		page.getElementByName("_login_checkcode").setAttribute("value", vecCode);
    		//登录
    		DomElement loginimg=page.getElementByName("image");
             
            HtmlPage loadPage = (HtmlPage) loginimg.click();
            Thread.sleep(9000);
            String str="登录错误";
            if(loadPage.asXml().contains(str)) {
            	dataMap.put("errorCode", "0002");
            	dataMap.put("errorInfo", " 登录失败");
	        	System.out.println("登录失败");
            }else {
            HtmlPage basicInfos=webClient.getPage("http://58.215.195.18:10010/zg_info.do?temp="+System.currentTimeMillis());
            Map<String,Object> parseBaseInfo=parseBaseInfo(basicInfos);
            
            HtmlPage flowspage= webClient.getPage("http://58.215.195.18:10010/mx_info.do?flag=1&temp="+System.currentTimeMillis());
            List<Map<String,Object>> flows=flows(flowspage);
         
            HtmlPage loanspage= webClient.getPage("http://58.215.195.18:10010/grdk_query.do?temp="+System.currentTimeMillis());
            Map<String, Object> loans=loans(loanspage);
                
            //个人信息
                dataMap.put("basicInfos", parseBaseInfo);
                //流水
                dataMap.put("flows", flows);
                //贷款信息  无贷款信息测试
                dataMap.put("loans", loans);
                map.put("data", dataMap);
                map.put("userId", idCard);
                map.put("city", cityCode);
                map.put("cityName", "无锡");
                map.put("insertTime", Dates.currentTime());
            }
        //数据推送
        map = new Resttemplate().SendMessage(map,"http://192.168.3.16:8089/HSDC/person/accumulationFund");
        } catch (Exception e) {
            logger.warn("无锡公积金认证失败", e);
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
    private  List<Map<String,Object>> flows(HtmlPage flowspage) {
    	HtmlSubmitInput submit=flowspage.getElementByName("submit");
    	List<Map<String,Object>> flows = new ArrayList<Map<String,Object>>();
    	try {
			HtmlPage detailflowspage= submit.click();
			Thread.sleep(5000);
   	     List<String> itemList = new ArrayList<>();
   	     itemList.add(detailflowspage.getElementsByTagName("table").get(0).asXml());
		 for (int i = 0; i < itemList.size(); i++) {
			List<List<String>> list = table(itemList.get(i));
				for (int j = list.size()-1; j >0; j--) {
				List<String> trItem = list.get(j); 
				if(trItem.get(3).contains("正常汇缴")){
					Map<String,Object> item = new HashMap<String, Object>(16);
					
					//操作时间（2015-08-19）
					item.put("operatorDate",datetype(trItem.get(1),"1"));
					//操作金额
					item.put("amount",trItem.get(4));
					//操作类型
					item.put("type","汇缴");
					//业务描述（汇缴201508公积金）
					item.put("bizDesc","汇缴" + datetype(trItem.get(2),"4")+"公积金");
					//单位名称
					item.put("companyName",trItem.get(0));
					//缴费月份
					item.put("payMonth",datetype(trItem.get(2),"1"));
					flows.add(item);
				}
			}
		}
    	} catch (Exception e) {
			e.printStackTrace();
		}
   		return flows;
   	}
	private Map<String, Object> parseBaseInfo(HtmlPage basicInfo) {
        HtmlTable table = (HtmlTable) basicInfo.getElementsByTagName("table").get(0);
    		
    	List<List<String>> list =table(table.asXml());
		Map<String,Object> baseInfo = new HashMap<String, Object>(16);
		try {
			//用户姓名
		baseInfo.put("name",list.get(1).get(3));
		//身份证号码
		baseInfo.put("idCard",list.get(2).get(1));
		//单位公积金账号
		baseInfo.put("companyFundAccount","");
		//个人公积金账号
		baseInfo.put("personFundAccount","");
		//公司名称
		baseInfo.put("companyName",list.get(2).get(3));
		//个人公积金卡号
		baseInfo.put("personFundCard","");
		//缴费基数
		baseInfo.put("baseDeposit",datetype(list.get(5).get(1),"2"));
		//公司缴费比例
		baseInfo.put("companyRatio",list.get(7).get(1));
		//个人缴费比例
		baseInfo.put("personRatio",list.get(6).get(1));
		//个人缴费金额
		baseInfo.put("personDepositAmount","");
		//公司缴费金额
		baseInfo.put("companyDepositAmount","");
		//最后缴费日期
		baseInfo.put("lastDepositDate",list.get(8).get(1));
		String balance=datetype(list.get(4).get(1),"2");
		//余额
		baseInfo.put("balance",balance.substring(0, balance.length()-2));
		//状态（正常）
		baseInfo.put("status","无");
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
    	String str="当前页:0/0";
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
    		loans.put("loanAmount",  datetype(list.get(2).get(0),"2"));
    		//最近还款日期
    		loans.put("lastPaymentDate", list.get(4).get(0));
    		//还款状态
    		loans.put("status",  "");
    		//贷款余额
    		loans.put("loanBalance",  datetype(list.get(3).get(1),"2"));
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
    /**
	   * 获取图形验证码
	   * */
	  public Map<String, Object> getImageCode(HttpServletRequest request){
		  Map<String, Object> map = new HashMap<String, Object>(16);
		  Map<String,String> mapPath=new HashMap<String, String>(16);
	        HttpSession session = request.getSession();
	        
	        WebClient webClient=new WebClientFactory().getWebClient();
	        //=============图形验证码=====================
	        try {
		   UnexpectedPage page7 = webClient.getPage("http://58.215.195.18:10010/jcaptcha?tmp=0.333");
           String path = request.getServletContext().getRealPath("/vecImageCode");
           File file = new File(path);
           if (!file.exists()) {
               file.mkdirs();
           }
           String fileName = "XZCode" + System.currentTimeMillis() + ".png";
           BufferedImage bi = ImageIO.read(page7.getInputStream());
           ImageIO.write(bi, "png", new File(file, fileName));
           
           mapPath.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/vecImageCode/" + fileName);
           map.put("data",mapPath);
           map.put("errorCode", "0000");
           map.put("errorInfo", "验证码获取成功");
           session.setAttribute("KM-WebClient", webClient);
          /// session.setAttribute("XZ-page", page);
	        }catch (Exception e) {
	        	logger.warn("无锡住房公积金",e);
       		map.put("errorCode", "0001");
              map.put("errorInfo", "网络连接异常!");
  			e.printStackTrace();
			}
		return map; 
}
	  
		  private static String datetype(String amount,String type) throws ParseException{
				String amountend="";
				String str ="1";
				String str1 ="2";
				String str2 ="3";
				String str3 ="4";
		    	if(type.equals(str)) {
		    		amountend= amount.replace(".", "-");
				}else if(type.equals(str1)) {
					amountend=amount.replace(",", "");
				}else if(type.equals(str2)) {
					amountend=amount.replace("-", "");
				}else if(type.equals(str3)) {
					amountend=amount.replace(".", "");
				}
				return amountend;
			 }
}
 
