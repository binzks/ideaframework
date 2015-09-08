package com.ideaframework.jdbc.support.model;

import java.util.List;

/**
 * Created by zhoubin on 15/9/7.
 * SqlPrepare生成的数据sql和values的返回对象
 */
public class SqlValues {

    private String sql;  //sql语句

    private String valSql;  //新增数据结构，value值sql语句 (?,?,..)

    private List<Object> values;  //sql语句值列表

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getValSql() {
        return valSql;
    }

    public void setValSql(String valSql) {
        this.valSql = valSql;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }
}
