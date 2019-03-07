package org.ny.woods.os;

import java.util.ArrayList;

/**
 *
 * 消息传送工具
 *
 * 如果想在多个HView中共享消息，那么就需要一个消息传送的工具。
 */
public class MsgHandler extends ArrayList<MsgHandler.Callback> {
    public interface Callback {
        /**
         * 接收消息
         * @param msg
         */
        void onHandleMsg(Message msg);
    }

    public void addListener(Callback listener) {
        this.add(listener);
    }

    public void removeListener(Callback listener) {
        this.remove(listener);
    }

    /**
     * 发送消息
     * @param msg
     */
    public void sendMsg(Message msg) {
        ArrayList<Callback> list = new ArrayList<Callback>(this);
        for(Callback listener : list) {
            listener.onHandleMsg(msg);
        }
    }
}
