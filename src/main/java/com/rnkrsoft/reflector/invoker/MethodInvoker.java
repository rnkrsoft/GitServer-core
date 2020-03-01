package com.rnkrsoft.reflector.invoker;

import com.rnkrsoft.reflector.type.ParameterType;
import com.rnkrsoft.reflector.type.ReturnType;

public interface MethodInvoker extends Invoker{
    /**
     * 方法返回类型
     *
     * @return 类型
     */
    ReturnType getReturn();

    /**
     * 入参类型
     * @return
     */
    ParameterType[] getParameters();
}
