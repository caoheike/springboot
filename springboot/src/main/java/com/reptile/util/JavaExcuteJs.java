package com.reptile.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileReader;

/**
 * java执行js方法
 *
 * @author mrlu
 * @date 2017/12/19
 */
public class JavaExcuteJs {
    public static void main(String[] args) throws Exception {
//        excuteJs("f://shanxi.js","strEnc","18682940971","3","2","1");
        String valAesEncryptSet = JavaExcuteJs.excuteJs("static/js/telecomlogin.js", "valAesEncryptSet", "131420");
        System.out.println(valAesEncryptSet);
    }

    /**
     * java代码执行js方法
     * @param jsPath   js文件所在路径
     * @param methodName  执行方法名称
     * @param parameter  参数
     * @return
     * @throws Exception
     */
    public static String excuteJs(String jsPath,String methodName,String ...parameter) throws Exception {
        Resource classPathResource = new ClassPathResource(jsPath);
        File file = classPathResource.getFile();
        String result=null;
        ScriptEngineManager manager=new ScriptEngineManager();
        ScriptEngine javascript = manager.getEngineByName("javascript");
        FileReader fileReader=new FileReader(file.getAbsoluteFile());
        javascript.eval(fileReader);
        if(javascript instanceof Invocable) {
            Invocable invoke = (Invocable)javascript;
            Object strEnc = invoke.invokeFunction(methodName, parameter);
            result=strEnc.toString();
        }
        return result;
    }
}
