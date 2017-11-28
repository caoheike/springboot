package com.reptile.service.socialSecurity;

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
@Service
public class ChongQingSocialSecurityService {
	@Autowired 
	private application applicat;
	private Logger logger = LoggerFactory.getLogger(GuiYangAccumulationfundService.class);
	DecimalFormat df= new DecimalFormat("#.00");
    List<String> alert=new ArrayList<>();
    public Map<String, Object> loadImageCode(HttpServletRequest request){
        logger.warn("获取重庆社保图片验证码");
        Map<String, Object> map = new HashMap<>();
        Map<String, String> datamap = new HashMap<>();
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
/*
 * 确定是否需要选择要查询参保的信息
 */
    public Map<String, Object> getQueryDeatil(HttpServletRequest request, String userCard, String password, String imageCode,String idCardNum) {
        Map<String, Object> map = new HashMap<>();
        
    	
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
	    		 HtmlPage Infos = webClient.getPage(webRequest);
	    		 Thread.sleep(2000);              		       		      		
	       		System.out.println("---------------------------"+Infos.asText());
	       		session.setAttribute("htmlWebClient-chongqing", webClient);
	             session.setAttribute("basicInfos-chongqing", Infos);
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
        if(map!=null&&"0000".equals(map.get("errorCode").toString())){
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
   
    
      /*
       * 信息获取        
       */
    public Map<String, Object> getDeatil(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
    	Map<String,Object> baseInfo = new HashMap<String, Object>();
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
	                if(basicInfos.asText().indexOf("个人基础信息")==-1){
	                	logger.warn("重庆社保基本信息获取失败");
	                    map.put("errorCode", "0001");
	                    map.put("errorInfo", "当前网络繁忙，请刷新后重试");
	                    return map;//
	                }
	                HtmlTable typeTable = (HtmlTable) basicInfos.getElementsByTagName("table").get(2);//参保状态信息
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
	                HtmlPage yanglaoAmount = webClient.getPage(webRequest);//医疗余额明细页面
	                Thread.sleep(2000);
	                System.out.println(yanglaoAmount.asText());
	
	                HtmlTable AmountTable = (HtmlTable) yanglaoAmount.getElementById("chooseItemTable");
	                Double medicalInsuranceAmount = Double.parseDouble(AmountTable.getCellAt(1, 3).getTextContent());
	                System.out.println(medicalInsuranceAmount);
	                
	                Double endowmentInsuranceAmount = (Double) yanglaoList.get(yanglaoList.size()-1);
	                baseInfo.put("endowmentInsuranceAmount", df.format(endowmentInsuranceAmount));//养老保险缴费余额
	                yanglaoList.remove(yanglaoList.size()-1);
	                baseInfo.put("medicalInsuranceAmount", df.format(medicalInsuranceAmount));//医疗保险缴费余额	
	                
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
    
   
    
    /*
     * 明细获取
     */  
    public List<Object> getInfos(WebClient webClient,String Type,String Url,String code,String infoTableId) throws Exception{
    	List<Object> InfosList=new ArrayList<Object>();

    	URL url = new URL(Url);
    	WebRequest webRequest = new WebRequest(url);
    	webRequest.setHttpMethod(HttpMethod.GET);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new NameValuePair("code", code));
        webRequest.setRequestParameters(list);
        HtmlPage yanglaoinfo = webClient.getPage(webRequest);//明细页面     
        Thread.sleep(2000);
        if(!yanglaoinfo.asText().contains("上一页")){
        	return InfosList;
	    }             
        DomNodeList optionList = yanglaoinfo.getElementById("td_year").getElementsByTagName("option");//年份列表
        List<String> amount = new ArrayList<String>();//保险余额
        for(int option=0;option<optionList.size();option++){          
        	String temp = yanglaoinfo.getElementsByTagName("span").get(0).getTextContent();
         	int num1 = temp.indexOf(" / ");
         	String count = temp.substring(num1+3);//页数
        	SecurityBean securityBean = new SecurityBean();
        	Set<Object> monthList=new TreeSet<Object>();//月数Set
        	for(int i=0;i<Integer.parseInt(count);i++){               		
        		HtmlTable infosTable = (HtmlTable) yanglaoinfo.getElementById(infoTableId);
        		if("015".equals(code)||"023".equals(code)){
        			securityBean = getinfos(infosTable,Type,securityBean,monthList,amount,code);   
        		}else {
        			securityBean = getinfo(infosTable,Type,securityBean,monthList,code);   
        		}
        		
        		HtmlForm infosForm = yanglaoinfo.getForms().get(1);   
        		infosForm.getElementsByTagName("a").get(2).click();
        		Thread.sleep(500);
        	}
        	if(!"0".equals(securityBean.getMonth_count())){
        		InfosList.add(securityBean);
        	}      		       	
        	HtmlSelect selectType=(HtmlSelect) yanglaoinfo.getElementsByTagName("select").get(0);//数据类型select
		 	selectType.setSelectedIndex(option+1);//选中下一年
		 	alert.clear();
		 	yanglaoinfo = yanglaoinfo.getElementById("queryBtn").click();
		 	Thread.sleep(1000);
		    if(alert.size()==1&&alert.get(0).contains("没有当前条件所对应的")){
		    	break;
		    }
        }
        if("015".equals(code)){
        	Double sumpay = amount(amount);//养老余额
            InfosList.add(sumpay);
        }
        
        return InfosList;
    }
    
    /*
     * 获取基本信息
     */
    public Map<String,Object> getBaseInfo(HttpServletRequest request) throws Exception{
    	HttpSession session = request.getSession();
        Object htmlWebClient = session.getAttribute("htmlWebClient-chongqing");
        Object htmlPage = session.getAttribute("htmlPage-chongqing");
		Map<String,Object> baseInfo = new HashMap<String, Object>();
        Map<String, Object> map = new HashMap<>();
		if (htmlWebClient != null && htmlPage != null) {
            HtmlPage infosHtml = (HtmlPage) htmlPage;
            WebClient webClient = (WebClient) htmlWebClient;           
            HtmlTable infosTable = (HtmlTable) infosHtml.getElementById("basicInfoTable");          
            baseInfo.put("name", infosTable.getCellAt(1, 1).getTextContent());//姓名
    		baseInfo.put("identityCards", infosTable.getCellAt(3, 1).getTextContent());//公民身份号码
    		baseInfo.put("sex", infosTable.getCellAt(2, 1).getTextContent());//性别
    		baseInfo.put("birthDate", infosTable.getCellAt(4, 1).getTextContent());//出生日期
    		baseInfo.put("nation", infosTable.getCellAt(2, 3).getTextContent());//民族
    		baseInfo.put("country", "");//国家
    		baseInfo.put("personalIdentity", infosTable.getCellAt(5, 3).getTextContent());//个人身份
    		baseInfo.put("workDate", infosTable.getCellAt(3, 3).getTextContent());//参加工作时间
    		baseInfo.put("residenceType",infosTable.getCellAt(5, 1).getTextContent());//户口性质
    		baseInfo.put("residenceAddr", "");//户口所在地地址
    		baseInfo.put("residencePostcodes", "");//户口所在地邮政编码
    		baseInfo.put("contactAddress", "");//居住地(联系)地址
    		baseInfo.put("contactPostcodes", "");//居住地（联系）邮政编码
    		baseInfo.put("queryMethod", "");//获取对账单方式
    		baseInfo.put("email", "");//电子邮件地址
    		baseInfo.put("educationalBackground", "");//文化程度
    		baseInfo.put("telephone", "");//参保人电话
    		baseInfo.put("phoneNo", "");//参保人手机
    		baseInfo.put("income", "");//申报月均工资收入（元）
    		baseInfo.put("documentType", "");//证件类型
    		baseInfo.put("documentNumber", infosTable.getCellAt(3, 1).getTextContent());//证件号码
    		baseInfo.put("bankName", "");//委托代发银行名称
    		baseInfo.put("bankNumber", "");//委托代发银行账号
    		baseInfo.put("paymentPersonnelCategory", "");//缴费人员类别
    		baseInfo.put("insuredPersonCategory", "");//医疗参保人员类别
    		baseInfo.put("retireType", "");//离退休类别
    		baseInfo.put("retireDate", "");//离退休日期
    		baseInfo.put("sentinelMedicalInstitutions1", "");//定点医疗机构 1
    		baseInfo.put("sentinelMedicalInstitutions2","");//定点医疗机构 2
    		baseInfo.put("sentinelMedicalInstitutions3", "");//定点医疗机构 3
    		baseInfo.put("sentinelMedicalInstitutions4", "");//定点医疗机构 4
    		baseInfo.put("sentinelMedicalInstitutions5", "");//定点医疗机构 5
    		baseInfo.put("specialDisease", "");//是否患有特殊病
    		baseInfo.put("unemploymentInsuranceAmount", "");//失业保险缴费余额
			
			baseInfo.put("maternityInsuranceAmount", "");//生育保险缴费余额
			baseInfo.put("accidentInsuranceAmount", "");//工伤保险缴费余额
			baseInfo.put("totalAmount", "");//总额
			System.out.println("-------"+baseInfo);
    		
		}else {
            logger.warn("重庆社保基础信息获取出错 ");
            map.put("errorCode", "0001");
            map.put("errorInfo", "非法操作！");
        }
		return baseInfo;
		
    }
    
    
    
    
    
    /*
     * 解析保险信息(医疗和养老)
     */
    public SecurityBean getinfos(HtmlTable InfosTable,String type,SecurityBean securityBean,Set<Object> monthList,List<String> amount,String num){     	
    	List<SecurityBean> infosList=new ArrayList<SecurityBean>();
    	DomNodeList trList = InfosTable.getElementsByTagName("tr");
    	String temp="";
    	
    	for(int tr=1;tr<trList.size();tr++){
    		String month = InfosTable.getCellAt(tr, 0).getTextContent();//月份
    		if(month.equals(temp)){
    			continue;
    		}
    		monthList.add(month);
    		String payType = InfosTable.getCellAt(tr, 5).getTextContent();//月份
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
    			securityBean.setBase_number(InfosTable.getCellAt(tr, 3).getTextContent());
    			securityBean.setCompany_name(InfosTable.getCellAt(tr, 1).getTextContent());
    			securityBean.setMonthly_personal_income(InfosTable.getCellAt(tr, 4).getTextContent());
    			securityBean.setLast_pay_date(InfosTable.getCellAt(tr, 0).getTextContent());
    			String year = InfosTable.getCellAt(tr, 0).getTextContent();
    			securityBean.setYear(year.substring(0, 4));
    			if("正常参保".equals(type)){
    				type="缴存";
    			}
    			securityBean.setType(type);
    		}   
    		if("015".equals(num)){//为养老保险，要算余额
    			String pay = InfosTable.getCellAt(tr, 4).getTextContent();
        		amount.add(pay);
    		}
    		
    	}
    	
    	securityBean.setMonth_count(String.valueOf(monthList.size()));
    	return securityBean;
    }
    /*
     * 解析保险信息（其他三个保险）
     */
    public SecurityBean getinfo(HtmlTable InfosTable,String type,SecurityBean securityBean,Set<Object> monthList,String num){     	
    	List<SecurityBean> infosList=new ArrayList<SecurityBean>();
    	DomNodeList trList = InfosTable.getElementsByTagName("tr");
    	String payType = "";
    	String month = "";
    	for(int tr=1;tr<trList.size();tr++){
    		if("043".equals(num)){
        		payType = InfosTable.getCellAt(tr, 6).getTextContent();
        	}else{    		
        		payType = InfosTable.getCellAt(tr, 5).getTextContent();
        	}
    		
    		if(!"足额缴费".equals(payType)&&!"已实缴".equals(payType)){   			
    			continue;
    		}
    		if("052".equals(num)){//为工伤保险
    			month = InfosTable.getCellAt(tr, 1).getTextContent();//月份
    			
    		}else{
    			month = InfosTable.getCellAt(tr, 0).getTextContent();//月份
    		}
    		monthList.add(month);
    		if(securityBean.getCompany_name()==null){
    			
    			if("052".equals(num)){
    				securityBean.setCompany_name(InfosTable.getCellAt(tr, 2).getTextContent());//
    				securityBean.setBase_number(InfosTable.getCellAt(tr, 4).getTextContent());//
    			}else {
    				securityBean.setCompany_name(InfosTable.getCellAt(tr, 1).getTextContent());//
    				securityBean.setBase_number(InfosTable.getCellAt(tr, 3).getTextContent());//
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
    /*
     * 计算余额（养老保险）
     */
    public Double amount(List<String> amount){
    	Double sumAmount = 0.00;
    	for(int i=0;i<amount.size();i++){
    		sumAmount = sumAmount+Double.parseDouble(amount.get(i));
    	}
    	return sumAmount;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
