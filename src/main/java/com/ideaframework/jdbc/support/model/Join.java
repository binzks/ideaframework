package com.ideaframework.jdbc.support.model;

/**
 * Created by zhoubin on 15/9/7.
 * model关联表的定义
 */
public class Join {

    private String name;  //关联的名称，用于标识关联，获取关联表的表名和别名

    private String table; //关联的表名称

    private String type;  //关联类型 left join, right join, inner join

    private String key;   //关联表的字段名称

    private String joinName; //关联的主表的关联名称，如果null表示关联model的主表，如果不为null表示关联model的关联表

    private String joinKey;  //关联的主表的字段名称

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getJoinName() {
        return joinName;
    }

    public void setJoinName(String joinName) {
        this.joinName = joinName;
    }

    public String getJoinKey() {
        return joinKey;
    }

    public void setJoinKey(String joinKey) {
        this.joinKey = joinKey;
    }
}
