package com.rnkrsoft.gitserver.service.impl.sqlite;

import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.gitserver.http.AjaxRequest;
import com.rnkrsoft.gitserver.http.AjaxResponse;
import com.rnkrsoft.gitserver.service.UserService;
import com.rnkrsoft.gitserver.service.domain.*;
import com.rnkrsoft.litebatis.LiteBatis;
import com.rnkrsoft.litebatis.condition.JdbcCondition;
import com.rnkrsoft.litebatis.context.LiteBatisContext;
import com.rnkrsoft.litebatis.dao.DataAccessObject;
import com.rnkrsoft.litebatis.entity.Pagination;
import com.rnkrsoft.litebatis.session.Session;

import java.util.List;

public class SqliteUserService implements UserService {

    @Override
    public AjaxResponse<RegisterUserResponse> registerUser(AjaxRequest<RegisterUserRequest> ajaxRequest) {
        RegisterUserRequest request = ajaxRequest.getData();
        Session session = LiteBatis.session();
        DataAccessObject<UserEntity, String> userDao = session.dao(UserEntity.class);
        userDao.insert(UserEntity.builder().username(request.getUsername()).email(request.getEmail()).password(request.getPasswordSha1()).valid(true).build());
        RegisterUserResponse response = new RegisterUserResponse();
        return new AjaxResponse<RegisterUserResponse>(response);
    }

    @Override
    public AjaxResponse<UpdateUserResponse> updateUser(AjaxRequest<UpdateUserRequest> ajaxRequest) {
        UpdateUserRequest request = ajaxRequest.getData();
        Session session = LiteBatis.session();
        DataAccessObject<UserEntity, String> userDao = session.dao(UserEntity.class);
        userDao.updateByPrimaryKeySelective(UserEntity.builder().username(request.getUsername()).email(request.getEmail()).valid(request.getValid()).password(request.getPassword()).build());
        UpdateUserResponse response = new UpdateUserResponse();
        return new AjaxResponse<UpdateUserResponse>(response);
    }

    @Override
    public AjaxResponse<DeleteUserResponse> deleteUser(AjaxRequest<DeleteUserRequest> ajaxRequest) {
        DeleteUserRequest request = ajaxRequest.getData();
        Session session = LiteBatis.session();
        DataAccessObject<UserEntity, String> userDao = session.dao(UserEntity.class);
        userDao.deleteByPrimaryKey(request.getUsername());
        DeleteUserResponse response = new DeleteUserResponse();
        return new AjaxResponse<DeleteUserResponse>(response);
    }

    @Override
    public AjaxResponse<ListUsersResponse> listUsers(AjaxRequest<ListUsersRequest> ajaxRequest) {
        ListUsersRequest request = ajaxRequest.getData();
        Session session = LiteBatis.session();
        DataAccessObject<UserEntity, String> userDao = session.dao(UserEntity.class);
        List<UserEntity> userEntities = userDao.selectSelective(UserEntity.builder().build());
        ListUsersResponse response = new ListUsersResponse();
        for (UserEntity userEntity : userEntities){
            response.getUsers().add(userEntity.getUsername());
        }
        return new AjaxResponse<ListUsersResponse>(response);
    }

    @Override
    public AjaxResponse<QueryUsersResponse> queryUsers(AjaxRequest<QueryUsersRequest> ajaxRequest) {
        QueryUsersRequest request = ajaxRequest.getData();
        Session session = LiteBatis.session();
        DataAccessObject<UserEntity,String> userDao = session.dao(UserEntity.class);
        JdbcCondition.JdbcConditionBuilder conditionBuilder = JdbcCondition.builder();
        if (request.isUserNameLike()){
            conditionBuilder.andLike("username");
        }
        Pagination<UserEntity> pagination = userDao.querySelective(LiteBatisContext.builder().condition(conditionBuilder.build()).build(), new Pagination<UserEntity>(request.getPageSize(), request.getPageNo(), UserEntity.builder().username(request.getUserName()).build()));
        QueryUsersResponse response = new QueryUsersResponse();
        response.setTotal(pagination.getTotal());
        response.setPageNo(pagination.getCurPageNo());
        for (UserEntity userEntity : pagination.getRecords()){
            response.addRecord(new QueryUsersResponse.Record(userEntity.getUsername(), userEntity.getEmail(), Boolean.toString(userEntity.getValid())));
        }
        return new AjaxResponse<QueryUsersResponse>(response);
    }
}
