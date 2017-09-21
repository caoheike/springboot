package com.reptile.util;

import net.sf.json.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ldw on 2017-9-14
 * <p>
 * 使用注解形式的aop，在类明上增加@aspect注解
 * 通过aop的方式返回认证状态
 */
@Component
@Aspect
public class AopClass {
    private Logger logger = LoggerFactory.getLogger(AopClass.class);
    @Autowired
	private static application applications;
    @Pointcut("@annotation(com.reptile.util.CustomAnnotation)")
    public void pointCut() {
    }

//    @Before("pointCut()")
//    public void aopMethod(JoinPoint joinPoint) {
//        try {
//            String className = joinPoint.getSignature().getDeclaringTypeName(); //所调用类名全称
//            String argsName = joinPoint.getArgs()[1].toString();//所调方法第一个参数
//            beforeTuiSong(className, argsName);
//        } catch (Exception e) {
//            logger.warn("mrlu认证状态推送失败" + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    @Around("pointCut()")
    public  Map<String,Object> around(ProceedingJoinPoint joinPoint) throws Throwable {
        Map<String,Object> map=new HashMap<String,Object>();
        try {
            String className = joinPoint.getSignature().getDeclaringTypeName(); //所调用类名全称
            String argsName = joinPoint.getArgs()[1].toString();//所调方法第一个参数
            Map<String, Object> stringObjectMap = beforeTuiSong(className, argsName);
            if(stringObjectMap.toString().contains("2222")){
                map.put("errorCode","0001");
                map.put("errorInfo","该账号未实名认证！");
                return map;
            }else{
                map= (Map<String, Object>) joinPoint.proceed();
                className = joinPoint.getSignature().getDeclaringTypeName(); //所调用类名全称
                argsName = joinPoint.getArgs()[1].toString();//所调方法第一个参数
                String approveState = "200";
                JSONObject jsonObject = JSONObject.fromObject(map);
                if (jsonObject.get("errorCode").equals("0000")) {
                    approveState = "300";
                }
                afterTuiSong(className, argsName, approveState);
            }
        } catch (Exception e) {
            logger.warn("mrlu认证状态推送失败" + e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

//    @AfterReturning(returning = "rvt", pointcut = "@annotation(com.reptile.util.CustomAnnotation)")
//    public Object AfterExec(JoinPoint joinPoint, Object rvt) {
//        try {
//            String className = joinPoint.getSignature().getDeclaringTypeName(); //所调用类名全称
//            String argsName = joinPoint.getArgs()[1].toString();//所调方法第一个参数
//            String approveState = "200";
//            JSONObject jsonObject = JSONObject.fromObject(rvt);
//            if (jsonObject.get("errorCode").equals("0000")) {
//                approveState = "300";
//            }
//            afterTuiSong(className, argsName, approveState);
//        } catch (Exception e) {
//            logger.warn("mrlu认证状态推送失败" + e.getMessage());
//            e.printStackTrace();
//        }
//        return rvt;
//    }


    //认证前推送状态
    public Map<String, Object> beforeTuiSong(String className, String argsName) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> dataMap = new HashMap<String, String>();
        if (className.contains("TelecomController") || className.contains("PhoneBillsController") || className.contains("GlobalUnicomController")) {
            dataMap.put("approveName", "callLog");
        }
        if (className.contains("CreditController")) {
            dataMap.put("approveName", "creditInvestigation");
        }
        if (className.contains("ZXBankController")) {
            dataMap.put("approveName", "bankBillFlow");
        }

        dataMap.put("cardNumber", argsName);
        dataMap.put("approveState", "100");
        map.put("data", dataMap);
        Resttemplate resttemplate = new Resttemplate();
        Map<String, Object> mapResult = resttemplate.SendMessage(map, ConstantInterface.aopPort +"/HSDC/authcode/Autherized");
        return mapResult;
    }


    //认证后推送状态
    public Map<String, Object> afterTuiSong(String className, String argsName, String approveState) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> dataMap = new HashMap<String, String>();
        if (className.contains("TelecomController") || className.contains("PhoneBillsController") || className.contains("GlobalUnicomController")) {
            dataMap.put("approveName", "callLog");
        }
        if (className.contains("CreditController")) {
            dataMap.put("approveName", "creditInvestigation");
        }
        if (className.contains("ZXBankController")) {
            dataMap.put("approveName", "bankBillFlow");
        }

        dataMap.put("cardNumber", argsName);
        dataMap.put("approveState", approveState);
        map.put("data", dataMap);
        Resttemplate resttemplate = new Resttemplate();
        Map<String, Object> mapResult= resttemplate.SendMessage(map, applications.getSendip() + "/HSDC/authcode/Autherized");
        return mapResult;
    }

}
