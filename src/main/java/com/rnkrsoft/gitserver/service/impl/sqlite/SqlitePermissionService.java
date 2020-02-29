package com.rnkrsoft.gitserver.service.impl.sqlite;

import com.rnkrsoft.gitserver.enums.PermissionEnum;
import com.rnkrsoft.gitserver.service.PermissionService;
import com.rnkrsoft.gitserver.service.domain.GetOwnPermissionUserDetailRequest;
import com.rnkrsoft.gitserver.service.domain.GetOwnPermissionUserDetailResponse;
import com.rnkrsoft.gitserver.service.domain.ListRepositoryPermissionUserRequest;
import com.rnkrsoft.gitserver.service.domain.ListRepositoryPermissionUserResponse;

public class SqlitePermissionService implements PermissionService {

    @Override
    public boolean hasPermission(String repositoryName, String username, String operate) {
        return false;
    }

    @Override
    public void grantPermission(String repositoryName, String username, PermissionEnum... operates) {

    }

    @Override
    public void revokePermission(String permissionId) {

    }

    @Override
    public ListRepositoryPermissionUserResponse listRepositoryPermissionUser(ListRepositoryPermissionUserRequest request) {
        return null;
    }

    @Override
    public GetOwnPermissionUserDetailResponse getOwnPermissionUserDetail(GetOwnPermissionUserDetailRequest request) {
        return null;
    }
}
