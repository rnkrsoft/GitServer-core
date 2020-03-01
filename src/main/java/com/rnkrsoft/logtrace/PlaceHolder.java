package com.rnkrsoft.logtrace;

import com.rnkrsoft.util.MessageFormatter;

/**
 * Created by woate on 2017/1/6.
 */
public class PlaceHolder {
    String format;
    Object[] args;

    PlaceHolder(String format, Object[] args) {
        this.format = format;
        this.args = args;
    }

    @Override
    public String toString() {
        return MessageFormatter.format((format == null ? "" : format), (args == null ? new Object[0] : args));
    }
}
