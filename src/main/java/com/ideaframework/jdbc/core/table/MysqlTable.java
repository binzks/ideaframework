package com.ideaframework.jdbc.core.table;

import com.ideaframework.jdbc.support.JdbcTable;
import com.ideaframework.jdbc.support.table.Field;
import com.ideaframework.jdbc.support.table.Index;
import com.ideaframework.jdbc.support.table.Table;
import com.ideaframework.support.SpringApplicationContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by zhoubin on 15/9/6.
 * Mysql数据库表处理对象
 */
public class MysqlTable implements JdbcTable {

    private static Logger logger = Logger.getLogger(JdbcTable.class);  //日志对象
    private Table table; //表结构定义
    private JdbcTemplate jdbcTemplate; //处理sql的spring jdbc对象

    @Override
    public void setTable(Table table) {
        this.table = table;
        this.jdbcTemplate = (JdbcTemplate) SpringApplicationContext.getBean(table.getDsName());
    }

    @Override
    public void initTable() {
        List<Map<String, Object>> list = null;
        try {
            list = jdbcTemplate.queryForList("describe " + this.table.getName());
        } catch (Exception e) {
            logger.debug("describe " + this.table.getName() + "错误[" + e.getMessage() + "]");
        }
        if (null == list) {
            String sql = getCreateTableSql();
            logger.debug(sql);
            jdbcTemplate.execute(sql);
            initIndexes();
            initTableData();
        } else {
            String sql = getAlertTableSql(list);
            if (StringUtils.isNotBlank(sql)) {
                logger.debug(sql);
                jdbcTemplate.execute(sql);
            }
        }
    }

    /***
     * 创建表的索引
     */
    private void initIndexes() {
        List<Index> indexes = this.table.getIndexes();
        if (null == indexes) {
            return;
        }
        StringBuffer sql = new StringBuffer();
        for (Index index : indexes) {
            sql.append("ALTER TABLE `" + this.table.getName() + "` ADD");
            if (index.getType().toUpperCase().equals("UNIQUE")) {
                sql.append(" UNIQUE ");
            }
            sql.append(" INDEX `" + index.getName() + "` (" + index.getFields() + ")  COMMENT '"
                    + index.getComment() + "';");
        }
        if (sql.length() > 0) {
            logger.debug(sql.toString());
            this.jdbcTemplate.execute(sql.toString());
        }
    }

    /***
     * 添加表的默认数据
     */
    private void initTableData() {
        List<Object[]> datas = this.table.getDatas();
        if (null == datas) {
            return;
        }
        StringBuffer colSql = new StringBuffer();
        StringBuffer valSql = new StringBuffer();
        for (Field field : this.table.getFields()) {
            colSql.append(",`").append(field.getName()).append("`");
            valSql.append(",?");
        }
        if (colSql.length() > 0) {
            colSql.delete(0, 1);
            valSql.delete(0, 1);
        }
        String dataSql = "INSERT INTO `" + this.table.getName() + "`(" + colSql + ") VALUES (" + valSql + ")";
        logger.debug(dataSql);
        jdbcTemplate.batchUpdate(dataSql, datas);
    }

    /***
     * 检查字段定义和表实际字段是否一致
     *
     * @param type         数据库字段类型
     * @param isNull       数据库字段是否可空
     * @param defaultValue 数据库字段默认值
     * @param field        表定义的字段
     * @return 一致返回true 不一致返回false
     */
    private boolean checkEqual(String type, String isNull, String defaultValue, Field field) {
        String fieldType = field.getType();
        String size = field.getSize();
        if (null != size) {
            fieldType += "(" + size + ")";
        }
        if (!type.toUpperCase().equals(fieldType.toUpperCase())) {
            return false;
        }
        String fieldIsNull = "NO";
        if (field.getIsNull()) {
            fieldIsNull = "YES";
        }
        if (!isNull.equals(fieldIsNull)) {
            return false;
        }
        String fieldDefault = field.getDefaultValue();
        if (null == defaultValue) {
            if (null != fieldDefault && !fieldDefault.toUpperCase().equals("NULL")) {
                return false;
            }
        } else {
            if (null == fieldDefault) {
                return false;
            } else {
                if (!defaultValue.toUpperCase().equals(fieldDefault.toUpperCase())) {
                    return false;
                }
            }
        }
        return true;
    }

    /***
     * 根据字段定义获取创建字段的sql语句，其中主键固定为int(11)不可空自增长
     *
     * @param field 表定义的字段
     * @return
     */
    private String getFieldSql(Field field) {
        StringBuffer sql = new StringBuffer();
        String name = field.getName();
        if (name.equals(this.table.getPkName())) {
            sql.append("`" + name + "` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键'");
        } else {
            sql.append("`" + name + "` " + field.getType());
            String size = field.getSize();
            if (null != size) {
                sql.append("(" + size + ")");
            }
            if (!field.getIsNull()) {
                sql.append(" NOT NULL");
            }
            String defaultValue = field.getDefaultValue();
            if (StringUtils.isNotBlank(defaultValue)) {
                if (defaultValue.trim().toUpperCase().equals("NULL")) {
                    sql.append(" DEFAULT NULL");
                } else {
                    sql.append(" DEFAULT '" + defaultValue + "'");
                }
            }
            if (StringUtils.isNotBlank(field.getComment())) {
                sql.append(" COMMENT '" + field.getComment() + "'");
            }
        }
        return sql.toString();
    }

    /***
     * 获取创建表的sql语句
     *
     * @return
     */
    private String getCreateTableSql() {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE `" + this.table.getName() + "` (");
        for (Field field : this.table.getFields()) {
            sql.append(getFieldSql(field));
            sql.append(",");
        }
        sql.append("PRIMARY KEY (`" + this.table.getPkName() + "`))");
        String comment = this.table.getComment();
        if (StringUtils.isNotBlank(comment)) {
            sql.append(" COMMENT='" + comment + "'");
        }
        return sql.toString();
    }

    /***
     * 获得修改表的sql语句，只修改字段，不修改索引
     *
     * @param list
     * @return
     */
    private String getAlertTableSql(List<Map<String, Object>> list) {
        StringBuffer sql = new StringBuffer();
        String pkName = this.table.getPkName();
        for (Field field : this.table.getFields()) {
            String name = field.getName();
            // 主键字段不处理
            if (name.equals(pkName)) {
                continue;
            }
            String fieldSqlType = "add";
            for (Map<String, Object> map : list) {
                if (map.get("Field").equals(name)) {
                    String type = (String) map.get("Type");
                    String isNull = (String) map.get("NuLL");
                    String defaultValue = (String) map.get("Default");
                    if (checkEqual(type, isNull, defaultValue, field)) {
                        fieldSqlType = "null";
                    } else {
                        fieldSqlType = "change";
                    }
                    list.remove(map);
                    break;
                }
            }
            if (fieldSqlType.equals("add")) {
                if (sql.length() > 0) {
                    sql.append(",");
                }
                sql.append(" ADD " + getFieldSql(field));
            } else if (fieldSqlType.equals("change")) {
                if (sql.length() > 0) {
                    sql.append(",");
                }
                sql.append(" CHANGE `" + name + "` " + getFieldSql(field));
            }
        }
        if (sql.length() > 0) {
            sql.insert(0, "ALTER TABLE `" + this.table.getName() + "`");
            sql.append(";");
        }
        return sql.toString();
    }
}
