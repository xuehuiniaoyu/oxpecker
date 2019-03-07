package org.ny.woods.os;

/**
 * 消息体
 * 作为MsgHandler传递的封装对象
 */
public class Message {
    private int what;
    private Object obj;

    public Message() {
    }

    public Message(int what, Object obj) {
        this.what = what;
        this.obj = obj;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public <T> T getObj() {
        return (T) obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
