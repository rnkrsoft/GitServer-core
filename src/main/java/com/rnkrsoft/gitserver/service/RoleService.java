package com.rnkrsoft.gitserver.service;

import com.rnkrsoft.gitserver.entity.RoleEntity;

import java.util.List;

public interface RoleService {

    /**
     * 增加角色
     * @param roleName 角色名称
     * @param roleDesc 角色描述
     */
    void addRole(String roleName, String roleDesc);

    /**
     * 删除角色
     * @param roleName 角色名称
     */
    void deleteRole(String roleName);

    /**
     * 修改角色
     * @param roleName 角色名称
     * @param roleDesc 角色描述
     * @param valid 是否无效
     */
    void updateRole(String roleName, String roleDesc, boolean valid);

    /**
     * 列出所有的角色
     * @return 角色列表
     */
    List<RoleEntity> listRoles();
}
