package com.rnkrsoft.gitserver.service;


import com.rnkrsoft.gitserver.enums.PermissionEnum;
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
     * 检查当前用户是否拥有指定仓库对应操作是否拥有权限
     *
     * @param repositoryName 仓库名称
     * @param username       用户名
     * @param operate        操作类型
     * @return 返回真拥有权限
     */
    boolean hasPermission(String repositoryName, String username, String operate);

    /**
     * 赋予指定仓库指定用户权限
     *
     * @param repositoryName 仓库名称
     * @param username       用户名
     * @param operates       操作类型
     */
    void grantPermission(String repositoryName, String username, PermissionEnum... operates);

    /**
     * 撤销指定仓库指定用户权限
     *
     * @param permissionId 权限编号
     */
    void revokePermission(String permissionId);

    /**
     * 列出指定仓库的所有权限
     *
     * @param request 请求
     * @return 应答
     */
    ListRepositoryPermissionUserResponse listRepositoryPermissionUser(ListRepositoryPermissionUserRequest request);

    /**
     * 获取用户的所有权限详情
     *
     * @return 应答
     */
    GetOwnPermissionUserDetailResponse getOwnPermissionUserDetail(GetOwnPermissionUserDetailRequest request);
}
