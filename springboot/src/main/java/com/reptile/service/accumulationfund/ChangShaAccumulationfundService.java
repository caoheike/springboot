package com.reptile.service.accumulationfund;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.model.AccumulationFlows;
import com.reptile.util.MyCYDMDemo;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;

/**
 * 
 * @ClassName: ChangShaAccumulationfundService  
 * @Description: TODO  
 * @author: fangshuang
 * @date 2017年12月29日  
 *
 */
@Service
public class ChangShaAccumulationfundService {
	@Autowired 
	private application applicat;
    private Logger logger = LoggerFactory.getLogger(GuiYangAccumulationfundService.class);
	DecimalFormat df= new DecimalFormat("#.00");
	Date date=new Date();
    public Map<String, Object> getDeatilMes(HttpServletRequest request,String userCard, String password,String idCardNum) {
        logger.warn("获取长沙公积金数据");
        Map<String, Object> map = new HashMap<>(10);
        Map<String, Object> dataMap = new HashMap<>(10);
        Map<String, Object> data = new HashMap<>(10);
        Map<String, Object> loansdata = new HashMap<>(10);
        List<Object> beanList=new ArrayList<Object>();
        List<Object> loansList=new ArrayList<Object>();
        WebClient webClient = new WebClientFactory().getWebClient();
        HtmlPage page = null;
        try {
        	PushState.state(idCardNum, "accumulationFund", 100);
        	
            page = webClient.getPage("http://www.csgjj.com.cn:8001/login.do?t=1");
            Thread.sleep(2000);
            page.getElementById("username").setAttribute("value",userCard);
            page.getElementById("password").setAttribute("value",password);
            String code = page.getElementById("codeBox").getAttribute("style");
            System.out.println(code);
            String disPlay = "display: none;";
            if(!disPlay.equals(code)){
            	HtmlImage imgCode = (HtmlImage) page.getElementById("codeimg");
                BufferedImage read = imgCode.getImageReader().read(0);
                ImageIO.write(read, "png", new File("C://aa.png"));
                Map<String, Object> codes = MyCYDMDemo.getCode("C://aa.png");
                String vecCode = codes.get("strResult").toString();
                page.getElementById("checkCode").setAttribute("value", vecCode);
            }
            HtmlPage posthtml = page.getElementsByTagName("a").get(4).click();
            Thread.sleep(2000);
            String error = posthtml.getElementById("error").getTextContent();
            
            final String yonghuMing  = "用户名";
            final String miMa = "密码";
            if(error.indexOf(yonghuMing)!=-1||error.indexOf(miMa)!=-1){
            	map.put("errorCode", "0005");
                map.put("errorInfo", "用户名或密码错误,请重试！");
                return map;
            }
            final String yanzhengMa = "验证码错误";
            if(yanzhengMa.equals(error)){
            	map.put("errorCode", "0005");
                map.put("errorInfo", "网络异常，请重试！");
                return map;
            }
            final String wuXiao = "无效的身份证号码";
            if(wuXiao.equals(error)){
            	map.put("errorCode", "0005");
                map.put("errorInfo", "身份证号码无效，请重试！");
                return map;
            }
            
           
            
            /*
             * 基本信息
             */
            //首页
            String baseUrl = "http://www.csgjj.com.cn:8001/index.do";
            HtmlPage baseInfo =  getPage(baseUrl,webClient);        	       
            //身份证
            String idCard = baseInfo.getElementsByTagName("span").get(7).getTextContent().substring(5);
            //个人公积金号码
            String personFundAccount = baseInfo.getElementsByTagName("span").get(8).getTextContent().substring(5);
            String name = baseInfo.getElementsByTagName("div").get(7).getTextContent();
            int num = name.indexOf("(");
            if(num!=-1){
            	name = name.substring(0,num);
            }
            //基本信息页，获取余下基本信息
            String baseUrl1 = "http://www.csgjj.com.cn:8001/per/queryPerInfo.do";
            HtmlPage baseInfo1 =  getPage(baseUrl1,webClient);
            final String danWei = "单位名称";
            if(baseInfo1.asText().indexOf(danWei)==-1){
            	logger.warn("长沙住房公积金基本信息获取失败");
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
            }
            request.getSession().setAttribute("changshaAccumu-webClient", webClient);
            request.getSession().setAttribute("changshaAccumu-posthtml", baseInfo1);
            data = getBaseInfo(request);
            data.put("userCard", idCard);
            data.put("personFundAccount", personFundAccount);
            data.put("name", name);
            dataMap.put("basicInfos", data);
            /*
             * 明细信息
             */
            String infoUrl = "http://www.csgjj.com.cn:8001/per/queryPerDeposit.do";
            HtmlPage info =  getPage(infoUrl,webClient);
            final String denRen = "个人明细列表";
            if(info.asText().indexOf(denRen)==-1){
            	logger.warn("长沙住房公积金明细信息获取失败");
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
            }
            AccumulationFlows flows = new AccumulationFlows();
            DomElement infos=  info.getElementById("dataList");
            Document doc=   Jsoup.parse(infos.asXml());
            Elements divs=  doc.getElementsByTag("div");
            final int size = divs.size()-5;
            final int d = 12;
            for (int i = 26; i < size; i=i+d) {
            	System.out.println(divs.get(i).text()+"-----------"+i+"------------------");	           	
            		String type = divs.get(i+9).text();
            	if(type.indexOf("汇缴")==-1&&type.indexOf("补缴")==-1){
					continue;
				}
				if(type.indexOf("汇缴")!=-1){
					type="汇缴";
				}
				if(type.indexOf("补缴")!=-1){
					type="补缴";
				}
				flows.setType(type);
				//业务描述
				String bizDesc = type+divs.get(i).text()+"公积金";
				//操作金额
				flows.setAmount(divs.get(i+3).text().replace(",", ""));
				flows.setBizDesc(bizDesc);
				//操作时间
				flows.setOperatorDate(divs.get(i+1).text());
				//缴费月份
				flows.setPayMonth(divs.get(i).text());
				//操作类型
				flows.setType(type);
				//公司名
				flows.setCompanyName(divs.get(i+2).text());
				JSONObject jsonObject = JSONObject.fromObject(flows);
    			String jsonBean = jsonObject.toString();
    			System.out.println(jsonBean);
				beanList.add(jsonBean);           	
			}           
            dataMap.put("flows", beanList);
           /*
            * 贷款信息
            */
            String loansUrl = "http://www.csgjj.com.cn:8001/per/queryPerAppState.do";
            HtmlPage loansInfos =  getPage(loansUrl,webClient);
            final String jinDu = "贷款办理进度";
            if(loansInfos.asText().indexOf(jinDu)==-1){
            	logger.warn("长沙住房公积金明细信息获取失败");
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
            } 
            final String s = "对不起,您当前无贷款信息";
            if(loansInfos.asText().indexOf(s)!=-1){
            	dataMap.put("loans", loansList); 
            }else{
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
            logger.warn("长沙住房公积金获取失败",e);
            e.printStackTrace();
            map.put("errorCode", "0001");
            map.put("errorInfo", "当前网络繁忙，请刷新后重试");
        }finally {
            webClient.close();
        }
        SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );
		String today = sdf.format(date);
        map.put("insertTime", today);
        map.put("cityName", "长沙市");
        map.put("city", "017");
        map.put("userId", idCardNum);
        map.put("data", dataMap);
        
        Resttemplate resttemplate=new Resttemplate();
        map = resttemplate.SendMessage(map, applicat.getSendip()+"/HSDC/person/accumulationFund");
        /*if(map!=null&&"0000".equals(map.get("errorCode").toString())){
          	 PushState.state(idCardNum, "accumulationFund", 300);
              map.put("errorInfo","推送成功");
              map.put("errorCode","0000");
              
          }else{
          	 PushState.state(idCardNum, "accumulationFund", 200);
              map.put("errorInfo","推送失败");
              map.put("errorCode","0001");
          }*/
        return map;
    }
    /**
     * 基本信息 
     * @param request
     * @return
     */
    @SuppressWarnings({ "unused", "resource" })
	public Map<String, Object> getBaseInfo(HttpServletRequest request){
    	Map<String, Object> data = new HashMap<>(20);
        HttpSession session = request.getSession();
    	Object htmlWebClient = session.getAttribute("changshaAccumu-webClient");
        Object htmlPage = session.getAttribute("changshaAccumu-posthtml");
        if (htmlWebClient != null && htmlPage != null) {
            HtmlPage page = (HtmlPage) htmlPage;
            WebClient webClient = (WebClient) htmlWebClient;
            data.put("companyName", page.getElementById("corpcode2").getTextContent().trim());
            String personDepositAmount = page.getElementById("perdepmny").getTextContent().trim();
            //个人缴费金额
        	data.put("personDepositAmount", personDepositAmount.replace("元", ""));
        	//缴费基数
        	data.put("baseDeposit", page.getElementById("bmny").getTextContent().trim().replace("元", ""));
        	//公司缴费金额
        	String companyDepositAmount = page.getElementById("corpdepmny").getTextContent().trim();
        	//个人公积金卡号
        	data.put("personFundCard", "");
        	//公司缴费比例//////
        	data.put("companyRatio", "");
        	//个人缴费比例/////////////
        	data.put("personRatio", "");
        	//公司公积金账号
        	data.put("companyFundAccount", page.getElementById("corpcode").getTextContent().trim());
        	//公司缴费金额
        	data.put("companyDepositAmount", companyDepositAmount.replace("元", ""));
        	//最后缴费日期
        	data.put("lastDepositDate", page.getElementById("payendmnh").getTextContent().trim());
        	//余额
        	data.put("balance", page.getElementById("accbal").getTextContent().trim().replace("元", ""));
        	//状态
        	data.put("status", page.getElementById("accstate").getTextContent().trim());        				
        	
            
        }else{
        	logger.warn("重庆住房公积金登录过程中出错 ");
        	data.put("errorCode", "0001");
        	data.put("errorInfo", "非法操作！");
            return data;
        }
        return data;
    }
    public HtmlPage getPage(String urls,WebClient webClient) throws Exception{
    	URL url = new URL(urls);            
    	WebRequest webRequest = new WebRequest(url);
    	webRequest.setHttpMethod(HttpMethod.GET);  
    	
    	HtmlPage info = webClient.getPage(webRequest);
    	
    	Thread.sleep(2000);
        return info;
    }
}
