package com.rnkrsoft.gitserver.entity;

public class RoleEntity {
    /**
     * 角色名称，显示为英文
     */
    String roleName;
    /**
     * 角色描述，显示为中文
     */
    String roleDesc;
    /**
     * 当前角色是否失效
     */
    boolean valid;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
