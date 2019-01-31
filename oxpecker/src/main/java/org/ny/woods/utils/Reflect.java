package org.ny.woods.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具
 * Created by tjy on 2017/7/20 0020.
 */

public class Reflect {
    // 类(静态方法或属性使用)
    private Class<?> clz;
    // 对象
    private Object obj;
    // 方法名
    private String methodName;
    // 方法参数
    private Class<?>[] methodArgs;

    public static class ConstructorInfo {
        Class<?> clz;
        Class<?>[] parameterTypes;

        public ConstructorInfo(ConstructorInfo info) {
            this.clz = info.clz;
            this.parameterTypes = info.parameterTypes;
        }

        public ConstructorInfo(Class<?> clz, Class<?>[] parameterTypes) {
            this.clz = clz;
            this.parameterTypes = parameterTypes;
        }

        /**
         * 创建对象
         * @param objects
         * @param <T>
         * @return
         */
        public <T> T newInstance(Object ... objects) {
            try {
                Constructor constructor = this.clz.getConstructor(parameterTypes);
                try {
                    return (T) constructor.newInstance(objects);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e.getMessage());
            }
            return null;
        }
    }

    /**
     * 创建一个新对象
     * @return
     */
    public static Reflect newInstance() {
        return new Reflect();
    }

    /**
     * 清理残留
     * @return
     */
    public Reflect clear() {
        clz = null;
        obj = null;
        methodName = null;
        methodArgs = null;
        return this;
    }

    /**
     * 锁定对象（到对象）【返回本身】
     * @param obj
     * @return
     */
    public Reflect on(Object obj) {
        this.obj = obj;
        return this;
    }

    /**
     * 锁定对象（到类）【返回本身】
     * @param clz
     * @return
     */
    public Reflect on(Class<?> clz) {
        this.clz = clz;
        return this;
    }

    /**
     * 锁定对象（到类）【返回本身】
     * @param className
     * @return
     */
    public Reflect on(String className) {
        try {
            this.clz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
        return this;
    }

    /**
     * 锁定对象（到类）【返回本身】
     * @param className
     * @return
     */
    public Reflect on(String className, ClassLoader classLoader) {
        try {
            this.clz = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
        return this;
    }

    /**
     * 锁定方法【返回本身】
     * @param name
     * @param args
     * @return
     */
    public Reflect method(String name, Class<?> ... args) {
        this.methodName = name;
        this.methodArgs = args;
        return this;
    }

    /**
     * 构造
     * @param parameterTypes
     * @return
     */
    public ConstructorInfo constructor(Class<?> ... parameterTypes) {
        return new ConstructorInfo(clz, parameterTypes);
    }

    /**
     * 改变属性值【返回本身】
     * @param name
     * @param value
     */
    public Reflect set(String name, Object value) {
        Class<?> clz = this.obj != null ? this.obj.getClass() : this.clz;
        return set(clz, name, value);
    }

    Reflect set(Class<?> clz, String name, Object value) {
        if(clz != null) {
            try {
                Field field = clz.getDeclaredField(name);
                field.setAccessible(true);
                field.set(this.obj, value);
            } catch (NoSuchFieldException e) {
                return set(clz.getSuperclass(), name, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * 获取属性内容【返回结果】
     * @param name
     * @return
     */
    public <T> T get(String name) {
        if(name.contains(".")) {
            String[] names = name.split("\\.");
            Class<?> clz = this.obj != null ? this.obj.getClass() : this.clz;
            Object obj = null;
            for(String n : names) {
                obj = get(clz, n);
                clz = (this.obj = obj).getClass();
            }
            return (T) obj;
        }
        else {
            Class<?> clz = this.obj != null ? this.obj.getClass() : this.clz;
            return (T) get(clz, name);
        }
    }

    Object get(Class<?> clz, String name) {
        if(clz != null) {
            try {
                Field field = clz.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(this.obj);
            } catch (NoSuchFieldException e) {
                return get(clz.getSuperclass(), name);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 执行反射命令【返回结果】
     * @param objs
     * @return
     */
    public <T> T invoke(Object ... objs) {
        Class<?> clz = this.obj != null ? this.obj.getClass() : this.clz;
        return (T) invoke(clz, objs);
    }

    Object invoke(Class<?> clz, Object ... objs) {
        if(clz != null) {
            try {
                Method method = clz.getDeclaredMethod(methodName, methodArgs);
                method.setAccessible(true);
                return method.invoke(this.obj, objs);
            } catch (NoSuchMethodException e) {
                return invoke(clz.getSuperclass(), objs);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Object typeTo(Class<?> toClass, Object object) {
        if(object instanceof Double) {
            Double doubleValue = (Double) object;
            if (toClass == Integer.class || toClass == int.class) {
                return doubleValue.intValue();
            }
            if (toClass == Float.class || toClass == float.class) {
                return doubleValue.floatValue();
            }
            if (toClass == Long.class || toClass == long.class) {
                return doubleValue.longValue();
            }
            if (toClass == double.class) {
                return doubleValue.doubleValue();
            }
        }
        else if(object instanceof String) {
            String stringValue = (String) object;
            if(toClass == Character.class || toClass == char.class) {
                return stringValue.toCharArray()[0];
            }
        }
        else if(object instanceof Boolean && toClass == boolean.class) {
            return ((Boolean) object).booleanValue();
        }

        return object;
    }
}
