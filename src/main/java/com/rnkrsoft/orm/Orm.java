package com.rnkrsoft.orm;

import com.rnkrsoft.orm.dao.DataAccessObject;
import com.rnkrsoft.orm.datasource.UnpooledDataSource;
import com.rnkrsoft.orm.session.Session;
import com.rnkrsoft.orm.session.SessionFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

/**
 * Created by woate on 2020/02/24.
 * Orm和心累
 */
public final class Orm {
    private final OrmSetting setting;
    private DataSource dataSource;

    /**
     * 通过配置对象创建Orm实例
     * @param setting 配置对象
     * @throws SQLException 如果没找到驱动类或者数据源抛出
     */
    private Orm(OrmSetting setting) throws SQLException {
        Class driverClass = null;
        try {
            driverClass = Class.forName(setting.getJdbcDriverClassName());
        } catch (ClassNotFoundException e) {
           throw new SQLException("not found jdbc driver " + setting.getJdbcDriverClassName(), e);
        }
        //如果是数据库连接池
        if(DataSource.class.isAssignableFrom(driverClass)){
            //TODO 数据源未实现
            throw new RuntimeException("TODO 数据源未实现");
        }else if (Driver.class.isAssignableFrom(driverClass)){
            this.dataSource = new UnpooledDataSource(setting.getJdbcDriverClassName(), setting.getJdbcUrl(), setting.getUsername(), setting.getPassword());
        }
        this.setting = setting;
        //使用当前的配置信息初始化数据访问对象
        DataAccessObject.init(setting);
    }

    /**
     * 获取Orm会话信息
     * @return 会话对象
     */
    public Session getSession(){
       return SessionFactory.createSession(this);
    }

    /**
     * 获取数据库连接
     * @return 数据库连接对象
     * @throws SQLException 数据库异常
     */
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection(setting.getUsername(), setting.getPassword());
    }

    static Orm INSTANCE;

    /**
     * 使用propeties进行初始化
     * @param properties 配置对象
     * @throws SQLException  数据库异常
     * @see #init(OrmSetting)
     */
    public static synchronized void init(Properties properties) throws SQLException {
        init(OrmSetting.builder()
                .jdbcDriverClassName(properties.getProperty("orm.jdbc.driver"))
                .jdbcUrl(properties.getProperty("orm.jdbc.url"))
                .username(properties.getProperty("orm.jdbc.username"))
                .password(properties.getProperty("orm.jdbc.password"))
                .build());
    }

    /**
     * 初始化Orm
     * @param setting 配置对象
     * @return 初始化好的Orm对象
     * @throws SQLException 数据库异常
     * @see #init(Properties)
     */
    public static synchronized Orm init(OrmSetting setting) throws SQLException {
        if (INSTANCE != null) {

        }
        INSTANCE = new Orm(setting);
        return INSTANCE;
    }

    /**
     * 获取会话对象
     * @return 会话对象
     * @see #getSession()
     */
    public static Session session(){
        return INSTANCE.getSession();
    }

    /**
     * 获取数据库连接
     * @return 连接对象
     * @throws SQLException 数据库异常
     * @see #getConnection()
     */
    public static Connection connection() throws SQLException {
        return INSTANCE.getConnection();
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
