package com.rnkrsoft.gitserver.service.impl.sqlite;

import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.gitserver.http.AjaxRequest;
import com.rnkrsoft.gitserver.http.AjaxResponse;
import com.rnkrsoft.gitserver.service.UserService;
import com.rnkrsoft.gitserver.service.domain.*;
import com.rnkrsoft.litebatis.LiteBatis;
import com.rnkrsoft.litebatis.LiteBatisSetting;
import com.rnkrsoft.litebatis.dao.DataAccessObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by test on 2020/3/5.
 */
public class SqliteUserServiceTest {

    UserService userService = new SqliteUserService();

    @Test
    public void testRegisterUser() throws Exception {
        LiteBatis.init(LiteBatisSetting.builder()
                .entityClass(UserEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<UserEntity, String> dao = LiteBatis.session().dao(UserEntity.class);
        dao.dropTable();
        dao.createTable();
        RegisterUserRequest request = RegisterUserRequest.builder().username("test1").email("woate@qq.com").passwordSha1("122345").build();
        AjaxResponse<RegisterUserResponse> ajaxResponse = userService.registerUser(new AjaxRequest<RegisterUserRequest>("", request));
        System.out.println(ajaxResponse.getData());
        System.out.println(dao.selectSelective(UserEntity.builder().build()));
    }

    @Test
    public void testUpdateUser() throws Exception {
        LiteBatis.init(LiteBatisSetting.builder()
                .entityClass(UserEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<UserEntity, String> dao = LiteBatis.session().dao(UserEntity.class);
        dao.dropTable();
        dao.createTable();
        RegisterUserRequest request = RegisterUserRequest.builder().username("test1").email("woate@qq.com").passwordSha1("122345").build();
        AjaxResponse<RegisterUserResponse> ajaxResponse = userService.registerUser(new AjaxRequest<RegisterUserRequest>("", request));

        userService.updateUser(new AjaxRequest<UpdateUserRequest>("", UpdateUserRequest.builder().username("test1").email("test@qq.com").build()));

        System.out.println(ajaxResponse.getData());
        System.out.println(dao.selectSelective(UserEntity.builder().build()));
    }

    @Test
    public void testDeleteUser() throws Exception {
        LiteBatis.init(LiteBatisSetting.builder()
                .entityClass(UserEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<UserEntity, String> dao = LiteBatis.session().dao(UserEntity.class);
        dao.dropTable();
        dao.createTable();
        RegisterUserRequest request = RegisterUserRequest.builder().username("test1").email("woate@qq.com").passwordSha1("122345").build();
        AjaxResponse<RegisterUserResponse> ajaxResponse = userService.registerUser(new AjaxRequest<RegisterUserRequest>("", request));
        System.out.println(dao.selectSelective(UserEntity.builder().build()));
        userService.deleteUser(new AjaxRequest<DeleteUserRequest>("", DeleteUserRequest.builder().username("test1").build()));

        System.out.println(dao.selectSelective(UserEntity.builder().build()));
    }

    @Test
    public void testListUsers() throws Exception {
        LiteBatis.init(LiteBatisSetting.builder()
                .entityClass(UserEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<UserEntity, String> dao = LiteBatis.session().dao(UserEntity.class);
        dao.dropTable();
        dao.createTable();
        RegisterUserRequest request = RegisterUserRequest.builder().username("test1").email("woate@qq.com").passwordSha1("122345").build();
         userService.registerUser(new AjaxRequest<RegisterUserRequest>("", request));
        AjaxResponse<ListUsersResponse> ajaxResponse = userService.listUsers(new AjaxRequest<ListUsersRequest>("", ListUsersRequest.builder().build()));
        System.out.println(ajaxResponse.getData());
    }

    @Test
    public void testQueryUsers() throws Exception {
        LiteBatis.init(LiteBatisSetting.builder()
                .entityClass(UserEntity.class)
                .jdbcDriverClassName("org.sqlite.JDBC")
                .jdbcUrl("jdbc:sqlite:sample.db")
                .build());
        DataAccessObject<UserEntity, String> dao = LiteBatis.session().dao(UserEntity.class);
        dao.dropTable();
        dao.createTable();
        for (int i = 0; i <11 ; i++) {
            RegisterUserRequest request = RegisterUserRequest.builder().username("test" + i).email("woate@qq.com").passwordSha1("122345").build();
            userService.registerUser(new AjaxRequest<RegisterUserRequest>("", request));
        }

        AjaxResponse<QueryUsersResponse> ajaxResponse = userService.queryUsers(new AjaxRequest<QueryUsersRequest>("", QueryUsersRequest.builder().pageSize(10).pageNo(2).build()));
        System.out.println(ajaxResponse.getData());
    }
}