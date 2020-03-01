package com.rnkrsoft.orm.statement;

import com.rnkrsoft.interfaces.EnumIntegerCode;
/**
 * Created by woate on 2020/03/01.
 * ORM 语句类型
 */
public enum JdbcStatementType implements EnumIntegerCode {
    INSERT(1, "插入"),
    INSERT_SELECTIVE(2, "非NULL值插入"),
    DELETE_PRIMARY_KEY(3, "按主键字段删除"),
    DELETE_SELECTIVE(4, "按非NULL值组成条件删除"),
    UPDATE_PRIMARY_KEY(5, "按主键字段修改所有非主键字段值"),
    UPDATE_PRIMARY_KEY_SELECTIVE(6, "按主键字段修改所有非主键字段中非NULL的值"),
    SELECT_PRIMARY_KEY(7, "按主键字段获取记录"),
    SELECT_SELECTIVE(8, "按非NULL值组成条件查询记录"),
    COUNT_SELECTIVE(9, "按非NULL值组成条件统计条数"),
    PAGINATION_SELECT_SELECTIVE(10, "按非NULL值组成条件分页查询记录");
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
