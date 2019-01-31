package org.ny.woods.layout.tools;

/**
 *
 * 某些方法不能在解析的时候直接调用，必须要等到UI加载完才能计算
 * 所以必须要等到被执行了onMeasure方法后才会被调用
 * 由此方法必须被保存起来
 */
public abstract class UiFuncExec<T> {
    String name;
    T value;
    public UiFuncExec(String name, T value) {
        this.name = name;
        this.value = value;
    }
    protected abstract void onExec(T value);

    public UiFuncExec exec() {
        onExec(this.value);
        return this;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(obj instanceof UiFuncExec) {
            if (((UiFuncExec) obj).name.equals(this.name))
                return true;
        }
        return false;
    }
}
