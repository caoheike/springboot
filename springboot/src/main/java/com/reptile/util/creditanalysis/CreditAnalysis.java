package com.reptile.util.creditanalysis;

import com.reptile.util.CustomException;
import com.reptile.util.Resttemplate;
import com.reptile.util.SimpleHttpClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 征信数据解析
 *
 * @author mrlu
 * @date 2018/1/3
 */
public class CreditAnalysis {
    private static Logger logger = LoggerFactory.getLogger(CreditConstant.class);

    public static void main(String[] args) throws MalformedURLException {
        Map<String,Object> dataMap=new HashMap<>();
        analysisCredit(dataMap);
    }

    public static Map<String, Object> analysisCredit(Map<String, Object> dataMap){

        //解析结果信息
        Map<String, Object> resultDataMap = new HashMap<>();
//        logger.warn("开始解析征信数据...");
//        Object userId = dataMap.get("userId");
//        if (userId == null || userId.toString().length() == 0) {
//            logger.warn("征信解析时身份证不明确...");
//            return CreditConstant.setErrorFailMap("0002", "身份证不明确");
//        }
//        Map<String, Object> data = (Map<String, Object>) dataMap.get("data");
//        String reportHtml = data.get("reportHtml").toString();
        Elements table = null;
        Document parse = null;
        String userId = "610403199112021515";
        try {
            parse = Jsoup.parse(new File("f://zhufang.htm"), "utf-8");
//            parse = Jsoup.parse(reportHtml);
            table = parse.getElementsByTag("table");
            logger.warn(userId.toString() + "此次解析征信页面含有的table数量为:" + table.size());
        } catch (Exception e) {
            logger.warn("征信数据解析失败，页面无法转换为正常html页面...");
            return CreditConstant.setErrorFailMap("0003", "征信页面数据格式异常");
        }
        //推送数据包;
        JSONObject resultData = new JSONObject();
        //数据集合
        JSONObject resultObj = new JSONObject();
        //征信报告基本信息
        JSONObject creditBasic = new JSONObject();
        //征信报告概要
        JSONObject creditSummary = new JSONObject();
        //征信报告信用卡信息
        JSONArray creditCard = new JSONArray();
        //征信报告贷款信息(住房贷款信息，其他信息)
        JSONArray creditLoan = new JSONArray();
        //征信报告担保信息
        JSONArray creditGuarantee = new JSONArray();
        //机构查询信息
        JSONArray orgQueryData = new JSONArray();
        //机构查询信息
        JSONArray personQueryData = new JSONArray();
        try {
            //读取页面中第二个表格中关于征信报告的基本信息
            Element tbBasic1 = table.get(1);
            Elements trBasic1 = tbBasic1.getElementsByTag("tr");
            String flagReport = "个人信用报告";
            if (flagReport.equals(trBasic1.get(0).text())) {
                Elements tdBasic1 = trBasic1.get(1).getElementsByTag("td");
                //报告编号
                creditBasic.put("order_id", tdBasic1.get(0).text().substring(5));
                //查询时间
                creditBasic.put("selectTime", tdBasic1.get(1).text().substring(5));
                creditSummary.put("query_date", tdBasic1.get(1).text().substring(5));
                //报告时间
                creditBasic.put("reportTime", tdBasic1.get(2).text().substring(5));
            } else {
                logger.warn("页面第2个表格非个人信用报告...");
                return CreditConstant.setErrorFailMap("0004", "个人信用报告获取失败");
            }
            //获取征信报告姓名、证件类型、证件号码
            Element tbBasic2 = table.get(2);
            Elements trBasic2 = tbBasic2.getElementsByTag("tr");
            String nameFlag = "姓名：";
            Elements tdBasic2 = trBasic2.get(0).getElementsByTag("td");
            if (tdBasic2.get(0).text().contains(nameFlag)) {
                //报告姓名
                creditBasic.put("name", tdBasic2.get(0).text().substring(3));
                //证件类型
                creditBasic.put("type", tdBasic2.get(1).text().substring(5));
                //证件号码
                creditBasic.put("idnumber", tdBasic2.get(2).text().substring(5));
                //证件空白
                creditBasic.put("kongbai", "");
            } else {
                logger.warn("页面第3个表格非个人信用报告...");
                return CreditConstant.setErrorFailMap("0004", "个人信用报告获取失败");
            }
            resultObj.put("credit_basic", creditBasic);
        } catch (Exception e) {
            logger.warn("获取个人信用报告失败", e);
            return CreditConstant.setErrorFailMap("0005", "获取个人信用报告失败，请联系业务人员");
        }

        try {
            //获取征信报告概要
            Element summaryTable = table.get(7);
            Elements trSummary = summaryTable.getElementsByTag("tr");
            String tdTitle = trSummary.get(0).text();
            String keyWord1 = "信用卡";
            String keyWord2 = "购房贷款";
            String keyWord3 = "其他贷款";
            if (tdTitle.contains(keyWord1) && tdTitle.contains(keyWord2) && tdTitle.contains(keyWord3)) {
                //账户数信息
                Elements tdSummary1 = trSummary.get(1).getElementsByTag("td");
                //信用卡账户数
                creditSummary.put("card_account", tdSummary1.get(1).text());
                //购房贷款账户数
                creditSummary.put("housing_loan_account", tdSummary1.get(2).text());
                //其他贷款账户数
                creditSummary.put("other_loan_account", tdSummary1.get(3).text());

                //未结清/未销户账户数
                Elements tdSummary2 = trSummary.get(2).getElementsByTag("td");
                //信用卡未结清/未销户账户数
                creditSummary.put("card_notsettled", tdSummary2.get(1).text());
                //购房贷款未结清/未销户账户数
                creditSummary.put("housing_loan_notsettled", tdSummary2.get(2).text());
                //其他贷款未结清/未销户账户数
                creditSummary.put("other_loan_notsettled", tdSummary2.get(3).text());

                //发生过逾期的账户数
                Elements tdSummary3 = trSummary.get(3).getElementsByTag("td");
                //信用卡发生过逾期的账户数
                creditSummary.put("card_overdue", tdSummary3.get(1).text());
                //购房贷款发生过逾期的账户数
                creditSummary.put("housing_loan_overdue", tdSummary3.get(2).text());
                //其他贷款发生过逾期的账户数
                creditSummary.put("other_loan_overdue", tdSummary3.get(3).text());

                // 发生过90天以上逾期的账户数
                Elements tdSummary4 = trSummary.get(4).getElementsByTag("td");
                //信用卡发生过90天以上逾期的账户数
                creditSummary.put("card_90overdue", tdSummary4.get(1).text());
                //购房贷款发生过90天以上逾期的账户数
                creditSummary.put("housing_loan_90overdue", tdSummary4.get(2).text());
                //其他贷款发生过90天以上逾期的账户数
                creditSummary.put("other_loan_90overdue", tdSummary4.get(3).text());

                // 为他人担保笔数
                Elements tdSummary5 = trSummary.get(5).getElementsByTag("td");
                //信用卡为他人担保笔数
                creditSummary.put("card_guaranty", tdSummary5.get(1).text());
                //购房贷款为他人担保笔数
                creditSummary.put("housing_loan_guaranty", tdSummary5.get(2).text());
                //其他贷款为他人担保笔数
                creditSummary.put("other_loan_guaranty", tdSummary5.get(3).text());
            } else {
                logger.warn("没有获取到正确的征信报告概要");
                return CreditConstant.setErrorFailMap("0006", "征信报告概要获取失败");
            }
            resultObj.put("credit_summary", creditSummary);
        } catch (Exception e) {
            logger.warn("征信概要解析失败", e);
            return CreditConstant.setErrorFailMap("0007", "征信概要解析失败");
        }

        try {
            Elements ol = parse.getElementsByTag("span");
            for (Element el : ol) {
                //获取信用卡明细
                if ("信用卡".equals(el.text())) {
                    List<String> cardList = new ArrayList<>();
                    Element element = el.nextElementSibling();
                    Elements li = element.getElementsByTag("li");
                    for (Element item : li) {
                        cardList.add(item.text());
                    }
                    creditCard = getCreditCardList(cardList);
                }
                //获取其他贷款明细
                //获取购房贷款明细
                if ("其他贷款".equals(el.text()) || "购房贷款".equals(el.text())) {
                    List<String> otherLoan = new ArrayList<>();
                    Element element = el.nextElementSibling();
                    Elements li = element.getElementsByTag("li");
                    for (Element item : li) {
                        otherLoan.add(item.text());
                    }
                    creditLoan = getOtherLoanList(otherLoan, el.text(), creditLoan);
                }

                //为他人担保信息
                if ("为他人担保信息".equals(el.text())) {
                    List<String> otherGuarantee = new ArrayList<>();
                    Element element = el.nextElementSibling();
                    Elements li = element.getElementsByTag("li");
                    for (Element item : li) {
                        otherGuarantee.add(item.text());
                    }
                    creditGuarantee = getCreditGuarantee(otherGuarantee);
                }
            }
        } catch (CustomException e) {
            logger.warn(e.getExceptionInfo(), e.getException());
            return CreditConstant.setErrorFailMap("0008", e.exceptionInfo);
        } catch (Exception e) {
            logger.warn("数据明细获取失败", e);
            return CreditConstant.setErrorFailMap("0009", "数据明细解析失败");
        }
        resultObj.put("credit_card", creditCard);
        resultObj.put("credit_loan", creditLoan);
        resultObj.put("credit_guarantee", creditGuarantee);
        //公共记录
        JSONArray publicData = new JSONArray();
        JSONObject onePbData = new JSONObject();
        onePbData.put("public_record", "公共记录");
        publicData.add(onePbData);
        resultObj.put("credit_ggjl", publicData);
        resultData.put("data", resultObj);

        try {
            //机构查询明细
            Element orgElement = table.get(10);
            orgQueryData = analysisOrgQuery(orgElement, "org");
            //个人查询明细
            Element personElement = table.get(11);
            personQueryData = analysisOrgQuery(personElement, "per");
        } catch (CustomException e) {
            logger.warn(e.getExceptionInfo(), e.getException());
            return CreditConstant.setErrorFailMap("0010", e.exceptionInfo);
        } catch (Exception e) {
            logger.warn("数据明细获取失败", e);
            return CreditConstant.setErrorFailMap("0011", "查询明细信息解析失败");
        }
        resultObj.put("credit_chaxun1", orgQueryData);
        resultObj.put("credit_chaxun2", personQueryData);
        resultData.put("idcode", userId.toString());
        System.out.println(resultData);
        try {
            Map<String,Object> postData=new HashMap<>();
            postData.put("data",resultObj.toString());
            String post = SimpleHttpClient.post("http://192.168.3.16:8099/HSDC/person/creditInvestigation", postData, null);
            JSONObject jsonObject = JSONObject.fromObject(post);

            if(jsonObject.get("errorCode").equals("0000")){
                resultDataMap.put("errorCode","0000");
                resultDataMap.put("errorInfo","查询成功");
            }else{
                resultDataMap.put("errorCode",jsonObject.get("errorCode"));//异常处理
                resultDataMap.put("errorInfo",jsonObject.get("errorInfo"));
            }
            return resultDataMap;
        } catch (Exception e) {
            logger.warn("征信推送数据过程中出现异常");
            return CreditConstant.setErrorFailMap("0012", "征信推送数据过程中出现异常");
        }
    }

    /**
     * 解析信用卡账单列表
     *
     * @param cardList
     * @return
     */
    public static JSONArray getCreditCardList(List<String> cardList) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (String cardRecord : cardList) {
                if (cardRecord != null && !cardRecord.isEmpty()) {
                    JSONObject cardJson = new JSONObject();
                    cardJson.put("overdue", "0");
                    cardJson.put("five_years_overdue", "0");
                    cardJson.put("ninety_days_overdue", "0");
                    cardJson.put("overdue_money", "0");
                    //卡类型
                    String purpose = cardRecord.substring(cardRecord.indexOf("发放的") + 3, cardRecord.indexOf("（"));
                    cardJson.put("account_category", purpose);

                    if ("贷记卡".equals(purpose)) {
                        //判断是否有逾期
                        if (cardRecord.contains("逾期状态")) {
                            cardJson.put("overdue", "1");
                            //读取五年内逾期月数
                            if (cardRecord.contains("年内有") && cardRecord.contains("个月处于逾期状态")) {
                                String counts = cardRecord.substring(cardRecord.indexOf("年内有") + 3, cardRecord.indexOf("个月处于逾期状态"));
                                cardJson.put("five_years_overdue", counts);
                            }
                            //逾期超过90天次数
                            if (cardRecord.contains("逾期超过90天")) {
                                String yuQi90Count = cardRecord.substring(cardRecord.indexOf("其中") + 2, cardRecord.indexOf("个月逾期"));
                                cardJson.put("ninety_days_overdue", yuQi90Count);
                            }
                            //当前逾期金额
                            if (cardRecord.contains("逾期金额")) {
                                String dueMoney = cardRecord.substring(cardRecord.indexOf("逾期金额") + 4);
                                dueMoney = dueMoney.substring(0, dueMoney.indexOf("。")).replace(",", "").trim();
                                cardJson.put("overdue_money", dueMoney);
                            }
                        }
                    } else if ("准贷记卡".equals(purpose)) {
                        //判断是否有透支超过60天记录（逾期）
                        if (cardRecord.contains("透支超过60天")) {
                            cardJson.put("overdue", "1");
                            //读取五年内逾期月数
                            if (cardRecord.contains("年内有") && cardRecord.contains("个月透支超过60天")) {
                                String counts = cardRecord.substring(cardRecord.indexOf("年内有") + 3, cardRecord.indexOf("个月透支超过60天"));
                                cardJson.put("five_years_overdue", counts);
                            }
                            //逾期超过90天次数
                            if (cardRecord.contains("透支超过90天")) {
                                String yuQi90Count = cardRecord.substring(cardRecord.indexOf("其中") + 2, cardRecord.indexOf("个月透支超过90天"));
                                cardJson.put("ninety_days_overdue", yuQi90Count);
                            }
                        }
                    } else {
                        //系统暂无匹配类型的信用卡
                    }

                    // 正常、逾期、呆账、未激活、销户
                    //帐户状态：未激活,销户, 正常，呆账
                    if (cardRecord.indexOf("未激活") > 0) {
                        cardJson.put("account_state", "未激活");
                    } else if (cardRecord.indexOf("销户") > 0) {
                        cardJson.put("account_state", "销户");
                    } else if (cardRecord.indexOf("呆账") > 0) {
                        cardJson.put("account_state", "呆账");
                    } else if (cardRecord.indexOf("逾期金额") > 0) {
                        cardJson.put("account_state", "逾期");
                    } else {
                        cardJson.put("account_state", "正常");
                    }

                    //发卡日期
                    String cardTime = cardRecord.substring(0, cardRecord.indexOf("日") + 1);
                    cardJson.put("grant_date", cardTime);
                    //发卡行
                    String bankName = cardRecord.substring(cardRecord.indexOf("日") + 1, cardRecord.indexOf("发放的"));
                    cardJson.put("bank", bankName);
                    //账户类型
                    String accountType = cardRecord.substring(cardRecord.indexOf("（") + 1, cardRecord.indexOf("）"));
                    cardJson.put("account_type", accountType);
                    //截止日期
                    String expireDateStr = cardRecord.substring(cardRecord.indexOf("截至") + 2);
                    expireDateStr = expireDateStr.substring(0, expireDateStr.indexOf("月") + 1);
                    cardJson.put("query_date", expireDateStr);
                    //信用额度
                    String creditLine = "0";
                    if (cardRecord.contains("信用额度")) {
                        if (cardRecord.contains("折合人民币")) {
                            creditLine = cardRecord.substring(cardRecord.indexOf("折合人民币") + 5);
                        } else {
                            creditLine = cardRecord.substring(cardRecord.indexOf("信用额度") + 4);
                        }
                        creditLine = creditLine.substring(0, creditLine.indexOf("，")).replace(",", "").trim();
                        cardJson.put("credit_Line", creditLine);
                    } else {
                        cardJson.put("credit_Line", creditLine);
                    }

                    //已使用额度
                    if (cardRecord.contains("已使用额度") || cardRecord.contains("透支余额")) {
                        String usedMoney = null;
                        if (cardRecord.contains("已使用额度")) {
                            usedMoney = cardRecord.substring(cardRecord.indexOf("已使用额度") + 5);
                            if (usedMoney.contains("逾期金额")) {
                                usedMoney = usedMoney.substring(0, usedMoney.indexOf(",")).replace(",", "").trim();
                            } else {
                                usedMoney = usedMoney.substring(0, usedMoney.indexOf("。")).replace(",", "").trim();
                            }
                        } else if (cardRecord.contains("透支余额")) {
                            usedMoney = cardRecord.substring(cardRecord.indexOf("透支余额") + 4);
                            usedMoney = usedMoney.substring(0, usedMoney.indexOf("。")).replace(",", "").trim();
                            if ("0".equals(creditLine)) {
                                int i = Integer.parseInt(creditLine);
                                int j = Integer.parseInt(usedMoney);
                                if (j - i > 0) {
                                    cardJson.put("account_state", "逾期");
                                    cardJson.put("overdue_money", j - i);
                                }
                            }
                        }
                        cardJson.put("used_line", usedMoney);
                    } else {
                        cardJson.put("used_line", "0");
                    }
                    jsonArray.add(cardJson);
                }
            }
            return jsonArray;
        } catch (Exception e) {
            throw new CustomException("信用卡账单解析失败", e);
        }
    }


    /**
     * 解析其他贷款信息和购房贷款信息
     *
     * @param otherLoan
     * @return
     */
    public static JSONArray getOtherLoanList(List<String> otherLoan, String loanType, JSONArray otherLoanList) {
        try {
            for (String loadRecord : otherLoan) {
                JSONObject oneLoanRecord = new JSONObject();
                if (loadRecord != null && !loadRecord.isEmpty()) {
                    //贷款类型
                    oneLoanRecord.put("type", loanType);
                    //发放日期
                    String cardTime = loadRecord.substring(0, loadRecord.indexOf("日") + 1);
                    oneLoanRecord.put("grant_date", cardTime);
                    //发放机构
                    String bankName = loadRecord.substring(loadRecord.indexOf("日") + 1, loadRecord.indexOf("发放的"));
                    oneLoanRecord.put("institution", bankName);
                    //发放金额
                    String creditLine = loadRecord.substring(loadRecord.indexOf("发放的") + 3, loadRecord.indexOf("（") - 1).replace(",", "").trim();
                    oneLoanRecord.put("money", creditLine);
                    //贷款用途
                    String purpose = loadRecord.substring(loadRecord.indexOf("）") + 1, loadRecord.indexOf("，"));
                    oneLoanRecord.put("account_category_main", purpose);
                    //其他贷款备注
                    oneLoanRecord.put("account_category", "");
                    //
                    //到期日期
                    if (loadRecord.contains("日到期")) {
                        String expireDate = loadRecord.substring(loadRecord.indexOf("，") + 1, loadRecord.indexOf("日到期") + 1);
                        oneLoanRecord.put("expired_date", expireDate);
                    } else {
                        oneLoanRecord.put("expired_date", "");
                    }
                    //截止日期
                    if (loadRecord.contains("截至")) {
                        String pDateStr = loadRecord.substring(loadRecord.indexOf("截至") + 2, loadRecord.indexOf("余额") - 1);
                        oneLoanRecord.put("query_date", pDateStr);
                    } else {
                        oneLoanRecord.put("query_date", "");
                    }
                    //余额
                    if (loadRecord.contains("余额")) {
                        String remainderStr = loadRecord.substring(loadRecord.indexOf("余额") + 2).replace(",", "");
                        if (loadRecord.contains("逾期金额")) {
                            remainderStr = remainderStr.substring(0, remainderStr.indexOf("，")).replaceAll(",", "").trim();
                        } else {
                            remainderStr = remainderStr.substring(0, remainderStr.indexOf("。")).replaceAll(",", "").trim();
                        }
                        oneLoanRecord.put("figure", remainderStr);
                    } else {
                        oneLoanRecord.put("figure", "0");
                    }

                    //贷款状态
                    if (loadRecord.contains("已结清")) {
                        oneLoanRecord.put("loan_state", "已结清");

                        String pDateStr = loadRecord.substring(loadRecord.indexOf("，") + 1, loadRecord.indexOf("已结清"));
                        oneLoanRecord.put("query_date", pDateStr);
                    } else if (loadRecord.contains("已转出")) {
                        oneLoanRecord.put("loan_state", "已转出");
                    } else if (loadRecord.contains("逾期金额")) {
                        oneLoanRecord.put("loan_state", "逾期");
                    } else if (loadRecord.contains("呆账")) {
                        oneLoanRecord.put("loan_state", "呆账");
                    } else {
                        oneLoanRecord.put("loan_state", "正常");
                    }

                    //逾期状态
                    oneLoanRecord.put("overdue", "0");
                    oneLoanRecord.put("years5_overdue", "0");
                    oneLoanRecord.put("days90_overdue", "0");
                    oneLoanRecord.put("yuqi_money", "0");
                    //判断是否有逾期
                    if (loadRecord.contains("逾期状态")) {
                        oneLoanRecord.put("overdue", "1");
                        //读取五年内逾期月数
                        if (loadRecord.contains("年内有") && loadRecord.contains("个月处于逾期状态")) {
                            String counts = loadRecord.substring(loadRecord.indexOf("年内有") + 3, loadRecord.indexOf("个月处于逾期状态"));
                            oneLoanRecord.put("years5_overdue", counts);
                        }
                        //逾期超过90天次数
                        if (loadRecord.contains("逾期超过90天")) {
                            String yuQi90Count = loadRecord.substring(loadRecord.indexOf("其中") + 2, loadRecord.indexOf("个月逾期"));
                            oneLoanRecord.put("days90_overdue", yuQi90Count);
                        }
                        //当前逾期金额
                        if (loadRecord.contains("逾期金额")) {
                            String dueMoney = loadRecord.substring(loadRecord.indexOf("逾期金额") + 4);
                            dueMoney = dueMoney.substring(0, dueMoney.indexOf("。")).replace(",", "").trim();
                            oneLoanRecord.put("yuqi_money", dueMoney);
                        }
                    }
                }

                otherLoanList.add(oneLoanRecord);
            }
            return otherLoanList;
        } catch (Exception e) {
            throw new CustomException("贷款信息解析失败", e);
        }
    }

    /**
     * 解析为他人担保信息
     *
     * @param otherGuarantee
     * @return
     */
    public static JSONArray getCreditGuarantee(List<String> otherGuarantee) {
        JSONArray jsonGuarantee = new JSONArray();
        try {
            for (String recorde : otherGuarantee) {
                JSONObject infoMes = new JSONObject();
                if (recorde != null && !recorde.isEmpty()) {
                    //起始日期
                    String startTime = recorde.substring(0, recorde.indexOf("，"));
                    infoMes.put("grant_date", startTime);
                    //被担保人
                    String nameLoan = recorde.substring(recorde.indexOf("为") + 1, recorde.indexOf("（"));
                    infoMes.put("name_loan", nameLoan);
                    //证件类型
                    String cardType = recorde.substring(recorde.indexOf("证件类型：") + 5);
                    cardType = cardType.substring(0, cardType.indexOf("，"));
                    infoMes.put("cardType", cardType);
                    //证件后四位
                    String cardLoan = recorde.substring(recorde.indexOf("）") - 4, recorde.indexOf("）"));
                    infoMes.put("card_loan", cardLoan);
                    //贷款发放机构
                    String institution = recorde.substring(recorde.indexOf("）在") + 2, recorde.indexOf("办理的贷款"));
                    infoMes.put("institution", institution);
                    //担保合同金额
                    String dbMoney = recorde.substring(recorde.indexOf("担保贷款合同金额") + 8);
                    dbMoney = dbMoney.substring(0, dbMoney.indexOf("，")).replace(",", "");
                    infoMes.put("money", dbMoney);
                    //担保金额
                    String dbMoney2 = recorde.substring(recorde.indexOf("担保金额") + 4);
                    dbMoney2 = dbMoney2.substring(0, dbMoney2.indexOf("。")).replace(",", "");
                    infoMes.put("money2", dbMoney2);
                    //截止日期
                    String queryDate = recorde.substring(recorde.indexOf("截至") + 2);
                    queryDate = queryDate.substring(0, queryDate.indexOf("，"));
                    infoMes.put("query_date", queryDate);
                    //余额
                    String figure = recorde.substring(recorde.indexOf("担保贷款余额") + 6);
                    figure = figure.substring(0, figure.indexOf("。")).replace(",", "");
                    infoMes.put("figure", figure);
                    //贷款状态
                    if (figure.equals("0")) {
                        infoMes.put("loan_state", "已结清");
                    } else {
                        if (recorde.contains("逾期金额")) {
                            infoMes.put("loan_state", "逾期");
                        } else {
                            infoMes.put("loan_state", "正常");
                        }
                    }
                    //逾期金额
                    infoMes.put("yuqi_money", "待确定");
                }
                jsonGuarantee.add(infoMes);
            }
            return jsonGuarantee;
        } catch (Exception e) {
            throw new CustomException("担保信息解析失败", e);
        }
    }

    /**
     * 解析机构查询表单
     *
     * @param table
     */
    public static JSONArray analysisOrgQuery(Element table, String flag) {
        JSONArray orgQueryData = new JSONArray();
        String signle = "";
        try {
            Elements tr = table.getElementsByTag("tr");
            if (tr.size() > 2) {
                String text = tr.get(0).text();

                if (flag.equals("org")) {
                    signle = "机构查询记录明细";
                } else if (flag.equals("per")) {
                    signle = "个人查询记录明细";
                }
                if (text.equals(signle)) {
                    for (int i = 3; i < tr.size() - 1; i++) {
                        JSONObject jsonData = new JSONObject();
                        Elements td = tr.get(i).getElementsByTag("td");
                        jsonData.put("chaxun_date", td.get(1).text());
                        jsonData.put("caozuoyuan", td.get(2).text());
                        jsonData.put("reason", td.get(3).text());
                        orgQueryData.add(jsonData);
                    }
                } else {
                    System.out.println(signle + "记录表格获取失败");
                }
            }
            return orgQueryData;
        } catch (Exception e) {
            throw new CustomException(signle + "解析失败", e);
        }
    }
}
