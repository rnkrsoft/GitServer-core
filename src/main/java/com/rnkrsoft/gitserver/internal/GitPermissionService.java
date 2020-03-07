package com.rnkrsoft.gitserver.internal;

import com.rnkrsoft.gitserver.enums.PermissionTypeEnum;

/**
 * Created by test on 2020/3/5.
 */
public interface GitPermissionService {
    /**
     * 检查当前用户是否拥有指定仓库对应操作是否拥有权限
     *
     * @param repositoryName 仓库名称
     * @param username       用户名
     * @param permissionType   权限类型
     * @return 返回真拥有权限
     */
    boolean hasPermission(String repositoryName, String username, PermissionTypeEnum permissionType);

    /**
     * 赋予指定仓库指定用户权限
     *
     * @param repositoryName 仓库名称
     * @param username       用户名
     * @param operates       操作类型
     */
    void grantPermission(String repositoryName, String username, PermissionTypeEnum... operates);

    /**
     * 撤销指定仓库指定用户权限
     *
     * @param permissionId 权限编号
     */
    void revokePermission(String permissionId);

}
