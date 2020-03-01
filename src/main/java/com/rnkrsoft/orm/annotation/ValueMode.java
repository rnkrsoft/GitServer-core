package com.rnkrsoft.orm.annotation;

import com.rnkrsoft.interfaces.EnumStringCode;

/**
 * Created by woate on 2018/6/3.
 */
public enum ValueMode implements EnumStringCode {
    EQ("=", "等于"),
    LT("<", "小于"),
    LTE("<=", "小于等于"),
    GT(">", "大于"),
    GTE(">=", "大于等于"),
    NE("<>", "不等于"),
    LIKE("like", "模糊查询"),
    IN("in", "在集合里"),
    NOT_IN("not in", "不在集合里"),
    NONE("", "无");
    String code;
    String desc;

    ValueMode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
