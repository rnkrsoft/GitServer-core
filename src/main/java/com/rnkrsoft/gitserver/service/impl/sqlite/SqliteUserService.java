package com.rnkrsoft.gitserver.service.impl.sqlite;

import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.gitserver.service.UserService;
import com.rnkrsoft.gitserver.service.domain.QueryUsersRequest;
import com.rnkrsoft.gitserver.service.domain.QueryUsersResponse;
import com.rnkrsoft.gitserver.service.domain.UpdateUserRequest;
import com.rnkrsoft.orm.Orm;
import com.rnkrsoft.orm.condition.JdbcCondition;
import com.rnkrsoft.orm.dao.DataAccessObject;
import com.rnkrsoft.orm.entity.Pagination;
import com.rnkrsoft.orm.session.Session;

import java.util.List;

public class SqliteUserService implements UserService {

    @Override
    public void registerUser(String username, String email, String passwordSha1) {
        Session session = Orm.session();
        DataAccessObject<UserEntity> userDao = session.dao(UserEntity.class);
        userDao.insert(UserEntity.builder().username(username).email(email).password(passwordSha1).valid(true).build());
    }

    @Override
    public void updateUser(UpdateUserRequest request) {
        Session session = Orm.session();
        DataAccessObject<UserEntity> userDao = session.dao(UserEntity.class);
        userDao.updateByPrimaryKeySelective(UserEntity.builder().username(request.getUsername()).email(request.getEmail()).valid(request.getValid()).password(request.getPassword()).build());
    }

    @Override
    public void deleteUser(String username) {
        Session session = Orm.session();
        DataAccessObject<UserEntity> userDao = session.dao(UserEntity.class);
        userDao.deleteByPrimaryKey(UserEntity.builder().username(username).build());
    }

    @Override
    public List<UserEntity> listUsers() {
        Session session = Orm.session();
        DataAccessObject<UserEntity> userDao = session.dao(UserEntity.class);
        return userDao.selectSelective(UserEntity.builder().build());
    }

    @Override
    public QueryUsersResponse queryUsers(QueryUsersRequest request) {
        Session session = Orm.session();
        DataAccessObject<UserEntity> userDao = session.dao(UserEntity.class);
        JdbcCondition.JdbcConditionBuilder conditionBuilder = JdbcCondition.builder();
        if (request.isUserNameLike()){
            conditionBuilder.andLike("username");
        }
        Pagination<UserEntity> pagination = userDao.querySelective(new Pagination<UserEntity>(request.getPageSize(), request.getPageNo(), UserEntity.builder().username(request.getUserName()).build()), conditionBuilder.build());
        QueryUsersResponse response = new QueryUsersResponse();
        response.setTotal(pagination.getTotal());
        response.setPageNo(pagination.getCurPageNo());
        for (UserEntity userEntity : pagination.getRecords()){
            response.addRecord(new QueryUsersResponse.Record(userEntity.getUsername(), userEntity.getEmail(), Boolean.toString(userEntity.isValid())));
        }
        return response;
    }

    @Override
    public boolean hasUser(String username) {
        Session session = Orm.session();
        DataAccessObject<UserEntity> userDao = session.dao(UserEntity.class);
        return userDao.countSelective(UserEntity.builder().username(username).build()) > 0;
    }

    @Override
    public boolean hasAuthority(String username, String passwordSha1) {
        Session session = Orm.session();
        DataAccessObject<UserEntity> userDao = session.dao(UserEntity.class);
        UserEntity userEntity = userDao.selectByPrimaryKey(UserEntity.builder().username(username).build());
        if (userEntity == null) {
            return false;
        }
        return passwordSha1.equals(userEntity.getPassword());
    }
}
