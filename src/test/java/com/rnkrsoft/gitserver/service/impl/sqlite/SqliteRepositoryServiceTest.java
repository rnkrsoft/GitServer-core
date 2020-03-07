package com.rnkrsoft.gitserver.service.impl.sqlite;

import com.rnkrsoft.gitserver.GitServerFactory;
import com.rnkrsoft.gitserver.entity.PermissionEntity;
import com.rnkrsoft.gitserver.entity.RepositoryEntity;
import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.gitserver.enums.PermissionTypeEnum;
import com.rnkrsoft.gitserver.http.AjaxRequest;
import com.rnkrsoft.gitserver.http.AjaxResponse;
import com.rnkrsoft.gitserver.service.RepositoryService;
import com.rnkrsoft.gitserver.service.domain.*;
import com.rnkrsoft.litebatis.LiteBatis;
import com.rnkrsoft.litebatis.LiteBatisSetting;
import com.rnkrsoft.litebatis.dao.DataAccessObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by test on 2020/3/5.
 */
public class SqliteRepositoryServiceTest {
    RepositoryService repositoryService = new SqliteRepositoryService(GitServerFactory.getInstance());
    @Test
    public void testCreateRepository() throws Exception {
        LiteBatis.init(LiteBatisSetting.builder()
                .entityClass(UserEntity.class, RepositoryEntity.class, PermissionEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:./target/sample.db")
                .build());
        DataAccessObject<UserEntity, String> userDao = LiteBatis.session().dao(UserEntity.class);
        DataAccessObject<RepositoryEntity, Integer> repositoryDao = LiteBatis.session().dao(RepositoryEntity.class);
        DataAccessObject<PermissionEntity, Integer> permissionDao = LiteBatis.session().dao(PermissionEntity.class);
//        userDao.dropTable();
//        repositoryDao.dropTable();
//        permissionDao.dropTable();
        userDao.createTable();
        repositoryDao.createTable();
        permissionDao.createTable();
        for (int i = 0; i < 10; i++) {
            AjaxResponse<CreateRepositoryResponse> ajaxResponse = repositoryService.createRepository(new AjaxRequest<CreateRepositoryRequest>("", CreateRepositoryRequest.builder()
                    .repositoryName("test" + i)
                    .repositoryDesc("测试项目")
                    .owner("woate")
                    .build()));
            System.out.println(ajaxResponse);
        }

        System.out.println(repositoryDao.selectSelective(RepositoryEntity.builder().build()));
    }

    @Test
    public void testDeleteRepository() throws Exception {
        LiteBatis.init(LiteBatisSetting.builder()
                .entityClass(UserEntity.class, RepositoryEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:./target/sample.db")
                .build());
        DataAccessObject<UserEntity, String> userDao = LiteBatis.session().dao(UserEntity.class);
        DataAccessObject<RepositoryEntity, Integer> repositoryDao = LiteBatis.session().dao(RepositoryEntity.class);
        userDao.dropTable();
        userDao.createTable();
        repositoryDao.dropTable();
        repositoryDao.createTable();
        AjaxResponse<CreateRepositoryResponse> ajaxResponse = repositoryService.createRepository(new AjaxRequest<CreateRepositoryRequest>("", CreateRepositoryRequest.builder()
                .repositoryName("test")
                .repositoryDesc("测试项目")
                .owner("woate")
                .build()));
        System.out.println(ajaxResponse);

        System.out.println(repositoryService.deleteRepository(new AjaxRequest<DeleteRepositoryRequest>("", DeleteRepositoryRequest.builder().repositoryId(ajaxResponse.getData().getRepositoryId()).build())));
    }

    @Test
    public void testListRepositoriesName() throws Exception {
        LiteBatis.init(LiteBatisSetting.builder()
                .entityClass(UserEntity.class, RepositoryEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:./target/sample.db")
                .build());
        DataAccessObject<UserEntity,String> userDao = LiteBatis.session().dao(UserEntity.class);
        DataAccessObject<RepositoryEntity, Integer> repositoryDao = LiteBatis.session().dao(RepositoryEntity.class);
        userDao.dropTable();
        userDao.createTable();
        repositoryDao.dropTable();
        repositoryDao.createTable();
        for (int i = 0; i < 10; i++) {
            AjaxResponse<CreateRepositoryResponse> ajaxResponse = repositoryService.createRepository(new AjaxRequest<CreateRepositoryRequest>("", CreateRepositoryRequest.builder()
                    .repositoryName("test" + i)
                    .repositoryDesc("测试项目")
                    .owner("woate")
                    .build()));
            System.out.println(ajaxResponse);
        }
        System.out.println(repositoryService.listRepositoriesName(new AjaxRequest<ListRepositoriesNameRequest>("", new ListRepositoriesNameRequest())));
    }

    @Test
    public void testQueryRepository() throws Exception {
        LiteBatis.init(LiteBatisSetting.builder()
                .entityClass(UserEntity.class, RepositoryEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:./target/sample.db")
                .build());
        DataAccessObject<UserEntity, String> userDao = LiteBatis.session().dao(UserEntity.class);
        DataAccessObject<RepositoryEntity, Integer> repositoryDao = LiteBatis.session().dao(RepositoryEntity.class);
        userDao.dropTable();
        userDao.createTable();
        repositoryDao.dropTable();
        repositoryDao.createTable();
        for (int i = 0; i < 10; i++) {
            AjaxResponse<CreateRepositoryResponse> ajaxResponse = repositoryService.createRepository(new AjaxRequest<CreateRepositoryRequest>("", CreateRepositoryRequest.builder()
                    .repositoryName("test" + i)
                    .repositoryDesc("测试项目")
                    .owner("woate")
                    .build()));
            System.out.println(ajaxResponse);
        }
        AjaxResponse<QueryRepositoryResponse> ajaxResponse = repositoryService.queryRepository(new AjaxRequest<QueryRepositoryRequest>("", QueryRepositoryRequest.builder().pageSize(5).pageNo(1).build()));
        System.out.println(ajaxResponse);
    }
}