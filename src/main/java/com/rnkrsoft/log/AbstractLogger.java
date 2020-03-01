package com.rnkrsoft.log;

import com.rnkrsoft.util.ClassUtils;

public abstract class AbstractLogger implements Logger {
    protected String getCaller() {
        return ClassUtils.getCallerClassName(false, 2);
    }
}
