package com.rnkrsoft.reflector.invoker;

public interface Invoker {
    /**
     * 执行包装的方法
     *
     * @param target 目标对象
     * @param args   参数数组
     * @param <T>
     * @return 执行结果
     * @throws Exception 异常
     */
    <T> T invoke(Object target, Object... args) throws Exception;
}
