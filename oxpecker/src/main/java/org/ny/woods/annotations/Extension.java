package org.ny.woods.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)

/**
 * 给函数或属性扩展一个或多个名称出来，你可以用扩展出来的名称访问该方法或属性。
 * 比如你在配置中希望通过gap来代替访问margin的动作，你可以：
 *
 * @Extension("gap")
 * public void setMargin(...) {
 *
 * }
 *
 * 这是一个最简单的例子
 *
 * 然而你能通过 getExtension(String name) 来获取扩展名
 *
 * 如果你希望得到更多帮助，可以参考：
 * @see org.zoon.header.layout.HNode#getExtension(String)
 *
 */
public @interface Extension {
    String[] value() default "";
}
