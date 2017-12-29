package com.reptile.service.accumulationfund;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.reptile.model.AccumulationFlows;
import com.reptile.util.ConstantInterface;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 
 * @ClassName: NingBoAccumulationfundService  
 * @Description: TODO  
 * @author: fangshuang
 * @date 2017年12月29日  
 *
 */
@Service
public class NingBoAccumulationfundService {
	@Autowired 
	private application applicat;
    private Logger logger = LoggerFactory.getLogger(GuiYangAccumulationfundService.class);
    
    public Map<String, Object> loadImageCode(HttpServletRequest request) {
        logger.warn("获取宁波公积金图片验证码");
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
            page = webClient.getPage("http://www.nbgjj.com/perlogin.jhtml");
            HtmlImage rand = (HtmlImage) page.getElementById("guestbookCaptcha");
            BufferedImage read = rand.getImageReader().read(0);
            String fileName = "guiyang" + System.currentTimeMillis() + ".png";
            ImageIO.write(read, "png", new File(file, fileName));
            datamap.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/ImageCode/" + fileName);
            map.put("errorCode", "0000");
            map.put("errorInfo", "加载验证码成功");
            map.put("data", datamap);
            session.setAttribute("htmlWebClient-ningbo", webClient);
            session.setAttribute("htmlPage-ningbo", page);
        } catch (IOException e) {
            logger.warn("宁波住房公积金 ", e);
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
        Object htmlWebClient = session.getAttribute("htmlWebClient-ningbo");
        Object htmlPage = session.getAttribute("htmlPage-ningbo");
        if (htmlWebClient != null && htmlPage != null) {
            HtmlPage page = (HtmlPage) htmlPage;
            WebClient webClient = (WebClient) htmlWebClient;
            
            try {           	
            	//alert监控
            	List<String> alert=new ArrayList<>();
                CollectingAlertHandler alertHandler=new CollectingAlertHandler(alert);
                webClient.setAlertHandler(alertHandler);
                page.getElementById("cardno").setAttribute("value",userCard);
                page.getElementById("perpwd").setAttribute("value",password);
                page.getElementById("verify").setAttribute("value",imageCode);
                HtmlPage loginHtml = page.getElementById("sub").click();
                Thread.sleep(2000);
                //账户明细查询页
                HtmlPage posthtml1 = webClient.getPage("http://www.nbgjj.com/perquery.jhtml");
                Thread.sleep(3000);
                System.out.println(alert.size());
                logger.warn("登录宁波住房公积金:"+alert.size());
                final String mimaheDui = "密码错误，请核对!";
                final String yanzhengMa = "验证码不正确";
                if(alert.size()>0){
                	if(alert.get(0).equals(mimaheDui)){
                		map.put("errorCode", "0005");
                		map.put("errorInfo", "密码输入错误！请重新输入");
                	}else if(alert.get(0).equals(yanzhengMa)){
                		map.put("errorCode", "0005");
                		map.put("errorInfo", "验证码不正确！");
                	}else{
                		map.put("errorCode", "0001");
                		map.put("errorInfo", alert.get(0));
                	}
                	logger.warn(alert.get(0));                   
                    return map;
                }      
            	PushState.state(idCardNum, "accumulationFund",100);
            	//账户明细查询页
                HtmlPage posthtml = webClient.getPage("http://www.nbgjj.com/perdetail.jhtml");
                Thread.sleep(3000);
                final String chaxunmingXi = "提供近三个自然年个人明细查询";
                if(posthtml.asText().indexOf(chaxunmingXi)!=-1){
                	Calendar c = Calendar.getInstance();
                	
            		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy-MM-dd" );
            		String today = sdf.format(date);
            		c.setTime(date);
            		c.add(Calendar.YEAR,-3);
            		Date y = c.getTime();
            		String starttime = sdf.format(y);
            		posthtml.getElementById("endDate").setAttribute("value", today);
            		posthtml.getElementById("startDate").setAttribute("value", starttime);
            		HtmlSelect type = (HtmlSelect) posthtml.getElementById("indiacctype");
            		type.setAttribute("value", "1");
            		HtmlPage resultHtml = posthtml.getElementById("sub").click();
            		Thread.sleep(2000);
            		logger.warn("宁波住房公积金账户明细查询:"+alert.size());
            		final String jiLu = "条记录";
            		if(resultHtml.asText().indexOf(jiLu)==-1){
            			logger.warn("宁波住房公积金账户明细获取失败");
                        map.put("errorCode", "0001");
                        map.put("errorInfo", "当前网络繁忙，请刷新后重试");                     
            		}else {
            			AccumulationFlows flows = new AccumulationFlows();
            			HtmlTable posttable = (HtmlTable) resultHtml.getElementById("queryTable");
            			String a = resultHtml.asText();
            			int num1 = a.indexOf("共 ");
            			int num2 = a.indexOf(" 条记录，共 ");
            			//总记录数
            			String  phonenum = a.substring(num1+2, num2);
            			for(int i=Integer.valueOf(phonenum);i>=1;i--){             				
            				String type1 = posttable.getCellAt(i,2).asText();
            				if(type1.indexOf("汇缴")==-1&&type1.indexOf("补缴")==-1){
            					continue;
            				}
            				if("少缴补缴".equals(type1)){
            					type1="补缴";
            				}
            				String time = posttable.getCellAt(i,1).asText().substring(0, 7).replace("-", "");
            				String bizDesc = type1+time+"公积金";
            				flows.setAmount(posttable.getCellAt(i,3).asText());
            				flows.setBizDesc(bizDesc);
            				flows.setOperatorDate(posttable.getCellAt(i,1).asText());
            				flows.setPayMonth(posttable.getCellAt(i,1).asText().substring(0, 7));
            				flows.setType(type1);
            				flows.setCompanyName(posttable.getCellAt(i,0).asText());
            				JSONObject jsonObject = JSONObject.fromObject(flows);
        	    			String jsonBean = jsonObject.toString();
        	    			System.out.println(jsonBean);
            				beanList.add(jsonBean);
            			}
            			dataMap.put("flows", beanList); 
            			

            		}
            		
            		
                }
                //基本信息查询页
                HtmlPage basicInfoshtml = webClient.getPage("http://www.nbgjj.com/perquery.jhtml");
                Thread.sleep(2000);
                final String zhanghujiGou = "账户机构";
                if(basicInfoshtml.asText().indexOf(zhanghujiGou)!=-1){
                	HtmlTable table = (HtmlTable) basicInfoshtml.getElementById("queryTable");
                	DomNodeList tdList = table.getElementsByTagName("td");
                	String companyName = basicInfoshtml.getElementById("unitaccname").getTextContent().substring(5);
                	String name = basicInfoshtml.getElementById("accname").getTextContent().substring(3);
                	/*BasicInfos.setCompanyName(companyName);//*/
                	data.put("companyName", companyName);
                	data.put("name", name);
                	data.put("userCard", userCard);
                	/*BasicInfos.setName(name);
                	BasicInfos.setIdCard(userCard);*/
                	//个人缴费金额
                	data.put("personDepositAmount", table.getElementsByTagName("td").get(17).getTextContent());
                	//BasicInfos.setPersonDepositAmount(table.getElementsByTagName("td").get(17).getTextContent());
                	//个人公积金账号
                	data.put("personFundAccount", table.getElementsByTagName("td").get(5).getTextContent());
                	//BasicInfos.setPersonFundAccount(table.getElementsByTagName("td").get(5).getTextContent());
                	//缴费基数
                	data.put("baseDeposit", table.getElementsByTagName("td").get(9).getTextContent());
                	///BasicInfos.setBaseDeposit(table.getElementsByTagName("td").get(9).getTextContent());
                	//个人公积金卡号
                	data.put("personFundCard", "");
                	//BasicInfos.setPersonFundCard(table.getElementsByTagName("td").get(5).getTextContent());
                	//公司缴费比例
                	data.put("companyRatio", "");
                	//BasicInfos.setCompanyRatio(table.getElementsByTagName("td").get(17).getTextContent());/////
                	//个人缴费比例
                	data.put("personRatio", "");
                	//BasicInfos.setPersonRatio(table.getElementsByTagName("td").get(17).getTextContent());/////
                	//公司公积金账号
                	data.put("companyFundAccount", "");
                	//BasicInfos.setCompanyFundAccount(companyFundAccount);
                	//公司缴费金额
                	data.put("companyDepositAmount", "");
                	//BasicInfos.setCompanyDepositAmount(table.getElementsByTagName("td").get(17).getTextContent());
                	//最后缴费日期
                	data.put("lastDepositDate", table.getElementsByTagName("td").get(27).getTextContent());
                	//BasicInfos.setLastDepositDate(table.getElementsByTagName("td").get(27).getTextContent());
                	//余额
                	data.put("balance", table.getElementsByTagName("td").get(11).getTextContent());
                	//BasicInfos.setBalance(table.getElementsByTagName("td").get(11).getTextContent());
                	//状态   
                	data.put("status", table.getElementsByTagName("td").get(13).getTextContent());
                	//BasicInfos.setStatus(table.getElementsByTagName("td").get(13).getTextContent());       
                	dataMap.put("basicInfos", data);
                }
                //贷款查询页
                HtmlPage loanhtml = webClient.getPage("http://www.nbgjj.com/loanbase.jhtml");
                Thread.sleep(2000);
                //没有贷款信息时alertList.get(0).toString().contains("") 
                final String  noinFo = "根据身份证号未找到对应的合同信息!";
                if(alert.get(0).toString().contains(noinFo)){              	
                	loansdata.put("loanAccNo", "");
                	loansdata.put("loanLimit", "");
                	loansdata.put("openDate", "");
                	loansdata.put("loanAmount", "");
                	loansdata.put("lastPaymentDate", "");
                	loansdata.put("status", "");
                	loansdata.put("loanBalance", "");
                	loansdata.put("paymentMethod", "");
                	dataMap.put("loans", loansdata);            	
                }else{
                	loansdata.put("loanAccNo", "");
                	loansdata.put("loanLimit", "");
                	loansdata.put("openDate", "");
                	loansdata.put("loanAmount", "");
                	loansdata.put("lastPaymentDate", "");
                	loansdata.put("status", "");
                	loansdata.put("loanBalance", "");
                	loansdata.put("paymentMethod", "");
                	dataMap.put("loans", loansdata);        
                }
                System.out.println(alert.get(0).toString());
                logger.warn("宁波住房公积金贷款明细查询:"+alert.size());
                
                
                              
            } catch (Exception e) {
                logger.warn("宁波住房公积金获取失败",e);
                e.printStackTrace();
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
                return map;
            }finally {
                webClient.close();
            }
        } else {
            logger.warn("宁波住房公积金登录过程中出错 ");
            map.put("errorCode", "0001");
            map.put("errorInfo", "非法操作！请确认验证码是否正确！");
            return map;
        }
        SimpleDateFormat sdf =  new SimpleDateFormat( "yyyyMMdd hh:mm:ss" );
		String today = sdf.format(date);
        map.put("insertTime", today);
        map.put("cityName", "宁波市");
        map.put("city", "011");
        map.put("userId", idCardNum);
        map.put("data", dataMap);   
        
        Resttemplate resttemplate=new Resttemplate();
        map = resttemplate.SendMessage(map, applicat.getSendip()+"/HSDC/person/accumulationFund");
        final String o = "0000";
        final String errorCode = "errorCode";
        if(map!=null&&o.equals(map.get(errorCode).toString())){
	    	PushState.state(idCardNum, "accumulationFund",300);
	    	map.put("errorInfo","推送成功");
	    	map.put("errorCode","0000");
          
        }else{
        	//--------------------数据中心推送状态----------------------
        	PushState.state(idCardNum, "accumulationFund",200);
        	//---------------------数据中心推送状态----------------------
        	
            map.put("errorInfo","推送失败");
            map.put("errorCode","0001");
        	
        }
        return map;
    }
	
}
