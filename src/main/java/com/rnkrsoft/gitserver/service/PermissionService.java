package com.rnkrsoft.gitserver.service;


import com.rnkrsoft.gitserver.http.AjaxRequest;
import com.rnkrsoft.gitserver.http.AjaxResponse;
import com.rnkrsoft.gitserver.service.domain.GetOwnPermissionUserDetailRequest;
import com.rnkrsoft.gitserver.service.domain.GetOwnPermissionUserDetailResponse;
import com.rnkrsoft.gitserver.service.domain.ListRepositoryPermissionUserRequest;
import com.rnkrsoft.gitserver.service.domain.ListRepositoryPermissionUserResponse;

/**
 * 权限服务
 * Created by woate on 2020/02/24.
 */
public interface PermissionService {

    /**
     * 列出指定仓库的所有权限
     *
     * @param ajaxRequest 请求
     * @return 应答
     */
    AjaxResponse<ListRepositoryPermissionUserResponse> listRepositoryPermissionUser(AjaxRequest<ListRepositoryPermissionUserRequest> ajaxRequest);

    /**
     * 获取用户的所有权限详情
     * @param ajaxRequest 请求
     * @return 应答
     */
    AjaxResponse<GetOwnPermissionUserDetailResponse> getOwnPermissionUserDetail(AjaxRequest<GetOwnPermissionUserDetailRequest> ajaxRequest);
}
