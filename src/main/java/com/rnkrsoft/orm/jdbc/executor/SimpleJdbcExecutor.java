package com.rnkrsoft.orm.jdbc.executor;

import com.rnkrsoft.orm.Orm;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.statement.JdbcStatement;
import com.rnkrsoft.util.ValueUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by woate on 2020/3/1.
 * 一个简单的Jdbc执行器实现
 */
public class SimpleJdbcExecutor implements JdbcExecutor {
    Orm orm;

    public SimpleJdbcExecutor(Orm orm) {
        this.orm = orm;
    }

    /**
     * 执行自定义具有占位符的SQL语句
     *
     * @param sql    带有占位符的SQL语句
     * @param values 值数组与占位符匹配
     * @return 变更条数
     * @throws SQLException 数据库异常
     */
    public int executeUpdate(String sql, Object... values) throws SQLException {
        Connection connection = this.orm.getConnect(false);
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            setParameter(ps, values);
            return ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public int executeUpdate(JdbcStatement statement) throws SQLException {
        Connection connection = this.orm.getConnect(false);
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(statement.getPlaceholderSql());
            setParameter(ps, statement);
            return ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * 执行查询
     *
     * @param statement
     * @param rowMapper
     * @param <T>
     * @return
     * @throws SQLException
     */
    public <T> List<T> executeQuery(JdbcStatement statement, Orm.RowMapper<T> rowMapper) throws SQLException {
        Connection connection = this.orm.getConnect(false);
        PreparedStatement ps = null;
        ResultSet rs = null;
        final List<T> result = new ArrayList<T>();
        try {
            ps = connection.prepareStatement(statement.getPlaceholderSql());
            setParameter(ps, statement);
            rs = ps.executeQuery();
            while (rs.next()) {
                T object = rowMapper.mapRow(rs, rs.getRow());
                result.add(object);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return result;
    }

    public <T> T executeQueryFirst(JdbcStatement statement, Orm.RowMapper<T> rowMapper) throws SQLException {
        Connection connection = this.orm.getConnect(false);
        PreparedStatement ps = null;
        ResultSet rs = null;
        T object = null;
        try {
            ps = connection.prepareStatement(statement.getPlaceholderSql());
            setParameter(ps, statement);
            rs = ps.executeQuery();
            if (rs.next()) {
                object = rowMapper.mapRow(rs, rs.getRow());
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return object;
    }

    /**
     * 设置占位符对应的值
     *
     * @param ps
     * @param values
     * @throws SQLException
     */
    void setParameter(PreparedStatement ps, Object[] values) throws SQLException {
        int columnSize = values.length;
        for (int i = 0; i < columnSize; i++) {
            ps.setObject(i + 1, values[i]);
        }
    }

    /**
     * 设置占位符对应的值
     *
     * @param ps 预编译语句对象
     * @throws SQLException
     */
    void setParameter(PreparedStatement ps, JdbcStatement statement) throws SQLException {
        List<ColumnMetadata> columnMetadataList = statement.getColumns();
        int columnSize = columnMetadataList.size();
        for (int i = 0; i < columnSize; i++) {
            ColumnMetadata columnMetadata = columnMetadataList.get(i);
            Class javaType = columnMetadata.getJavaType();
            Object value = ValueUtils.convert(statement.getValues()[i], javaType);
            ps.setObject(i + 1, value);
        }
    }

}
