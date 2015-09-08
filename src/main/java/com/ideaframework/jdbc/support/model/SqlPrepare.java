package com.ideaframework.jdbc.support.model;

import javafx.scene.control.Tab;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhoubin on 15/9/7.
 * 主要用于生成关联sql语句，将数据对象转换为sql语句和值
 */
public class SqlPrepare {

    private final String TABLE_ALIAS_PREFIX = "t"; // 主表别名
    private String joinSql; // model设置的统一关联查询sql
    private Map<String, String> tableAliasMap; // 表的别名

    /***
     * 初始化sqlPrepare，主要生产关联表信息和关联表别名
     *
     * @param joins 关联表定义
     */
    public SqlPrepare(List<Join> joins) {
        tableAliasMap = new HashMap<>();
        if (null == joins) {
            joinSql = "";
        } else {
            //先生成所有关联表的别名
            for (int i = 0; i < joins.size(); i++) {
                tableAliasMap.put(joins.get(i).getName(), TABLE_ALIAS_PREFIX + (i + 1));
            }
            //生成关联sql语句
            StringBuffer joinBuffer = new StringBuffer();
            for (Join join : joins) {
                String alias = tableAliasMap.get(join.getName());
                String joinAlias = tableAliasMap.get(join.getJoinName());
                joinBuffer.append(" ").append(join.getType()).append(" `").append(join.getTable()).append("` AS ")
                        .append(alias).append(" on ").append(alias).append(".").append(join.getKey()).
                        append("=").append(joinAlias).append(".").append(join.getJoinKey());
            }
            joinSql = joinBuffer.toString();
        }
    }

    /**
     * 返回关联表sql语句
     *
     * @return
     */
    public String getJoinSql() {
        return this.joinSql;
    }

    /***
     * 根据列的定义获取列的查询语句块
     *
     * @param columns 列定义
     * @return 列的语句块
     */
    public String getColumnSql(List<Column> columns) {
        StringBuffer columnBuffer = new StringBuffer();
        for (Column column : columns) {
            if (columnBuffer.length() > 0) {
                columnBuffer.append(",");
            }
            columnBuffer.append(getTableAlias(column.getJoinName())).append(".").append(column.getName());
            String alias = column.getAlias();
            if (StringUtils.isNotBlank(alias)) {
                columnBuffer.append(" AS ").append(alias);
            }
        }
        return columnBuffer.toString();
    }

    /***
     * 根据过滤条件生成，过滤的sql语句和过滤值
     *
     * @param filterList 过滤条件
     * @return
     */
    public SqlValues getFilter(List<Filter> filterList) {
        SqlValues sqlValues = new SqlValues();
        if (null == filterList) {
            sqlValues.setSql("");
            sqlValues.setValues(null);
            return sqlValues;
        }
        StringBuffer filterBuffer = new StringBuffer();
        List<Object> filterValues = new ArrayList<>();
        for (Filter filter : filterList) {
            filterBuffer.append(" AND ").append(getTableAlias(filter.getJoinName())).append(".").append(filter.getKey())
                    .append(" ");
            String filterType = filter.getType().trim().toUpperCase();
            if (filterType.equals("=") || filterType.equals("!=") || filterType.equals(">") || filterType.equals(">=")
                    || filterType.equals("<") || filterType.equals("<=")) {
                filterBuffer.append(filterType).append("?");
                filterValues.add(filter.getValue());
            } else if (filterType.equals("LIKE")) {
                filterBuffer.append(filterType).append(" ").append("?");
                filterValues.add("%" + filter.getValue() + "%");
            } else if (filterType.equals("BETWEEN")) {
                filterBuffer.append("BETWEEN ? AND ?");
                String[] values = filter.getValue().toString().split(",");
                filterValues.add(values[0]);
                filterValues.add(values[1]);
            } else if (filterType.equals("IN") || filterType.equals("NOT IN")) {
                if (null == filter.getValue()) {
                    filterBuffer.append(filterType).append("(?)");
                    filterValues.add(null);
                } else {
                    String[] values = filter.getValue().toString().split(",");
                    StringBuffer in = new StringBuffer();
                    for (int i = 0; i < values.length; i++) {
                        in.append(",?");
                        filterValues.add(values[i]);
                    }
                    if (in.length() > 0) {
                        in.delete(0, 1);
                    }
                    filterBuffer.append(filterType).append("(").append(in).append(")");
                }
            } else if (filterType.equals("IS NULL") || filterType.equals("IS NOT NULL")) {
                filterBuffer.append(filterType);
            }
        }
        sqlValues.setSql(filterBuffer.toString());
        sqlValues.setValues(filterValues);
        return sqlValues;
    }

    /***
     * 根据数据对象生成新增数据的sql语句和值，如果instance为null表示dataMap数据，否则以instance数据生成
     *
     * @param instance 对象类型数据
     * @param dataMap  map类型数据
     * @param columns  model对应的列
     * @param pkName   model主键名称
     * @return
     */
    public SqlValues getInsert(Object instance, Map<String, Object> dataMap, List<Column> columns, String pkName) {
        StringBuffer colSql = new StringBuffer();
        StringBuffer valSql = new StringBuffer();
        List<Object> values = new ArrayList<>();
        for (Column column : columns) {
            String tableAlias = getTableAlias(column.getJoinName());
            if (!tableAlias.equals(TABLE_ALIAS_PREFIX)) {
                continue;
            }
            String colName = column.getName();
            if (colName.equals(pkName)) {
                continue;
            }
            Object value = null;
            if (null != instance) {
                value = getFieldValueByName(colName, instance);
            } else {
                value = dataMap.get(colName);
            }
            if (colSql.length() > 0) {
                colSql.append(",");
                valSql.append(",");
            }
            colSql.append("`").append(colName).append("`");
            valSql.append("?");
            values.add(value);
        }
        SqlValues sqlValues = new SqlValues();
        sqlValues.setSql(colSql.toString());
        sqlValues.setValSql(valSql.toString());
        sqlValues.setValues(values);
        return sqlValues;
    }

    /**
     * 根据数据对象生成修改数据sql和值，如果instance为null表示dataMap数据，否则以instance数据生成，数据一定要包含主键值
     *
     * @param instance     对象类型数据
     * @param dataMap      map类型数据
     * @param isHandleNull null字段是否更新，true是 false不更新
     * @param columns      model对应的列
     * @param pkName       model的主键名称
     * @return
     */
    public SqlValues getUpdate(Object instance, Map<String, Object> dataMap, boolean isHandleNull, List<Column> columns, String pkName) {
        StringBuffer colSql = new StringBuffer();
        List<Object> values = new ArrayList<>();
        for (Column column : columns) {
            String tableAlias = getTableAlias(column.getJoinName());
            if (!tableAlias.equals(TABLE_ALIAS_PREFIX)) {
                continue;
            }
            String colName = column.getName();
            if (colName.equals(pkName)) {
                continue;
            }
            Object value = null;
            if (null != instance) {
                value = getFieldValueByName(colName, instance);
            } else {
                value = dataMap.get(colName);
            }
            if (null != value || isHandleNull) {
                if (colSql.length() > 0) {
                    colSql.append(",");
                }
                colSql.append("`").append(colName).append("`=?");
                values.add(value);
            }
        }
        Object pkValue = null;
        if (null != instance) {
            pkValue = getFieldValueByName(pkName, instance);
        } else {
            pkValue = dataMap.get(pkName);
        }
        values.add(pkValue);
        SqlValues sqlValues = new SqlValues();
        sqlValues.setSql(colSql.toString());
        sqlValues.setValues(values);
        return sqlValues;
    }

    /***
     * 获取关联的数据库表在生成sql的别名，如果没有获取则返回主表的别名
     *
     * @param joinName 关联名称
     * @return 关联表别名
     */
    public String getTableAlias(String joinName) {
        String tableAlias = tableAliasMap.get(joinName);
        if (null == tableAlias) {
            tableAlias = TABLE_ALIAS_PREFIX;
        }
        return tableAlias;
    }


    /***
     * 生成排序的sql语句
     *
     * @param orders
     * @return
     */
    public String getOrderSql(List<Order> orders) {
        if (null == orders) {
            return "";
        } else {
            StringBuffer orderBuffer = new StringBuffer();
            for (Order order : orders) {
                if (orderBuffer.length() > 0) {
                    orderBuffer.append(",");
                }
                if (order.getType().toUpperCase().equals("ASC")) {
                    orderBuffer.append(getTableAlias(order.getJoinName())).append(".").append(order.getKey())
                            .append(" ASC");
                } else {
                    orderBuffer.append(getTableAlias(order.getJoinName())).append(".").append(order.getKey())
                            .append(" DESC");
                }
            }
            return orderBuffer.toString();
        }
    }

    /***
     * 回去对象指定属性的值
     *
     * @param fieldName 属性名称
     * @param object    取值对象
     * @return
     */
    private Object getFieldValueByName(String fieldName, Object object) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, new Object[]{});
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
