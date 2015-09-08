package com.ideaframework.jdbc.support.model;

/**
 * Created by zhoubin on 15/9/7.
 * model查询的过滤条件定义
 */
public class Filter {

    private String joinName; //关联名称，用于确定过滤的数据库表

    private String key; //过滤字段名称

    private String type; //过滤类型  =,!=,>,<,>=,<=,between,like,in,not in

    private Object value; //过滤值，如果是between则需要设置2个值","分开，如 1,10；如果是in或者not in则多个值用","分开

    public Filter() {

    }

    public Filter(String key, String type, Object value) {
        this.joinName = null;
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public Filter(String joinName, String key, String type, Object value) {
        this.joinName = joinName;
        this.key = key;
        this.type = type;
        this.value = value;
    }

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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
