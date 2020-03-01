package com.rnkrsoft.reflector;

import com.rnkrsoft.logtrace.ErrorContextFactory;
import com.rnkrsoft.reflector.invoker.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.*;

public class Reflector {
    Class clazz;
    /**
     * 无参构造函数对象
     */
    Constructor<?> noArgsConstructor;

    public Reflector(Class clazz) {
        this.clazz = clazz;
        addNoArgsConstructor(this.clazz);
        addGetterMethods(this.clazz);
    }
    boolean canAccessPrivateMethods() {
        try {
            SecurityManager securityManager = System.getSecurityManager();
            if (null != securityManager) {
                securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
            }
        } catch (SecurityException e) {
            return false;
        }
        return true;
    }

    void addNoArgsConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        boolean found = false;
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                if (canAccessPrivateMethods()) {
                    try {
                        constructor.setAccessible(true);
                    } catch (Exception e) {
                        // Ignored. This is only a final precaution, nothing we can do.
                    }
                }

                if (constructor.isAccessible()) {
                    this.noArgsConstructor = constructor;
                }
                found = true;
            }
        }
    }
    /**
     * 将方法对象生成字符串形式
     * @param method 方法对象
     * @return 方法名字符串形式
     */
    String getSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        Class<?> returnType = method.getReturnType();
        if (returnType != null) {
            sb.append(returnType.getName()).append('#');
        }
        sb.append(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            if (i == 0) {
                sb.append(':');
            } else {
                sb.append(',');
            }
            sb.append(parameters[i].getName());
        }
        return sb.toString();
    }
    /**
     * 处理完成后的uniqueMethods中只存放着唯一的方法对象
     * @param uniqueMethods 类中唯一的方法对象
     * @param methods 所有当前类及其父类的方法
     */
    void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] methods) {
        for (Method currentMethod : methods) {
            //如果遍历的方法不为桥接方法，则根据方法签字（返回类型#方法名:参数类型1 参数类型n）为唯一键，进行方法的处理
            //如果子类已经覆盖定义的方法，则使用子类的方法
            if (!currentMethod.isBridge()) {
                String signature = getSignature(currentMethod);
                if (!uniqueMethods.containsKey(signature)) {
                    if (canAccessPrivateMethods()) {
                        try {
                            currentMethod.setAccessible(true);
                        } catch (Exception e) {
                            // Ignored. This is only a final precaution, nothing we can do.
                        }
                    }
                    uniqueMethods.put(signature, currentMethod);
                }
            }
        }
    }
    /**
     * 获取当前类及其父类所有的方法
     * @param clazz
     * @return
     */
    Method[] getClassMethods(Class<?> clazz) {
        Map<String, Method> uniqueMethods = new LinkedHashMap<String, Method>();
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());
            //遍历所有接口
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                addUniqueMethods(uniqueMethods, anInterface.getMethods());
            }
            currentClass = currentClass.getSuperclass();
        }

        Collection<Method> methods = uniqueMethods.values();

        return methods.toArray(new Method[methods.size()]);
    }


    void addMethodConflict(Map<String, List<Method>> conflictingMethods, String name, Method method) {
        //存放的存在冲突的方法列表
        List<Method> methods = conflictingMethods.get(name);
        if (methods == null) {
            methods = new ArrayList<Method>();
            conflictingMethods.put(name, methods);
        }
        methods.add(method);
    }

    /**
     * 调用此方法存放最终的Getter方法对象
     * @param fieldName 字段名
     * @param method 方法对象
     */
    void addGetMethod(String fieldName, Method method){
        //fixme
    }
    void resolveGetterConflicts(Map<String, List<Method>> conflictingGetters) {
        for (String propName : conflictingGetters.keySet()) {
            List<Method> getters = conflictingGetters.get(propName);
            Iterator<Method> iterator = getters.iterator();
            Method firstMethod = iterator.next();
            if (getters.size() == 1) {
                addGetMethod(propName, firstMethod);
            } else {
                Method getter = firstMethod;
                Class<?> getterType = firstMethod.getReturnType();
                while (iterator.hasNext()) {
                    Method method = iterator.next();
                    Class<?> methodType = method.getReturnType();
                    if (methodType.equals(getterType)) {
                        throw ErrorContextFactory.instance().message("类'{}'中存在冲突的Getter方法的重载方法'{}'.不符合JavaBean的规范", firstMethod.getDeclaringClass(), propName).runtimeException();
                    } else if (methodType.isAssignableFrom(getterType)) {
                        // OK getter type is descendant
                    } else if (getterType.isAssignableFrom(methodType)) {
                        getter = method;
                        getterType = methodType;
                    } else {
                        throw ErrorContextFactory.instance().message("类'{}'中存在冲突的Getter方法的重载方法'{}'.不符合JavaBean的规范", firstMethod.getDeclaringClass(), propName).runtimeException();
                    }
                }
                addGetMethod(propName, getter);
            }
        }
    }

    void addGetterMethods(Class<?> clazz){
        //先存放会冲突的Getter方法
        Map<String, List<Method>> conflictingGetters = new LinkedHashMap<String, List<Method>>();
        Method[] methods = getClassMethods(clazz);
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("get") || name.startsWith("is")) {
                if (IGNORE_FIELD.contains(name)) {
                    continue;
                }
                //GETTER为无参
                if (method.getParameterTypes().length == 0) {
                    addMethodConflict(conflictingGetters, name, method);
                }
            }
        }
        //相同方法名按照子类覆盖的方法原则保存到Getter数组中
        resolveGetterConflicts(conflictingGetters);
    }

    /**
     * 获取所有字段，如果子类和父类有相同的字段名，则采用覆盖原则，直接使用子类的字段
     * @return
     */
    public Field[] getFields(){
        return null;
    }
    public MethodInvoker[] getMethods(){
        return null;
    }
    public MethodInvoker getMethod(String methodName){
        return null;
    }


    static final Set<String> IGNORE_FIELD = new HashSet();
    static final Set<Class> NOT_SUPPORTED = new HashSet();

    static {
        IGNORE_FIELD.add("CASE_INSENSITIVE_ORDER");
        IGNORE_FIELD.add("class");
        IGNORE_FIELD.add("BYTES");
        IGNORE_FIELD.add("SIZE");
        IGNORE_FIELD.add("MAX_VALUE");
        IGNORE_FIELD.add("MIN_VALUE");
        IGNORE_FIELD.add("TYPE");

        NOT_SUPPORTED.add(Class.class);
        NOT_SUPPORTED.add(Object.class);
        NOT_SUPPORTED.add(String.class);
        NOT_SUPPORTED.add(Byte.class);
        NOT_SUPPORTED.add(Byte.TYPE);
        NOT_SUPPORTED.add(Boolean.class);
        NOT_SUPPORTED.add(Boolean.TYPE);
        NOT_SUPPORTED.add(Short.class);
        NOT_SUPPORTED.add(Short.TYPE);
        NOT_SUPPORTED.add(Integer.class);
        NOT_SUPPORTED.add(Integer.TYPE);
        NOT_SUPPORTED.add(Long.class);
        NOT_SUPPORTED.add(Long.TYPE);
        NOT_SUPPORTED.add(Float.class);
        NOT_SUPPORTED.add(Float.TYPE);
        NOT_SUPPORTED.add(Double.class);
        NOT_SUPPORTED.add(Double.TYPE);
        NOT_SUPPORTED.add(BigDecimal.class);
        NOT_SUPPORTED.add(Date.class);
        NOT_SUPPORTED.add(Time.class);
        NOT_SUPPORTED.add(java.sql.Date.class);
        NOT_SUPPORTED.add(java.sql.Time.class);
        NOT_SUPPORTED.add(java.sql.Timestamp.class);
    }
}
