package com.rnkrsoft.util;

/**
 * Created by woate on 2020/02/24.
 * 类工具
 */
public class ClassUtils {
    /**
     * 获取调用类的类名
     *
     * @return 调用类的类名
     */
    public static String getCallerClassName() {
        return getCallerClassName(false, 0);
    }

    /**
     * 获取调用当前调用该方法的上一个方法的方法完整名称
     *
     * @return 方法完整名称， 方法名用#号分割
     */
    public static String getCallerMethodName() {
        return getCaller(false, 0, true, true);
    }

    /**
     * 获取调用类的类名
     *
     * @param simple 是否为简写
     * @param offset 偏移，用于获取更上一级的调用类
     * @return 类名
     */
    public static String getCallerClassName(boolean simple, int offset) {
        return getCaller(simple, offset, false, false);
    }

    /**
     * 获取当前的方法名
     *
     * @param simple 是否为简写
     * @param offset 偏移，用于获取更上一级的调用类
     * @param method 是否包含方法名
     * @return
     */
    public static String getCaller(boolean simple, int offset, boolean method, boolean lineNumber) {
        String name;
        StackTraceElement[] stackTraceElement = new Throwable().getStackTrace();
        for (StackTraceElement stack : stackTraceElement) {
            name = stack.getClassName();
            if (name.equals(ClassUtils.class.getName())) {
                continue;
            }
            if (offset > 0) {
                offset--;
                continue;
            }
            //如果获取简写类名
            if (simple) {
                name = getSimpleName(name);
            }
            if (method) {
                name = name + "." + stack.getMethodName();
            }
            if (lineNumber) {
                name = name + ":" + stack.getLineNumber();
            }
            return name;
        }
        return null;
    }


    /**
     * 获取类名的简写
     *
     * @param className 类名
     * @return
     */
    static String getSimpleName(String className) {
        String[] names = className.split("\\.");
        return names[names.length - 1];
    }

}