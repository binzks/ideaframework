package com.ideaframework.jdbc.support;

import com.ideaframework.jdbc.support.model.Filter;
import com.ideaframework.jdbc.support.model.Model;
import com.ideaframework.jdbc.support.model.Order;

import java.util.List;
import java.util.Map;

/**
 * Created by zhoubin on 15/9/7.
 * model处理数据接口
 */
public interface JdbcModel {

    /***
     * 设置model定义信息
     *
     * @param model model定义
     */
    public void setModel(Model model);

    /**
     * 根据主键值删除数据
     *
     * @param id 要删除数据的id
     */
    public void deleteById(Object id);

    /***
     * 根据过滤条件删除数据
     *
     * @param filters 过滤条件
     */
    public void delete(List<Filter> filters);

    /***
     * 新增数据，返回新增数据的数据id，数据对象必须对应model列，并且有get字段名方法
     *
     * @param instance 待新增的数据
     * @return
     */
    public int insert(Object instance);

    /***
     * 新增数据，返回新增数据的数据id
     *
     * @param dataMap 待新增的数据
     * @return
     */
    public int insert(Map<String, Object> dataMap);


    /***
     * 批量新增数据，数据list必须具体有相同的格式，取第一条数据的数据格式生成新增sql语句
     *
     * @param list 待新增的数据列表
     */
    public void batchInsert(List<Map<String, Object>> list);

    /***
     * 修改数据，数据主键必须有值，不修改数据值为null的列
     *
     * @param instance 待修改的数据
     */
    public void update(Object instance);

    /***
     * 修改数据，数据主键必须有值，不修改数据值为null的列
     *
     * @param dataMap 待修改的数据
     */
    public void update(Map<String, Object> dataMap);

    /***
     * 修改数据，数据主键必须有值
     *
     * @param instance     待修改的数据
     * @param isHandleNull 是否要修改值为null的字段true表示是
     */
    public void update(Object instance, boolean isHandleNull);

    /***
     * 修改数据，数据主键必须有值
     *
     * @param dataMap      待修改的数据
     * @param isHandleNull 是否要修改值为null的字段true表示是
     */
    public void update(Map<String, Object> dataMap, boolean isHandleNull);

    /***
     * 批量修改数据，list必须具有相同的数据结构，以第一条数据生成sql语句，不修改值为null的数据
     *
     * @param list 待修改的数据列表
     */
    public void batchUpdate(List<Map<String, Object>> list);

    /**
     * 批量修改数据，list必须具有相同的数据结构，以第一条数据生成sql语句
     *
     * @param list         待修改的数据列表
     * @param isHandleNull 是否要修改值为null的字段true表示是
     */
    public void batchUpdate(List<Map<String, Object>> list, boolean isHandleNull);

    /**
     * 获取数据总数
     *
     * @return
     */
    public Integer getTotalCount();

    /**
     * 根据过滤条件获取数据总数
     *
     * @param filters 过滤条件
     * @return
     */
    public Integer getTotalCount(List<Filter> filters);

    /***
     * 查询单条数据，如果没有数据则返回null
     *
     * @return
     */
    public Map<String, Object> selectMap();

    /***
     * 根据条件查询单条数据，如果没有数据则返回null
     *
     * @param filters 过滤条件
     * @return
     */
    public Map<String, Object> selectMap(List<Filter> filters);

    /***
     * 根据条件获取指定字段的单条数据，如果没有数据则返回null
     *
     * @param filters 过滤条件
     * @param columns 要查询的字段，多个,隔开 *表示全部
     * @return
     */
    public Map<String, Object> selectMap(List<Filter> filters, String columns);

    /**
     * 获取单条数据，如果没有数据则返回null
     *
     * @param filters 过滤条件
     * @param orders  排序
     * @param columns 要查询的字段，多个,隔开 *表示全部
     * @return
     */
    public Map<String, Object> selectMap(List<Filter> filters, List<Order> orders, String columns);

    /***
     * 查询数据，如果没有数据则返回null
     *
     * @return
     */
    public List<Map<String, Object>> selectMaps();

    /***
     * 根据条件查询数据，如果没有数据则返回null
     *
     * @param filters 过滤条件
     * @return
     */
    public List<Map<String, Object>> selectMaps(List<Filter> filters);

    /***
     * 根据条件获取指定字段的数据，如果没有数据则返回null
     *
     * @param filters 过滤条件
     * @param columns 要查询的字段，多个,隔开 *表示全部
     * @return
     */
    public List<Map<String, Object>> selectMaps(List<Filter> filters, String columns);

    /***
     * 根据条件获取分页数据，如果没有数据则返回null
     *
     * @param filters 过滤条件
     * @param begin   开始数据行
     * @param size    查询数量，0表示不限制
     * @return
     */
    public List<Map<String, Object>> selectMaps(List<Filter> filters, int begin,
                                                int size);

    /***
     * 根据条件获取指定字段的分页数据，如果没有数据则返回null
     *
     * @param filters 过滤条件
     * @param columns 要查询的字段，多个,隔开 *表示全部
     * @param begin   开始数据行
     * @param size    查询数量，0表示不限制
     * @return
     */
    public List<Map<String, Object>> selectMaps(List<Filter> filters, String columns, int begin,
                                                int size);

    /**
     * 获取数据，如果没有数据则返回null
     *
     * @param filters 过滤条件
     * @param orders  排序
     * @param columns 要查询的字段，多个,隔开 *表示全部
     * @param begin   开始数据行
     * @param size    查询数量，0表示不限制
     * @return
     */
    public List<Map<String, Object>> selectMaps(List<Filter> filters, List<Order> orders, String columns, int begin,
                                                int size);
}
