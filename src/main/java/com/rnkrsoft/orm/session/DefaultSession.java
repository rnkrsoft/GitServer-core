package com.rnkrsoft.orm.session;

import com.rnkrsoft.orm.Orm;
import com.rnkrsoft.orm.OrmSetting;
import com.rnkrsoft.orm.dao.DataAccessObject;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

class DefaultSession implements Session {
    Connection connection;
    Orm orm;
    public DefaultSession(Orm orm) {
        this.orm = orm;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = orm.getConnection();
        this.connection = connection;
        return connection;
    }

    @Override
    public Session begin() throws SQLException {
        this.connection.setAutoCommit(false);
        return this;
    }

    @Override
    public Session commit() throws SQLException {
        this.connection.commit();
        this.connection.setAutoCommit(true);
        return this;
    }

    @Override
    public Session rollback() throws SQLException {
        this.connection.rollback();
        return this;
    }

    @Override
    public <T> DataAccessObject<T> dao(Class<T> entityClass) {
        return DataAccessObject.dao(entityClass);
    }

    @Override
    public void close() throws IOException {
        if (this.connection != null){
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        SessionFactory.close();
    }
}
