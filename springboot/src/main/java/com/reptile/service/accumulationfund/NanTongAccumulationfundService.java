package com.reptile.service.accumulationfund;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.reptile.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 
 * @ClassName: NanTongAccumulationfundService  
 * @Description: TODO (南通公积金)
 * @author: xuesongcui
 * @date 2018年1月2日  
 *
 */
@Service
public class NanTongAccumulationfundService {

    private Logger logger = LoggerFactory.getLogger(NanTongAccumulationfundService.class);
    
	private static String success = "0000";
	private static String errorCode = "errorCode";

    public Map<String, Object> getDetailMes(HttpServletRequest request, String idCard, String userName, String passWord, String cityCode,String idCardNum) {
        Map<String, Object> map = new HashMap<>(16);
        Map<String, Object> dataMap = new HashMap<>(16);
        WebClient webClient = new WebClientFactory().getWebClient();
        try {
        	PushState.state(idCardNum, "accumulationFund",100);
            logger.warn("登录南通住房公积金网");
//            String str = "http://58.221.92.98:8080/searchPersonLogon.do?spidno=" + idCard + "&spname=" + URLEncoder.encode(userName) + "&sppassword=" + passWord;

            List<String> alert = new ArrayList<>();
            CollectingAlertHandler alertHandler = new CollectingAlertHandler(alert);
            webClient.setAlertHandler(alertHandler);
            HtmlPage page = webClient.getPage("http://www.ntgjj.com/gjjcx_gr.aspx?UrlOneClass=78");
            page.getElementById("txtCode1").setAttribute("value", idCard);
            page.getElementById("txtUserName1").setAttribute("value", userName);
            page.getElementById("txtPwd1").setAttribute("value", passWord);
            page = page.getElementById("btnsub1").click();
            Thread.sleep(2000);

            if (alert.size() > 0) {
                map.put("errorCode", "0005");
                map.put("errorInfo", alert.get(0));
                return map;
            }

            String pageContext = page.asText();
            HtmlPage loadPage = null;
            int count = 0;
            String personCode = "人员代码";
            String op = "操作";
            if (pageContext.contains(personCode) && pageContext.contains(op)) {
                DomNodeList<DomElement> a = page.getElementsByTagName("a");
                count = a.size();
                loadPage = a.get(a.size() - 1).click();
                Thread.sleep(2000);
            } else {
                loadPage = page;
            }
            logger.warn("判断该账户缴存单位个数：" + count);
            pageContext = loadPage.asText();
            String detailStr = "个人明细查询";
            String basicStr = "个人公积金基本信息";
            if (pageContext.contains(detailStr) && pageContext.contains(basicStr)) {
                logger.warn("登录成功，获取到基本信息");
                HtmlPage page2 = webClient.getPage("http://58.221.92.98:8080/searchGrye.do");
                dataMap.put("basicInfos", this.parseBaseInfo(page2));
                Calendar instance = Calendar.getInstance();
                SimpleDateFormat sim = new SimpleDateFormat("yyyy");
                String format = sim.format(instance.getTime());
                int years = Integer.parseInt(format);
                List<String> itemList = new ArrayList<>();
                logger.warn("获取详细缴纳信息");
                int con = 3;
                for (int i = 0; i < con; i++) {
                    HtmlPage page1 = webClient.getPage("http://58.221.92.98:8080/searchGrmx.do?year=" + years);
                    Thread.sleep(1000);
                    itemList.add(page1.getElementsByTagName("table").get(0).asXml());
                    System.out.println(page1.asText());
                    years--;
                }
                dataMap.put("flows", this.parseFlows(itemList));
                dataMap.put("loans", this.getLoans(webClient));
                map.put("data", dataMap);
                map.put("userId", idCardNum);
                map.put("city", cityCode);
                map.put("cityName", "南通");
                map.put("insertTime", Dates.currentTime());
                //数据推送
                map = new Resttemplate().SendMessage(map,ConstantInterface.port+"/HSDC/person/accumulationFund");

    		    if(map!=null && success.equals(map.get(errorCode).toString())){
    		    	PushState.state(idCardNum, "accumulationFund",300);
    		    	map.put("errorInfo","查询成功");
    		    	map.put("errorCode","0000");
                }else{
                	//--------------------数据中心推送状态----------------------
                	PushState.state(idCardNum, "accumulationFund",200);
                	//---------------------数据中心推送状态----------------------
                	
                    map.put("errorInfo","查询失败");
                    map.put("errorCode","0001");
                	
                }
            } else {
                map.put("errorCode", "0001");
                map.put("errorInfo", "认证过程中出现未知错误");
            }
        } catch (Exception e) {
            logger.warn("南通公积金认证失败", e);
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
     * 获取贷款信息
     * @param webClient
     * @return
     * @throws FailingHttpStatusCodeException
     * @throws MalformedURLException
     * @throws IOException
     */
    public Map<String,Object> getLoans(WebClient webClient) throws Exception{
    	HtmlPage page = webClient.getPage("http://58.221.92.98:8080/searchGrhkmx.do");
    	
    	Map<String,Object> loans = new HashMap<String, Object>(16);
    	System.out.println(page.asText());
    	String str = "根据您提供的身份证号码未查询到您的贷款信息";
    	if(!page.asText().contains(str)){
    		
    		String table = page.getElementsByTagName("table").get(0).asXml();
    		
    		List<List<String>> list = table1(table);
    		//贷款账号
    		loans.put("loanAccNo", "");
    		//贷款期限
    		loans.put("loanLimit", list.get(2).get(1));
    		//开户日期
    		loans.put("openDate",   list.get(1).get(0));
    		//贷款总额
    		loans.put("loanAmount", NanTongAccumulationfundService.numFormat(list.get(2).get(0)));
    		//最近还款日期
    		loans.put("lastPaymentDate", list.get(4).get(0));
    		//还款状态
    		loans.put("status",  "");
    		//贷款余额
    		loans.put("loanBalance", NanTongAccumulationfundService.numFormat(list.get(3).get(1)));
    		//还款方式
    		loans.put("paymentMethod", list.get(5).get(0));
    	}
    	
    	return loans;
    	
    }
    
    
    /**
	 * 解析基础信息
	 * @param xml
	 * @return
     * @throws ParseException 
	 */
	public Map<String,Object> parseBaseInfo(HtmlPage loadPage) throws ParseException{
		HtmlTable table = (HtmlTable) loadPage.getElementsByTagName("table").get(0);
		
		List<List<String>> list = table1(table.asXml());
		
		Map<String,Object> baseInfo = new HashMap<String, Object>(16);
		//用户姓名
		baseInfo.put("name",list.get(1).get(0));
		//身份证号码
		baseInfo.put("idCard",list.get(1).get(1));
		//单位公积金账号
		baseInfo.put("companyFundAccount",list.get(0).get(1));
		//个人公积金账号
		baseInfo.put("personFundAccount",list.get(2).get(0));
		//公司名称
		baseInfo.put("companyName",list.get(0).get(0));
		//个人公积金卡号
		baseInfo.put("personFundCard","");
		//缴费基数
		baseInfo.put("baseDeposit",NanTongAccumulationfundService.numFormat(list.get(2).get(1)));
		//公司缴费比例
		baseInfo.put("companyRatio","");
		//个人缴费比例
		baseInfo.put("personRatio","");
		//个人缴费金额
		baseInfo.put("personDepositAmount","");
		//公司缴费金额
		baseInfo.put("companyDepositAmount","");
		//最后缴费日期
		baseInfo.put("lastDepositDate",list.get(5).get(0));
		//余额
		baseInfo.put("balance",NanTongAccumulationfundService.numFormat(list.get(3).get(1)));
		//状态（正常）
		baseInfo.put("status",list.get(5).get(1));
		
		return baseInfo;
	}
	
	/**
	 * 解析公积金交易流水信息
	 * @param xml
	 * @param company
	 * @return
	 * @throws ParseException 
	 */
	public List<Map<String,Object>> parseFlows(List<String> itemList) throws ParseException{
		List<Map<String,Object>> flows = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < itemList.size(); i++) {
			
			List<List<String>> list = table1(itemList.get(i));
			
			for (int j = 1; j < list.size(); j++) {
				List<String> trItem = list.get(j); 
				if(trItem.get(3).contains("汇缴") || trItem.get(3).contains("补缴")){
					Map<String,Object> item = new HashMap<String, Object>(16);
					//操作时间（2015-08-19）
					item.put("operatorDate",trItem.get(1));
					//操作金额
					item.put("amount",NanTongAccumulationfundService.numFormat(trItem.get(4)));
					String type = "";
					if(trItem.get(3).contains("汇缴")){
						type = "汇缴";
					}else if(trItem.get(3).contains("补缴")){
						type = "补缴";
					}
					//操作类型
					item.put("type",type);
					//业务描述（汇缴201508公积金）
					item.put("bizDesc",type + trItem.get(2)+"公积金");
					//单位名称
					item.put("companyName","");
					//缴费月份
					item.put("payMonth",trItem.get(2));
					
					flows.add(item);
				}
				
			}
		}
		
		return flows;
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
	  * 去除千分符
	  * @param amount
	  * @return
	  * @throws ParseException
	  */
	 private static String numFormat(String amount) throws ParseException{
		 return amount.replace(",", "");
	 }
	 
    
}
