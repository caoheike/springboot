package com.reptile.util;

/**
 * 自定义异常类
 *
 * @author mrlu
 * @date 2016/12/23
 */
public class CustomException extends RuntimeException {
    public String exceptionInfo;
    public Exception e;
    public CustomException(String message,Exception e){
        this.exceptionInfo=message;
        this.e=e;
    }

    public String getExceptionInfo(){
        return exceptionInfo;
    }
    public Exception getException(){
        return e;
    }

}
