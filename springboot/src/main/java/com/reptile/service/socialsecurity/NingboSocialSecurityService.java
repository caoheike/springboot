package com.reptile.service.socialsecurity;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.reptile.model.SecurityBean;
import com.reptile.service.accumulationfund.GuiYangAccumulationfundService;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 
 * @ClassName: NingboSocialSecurityService  
 * @Description: TODO  
 * @author: fangshuang
 * @date 2017年12月29日  
 *
 */
@Service
public class NingboSocialSecurityService {
	@Autowired 
	private application applicat;
	
	private Logger logger = LoggerFactory.getLogger(GuiYangAccumulationfundService.class);
	DecimalFormat df= new DecimalFormat("#.00");
    public Map<String, Object> loadImageCode(HttpServletRequest request){
        logger.warn("获取宁波社保图片验证码");
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
            page = webClient.getPage("https://rzxt.nbhrss.gov.cn/nbsbk-rzxt/web/pages/index.jsp");
            Thread.sleep(2000);
            HtmlImage rand = (HtmlImage) page.getElementById("yzmJsp");
            BufferedImage read = rand.getImageReader().read(0);
            String fileName = "guiyang" + System.currentTimeMillis() + ".png";
            ImageIO.write(read, "png", new File(file, fileName));
            datamap.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/ImageCode/" + fileName);
            map.put("errorCode", "0000");
            map.put("errorInfo", "加载验证码成功");
            map.put("data", datamap);
            session.setAttribute("htmlWebClient-ningbo", webClient);
            session.setAttribute("htmlPage-ningbo", page);
        } catch (Exception e) {
            logger.warn("宁波社保 ", e);
            map.put("errorCode", "0001");
            map.put("errorInfo", "当前网络繁忙，请刷新后重试");
            e.printStackTrace();

        }
        return map;
    }
    @SuppressWarnings("unused")
	public Map<String, Object> getDeatilMes(HttpServletRequest request, String userCard, String password, String imageCode,String idCardNum) {
        Map<String, Object> map = new HashMap<>(10);
        Map<String, Object> dataMap = new HashMap<>(10);
        Map<String, Object> loansdata = new HashMap<>(10);
    	Map<String,Object> baseInfo = new HashMap<String, Object>(10);
    	List<Object> yanglaoList=new ArrayList<Object>();
    	List<Object> yiliaoList=new ArrayList<Object>();
    	List<Object> shiyeList=new ArrayList<Object>();
    	List<Object> gongshList=new ArrayList<Object>();
    	List<Object> shengyuList=new ArrayList<Object>();
    	
        Date date=new Date();
        HttpSession session = request.getSession();
        Object htmlWebClient = session.getAttribute("htmlWebClient-ningbo");
        Object htmlPage = session.getAttribute("htmlPage-ningbo");

        if (htmlWebClient != null && htmlPage != null) {
            HtmlPage page = (HtmlPage) htmlPage;
            WebClient webClient = (WebClient) htmlWebClient;
            List<String> alert=new ArrayList<>();
            CollectingAlertHandler alertHandler=new CollectingAlertHandler(alert);
            webClient.setAlertHandler(alertHandler);
            try {
                page.getElementById("loginid").setAttribute("value",userCard);
                page.getElementById("pwd").setAttribute("value",password);
                page.getElementById("yzm").setAttribute("value",imageCode);
                HtmlPage pageLogin = page.getElementById("btnLogin").click();
                Thread.sleep(2000);
                String errorInfo = pageLogin.getElementById("errDiv").getTextContent();  
                System.out.println(errorInfo);
                final String a= "请妥善保管好";
                final String b= "验证码输入错误";
                final String c= "E1001";
                final String d= "账号或者密码不正确";
                
                if(!errorInfo.contains(a)){
                	logger.warn(errorInfo);
                	if(errorInfo.contains(d)){
                		 map.put("errorCode", "0005");
                		 map.put("errorInfo", "账号或者密码不正确！");	
                	}else if(errorInfo.contains(b)){
                		 map.put("errorCode", "0005");
                		 map.put("errorInfo", "验证码输入错误！");
                	}else if(errorInfo.contains(c)){
               		 map.put("errorCode", "0005");
               		 map.put("errorInfo", "证件号错误！");
                	}else{
                		map.put("errorCode", "0001");
                		map.put("errorInfo",errorInfo);
                	}
                	
                    
                    return map;
                }
            	PushState.state(idCardNum, "socialSecurity", 100);
                HtmlPage basicInfos = webClient.getPage("https://rzxt.nbhrss.gov.cn/nbsbk-rzxt/web/pages/query/query-grxx.jsp");
                Thread.sleep(2000);
                final String e= "个人信息";
                if(basicInfos.asText().indexOf(e)==-1){
                	logger.warn("宁波社保基本信息获取失败！");
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "宁波社保基本信息获取失败！");
                    return map;
                }
                String idcard = basicInfos.getElementById("sfz").getTextContent();
                String birthday = idcard .substring(6,10)+"."+idcard .substring(10,12)+"."+idcard .substring(12,14);
                baseInfo.put("name", basicInfos.getElementById("xm").getTextContent());
        		baseInfo.put("identityCards", idcard);
        		baseInfo.put("sex", basicInfos.getElementById("xb").getTextContent());
        		baseInfo.put("birthDate", birthday);
        		baseInfo.put("nation", "");
        		baseInfo.put("country", basicInfos.getElementById("gj").getTextContent());
        		baseInfo.put("personalIdentity", "");
        		baseInfo.put("workDate", "");
        		baseInfo.put("residenceType", "");
        		baseInfo.put("residenceAddr", "");
        		baseInfo.put("residencePostcodes","");
        		baseInfo.put("contactAddress", basicInfos.getElementById("czdz").getTextContent());
        		baseInfo.put("contactPostcodes", basicInfos.getElementById("yzbm").getTextContent());
        		baseInfo.put("queryMethod", "");
        		baseInfo.put("email", "");
        		baseInfo.put("educationalBackground", "");
        		baseInfo.put("telephone","");
        		baseInfo.put("phoneNo", "");
        		baseInfo.put("income", "");
        		baseInfo.put("documentType", "");
        		baseInfo.put("documentNumber", "");
        		baseInfo.put("bankName", "");
        		baseInfo.put("bankNumber", "");
        		baseInfo.put("paymentPersonnelCategory", "");
        		baseInfo.put("insuredPersonCategory", "");
        		baseInfo.put("retireType", "");
        		baseInfo.put("retireDate", "");
        		baseInfo.put("sentinelMedicalInstitutions1", "");
        		baseInfo.put("sentinelMedicalInstitutions2", "");
        		baseInfo.put("sentinelMedicalInstitutions3", "");
        		baseInfo.put("sentinelMedicalInstitutions4", "");
        		baseInfo.put("sentinelMedicalInstitutions5", "");
        		baseInfo.put("specialDisease", "");		      
        		yanglaoList = getYangLaoInfo(webClient);
        		Double endowmentInsuranceAmount = (Double) yanglaoList.get(yanglaoList.size()-1);
        		baseInfo.put("endowmentInsuranceAmount",endowmentInsuranceAmount);
        		yanglaoList.remove(yanglaoList.size()-1);
        		yiliaoList = getYiLiaoList(webClient);
        		Double medicalInsuranceAmount = (Double) yiliaoList.get(yiliaoList.size()-1);
        		baseInfo.put("medicalInsuranceAmount",medicalInsuranceAmount);
        		
        		yiliaoList.remove(yiliaoList.size()-1);
        		//失业保险缴费余额
        		baseInfo.put("unemploymentInsuranceAmount", "");
        		//生育保险缴费余额
        		baseInfo.put("maternityInsuranceAmount", "");
        		//工伤保险缴费余额
        		baseInfo.put("accidentInsuranceAmount", "");
        		dataMap.put("personalInfo", baseInfo);
        		dataMap.put("endowmentInsurance", yanglaoList);
        		dataMap.put("medicalInsurance", yiliaoList);
        		dataMap.put("unemploymentInsurance", shiyeList);
            	dataMap.put("accidentInsurance", gongshList);
            	dataMap.put("maternityInsurance", shengyuList);
            	dataMap.put("totalAmount", df.format(endowmentInsuranceAmount+medicalInsuranceAmount));
            }catch (Exception e) {
                logger.warn("宁波社保获取失败",e);
                e.printStackTrace();
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
                return map;
            }finally {
                webClient.close();
            }
        } else {
            logger.warn("宁波社保登录过程中出错 ");
            map.put("errorCode", "0001");
            map.put("errorInfo", "非法操作！请确认验证码是否正确！");
            return map;
        }
        
        SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy年MM月dd日  hh:mm:ss" );
		String today = sdf.format(date);
        map.put("data", dataMap);
        map.put("cityName", "宁波");
        map.put("city", "010");
        map.put("userId", idCardNum);
        map.put("createTime", today);
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
    @SuppressWarnings("unused")
	public List<Object> getYangLaoInfo(WebClient webClient) throws Exception{
    	List<Object> yanglaoList=new ArrayList<Object>();
        HtmlPage basicInfos = webClient.getPage("https://rzxt.nbhrss.gov.cn/nbsbk-rzxt/web/pages/query/query-ylbx.jsp");
        Thread.sleep(2000);
        String companyName = basicInfos.getElementById("ylbx").getElementsByTagName("td").get(2).getTextContent().substring(5);
        String type = basicInfos.getElementById("ylbx").getElementsByTagName("td").get(3).getTextContent().substring(5);
        final String f = "参保缴费";
        if(f.equals(type)){
        	type="缴存";
        }
        int num1 = basicInfos.getElementById("sjzl").getTextContent().indexOf("共 ");
        int num2 = basicInfos.getElementById("sjzl").getTextContent().indexOf(" 条");
        String num = basicInfos.getElementById("sjzl").getTextContent().substring(num1+2, num2);
        HtmlTable mytable = (HtmlTable) basicInfos.getElementById("mytable");
        SecurityBean yanglaoBean = null;
        String year="";        
        int month=0;
        //缴费余额
        double endowmentInsuranceAmount=0.00;
        for(int i=1;i<=(Integer.valueOf(num)-1);i++){       	
        	//第i行年份
        	String tableyear1 = mytable.getCellAt(i, 0).asText().substring(0, 4);
        	//第i+1行年份
        	String tableyear2 = mytable.getCellAt(i+1, 0).asText().substring(0, 4);
        	//缴费金额
        	String jine = mytable.getCellAt(i, 2).asText();
        	if(tableyear1.equals(tableyear2)){       		
        		month=month+1;
        		if(month==2){  
        			yanglaoBean = new SecurityBean();
        			yanglaoBean.setYear(tableyear1);
        			yanglaoBean.setBase_number(mytable.getCellAt(i, 1).asText());
        			yanglaoBean.setLast_pay_date(mytable.getCellAt(i, 0).asText());
        			yanglaoBean.setCompany_name(companyName);
        			yanglaoBean.setType(type); 
        			yanglaoBean.setMonthly_personal_income(mytable.getCellAt(i, 2).asText());
        		}       		
        	}else {      		
        		month=month+1;
        		yanglaoBean.setMonth_count(String.valueOf(month));
        		
        		yanglaoList.add(yanglaoBean);
        		System.out.println(yanglaoBean);
        		//年缴纳月份归0
        		month=1;
        	}
        	if(i==Integer.valueOf(num)-1){
        		month=month+1;
        		yanglaoBean.setMonth_count(String.valueOf(month));
        		
        		yanglaoList.add(yanglaoBean);
        		System.out.println(yanglaoBean);
        	}
        	//养老金余额
        	endowmentInsuranceAmount=endowmentInsuranceAmount+Double.valueOf(jine);
        	
        }
        yanglaoList.add(df.format(endowmentInsuranceAmount));
        return yanglaoList;
    }
    public List<Object> getYiLiaoList(WebClient webClient) throws Exception{
    	List<Object> yiliaoList=new ArrayList<Object>();
        HtmlPage basicInfos = webClient.getPage("https://rzxt.nbhrss.gov.cn/nbsbk-rzxt/web/pages/query/query-yilbx.jsp");		
        Thread.sleep(2000);
        //判断用户属于医保城镇居民，还是市级城镇职工
        final String g = "服务不可用，请稍候再试";
        if(basicInfos.asText().indexOf(g)!=-1){
        	basicInfos = basicInfos.getElementById("yilbxLi2").click();
        	Thread.sleep(1000);
        }
        String companyName = basicInfos.getElementById("yilbx").getElementsByTagName("td").get(2).getTextContent().substring(5);
        String type = basicInfos.getElementById("yilbx").getElementsByTagName("td").get(4).getTextContent().substring(5);
        final String t = "参保缴费";
        if(t.equals(type)){
        	type="缴存";
        }
        int num1 = basicInfos.getElementById("sjzl").getTextContent().indexOf("共 ");
        int num2 = basicInfos.getElementById("sjzl").getTextContent().indexOf(" 条");
        //记录条数
        String num = basicInfos.getElementById("sjzl").getTextContent().substring(num1+2, num2);
        HtmlTable mytable = (HtmlTable) basicInfos.getElementById("mytable");
        SecurityBean yiliaoBean = null;
        @SuppressWarnings("unused")
		String year="";
        int month=0;
        //缴费余额
        double medicalInsuranc=0.00;
        for(int i=1;i<=(Integer.valueOf(num)-1);i++){       	
        	//第i行年份
        	String tableyear1 = mytable.getCellAt(i, 0).asText().substring(0, 4);
        	//第i+1行年份
        	String tableyear2 = mytable.getCellAt(i+1, 0).asText().substring(0, 4);
        	//缴费金额
        	String jine = mytable.getCellAt(i, 2).asText();
        	if(tableyear1.equals(tableyear2)){    
        		month=month+1;
        		if(month==2){   
        			yiliaoBean = new SecurityBean();
        			yiliaoBean.setYear(tableyear1);
        			yiliaoBean.setBase_number(mytable.getCellAt(i, 1).asText());
        			yiliaoBean.setLast_pay_date(mytable.getCellAt(i, 0).asText());
        			yiliaoBean.setCompany_name(companyName);
        			yiliaoBean.setType(type);
        			yiliaoBean.setMonthly_personal_income(mytable.getCellAt(i, 2).asText());
        		}       		
        	}else {
        		month=month+1;
        		yiliaoBean.setMonth_count(String.valueOf(month));        			
    			yiliaoList.add(yiliaoBean);
    			System.out.println(yiliaoBean);
    			//年缴纳月份归0
        		month=1;
        	}
        	if(i==Integer.valueOf(num)-1){
        		month=month+1;
        		yiliaoBean.setMonth_count(String.valueOf(month));
        		
    			yiliaoList.add(yiliaoBean);
    			System.out.println(yiliaoBean);
        	}
        	//医疗余额
        	medicalInsuranc = medicalInsuranc+Double.valueOf(jine);
        }
        HtmlTable table = (HtmlTable) basicInfos.getElementById("mytable2");
        @SuppressWarnings("rawtypes")
		DomNodeList tr = table.getElementsByTagName("tr");
        //医疗花去费用
        double n = 0.00;
        for (int i=1;i<tr.size();i++){
        	n = n+Double.valueOf(table.getCellAt(i, 7).asText());
        }
        medicalInsuranc = medicalInsuranc+n;
        yiliaoList.add(df.format(medicalInsuranc));
        return yiliaoList;
    }
 }
