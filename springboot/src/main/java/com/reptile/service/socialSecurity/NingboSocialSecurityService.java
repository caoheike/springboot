package com.reptile.service.socialSecurity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.reptile.model.SecurityBean;
import com.reptile.service.accumulationfund.GuiYangAccumulationfundService;
import com.reptile.util.ConstantInterface;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;

@Service
public class NingboSocialSecurityService {
	@Autowired 
	private application applicat;
	
	private Logger logger = LoggerFactory.getLogger(GuiYangAccumulationfundService.class);
	DecimalFormat df= new DecimalFormat("#.00");
    public Map<String, Object> loadImageCode(HttpServletRequest request){
        logger.warn("获取宁波社保图片验证码");
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
    public Map<String, Object> getDeatilMes(HttpServletRequest request, String userCard, String password, String imageCode) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        Map<String, Object> loansdata = new HashMap<>();
    	Map<String,Object> baseInfo = new HashMap<String, Object>();
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
                HtmlPage logon = page.getElementById("btnLogin").click();
                Thread.sleep(5000);
                System.out.println(alert.size());
                logger.warn("登录宁波住房公积金:"+alert.size());
                if(alert.size()>0){
                    map.put("errorCode", "0005");
                    map.put("errorInfo", alert.get(0));
                    return map;
                }
                HtmlPage basicInfos = webClient.getPage("https://rzxt.nbhrss.gov.cn/nbsbk-rzxt/web/pages/query/query-grxx.jsp");
                Thread.sleep(2000);
                if(basicInfos.asText().indexOf("个人信息")==-1){
                	logger.warn("宁波社保基本信息获取失败");
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "当前网络繁忙，请刷新后重试");
                }
                String idcard = basicInfos.getElementById("sfz").getTextContent();
                String birthday = idcard .substring(6,10)+"."+idcard .substring(10,12)+"."+idcard .substring(12,14);
                baseInfo.put("name", basicInfos.getElementById("xm").getTextContent());//姓名
        		baseInfo.put("identityCards", idcard);//公民身份号码
        		baseInfo.put("sex", basicInfos.getElementById("xb").getTextContent());//性别
        		baseInfo.put("birthDate", birthday);//出生日期
        		baseInfo.put("nation", "");//民族
        		baseInfo.put("country", basicInfos.getElementById("gj").getTextContent());//国家
        		baseInfo.put("personalIdentity", "");//个人身份
        		baseInfo.put("workDate", "");//参加工作时间
        		baseInfo.put("residenceType", "");//户口性质
        		baseInfo.put("residenceAddr", "");//户口所在地地址
        		baseInfo.put("residencePostcodes","");//户口所在地邮政编码
        		baseInfo.put("contactAddress", basicInfos.getElementById("czdz").getTextContent());//居住地(联系)地址
        		baseInfo.put("contactPostcodes", basicInfos.getElementById("yzbm").getTextContent());//居住地（联系）邮政编码
        		baseInfo.put("queryMethod", "");//获取对账单方式
        		baseInfo.put("email", "");//电子邮件地址
        		baseInfo.put("educationalBackground", "");//文化程度
        		baseInfo.put("telephone","");//参保人电话
        		baseInfo.put("phoneNo", "");//参保人手机
        		baseInfo.put("income", "");//申报月均工资收入（元）
        		baseInfo.put("documentType", "");//证件类型/////
        		baseInfo.put("documentNumber", "");//证件号码////////
        		baseInfo.put("bankName", "");//委托代发银行名称
        		baseInfo.put("bankNumber", "");//委托代发银行账号
        		baseInfo.put("paymentPersonnelCategory", "");//缴费人员类别
        		baseInfo.put("insuredPersonCategory", "");//医疗参保人员类别
        		baseInfo.put("retireType", "");//离退休类别
        		baseInfo.put("retireDate", "");//离退休日期
        		baseInfo.put("sentinelMedicalInstitutions1", "");//定点医疗机构 1
        		baseInfo.put("sentinelMedicalInstitutions2", "");//定点医疗机构 2
        		baseInfo.put("sentinelMedicalInstitutions3", "");//定点医疗机构 3
        		baseInfo.put("sentinelMedicalInstitutions4", "");//定点医疗机构 4
        		baseInfo.put("sentinelMedicalInstitutions5", "");//定点医疗机构 5
        		baseInfo.put("specialDisease", "");//是否患有特殊病       		      
        		yanglaoList = getYangLaoInfo(webClient);//获取养老List
        		baseInfo.put("endowmentInsuranceAmount",yanglaoList.get(yanglaoList.size()-1));
        		yanglaoList.remove(yanglaoList.size()-1);//去掉list中养老余额
        		yiliaoList = getYiLiaoList(webClient);//获取医疗List
        		baseInfo.put("medicalInsuranceAmount",yiliaoList.get(yiliaoList.size()-1));
        		yiliaoList.remove(yiliaoList.size()-1);//去掉list中医疗余额
        		baseInfo.put("unemploymentInsuranceAmount", "");//失业保险缴费余额
        		baseInfo.put("maternityInsuranceAmount", "");//生育保险缴费余额
        		baseInfo.put("accidentInsuranceAmount", "");//工伤保险缴费余额
        		dataMap.put("personalInfo", baseInfo);
        		dataMap.put("endowmentInsurance", yanglaoList);
        		dataMap.put("medicalInsurance", yiliaoList);
        		dataMap.put("unemploymentInsurance", shiyeList);
            	dataMap.put("accidentInsurance", gongshList);
            	dataMap.put("maternityInsurance", shengyuList);
            }catch (Exception e) {
                logger.warn("宁波社保获取失败",e);
                e.printStackTrace();
                map.put("errorCode", "0001");
                map.put("errorInfo", "当前网络繁忙，请刷新后重试");
            }finally {
                webClient.close();
            }
        } else {
            logger.warn("宁波社保登录过程中出错 ");
            map.put("errorCode", "0001");
            map.put("errorInfo", "非法操作！");
        }
        
        SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy年MM月dd日  hh:mm:ss" );
		String today = sdf.format(date);
        map.put("data", dataMap);
        map.put("cityName", "宁波");
        map.put("city", "010");
        map.put("userId", userCard);
        map.put("createTime", today);
        /*Resttemplate resttemplate=new Resttemplate();
        map = resttemplate.SendMessage(map, applicat.getSendip()+"/HSDC/person/socialSecurity");*/
        return map;
    }
    public List<Object> getYangLaoInfo(WebClient webClient) throws Exception{
    	List<Object> yanglaoList=new ArrayList<Object>();
        HtmlPage basicInfos = webClient.getPage("https://rzxt.nbhrss.gov.cn/nbsbk-rzxt/web/pages/query/query-ylbx.jsp");
        Thread.sleep(2000);
        String company_name = basicInfos.getElementById("ylbx").getElementsByTagName("td").get(2).getTextContent().substring(5);
        String type = basicInfos.getElementById("ylbx").getElementsByTagName("td").get(3).getTextContent().substring(5);
        if("参保缴费".equals(type)){
        	type="缴存";
        }
        //String niandu = basicInfos.getElementById("ylbx3").getElementsByTagName("td").get(0).getTextContent().substring(3);//最近整年交社保年份
        //String jiaofei = basicInfos.getElementById("ylbx3").getElementsByTagName("td").get(5).getTextContent().substring(12);//截止最近整年交社保的缴费
        int num1 = basicInfos.getElementById("sjzl").getTextContent().indexOf("共 ");
        int num2 = basicInfos.getElementById("sjzl").getTextContent().indexOf(" 条");
        String num = basicInfos.getElementById("sjzl").getTextContent().substring(num1+2, num2);
        HtmlTable mytable = (HtmlTable) basicInfos.getElementById("mytable");
        SecurityBean YangLaoBean = null;
        String year="";        
        int month=0;
        double endowmentInsuranceAmount=0.00;//缴费余额
        for(int i=1;i<=(Integer.valueOf(num)-1);i++){       	
        	String tableyear1 = mytable.getCellAt(i, 0).asText().substring(0, 4);//第i行年份
        	String tableyear2 = mytable.getCellAt(i+1, 0).asText().substring(0, 4);//第i+1行年份
        	String jine = mytable.getCellAt(i, 2).asText();//缴费金额
        	if(tableyear1.equals(tableyear2)){       		
        		month=month+1;
        		if(month==2){  
        			YangLaoBean = new SecurityBean();
        			YangLaoBean.setYear(tableyear1);
        			YangLaoBean.setBase_number(mytable.getCellAt(i, 1).asText());
        			YangLaoBean.setLast_pay_date(mytable.getCellAt(i, 0).asText());
        			YangLaoBean.setCompany_name(company_name);
        			YangLaoBean.setType(type); 
        			YangLaoBean.setMonthly_personal_income(mytable.getCellAt(i, 2).asText());
        		}       		
        	}else {      		
        		month=month+1;
        		YangLaoBean.setMonth_count(String.valueOf(month));
        		/*JSONObject jsonObject = JSONObject.fromObject(YangLaoBean);
    			String jsonBean = jsonObject.toString();*/
    			//System.out.println(jsonBean);
        		yanglaoList.add(YangLaoBean);
        		System.out.println(YangLaoBean);
        		month=1;//年缴纳月份归0
        	}
        	if(i==Integer.valueOf(num)-1){
        		month=month+1;
        		YangLaoBean.setMonth_count(String.valueOf(month));
        		/*JSONObject jsonObject = JSONObject.fromObject(YangLaoBean);
    			String jsonBean = jsonObject.toString();*/
    			//System.out.println(jsonBean);
        		yanglaoList.add(YangLaoBean);
        		System.out.println(YangLaoBean);
        	}
        	endowmentInsuranceAmount=endowmentInsuranceAmount+Double.valueOf(jine);//养老金余额
        	
        }
        yanglaoList.add(df.format(endowmentInsuranceAmount));
        return yanglaoList;
    }
    public List<Object> getYiLiaoList(WebClient webClient) throws Exception{
    	List<Object> yiliaoList=new ArrayList<Object>();
        HtmlPage basicInfos = webClient.getPage("https://rzxt.nbhrss.gov.cn/nbsbk-rzxt/web/pages/query/query-yilbx.jsp");		
        Thread.sleep(2000);
        if(basicInfos.asText().indexOf("服务不可用，请稍候再试")!=-1){//判断用户属于医保城镇居民，还是市级城镇职工
        	basicInfos = basicInfos.getElementById("yilbxLi2").click();
        	Thread.sleep(1000);
        }
        String company_name = basicInfos.getElementById("yilbx").getElementsByTagName("td").get(2).getTextContent().substring(5);//yilbx
        String type = basicInfos.getElementById("yilbx").getElementsByTagName("td").get(4).getTextContent().substring(5);
        if("参保缴费".equals(type)){
        	type="缴存";
        }
        //String niandu = basicInfos.getElementById("ylbx3").getElementsByTagName("td").get(0).getTextContent().substring(3);//最近整年交社保年份
        //String jiaofei = basicInfos.getElementById("ylbx3").getElementsByTagName("td").get(5).getTextContent().substring(12);//截止最近整年交社保的缴费
        int num1 = basicInfos.getElementById("sjzl").getTextContent().indexOf("共 ");//sjzl
        int num2 = basicInfos.getElementById("sjzl").getTextContent().indexOf(" 条");
        String num = basicInfos.getElementById("sjzl").getTextContent().substring(num1+2, num2);//记录条数
        HtmlTable mytable = (HtmlTable) basicInfos.getElementById("mytable");
        SecurityBean YiLiaoBean = null;
        String year="";
        int month=0;
        double medicalInsuranc=0.00;//缴费余额
        for(int i=1;i<=(Integer.valueOf(num)-1);i++){       	
        	String tableyear1 = mytable.getCellAt(i, 0).asText().substring(0, 4);//第i行年份
        	String tableyear2 = mytable.getCellAt(i+1, 0).asText().substring(0, 4);//第i+1行年份
        	String jine = mytable.getCellAt(i, 2).asText();//缴费金额
        	if(tableyear1.equals(tableyear2)){    
        		month=month+1;
        		if(month==2){   
        			YiLiaoBean = new SecurityBean();
        			YiLiaoBean.setYear(tableyear1);
        			YiLiaoBean.setBase_number(mytable.getCellAt(i, 1).asText());
        			YiLiaoBean.setLast_pay_date(mytable.getCellAt(i, 0).asText());
        			YiLiaoBean.setCompany_name(company_name);
        			YiLiaoBean.setType(type);
        			YiLiaoBean.setMonthly_personal_income(mytable.getCellAt(i, 2).asText());
        		}       		
        	}else {
        		month=month+1;
        		YiLiaoBean.setMonth_count(String.valueOf(month));
        		/*JSONObject jsonObject = JSONObject.fromObject(YiLiaoBean);
    			String jsonBean = jsonObject.toString();*/  			
    			yiliaoList.add(YiLiaoBean);
    			System.out.println(YiLiaoBean);
        		month=1;//年缴纳月份归0
        	}
        	if(i==Integer.valueOf(num)-1){
        		month=month+1;
        		YiLiaoBean.setMonth_count(String.valueOf(month));
        		/*JSONObject jsonObject = JSONObject.fromObject(YiLiaoBean);
    			String jsonBean = jsonObject.toString();*/
    			//System.out.println(jsonBean);
    			yiliaoList.add(YiLiaoBean);
    			System.out.println(YiLiaoBean);
        	}
        	medicalInsuranc = medicalInsuranc+Double.valueOf(jine);//医疗余额
        }
        HtmlTable table = (HtmlTable) basicInfos.getElementById("mytable2");
        DomNodeList tr = table.getElementsByTagName("tr");
        double n = 0.00;//医疗花去费用
        for (int i=1;i<tr.size();i++){
        	n = n+Double.valueOf(table.getCellAt(i, 7).asText());
        }
        medicalInsuranc = medicalInsuranc+n;
        yiliaoList.add(df.format(medicalInsuranc));
        return yiliaoList;
    }
 }
