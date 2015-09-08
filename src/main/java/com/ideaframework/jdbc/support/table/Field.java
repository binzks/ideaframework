package com.ideaframework.jdbc.support.table;

/**
 * Created by zhoubin on 15/9/6.
 * table的列定义，用于生成数据库脚本
 */
public class Field {

    private String name;    //字段名称

    private String comment; //字段说明

    private String type;  //字段类型 varchar int等数据库类型

    private String size;  //字段长度

    private Boolean isNull; //字段是否可空，true可以，false不可空

    private String defaultValue;  //字段的默认值

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Boolean getIsNull() {
        return isNull;
    }

    public void setIsNull(Boolean isNull) {
        this.isNull = isNull;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
