package com.rnkrsoft.orm.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 无池化的数据源
 */
public class UnpooledDataSource implements DataSource {
    private ClassLoader driverClassLoader;
    private String jdbcDriverClassName;
    private String jdbcUrl;
    private String username;
    private String password;
    private Boolean autoCommit;
    private Integer transactionIsolationLevel;

    public UnpooledDataSource() {
    }

    public UnpooledDataSource(String jdbcDriverClassName, String jdbcUrl) {
        this.jdbcDriverClassName = jdbcDriverClassName;
        this.jdbcUrl = jdbcUrl;
    }

    public UnpooledDataSource(String jdbcDriverClassName, String jdbcUrl, String username, String password) {
        this.jdbcDriverClassName = jdbcDriverClassName;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    private synchronized void initializeDriver() throws SQLException {
        if (!registeredDrivers.containsKey(jdbcDriverClassName)) {
            Class<?> driverType;
            try {
                if (driverClassLoader != null) {
                    driverType = Class.forName(jdbcDriverClassName, true, driverClassLoader);
                } else {
                    driverType = Thread.currentThread().getContextClassLoader().loadClass(jdbcDriverClassName);
                }
                Driver driverInstance = (Driver) driverType.newInstance();
                DriverManager.registerDriver(new DriverProxy(driverInstance));
                registeredDrivers.put(jdbcDriverClassName, driverInstance);
            } catch (Exception e) {
                throw new SQLException("Error setting driver on UnpooledDataSource. Cause: " + e);
            }
        }
    }

    private void configureConnection(Connection conn) throws SQLException {
        if (autoCommit != null && autoCommit != conn.getAutoCommit()) {
            conn.setAutoCommit(autoCommit);
        }
        if (transactionIsolationLevel != null) {
            conn.setTransactionIsolation(transactionIsolationLevel);
        }
    }

    private Connection doGetConnection(Properties properties) throws SQLException {
        initializeDriver();
        Connection connection = DriverManager.getConnection(jdbcUrl, properties);
        configureConnection(connection);
        return connection;
    }

    private Connection doGetConnection(String username, String password) throws SQLException {
        Properties props = new Properties();
        if (username != null) {
            props.setProperty("user", username);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        return doGetConnection(props);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(username, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return doGetConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        DriverManager.setLogWriter(printWriter);
    }

    @Override
    public void setLoginTimeout(int loginTimeout) throws SQLException {
        DriverManager.setLoginTimeout(loginTimeout);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return  Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }


    private static Map<String, Driver> registeredDrivers = new ConcurrentHashMap<String, Driver>();

    /**
     * 在静态构造块中通过Jdbc spi接口查询所有注册的Jdbc驱动类型和驱动实例
     */
    static {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            registeredDrivers.put(driver.getClass().getName(), driver);
        }
    }


    private static class DriverProxy implements Driver {
        private Driver driver;

        DriverProxy(Driver d) {
            this.driver = d;
        }

        @Override
        public boolean acceptsURL(String u) throws SQLException {
            return this.driver.acceptsURL(u);
        }

        @Override
        public Connection connect(String u, Properties p) throws SQLException {
            return this.driver.connect(u, p);
        }

        @Override
        public int getMajorVersion() {
            return this.driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return this.driver.getMinorVersion();
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
            return this.driver.getPropertyInfo(u, p);
        }

        @Override
        public boolean jdbcCompliant() {
            return this.driver.jdbcCompliant();
        }

        // @Override only valid jdk7+
        public Logger getParentLogger() {
            return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        }
    }

}
