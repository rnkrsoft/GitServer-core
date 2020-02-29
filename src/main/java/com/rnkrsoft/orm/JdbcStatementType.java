package com.rnkrsoft.orm;

import com.rnkrsoft.interfaces.EnumIntegerCode;

public enum JdbcStatementType implements EnumIntegerCode {
    INSERT(1, "插入"),
    INSERT_SELECTIVE(2, "插入"),
    DELETE_PRIMARY_KEY(3, "删除"),
    DELETE_SELECTIVE(4, "删除"),
    UPDATE_PRIMARY_KEY(5, "修改"),
    UPDATE_PRIMARY_KEY_SELECTIVE(6, "修改"),
    SELECT_PRIMARY_KEY(7, "查询"),
    SELECT_SELECTIVE(8, "查询");
    int code;
    String desc;

    JdbcStatementType(int code, String desc) {
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
