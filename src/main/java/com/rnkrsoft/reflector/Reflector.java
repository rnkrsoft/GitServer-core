package com.rnkrsoft.reflector;

import com.rnkrsoft.util.MessageFormatter;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.*;

/**
 * Created by woate on 2020/02/24.
 * 类反射器
 */
public class Reflector {
    private static final Map<Class, Reflector> REFLECTORS = new WeakHashMap<Class, Reflector>();
    private static final Set<String> IGNORE_FIELD = new HashSet();
    private static final Set<String> IGNORE_METHOD = new HashSet();
    private static final Set<Class> NOT_SUPPORTED = new HashSet();

    static {
        IGNORE_FIELD.add("$");
        IGNORE_FIELD.add("CASE_INSENSITIVE_ORDER");
        IGNORE_FIELD.add("serialVersionUID");
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

    /**
     * 具有缓存的仿射器构建
     * @param type 类对象
     * @return 反射器
     */
    public static synchronized Reflector reflector(Class type) {
        Reflector reflector = REFLECTORS.get(type);
        if (reflector == null){
            reflector = new Reflector(type);
            REFLECTORS.put(type, reflector);
        }
        return reflector;
    }

    public <T> T newInstance() {
        T result = null;
        try {
            result = (T) noArgsConstructor.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 方法过滤器，用于忽略指定的方法名
     */
    public static interface MethodFilter {
        public boolean ignore(Method method);
    }

    /**
     * 类过滤器，用于忽略指定的类对象
     */
    public static interface ClassFilter {
        public boolean ignore(Class type);
    }

    Class type;
    ClassFilter classFilter;
    MethodFilter methodFilter;
    /**
     * 无参构造函数对象
     */
    Constructor<?> noArgsConstructor;
    /**
     * 当前反射器提取的类的成员字段类型
     */
    final Map<String, Class> fieldTypes = new HashMap<String, Class>();
    /**
     * 当前反射器提取的类的成员字段Field对象
     */
    final Map<String, Field> fields = new HashMap<String, Field>();
    /**
     * 当前反射器提取的类的所有方法
     */
    final Map<String, Method> methods = new HashMap<String, Method>();
    /**
     * 当前反射器提取的类的所有getter方法
     */
    final Map<String, Method> getters = new HashMap<String, Method>();
    /**
     * 当前反射器提取的类的所有setter方法
     */
    final Map<String, Method> setters = new HashMap<String, Method>();

    /**
     * 构建一个类的反射器
     * @param type 类对象
     */
    private Reflector(Class type) {
        this(type, null, null);
    }

    /**
     * 构建一个有类过滤器的反射器
     * @param type 类对象
     * @param classFilter 类过滤器
     */
    public Reflector(Class type, ClassFilter classFilter) {
        this(type, classFilter, null);
    }

    /**
     * 构建一个有方法过滤器的反射器
     * @param type 类对象
     * @param methodFilter 方法过滤器
     */
    public Reflector(Class type, MethodFilter methodFilter) {
        this(type, null, methodFilter);
    }

    /**
     * 构建一个有方法过滤器和类过滤器的反射器
     * @param type 类对象
     * @param classFilter 类过滤器
     * @param methodFilter 方法过滤器
     */
    public Reflector(Class type, ClassFilter classFilter, MethodFilter methodFilter) {
        this.type = type;
        if (classFilter == null) {
            this.classFilter = new ClassFilter() {
                @Override
                public boolean ignore(Class type) {
                    return false;
                }
            };
        } else {
            this.classFilter = classFilter;
        }
        if (classFilter == null) {
            this.methodFilter = new MethodFilter() {
                @Override
                public boolean ignore(Method method) {
                    return false;
                }
            };
        } else {
            this.methodFilter = methodFilter;
        }
        if (NOT_SUPPORTED.contains(this.type)) {

        } else {
            addNoArgsConstructor(this.type);
            addFields(this.type);
            addGetterMethods(this.type);
            addSetterMethods(this.type);
        }
    }

    private boolean canAccessPrivateMethods() {
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

    /**
     * 获取无参构造函数
     *
     * @param type JavaBean类
     */
    void addNoArgsConstructor(Class<?> type) {
        Constructor<?>[] constructors = type.getDeclaredConstructors();
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
            }
        }
        if (this.noArgsConstructor == null){
            throw new RuntimeException("please define No Args Constructor Method '"+  type.getSimpleName()+"()'");
        }
    }

    void addField(Field field) {
        if (!IGNORE_FIELD.contains(field.getName())) {
            this.fields.put(field.getName(), field);
            this.fieldTypes.put(field.getName(), field.getType());
        }
    }

    void addFields(Class<?> type) {
        Class currentClass = type;
        while (currentClass != null) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                if (canAccessPrivateMethods()) {
                    try {
                        field.setAccessible(true);
                    } catch (Exception e) {
                        // Ignored. This is only a final precaution, nothing we can do.
                    }
                }
                if (IGNORE_FIELD.contains(field.getName())) {
                    continue;
                }
                //如果字段能够访问
                if (field.isAccessible()) {
                    //如果父类字段在子类中不存在
                    if (!this.fields.containsKey(field.getName())) {
                        int modifiers = field.getModifiers();
                        //如果字段不是final或者类变量，则添加到字段中
                        if (!(Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers))) {
                            addField(field);
                        }
                    }
                }
            }
            Class<?> superclass = currentClass.getSuperclass();
            if (!NOT_SUPPORTED.contains(superclass)) {
                currentClass = superclass;
            } else {
                currentClass = null;
            }
        }
    }

    /**
     * 将方法对象生成字符串形式
     *
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
     *
     * @param uniqueMethods 类中唯一的方法对象
     * @param methods       所有当前类及其父类的方法
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
     *
     * @param clazz
     * @return
     */
    Collection<Method> getClassMethods(Class<?> clazz) {
        Map<String, Method> uniqueMethods = new LinkedHashMap<String, Method>();
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            if (classFilter.ignore(currentClass)) {
                currentClass = null;
                continue;
            }
            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());
            //遍历所有接口
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                addUniqueMethods(uniqueMethods, anInterface.getMethods());
            }
            Class<?> superclass = currentClass.getSuperclass();
            if (!NOT_SUPPORTED.contains(superclass)) {
                currentClass = superclass;
            } else {
                currentClass = null;
            }
        }

        Collection<Method> methods = uniqueMethods.values();
        return methods;
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

    void addMethod(String signature, Method method) {
        methods.put(signature, method);
    }

    void addSetMethod(String name, Method method) {
        if (!IGNORE_FIELD.contains(name)) {
            setters.put(name, method);
        }
    }

    void resolveSetterConflicts(Map<String, List<Method>> conflictingSetters) {
        for (String fieldName : conflictingSetters.keySet()) {
            List<Method> setters = conflictingSetters.get(fieldName);
            Method firstMethod = setters.get(0);
            if (setters.size() == 1) {
                addSetMethod(fieldName, firstMethod);
            } else {
                Class<?> expectedType = fieldTypes.get(fieldName);
                if (expectedType == null) {
                    throw new NonComplianceJavaBeanSpecification(MessageFormatter.format("类'{}'中存在冲突的Setter方法的重载方法'{}'.不符合JavaBean的规范", firstMethod.getDeclaringClass(), fieldName));
                } else {
                    Iterator<Method> methods = setters.iterator();
                    Method setter = null;
                    while (methods.hasNext()) {
                        Method method = methods.next();
                        if (method.getParameterTypes().length == 1
                                && expectedType.equals(method.getParameterTypes()[0])) {
                            setter = method;
                            break;
                        }
                    }
                    if (setter == null) {
                        throw new NonComplianceJavaBeanSpecification(MessageFormatter.format("类'{}'中存在冲突的Setter方法的重载方法'{}'.不符合JavaBean的规范", firstMethod.getDeclaringClass(), fieldName));
                    }
                    addSetMethod(fieldName, setter);
                }
            }
        }
    }

    void addSetterMethods(Class<?> cls) {
        Map<String, List<Method>> conflictingSetters = new LinkedHashMap<String, List<Method>>();
        Collection<Method> methods = getClassMethods(cls);
        for (Method method : methods) {
            if (methodFilter.ignore(method)) {
                continue;
            }
            String name = method.getName();
            if (name.startsWith("set")) {
                if (IGNORE_FIELD.contains(name)) {
                    continue;
                }
                //SETTER为1个参数
                if (method.getParameterTypes().length == 1) {
                    addMethodConflict(conflictingSetters, firstCharToLower(name.substring(3)), method);
                }
            }
        }
        resolveSetterConflicts(conflictingSetters);
    }

    /**
     * 调用此方法存放最终的Getter方法对象
     *
     * @param fieldName 字段名
     * @param method    方法对象
     */
    void addGetMethod(String fieldName, Method method) {
        getters.put(fieldName, method);
    }

    /**
     * 解决Getter方法存在子类和父类冲突的问题
     *
     * @param conflictingGetters 具有冲突的Getter方法列表
     */
    void resolveGetterConflicts(Map<String, List<Method>> conflictingGetters) {
        for (String fieldName : conflictingGetters.keySet()) {
            List<Method> getters = conflictingGetters.get(fieldName);
            Iterator<Method> iterator = getters.iterator();
            Method firstMethod = iterator.next();
            if (getters.size() == 1) {
                addGetMethod(fieldName, firstMethod);
            } else {
                Class<?> expectedType = fieldTypes.get(fieldName);
                if (expectedType == null) {
                    throw new NonComplianceJavaBeanSpecification(MessageFormatter.format("类'{}'中存在冲突的Setter方法的重载方法'{}'.不符合JavaBean的规范", firstMethod.getDeclaringClass(), fieldName));
                } else {
                    Method getter = null;
                    while (iterator.hasNext()) {
                        Method method = iterator.next();
                        Class<?> methodType = method.getReturnType();
                        if (!methodType.equals(expectedType)) {
                            throw new NonComplianceJavaBeanSpecification(MessageFormatter.format("类'{}'中存在冲突的Getter方法的重载方法'{}'.不符合JavaBean的规范", firstMethod.getDeclaringClass(), fieldName));
                        } else if (expectedType.isAssignableFrom(methodType)) {
                            getter = method;
                        } else {
                            throw new NonComplianceJavaBeanSpecification(MessageFormatter.format("类'{}'中存在冲突的Getter方法的重载方法'{}'.不符合JavaBean的规范", firstMethod.getDeclaringClass(), fieldName));
                        }
                    }
                    addGetMethod(fieldName, getter);
                }
            }
        }
    }

    /**
     * 将类的Getter方法放入缓存中
     *
     * @param type JavaBean类
     */
    void addGetterMethods(Class<?> type) {
        //先存放会冲突的Getter方法
        Map<String, List<Method>> conflictingGetters = new LinkedHashMap<String, List<Method>>();
        Collection<Method> methods = getClassMethods(type);
        for (Method method : methods) {
            if (methodFilter.ignore(method)) {
                continue;
            }
            String name = method.getName();
            if (name.startsWith("get")) {
                if (IGNORE_FIELD.contains(name)) {
                    continue;
                }

                //GETTER为无参
                if (method.getParameterTypes().length == 0) {
                    addMethodConflict(conflictingGetters, firstCharToLower(name.substring(3)), method);
                }
            } else if (name.startsWith("is")) {
                if (IGNORE_FIELD.contains(name)) {
                    continue;
                }

                //GETTER为无参
                if (method.getParameterTypes().length == 0) {
                    addMethodConflict(conflictingGetters, firstCharToLower(name.substring(2)), method);
                }
            }
        }
        //相同方法名按照子类覆盖的方法原则保存到Getter数组中
        resolveGetterConflicts(conflictingGetters);
    }

    /**
     * 根据方法签字来获取方法执行器
     *
     * @param signature 签字
     * @return 方法执行器
     */
    public MethodInvoker getMethod(String signature) {
        final Method method = methods.get(signature);
        return new MethodInvoker() {
            @Override
            public ReturnType getReturn() {
                return new ReturnType(method.getReturnType());
            }

            @Override
            public ParameterTypes getParameters() {
                return new ParameterTypes(method.getParameterTypes());
            }

            @Override
            public <T> T invoke(Object target, Object... args) throws Exception {
                return (T) method.invoke(target, args);
            }
        };
    }

    public Invoker getSetter(String fieldName) throws NoSuchMethodException {
        final Method method = setters.get(fieldName);
        if (method == null) {
            final Field field = fields.get(fieldName);
            if (field == null) {
                throw new NoSuchMethodException("no such method 'get" + firstCharToUpper(fieldName) + "'");
            }
            return new Invoker() {
                @Override
                public <T> T invoke(Object target, Object... args) throws Exception {
                    field.set(target, args[0]);
                    return null;
                }
            };
        }
        return new SetterMethodInvoker() {
            @Override
            public ReturnType getReturn() {
                return new ReturnType(method.getReturnType());
            }

            @Override
            public ParameterTypes getParameters() {
                return new ParameterTypes(method.getParameterTypes());
            }

            @Override
            public <T> T invoke(Object target, Object... args) throws Exception {
                return (T) method.invoke(target, args);
            }
        };
    }

    /**
     * 获取Getter执行器
     *
     * @param fieldName 字段名
     * @return 方法执行器
     */
    public Invoker getGetter(String fieldName) throws NoSuchMethodException {
        final Method method = getters.get(fieldName);
        if (method == null) {
            final Field field = fields.get(fieldName);
            if (field == null) {
                throw new NoSuchMethodException("no such method 'get" + firstCharToUpper(fieldName) + "'");
            }
            return new Invoker() {

                @Override
                public <T> T invoke(Object target, Object... args) throws Exception {
                    return (T) field.get(target);
                }
            };
        }
        return new GetterMethodInvoker() {
            @Override
            public ReturnType getReturn() {
                return new ReturnType(method.getReturnType());
            }

            @Override
            public ParameterTypes getParameters() {
                return ParameterTypes.NO_ARGS;
            }

            @Override
            public <T> T invoke(Object target, Object... args) throws Exception {
                return (T) method.invoke(target);
            }
        };
    }

    /**
     * 首字母大写
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String firstCharToUpper(String str) {
        char[] chars = str.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /**
     * 首字母小写
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String firstCharToLower(String str) {
        char[] chars = str.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static class ParameterTypes {
        public static final ParameterTypes NO_ARGS = new ParameterTypes(new Class[0]);

        Class[] types;

        public ParameterTypes(Class[] types) {
            this.types = types;
        }

        public Class getParameter(int index) {
            return this.types[index];
        }
    }
    public static class ReturnType {
        Class type;

        public ReturnType(Class type) {
            this.type = type;
        }

        public Class getType() {
            return type;
        }
    }

    public static interface Invoker {
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

    public static interface MethodInvoker extends Invoker{
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
        ParameterTypes getParameters();
    }

    public static interface GetterMethodInvoker extends MethodInvoker {
    }
    public static interface SetterMethodInvoker extends MethodInvoker{
    }

    public static class NonComplianceJavaBeanSpecification extends RuntimeException{
        public NonComplianceJavaBeanSpecification(String message) {
            super(message);
        }
    }
}
