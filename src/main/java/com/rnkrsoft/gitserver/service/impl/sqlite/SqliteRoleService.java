package com.rnkrsoft.gitserver.service.impl.sqlite;

import com.rnkrsoft.gitserver.entity.RoleEntity;
import com.rnkrsoft.orm.Pagination;
import com.rnkrsoft.gitserver.service.RoleService;

import java.util.List;

public class SqliteRoleService implements RoleService {
    @Override
    public void addRole(String roleName, String roleDesc) {

    }

    @Override
    public void deleteRole(String roleName) {

    }

    @Override
    public void updateRole(String roleName, String roleDesc, boolean valid) {

    }

    @Override
    public List<RoleEntity> listRoles() {
        return null;
    }

    @Override
    public Pagination<RoleEntity> queryRoles(String roleName, boolean roleNameLike, int pageSize, int pageNo) {
        return null;
    }
}
