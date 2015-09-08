package com.ideaframework.jdbc.support.model;

/**
 * Created by zhoubin on 15/9/6.
 * model列的定义，对应数据库的字段
 */
public class Column {

    private String name; //列的名称，对应数据库表的字段名称

    private String joinName; //关联名称，如果列是关联表的字段，则填写关联的名称获取关联表别名

    private String alias; //列的别名，用于数据库查询的时候使用别名

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJoinName() {
        return joinName;
    }

    public void setJoinName(String joinName) {
        this.joinName = joinName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
