package com.rnkrsoft.gitserver.service.impl.sqlite;

import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.gitserver.service.UserService;
import com.rnkrsoft.gitserver.service.domain.QueryUsersRequest;
import com.rnkrsoft.gitserver.service.domain.QueryUsersResponse;
import com.rnkrsoft.gitserver.service.domain.UpdateUserRequest;

import java.util.List;

public class SqliteUserService implements UserService {

    @Override
    public void registerUser(String username, String email, String passwordSha1) {

    }

    @Override
    public void updateUser(UpdateUserRequest request) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public List<UserEntity> listUsers() {
        return null;
    }

    @Override
    public QueryUsersResponse queryUsers(QueryUsersRequest request) {
        return null;
    }

    @Override
    public boolean hasUser(String username) {
        return false;
    }

    @Override
    public boolean hasAuthority(String username, String passwordSha1) {
        return false;
    }
}
