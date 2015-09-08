package com.ideaframework.jdbc;

import com.ideaframework.jdbc.core.model.MysqlModel;
import com.ideaframework.jdbc.support.DataBaseType;
import com.ideaframework.jdbc.support.JdbcModel;
import com.ideaframework.jdbc.support.NotSupportDataBaseTypeException;
import com.ideaframework.jdbc.support.model.Model;

/**
 * Created by zhoubin on 15/9/7.
 * model工厂，用于获取model对象
 */
public class ModelFactory {

    /***
     * 获取数据model对象，根据数据库类型获取对应对象，用于数据增、删、改、查
     *
     * @param dataBaseType 数据库类型
     * @param model        数据model定义
     * @return 数据model对象
     * @throws NotSupportDataBaseTypeException 不支持的数据库类型异常
     */
    public static JdbcModel getModel(DataBaseType dataBaseType, Model model) throws NotSupportDataBaseTypeException {
        JdbcModel jdbcModel = null;
        if (dataBaseType == DataBaseType.Mysql) {
            jdbcModel = new MysqlModel();
        } else {
            throw new NotSupportDataBaseTypeException("不支持的数据库类型[" + dataBaseType + "]");
        }
        jdbcModel.setModel(model);
        return jdbcModel;
    }
}
