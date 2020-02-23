package com.rnkrsoft.gitserver.log;

import com.rnkrsoft.gitserver.utils.ClassUtils;

public abstract class AbstractLogger implements Logger{
    protected String getCaller(){
        return ClassUtils.getCallerClassName(false, 2);
    }
}
