package com.reptile.service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.reptile.model.FormBean;
import com.reptile.model.Option;
import com.reptile.model.Question;
import com.reptile.springboot.Scheduler;
import com.reptile.util.ConstantInterface;
import com.reptile.util.HttpUtils;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HotWong on 2017/5/2 0002.
 */
@Service("creditService")
public class CreditService {
    private final static String loginUrl = "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp";
    private final static String ApplyUrl = "https://ipcrs.pbccrc.org.cn/reportAction.do?method=applicationReport";
    private final static String queryUrl = "https://ipcrs.pbccrc.org.cn/reportAction.do?method=queryReport";

    private Logger logger = LoggerFactory.getLogger(CreditService.class);

    public Map<String, Object> getVerifyImage(String type, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        try {
//            Object sessionWebClient = request.getSession().getAttribute("sessionWebClient-ZX");
//            Object sessionLoginPage = request.getSession().getAttribute("sessionLoginPage-ZX");
            String verifyImages = request.getSession().getServletContext().getRealPath("/verifyImages");
            File file = new File(verifyImages + File.separator);
            if (!file.exists()) {
                file.mkdir();
            }
            String fileName = System.currentTimeMillis() + ".jpg";
            final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45, Scheduler.ip, Scheduler.port);
//            final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
            webClient.setJavaScriptTimeout(20000);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setJavaScriptEnabled(true); // 启用JS解释器，默认为true
            webClient.getOptions().setCssEnabled(false);// 禁用css支持
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);// js运行错误时，是否抛出异常
            webClient.getOptions().setTimeout(10000); // 设置连接超时时间，这里是30S。如果为0，则无限期等待
            webClient.getCookieManager().setCookiesEnabled(true);
            webClient.addRequestHeader("Host", "ipcrs.pbccrc.org.cn");
            webClient.addRequestHeader("Referer", "https://ipcrs.pbccrc.org.cn/");
            webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
            HtmlPage loginPage = null;

            loginPage = webClient.getPage(loginUrl);

            if (type != null && type.equals("reg")) {
                HtmlForm userForm = loginPage.getFormByName("userForm");
                HtmlPage regPage = userForm.getInputByValue("新用户注册").click();
                HtmlImage verifyCodeImagePage = (HtmlImage) regPage.getElementById("imgrc");
                BufferedImage bi = verifyCodeImagePage.getImageReader().read(0);
                ImageIO.write(bi, "JPG", new File(file, fileName));
                request.getSession().setAttribute("sessionRegPage", regPage);
            } else {
                HtmlImage verifyCodeImagePage = (HtmlImage) loginPage.getElementById("imgrc");
                BufferedImage bi = verifyCodeImagePage.getImageReader().read(0);
                ImageIO.write(bi, "JPG", new File(file, fileName));
                request.getSession().setAttribute("sessionLoginPage-ZX", loginPage);
            }
            request.getSession().setAttribute("sessionWebClient-ZX", webClient);
            data.put("imageUrl", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/verifyImages/" + fileName);
            data.put("ResultInfo", "查询成功");
            data.put("ResultCode", "0000");
        } catch (Exception e) {
            Scheduler.sendGet(Scheduler.getIp);
            logger.warn(e.getMessage() + "  获取征信验证码   mrlu", e);
            data.put("ResultInfo", "系统繁忙，请稍后再试！");
            data.put("ResultCode", "0002");
            map.put("errorCode","0001");
            map.put("errorInfo","系统繁忙，请稍后再试！");
        }
        map.put("data", data);
        return map;
    }

    public Map<String, Object> sendSms(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        Object sessionWebClient = request.getSession().getAttribute("sessionWebClient-ZX");
        Object sessionApplyPage = request.getSession().getAttribute("sessionApplyPage");
        try {
            if (sessionWebClient != null && sessionApplyPage != null) {
                HtmlPage applyPage = (HtmlPage) sessionApplyPage;
                HtmlButtonInput tradecode = (HtmlButtonInput) applyPage.getElementById("tradecode");
                tradecode.click();
                request.getSession().setAttribute("sessionApplyPage", applyPage);
                data.put("ResultInfo", "发送成功");
                data.put("ResultCode", "0000");
            } else {
                throw new Exception("服务器繁忙，请刷新页面后重试!");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage() + " 征信发送手机验证码    mrlu", e);
            data.put("ResultInfo", "系统繁忙，请稍后再试！");
            data.put("ResultCode", "0002");
        }
        map.put("data", data);
        return map;
    }

    /**
     * 用户注册-发送短信验证码
     *
     * @param request
     * @return Map<String,Object>
     */
    public Map<String, Object> sendRegSms(String phone, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        if (phone == null || phone.equals("")) {
            data.put("ResultInfo", "手机号码错误！");
            data.put("ResultCode", "0001");
            map.put("data", data);
            return map;
        }
        Object sessionWebClient = request.getSession().getAttribute("sessionWebClient-ZX");
        Object sessionRegPage = request.getSession().getAttribute("sessionRegPage");
        try {
            if (sessionWebClient != null && sessionRegPage != null) {
                HtmlPage regPage = (HtmlPage) sessionRegPage;
                HtmlForm userForm = regPage.getFormByName("userForm");
                userForm.getInputByName("userInfoVO.mobileTel").setValueAttribute(phone);
                regPage.getElementById("getCode").click();
                request.getSession().setAttribute("sessionRegPage", regPage);
                data.put("ResultInfo", "发送成功");
                data.put("ResultCode", "0000");
            } else {
                throw new Exception("服务器繁忙，请刷新页面后重试!");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage() + " 征信发送注册短信验证码    mrlu", e);
            data.put("ResultInfo", "系统繁忙，请稍后再试！");
            data.put("ResultCode", "0002");
        }
        map.put("data", data);
        return map;
    }

    /**
     * 征信用户注册补充信息
     *
     * @param bean
     * @param request
     * @return Map<String,Object>
     */
    public Map<String, Object> reg(FormBean bean, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        Object sessionWebClient = request.getSession().getAttribute("sessionWebClient-ZX");
        Object sessionRegPage = request.getSession().getAttribute("sessionRegPage");
        try {
            if (sessionWebClient != null && sessionRegPage != null) {
                HtmlPage regPage = (HtmlPage) sessionRegPage;
                HtmlForm userForm = regPage.getFormByName("userForm");
                userForm.getInputByName("userInfoVO.loginName").setValueAttribute(bean.getUserName());
                userForm.getInputByName("userInfoVO.password").setValueAttribute(bean.getUserPass());
                userForm.getInputByName("userInfoVO.confirmpassword").setValueAttribute(bean.getUserPass());
                userForm.getInputByName("userInfoVO.email").setValueAttribute(bean.getEmail());
                userForm.getInputByName("userInfoVO.mobileTel").setValueAttribute(bean.getPhone());
                userForm.getInputByName("userInfoVO.verifyCode").setValueAttribute(bean.getVerifyCode());
                HtmlPage resultPage = userForm.getInputByValue("提交").click();
                if (resultPage.asText().indexOf("密码") != -1 && resultPage.asText().indexOf("手机号码") != -1) {
                    StringBuilder sb = new StringBuilder();
                    if (resultPage.getElementById("_error_field_") != null) {
                        sb.append(resultPage.getElementById("_error_field_").asText());
                    }
                    sb.append(resultPage.getElementById("loginNameInfo").asText());
                    sb.append(resultPage.getElementById("passwordInfo").asText());
                    sb.append(resultPage.getElementById("cfpasswordInfo").asText());
                    sb.append(resultPage.getElementById("emailInfo").asText());
                    sb.append(resultPage.getElementById("mobileTelInfo").asText());
                    sb.append(resultPage.getElementById("verifyCodeInfo").asText());
                    sb.toString();
                    data.put("ResultInfo", sb.toString());
                    data.put("ResultCode", "0001");
                } else {
                    data.put("ResultInfo", "注册成功");
                    data.put("ResultCode", "0000");
                }

            } else {
                throw new Exception("服务器繁忙，请刷新页面后重试!");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage() + " 征信注册补充信息    mrlu", e);
            data.put("ResultInfo", "系统繁忙，请稍后再试！");
            data.put("ResultCode", "0002");
        }
        map.put("data", data);
        return map;
    }

    /**
     * 征信用户预注册
     *
     * @param bean
     * @param request
     * @return Map<String,Object>
     */
    public Map<String, Object> preReg(FormBean bean, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        Object sessionWebClient = request.getSession().getAttribute("sessionWebClient-ZX");
        Object sessionRegPage = request.getSession().getAttribute("sessionRegPage");
        try {
            if (sessionWebClient != null && sessionRegPage != null) {
                HtmlPage regPage = (HtmlPage) sessionRegPage;
                HtmlForm userForm = regPage.getFormByName("userForm");
                userForm.getInputByName("userInfoVO.name").setValueAttribute(bean.getUserName());
                userForm.getInputByName("userInfoVO.certNo").setValueAttribute(bean.getUserId().toString());
                userForm.getInputByName("_@IMGRC@_").setValueAttribute(bean.getVerifyCode());
                HtmlCheckBoxInput servearticle = (HtmlCheckBoxInput) regPage.getElementById("servearticle");
                servearticle.setChecked(true);
                servearticle.setAttribute("checked", "checked");
                regPage = userForm.getInputByValue("下一步").click();
                String result = regPage.asText();
                if (result.indexOf("密码") != -1 && result.indexOf("手机号码") != -1) {
                    data.put("ResultInfo", "预注册成功，下一步补充信息!");
                    data.put("ResultCode", "0000");
                    request.getSession().setAttribute("sessionRegPage", regPage);
                } else {
                    data.put("ResultInfo", regPage.getElementById("_error_field_").asText());
                    data.put("ResultCode", "0001");
                }
            } else {
                throw new Exception("服务器繁忙，请刷新页面后重试!");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage() + " 征信预注册    mrlu", e);
            data.put("ResultInfo", "验证码错误，请重新输入！");
            data.put("ResultCode", "0002");
        }
        map.put("data", data);
        return map;
    }

    public Map<String, Object> subSms(String sms, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        if (sms == null || sms.length() != 6) {
            data.put("ResultInfo", "验证码错误!");
            data.put("ResultCode", "0001");
            map.put("data", data);
            return map;
        }
        Object sessionWebClient = request.getSession().getAttribute("sessionWebClient-ZX");
        Object sessionApplyPage = request.getSession().getAttribute("sessionApplyPage");
        try {
            if (sessionWebClient != null && sessionApplyPage != null) {
                HtmlPage applyPage = (HtmlPage) sessionApplyPage;
                applyPage.getElementById("verifyCode").setAttribute("value", sms);
                HtmlButtonInput nextstep = (HtmlButtonInput) applyPage.getElementById("nextstep");
                applyPage = nextstep.click();
                if (applyPage.asText().indexOf("获取动态码") != -1) {
                    data.put("ResultInfo", applyPage.getElementById("messages").asText());
                    data.put("ResultCode", "0001");
                } else {
                    if (applyPage.asText().indexOf("已存在") != -1) {
                        HtmlButtonInput jixu = applyPage.querySelector(".regist_btn");
                        applyPage = jixu.click();
                    }
                    data.put("ResultInfo", applyPage.querySelector(".span-grey2").asText());
                    data.put("ResultCode", "0000");
                    map.put("ResultInfo", applyPage.querySelector(".span-grey2").asText());
                    map.put("ResultCode", "0000");
                    applyPage.cleanUp();
                    applyPage.getWebClient().close();
                    request.getSession().setAttribute("sessionQuestionPage", null);
                }
            } else {
                throw new Exception("服务器繁忙，请刷新页面后重试!");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage() + " 提交信息    mrlu", e);
            data.put("ResultInfo", "系统繁忙，请稍后再试！");
            data.put("ResultCode", "0002");
            map.put("ResultInfo", "系统繁忙，请稍后再试！");
            map.put("ResultCode", "0002");
        }
        map.put("data", data);
        return map;
    }

    public Map<String, Object> login(FormBean bean, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            if (!bean.verifyCredit(bean)) {
                data.put("ResultInfo", "提交数据有误,请刷新页面后重新输入!");
                data.put("ResultCode", "0001");
                map.put("data", data);
                return map;
            }
            Object sessionWebClient = request.getSession().getAttribute("sessionWebClient-ZX");
            Object sessionLoginPage = request.getSession().getAttribute("sessionLoginPage-ZX");
            if (sessionWebClient != null && sessionLoginPage != null) {
                final WebClient webClient = (WebClient) sessionWebClient;
                final HtmlPage loginPage = (HtmlPage) sessionLoginPage;
                HtmlForm form = loginPage.getForms().get(0);
                try {
                    form.getInputByName("loginname").setValueAttribute(bean.getUserName());
                } catch (Exception e) {
                    loginPage.cleanUp();
                    request.getSession().setAttribute("sessionWebClient-ZX", null);
                    request.getSession().setAttribute("sessionLoginPage-ZX", null);
                    data.put("ResultInfo", "服务器繁忙或登录超时,请重新登录!");
                    data.put("ResultCode", "0002");
                    map.put("data", data);
                    webClient.close();
                    return map;
                }
                form.getInputByName("password").setValueAttribute(bean.getUserPass());
                form.getInputByName("_@IMGRC@_").setValueAttribute(bean.getVerifyCode());
                HtmlSubmitInput submit = form.getInputByValue("登录");
                HtmlPage login = submit.click();
                String result = login.asText();
                if (result.indexOf("用户登录") != -1) {
                    HtmlElement error = (HtmlElement) login.getByXPath("//div[@class='erro_div3']").get(0);
                    data.put("ResultInfo", error.asText().trim());
                    data.put("ResultCode", "0001");
                    map.put("data", data);
                    webClient.close();
                    return map;
                }

                HtmlPage applyPage = webClient.getPage(ApplyUrl);
                String resultStr = applyPage.asText();
                //ludangwei  2017-08-03
                // if(resultStr.indexOf("已生成")！=-1){
                if (resultStr.contains("加工成功")) {
                    request.getSession().setAttribute("user", bean);
                    data.put("ResultInfo", "信用已生成，请直接查询信用。");
                    data.put("ResultCode", "0003");
                    map.put("data", data);
                    return map;
                }
                if(resultStr.contains("处理中")){
                    HtmlPage page = webClient.getPage(queryUrl);
                    NamedNodeMap nextstep = page.getElementById("nextstep").getAttributes();
                    Node disabled = nextstep.getNamedItem("disabled");
                    if(disabled==null){
                        request.getSession().setAttribute("user", bean);
                        data.put("ResultInfo", "信用已生成，请直接查询信用。");
                        data.put("ResultCode", "0003");
                        map.put("data", data);
                        return map;
                    }
                }
                List<HtmlCheckBoxInput> list = applyPage.getByXPath("//input[@type='checkbox']");
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setChecked(true);
                    list.get(i).setAttribute("checked", "checked");
                }
                HtmlButtonInput nextstep = (HtmlButtonInput) applyPage.getElementById("nextstep");
                if (nextstep.getAttribute("disabled") != null && nextstep.getAttribute("disabled").equals("disabled")) {
                    if (resultStr.indexOf("手机动态码") != -1 && resultStr.indexOf("处理中") != -1) {
                        data.put("ResultInfo", "您的信用信息查询申请已提交，请在24小时后访问平台获取结果!");
                    } else {
                        data.put("ResultInfo", "您的信用信息查询申请已提交，正在处理中，验证结果会在24小时内发送到您手机！");
                    }
                    data.put("ResultCode", "0004");
                    map.put("data", data);
                    webClient.close();
                    return map;
                }
                if (resultStr.indexOf("手机动态码") != -1) {
                    request.getSession().setAttribute("sessionApplyPage", applyPage);
                    data.put("phone", applyPage.querySelector(".user_text").asText());
                    data.put("type", "phone");
                } else {
                    DomElement radiobutton3 = applyPage.getElementById("radiobutton3");
                    if (radiobutton3 == null) {
                        data.put("ResultInfo", "对不起，系统验证失败，请在官网进行相关验证后再来认证征信报告。");
                        data.put("ResultCode", "0006");
                        map.put("data", data);
                        webClient.close();
                        return map;
                    }
                    applyPage.getElementById("radiobutton3").click();
                    applyPage = nextstep.click();
                    if (applyPage.asText().indexOf("已存在") != -1) {
                        HtmlButtonInput jixu = applyPage.querySelector(".regist_btn");
                        applyPage = jixu.click();
                    }
                    List<HtmlListItem> items = applyPage.getByXPath("//li");
                    List<Question> questions = new ArrayList<Question>();
                    for (int i = 0; i < items.size() - 1; i++) {
                        List<HtmlElement> spans = items.get(i).getElementsByTagName("span");
                        Question question = new Question();
                        question.setQuestion("问题" + spans.get(0).asText().trim());
                        List<Option> options = new ArrayList<Option>();
                        for (int j = 1; j < spans.size(); j++) {
                            Option option = new Option();
                            option.setTitle(spans.get(j).asText().trim());
                            option.setName("option" + (i + 1));
                            option.setValue(j + "");
                            options.add(option);
                        }
                        question.setOptions(options);
                        questions.add(question);
                    }
                    request.getSession().setAttribute("sessionQuestionPage", applyPage);
                    loginPage.cleanUp();
                    request.getSession().setAttribute("sessionLoginPage-ZX", null);
                    data.put("questions", questions);
                    request.getSession().setAttribute("questions", questions);
                    data.put("type", "ask");
                }
                data.put("ResultInfo", "查询成功");
                data.put("ResultCode", "0000");
            } else {
                throw new Exception("服务器繁忙，请刷新页面后重试!");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage() + "  登录征信   mrlu", e);
            data.put("ResultInfo", "系统繁忙，请稍后再试！");
            data.put("ResultCode", "0002");
        }
        map.put("data", data);
        return map;
    }

    public Map<String, Object> question(String options, String userId, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            if (options == null) {
                throw new Exception("参数为空!");
            }
            Integer[] args = (Integer[]) JSONArray.toArray(JSONArray.fromObject(options), Integer.class);
            Object sessionQuestionPage = request.getSession().getAttribute("sessionQuestionPage");
            if (sessionQuestionPage != null) {
                HtmlPage questionPage = (HtmlPage) sessionQuestionPage;
                HtmlPage applyPage = questionPage.getWebClient().getPage(ApplyUrl);
                List<HtmlCheckBoxInput> list = applyPage.getByXPath("//input[@type='checkbox']");
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setChecked(true);
                    list.get(i).setAttribute("checked", "checked");
                }
                applyPage = applyPage.getElementById("radiobutton3").click();
                HtmlButtonInput nextstep = (HtmlButtonInput) applyPage.getElementById("nextstep");
                if (nextstep.getAttribute("disabled") != null && nextstep.getAttribute("disabled").equals("disabled")) {
                    data.put("ResultInfo", "您已提交申请，验证结果会在24小时内发送到您手机！");
                    data.put("ResultCode", "0000");
                    map.put("data", data);
                    return map;
                }
                applyPage = nextstep.click();
                for (int i = 0; i < args.length; i++) {
                    HtmlListItem item = (HtmlListItem) applyPage.getByXPath("//li").get(i);
                    HtmlRadioButtonInput radio = (HtmlRadioButtonInput) item.getElementsByTagName("input").get(args[i] - 1);
                    radio.click();
                }
                HtmlPage result = applyPage.getElementById("id_next").click();
                DomNodeList<DomElement> plist = result.getElementsByTagName("p");
                StringBuilder sb = new StringBuilder();
                for (DomElement dom : plist) {
                    sb.append(dom.asText().trim()).append("\n");
                }
                data.put("ResultInfo", sb.toString());
                data.put("ResultCode", "0000");
                try {
                    Map<String, Object> resMap = new HashedMap();
                    Map<String, Object> resData = new HashedMap();
                    if (request.getSession().getAttribute("questions") != null) {
                        List<Question> questions = (List<Question>) request.getSession().getAttribute("questions");
                        resData.put("questions", questions);
                        resData.put("options", options);
                        resMap.put("ResultInfo", "操作成功");
                        resMap.put("ResultCode", "0000");
                        resMap.put("userId", userId);
                        resMap.put("data", resData);
                        HttpUtils.sendPost(ConstantInterface.port + "/HSDC/person/creditInvestigationQuestion", JSONObject.fromObject(resMap).toString());

                        //ludangwei 2017-08-11
//                        map= resttemplate.SendMessageCredit(JSONObject.fromObject(resMap), "http://192.168.3.16:8089/HSDC/person/creditInvestigationQuestion");
                    }
                } catch (Exception e) {
                    logger.warn(e.getMessage() + " 获取征信问题    mrlu", e);
                }
                applyPage.cleanUp();
                questionPage.cleanUp();
                questionPage.getWebClient().close();
                request.getSession().setAttribute("sessionQuestionPage", null);
            } else {
                throw new Exception("您已超时，请重新登录!");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage() + " 征信    mrlu", e);
            data.put("ResultInfo", "系统繁忙，请稍后再试！");
            data.put("ResultCode", "0002");
        }
        map.put("data", data);
        return map;
    }

    //查询信用报告
    public Map<String, Object> queryCredit(HttpServletRequest request, String userId, String verifyCode, String UUID) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        PushSocket.pushnew(map, UUID, "1000","登录中");
        PushState.state(userId, "creditInvestigation",100);
        try {
        	Thread.sleep(2000);
            Object sessionWebClient = request.getSession().getAttribute("sessionWebClient-ZX");
            if (sessionWebClient != null) {
                final WebClient webClient = (WebClient) sessionWebClient;
                HtmlPage queryPage = webClient.getPage(queryUrl);
                Thread.sleep(3000);
                PushSocket.pushnew(map, UUID, "2000","登录成功");
                //判断3个选项中个人信用报告是否可选
                NamedNodeMap radiobutton1 = queryPage.getElementById("radiobutton1").getAttributes();
                Node aClass = radiobutton1.getNamedItem("disabled");
                if (aClass != null) {
                    webClient.close();
                    map.put("ResultInfo", "信用报告未生成！");
                    map.put("ResultCode", "0001");
                    map.put("errorInfo", "信用报告未生成！");
                    PushState.state(userId, "creditInvestigation",200);
                    PushSocket.pushnew(map, UUID, "3000","信用报告未生成！");
                    map.put("errorCode", "0001");
                    return map;
                }
                //end mrlu 2017-09-6


                queryPage.getElementById("tradeCode").setAttribute("value", verifyCode);
                queryPage.getElementById("radiobutton1").click();
                HtmlPage resultPage = queryPage.getElementById("nextstep").click();
                if (resultPage.asText().indexOf("身份验证码") != -1) {
                    map.put("ResultInfo", resultPage.getElementById("codeinfo").asText());
                    map.put("ResultCode", "0001");
                    map.put("errorInfo", resultPage.getElementById("codeinfo").asText());
                    map.put("errorCode", "0001");
                    PushState.state(userId, "creditInvestigation",200);
                    PushSocket.pushnew(map, UUID, "3000",resultPage.getElementById("codeinfo").asText());
                } else {
                    //推送长连接状态
                    //PushSocket.push(map, UUID, "0000");
                	 Thread.sleep(2000);
                	 PushSocket.pushnew(map, UUID, "5000","获取数据中");
                    HtmlTable table = (HtmlTable) resultPage.getElementsByTagName("table").get(0).getElementsByTagName("table").get(1);
                    HtmlTable tableTime = (HtmlTable) resultPage.getElementsByTagName("table").get(0).getElementsByTagName("table").get(0);
                    String realName = table.getCellAt(0, 0).asText();
                    String userIdA = table.getCellAt(0, 2).asText();
                    String queryTime = tableTime.getCellAt(1, 2).asText();
                    map.put("ResultInfo", "查询成功");
                    map.put("ResultCode", "0000");
                    map.put("userId", userId);
                    data.put("verifyCode", verifyCode);
                    data.put("userIdA", userIdA.substring(userIdA.indexOf("：") + 1).trim());
                    data.put("realName", realName.substring(realName.indexOf("：") + 1).trim());
                    data.put("queryTime", queryTime.substring(queryTime.indexOf("：") + 1));
                    if (request.getSession().getAttribute("user") != null) {
                        FormBean user = (FormBean) request.getSession().getAttribute("user");
                        data.put("userName", user.getUserName());
                        data.put("userPass", user.getUserPass());
                    }
                    data.put("reportHtml", resultPage.asXml());
                    try {
                        map.put("data", data);
                        PushSocket.pushnew(map, UUID, "6000","获取数据成功");
                        //ludangwei 2017-08-03
                        // HttpUtils.sendPost("http://192.168.3.16:8089/HSDC/person/creditInvestigation", JSONObject.fromObject(map).toString());
                        Resttemplate temp = new Resttemplate();
                        map = temp.SendMessageCredit(JSONObject.fromObject(map), ConstantInterface.port + "/HSDC/person/creditInvestigation");

                        if (map != null && "0000".equals(map.get("ResultCode").toString())) {
                            map.put("errorInfo", "查询成功");
                            map.put("errorCode", "0000");
                            PushSocket.pushnew(map, UUID, "8000","认证成功");
                            PushState.state(userId, "creditInvestigation",300);

                        } else {
                            map.put("errorInfo", map.get("ResultInfo"));
                            map.put("errorCode", "0001");
                            PushSocket.pushnew(map, UUID, "9000",map.get("ResultInfo").toString());
                            PushState.state(userId, "creditInvestigation",200);
                        }
                        webClient.close();
                    } catch (Exception e) {
                        logger.warn(e.getMessage() + " 查询征信报告推送异常    mrlu", e);
                        map.put("ResultInfo", "系统繁忙，请稍后再试！");
                        map.put("ResultCode", "0002");
                        map.put("errorInfo", "系统繁忙，请稍后再试！");
                        map.put("errorCode", "0002");
                        PushState.state(userId, "creditInvestigation",200);
                        PushSocket.pushnew(map, UUID, "9000","系统繁忙，请稍后再试！");
                        webClient.close();
                    }
                }
            } else {
                map.put("ResultInfo", "您已超时,请重新登录查询!");
                map.put("ResultCode", "0002");
                map.put("errorInfo", "您已超时,请重新登录查询!");
                map.put("errorCode", "0002");
                PushState.state(userId, "creditInvestigation",200);
                PushSocket.pushnew(map, UUID, "3000","您已超时,请重新登录查询!");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage() + " 查询征信报告    mrlu", e);
            map.put("ResultInfo", "系统繁忙，请稍后再试！");
            map.put("ResultCode", "0002");
            map.put("errorInfo", "系统繁忙，请稍后再试！");
            map.put("errorCode", "0002");
            PushState.state(userId, "creditInvestigation",200);
            PushSocket.pushnew(map, UUID, "3000","系统繁忙，请稍后再试！");
        }
        data.put("reportHtml", "");
        map.put("data", data);
        return map;
    }
}
