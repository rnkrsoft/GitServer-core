package com.rnkrsoft.logtrace;

import java.lang.*;

/**
 * Created by rnkrsoft.com on 2017/1/6.
 */
public class SubError implements Error {
    /**
     * 错误码
     */
    String code;
    /**
     * 错误码描述
     */
    PlaceHolder desc = null;

    public SubError(String code, String format, Object ... args) {
        this.code = code;
        this.desc = new PlaceHolder(format, args);
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc.toString();
    }
}
