package com.ideaframework.jdbc.support.table;

/**
 * Created by zhoubin on 15/9/6.
 * table的索引定义，主要用于生成表索引
 */
public class Index {

    private String name; //索引名称

    private String type; //索引类型，唯一或者索引

    private String comment; //索引描述

    private String fields; //索引字段，多个,号隔开

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }
}
