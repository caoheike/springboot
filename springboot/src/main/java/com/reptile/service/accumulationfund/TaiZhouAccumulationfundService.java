package com.reptile.service.accumulationfund;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.reptile.model.AccumulationFlows;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;
/**
 * 
 * @ClassName: TaiZhouAccumulationfundService  
 * @Description: TODO  
 * @author: fangshuang
 * @date 2017年12月29日  
 *
 */
@Service
public class TaiZhouAccumulationfundService {
	@Autowired 
	private application applicat;
    private Logger logger = LoggerFactory.getLogger(GuiYangAccumulationfundService.class);
    public Map<String, Object> loadImageCode(HttpServletRequest request) {
        logger.warn("获取台州公积金图片验证码");
        Map<String, Object> map = new HashMap<>(10);
        Map<String, String> datamap = new HashMap<>(10);
        String path = request.getServletContext().getRealPath("ImageCode");
        HttpSession session = request.getSession();

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        WebClient webClient = new WebClientFactory().getWebClient();

        HtmlPage page = null;
        try {
            page = webClient.getPage("http://www.tzgjj.gov.cn/index.php?m=grgjjsearch&c=index&a=index&catid=316");
            HtmlImage rand = (HtmlImage) page.getElementById("yzm");
            BufferedImage read = rand.getImageReader().read(0);
            String fileName = "guiyang" + System.currentTimeMillis() + ".png";
            ImageIO.write(read, "png", new File(file, fileName));
            datamap.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/ImageCode/" + fileName);
            map.put("errorCode", "0000");
            map.put("errorInfo", "加载验证码成功");
            map.put("data", datamap);
            session.setAttribute("htmlWebClient-taizhou", webClient);
            session.setAttribute("htmlPage-taizhou", page);
        } catch (IOException e) {
            logger.warn("台州住房公积金 ", e);
            map.put("errorCode", "0001");
            map.put("errorInfo", "当前网络繁忙，请刷新后重试");
            e.printStackTrace();

        }
        return map;
    }
    @SuppressWarnings({ "unused", "rawtypes" })
	public Map<String, Object> getDeatilMes(HttpServletRequest request, String userCard, String password, String imageCode,String idCardNum) {
        Map<String, Object> map = new HashMap<>(10);
        Map<String, Object> dataMap = new HashMap<>(10);
        Map<String, Object> data = new HashMap<>(10);
        Map<String, Object> loansdata = new HashMap<>(10);
        List<Object> beanList=new ArrayList<Object>();
        Date date=new Date();
        HttpSession session = request.getSession();
        Object htmlWebClient = session.getAttribute("htmlWebClient-taizhou");
        Object htmlPage = session.getAttribute("htmlPage-taizhou");

        if (htmlWebClient != null && htmlPage != null) {
            HtmlPage page = (HtmlPage) htmlPage;
            WebClient webClient = (WebClient) htmlWebClient;
            
            try {
            	List<String> alert=new ArrayList<>();
                CollectingAlertHandler alertHandler=new CollectingAlertHandler(alert);
                webClient.setAlertHandler(alertHandler);
            	PushState.state(idCardNum, "accumulationFund", 100);
                page.getElementById("sfzh").setAttribute("value",userCard);
                page.getElementById("pswd").setAttribute("value",password);
                HtmlInput codeinput = (HtmlInput) page.getElementById("code");
                codeinput.setValueAttribute(imageCode);
                System.out.println(page.getElementById("code").getAttribute("value"));
                HtmlPage posthtml = page.getElementById("add_submit").click();
                Thread.sleep(2000);
                //alert弹框出现账户密码未输入的错误，输入错误的提示在跳转页面进行提示，若错误几秒后会跳转回登录页面
                System.out.println(alert.size());
                logger.warn("登录台州住房公积金:"+alert.size());
                if(alert.size()>0){
                    map.put("errorCode", "0005");
                    map.put("errorInfo", alert.get(0));
                    return map;
                }
                final String q = "【归集情况】";
                if(posthtml.asText().indexOf(q)==-1){
                	logger.warn("台州住房公积金获取失败");
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "当前网络繁忙，请刷新后重试");
                }else{
                	//基本信息
                	HtmlTable mytable = (HtmlTable) posthtml.getElementsByTagName("table").get(15);
                	//明细信息
                	HtmlTable infotable = (HtmlTable) posthtml.getElementsByTagName("table").get(16);
                	DomNodeList trList = infotable.getElementsByTagName("tr");
                	String companyName = mytable.getElementsByTagName("td").get(8).getTextContent().substring(5);               	
                	data.put("companyName", companyName);
                	data.put("name", mytable.getElementsByTagName("td").get(1).getTextContent());
                	data.put("userCard", userCard);
                	//个人缴费金额
                	data.put("personDepositAmount", "");
                	//个人公积金账号
                	data.put("personFundAccount", mytable.getElementsByTagName("td").get(0).getTextContent());
                	//缴费基数
                	data.put("baseDeposit", "0");
                	//个人公积金卡号
                	data.put("personFundCard", "");
                	//公司缴费比例
                	data.put("companyRatio", "");
                	//个人缴费比例
                	data.put("personRatio", "");
                	//公司公积金账号
                	data.put("companyFundAccount", "");
                	//公司缴费金额
                	data.put("companyDepositAmount", "");
                	//最后缴费日期
                	data.put("lastDepositDate", mytable.getElementsByTagName("td").get(6).getTextContent());
                	//余额
                	data.put("balance", mytable.getElementsByTagName("td").get(3).getTextContent());
                	//状态
                	data.put("status", mytable.getElementsByTagName("td").get(5).getTextContent());
                	dataMap.put("basicInfos", data);
                	
                	
                	final int a = 3;
                	for(int i=trList.size()-1;i>=a;i--){
                		AccumulationFlows flows = new AccumulationFlows();
                		String type1 = infotable.getCellAt(i,0).asText();
                		String time1 = infotable.getCellAt(i,3).asText();
        				if(type1.indexOf("汇缴")==-1&&type1.indexOf("补缴")==-1){
        					continue;
        				}
        				
        				if(type1.indexOf("补缴")!=-1){
        					type1="补缴";
        				}
        				if(type1.indexOf("汇缴")!=-1){
        					type1="汇缴";
        				}
        				if(i!=trList.size()-1){
        					if(time1.equals(infotable.getCellAt(i+1,3).asText())){
            					continue;
            				}
        				}
        				
        				String operatorDate = infotable.getCellAt(i,3).asText();
        				String time = operatorDate.substring(0,7).replace("-", "");
        				String bizDesc = type1+time+"公积金";
        				flows.setAmount(infotable.getCellAt(i,2).asText());
        				flows.setBizDesc(bizDesc);
        				flows.setOperatorDate(operatorDate);
        				flows.setPayMonth(time);
        				flows.setType(type1);
        				flows.setCompanyName(companyName);
        				
    	    			System.out.println(flows);
        				beanList.add(flows);
                	}
                	dataMap.put("loans", null); 
                	dataMap.put("flows", beanList);
                }
            }catch (Exception e) {
                logger.warn("台州住房公积金获取失败",e);
                e.printStackTrace();
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
            }finally {
                webClient.close();
            }
        } else {
            logger.warn("台州住房公积金登录过程中出错 ");
            map.put("errorCode", "0001");
            map.put("errorInfo", "非法操作！");
        }
        SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy年MM月dd日 hh:mm:ss" );
		String today = sdf.format(date);
        map.put("insertTime", today);
        map.put("cityName", "台州市");
        map.put("city", "015");
        map.put("userId", idCardNum);
        map.put("data", dataMap);   
        
        Resttemplate resttemplate=new Resttemplate();
        map = resttemplate.SendMessage(map, applicat.getSendip()+"/HSDC/person/accumulationFund");
        final String o = "0000";
        final String errorCode = "errorCode";
        if(map!=null&&o.equals(map.get(errorCode).toString())){
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
