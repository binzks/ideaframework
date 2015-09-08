package com.ideaframework.jdbc.support;

/**
 * Created by zhoubin on 15/9/7.
 * 不支持的数据库类型异常
 */
public class NotSupportDataBaseTypeException extends Exception {

    public NotSupportDataBaseTypeException(String msg) {
        super(msg);
    }
}
