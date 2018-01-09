package com.reptile.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PushState {

    public static void state(String UserCard, String approveName, int stat) {
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> stati = new HashMap<String, Object>();
        String stats = stat + "";
        stati.put("cardNumber", UserCard);
        stati.put("approveName", approveName);
        stati.put("approveState", stats);
        data.put("data", stati);
        System.out.println("-state开始推送-"+data);
        Resttemplate resttemplatestati = new Resttemplate();
        resttemplatestati.SendMessage(data, ConstantInterface.port + "/HSDC/authcode/Autherized");
    }
    /**
     * 200
     * @param UserCard
     * @param approveName
     * @param stat
     * @param message
     */
    public static void state(String UserCard, String approveName, int stat,String message) {
        message = judgePunctuation(message);
        if("callLog".equals(approveName)) {
    		message = "您提交的运营商认证失败，失败原因："+message+"，您可以重新认证或者选择其他产品。";
    	}else if("CHSI".equals(approveName)){
    		message = "您提交的学信网认证失败，失败原因："+message+"，您可以重新认证或者选择其他产品。";
    	}else if("TaoBao".equals(approveName)){
    		message = "您提交的淘宝网认证失败，失败原因："+message+"，您可以重新认证或者选择其他产品。";
    	}else if("creditInvestigation".equals(approveName)){
    		message = "您提交的个人征信认证失败，失败原因："+message+", 您可以重新认证或者选择其他产品。";
    	}else if("bankBillFlow".equals(approveName)){
    		message = "您提交的信用卡认证失败，失败原因："+message+"，您可以重新认证或者选择其他产品。";
    	}else if("savings".equals(approveName)){
    		message = "您提交的储蓄卡认证失败，失败原因："+message+"，您可以重新认证或者选择其他产品。";
    	}

        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> stati = new HashMap<String, Object>();
        String stats = stat + "";
        stati.put("cardNumber", UserCard);
        stati.put("approveName", approveName);
        stati.put("approveState", stats);
        stati.put("message", message);
        data.put("data", stati);
        System.out.println("-200--state开始推送-"+data);
        Resttemplate resttemplatestati = new Resttemplate();
        resttemplatestati.SendMessage(data, ConstantInterface.port+"/HSDC/authcode/Autherized");
    }
    /**
     * 银行的  200
     * @param UserCard
     * @param approveName
     * @param stat
     * @param message
     */
    public static void statenew(String UserCard, String approveName, int stat,String message) {
        message = judgePunctuation(message);
    	if("bankBillFlow".equals(approveName)){
    		message = "您提交的信用卡认证失败，失败原因："+message+"，您可以重新认证或者选择其他产品。";
    	}
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> stati = new HashMap<String, Object>();
        String stats = stat + "";
        stati.put("cardNumber", UserCard);
        stati.put("approveName", approveName);
        stati.put("approveState", stats);
        stati.put("message", message);
        data.put("data", stati);
        System.out.println("-200--state开始推送-"+data);
        Resttemplate resttemplatestati = new Resttemplate();
        resttemplatestati.SendMessage(data, ConstantInterface.port+"/HSDC/authcode/messagePush");
    }

    /**
     * 处理以标点结尾的字符串
     * @param message
     */
    private static String judgePunctuation(String message){
        if(message!=null&&message.length()>0){
            int length=message.length();
            String lastChar=message.substring(length-1);
            String regx="[ ！，。!,.]";
            Pattern pattern=Pattern.compile(regx);
            Matcher matcher = pattern.matcher(lastChar);
            boolean hasIt = matcher.find();
            if(hasIt){
                message=message.substring(0,message.length()-1);
            }
        }
        return message;
    }

    public static void main(String[] args) {
        state("610403199112021515","callLog",0001,"网络繁忙。网络异常,");
    }
}
