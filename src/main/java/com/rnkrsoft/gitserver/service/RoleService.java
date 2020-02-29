package com.rnkrsoft.gitserver.service;

import com.rnkrsoft.gitserver.entity.RoleEntity;
import com.rnkrsoft.orm.Pagination;

import java.util.List;
/**
 * 角色服务
 * Created by woate on 2020/02/24.
 */
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

    /**
     * 分页查询角色列表
     * @param roleName 角色名
     * @param roleNameLike 角色名模糊查询
     * @param pageSize 分页大小
     * @param pageNo 当前页
     * @return 分页对象
     */
    Pagination<RoleEntity> queryRoles(String roleName, boolean roleNameLike, int pageSize, int pageNo);
}
