package com.reptile.service.accumulationfund;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.reptile.util.Dates;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;

@Service
public class YuLinAccumulationfundService {
	private Logger logger =  LoggerFactory.getLogger(YuLinAccumulationfundService.class);
	
	@Autowired
	private application application;
	
	/**
	 * 获取玉林公积金详情
	 * @param request
	 * @param idCard 用户名
	 * @param passWord 密码
	 * @param cityCode 城市编码
	 * @return
	 */
	public Map<String, Object> doGetDetail(HttpServletRequest request,
			String idCard, String passWord, String cityCode) {
		
		Map<String, Object> data = new HashMap<>();
        WebClient webClient = new WebClientFactory().getWebClient();
        try {
			HtmlPage loginPage = webClient.getPage("http://www.gxylgjj.com/index.asp");
			//获取登录表单
			HtmlForm form = (HtmlForm) loginPage.getElementByName("Login");
			//用户名为身份证号
	        HtmlTextInput  username = form.getInputByName("UserName");
	        username.setValueAttribute(idCard);
	        //密码
	        HtmlPasswordInput password = form.getInputByName("Password");
	        password.setValueAttribute(passWord);
	        
	        //对alert弹框进行监听
	        List<String> list = new ArrayList<String>();
	        CollectingAlertHandler alert = new CollectingAlertHandler(list);
	        webClient.setAlertHandler(alert);
	        //登录
	        HtmlSubmitInput submit = form.getInputByName("Submit");
	        HtmlPage nextPage = (HtmlPage) submit.click();
	        
	        if(list.size() > 0){
        		data.put("errorCode", "0001");
        		data.put("errorInfo", list.get(0));
	        }else{
	        	Map<String, Object> map = new HashMap<>();
	        	Map<String,Object> basicInfos = this.parseBaseInfo(nextPage.getElementsByTagName("table").get(9).asXml());
	        	map.put("basicInfos", basicInfos);
	        	map.put("flows", this.parseFlows(nextPage.getElementsByTagName("table").get(10).asXml(), (String)basicInfos.get("companyName")));
	        	map.put("loans", this.parseLoans(nextPage));
	        	
			    data.put("data", map);
			    data.put("city", cityCode);
			    data.put("cityName", "玉林");
			    data.put("userId", idCard);
			    data.put("insertTime", Dates.currentTime());
			    //数据推送
//			    data = new Resttemplate().SendMessage(data,application.getSendip()+"/HSDC/person/accumulationFund");
			    data = new Resttemplate().SendMessage(data,"http://192.168.3.16:8089/HSDC/person/accumulationFund");
	        }
		} catch (Exception e) {
			logger.warn("获取玉林公积金详情失败",e);
			data.put("errorCode", "0002");
    		data.put("errorInfo", "网络繁忙，请重试！");
		}finally {
            if (webClient != null) {
                webClient.close();
            }
        }
		return data;
	}
	
	/**
	 * 解析基础信息
	 * @param xml
	 * @return
	 */
	public Map<String,Object> parseBaseInfo(String xml){
		List<List<String>> list = table1(xml);
		
		Map<String,Object> baseInfo = new HashMap<String, Object>();
		baseInfo.put("name",list.get(1).get(1));//用户姓名
		baseInfo.put("idCard",list.get(3).get(1));//身份证号码
		baseInfo.put("companyFundAccount","");//单位公积金账号
		baseInfo.put("personFundAccount",list.get(0).get(1));//个人公积金账号
		baseInfo.put("companyName",list.get(2).get(1));//公司名称
		baseInfo.put("personFundCard","");//个人公积金卡号
		baseInfo.put("baseDeposit","");//缴费基数
		baseInfo.put("companyRatio","");//公司缴费比例
		baseInfo.put("personRatio","");//个人缴费比例
		baseInfo.put("personDepositAmount",list.get(6).get(1));//个人缴费金额
		baseInfo.put("companyDepositAmount",list.get(7).get(1));//公司缴费金额
		baseInfo.put("lastDepositDate","");//最后缴费日期
		baseInfo.put("balance",list.get(9).get(1));//余额
		baseInfo.put("status",list.get(10).get(1));//状态（正常）
		
		return baseInfo;
	}
	
	/**
	 * 解析公积金交易流水信息
	 * @param xml
	 * @param company
	 * @return
	 */
	public List<Map<String,Object>> parseFlows(String xml,String company){
		List<Map<String,Object>> flows = new ArrayList<Map<String,Object>>();
		
		List<List<String>> list = table1(xml);
		for (int i = 1; i < list.size(); i++) {
			List<String> trItem = list.get(i); 
			
			Map<String,Object> item = new HashMap<String, Object>();
			item.put("operatorDate",trItem.get(0));//操作时间（2015-08-19）
			item.put("amount",trItem.get(2));//操作金额
			String type = "";
			if(trItem.get(1).equals("单位汇缴")){
				type = "汇缴";
			}else{
				type = trItem.get(1);
			}
			item.put("type",type);//操作类型
			item.put("bizDesc",type + trItem.get(0)+"公积金");//业务描述（汇缴201508公积金）
			item.put("companyName",company);//单位名称
			item.put("payMonth",trItem.get(0));//缴费月份
			
			flows.add(item);
		}
		
		return flows;
	}
	/**
	 * 解析公积金贷款信息
	 * @param xml
	 * @param company
	 * @return
	 * @throws IOException 
	 */
	public Map<String,Object> parseLoans(HtmlPage htmlpage) throws IOException{
		HtmlImage img = (HtmlImage) htmlpage.getElementsByTagName("img").get(0);
		HtmlPage nextPage = (HtmlPage) img.click();
		HtmlTable table = (HtmlTable) nextPage.getElementsByTagName("table").get(9);
		
		List<List<String>> list = table1(table.asXml());
		
		Map<String,Object> loans = new HashMap<String, Object>();
		loans.put("loanAccNo", list.get(3).get(1));//贷款账号
		loans.put("loanLimit", list.get(7).get(1));//贷款期限
		loans.put("openDate",  "");//开户日期
		loans.put("loanAmount", list.get(6).get(1));//贷款总额
		loans.put("lastPaymentDate", "");//最近还款日期
		loans.put("status",  "");//还款状态
		loans.put("loanBalance", list.get(7).get(1));//贷款余额
		loans.put("paymentMethod", "");//还款方式
		
		return loans;
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
			 for (int j=0; j<tds.size(); j++){  
				 String txt = tds.get(j).text();  
				 item.add(txt);
			 }  
			 list.add(item);
		 }
		 
		return list;	
    } 

}
