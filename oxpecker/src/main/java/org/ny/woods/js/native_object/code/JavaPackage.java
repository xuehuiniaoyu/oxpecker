package org.ny.woods.js.native_object.code;

import org.ny.woods.exception.HException;

import java.util.HashMap;

public class JavaPackage extends HashMap<String, String> {
    private String requireClass;
    /* 导入类名 */
    public JavaPackage require(String className) {
        try {
            this.requireClass = className;
            put(className.substring(className.lastIndexOf(".") + 1), this.requireClass);
        } catch (Exception e) {
            throw new HException(className+" require fail!");
        }
        return this;
    }

    /* 将使用别名代替类名 */
    public JavaPackage as(String name) {
        if(this.requireClass != null) {
            this.put(name, this.requireClass);
            this.requireClass = null;
        }
        return this;
    }

    /* 加载类 */
    public String loadClass(String className) {
        if(this.containsKey(className))
            return this.get(className);
        if(className.contains("$")) {
            int splitIndex = className.indexOf("$");
            String mainClassName = className.substring(0, splitIndex);
            String theCompleteClassName = this.containsKey(mainClassName) ? this.get(mainClassName) : mainClassName;
            return theCompleteClassName + className.substring(splitIndex);
        }
        return className;
    }

    public static void main(String[] args) {
        JavaPackage javaPackage = new JavaPackage();
        javaPackage.require("android.graphics.Paint");
        System.out.println(javaPackage.loadClass("Paint$Style"));
    }
}
