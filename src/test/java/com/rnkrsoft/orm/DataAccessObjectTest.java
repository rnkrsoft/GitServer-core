package com.rnkrsoft.orm;

import com.rnkrsoft.orm.condition.JdbcCondition;
import com.rnkrsoft.orm.entity.Pagination;
import com.rnkrsoft.orm.dao.DataAccessObject;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DataAccessObjectTest {
    @Test
    public void createTable() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.session().dao(DemoEntity.class);
        dao.createTable();
    }

    @Test
    public void dropTable() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.session().dao(DemoEntity.class);
        dao.dropTable();
    }

    @Test
    public void insert() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.session().dao(DemoEntity.class);
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
    public void insertSelective() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.session().dao(DemoEntity.class);
        for (int i = 0; i < 16; i++) {
            DemoEntity demoEntity = DemoEntity.builder().name(UUID.randomUUID().toString()).age(i).sex(2).build();
            demoEntity.setCreateDate(new Date());
            demoEntity.setLastUpdateDate(new Date());
            dao.insertSelective(demoEntity);
        }
    }

    @Test
    public void updateByPrimaryKeySelective() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.session().dao(DemoEntity.class);
        int cnt = dao.updateByPrimaryKeySelective(DemoEntity.builder().name("1234").age(123456).build());
        System.out.println(cnt);
    }

    @Test
    public void updateByPrimaryKey() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao =Orm.session().dao(DemoEntity.class);
        int cnt = dao.updateByPrimaryKey(DemoEntity.builder().name("1234").age(123456).build());
        System.out.println(cnt);
    }

    @Test
    public void deleteByPrimaryKey() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.session().dao(DemoEntity.class);
        int cnt = dao.deleteByPrimaryKey(DemoEntity.builder().name("1234").build());
        System.out.println(cnt);
    }

    @Test
    public void selectByPrimaryKey() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.session().dao(DemoEntity.class);
        DemoEntity entity = dao.selectByPrimaryKey(DemoEntity.builder().name("1234").build());
        System.out.println(entity);
    }

    @Test
    public void selectSelective() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao =Orm.session().dao(DemoEntity.class);
        List<DemoEntity> list = dao.selectSelective(DemoEntity.builder().sex(1).name("%1%").build(), JdbcCondition.builder().andLike("name").andEq("sex").build());
        for (DemoEntity demoEntity : list){
            System.out.println(demoEntity);
        }
        System.out.println(list.size());
    }


    @Test
    public void querySelective() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.session().dao(DemoEntity.class);
        Pagination<DemoEntity> pagination = new Pagination<DemoEntity>(10, 1, DemoEntity.builder().age(21).build());
        dao.querySelective(pagination, JdbcCondition.builder().orEq("name").orEq("age").build());
        System.out.println(pagination.getCurPageNo());
        System.out.println(pagination.getPageNum());
        System.out.println(pagination.getTotal());
        System.out.println(pagination.getRecords().size());
    }

    @Test
    public void countSelective() throws SQLException {
        Orm.init(OrmSetting.builder()
                .entityClass(DemoEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<DemoEntity> dao = Orm.session().dao(DemoEntity.class);
        int count = dao.countSelective(DemoEntity.builder().sex(1).name("%1").build(), JdbcCondition.builder().andLike("name").andEq("sex").build());
        System.out.println(count);
    }


}