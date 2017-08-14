package com.reptile.service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.reptile.model.FormBean;
import com.reptile.model.PersonAccount;
import com.reptile.model.PersonInfo;
import com.reptile.util.Resttemplate;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpUtils;

/**
 * Created by HotWong on 2017/5/2 0002.
 */
@Service("socialSecurityService")
public class SocialSecurityService {
    private final static String loginUrl="http://117.36.52.39/sxlssLogin.jsp";
    private final static String infoUrl="http://117.36.52.39/personInfoQuery.do";
    private final static String detailsUrl="http://117.36.52.39/personAccountQuery.do";

    public Map<String,Object> login(FormBean bean){
        Map<String,Object> map=new HashMap<String,Object>();
        Map<String,Object> data=new HashMap<String,Object>();
        try {
            if(bean.getUserName()==null || bean.getUserName().equals("")){
                throw new NullPointerException("请输入姓名!");
            }
            if(bean.getUserId()==null){
                throw new NullPointerException("请输入身份证号!");
            }
            final WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setCssEnabled(false);// 禁用css支持
            webClient.getOptions().setThrowExceptionOnScriptError(false);// 忽略js异常
            webClient.getOptions().setTimeout(8000); // 设置连接超时时间
            final HtmlPage loginPage = webClient.getPage(loginUrl);
            HtmlForm form = loginPage.getForms().get(0);
            HtmlTextInput userId = form.getInputByName("uname");
            HtmlTextInput userName = form.getInputByName("aac003");
            HtmlTextInput verifyCode = (HtmlTextInput) loginPage.getElementById("PSINPUT");
            HtmlTextInput checkCode = (HtmlTextInput) loginPage.getElementById("checkCode");
            HtmlImageInput submit = form.getInputByName("Icon2");
            userId.setValueAttribute(bean.getUserId().toString().trim());
            userName.setValueAttribute(bean.getUserName().trim());
            verifyCode.setValueAttribute(checkCode.getValueAttribute());
            HtmlPage resultPage=(HtmlPage)submit.click();
            String result=resultPage.asText();
            if(result.indexOf("公民身份证号码")!=-1){
                throw new NullPointerException("身份证号码或姓名不正确，请重新输入!");
            }
            HtmlPage infoPage = webClient.getPage(infoUrl);
            HtmlTable infoTable=(HtmlTable)infoPage.getElementsByTagName("table").get(0);
            PersonInfo person=new PersonInfo();
            person.setUserName(infoTable.getCellAt(1, 1).asText().trim());
            person.setPersonStatus(infoTable.getCellAt(1, 3).asText().trim());
            person.setPayStatus(infoTable.getCellAt(1, 5).asText().trim());
            person.setCreateTime(infoTable.getCellAt(2, 1).asText().trim());
            person.setAmount(infoTable.getCellAt(2, 3).asText().trim());
            person.setAgencyName(infoTable.getCellAt(3, 1).asText().trim());
            HtmlPage detailsPage = webClient.getPage(detailsUrl);
            List<PersonAccount> accountList=new ArrayList<PersonAccount>();
            HtmlTable accountTable=(HtmlTable)detailsPage.getElementsByTagName("table").get(0);
            List<HtmlTableRow> accountRows=accountTable.getRows();
            for (int i = 2; i < accountRows.size(); i++) {
                PersonAccount pa=new PersonAccount();
                pa.setYear(accountTable.getCellAt(i, 0).asText().trim());
                pa.setPayMonthNumber(accountTable.getCellAt(i, 1).asText().trim());
                pa.setPayBaseNumber(accountTable.getCellAt(i, 2).asText().trim());
                pa.setPersonPayAmount(accountTable.getCellAt(i, 3).asText().trim());
                pa.setCompanyPayAmount(accountTable.getCellAt(i, 4).asText().trim());
                pa.setLastYearPayMonthNumber(accountTable.getCellAt(i, 5).asText().trim());
                accountList.add(pa);
            }
            person.setAccountList(accountList);
            webClient.close();
            data.put("person",person);
            map.put("ResultInfo","查询成功");
            map.put("ResultCode","0000");
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            map.put("userId",bean.getUserId().toString());
            map.put("queryDate",sdf.format(new Date()).toString());
            map.put("data",data);
//            HttpUtils.sendPost("http://192.168.3.16:8089/HSDC/person/socialSecurity", JSONObject.fromObject(map).toString());
            //ludangwei 2017-08-11
            Resttemplate resttemplate = new Resttemplate();
            map = resttemplate.SendMessageCredit(JSONObject.fromObject(map), "http://192.168.3.4:8081/HSDC/person/socialSecurity");
        }catch (NullPointerException e) {
            map.put("ResultInfo","服务器繁忙，请稍后再试！");
            map.put("ResultCode","0001");
        } catch (Exception e) {
            map.clear();
            data.clear();
            map.put("ResultInfo","服务器繁忙，请稍后再试！");
            map.put("ResultCode","0002");
        }
        map.put("data",data);
        return map;
    }


}
