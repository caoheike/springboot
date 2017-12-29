package com.reptile.service.accumulationfund;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.reptile.model.AccumulationFlows;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;
/**
 * 
 * @ClassName: NanNingAccumulationfundService  
 * @Description: TODO  
 * @author: fangshuang
 * @date 2017年12月29日  
 *
 */
@Service
public class NanNingAccumulationfundService {
	@Autowired 
	private application applicat;
    private Logger logger = LoggerFactory.getLogger(GuiYangAccumulationfundService.class);
	DecimalFormat df= new DecimalFormat("#.00");
	Date date=new Date();
    @SuppressWarnings({ "rawtypes", "unused" })
	public Map<String, Object> getDeatilMes(HttpServletRequest request,String userCard, String password,String idCardNum) {
        logger.warn("获取南宁公积金数据");
        Map<String, Object> map = new HashMap<>(20);
        Map<String, Object> datamap = new HashMap<>(20);
        Map<String, Object> data = new HashMap<>(20);
        Map<String, Object> loansdata = new HashMap<>(20);
        List<Object> beanList=new ArrayList<Object>();
        WebClient webClient = new WebClientFactory().getWebClient();
        HtmlPage page = null;
        try {
        	List<String> alert=new ArrayList<>();
            CollectingAlertHandler alertHandler=new CollectingAlertHandler(alert);
            webClient.setAlertHandler(alertHandler);
            page = webClient.getPage("http://www.nngjj.com/web/");
            page.getElementById("idcard").setAttribute("value",userCard);
            page.getElementById("password").setAttribute("value",password);
            HtmlPage posthtml = page.getElementById("gjjform").getElementsByTagName("a").get(0).click();
            Thread.sleep(2000);
            System.out.println(alert.size());
            logger.warn("登录南宁住房公积金:"+alert.size());
            if(alert.size()>0){           	
                map.put("errorCode", "0005");
                map.put("errorInfo", alert.get(0));
                alert.remove(0);
                return map;
            }
            System.out.println(posthtml.asText());
            final String zhiGong = "职工住房公积金基本情况";
            if(posthtml.asText().indexOf(zhiGong)==-1){
            	logger.warn("宁波住房公积金获取失败");
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
            }else{
            	/*
            	 * 基本信息
            	 */
            	PushState.state(idCardNum, "accumulationFund", 100);
            	HtmlTable mytable = (HtmlTable) posthtml.getElementsByTagName("table").get(11);
            	String companyName = mytable.getElementsByTagName("td").get(15).getTextContent();
            	String company = mytable.getElementsByTagName("td").get(19).getTextContent();
            	String person = mytable.getElementsByTagName("td").get(21).getTextContent();
            	//公司缴费比例
            	Double companybi = Double.valueOf(company)/(Double.valueOf(company)+Double.valueOf(person));
            	//个人缴费比例
            	Double personbi = Double.valueOf(person)/(Double.valueOf(company)+Double.valueOf(person));
            	data.put("companyName", companyName);
            	data.put("name", mytable.getElementsByTagName("td").get(1).getTextContent());
            	data.put("userCard", userCard);
            	//个人缴费金额
            	data.put("personDepositAmount", person);
            	//个人公积金账号
            	data.put("personFundAccount", userCard);
            	//缴费基数
            	data.put("baseDeposit", null);
            	//个人公积金卡号
            	data.put("personFundCard", null);
            	//公司缴费比例
            	data.put("companyRatio", String.valueOf(companybi));
            	//个人缴费比例
            	data.put("personRatio", String.valueOf(personbi));
            	//公司公积金账号
            	data.put("companyFundAccount", mytable.getElementsByTagName("td").get(7).getTextContent());
            	//公司缴费金额
            	data.put("companyDepositAmount", company);
            	//最后缴费日期
            	data.put("lastDepositDate", mytable.getElementsByTagName("td").get(29).getTextContent());
            	//余额
            	data.put("balance", mytable.getElementsByTagName("td").get(27).getTextContent());
            	//状态
            	data.put("status", mytable.getElementsByTagName("td").get(5).getTextContent());
            	datamap.put("basicInfos", data);
            	/*
            	 * 明细,查找六年
            	 */
            	HtmlForm timeForm = (HtmlForm) posthtml.getElementById("queryForm");
        		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyyMMdd" );
        		String today = sdf.format(date);
        		Calendar c = Calendar.getInstance();
        		c.setTime(new Date());
                c.add(Calendar.YEAR, -6);
                Date y = c.getTime();
                String startDate = sdf.format(y);            	
            	timeForm.getElementsByTagName("input").get(0).setAttribute("value", startDate);
            	timeForm.getElementsByTagName("input").get(1).setAttribute("value", today);
            	//查找table
            	HtmlTable querytable = (HtmlTable) posthtml.getElementsByTagName("table").get(16);
            	HtmlPage infohtml = querytable.getElementsByTagName("a").get(0).click();
                Thread.sleep(500);
            	System.out.println(alert.size());
                logger.warn("登录南宁住房公积金:"+alert.size());
                if(alert.size()>0){           	
                    map.put("errorCode", "0005");
                    map.put("errorInfo", alert.get(0));
                    return map;
                }
                //明细table
                HtmlTable infotable = (HtmlTable) posthtml.getElementsByTagName("table").get(18);                
                DomNodeList tr = infotable.getElementsByTagName("tr");
            	for(int i=1; i<tr.size();i++){
            		AccumulationFlows flows = new AccumulationFlows();
            		String type1 = infotable.getCellAt(i,1).asText();
    				if(type1.indexOf("汇交分配")==-1&&type1.indexOf("补交分配")==-1){
    					continue;
    				}
    				if("补交分配".equals(type1)){
    					type1="补缴";
    				}
    				if("汇交分配".equals(type1)){
    					type1="汇缴";
    				}
    				String operatorDate = infotable.getCellAt(i,0).asText();
    				String time = operatorDate.substring(0,6);
    				String bizDesc = type1+time+"公积金";
    				flows.setAmount(infotable.getCellAt(i,3).asText());
    				flows.setBizDesc(bizDesc);
    				flows.setOperatorDate(operatorDate);
    				flows.setPayMonth(time);
    				flows.setType(type1);
    				flows.setCompanyName(companyName);
    				
	    			System.out.println(flows);
    				beanList.add(flows);
            	}
            	datamap.put("flows", beanList);
            	/*
            	 * 贷款信息
            	 */
            	page = webClient.getPage("http://www.nngjj.com/web/");
            	Thread.sleep(2000);
                page.getElementById("idcard").setAttribute("value",userCard);
                page.getElementById("password").setAttribute("value",password);
                HtmlPage daikuanlogon = page.getElementById("gjjform").getElementsByTagName("a").get(1).click();
                Thread.sleep(2000);
                System.out.println(alert.size());
                logger.warn("登录南宁住房公积金:"+alert.size());
                if(alert.size()>0){           	
                    map.put("errorCode", "0005");
                    map.put("errorInfo", alert.get(0));
                    return map;
                }
                //没有贷款信息时 
                final String daiKuan = "该职工无贷款信息";
                if(daikuanlogon.asText().contains(daiKuan)){  	               	
                	datamap.put("loans", null);            	
                }else{
                	loansdata.put("loanAccNo", "");
                	loansdata.put("loanLimit", "");
                	loansdata.put("openDate", "");
                	loansdata.put("loanAmount", "");
                	loansdata.put("lastPaymentDate", "");
                	loansdata.put("status", "");
                	loansdata.put("loanBalance", "");
                	loansdata.put("paymentMethod", "");
                	datamap.put("loans", loansdata);        
                }
            }
        }catch (Exception e) {
            logger.warn("南宁住房公积金获取失败",e);
            e.printStackTrace();
            map.put("errorCode", "0001");
            map.put("errorInfo", "当前网络繁忙，请刷新后重试");
        }finally {
            webClient.close();
        }
        SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );
		String today = sdf.format(date);
        map.put("insertTime", today);
        map.put("cityName", "南宁市");
        map.put("city", "013");
        map.put("userId", idCardNum);
        map.put("data", datamap);
        
        Resttemplate resttemplate=new Resttemplate();
        map = resttemplate.SendMessage(map, applicat.getSendip()+"/HSDC/person/accumulationFund");
        final String tuiSong = "0000";
        final String errorCode = "errorCode";
        if(map!=null&&tuiSong.equals(map.get(errorCode).toString())){
          	 PushState.state(idCardNum, "accumulationFund", 300);
              map.put("errorInfo","推送成功");
              map.put("errorCode","0000");
              
          }else{
          	 PushState.state(idCardNum, "accumulationFund", 200);
              map.put("errorInfo","推送失败");
              map.put("errorCode","0001");
          }
        return map;
    }
}
