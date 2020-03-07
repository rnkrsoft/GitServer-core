package com.rnkrsoft.log.impl;

import com.rnkrsoft.litebatis.util.DateUtils;
import com.rnkrsoft.litebatis.util.StringUtils;
import com.rnkrsoft.log.AbstractLogger;
import com.rnkrsoft.util.MessageFormatter;

public class StdoutLogger extends AbstractLogger {
    public static final String FORMAT = "{} {} - {}";
    public static final String FORMAT_EXCEPTION = "{} {} - {} \n{}";

    @Override
    public void debug(String format, Throwable cause, Object... args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        print(0, MessageFormatter.format(FORMAT_EXCEPTION, DateUtils.sysdate(), caller, msg, StringUtils.toString(cause)));
    }

    @Override
    public void debug(String format, Object[] args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        print(0, MessageFormatter.format(FORMAT, DateUtils.sysdate(), caller, msg));
    }

    @Override
    public void info(String format, Throwable cause, Object... args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        print(0, MessageFormatter.format(FORMAT_EXCEPTION, DateUtils.sysdate(), caller, msg, StringUtils.toString(cause)));
    }

    @Override
    public void info(String format, Object[] args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        print(0, MessageFormatter.format(FORMAT, DateUtils.sysdate(), caller, msg));
    }

    @Override
    public void warn(String format, Throwable cause, Object... args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        print(0, MessageFormatter.format(FORMAT_EXCEPTION, DateUtils.sysdate(), caller, msg, StringUtils.toString(cause)));
    }

    @Override
    public void warn(String format, Object[] args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        print(0, MessageFormatter.format(FORMAT, DateUtils.sysdate(), caller, msg));
    }

    @Override
    public void error(String format, Throwable cause, Object... args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        print(0, MessageFormatter.format(FORMAT_EXCEPTION, DateUtils.sysdate(), caller, msg, StringUtils.toString(cause)));
    }

    @Override
    public void error(String format, Object[] args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        print(0, MessageFormatter.format(FORMAT, DateUtils.sysdate(), caller, msg));
    }

    void print(int level, String message) {
        System.out.println(message);
    }
}
