package com.ideaframework.jdbc.core.model;

import com.google.gson.Gson;
import com.ideaframework.jdbc.support.JdbcModel;
import com.ideaframework.jdbc.support.model.*;
import com.ideaframework.support.SpringApplicationContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by zhoubin on 15/9/7.
 */
public class MysqlModel implements JdbcModel {

    private static Logger logger = Logger.getLogger(JdbcModel.class);  //log4j日志对象
    private Model model;  //model定义
    private JdbcTemplate jdbcTemplate;  //spring jdbctemplate对象
    private SqlPrepare sqlPrepare;  //数据对象和sql语句处理对象
    private String columnSql;  //model列的查询语句块
    private String filterSql; //model定义的默认过滤条件sql语句，在查询的时候先增加过滤条件再增加业务过滤条件
    private List<Object> filterValues; //model定义的默认过滤条件值
    private String orderSql;  //model定义的默认排序，先增加默认排序再增加业务排序

    @Override
    public void setModel(Model model) {
        this.model = model;
        this.jdbcTemplate = (JdbcTemplate) SpringApplicationContext.getBean(model.getDsName());
        this.sqlPrepare = new SqlPrepare(model.getJoins());
        this.columnSql = this.sqlPrepare.getColumnSql(this.model.getColumns());
        SqlValues sqlValues = this.sqlPrepare.getFilter(this.model.getFilters());
        this.filterSql = sqlValues.getSql();
        this.filterValues = sqlValues.getValues();
        this.orderSql = this.sqlPrepare.getOrderSql(this.model.getOrders());
    }

    @Override
    public void deleteById(Object id) {
        String sql = "DELETE FROM `" + this.model.getTableName() + "` WHERE " + this.model.getPkName() + "=?";
        logger.debug("sql: " + sql + " values: " + id);
        this.jdbcTemplate.update(sql, id);
    }

    @Override
    public void delete(List<Filter> filters) {
        SqlValues sqlValues = this.sqlPrepare.getFilter(filters);
        String tableName = this.model.getTableName();
        String tableAlias = this.sqlPrepare.getTableAlias(tableName);
        String sql = "DELETE " + tableAlias + " FROM `" + tableName + "` " + tableAlias + " WHERE 1=1"
                + sqlValues.getSql();
        List<Object> values = sqlValues.getValues();
        logger.debug("sql: " + sql + " values: " + new Gson().toJson(values));
        this.jdbcTemplate.update(sql, values.toArray());
    }

    @Override
    public int insert(Object instance) {
        return insert(instance, null);
    }

    @Override
    public int insert(Map<String, Object> dataMap) {
        return insert(null, dataMap);
    }

    /***
     * 新增数据方法，将对象或者map对象添加到数据库，如果instance为null则以map对象添加
     *
     * @param instance 数据对象
     * @param dataMap  数据map
     * @return 新增的数据的id
     */
    private int insert(Object instance, Map<String, Object> dataMap) {
        SqlValues sqlValues = this.sqlPrepare.getInsert(instance, dataMap, this.model.getColumns(), this.model.getPkName());
        String sql = "INSERT INTO " + this.model.getTableName() + "(" + sqlValues.getSql() + ")" + " VALUES " + "(" + sqlValues.getValSql()
                + ")";
        List<Object> values = sqlValues.getValues();
        logger.debug("sql: " + sql + " values: " + new Gson().toJson(values));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                for (int i = 0; i < values.size(); i++) {
                    ps.setObject(i + 1, values.get(i));
                }
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public void batchInsert(List<Map<String, Object>> list) {
        String sql = null;
        List<Object[]> batchArgs = new ArrayList<>();
        List<Column> columns = this.model.getColumns();
        String pkName = this.model.getPkName();
        for (Map<String, Object> map : list) {
            SqlValues sqlValues = this.sqlPrepare.getInsert(null, map, columns, pkName);
            if (null == sql) {
                sql = "INSERT INTO " + this.model.getTableName() + "(" + sqlValues.getSql() + ")" + " VALUES " + "(" + sqlValues.getValSql()
                        + ")";
            }
            batchArgs.add(sqlValues.getValues().toArray());
        }
        logger.debug("sql: " + sql + " values: " + new Gson().toJson(batchArgs));
        this.jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Override
    public void update(Object instance) {
        update(instance, null, false);
    }

    @Override
    public void update(Map<String, Object> dataMap) {
        update(null, dataMap, false);
    }

    @Override
    public void update(Object instance, boolean isHandleNull) {
        update(instance, null, isHandleNull);
    }

    @Override
    public void update(Map<String, Object> dataMap, boolean isHandleNull) {
        update(null, dataMap, isHandleNull);
    }

    /**
     * 修改数据对象到数据库，如果instance为null则以map数据修改，修改数据一定要有主键值，否则生成主键=null的条件
     *
     * @param instance     数据对象
     * @param dataMap      map数据
     * @param isHandleNull 是否修改值为null的数据到数据库
     */
    private void update(Object instance, Map<String, Object> dataMap, boolean isHandleNull) {
        String pkName = this.model.getPkName();
        SqlValues sqlValues = this.sqlPrepare.getUpdate(instance, dataMap, isHandleNull, this.model.getColumns(), pkName);
        String sql = "UPDATE `" + this.model.getTableName() + "` SET " + sqlValues.getSql() + " WHERE `" + pkName + "`=?";
        List<Object> values = sqlValues.getValues();
        logger.debug("sql: " + sql + " values: " + new Gson().toJson(values));
        this.jdbcTemplate.update(sql, values.toArray());
    }

    @Override
    public void batchUpdate(List<Map<String, Object>> list) {
        batchUpdate(list, false);
    }

    @Override
    public void batchUpdate(List<Map<String, Object>> list, boolean isHandleNull) {
        String sql = null;
        List<Object[]> batchArgs = new ArrayList<>();
        String pkName = this.model.getPkName();
        for (Map<String, Object> map : list) {
            SqlValues sqlValues = this.sqlPrepare.getUpdate(null, map, isHandleNull, this.model.getColumns(), pkName);
            if (null == sql) {
                sql = "UPDATE `" + this.model.getTableName() + "` SET " + sqlValues.getSql() + " WHERE `" + pkName + "`=?";
            }
            batchArgs.add(sqlValues.getValues().toArray());
        }
        logger.debug("sql: " + sql + " values: " + new Gson().toJson(batchArgs));
        this.jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Override
    public Integer getTotalCount() {
        return getTotalCount(null);
    }

    @Override
    public Integer getTotalCount(List<Filter> filters) {
        SqlValues sqlValues = getSelect(filters, null, "count(*) as count", 0, 0);
        String sql = sqlValues.getSql();
        List<Object> values = sqlValues.getValues();
        logger.debug("sql: " + sql + " values: " + new Gson().toJson(values));
        return this.jdbcTemplate.queryForObject(sql, values.toArray(), Integer.class);
    }

    @Override
    public Map<String, Object> selectMap() {
        return selectMap(null, null, this.columnSql);
    }

    @Override
    public Map<String, Object> selectMap(List<Filter> filters) {
        return selectMap(filters, null, this.columnSql);
    }

    @Override
    public Map<String, Object> selectMap(List<Filter> filters, String columns) {
        return selectMap(filters, null, columns);
    }

    @Override
    public Map<String, Object> selectMap(List<Filter> filters, List<Order> orders, String columns) {
        SqlValues sqlValues = getSelect(filters, orders, columns, 0, 0);
        String sql = sqlValues.getSql();
        List<Object> values = sqlValues.getValues();
        logger.debug("sql: " + sql + " values: " + new Gson().toJson(values));
        try {
            return this.jdbcTemplate.queryForMap(sql, values.toArray());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> selectMaps() {
        return selectMaps(null, null, this.columnSql, 0, 0);
    }

    @Override
    public List<Map<String, Object>> selectMaps(List<Filter> filters) {
        return selectMaps(filters, null, this.columnSql, 0, 0);
    }

    @Override
    public List<Map<String, Object>> selectMaps(List<Filter> filters, String columns) {
        return selectMaps(filters, null, columns, 0, 0);
    }

    @Override
    public List<Map<String, Object>> selectMaps(List<Filter> filters, int begin, int size) {
        return selectMaps(filters, null, this.columnSql, begin, size);
    }

    @Override
    public List<Map<String, Object>> selectMaps(List<Filter> filters, String columns, int begin, int size) {
        return selectMaps(filters, null, columns, begin, size);
    }

    @Override
    public List<Map<String, Object>> selectMaps(List<Filter> filters, List<Order> orders, String columns, int begin, int size) {
        SqlValues sqlValues = getSelect(filters, orders, columns, begin, size);
        String sql = sqlValues.getSql();
        List<Object> values = sqlValues.getValues();
        logger.debug("sql: " + sql + " values: " + new Gson().toJson(values));
        try {
            return this.jdbcTemplate.queryForList(sql, values.toArray());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /***
     * 获取查询sql语句和过滤值，如果model本身有过滤和排序的加上预先定义的过滤和排序，再加业务过滤排序查询
     *
     * @param filters 业务过滤条件
     * @param orders  业务排序条件
     * @param columns 查询列字段
     * @param begin   数据开始行数
     * @param size    查询数据量
     * @return
     */
    private SqlValues getSelect(List<Filter> filters, List<Order> orders, String columns, int begin, int size) {
        StringBuffer sql = new StringBuffer();
        List<Object> values = new ArrayList<>();
        String tableName = this.model.getTableName();
        sql.append("SELECT " + columns + " FROM `" + tableName + "` AS " + this.sqlPrepare.getTableAlias(tableName) + " " + this.sqlPrepare.getJoinSql()
                + " WHERE 1=1" + filterSql);
        if (null != this.filterValues) {
            values.addAll(this.filterValues);
        }
        SqlValues sqlValues = this.sqlPrepare.getFilter(filters);
        sql.append(sqlValues.getSql());
        List<Object> fv = sqlValues.getValues();
        if (null != fv) {
            values.addAll(fv);
        }
        String order = this.orderSql;
        order += this.sqlPrepare.getOrderSql(orders);
        if (StringUtils.isNotBlank(order)) {
            sql.append(" ORDER BY " + order);
        }
        if (size > 0) {
            sql.append(" LIMIT " + begin + "," + size);
        }
        SqlValues sv = new SqlValues();
        sv.setSql(sql.toString());
        sv.setValues(values);
        return sv;
    }
}
