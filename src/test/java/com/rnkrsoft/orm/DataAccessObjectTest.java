package com.rnkrsoft.orm;

import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class DataAccessObjectTest {

    @Test
    public void insert() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
//        Orm.INSTANCE.executeUpdate("create table demo(name varchar(20), age int)");
        DataAccessObject<DemoEntity> dao = Orm.dao(DemoEntity.class);
        int cnt =dao.insert(DemoEntity.builder().name("1234").age(21).build());
        System.out.println(cnt);
    }

    @Test
    public void insertSelective() {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
//        Orm.INSTANCE.executeUpdate("create table demo(name varchar(20), age int)");
        DataAccessObject<DemoEntity> dao = Orm.dao(DemoEntity.class);
        int cnt = dao.insertSelective(DemoEntity.builder().name("1234").age(21).build());
        System.out.println(cnt);
    }

    @Test
    public void updateByPrimaryKeySelective() {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.dao(DemoEntity.class);
        int cnt = dao.updateByPrimaryKeySelective(DemoEntity.builder().name("1234").age(123456).build());
        System.out.println(cnt);
    }

    @Test
    public void updateByPrimaryKey() {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.dao(DemoEntity.class);
        int cnt = dao.updateByPrimaryKey(DemoEntity.builder().name("1234").age(123456).build());
        System.out.println(cnt);
    }

    @Test
    public void deleteByPrimaryKey() {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.dao(DemoEntity.class);
        int cnt = dao.deleteByPrimaryKey(DemoEntity.builder().name("1234").build());
        System.out.println(cnt);
    }

    @Test
    public void selectByPrimaryKey() {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.dao(DemoEntity.class);
        DemoEntity entity = dao.selectByPrimaryKey(DemoEntity.builder().name("1234").build());
        System.out.println(entity);
    }

    @Test
    public void selectSelective() {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.dao(DemoEntity.class);
        List<DemoEntity> list = dao.selectSelective(DemoEntity.builder().name("1234").build());
        System.out.println(list);
    }
}