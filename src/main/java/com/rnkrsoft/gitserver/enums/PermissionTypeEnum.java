package com.rnkrsoft.gitserver.enums;

import com.rnkrsoft.interfaces.EnumIntegerCode;

public enum PermissionTypeEnum implements EnumIntegerCode {
    CREATE_REPOSITORY(101, "创建仓库"),
    UPDATE_REPOSITORY(102, "修改仓库"),
    DELETE_REPOSITORY(103, "删除仓库"),
    PUSH_REPOSITORY(104, "推送仓库"),
    PULL_REPOSITORY(105, "拉取仓库"),
    CREATE_USER(201, "创建用户"),
    UPDATE_USER(202, "创建用户"),
    DELETE_USER(203, "创建用户"),
    QUERY_USER(204, "查询用户"),
    GRANT_PERMISSION(301, "对用户赋予权限"),
    REVOKE_PERMISSION(302, "收回对用户赋予的权限");

    int code;
    String desc;

    PermissionTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
