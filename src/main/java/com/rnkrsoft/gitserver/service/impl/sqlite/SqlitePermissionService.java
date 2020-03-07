package com.rnkrsoft.gitserver.service.impl.sqlite;

import com.rnkrsoft.gitserver.http.AjaxRequest;
import com.rnkrsoft.gitserver.http.AjaxResponse;
import com.rnkrsoft.gitserver.service.PermissionService;
import com.rnkrsoft.gitserver.service.domain.GetOwnPermissionUserDetailRequest;
import com.rnkrsoft.gitserver.service.domain.GetOwnPermissionUserDetailResponse;
import com.rnkrsoft.gitserver.service.domain.ListRepositoryPermissionUserRequest;
import com.rnkrsoft.gitserver.service.domain.ListRepositoryPermissionUserResponse;

public class SqlitePermissionService implements PermissionService {

    @Override
    public AjaxResponse<ListRepositoryPermissionUserResponse> listRepositoryPermissionUser(AjaxRequest<ListRepositoryPermissionUserRequest> ajaxRequest) {
        return null;
    }

    @Override
    public AjaxResponse<GetOwnPermissionUserDetailResponse> getOwnPermissionUserDetail(AjaxRequest<GetOwnPermissionUserDetailRequest> ajaxRequest) {
        return null;
    }
}
