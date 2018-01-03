package com.reptile.service.socialsecurity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.model.SecurityBean;
import com.reptile.service.accumulationfund.GuiYangAccumulationfundService;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;
/**
 * 
 * @ClassName: ChongQingSocialSecurityService  
 * @Description: TODO  
 * @author: fangshuang
 * @date 2017年12月29日  
 *
 */
@Service
public class ChongQingSocialSecurityService {
	@Autowired 
	private application applicat;
	private Logger logger = LoggerFactory.getLogger(GuiYangAccumulationfundService.class);
	DecimalFormat df= new DecimalFormat("#.00");
    List<String> alert=new ArrayList<>();
    public Map<String, Object> loadImageCode(HttpServletRequest request){
        logger.warn("获取重庆社保图片验证码");
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
            page = webClient.getPage("http://ggfw.cqhrss.gov.cn/ggfw/index1.jsp");
            Thread.sleep(2000);
            HtmlImage rand = (HtmlImage) page.getElementById("yzmimg");
            BufferedImage read = rand.getImageReader().read(0);
            String fileName = "guiyang" + System.currentTimeMillis() + ".png";
            ImageIO.write(read, "png", new File(file, fileName));
            datamap.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/ImageCode/" + fileName);
            map.put("errorCode", "0000");
            map.put("errorInfo", "加载验证码成功");
            map.put("data", datamap);
            session.setAttribute("htmlWebClient-chongqing", webClient);
            session.setAttribute("htmlPage-chongqing", page);
        } catch (Exception e) {
            logger.warn("重庆社保 ", e);
            map.put("errorCode", "0001");
            map.put("errorInfo", "当前网络繁忙，请刷新后重试");
            e.printStackTrace();

        }
        return map;
    }
    /**
     * 确定是否需要选择要查询参保的信息
     * @param request
     * @param userCard
     * @param password
     * @param imageCode
     * @param idCardNum
     * @return
     */
    public Map<String, Object> getQueryDeatil(HttpServletRequest request, String userCard, String password, String imageCode,String idCardNum) {
        Map<String, Object> map = new HashMap<>(10);
        
    	
        HttpSession session = request.getSession();
        Object htmlWebClient = session.getAttribute("htmlWebClient-chongqing");
        Object htmlPage = session.getAttribute("htmlPage-chongqing");

        if (htmlWebClient != null && htmlPage != null) {
            HtmlPage page = (HtmlPage) htmlPage;
            WebClient webClient = (WebClient) htmlWebClient;
            CollectingAlertHandler alertHandler=new CollectingAlertHandler(alert);
            webClient.setAlertHandler(alertHandler);
            try {
            	PushState.state(idCardNum, "socialSecurity", 100);
                page.getElementById("sfzh").setAttribute("value",userCard);
                page.getElementById("password").setAttribute("value",password);
                page.getElementById("validateCode").setAttribute("value",imageCode);
                @SuppressWarnings("unused")
				HtmlPage basicInfos = page.getElementById("loginBtn").click();
                Thread.sleep(2000);               
                System.out.println(alert.size());
                logger.warn("登录重庆社保:"+alert.size());
                if(alert.size()>0){
                    map.put("errorCode", "0005");
                    map.put("errorInfo", alert.get(0));
                    return map;
                }
                String loadPath = "http://ggfw.cqhrss.gov.cn/ggfw/QueryBLH_main.do?code=888";					    																									
	    		 URL url = new URL(loadPath);
	    		 WebRequest webRequest = new WebRequest(url);
	    		 webRequest.setHttpMethod(HttpMethod.GET);
	    		 List<NameValuePair> list = new ArrayList<NameValuePair>();		    			
                list.add(new NameValuePair("code", "888"));
                webRequest.setRequestParameters(list);
	    		 HtmlPage infos = webClient.getPage(webRequest);
	    		 Thread.sleep(2000);              		       		      		
	       		System.out.println("---------------------------"+infos.asText());
	       		session.setAttribute("htmlWebClient-chongqing", webClient);
	             session.setAttribute("basicInfos-chongqing", infos);
	       		map = getDeatil(request);
            }catch (Exception e) {
                logger.warn("重庆社保获取失败",e);
                e.printStackTrace();
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
            }finally {
                webClient.close();
            }         
        }
        
        map.put("cityName", "重庆");
        map.put("city", "016");
        map.put("userId", userCard);
        Resttemplate resttemplate=new Resttemplate();
        map = resttemplate.SendMessage(map, applicat.getSendip()+"/HSDC/person/socialSecurity");
        final String o = "0000";
        final String errorCode = "errorCode";
        if(map!=null&&o.equals(map.get(errorCode).toString())){
          	PushState.state(idCardNum, "socialSecurity", 300);
          	map.put("errorInfo","推送成功");
          	map.put("errorCode","0000");
          }else{
          	PushState.state(idCardNum, "socialSecurity", 200);
          	map.put("errorInfo","推送失败");
          	map.put("errorCode","0001");
          }
        return map;
    }
   
    /**
     * 信息获取
     * @param request
     * @return
     */     
    public Map<String, Object> getDeatil(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>(10);
        Map<String, Object> dataMap = new HashMap<>(10);
    	Map<String,Object> baseInfo = new HashMap<String, Object>(10);
    	List<Object> yanglaoList=new ArrayList<Object>();
    	List<Object> yiliaoList=new ArrayList<Object>();
    	List<Object> shiyeList=new ArrayList<Object>();
    	List<Object> gongshList=new ArrayList<Object>();
    	List<Object> shengyuList=new ArrayList<Object>();
    	Date date=new Date();
        HttpSession session = request.getSession();
        Object htmlWebClient = session.getAttribute("htmlWebClient-chongqing");
        Object htmlPage = session.getAttribute("basicInfos-chongqing");

        if (htmlWebClient != null && htmlPage != null) {
        	
        
            HtmlPage basicInfos = (HtmlPage) htmlPage;
            WebClient webClient = (WebClient) htmlWebClient;
            CollectingAlertHandler alertHandler=new CollectingAlertHandler(alert);
            webClient.setAlertHandler(alertHandler);
            System.out.println(basicInfos.asText());
	    	try {
	    		final String jichuInfo = "个人基础信息";
	                if(basicInfos.asText().indexOf(jichuInfo)==-1){
	                	logger.warn("重庆社保基本信息获取失败");
	                    map.put("errorCode", "0001");
	                    map.put("errorInfo", "当前网络繁忙，请刷新后重试");
	                    return map;
	                }
	                //参保状态信息
	                HtmlTable typeTable = (HtmlTable) basicInfos.getElementsByTagName("table").get(2);
	                String yanglaoType = typeTable.getCellAt(3, 1).getTextContent();
	                String yiliaoType = typeTable.getCellAt(3, 2).getTextContent();
	                String shiyeType = typeTable.getCellAt(3, 3).getTextContent();
	                String gongshType = typeTable.getCellAt(3, 4).getTextContent();
	                String shengyuType = typeTable.getCellAt(3, 5).getTextContent();
	                
	                /*
	                 * 获取基本信息（除了余额）
	                 */
	                session.setAttribute("htmlWebClient-chongqing", webClient);
	                session.setAttribute("htmlPage-chongqing", basicInfos);
	                baseInfo = getBaseInfo(request);
	                
	                String yanglaoUrl="http://ggfw.cqhrss.gov.cn/ggfw/QueryBLH_main.do?code=015";
	                String yiliaoUrl="http://ggfw.cqhrss.gov.cn/ggfw/QueryBLH_main.do?code=023";
	                String gongshUrl="http://ggfw.cqhrss.gov.cn/ggfw/QueryBLH_main.do?code=052";
	                String shiyeUrl="http://ggfw.cqhrss.gov.cn/ggfw/QueryBLH_main.do?code=043";
	                String shengyuUrl="http://ggfw.cqhrss.gov.cn/ggfw/QueryBLH_main.do?code=062";
	                yanglaoList = getInfos(webClient,yanglaoType,yanglaoUrl,"015","ylbxgrjfmxxxTable");
	                yiliaoList = getInfos(webClient,yiliaoType,yiliaoUrl,"023","mtbxgrjfmxxxTable");
	                gongshList = getInfos(webClient,gongshType,gongshUrl,"052","gsjfmxxxTable");
	                shiyeList = getInfos(webClient,shiyeType,shiyeUrl,"043","sybxgrjfmxTable");
	                shengyuList = getInfos(webClient,shengyuType,shengyuUrl,"062","shyjfmxxxTable");
	                
	                
	                /*
	                 * 获取医疗余额
	                 */
	                URL url = new URL("http://ggfw.cqhrss.gov.cn/ggfw/QueryBLH_main.do?code=027");
	                
	            	WebRequest webRequest = new WebRequest(url);
	            	webRequest.setHttpMethod(HttpMethod.GET);
	                List<NameValuePair> list = new ArrayList<NameValuePair>();
	                list.add(new NameValuePair("code", "027"));
	                webRequest.setRequestParameters(list);
	                //医疗余额明细页面
	                HtmlPage yanglaoAmount = webClient.getPage(webRequest);
	                Thread.sleep(2000);
	                System.out.println(yanglaoAmount.asText());
	
	                HtmlTable amountTable = (HtmlTable) yanglaoAmount.getElementById("chooseItemTable");
	                Double medicalInsuranceAmount = Double.parseDouble(amountTable.getCellAt(1, 3).getTextContent());
	                System.out.println(medicalInsuranceAmount);
	                
	                Double endowmentInsuranceAmount = (Double) yanglaoList.get(yanglaoList.size()-1);
	                //养老保险缴费余额
	                baseInfo.put("endowmentInsuranceAmount", df.format(endowmentInsuranceAmount));
	                yanglaoList.remove(yanglaoList.size()-1);
	                //医疗保险缴费余额
	                baseInfo.put("medicalInsuranceAmount", df.format(medicalInsuranceAmount));	
	                
	                dataMap.put("personalInfo", baseInfo);
	        		dataMap.put("endowmentInsurance", yanglaoList);
	        		dataMap.put("medicalInsurance", yiliaoList);
	        		dataMap.put("unemploymentInsurance", shiyeList);
	            	dataMap.put("accidentInsurance", gongshList);
	            	dataMap.put("maternityInsurance", shengyuList);
	                
	                
	            }catch (Exception e) {
	                logger.warn("重庆社保获取失败",e);
	                e.printStackTrace();
	                map.put("errorCode", "0001");
	                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
	            }finally {
	                webClient.close();
	            }
        }
        
        SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy年MM月dd日  hh:mm:ss" );
		String today = sdf.format(date);
        map.put("data", dataMap);       
        map.put("createTime", today);
        
        return map;
    }
    
   
    /**
     * 明细获取
     * @param webClient
     * @param type
     * @param url1
     * @param code
     * @param infoTableId
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	public List<Object> getInfos(WebClient webClient,String type,String url1,String code,String infoTableId) throws Exception{
    	List<Object> infosList=new ArrayList<Object>();

    	URL url = new URL(url1);
    	WebRequest webRequest = new WebRequest(url);
    	webRequest.setHttpMethod(HttpMethod.GET);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new NameValuePair("code", code));
        webRequest.setRequestParameters(list);
        //明细页面
        HtmlPage yanglaoinfo = webClient.getPage(webRequest);     
        Thread.sleep(2000);
        final String up = "上一页";
        if(!yanglaoinfo.asText().contains(up)){
        	return infosList;
	    }             
        //年份列表
        DomNodeList optionList = yanglaoinfo.getElementById("td_year").getElementsByTagName("option");
        //保险余额
        List<String> amount = new ArrayList<String>();
        for(int option=0;option<optionList.size();option++){          
        	String temp = yanglaoinfo.getElementsByTagName("span").get(0).getTextContent();
         	int num1 = temp.indexOf(" / ");
         	//页数
         	String count = temp.substring(num1+3);
        	SecurityBean securityBean = new SecurityBean();
        	//月数Set
        	Set<Object> monthList=new TreeSet<Object>();
        	for(int i=0;i<Integer.parseInt(count);i++){               		
        		HtmlTable infosTable = (HtmlTable) yanglaoinfo.getElementById(infoTableId);
        		if("015".equals(code)||"023".equals(code)){
        			securityBean = getinfos(infosTable,type,securityBean,monthList,amount,code);   
        		}else {
        			securityBean = getinfo(infosTable,type,securityBean,monthList,code);   
        		}
        		
        		HtmlForm infosForm = yanglaoinfo.getForms().get(1);   
        		infosForm.getElementsByTagName("a").get(2).click();
        		Thread.sleep(500);
        	}
        	if(!"0".equals(securityBean.getMonth_count())){
        		infosList.add(securityBean);
        	}      		       	
        	//数据类型select
        	HtmlSelect selectType=(HtmlSelect) yanglaoinfo.getElementsByTagName("select").get(0);
        	//选中下一年
		 	selectType.setSelectedIndex(option+1);
		 	alert.clear();
		 	yanglaoinfo = yanglaoinfo.getElementById("queryBtn").click();
		 	Thread.sleep(1000);
		    if(alert.size()==1&&alert.get(0).contains("没有当前条件所对应的")){
		    	break;
		    }
        }
        final String yanglaoCode = "015";
        if(yanglaoCode.equals(code)){
        	//养老余额
        	Double sumpay = amount(amount);
        	infosList.add(sumpay);
        }
        
        return infosList;
    }
    /**
     * 获取基本信息
     * @param request
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unused", "resource" })
	public Map<String,Object> getBaseInfo(HttpServletRequest request) throws Exception{
    	HttpSession session = request.getSession();
        Object htmlWebClient = session.getAttribute("htmlWebClient-chongqing");
        Object htmlPage = session.getAttribute("htmlPage-chongqing");
		Map<String,Object> baseInfo = new HashMap<String, Object>(10);
        Map<String, Object> map = new HashMap<>(10);
		if (htmlWebClient != null && htmlPage != null) {
			
            HtmlPage infosHtml = (HtmlPage) htmlPage;
            WebClient webClient = (WebClient) htmlWebClient;           
            HtmlTable infosTable = (HtmlTable) infosHtml.getElementById("basicInfoTable"); 
            //姓名
            baseInfo.put("name", infosTable.getCellAt(1, 1).getTextContent());
            //公民身份号码
    		baseInfo.put("identityCards", infosTable.getCellAt(3, 1).getTextContent());
    		//性别
    		baseInfo.put("sex", infosTable.getCellAt(2, 1).getTextContent());
    		//出生日期
    		baseInfo.put("birthDate", infosTable.getCellAt(4, 1).getTextContent());
    		//民族
    		baseInfo.put("nation", infosTable.getCellAt(2, 3).getTextContent());
    		//国家
    		baseInfo.put("country", "");
    		//个人身份
    		baseInfo.put("personalIdentity", infosTable.getCellAt(5, 3).getTextContent());
    		//参加工作时间
    		baseInfo.put("workDate", infosTable.getCellAt(3, 3).getTextContent());
    		//户口性质
    		baseInfo.put("residenceType",infosTable.getCellAt(5, 1).getTextContent());
    		//户口所在地地址
    		baseInfo.put("residenceAddr", "");
    		//户口所在地邮政编码
    		baseInfo.put("residencePostcodes", "");
    		//居住地(联系)地址
    		baseInfo.put("contactAddress", "");
    		//居住地（联系）邮政编码
    		baseInfo.put("contactPostcodes", "");
    		//获取对账单方式
    		baseInfo.put("queryMethod", "");
    		//电子邮件地址
    		baseInfo.put("email", "");
    		//文化程度
    		baseInfo.put("educationalBackground", "");
    		//参保人电话
    		baseInfo.put("telephone", "");
    		//参保人手机
    		baseInfo.put("phoneNo", "");
    		//申报月均工资收入（元）
    		baseInfo.put("income", "");
    		//证件类型
    		baseInfo.put("documentType", "");
    		//证件号码
    		baseInfo.put("documentNumber", infosTable.getCellAt(3, 1).getTextContent());
    		//委托代发银行名称
    		baseInfo.put("bankName", "");
    		//委托代发银行账号
    		baseInfo.put("bankNumber", "");
    		//缴费人员类别
    		baseInfo.put("paymentPersonnelCategory", "");
    		//医疗参保人员类别
    		baseInfo.put("insuredPersonCategory", "");
    		//离退休类别
    		baseInfo.put("retireType", "");
    		//离退休日期
    		baseInfo.put("retireDate", "");
    		//定点医疗机构 1
    		baseInfo.put("sentinelMedicalInstitutions1", "");
    		//定点医疗机构 2
    		baseInfo.put("sentinelMedicalInstitutions2","");
    		//定点医疗机构 3
    		baseInfo.put("sentinelMedicalInstitutions3", "");
    		//定点医疗机构 4
    		baseInfo.put("sentinelMedicalInstitutions4", "");
    		//定点医疗机构 5
    		baseInfo.put("sentinelMedicalInstitutions5", "");
    		//是否患有特殊病
    		baseInfo.put("specialDisease", "");
    		//失业保险缴费余额
    		baseInfo.put("unemploymentInsuranceAmount", "");
    		//生育保险缴费余额
			baseInfo.put("maternityInsuranceAmount", "");
			//工伤保险缴费余额
			baseInfo.put("accidentInsuranceAmount", "");
			//总额
			baseInfo.put("totalAmount", "");
			System.out.println("-------"+baseInfo);
    		
		}else {
            logger.warn("重庆社保基础信息获取出错 ");
            map.put("errorCode", "0001");
            map.put("errorInfo", "非法操作！");
        }
		return baseInfo;
		
    }
    
    
    
    
    /**
     * 解析保险信息(医疗和养老)
     * @param infosTable
     * @param type
     * @param securityBean
     * @param monthList
     * @param amount
     * @param num
     * @return
     */ 
    @SuppressWarnings({ "unused", "rawtypes" })
	public SecurityBean getinfos(HtmlTable infosTable,String type,SecurityBean securityBean,Set<Object> monthList,List<String> amount,String num){     	
    	List<SecurityBean> infosList=new ArrayList<SecurityBean>();
    	DomNodeList trList = infosTable.getElementsByTagName("tr");
    	String temp="";
    	
    	for(int tr=1;tr<trList.size();tr++){
    		//月份
    		String month = infosTable.getCellAt(tr, 0).getTextContent();
    		if(month.equals(temp)){
    			continue;
    		}
    		monthList.add(month);
    		//月份
    		String payType = infosTable.getCellAt(tr, 5).getTextContent();
    		if("已退收".equals(payType)){
    			monthList.remove(month);
    			temp=month;
    			continue;
    		}
    		if("欠缴".equals(payType)){
    			monthList.remove(month);
    			continue;
    		}
    		if(securityBean.getCompany_name()==null){	
    			securityBean.setBase_number(infosTable.getCellAt(tr, 3).getTextContent());
    			securityBean.setCompany_name(infosTable.getCellAt(tr, 1).getTextContent());
    			securityBean.setMonthly_personal_income(infosTable.getCellAt(tr, 4).getTextContent());
    			securityBean.setLast_pay_date(infosTable.getCellAt(tr, 0).getTextContent());
    			String year = infosTable.getCellAt(tr, 0).getTextContent();
    			securityBean.setYear(year.substring(0, 4));
    			if("正常参保".equals(type)){
    				type="缴存";
    			}
    			securityBean.setType(type);
    		}   
    		//为养老保险，要算余额
    		if("015".equals(num)){
    			String pay = infosTable.getCellAt(tr, 4).getTextContent();
        		amount.add(pay);
    		}
    		
    	}
    	
    	securityBean.setMonth_count(String.valueOf(monthList.size()));
    	return securityBean;
    }
    
    /**
     * 解析保险信息（其他三个保险）
     * @param infosTable
     * @param type
     * @param securityBean
     * @param monthList
     * @param num
     * @return
     */  
    @SuppressWarnings({ "unused", "rawtypes" })
	public SecurityBean getinfo(HtmlTable infosTable,String type,SecurityBean securityBean,Set<Object> monthList,String num){     	
    	List<SecurityBean> infosList=new ArrayList<SecurityBean>();
    	DomNodeList trList = infosTable.getElementsByTagName("tr");
    	String payType = "";
    	String month = "";
    	for(int tr=1;tr<trList.size();tr++){
    		if("043".equals(num)){
        		payType = infosTable.getCellAt(tr, 6).getTextContent();
        	}else{    		
        		payType = infosTable.getCellAt(tr, 5).getTextContent();
        	}
    		
    		if(!"足额缴费".equals(payType)&&!"已实缴".equals(payType)){   			
    			continue;
    		}
    		//为工伤保险
    		if("052".equals(num)){
    			//月份
    			month = infosTable.getCellAt(tr, 1).getTextContent();
    			
    		}else{
    			//月份
    			month = infosTable.getCellAt(tr, 0).getTextContent();
    		}
    		monthList.add(month);
    		if(securityBean.getCompany_name()==null){
    			
    			if("052".equals(num)){
    				securityBean.setCompany_name(infosTable.getCellAt(tr, 2).getTextContent());
    				securityBean.setBase_number(infosTable.getCellAt(tr, 4).getTextContent());
    			}else {
    				securityBean.setCompany_name(infosTable.getCellAt(tr, 1).getTextContent());
    				securityBean.setBase_number(infosTable.getCellAt(tr, 3).getTextContent());
    			}   			
    			securityBean.setLast_pay_date(month);    		
    			securityBean.setYear(month.substring(0, 4));
    			if("正常参保".equals(type)){
    				type="缴存";
    			}
    			securityBean.setType(type);
    		}    		
    	}
    	securityBean.setMonth_count(String.valueOf(monthList.size()));
    	return securityBean;
    }
    
    /**
     * 计算余额（养老保险）
     * @param amount
     * @return
     */   
    public Double amount(List<String> amount){
    	Double sumAmount = 0.00;
    	for(int i=0;i<amount.size();i++){
    		sumAmount = sumAmount+Double.parseDouble(amount.get(i));
    	}
    	return sumAmount;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
