package org.ny.woods.js.native_object.reflect;

import org.ny.woods.exception.HException;
import org.ny.woods.js.native_object.code.JavaPackage;
import org.ny.woods.utils.Reflect;

public class RobustReflect extends Reflect {
    private JavaPackage javaPackage = new JavaPackage();

    /**
     * 构造函数
     */
    public static class RobustConstructorInfo extends Reflect.ConstructorInfo {
        public RobustConstructorInfo(ConstructorInfo info) {
            super(info);
        }

        /**
         * 创建对象
         *
         * @param objects
         * @param <T>
         * @return
         */
        public <T> T new_(Object... objects) {
            return newInstance(objects);
        }
    }

    /**
     * 引入包管理器
     *
     * @param javaPackage
     * @return
     */
    public RobustReflect from(JavaPackage javaPackage) {
        if(javaPackage != null) {
            this.javaPackage = javaPackage;
        }
        return this;
    }

    @Override
    public Reflect on(String className) {
        return super.on(javaPackage.loadClass(className));
    }

    @Override
    public Reflect on(String className, ClassLoader classLoader) {
        return super.on(javaPackage.loadClass(className), classLoader);
    }

    /**
     * 构造器，名称简单方便外部调用
     *
     * @param classNames
     * @return
     */
    public ConstructorInfo c_(String... classNames) {
        return new RobustConstructorInfo(constructor(classNames));
    }

    /**
     * 构造器
     *
     * @param classNames
     * @return
     */
    public ConstructorInfo constructor(String... classNames) {
        return new RobustConstructorInfo(constructor(convertToClass(classNames)));
    }

    /**
     *
     * 反射方法
     * @param name
     * @param classNames 类的全包名或别名
     * @return
     */
    public Reflect method1(String name, String ... classNames) {
        return method(name, convertToClass(classNames));
    }

    /**
     * 加载类
     *
     * @param className
     * @return
     */
    public Class<?> loadClass(String className) {
        if (javaPackage.containsKey(className)) {
            className = javaPackage.get(className);
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new HException("ClassNotFoundException:" + className);
        }
    }

    private Class[] convertToClass(String... classNames) {
        Class[] classes = new Class[classNames.length];
        for (int i = 0; i < classes.length; i++) {
            classes[i] = loadClass(classNames[i]);
        }
        return classes;
    }
}
