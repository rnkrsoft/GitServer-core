package com.rnkrsoft.orm;

import com.rnkrsoft.gitserver.entity.PermissionEntity;
import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.orm.jdbc.executor.SimpleJdbcExecutor;
import com.rnkrsoft.orm.metadata.TableMetadata;
import com.rnkrsoft.orm.dao.DataAccessObject;
import org.junit.Test;

import java.sql.SQLException;

public class OrmTest {

    @Test
    public void scan() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(UserEntity.class, PermissionEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<UserEntity> dao = Orm.session().dao(UserEntity.class);
        dao.insert(new UserEntity("test1", "", "", false));
    }

    @Test
    public void dao() {
    }

    @Test
    public void executeUpdate() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(UserEntity.class, PermissionEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        new SimpleJdbcExecutor().executeUpdate(Orm.session(), "drop table demo");
    }
}