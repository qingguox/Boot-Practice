package com.xlg.component.netty.apply.message;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author wangqingwei
 * Created on 2022-06-19
 */
public abstract class Message implements Serializable {

    public static Class<?> getMessageClass(int messageType) {
        return MESSAGE_CLASSES.get(messageType);
    }

    /**
     * 请求序号id
     */
    private int sequenceId;

    /**
     * 消息类型
     */
    private int messageType;

    public abstract int getMessageType();

    public static final int LOGIN_REQUEST_MESSAGE = 0;
    public static final int LOGIN_RESPONSE_MESSAGE = 1;

    private static final Map<Integer, Class<?>> MESSAGE_CLASSES = Maps.newConcurrentMap();

    static {
        MESSAGE_CLASSES.put(LOGIN_REQUEST_MESSAGE, LoginRequestMessage.class);
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
