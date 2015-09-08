package com.ideaframework.jdbc.support.model;

/**
 * Created by zhoubin on 15/9/7.
 * model的排序定义
 */
public class Order {

    private String joinName; //排序字段关联名称，如果null表示主表

    private String key;  //排序字段名称

    private String type; //排序规则 asc desc

    public String getJoinName() {
        return joinName;
    }

    public void setJoinName(String joinName) {
        this.joinName = joinName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
