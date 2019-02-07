package org.ny.woods.layout;

import android.view.View;

import org.hjson.JsonValue;
import org.ny.woods.annotations.Extension;
import org.ny.woods.layout.widget.HView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * 代表 HJson 配置文件中的一个对象
 * Represents an object in the configuration file is HJson
 *
 */
public class HNode {

    private JsonValue value;
    private List<HNode> children;
    private HashMap<String, String> methodExtension = new HashMap<>();

    private WeakReference<HNode> parent;

    // 是否结束
    private boolean died;

    public HNode(JsonValue value) {
        this.value = value;
        // 解析注解
        loadExtensions(this.getClass());
    }

    public boolean isDied() {
        return died;
    }

    void loadExtensions(Class<?> clz) {
        Method[] declaredMethods = clz.getDeclaredMethods();
        for(Method method : declaredMethods) {
            if(method.isAnnotationPresent(Extension.class)) {
                Extension extension = method.getAnnotation(Extension.class);
                String[] values = extension.value();
                for(String nameValue : values) {
                    String methodName = method.getName();
                    methodExtension.put(nameValue, methodName);
                }
            }
        }
        if(clz.getSuperclass() != null) {
            loadExtensions(clz.getSuperclass());
        }
    }

    /**
     * 添加孩子
     * @param hNode
     */
    public void addChild(HNode hNode) {
        if(children == null) {
            children = new ArrayList<>();
        }
        children.add(hNode);
        hNode.parent = new WeakReference<>(this);
    }

    public <T extends HView<? extends View>> T getParent() {
        if(parent == null)
            return null;
        return (T) parent.get();
    }

    /**
     * 设置属性
     * @param name
     * @param value
     */
    public void setAttr(String name, JsonValue value) {
    }

    /**
     * 获取孩子
     * @return
     */
    public List<HNode> getChildren() {
        return children;
    }

    /**
     * 获取第 position 个孩子
     * @param position
     * @return
     */
    public HNode getChildAt(int position) {
        return children.get(position);
    }

    /**
     * hJson内容
     * @return
     */
    public JsonValue getValue() {
        return value;
    }

    /**
     *
     * 获取扩展名
     * @param name
     * @return
     */
    public String getExtension(String name) {
        return methodExtension.get(name);
    }

    /**
     * 销毁、回收
     */
    public void onRecycle() {
        if(children != null) {
            for(HNode hNode : children) {
                hNode.onRecycle();
            }
        }
        if(parent != null) {
            parent.clear();
            parent = null;
        }
        died = true;
    }
}
