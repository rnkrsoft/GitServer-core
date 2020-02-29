package com.rnkrsoft.orm;

import com.rnkrsoft.orm.extractor.EntityExtractorHelper;
import com.rnkrsoft.orm.generator.JdbcStatement;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.metadata.DynamicMetadata;
import com.rnkrsoft.orm.metadata.TableMetadata;
import com.rnkrsoft.util.MessageFormatter;
import com.rnkrsoft.util.ValueUtils;

import java.sql.*;
import java.util.*;
/**
 * Orm和心累
 * Created by woate on 2020/02/24.
 */
public class Orm {
    OrmSetting setting;
    final EntityExtractorHelper entityExtractorHelper;
    final Map<Class, TableMetadata> tableMetadataMap = new HashMap<Class, TableMetadata>();
    final Map<Class, DataAccessObject> dataAccessObjects = new HashMap<Class, DataAccessObject>();

    public Orm(OrmSetting setting) {
        this.setting = setting;
        scan(this.setting.entityClasses);
        this.entityExtractorHelper = new EntityExtractorHelper();
        try {
            initDataBase(setting.getJdbcDriverClassName(), setting.getJdbcUrl(), setting.getUsername(), setting.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    <T> DataAccessObject<T> lookupDao(Class entityClass) {
        return dataAccessObjects.get(entityClass);
    }

    void initDataBase(String jdbcDriverClassName, String jdbcUrl, String username, String password) throws SQLException {
        try {
            Class.forName(jdbcDriverClassName);
        } catch (ClassNotFoundException e) {
            throw new SQLException(MessageFormatter.format("please check '{}'", jdbcDriverClassName));
        }


    }


    /**
     * 扫描实体类对象
     *
     * @param entityClasses 实体类列表
     */
    void scan(Set<Class> entityClasses) {
        EntityExtractorHelper helper = new EntityExtractorHelper();
        for (Class entityClass : entityClasses) {
            TableMetadata tableMetadata = helper.extractTable(entityClass);
            DataAccessObject dataAccessObject = new DataAccessObject(this, tableMetadata);
            dataAccessObjects.put(entityClass, dataAccessObject);
            tableMetadataMap.put(entityClass, tableMetadata);
        }
    }

    /**
     * 获取数据库链接
     *
     * @param pooled 是否进行池化
     * @return 数据库连接
     * @throws SQLException
     */
    Connection getConnect(boolean pooled) throws SQLException {
        Connection connection = DriverManager.getConnection(this.setting.getJdbcUrl(), this.setting.getUsername(), this.setting.getPassword());
        return connection;
    }
    public int executeUpdate(String sql, Object... values) throws SQLException {
        Connection conn = getConnect(true);
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            setParameter(ps, values);
            return ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    int executeUpdate(JdbcStatement statement) throws SQLException {
        Connection conn = getConnect(true);
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(statement.getPlaceholderSql());
            setParameter(ps, statement);
            return ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * 执行查询
     *
     * @param sql       带有占位符的sql语句
     * @param rowMapper 行映射器
     * @param values    占位符对应的值
     * @return 查询结果
     */
    <T> List<T> executeQuery(JdbcStatement statement, RowMapper<T> rowMapper) throws SQLException {
        Connection conn = getConnect(false);
        PreparedStatement ps = null;
        ResultSet rs = null;
        final List<T> result = new ArrayList<T>();
        try {
            ps = conn.prepareStatement(statement.getPlaceholderSql());
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
            if (conn != null) {
                conn.close();
            }
        }
        return result;
    }
    <T> T  executeQueryOne(JdbcStatement statement, RowMapper<T> rowMapper) throws SQLException {
        Connection conn = getConnect(false);
        PreparedStatement ps = null;
        ResultSet rs = null;
        T object = null;
        try {
            ps = conn.prepareStatement(statement.getPlaceholderSql());
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
            if (conn != null) {
                conn.close();
            }
        }
        return object;
    }

    void setParameter(PreparedStatement ps, Object[] values) throws SQLException {
        int columnSize = values.length;
        for (int i = 0; i < columnSize; i++) {
            ps.setObject(i + 1, values[i]);
        }
    }
    /**
     * 设置占位符对应的值
     *
     * @param ps     预编译语句对象
     * @throws SQLException
     */
    void setParameter(PreparedStatement ps, JdbcStatement statement) throws SQLException {
        List<ColumnMetadata> columnMetadataList = statement.getColumns();
        int columnSize = columnMetadataList.size();
        for (int i = 0; i < columnSize; i++) {
            ColumnMetadata columnMetadata = columnMetadataList.get(i);
            Class javaType = columnMetadata.getJavaType();
            Object value = ValueUtils.convert(statement.getValues().get(i), javaType);
            ps.setObject(i + 1, value);
        }
    }


    static Orm INSTANCE;

    /**
     * 获取数据访问对象
     *
     * @param entityClass 实体类型
     * @param <T>         实体类型
     * @return 数据访问对象实例
     */
    public static <T> DataAccessObject<T> dao(Class entityClass) {
        return INSTANCE.lookupDao(entityClass);
    }

    public static synchronized void init(OrmSetting setting) {
        if (INSTANCE != null) {

        }
        INSTANCE = new Orm(setting);
    }

    /**
     * 行映射器
     *
     * @param <T>
     */
    public interface RowMapper<T> {
        T mapRow(ResultSet rs, int rowNum) throws SQLException;
    }
}
