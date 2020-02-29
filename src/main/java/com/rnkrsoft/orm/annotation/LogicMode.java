package com.rnkrsoft.orm.annotation;

import com.rnkrsoft.interfaces.EnumStringCode;

/**
 * Created by rnkrsoft.com on 2018/6/3.
 */
public enum LogicMode implements EnumStringCode {
    AND("and", "且"),
    OR("or", "或");
    String code;
    String desc;
    LogicMode(String code, String desc){
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
