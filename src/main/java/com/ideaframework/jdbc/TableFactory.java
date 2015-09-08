package com.ideaframework.jdbc;

import com.ideaframework.jdbc.core.table.MysqlTable;
import com.ideaframework.jdbc.support.DataBaseType;
import com.ideaframework.jdbc.support.JdbcTable;
import com.ideaframework.jdbc.support.NotSupportDataBaseTypeException;
import com.ideaframework.jdbc.support.table.Table;

/**
 * Created by zhoubin on 15/9/6.
 * 数据库表工厂，用于生成数据库表处理对象
 */
public class TableFactory {

    /***
     * 获取数据库表处理对象，根据数据库类型获取相应语法的对象，并将表的定义设置到处理JdbcTable对象进行初始化
     *
     * @param dataBaseType 数据库类型
     * @param table        数据库表定义
     * @return 数据库表处理对象
     * @throws NotSupportDataBaseTypeException 不支持的数据库类型异常
     */
    public static JdbcTable getTable(DataBaseType dataBaseType, Table table) throws NotSupportDataBaseTypeException {
        JdbcTable jdbcTable = null;
        if (dataBaseType == DataBaseType.Mysql) {
            jdbcTable = new MysqlTable();
        } else {
            throw new NotSupportDataBaseTypeException("不支持的数据库类型[" + dataBaseType + "]");
        }
        jdbcTable.setTable(table);
        return jdbcTable;
    }
}
