package com.rnkrsoft.orm;

import com.rnkrsoft.gitserver.entity.PermissionEntity;
import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.orm.annotation.*;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.metadata.TableMetadata;
import lombok.Data;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class OrmTest {

    @Test
    public void scan() {
        Orm.init(OrmSetting.builder()
                .entityClass(UserEntity.class, PermissionEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<UserEntity> dao = Orm.dao(UserEntity.class);
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
//        Orm.INSTANCE.executeUpdate("create table demo(name varchar(20), age int)");
//        Orm.INSTANCE.executeUpdate("insert into demo(name, age) values('1234', 20)");
    }

    @Test
    public void executeQuery() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        Orm orm = Orm.INSTANCE;
        TableMetadata tableMetadata = orm.tableMetadataMap.get(DemoEntity.class);
//        List<DemoEntity> list = orm.executeQuery(new ColumnMetadata[]{tableMetadata.getColumn("name")},"select name, age from demo where name = ?", new Orm.RowMapper<DemoEntity>() {
//            @Override
//            public DemoEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
//                DemoEntity entity = new DemoEntity();
//                entity.name = rs.getString("name");
//                entity.age = rs.getInt("age");
//                return entity;
//            }
//        }, "1234");
//        System.out.println(list);

//        List<DemoEntity> list2 = dao.executeQuery("select name, age from demo", new Orm.RowMapper<DemoEntity>() {
//            @Override
//            public DemoEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
//                DemoEntity entity = new DemoEntity();
//                entity.name = rs.getString("name");
//                entity.age = rs.getInt("age");
//                return entity;
//            }
//        });
//        System.out.println(list2);

    }
}