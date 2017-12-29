package com.reptile.service.accumulationfund;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.reptile.model.AccumulationFund;
import com.reptile.model.AccumulationFundInfo;
import com.reptile.model.FormBean;
import com.reptile.util.*;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 陕西社保
 *
 * @author hotWang
 * @date 2016/10/31
 */
@Service("accumulationFundService")
public class AccumulationFundService {
    private  static String detailsUrl="http://query.xazfgjj.gov.cn/gjjcx_gjjmxcx.jsp?urltype=tree.TreeTempUrl&wbtreeid=1177";
    private  static String infoUrl="http://query.xazfgjj.gov.cn/gjjcx_gjjxxcx.jsp?urltype=tree.TreeTempUrl&wbtreeid=1178";
    private  static String loginUrl="http://query.xazfgjj.gov.cn/index.jsp?urltype=tree.TreeTempUrl&wbtreeid=1172";
    private  static String verifyCodeImageUrl="http://query.xazfgjj.gov.cn/system/resource/creategjjcheckimg.jsp?randomid="+System.currentTimeMillis();
    private static CrawlerUtil crawlerutil=new CrawlerUtil();
    private Logger logger= LoggerFactory.getLogger(AccumulationFundService.class);
    public Map<String,Object> login(FormBean bean, HttpServletRequest request,String idCardNum){
        Map<String,Object> map=new HashMap<String,Object>(16);
        Map<String,Object> data=new HashMap<String,Object>(16);
        try {
            if(!bean.verifyParams(bean)){
                map.put("ResultInfo","提交数据有误,请刷新页面后重新输入!");
                map.put("ResultCode","0001");
                map.put("errorInfo","提交数据有误,请刷新页面后重新输入!");
                map.put("errorCode","0001");
                map.put("data",data);
                return map;
            }
            HttpSession session = request.getSession();
            Object sessionWebClient = session.getAttribute("sessionWebClient-GJJ");
            Object sessionLoginPage = session.getAttribute("sessionLoginPage-GJJ");
            if(sessionWebClient!=null && sessionLoginPage!=null){
                final WebClient webClient = (WebClient) sessionWebClient;
                final HtmlPage loginPage = (HtmlPage) sessionLoginPage;

                HtmlForm form = loginPage.getForms().get(0);
                form.getInputByName("csrftoken").setValueAttribute("40507");
                form.getInputByName("wbidcard").setValueAttribute(bean.getUserId().toString());
                form.getInputByName("cxydmc").setValueAttribute("当前年度");
                form.getInputByName("flag").setValueAttribute("login");
                form.getInputByName("wbzhigongname").setValueAttribute(bean.getUserName());
                form.getInputByName("wbrealmima").setValueAttribute(bean.getUserPass());
                form.getInputByName("wbmima").setValueAttribute(bean.getUserPass());
                form.getInputByName("surveyyanzheng").setValueAttribute(bean.getVerifyCode());

                final List collectedAlerts = new ArrayList();
                webClient.setAlertHandler(new CollectingAlertHandler(collectedAlerts));
                HtmlImageInput submit = (HtmlImageInput)loginPage.getByXPath("//input[@type='image']").get(0);
                HtmlPage index=(HtmlPage)submit.click();
                Thread.sleep(1000);
                String str=index.asText();
                System.out.println(index.getTitleText());
                String cardNumber="身份证号码";
                String updatePwd="修改密码";
                if(str.indexOf(cardNumber)!=-1&&collectedAlerts.size()!=0){
                    map.put("ResultInfo",collectedAlerts.get(0));
                    map.put("ResultCode","0001");
                    map.put("errorInfo",collectedAlerts.get(0));
                    map.put("errorCode","0001");
                    map.put("data",data);
                    return map;
                }else if(index.getTitleText()!=null&&index.getTitleText().contains(updatePwd)){
                	map.put("ResultInfo","请先登录官网修改密码");
                    map.put("ResultCode","0001");
                    map.put("errorInfo","请先登录官网修改密码");
                    map.put("errorCode","0001");
                    map.put("data",data);
                    return map;
                }
                PushState.state(idCardNum, "accumulationFund", 100);
                HtmlPage detailsPage = webClient.getPage(detailsUrl);
                HtmlPage infoPage = webClient.getPage(infoUrl);
                HtmlTable infoTable=(HtmlTable)infoPage.getElementsByTagName("form").get(0).getElementsByTagName("table").get(0);
                AccumulationFundInfo info=new AccumulationFundInfo();
                info.setMonthBase(infoTable.getCellAt(3,3).asText());
                info.setUnitAccount(infoTable.getCellAt(4,1).asText());
                info.setUnitName(infoTable.getCellAt(5,1).asText());
                info.setOpeningDate(infoTable.getCellAt(7,1).asText());
                info.setCurrentState(infoTable.getCellAt(7,3).asText());
                info.setLastYearBalance(infoTable.getCellAt(9,1).asText());
                info.setPaidThisYear(infoTable.getCellAt(9,3).asText());
                info.setExternalTransfer(infoTable.getCellAt(10,1).asText());
                info.setExtractionThisYear(infoTable.getCellAt(10,3).asText());
                info.setBalance(infoTable.getCellAt(11,3).asText());
                List<AccumulationFund> detailsList = new ArrayList<AccumulationFund>();
                DomNodeList<HtmlElement> detils=detailsPage.getElementsByTagName("form").get(0).getElementsByTagName("tr");
                for(int i=3;i<detils.size();i++){
                    DomNodeList<HtmlElement> tdList=detils.get(i).getElementsByTagName("td");
                    AccumulationFund temp = new AccumulationFund();
                    for(int j=0;j<tdList.size();j++){
                        String key=detils.get(2).getElementsByTagName("td").get(j).asText();
                        String value=detils.get(i).getElementsByTagName("td").get(j).asText();
                        value="".equals(value)?null:value;
                        if("收入".equals(key)){
                            temp.setIncome(value);
                        }else if("摘要".equals(key)){
                            temp.setDesc(value);
                        }else if("日期".equals(key)){
                            temp.setCreateTime(value);
                        }else if("支出".equals(key)){
                            temp.setExpenditure(value);
                        }
                    }
                    detailsList.add(temp);
                }
                webClient.close();
               
                map.put("ResultInfo","查询成功");
                map.put("ResultCode","0000");
                data.put("info",info);
                data.put("detailsList",detailsList);
                map.put("city",bean.getCityCode());
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                map.put("queryDate",sdf.format(new Date()).toString());
                map.put("userId",idCardNum);
                map.put("userName",bean.getUserName().toString());
                map.put("userPass",bean.getUserPass().toString());
                map.put("data",data);

                Resttemplate resttemplate = new Resttemplate();
                map=resttemplate.SendMessageCredit(JSONObject.fromObject(map), ConstantInterface.port+"/HSDC/person/accumulationFund");
                String flagCode="0000";
                String flagInfo="ResultCode";
                if(map!=null&&flagCode.equals(map.get(flagInfo).toString())){
                	 PushState.state(idCardNum, "accumulationFund", 300);
                    map.put("errorInfo","推送成功");
                    map.put("errorCode","0000");
                }else{
                	 PushState.state(idCardNum, "accumulationFund", 200);
                    map.put("errorInfo","推送失败");
                    map.put("errorCode","0001");
                }
                //ludangwei 2017/08/10
                session.removeAttribute("sessionWebClient-GJJ");
                session.removeAttribute("sessionLoginPage-GJJ");
            }else{
            	 PushState.state(idCardNum, "accumulationFund", 200);
                System.out.print("服务器繁忙，请刷新页面后重试!");
            }
        } catch (Exception e) {
        	 PushState.state(idCardNum, "accumulationFund", 200);
            logger.warn(e.getMessage()+"     mrlu");
            map.clear();
            data.clear();
            map.put("ResultInfo","服务器繁忙，请刷新页面后重试!");
            map.put("ResultCode","0002");
            map.put("errorInfo","服务器繁忙，请刷新页面后重试!");
            map.put("errorCode","0002");
        }
        map.put("data",data);
        
        return map;
    }

    public Map<String,Object> getVerifyImage(HttpServletResponse response, HttpServletRequest request){
        Map<String,Object> data=new HashMap<String,Object>(16);
        Map<String,Object> map=new HashMap<String,Object>(16);
        try {
            HttpSession session = request.getSession();
            String verifyImages=request.getSession().getServletContext().getRealPath("/verifyImages");
            File file = new File(verifyImages+File.separator);
            if(!file.exists()){
                file.mkdir();
            }
            String fileName=System.currentTimeMillis()+".jpg";

            final WebClient webClient = new WebClientFactory().getWebClient();
//            final WebClient webClient = new WebClient(BrowserVersion.CHROME,Scheduler.ip,Scheduler.port);
//            webClient.getOptions().setCssEnabled(false);// 禁用css支持
//            webClient.getOptions().setThrowExceptionOnScriptError(false);// 忽略js异常
//            webClient.getOptions().setTimeout(8000); // 设置连接超时时间
            final HtmlPage loginPage = webClient.getPage(loginUrl);
            HtmlImage verifyCodeImagePage = (HtmlImage)loginPage.getByXPath("//img").get(20);
            BufferedImage bi=verifyCodeImagePage.getImageReader().read(0);
            ImageIO.write(bi, "JPG", new File(verifyImages,fileName));
            session.setAttribute("sessionWebClient-GJJ", webClient);
            session.setAttribute("sessionLoginPage-GJJ", loginPage);

            data.put("imagePath",request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/verifyImages/"+fileName);
            data.put("ResultInfo","查询成功");
            data.put("ResultCode","0000");
            map.put("errorInfo","查询成功");
            map.put("errorCode","0000");
            map.put("ResultInfo","查询成功");
            map.put("ResultCode","0000");
        } catch (Exception e) {
            logger.warn(e.getMessage()+"     mrlu");
            e.printStackTrace();

            System.out.println("更换ip+++++++++++++mrlu");
            data.put("ResultInfo","服务器繁忙，请稍后再试！");
            data.put("ResultCode","0002");
            map.put("errorInfo","服务器繁忙，请稍后再试！");
            map.put("errorCode","0002");
            map.put("ResultInfo","服务器繁忙，请刷新页面后重试!");
            map.put("ResultCode","0002");
        }
        map.put("data",data);
        return map;
    }


}
