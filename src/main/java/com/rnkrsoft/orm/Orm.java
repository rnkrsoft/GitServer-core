package com.rnkrsoft.orm;

import com.rnkrsoft.orm.jdbc.executor.SimpleJdbcExecutor;
import com.rnkrsoft.orm.extractor.EntityExtractorHelper;
import com.rnkrsoft.orm.metadata.TableMetadata;
import com.rnkrsoft.util.MessageFormatter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by woate on 2020/02/24.
 * Orm和心累
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
            DataAccessObject dataAccessObject = new DataAccessObject(this, new SimpleJdbcExecutor(this), tableMetadata);
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
    public Connection getConnect(boolean pooled) throws SQLException {
        Connection connection = DriverManager.getConnection(this.setting.getJdbcUrl(), this.setting.getUsername(), this.setting.getPassword());
        return connection;
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
