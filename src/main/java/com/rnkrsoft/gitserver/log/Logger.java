package com.rnkrsoft.gitserver.log;

public interface Logger {
    void debug(String format, Throwable cause, Object... args);
    void debug(String format, Object... args);
    void info(String format, Throwable cause, Object...args);
    void info(String format, Object...args);
    void warn(String format, Throwable cause, Object...args);
    void warn(String format, Object...args);
    void error(String format, Throwable cause, Object...args);
    void error(String format, Object...args);
}
