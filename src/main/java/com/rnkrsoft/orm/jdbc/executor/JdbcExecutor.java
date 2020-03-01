package com.rnkrsoft.orm.jdbc.executor;

import com.rnkrsoft.orm.Orm;
import com.rnkrsoft.orm.statement.JdbcStatement;

import java.sql.SQLException;
import java.util.List;
/**
 * Created by woate on 2020/3/1.
 * Jdbc执行器，负责SQL语句的执行
 */
public interface JdbcExecutor {
    /**
     * 执行有占位符的SQL
     * @param placeholderSql 带有占位符的SQL字符串
     * @param values 占位符对应的值数组
     * @return 影响记录条数
     * @throws SQLException 执行异常
     */
    int executeUpdate(String placeholderSql, Object... values) throws SQLException;

    /**
     * 执行语句对象
     * @param statement 语句对象
     * @return 影响记录条数
     * @throws SQLException 执行异常
     */
    int executeUpdate(JdbcStatement statement) throws SQLException;

    /**
     * 执行语句查询，返回一个数据映射为JavaBean的列表
     * @param statement 语句对象
     * @param rowMapper 行映射器
     * @param <T> Entity类型
     * @return 含有Entity对象的列表，无记录时返回空的list，不用检查是否为null
     * @throws SQLException 执行异常
     */
    <T> List<T> executeQuery(JdbcStatement statement, Orm.RowMapper<T> rowMapper) throws SQLException;

    /**
     * 执行语句查询，返回第一条满足条件的记录，如果没有返回null
     * @param statement 语句对象
     * @param rowMapper 行映射器
     * @param <T> Entity类型
     * @return Entity对象,无记录时返回null
     * @throws SQLException 执行异常
     */
    <T> T executeQueryFirst(JdbcStatement statement, Orm.RowMapper<T> rowMapper) throws SQLException;
}
