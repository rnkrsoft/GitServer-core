package com.rnkrsoft.orm.metadata;

import com.rnkrsoft.orm.JdbcStatementType;
import com.rnkrsoft.util.StringUtils;
import lombok.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表元信息
 */
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableMetadata<T> {
    /**
     * 实体类对象
     */
    Class<T> entityClass;
    /**
     * 数据访问对象
     */
    Class daoClass;
    /**
     * 实体类对象全限定名
     */
    String entityClassName = "";
    /**
     * 数据访问对象全限定名
     */
    String daoClassName = "";
    /**
     * Mapper文件名
     */
    String mapperName = "";
    /**
     * 表名
     */
    String tableName = "";
    /**
     * 备注
     */
    String comment = "";
    /**
     * 主键字段名列表
     */
    final List<String> primaryKeyColumnNameList = new ArrayList();
    /**
     * 非主键字段列表
     */
    final List<String> nonPrimaryKeyColumnNameList = new ArrayList<String>();
    /**
     * 字段缓存
     */
    final Map<String, ColumnMetadata> columnMetadataSet = new HashMap();
    /**
     * 使用的数据引擎
     */
    String dataEngine;
    /**
     * 当前已自增到的数值
     */
    Long autoIncrement;
    /**
     * 数据库模式
     */
    String schema = "";
    /**
     * 表前缀
     */
    String prefix = "";
    /**
     * 表后缀
     */
    String suffix = "";
    String sqlHeader = null;

    /**
     * 增加字段定义
     *
     * @param columnMetadata 字段元信息
     * @return 表元信息
     */
    public TableMetadata addColumn(ColumnMetadata columnMetadata) {
        columnMetadataSet.put(columnMetadata.getJdbcName(), columnMetadata);
        if (columnMetadata.isPrimaryKey()) {
            primaryKeyColumnNameList.add(columnMetadata.getJdbcName());
        }else {
            nonPrimaryKeyColumnNameList.add(columnMetadata.getJdbcName());
        }
        return this;
    }

    /**
     * 获取字段元信息
     *
     * @param columnName 字段名
     * @return 字段元信息
     */
    public ColumnMetadata getColumn(String columnName) {
        ColumnMetadata columnMetadata = columnMetadataSet.get(columnName);
        return columnMetadata;
    }

    /**
     * 获取字段元信息
     *
     * @param javaName java字段名
     * @return 字段元信息
     */
    public ColumnMetadata getColumnByJavaName(String javaName) {
        for (ColumnMetadata columnMetadata : columnMetadataSet.values()) {
            if (columnMetadata.getJavaName().equals(javaName)) {
                return columnMetadata;
            }
        }
        return null;
    }

    public String getFullTableName() {
        String table = tableName;
        if (!StringUtils.isBlank(prefix)) {
            table = prefix + "_" + table;
        }
        if (!StringUtils.isBlank(suffix)) {
            table = table + "_" + suffix;
        }
        if (!StringUtils.isBlank(schema)) {
            table = schema + "." + table;
        }
        return table;
    }

    @Override
    public String toString() {
        return entityClass.toString();
    }

    public T newObject() {
        T obj = null;
        try {
            Constructor noArgsConstructor = entityClass.getDeclaredConstructor();
            noArgsConstructor.setAccessible(true);
            obj = (T) noArgsConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            //fixme
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            //fixme
        } catch (InstantiationException e) {
            e.printStackTrace();
            //fixme
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            //fixme
        }
        return obj;
    }

    /**
     * 获取非主键字段名
     * @return
     */
    public List<String> getNonPrimaryKeyColumnNameList() {
        return this.nonPrimaryKeyColumnNameList;
    }

    /**
     * 获取主键字段列表
     * @return
     */
    public List<String> getPrimaryKeyColumnNameList() {
        return this.primaryKeyColumnNameList;
    }

    /**
     * 获取主键字段元信息列表
     * @return
     */
    public List<ColumnMetadata> getPrimaryKeyColumns(){
        List<ColumnMetadata> list = new ArrayList<ColumnMetadata>();
        for(String name : primaryKeyColumnNameList){
            list.add(getColumn(name));
        }
        return list;
    }

    /**
     * 获取所有字段名，包括主键字段
     * @return
     */
    public List<String> getColumns() {
        List<String> columns = new ArrayList<String>();
        columns.addAll(primaryKeyColumnNameList);
        columns.addAll(nonPrimaryKeyColumnNameList);
        return columns;
    }

    public Map<String, ColumnMetadata> getColumnMetadataSet() {
        return this.columnMetadataSet;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Class getDaoClass() {
        return daoClass;
    }

    public void setDaoClass(Class daoClass) {
        this.daoClass = daoClass;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }

    public String getDaoClassName() {
        return daoClassName;
    }

    public void setDaoClassName(String daoClassName) {
        this.daoClassName = daoClassName;
    }

    public String getMapperName() {
        return mapperName;
    }

    public void setMapperName(String mapperName) {
        this.mapperName = mapperName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDataEngine() {
        return dataEngine;
    }

    public void setDataEngine(String dataEngine) {
        this.dataEngine = dataEngine;
    }

    public Long getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(Long autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
