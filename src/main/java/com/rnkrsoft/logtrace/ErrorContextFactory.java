package com.rnkrsoft.logtrace;

/**
 * Created by woate on 2017/1/6.
 * 错误上下文工厂
 */
public class ErrorContextFactory {
    static ThreadLocal<ErrorContext> CTX = new ThreadLocal();

    /**
     * 获取当前线程上下文
     *
     * @return 上下文中
     */
    public static ErrorContext instance() {
        ErrorContext ctx = CTX.get();
        if (ctx == null) {
            ctx = new SimpleErrorContext();
            CTX.set(ctx);
        }
        return ctx;
    }

    /**
     * 保存当前的上下问到一个新的上下文中
     *
     * @return 新的上下文中
     */
    public static ErrorContext store() {
        ErrorContext ctx = ErrorContextFactory.instance();
        ErrorContext newCtx = new SimpleErrorContext();
        newCtx.stored(ctx);
        CTX.set(newCtx);
        return CTX.get();
    }

    /**
     * 重新调用当前保存的上下文对象
     *
     * @return 上下文对象
     */
    public static ErrorContext recall() {
        ErrorContext ctx = ErrorContextFactory.instance();
        if (ctx.stored() != null) {
            CTX.set(ctx.stored());
        }
        return CTX.get();
    }
}
