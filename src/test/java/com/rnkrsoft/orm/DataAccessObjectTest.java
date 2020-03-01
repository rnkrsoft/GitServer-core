package com.rnkrsoft.orm;

import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class DataAccessObjectTest {
    @Test
    public void createTable() {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.dao(DemoEntity.class);
        dao.createTable();
    }
    @Test
    public void dropTable() {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.dao(DemoEntity.class);
        dao.dropTable();
    }
    @Test
    public void insert() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
//        Orm.INSTANCE.executeUpdate("create table demo(name varchar(20), age int)");
        DataAccessObject<DemoEntity> dao = Orm.dao(DemoEntity.class);
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
       dao.insert(DemoEntity.builder().name(UUID.randomUUID().toString()).age(21).sex(1).build());
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
        List<DemoEntity> list = dao.selectSelective(DemoEntity.builder().age(21).build());
        System.out.println(list);
        System.out.println(list.size());
    }


    @Test
    public void querySelective() {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.dao(DemoEntity.class);
        Pagination<DemoEntity> pagination = new Pagination<DemoEntity>(10, 3, DemoEntity.builder().age(21).build());
        dao.querySelective(pagination);
        System.out.println(pagination.getCurPageNo());
        System.out.println(pagination.getPageNum());
        System.out.println(pagination.getTotal());
        System.out.println(pagination.getRecords().size());
    }

    @Test
    public void countSelective() {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.dao(DemoEntity.class);
        int count = dao.countSelective(DemoEntity.builder().age(21).build());
        System.out.println(count);
    }


}