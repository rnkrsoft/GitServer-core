package com.rnkrsoft.logtrace;

/**
 * Created by rnkrsoft.com on 2017/1/6.
 * 日志跟踪异常
 */
public class TraceableRuntimeException extends RuntimeException {
    ErrorContext context;

    public TraceableRuntimeException(String code, String desc, String activity, String message, String solution, Throwable cause) {
        this(ErrorContextFactory.instance().code(code, desc).activity(activity).message(message).solution(solution).cause(cause));
    }

    public TraceableRuntimeException(ErrorContext context) {
        super((context = (context == null ? ErrorContextFactory.instance() : context)).toString(), context.getCause());
        this.context = context;
    }

    public ErrorContext getContext() {
        return context;
    }

    @Override
    public String toString() {
        return this.context.toString();
    }
}
